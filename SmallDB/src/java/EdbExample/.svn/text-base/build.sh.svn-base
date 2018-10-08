#!/bin/bash

# define compile
JAVA_HOME=../../../java/jdk_linux64
JAVAC=$JAVA_HOME/bin/javac
JAR=$JAVA_HOME/bin/jar

PROGRAM_NAME=`basename $0`

CURRENT_DIR=`pwd`

usage()
{
   echo $PROGRAM_NAME [--class][--help]
   exit 0
}

error()
{
   echo "argument is null"
   usesage
}

compile_emeralddb()
{
   echo "emeralddb.jar is not exist."
   echo "compiling emeralddb ..."
   cd ../../driver/java
   `./build.sh`
   cd $CURRENT_DIR
   echo "compiling have finished"
   echo "copy emeraldb to $BIN_DIR"
}

compile_emeralddb_test()
{
   $JAVAC -classpath "$JDK_LIB:$LIB_DIR/emeralddb.jar:./lib/apache-commons-id.jar:./lib/apache-commons-discovery.jar" \
      `find $SOURCE_DIR -name "*.java" -print` -d $BIN_DIR
   echo "emeralddbTest compile success."
}

pack_emeralddb_test()
{
   if [ ! -d $TARGET_BIN_DIR ]; then
      mkdir $TARGET_BIN_DIR
   fi

   # META-INF dir is exist or not
   if [ ! -d ./META-INF ]; then
      mkdir META-INF
   fi

   cp $TARGET_BIN_DIR/emeralddb.jar $BIN_DIR
   cp  ./lib/*.jar $BIN_DIR
   echo "Manifest-Version: 1.0" > ./META-INF/MANIFEST.MF
   echo "Class-Path: emeralddb.jar apache-commons-id.jar apache-commons-discovery.jar">> ./META-INF/MANIFEST.MF
   echo "Main-Class: com.emeralddb.example.$1" >> ./META-INF/MANIFEST.MF
   echo -e "\n" >> ./META-INF/MANIFEST.MF
   echo "========================================================"
   cd $BIN_DIR
   ../$JAR -cvfm ../$TARGET_BIN_DIR/$1.jar ../META-INF/MANIFEST.MF ./
   cd $CURRENT_DIR
   echo "========================================================"
   echo "packing: $1.jar has finished."
}

pack_emeralddb_test2()
{
   if [ ! -d $TARGET_BIN_DIR ]; then
      mkdir $TARGET_BIN_DIR
   fi

   # META-INF dir is exist or not
   if [ ! -d ./META-INF ]; then
      mkdir META-INF
   fi

   cp $TARGET_BIN_DIR/emeralddb.jar $BIN_DIR
   cp  ./lib/*.jar $BIN_DIR
   echo "Manifest-Version: 1.0" > ./META-INF/MANIFEST.MF
   echo "Class-Path: emeralddb.jar apache-commons-id.jar apache-commons-discovery.jar">> ./META-INF/MANIFEST.MF
   echo "Main-Class: com.emeralddb.example.EmeralddbTest" >> ./META-INF/MANIFEST.MF
   echo -e "\n" >> ./META-INF/MANIFEST.MF
   echo "========================================================"
   cd $BIN_DIR
   ../$JAR -cvfm ../$TARGET_BIN_DIR/EmeralddbTest.jar ../META-INF/MANIFEST.MF ./
   cd $CURRENT_DIR
   echo "========================================================"
   echo "packing: $1.jar has finished."
}


SOURCE_DIR=./src
# define bin directory
BIN_DIR=./bin
# define target directory
TARGET_BIN_DIR=../../../bin
# define JDK_LIB
JDK_LIB=../../../java/jdk_linux64/lib
# define lib directory
LIB_DIR=$TARGET_BIN_DIR

if [ ! -d $BIN_DIR ]; then
   mkdir $BIN_DIR
fi

#if [ $# -le 1 ]; then
#   echo "no argument"
#   usage
#fi

# class is exist or not
#if [ ! -e $SOURCE_DIR/com/emeralddb/example/$2.java ]; then
#   echo "$SOURCE_DIR/com/emeralddb/example/$2.java is not exist."
#   exit 0
#fi

compile_emeralddb
compile_emeralddb_test
pack_emeralddb_test2

while test $# -gt 1
do
   case $1 in
      --class )
      compile_emeralddb
      compile_emeralddb_test
      pack_emeralddb_test $2
      exit 0
      ;;
      --help )
      usesage
      ;;
      * )
      error
   esac
   shift
done

#cp ../../emeralddb $TARGET_BIN_DIR
cp ./src/config.properties $TARGET_BIN_DIR
cp ./edbtest.sh $TARGET_BIN_DIR

# clean
rm -rf $BIN_DIR
