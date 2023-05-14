import matplotlib.pyplot as plt
from matplotlib.ticker import MultipleLocator
import numpy as np
import csv
import math as m

x = []
sin_deca = []
cos_deca = []
sin_py = []
cos_py = []
err_sin = []
err_cos = []
err_rel_sin = []
err_rel_cos = []
ulp_sin = []
ulp_cos = []

with open("sin_cos.csv", "r") as file:
    rd = csv.reader(file)
    for row in rd:
        i = float.fromhex(row[0])
        sinus = float.fromhex(row[1])
        cosinus = float.fromhex(row[2])
        us = float.fromhex(row[3])
        uc = float.fromhex(row[4])

        x.append(i)
        sin_deca.append(sinus)
        cos_deca.append(cosinus)
        ulp_sin.append(m.ulp(np.sin(i)) - us)
        ulp_cos.append(uc)
        sin_py.append(np.sin(i))
        cos_py.append(np.cos(i))
        abs_error_sin = np.sin(i) - sinus
        abs_error_cos = np.cos(i) - cosinus
        err_sin.append(abs_error_sin)
        err_cos.append(abs_error_cos)
        err_rel_sin.append(abs_error_sin / np.abs(np.sin(i)))
        err_rel_cos.append(abs_error_cos / np.abs(np.cos(i)))

    sinorcos = True

    fig, axs = plt.subplots(4)

    if sinorcos: # SIN
        axs[0].plot(x, sin_deca, label="sin(x) deca", linewidth=2)
        axs[0].xaxis.set_major_locator(plt.MultipleLocator(np.pi / 2))
        axs[0].legend()

        axs[1].plot(x, err_sin, label="erreur sin")
        axs[1].set_ylim([-3e-7, 3e-7])
        axs[1].legend()

        axs[2].plot(x, err_rel_sin, label="erreur relative sin")
        axs[2].set_ylim([-2e-6, 2e-6])
        axs[2].legend()

        axs[3].plot(x, ulp_sin, label="ulp(sin(x))")
        axs[3].legend()

    else: # COS
        axs[0].plot(x, cos_deca, label="cos(x) deca", linewidth=2)
        axs[0].xaxis.set_major_locator(plt.MultipleLocator(np.pi / 2))
        axs[0].legend()

        axs[1].plot(x, err_cos, label="erreur cos")
        axs[1].set_ylim([-3e-7, 3e-7])
        axs[1].legend()

        axs[2].plot(x, err_rel_cos, label="erreur relative cos")
        axs[2].set_ylim([-2e-6, 2e-6])
        axs[2].legend()

        axs[3].plot(x, ulp_cos, label="ulp(cos(x))")
        axs[3].legend()

    plt.show()
