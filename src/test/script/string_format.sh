#! /bin/bash
# Script permettant de tester les chaines de carractère

mkdir ../../../target/temp_test

in_print=("\"Bonjour\""
          "\"Te\\\"st\""         #Te\"st
          "\"Te\\\\st\""         #Te\\st
          "\"Test\\\"\""           #Test\"
)
out_print=("Bonjour"
           "Te\"st"     #Te"st
           "Te\\st"     #Te\st
           "Test\""     #Test"
)
cmpt=0
for i in ${in_print[*]}
  do
    echo {print\($i\)\;} > ../../../target/temp_test/res.deca
    decac ../../../target/temp_test/res.deca
    ima ../../../target/temp_test/res.ass >  ../../../target/temp_test/print.txt
    echo ${out_print[$cmpt]} > ../../../target/temp_test/waited.txt
    diff=$(diff -q ../../../target/temp_test/print.txt ../../../target/temp_test/waited.txt | grep "" -c)
    if [ $diff == 1 ]
    then
      echo Error on ${in_print[$cmpt]}
      echo Result :
      cat ../../../target/temp_test/print.txt
      rm -r ../../../target/temp_test
      exit 1
    fi
    cmpt=$(($cmpt+1))

  done

rm -r ../../../target/temp_test
exit 0
