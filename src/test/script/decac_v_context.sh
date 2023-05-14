#! /bin/sh

cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/main/bin:"$PATH"

succes=0
echecs=0

test_contx_invalide () {
    # $1 = premier argument.
    if decac -v "$1" 2>&1 | grep -q -e "ContextualError"
    then
        succes=$(( succes + 1 ))
    else
        echecs=$(( echecs + 1 ))
        echo "\033[0;31mSucces inattendu de test_contx sur $1.\033[0m"
    fi
}

for cas_de_test in src/test/deca/context/valid/*/*.deca
do
  if decac -v "$cas_de_test" 2>&1 | \
      grep -q -e '[a-z]'
  then
        echecs=$(( echecs + 1 ))
        echo "\033[0;31mEchec inattendu pour test_contx sur $cas_de_test \033[0m"
  else
        succes=$(( succes + 1 ))
  fi
done


for cas_de_test in src/test/deca/context/invalid/*/*.deca
do
    test_contx_invalide "$cas_de_test"
done

for cas_de_test in src/test/deca/codegen/*/*/*.deca
do
  if decac -v "$cas_de_test" 2>&1 | \
        grep -q -e '[a-z]'
  then
        echecs=$(( echecs + 1 ))
        echo "\033[0;31mEchec inattendu pour test_contx sur $cas_de_test \033[0m"
  else
        succes=$(( succes + 1 ))

  fi
done

echo "Done $((echecs + succes)) contextual tests, \033[0;38m$succes succeded, \033[0;31m$echecs failed\033[0m"

cd src/test/script
