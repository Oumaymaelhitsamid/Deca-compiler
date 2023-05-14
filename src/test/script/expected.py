#!/bin/python3

import sys

if len(sys.argv) == 2:
    path = sys.argv[1]
    with open(path, "r") as f:
        l = f.readline()
        while l != "\n" and len(l) != 0:
            print(l.lstrip("/ ").rstrip("\n"))
            l = f.readline()