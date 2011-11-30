#!/bin/bash

set -o verbose
set -o xtrace

if ! test -a nodo.class
then
        echo El bytecode del nodo no existe 1>&2
        exit 1
fi

if ! test -r nodo.class
then
        echo Se encontro el bytecode del nodo, pero no se tienen permisos de lectura 1>&2
        exit 2
fi

if ! test -s nodo.class
then
        echo El bytecode del nodo esta vacio 1>&2
        exit 3
fi

PORT=""
KNOWNS=""
LIBRARY=""
ID=""

while getopts ":p:c:b:i:" opt
do
	case $opt in
		p) PORT=$OPTARG;;
		c) KNOWNS=$OPTARG;;
		b) LIBRARY=$OPTARG;;
		i) ID=$OPTARG;;
		\?) ;;
	esac
done

PORT=${PORT:?Puerto de escucha no especificado}
KNOWNS=${KNOWNS:?Lista de nodos adyacentes no especificada}
LIBRARY=${LIBRARY:?Libreria de musica no especificada}
ID=${ID:?Id del nodo no especificada}

java -classpath nanoxml.jar:jaudiotagger.jar:. -Djava.rmi.server.codebase=file:output/ nodo -p $PORT -c $KNOWNS -b $LIBRARY -i $ID
