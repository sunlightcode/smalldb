/**
 *      Copyright (C) 2012 SunlightDB Inc.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.emeralddb.net;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.emeralddb.exception.BaseException;
import com.emeralddb.util.Helper;
/**
 * @author zhengle zhou
 * 
 */
public class ConnectionTCPImpl implements IConnection {

   private Socket clientSocket;
   private InputStream input  = null;
   private OutputStream output = null;
   private ConfigOptions options;
   private ServerAddress hostAddress;

   public ConnectionTCPImpl(ServerAddress addr, ConfigOptions options) {
      this.hostAddress = addr;
      this.options = options;
   }

   public boolean connect() throws BaseException {
      if (clientSocket != null) {
         return false;
      }

      BaseException lastError = null;
      InetSocketAddress addr = hostAddress.getHostAddress();
      try {
         clientSocket = new Socket();
         clientSocket.connect(addr, options.getConnectTimeout());

         clientSocket.setTcpNoDelay(!options.getUseNagle());
         clientSocket.setKeepAlive(options.getSocketKeepAlive());
         clientSocket.setSoTimeout(options.getSocketTimeout());
         input = new BufferedInputStream(clientSocket.getInputStream());
         output = clientSocket.getOutputStream();
      } catch (IOException ioe) {
         lastError = new BaseException("EDB_NETWORK");
         close();
         throw lastError;
      }
      return true;
   }

   public void close() {
      if (clientSocket != null) {
         try {
            clientSocket.close();
         } catch (Exception e) {
         } finally {
            input = null;
            output = null;
            clientSocket = null;
         }
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see com.SunlightDB.net.IConnection#changeConfigOptions(com.SunlightDB.net.
    * ConfigOptions)
    */
   @Override
   public void changeConfigOptions(ConfigOptions opts) throws BaseException {
      this.options = opts;
      close();
      connect();
   }

   /*
    * (non-Javadoc)
    * 
    * @see com.SunlightDB.net.IConnection#sendMessage(byte[], int)
    */
   @Override
   public boolean sendMessage(byte[] msg) {
      try
      {
         if( null == output )
         {
            return false;
         }
         output.write(msg);
         return true;
      }
      catch ( IOException e )
      {
         throw new BaseException ( "EDB_NETWORK" ) ;
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see com.SunlightDB.net.IConnection#receiveMessage()
    */
   @Override
   public byte[] receiveMessage()  {
      byte[] buf = new byte[4];

      input.mark(4);

      try
      {
         int rtn = input.read(buf);

         if (rtn != 4) {
            close();
            throw new BaseException("EDB_NETWORK");
         }
         int msgSize = Helper.byteToInt(buf);

         input.reset();

         buf = new byte[msgSize];
         rtn = 0;
         int retSize = 0;
         while (rtn < msgSize) {
            retSize = input.read(buf, rtn, msgSize - rtn);
            if (-1 == retSize) {
               close();
               throw new BaseException("EDB_NETWORK");
            }
            rtn += retSize;
         }

         if (rtn != msgSize) {
            StringBuffer bbf = new StringBuffer();
            for (byte by : buf) {
               bbf.append(String.format("%02x", by));
            }
            close();
            throw new BaseException("EDB_INVALIDARG");
         }
      }
      catch ( IOException e )
      {
         throw new BaseException ( "EDB_NETWORK" ) ;
      }

      return buf;
   }
}
