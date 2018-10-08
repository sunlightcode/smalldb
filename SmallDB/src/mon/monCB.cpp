/*******************************************************************************
   Copyright (C) 2013 SunlightDB Software Inc.

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
#include "monCB.hpp"
#include <sys/time.h>
#include <unistd.h>

MonAppCB::MonAppCB() :
   _insertTimes(0),
   _delTimes(0),
   _queryTimes(0)
{
   gettimeofday ( &_start, NULL ) ;
}

MonAppCB::~MonAppCB()
{
}

void MonAppCB::setInsertTimes ( long long insertTimes )
{
   _insertTimes = insertTimes ;
}
long long MonAppCB::getInsertTimes () const
{
   return _insertTimes ;
}
void MonAppCB::increaseInsertTimes()
{
   _mutex.get() ;
   _insertTimes++ ;
   _mutex.release() ;
}
void MonAppCB::setDelTimes ( long long delTimes )
{
   _delTimes=delTimes ;
}
long long MonAppCB::getDelTimes () const
{
   return _delTimes ;
}
void MonAppCB::increaseDelTimes ()
{
   _mutex.get() ;
   _delTimes++ ;
   _mutex.release () ;
}
void MonAppCB::setQueryTimes ( long long queryTimes )
{
   _queryTimes = queryTimes ;
}
long long MonAppCB::getQueryTimes () const
{
   return _queryTimes ;
}
void MonAppCB::increaseQueryTimes ()
{
   _mutex.get() ;
   _queryTimes++ ;
   _mutex.release () ;
}
long long MonAppCB::getServerRunTime ()
{
   struct timeval end ;
   gettimeofday ( &end, NULL ) ;
   long long timeuse = (end.tv_sec - _start.tv_sec ) ;
   return timeuse ;
}

