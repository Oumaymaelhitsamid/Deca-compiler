#! /bin/sh

cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

test_contx_invalide () {
    # $1 = premier argument.
    if test_context "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        echo "Echec attendu pour test_contx sur $1."
    else
        echo "\033[0;31mSucces inattendu de test_contx sur $1.\033[0m"
        exit 1
    fi
}

for cas_de_test in src/test/deca/context/valid/created/*.deca
do
  if test_context "$cas_de_test" 2>&1 | \
      grep -q -e ':[0-9][0-9]*:'
  then
      echo "\033[0;31mEchec inattendu pour test_contx sur $cas_de_test \033[0m"
      exit 1
  else
      echo "Succes attendu de test_contx sur $cas_de_test"
  fi
done


for cas_de_test in src/test/deca/context/invalid/created/*.deca
do
    test_contx_invalide "$cas_de_test"
done

cd src/test/script
make clean
