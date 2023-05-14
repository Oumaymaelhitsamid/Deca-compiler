#! /bin/sh

cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

test_synt_invalide () {
    # $1 = premier argument.
    if test_synt "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        echo "Echec attendu pour test_synt sur $1."
    else
        echo "Succes inattendu de test_synt sur $1."
        exit 1
    fi
}

for cas_de_test in src/test/deca/syntax/valid/*.deca
do
  if test_synt "$cas_de_test" 2>&1 | \
      grep -q -e ':[0-9][0-9]*:'
  then
      echo "Echec inattendu pour test_synt sur $cas_de_test"
      exit 1
  else
      echo "Succes attendu de test_synt sur $cas_de_test"
  fi
done


for cas_de_test in src/test/deca/syntax/invalid/*.deca
do
    test_synt_invalide "$cas_de_test"
done

cd src/test/script
make clean
