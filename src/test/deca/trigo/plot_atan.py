import matplotlib.pyplot as plt
import csv
import numpy as np

x = []
atan_deca = []
atan_py = []
atan_ulp = []
err_atan = []
errel_atan = []

with open("atan.csv", "r") as file:
    rd = csv.reader(file)
    for row in rd:
        i = (float.fromhex(row[0]))
        atanD = float.fromhex(row[1])
        atanP = np.arctan(i)
        atan_ulp.append(float.fromhex(row[2]))
        x.append(i)
        atan_deca.append(atanD)
        atan_py.append(atanP)
        err_atan.append(atanP - atanD)
        errel_atan.append((atanP-atanD)/atanP)


    fig, axs = plt.subplots(4)

    axs[0].plot(x, atan_deca, label="arctan(x) deca")
    axs[0].legend()

    axs[1].plot(x, err_atan, label="erreur arctan")
    axs[1].legend()

    axs[2].plot(x, errel_atan, label="erreur relative arctan")
    axs[2].legend()


    axs[3].plot(x, atan_ulp, label="ulp(atan(x))")
    axs[3].legend()

    plt.show()