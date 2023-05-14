import matplotlib.pyplot as plt
import csv
import numpy as np

x = []
asin_deca = []
asin_py = []
asin_ulp = []
err_asin = []
errel_asin = []

with open("asin.csv", "r") as file:
    rd = csv.reader(file)
    for row in rd:
        i = (float.fromhex(row[0]))
        asinD = float.fromhex(row[1])
        asinP = np.arcsin(i)
        au = float.fromhex(row[2])
        asin_ulp.append(au)
        x.append(i)
        asin_deca.append(asinD)
        asin_py.append(asinP)
        err_asin.append(asinP - asinD)
        errel_asin.append((asinP-asinD)/asinP)


    fig, axs = plt.subplots(4)

    axs[0].plot(x, asin_py,linestyle="dashed",   label="arcsin(x) python")
    axs[0].plot(x, asin_deca, label="arcsin(x) deca")
    axs[0].legend()

    axs[1].plot(x, err_asin, label="erreur arcsin")
    axs[1].legend()

    axs[2].plot(x, errel_asin, label="erreur relative arcsin")
    axs[2].legend()

    axs[3].plot(x, asin_ulp, label="ulp(asin(x))")
    axs[3].legend()

    plt.show()