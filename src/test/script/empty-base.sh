#! /bin/bash

# On lance les tests autour des programmes vides d'instructions

PATH=./src/main/bin:"$PATH"

cmpt=0
cmptmax=8

tab=("void0"
     "void1"
     "empty"
     "emptyInst"
     "emptyInsts"
     "commentSemiColon"
     "twoMains"
     "twoMains1")

valid_table=("valid"
             "valid"
             "valid"
             "valid"
             "valid"
             "valid"
             "invalid"
             "invalid")


while [ $cmpt -lt $cmptmax ]; do
    echo " "
    echo "testing "${tab[$cmpt]}": should be ${valid_table[$cmpt]}"
    rm -f ./src/test/deca/codegen/${valid_table[$cmpt]}/void/${tab[$cmpt]}.ass 2>/dev/null
    decac ./src/test/deca/codegen/${valid_table[$cmpt]}/void/${tab[$cmpt]}.deca 2>/dev/null
    if [[ ! -f ./src/test/deca/codegen/${valid_table[$cmpt]}/void/${tab[$cmpt]}.ass ]]; then
        echo "File "${tab[$cmpt]}" is not generated"
    else
        echo "File "${tab[$cmpt]}" is generated"
    fi
    cmpt=$(($cmpt+1))
done
