#/usr/bin/env bash
echo "Cleaning files..."
antlr4-clean
echo "Done!"
echo "Run started..."
echo "Checking if the java and javac binaries are installed..."

mandatory_binaries=( "java" "javac" )

for mandatory_binary in "${mandatory_binaries[@]}"
do
    if ! type "$mandatory_binary" > /dev/null; then # verify if the binaries are installed
        echo "ERROR! $mandatory_binary missing! Please, install it."
        exit
    fi
done

echo "Binaries installed!"

#1 - javac Files/*.java
#2 - antlr4-build
#3 - java -ea MainGrammar.BdexMain < ../Examples/nomedoficheiro.txt
#4 - java -ea MainGrammar.BdexMain 
#5 - java -ea MainGrammar.BdexMain > Output.java
#6 - java -ea MainGrammar.BdexMain < ../Examples/nomedoficheiro.txt > Output.java

echo "Compiling..."
javac Files/*.java
echo "Successfully compiled!"

echo "Building antlr4..."
antlr4 -visitor SecondaryGrammar/ReadFile.g4
javac SecondaryGrammar/*.java
antlr4-build
echo "Build successfull!"


examples=( "declarationExample.txt" "examples.txt" "table.txt" "biblioteca.txt" )
for example in "${examples[@]}"
do
    echo "Testing example..."
    java -ea MainGrammar.BdexMain < ../examples/$example
    #echo $example
    echo "Test finished!"
done

echo "Run finished."