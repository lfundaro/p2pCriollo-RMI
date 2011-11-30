#!/bin/bash

#set -o verbose
#set -o xtrace

if ! test -a cliente.class
then
	echo El bytecode del cliente no existe 1>&2
	exit 1
fi

if ! test -r cliente.class
then
	echo Se encontro el bytecode del cliente, pero no se tienen permisos de lectura 1>&2
	exit 2
fi

if ! test -s cliente.class
then
	echo El bytecode del cliente esta vacio 1>&2
	exit 3
fi

PORT=""
NODE=""
DOWNLOADS=""

while getopts ":p:n:d:" opt
do
	case $opt in
		p) PORT=$OPTARG;;
		n) NODE=$OPTARG;;
		d) DOWNLOADS=$OPTARG;;
		\?) ;;
	esac
done

PORT=${PORT:?Puerto del servidor no especificado}
NODE=${NODE:?Nodo no especificado}
DOWNLOADS=${DOWNLOADS:-'.'}

java cliente -p $PORT -n $NODE -d $DOWNLOADS
