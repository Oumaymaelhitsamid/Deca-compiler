#!/bin/bash

#! /bin/sh

PATH=./src/main/bin:"$PATH"

for f in $(find src/test/deca/codegen -name "*.deca")
do
    echo "testing $f"
    decac "$f"
	./src/test/script/testima.sh $f
done
