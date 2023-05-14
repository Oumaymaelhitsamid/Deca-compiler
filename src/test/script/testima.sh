#!/bin/bash

expected=$(./src/test/script/expected.py $1)
output=$(ima ${1%.deca}.ass)

if [[ "$expected" != "$output"* ]]; then
    echo "$1 failed"
    echo "expected $expected"
    echo "got $output"
fi
