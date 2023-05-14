#! /bin/bash

# On lance les tests autour des programmes prints

PATH=./src/main/bin:"$PATH"

cmpt=0
cmptmax=20

tab=("emptyPrint"
     "emptyPrintln"
     "uniquePrint"
     "uniquePrintln"
     "listExprPrint"
     "listExprPrintln"
     "noSCPrint"
     "noSCPrintln"
     "literalIntPrint"
     "literalIntPrintln"
     "literalFloatPrint"
     "literalFloatPrintln"
     "specialCharPrint"
     "specialCharPrintln"
     "panachePrint"
     "literalIntsPrint"
     "negativeLiteralIntsPrint"
     "literalBoolPrint")

valid_table=("valid"
             "valid"
             "valid"
             "valid"
             "valid"
             "valid"
             "invalid"
             "invalid"
             "valid"
             "valid"
             "valid"
             "valid"
             "valid"
             "valid"
             "valid"
             "valid"
             "valid"
             "valid")


while [ $cmpt -lt $cmptmax ]; do
    echo " "
    echo "testing "${tab[$cmpt]}": should be ${valid_table[$cmpt]}"
    rm -f ./src/test/deca/codegen/${valid_table[$cmpt]}/print/${tab[$cmpt]}.ass 2>/dev/null
    decac ./src/test/deca/codegen/${valid_table[$cmpt]}/print/${tab[$cmpt]}.deca 2>/dev/null
    if [[ ! -f ./src/test/deca/codegen/${valid_table[$cmpt]}/print/${tab[$cmpt]}.ass ]]; then
        echo "File "${tab[$cmpt]}" is not generated"
    else
        echo "File "${tab[$cmpt]}" is generated"
        ima ./src/test/deca/codegen/${valid_table[$cmpt]}/print/${tab[$cmpt]}.ass
    fi
    cmpt=$(($cmpt+1))
done
