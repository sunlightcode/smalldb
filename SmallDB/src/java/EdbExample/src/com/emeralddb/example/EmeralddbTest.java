package com.emeralddb.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import com.emeralddb.base.Emeralddb;
import com.emeralddb.net.ServerAddress;
import java.util.concurrent.CountDownLatch;
public class EmeralddbTest {
   public static Map<ServerAddress,Integer> nodeRecordMap = null;
   public static Integer count = 0;
   private static Random _ran = new Random();
   private String serverFile;
   private long insertTotalTime = 0;
   private String[] uuidArray = null;
   private final int INSERT_TIME = 50000;
   private InsertThread[] insertThreadArray;

   class InsertThread extends Thread {
      public void run()
      {
         Emeralddb edb = new Emeralddb();
         int rc = edb.init(serverFile);
         if( -1 == rc ) {
            return;
         }
         edb.startStat();
         System.out.println( "threadid:"+ getId() + " "
            + "start time:" + System.currentTimeMillis() );
         System.out.println( "threadid:" + getId() + " "
            + "insert numer:" + start + "-" + number );
         for( int i= start; i < number; i++ ) {
            edb.insert( uuidArray[i], String.format( "{_id:'%s'}", uuidArray[i] ) );
         }
        System.out.println( "threadid:" + getId() + " "
            + "end time:" + System.currentTimeMillis() );
        threadSignal.countDown();
         edb.endStat( number );
      }
      public InsertThread(int i, CountDownLatch threadSignal ) {
         this.start = i;
         this.threadSignal = threadSignal;
      }
      public void setNumber( int number ) {
         this.number = number;
      }
      private int start;
      private int number;
      private CountDownLatch threadSignal;
   }
   public static void main(String[] args) {
      EmeralddbTest et = new EmeralddbTest();

      if( args.length < 2 ) {
         et.usage();
         return;
      }
      int recordNumber = 0;
      try {
         recordNumber = Integer.parseInt( args[1] );
      } catch( NumberFormatException e ) {
         System.out.println( "Argument two is not right number.\n" );
         return;
      }
      et.setFile(args[0]);
      et.testRecord( recordNumber );
   }

   private void usage() {
      System.out.println( "Argument is not right.\n" );
      System.out.println( "EmeralddbTest [config-file] [record number]\n" );
      System.out.println( "For example: EmeralddbTest ./config.properties 1000\n" );
   }
   public void setFile(String file) {
      serverFile = file;
   }
   /**
    * @func for test record
    * @param recordNum
    * @param baseNum
    * @author zhouzhengle
    */
   public void testRecord(int recordNum ) {
      uuidArray = new String[ recordNum ];
      for( int i = 0; i < recordNum; i++ )
      {
         String uuid = java.util.UUID.randomUUID().toString().replaceAll( "-","" )
               + RandomGUID.newGuid();
         uuidArray[i] = uuid;
      }
      System.out.println( "start..." );
      long startTime = System.nanoTime();

      int threadNum = recordNum/INSERT_TIME;
      if( recordNum%INSERT_TIME != 0 ) {
         threadNum += 1;
      }
      insertThreadArray = new InsertThread[threadNum];
      CountDownLatch threadSignal = new CountDownLatch( threadNum );
      int index = 0;
      for( int i = 0; i < threadNum; i++ ) {
         insertThreadArray[i] = new InsertThread( index, threadSignal );
         if( recordNum < INSERT_TIME ) {
            insertThreadArray[i].setNumber( index + recordNum );
         } else {
            insertThreadArray[i].setNumber( index + INSERT_TIME );
            index += INSERT_TIME;
        }
         recordNum -= INSERT_TIME;
      }

      for( int i = 0; i < threadNum; i++ ) {
         insertThreadArray[i].start();
      }
      try {
         threadSignal.await();
      } catch( InterruptedException e ) {
         e.printStackTrace();
      }
      long endTime = System.nanoTime();
      insertTotalTime += (endTime-startTime);
      System.out.println("total time is " + insertTotalTime/( 1000*1000*1000) );
   }
}
