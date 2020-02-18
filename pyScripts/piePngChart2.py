# -- coding: UTF-8 --
import pandas as pd
import matplotlib.pyplot as plt


def str2file(file, str):
    f = open(file, 'w')
    f.write(str)
    f.close()

plt.switch_backend('agg')
plt.rcParams['font.sans-serif'] = ['SimHei']
plt.rcParams['axes.unicode_minus'] = False

df = pd.read_csv("out.txt", sep='\t')
values = [df.ix[0, "Fibrosis"], df.ix[0, "Cirrhosis"]]
color_pie = ['#377EB8', '#E41A1C']
labels=["Fibrosis", "Cirrhosis"]

fig = plt.figure(figsize=(3.5, 3.5), dpi=100)
plt.pie(values, labels=labels, colors=color_pie, autopct="%1.1f%%",
        wedgeprops=dict(width=1, edgecolor='#FFFFFF'),
        )
plt.title(u'区分肝纤维化和肝硬化', fontdict=dict(
    fontsize=15
))
plt.savefig("predict2.png")
