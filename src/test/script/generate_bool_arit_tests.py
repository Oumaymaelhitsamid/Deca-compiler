#!/bin/python

import os

def printdeclare(type, name, value):
    print(type, name, end='')
    if value:
        print("=", value, end='')
    print(";", end='')

bool_bin_ops = {
    'or':'||',
    'and':'&&'
}
bool_un_ops = ['!']

arit_bin_ops = {
    'add':'+',
    'sub':'-',
    'mul':'*',
    'div':'/',
    'mod':'%'
}
arit_un_ops = ['-']

comp = {
    'eq':'==',
    'neq':'!=',
    'lt':'<',
    'lte':'<=',
    'gt':'>',
    'gte':'>='
}

def writeprograms(operators, suffix, left, right):
    for name, op in operators.items():
        filepath = os.path.join(os.path.abspath("src/test/deca/codegen/generated"), name +'_'+ suffix +'.deca')
        decafile = open(filepath, "w")
        prog = ""
        if eval(right) == 0 and (op == '/' or op == '%'):
            comment = ''
        else:
            comment = str(eval(left+ op +right)).lower()
        prog += "// " + comment + "\n\n"
        prog += "{\n"
        prog += 'print('+ left.lower() + op +right.lower() +');' + "\n"
        prog += "}"
        print("writing "+ filepath)
        decafile.write(prog)
        decafile.close()

writeprograms(comp, 'int', '1', '1')
writeprograms(comp, 'intfloat', '1', '0.0')
writeprograms(comp, 'float', '10.0', '0.0')
writeprograms(arit_bin_ops, 'int', '10', '0')
writeprograms(arit_bin_ops, 'float', '1.0', '2.0')
#writeprograms(arit_comp, 'bool', 'True', 'False')