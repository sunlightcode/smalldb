#!/bin/bash

# define compile
JAVA_HOME=../../../java/jdk_linux64
JAVAC=$JAVA_HOME/bin/javac
JAR=$JAVA_HOME/bin/jar

CURRENT_DIR=`pwd`

# compile driver
cd ../../driver/java
`./build.sh`
cd $CURRENT_DIR

# define source directory
SOURCE_DIR=./src

# define bin directory
BIN_DIR=./bin
# define target directory
TARGET_BIN_DIR=../../../bin

# define JDK_LIB
JDK_LIB=../../../java/jdk_linux64/lib

if [ ! -d $BIN_DIR ]; then
   mkdir $BIN_DIR
fi

if [ ! -d $TARGET_BIN_DIR ]; then
   mkdir $TARGET_BIN_DIR
fi

# delete all .class file
find . -name "*.class" |xargs rm -rf
if [ ! -d ./META-INF ]; then
   mkdir ./META-INF
fi

echo "Manifest-Version: 1.0" >./META-INF/MANIFEST.MF
echo "Class-Path: lib/jfreechart-1.0.15.jar lib/jcommon-1.0.18.jar" >> \
   ./META-INF/MANIFEST.MF
echo "Main-CLass: com.emeralddb.base.EmeralddbGraphMonitor" >> \
   ./META-INF/MANIFEST.MF
echo -e "\n" >> ./META-INF/MANIFEST.MF

$JAVAC -classpath \
    "$JDK_LIB:./lib/jcommon-1.0.18.jar:./lib/jfreechart-1.0.15.jar" \
    `find $SOURCE_DIR -name "*.java" -print` -d $BIN_DIR

cp -R ./lib $TARGET_BIN_DIR
cp -R ./META-INF $BIN_DIR
cd $BIN_DIR
../$JAR -cvfm ../$TARGET_BIN_DIR/EdbMonitor.jar ./META-INF/MANIFEST.MF ./
cd ..

# clean
#rm -rf $BIN_DIR
#rm -rf META-INF
