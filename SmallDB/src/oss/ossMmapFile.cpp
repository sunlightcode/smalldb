/*******************************************************************************
   Copyright (C) 2013 smalldb Software Inc.

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU Affero General Public License, version 3,
   as published by the Free Software Foundation.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
   GNU Affero General Public License for more details.

   You should have received a copy of the GNU Affero General Public License
   along with this program. If not, see <http://www.gnu.org/license/>.
   *******************************************************************************/
#include "ossMmapFile.hpp"
#include "pd.hpp"

using namespace std;

#ifndef _WINDOWS

int _ossMmapFile::open ( const char * pFilename,
	unsigned int options )
{
	int rc = EDB_OK ;
	_mutex.get() ;
	rc = _fileOp.Open ( pFilename, options ) ;
	if ( EDB_OK == rc )
		_opened = true ;
	else
	{
		PD_LOG ( PDERROR, "Failed to open file, rc = %d",
			rc ) ;
		goto error ;
	}
	strncpy ( _fileName, pFilename, sizeof(_fileName) ) ;
done :
	_mutex.release () ;
	return rc ;
error :
	goto done ;
}

void _ossMmapFile::close ()
{
	_mutex.get() ;

	for ( vector<ossMmapSegment>::iterator i = _segments.begin() ;
		i != _segments.end(); i++ )
	{
		munmap ((void*)(*i)._ptr, (*i)._length ) ;
	}

	_segments.clear () ;
	if ( _opened )
	{
		_fileOp.Close () ;
		_opened = false ;
	}
	_mutex.release () ;
}

int _ossMmapFile::map ( unsigned long long offset,
	unsigned int length,
	void **pAddress )
{
	_mutex.get() ;
	int rc = EDB_OK ;
	ossMmapSegment seg ( 0, 0, 0 ) ;
	unsigned long long fileSize = 0 ;
	void *segment = NULL ;
	if ( 0 == length )
		goto done ;

	rc = _fileOp.getSize((offsetType *const)&fileSize);
	if ( rc )
	{
		PD_LOG ( PDERROR,
			"Failed to get file size, rc = %d", rc ) ;
		goto error ;
	}
	if ( offset + length > fileSize )
	{
		PD_LOG ( PDERROR,
			"Offset is greater than file size" ) ;
		rc = EDB_INVALIDARG ;
		goto error ;
	}

	// map region into memllory
	segment = mmap ( NULL, length, PROT_READ | PROT_WRITE,
		MAP_SHARED, _fileOp.getHandle () , offset ) ;
	if ( MAP_FAILED == segment )
	{
		PD_LOG ( PDERROR,
			"Failed to map offset %ld length %d, errno = %d",
			offset, length, errno ) ;
		if ( ENOMEM == errno )
			rc = EDB_OOM ;
		else if ( EACCES == errno )
			rc = EDB_PERM ;
		else
			rc = EDB_SYS ;
		goto error ;
	}

	seg._ptr = segment ;
	seg._length = length ;
	seg._offset = offset ;
	_segments.push_back ( seg ) ;
	if ( pAddress )
		*pAddress = segment ;
done :
	_mutex.release () ;
	return rc ;
error :
	goto done ;
}

#else
int _ossMmapFile::open(const char * pFilename,
	unsigned int options)
{
	int rc = EDB_OK;

	if (!_created)
	{
		hFile = CreateFile(pFilename,
			GENERIC_WRITE | GENERIC_READ,
			FILE_SHARE_READ | FILE_SHARE_WRITE,
			NULL,
			OPEN_ALWAYS,
			FILE_FLAG_SEQUENTIAL_SCAN,
			NULL);
	}

	DWORD le = GetLastError();

	if (GETLASTERROR_RETURN_OK == le || FILE_ALREADY_EXISTS == le)
	{
		//pFileName = pFilename;
		_created = true;
		strncpy(_fileName, pFilename, sizeof(_fileName));
		return rc;
	}
	else
	{
		rc = le;
		PD_LOG(PDERROR, "Failed to OpenFileMapping, rc = %d",
			rc);
		goto error;
	}
done:
	_mutex.release();
	return rc;
error:
	goto done;

}

void _ossMmapFile::close()
{
	_mutex.get();

	for (vector<ossMmapSegment>::iterator i = _segments.begin();
		i != _segments.end(); i++)
	{
		//munmap((void*)(*i)._ptr, (*i)._length);
		//UnmapViewOfFile(segment);
		CloseHandle(hFileMapping);
		CloseHandle(hFile);
	}
	_created = false;
	_mapped = false;
	_opened = false;
	_segments.clear();
	_mutex.release();
}

int _ossMmapFile::getSize(offsetType * const pFileSize)
{
	int             rc = 0;
	oss_struct_stat buf = { 0 };

	int size = 0;
	size = GetFileSize(hFile, NULL);
	DWORD le = GetLastError();
	if (GETLASTERROR_RETURN_OK == le || FILE_ALREADY_EXISTS == le)
	{
		*pFileSize = size;
		return rc;
	}
	else
	{
		rc = errno;
		goto err_exit;
	}

exit:
	return rc;

err_exit:
	*pFileSize = 0;
	goto exit;
}

bool _ossMmapFile::isValid()
{
	return (OSS_INVALID_HANDLE_FD_VALUE != _fileHandle);
}

void _ossMmapFile::seekToEnd(void)
{
	SetFilePointer(hFile, 0, NULL, SEEK_END);
	//oss_lseek(_fileHandle, 0, SEEK_END);
}

int _ossMmapFile::Write(const void * pBuffer, size_t size)
{
	int rc = 0;
	size_t currentSize = 0;
	DWORD wSize = 0;

	if (0 == size)
	{
		size = strlen((char *)pBuffer);
	}

	if (isValid())
	{
		do{
			//curTotleSize = curTotleSize + (size-currentSize);
			BOOL bRet = ::WriteFile(hFile, &((char*)pBuffer)[currentSize], size - currentSize, &wSize, NULL);
			if (!bRet)
			{
				rc = -1;
			}
			else
			{
				rc = wSize;
			}
			if (rc >= 0)
				currentSize += rc;
		} while (((-1 == rc) && (EINTR == errno)) ||
			((-1 != rc) && (currentSize != size)));

		if (-1 == rc)
		{
			rc = errno;
			goto exit;
		}

		rc = 0;
	}
exit:
	return rc;
}

int _ossMmapFile::map(unsigned long long offset,
	unsigned int length,
	void **pAddress)
{
	_mutex.get();
	int rc = EDB_OK;
	ossMmapSegment seg(0, 0, 0);
	unsigned long long fileSize = 0;
	void *segment = NULL;
	if (0 == length)
		goto done;

	rc = getSize((offsetType *const)&fileSize);
	if (rc)
	{
		PD_LOG(PDERROR,
			"Failed to get file size, rc = %d", rc);
		goto error;
	}

	if (offset + length > fileSize)
	{
		PD_LOG(PDERROR,
			"Offset is greater than file size");
		rc = EDB_INVALIDARG;
		goto error;
	}

	// map region into memllory
	hFileMapping = CreateFileMapping(hFile,
		NULL,
		PAGE_READWRITE,
		0,
		0,
		NULL);

	DWORD le = GetLastError();

	if (GETLASTERROR_RETURN_OK == le)
	{
		segment = MapViewOfFile(hFileMapping,
			FILE_MAP_ALL_ACCESS,
			DWORD(offset >> 32), DWORD(offset & 0xFFFFFFFF), length);
		le = GetLastError();
	}

	if (GETLASTERROR_RETURN_OK != le) {
		PD_LOG(PDERROR,
			"Failed to map offset %ld length %d, errno = %d",
			offset, length, errno);
		if (ENOMEM == errno)
			rc = EDB_OOM;
		else if (EACCES == errno)
			rc = EDB_PERM;
		else
			rc = EDB_SYS;
		goto error;
	}
	seg._ptr = segment;
	seg._length = length;
	seg._offset = offset;
	_segments.push_back(seg);
	if (pAddress)
		*pAddress = segment;
done:
	_mutex.release();
	return rc;
error:
	goto done;
}

#endif // _WINDOWS