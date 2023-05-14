from matplotlib import scale
import matplotlib.pyplot as plt
import csv
import numpy as np

e = []
p = []
ulp = []

with open("ulp.csv", "r") as file:
    rd = csv.reader(file)
    for row in rd:
        e.append(float.fromhex(row[0]))
        p.append(float.fromhex(row[1]))
        ulp.append(float.fromhex(row[2]))


    plt.yscale("log")
    plt.grid(True)
    plt.plot(e, ulp, '-o', label="ulp(10^x)")
    plt.legend()

    plt.show()