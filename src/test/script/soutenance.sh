#! /bin/sh

echo "----------------------------------------"
echo "           Script Soutenance            "
echo "----------------------------------------"
echo ""
echo Entrée pour commencer
read temp
echo "----------------------------------------"
echo "         Démonstration de decac         "
echo "----------------------------------------"
read temp
echo ""
echo "Execution de decac pour un programme simple puis éxécution"
echo "decac HelloWorld.deca"
echo ""
decac ./soutenance/HelloWorld.deca
echo "ima HelloWorld.ass"
echo ""
ima ./soutenance/HelloWorld.ass
read temp
echo ""
echo "Execution de decac -b : Affichage de la bannière"
echo ""
decac -b
read temp
echo ""
echo "Execution de decac -d : Affichage des messages de debug"
echo "decac -d HelloWorld.deca"
echo ""
decac -d ./soutenance/HelloWorld.deca
read temp
echo ""
echo "Execution de decac -h : Affichage le message d'aide"
echo ""
decac -h
read temp
echo ""
echo "Execution de decac -i : Ecrire un programme via stdin (test à la compilation)"
echo ""
decac -i
echo ""
echo "Execution de decac -n : Desactivation des vérifications à l'éxécution"
echo "Démonstration avec un fichier perf : Reduction du nombre d'instruction"
echo "decac syracuse_s.deca"
decac ./soutenance/syracuse_s.deca
echo "ima -s syracuse_s.ass"
ima -s ./soutenance/syracuse_s.ass
echo ""
echo "decac -n syracuse_s.deca"
decac -n ./soutenance/syracuse_s.deca
echo "ima -s syracuse_s.ass"
ima -s ./soutenance/syracuse_s.ass
read temp
echo ""
echo "Execution de decac -P : Compilation parallèle"
echo "decac -P 2 thead1.deca thread2.deca"
decac -P 2 ./soutenance/thread1.deca ./soutenance/thread2.deca
echo "ima thread1.ass "
ima ./soutenance/thread1.ass
echo "ima thread2.ass "
ima ./soutenance/thread2.ass
read temp
echo ""
echo "Execution de decac -p : Affichage le programme décompilé puis s'arrête"
echo "decac -p HelloWorld.deca"
decac -p ./soutenance/HelloWorld.deca
read temp
echo ""
echo "Execution de decac -r : Indique le nombre de registre maximum à utiliser"
echo ""
read temp
echo ""
echo "Execution de decac -v : Vérifie le programme contextuellement puis s'arrête"
echo "decac -v HelloWorld.deca"
decac -v ./soutenance/HelloWorld.deca
echo ""
echo "Programme contextuellement faux : "
echo "decac -v context_false_op.deca"
cat ./soutenance/context_false_op.deca
decac -v ./soutenance/context_false_op.deca
read temp
echo ""
echo "Execution de decac -w : Active les warning"
echo "Demonstration avec le warning limitant le nombre de lettre d'un ident"
echo ""
echo "decac warning_demo.deca"
decac ./soutenance/warning_demo.deca
echo ""
echo "decac -w warning_demo.deca"
decac -w ./soutenance/warning_demo.deca
read temp
echo "----------------------------------------"
echo "        Etapes de la compilation        "
echo "----------------------------------------"
echo ""
echo "Démonstration via un programme simple objet"
echo ""
read temp
echo "Programme utilisé Demo_object.deca : "
cat ./soutenance/Demo_object.deca
read temp
echo "Construction de l'arbre abstrait : "
./launchers/test_synt ./soutenance/Demo_object.deca
read temp
echo "Construction de l'arbre décoré : "
./launchers/test_context ./soutenance/Demo_object.deca
read temp
echo " Code généré : "
cat ./soutenance/Demo_object.ass
read temp
echo "------------ Résultat obtenu :"
ima ./soutenance/Demo_object.ass
read temp
echo ""
echo "----------------------------------------"
echo "Illustration d'un exemple complexe objet"
echo "----------------------------------------"
echo ""
echo "Programme Rue (UML sur le PPT)"
cat ./soutenance/rue_soutenance.deca
echo ""
echo "Donne à l'éxécution :"
decac ./soutenance/rue_soutenance.deca
ima ./soutenance/rue_soutenance.ass
echo "----------------------------------------"
echo "   Illustration des messges d'erreur    "
echo "----------------------------------------"
echo ""
read temp
echo "Illustration de quelques messages d'erreur"
echo ""
echo "Lexique & Synaxique : "
echo "Programme avec un caractère incorrect : "
cat ./soutenance/lex_err_1.deca
decac ./soutenance/lex_err_1.deca
echo ""
read temp
echo "Programme avec un include incorrect : "
cat ./soutenance/lex_err_2.deca
decac ./soutenance/lex_err_2.deca
echo ""
read temp
echo "Contextuelles : "
echo "Programme mélangeant les types :"
cat ./soutenance/cont_err_1.deca
decac ./soutenance/cont_err_1.deca
echo ""
read temp
echo "Programme redéfinissant une méthode :"
cat ./soutenance/cont_err_2.deca
decac ./soutenance/cont_err_2.deca
read temp
echo ""
echo " Erreur à l'éxécution :"
echo "Divsion par zero : "
cat ./soutenance/gen_err_1.deca
decac ./soutenance/gen_err_1.deca
ima ./soutenance/gen_err_1.ass
read temp
echo ""
echo "----------------------------------------"
echo "     Illustration des performances      "
echo "----------------------------------------"
echo ""
read tmp
echo "Evaluation des performances des fichiers fournis : "
decac -n ./soutenance/syracuse_s.deca
decac -n ./soutenance/ln2.deca
decac -n ./soutenance/ln2_fct.deca
echo "Syracuse : "
ima -s ./soutenance/syracuse_s.ass
read tmp
echo ""
echo "Ln2 : "
ima -s ./soutenance/ln2.ass
read tmp
echo ""
echo "Ln2 (via objet) : "
ima -s ./soutenance/ln2_fct.ass
read tmp
echo ""
echo "Tests supplémentaires :"
echo "7! = "
decac ./soutenance/facto_7.deca
ima -s ./soutenance/facto_7.ass
read tmp
echo ""
echo "----------------------------------------"
echo "        Tests de l'extension            "
echo "----------------------------------------"
echo ""
echo "Cosinus : "
cat ./soutenance/cos.deca
echo ""
decac ./soutenance/cos.deca
ima ./soutenance/cos.ass
echo ""
read tmp
echo "Sinus : "
decac ./soutenance/sin.deca
ima ./soutenance/sin.ass
echo ""
read tmp
echo "Arcsin : "
decac ./soutenance/asin.deca
ima ./soutenance/asin.ass
echo ""
read tmp
echo "Arctan : "
decac ./soutenance/atan.deca
ima ./soutenance/atan.ass
echo ""
read tmp
echo "Ulp : "
decac -n ./soutenance/ulp.deca
ima ./soutenance/ulp.ass
echo ""
read tmp
echo "Intégration d'un sinus de 0 à Pi"
cat ./soutenance/trapeze.deca
decac ./soutenance/trapeze.deca
ima ./soutenance/trapeze.ass
echo ""
read tmp
