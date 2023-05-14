#! /bin/bash
# Script testant tout les fichiers valides :
# Crée l'arbre abstrait, le decompile et le stock dans un fichier .deca
# Crée l'arbre abstrait de ce fichier, le decompile, et diff avec le fichier .deca

cd "$(dirname "$0")"/../../.. || exit 1

cmpt=0
mkdir target/temp_test

for f in src/test/deca/syntax/valid/*.deca; do
  decac -p $f > target/temp_test/res0_$cmpt.deca
  cmpt=$(($cmpt+1))
done

cmpt=0
for f in target/temp_test/*.deca; do
  decac -p $f > target/temp_test/res1_$cmpt.res
  cmpt=$(($cmpt+1))
done

cmpt=0
for f in src/test/deca/syntax/valid/*.deca; do
  diff=$(diff target/temp_test/res1_$cmpt.res target/temp_test/res0_$cmpt.deca |grep "" -c)
  if [ $diff == 1  ]
  then
    echo Error on:
    cat target/temp_test/res0_$cmpt.deca
    exit 1
  else
    temp=$(echo $f | cut -d '/' -f 6)
    echo "Succès attendu vérification décompilation pour $temp "
    cmpt=$(($cmpt+1))
  fi
done

rm -r target/temp_test
