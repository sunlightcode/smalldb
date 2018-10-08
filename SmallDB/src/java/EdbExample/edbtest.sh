# !bin/sh

error()
{
   #echo "$@" 1>&2
   usage_and_exit 1
}

usage()
{
   echo "Usage: $PROGRAM [--start][--test][--clean][--?][--help][--version]"
}

usage_and_exit()
{
   usage
   exit $1
}

version()
{
   echo "$PROGRAM version $VERSION"
}

check_file_exist()
{
   if [ ! -f $BASE_DIR/$EMERALDDB ]; then
      echo "$EMERALDDB is not exist in current directory."
      exit $1
   fi
   if [ ! -f $BASE_DIR/$CONFIG_FILE ]; then
      echo "$CONFIG_FILE is not exist in current directory."
      exit $1
   fi
}

create_server_dirs()
{
   if [ ! -d $BASE_DIR/$EDB_SERVER_DIR ]; then
      mkdir $BASE_DIR/$EDB_SERVER_DIR
      echo "create $BASE_DIR/$EDB_SERVER_DIR success."
   fi
}

check_process_stop()
{
   process_num=`ps -elf | grep emeralddb | grep -v grep | wc -l`
   if [ $process_num -gt 0 ]; then
      echo "$EMERALDDB process is exist."
      exit $1
   fi
}

start_edb_server()
{
   index=0
   cat $BASE_DIR/$CONFIG_FILE | while read line
   do
      server=$(echo $line | awk -F ":" '{print $1}')
      port=$(echo $line | awk -F ":" '{printf $2}')
      $BASE_DIR/$EMERALDDB -d $BASE_DIR/$EDB_SERVER_DIR/db$index.1 \
            -l $BASE_DIR/$EDB_SERVER_DIR/log$index.log \
            -s $port  1>/dev/null 2>/dev/null &
      index=$[$index+1]
   done
}

clean()
{
   `ps -elf | grep emeradldb | grep -v grep | awk '{print $4}' | xargs kill -9`
   `find ./ -name *.1 | xargs rm -rf`
   `find ./ -name *.log | xargs rm -rf`
   
}

EXIT_CODE=0
PROGRAM=`basename $0`
VERSION=1.0
BASE_DIR=.
EDB_SERVER_DIR=edb_server
EMERALDDB=emeralddb
CONFIG_FILE=config.properties
EDB_JAR=EmeralddbTest.jar

while test $# -gt 0
do
   case $1 in
      --start )
      check_file_exist
      check_process_stop
      create_server_dirs
      start_edb_server
      exit 0
      ;;
      --test )
         if [[ $2 =~ [0-9]+ ]]; then
            java -jar $BASE_DIR/$EDB_JAR $BASE_DIR/$CONFIG_FILE $2
         else
            echo "please test record number"
         fi
         exit 0
         ;;
      --clean )
         clean
         exit 0
         ;;
      --help )
      usage_and_exit 0
      ;;
      --version )
      version
      exit 0
      ;;
      -*)
      error "Unrecognized option: $1"
      ;;
      *)
      error
      break
      ;;
   esac
   shift
done


