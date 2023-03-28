MODE=""
COMPRESSION_MODE="COMPRESS"
DECOMPRESSION_MODE="DECOMPRESS"
LOG4J_CONFIG_LOC="log4j2.xml"

if [ $# -ne 2 ]; then
    echo "Invalid number of arguments, expect 3 arguments."
    echo "For compression: ./huffman.sh [mode] [inputPath]"
    echo "For decompression: ./huffman.sh [mode] [compressedPath]"
fi

if [[ ${1^^} =~ $DECOMPRESSION_MODE ]]; then
    MODE=$DECOMPRESSION_MODE
elif [[ ${1^^} =~ $COMPRESSION_MODE ]]; then
    MODE=$COMPRESSION_MODE
else
    echo "Expected either COMPRESS or DECOMPRESS as the mode."
    exit 1
fi

java -Dlog4j.configurationFile=$LOG4J_CONFIG_LOC -Dmode=$MODE \
    -cp "target/huffman-1.0.0.jar:lib/*" com.qfi.huffman.HuffmanCode $2

exit 0
