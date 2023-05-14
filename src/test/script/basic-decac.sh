#! /bin/sh

# Test de l'interface en ligne de commande de decac.
# On ne met ici qu'un test trivial, a vous d'en ecrire de meilleurs.

PATH=./src/main/bin:"$PATH"

in=src/test/deca/codegen/valid/void/empty.deca

decac_moins_b=$(decac -b)

if [ "$?" -ne 0 ]; then
    echo "ERREUR: decac -b a termine avec un status different de zero."
    exit 1
fi

if [ "$decac_moins_b" = "" ]; then
    echo "ERREUR: decac -b n'a produit aucune sortie"
    exit 1
fi

if echo "$decac_moins_b" | grep -i -e "erreur" -e "error"; then
    echo "ERREUR: La sortie de decac -b contient erreur ou error"
    exit 1
fi

decac_p=$(decac -p $in)

if [ "$?" -ne 0 ]; then
    echo "ERREUR: decac -p exited with non zero status"
    exit
fi

if echo "$decac_p" | grep -i -e "erreur" -e "error"; then
    echo "ERREUR: La sortie de decac -p contient erreur ou error"
    exit 1
fi

decac_i=$(echo "{}" | decac -pi)

if echo "$decac_i" | grep -i -e "erreur" -e "error"; then
    echo "ERREUR: La sortie de decac -i contient erreur ou error"
    exit 1
fi

decac_Para=$(echo "{}" | decac -iP 8)

if echo "$decac_Para" | grep -i -e "erreur" -e "error"; then
    echo "ERREUR: La sortie de decac -P 8 contient erreur ou error"
    exit 1
fi

decac_Para0=$(decac -P 0 2>&1)

if ! echo "$decac_Para0" | grep -i "positive"; then
    echo "ERREUR: La sortie de decac -P 0 n'a pas produit d'erreur"
    exit 1
fi

decac_Pinvalid=$(decac -P $in 2>&1)

if ! echo "$decac_Pinvalid" | grep -i "incorrect"; then
    echo "ERROR: invalid thread number gives no error"
    exit
fi

decac_d=$(decac -d $in)
decac_dd=$(decac -dd $in)
decac_ddd=$(decac -ddd $in)
decac_dddd=$(decac -dddd $in)

decac_w=$(decac -w $in)

decac_h=$(decac -h)

decac_r1=$(decac -r 1 $in 2>&1)
decac_r2=$(decac -r 2000 $in 2>&1)

if ! echo "$decac_r1" | grep -i "between"; then
    echo "ERROR: too low register amount does not produce error"
    exit 1
fi
if ! echo "$decac_r2" | grep -i "between"; then
    echo "ERROR: too high register amount does not produce error"
    exit 1
fi

decac_pv_mutex=$(decac -pv 2>&1)

if ! echo "$decac_pv_mutex" | grep -i "together"; then
    echo "ERROR: using -p and -v together did not fail"
    exit 1
fi

decac_nofile=$(decac 2>&1)
decac_notdecafile=$(decac eee.dec 2>&1)

if ! echo "$decac_nofile" | grep -i "error"; then
    echo "ERROR: using no files does not output error"
    exit 1
fi

if ! echo "$decac_notdecafile" | grep -i "not a"; then
    echo "ERROR: using invalid file does not output error"
    exit 1
fi

decac_invalid=$(decac -Y 2>&1)

if ! echo "$decac_invalid" | grep -i "unrecognized"; then
    echo "ERROR: unrecognized option does not produce error"
    exit 1
fi
