# -- coding: UTF-8 --
import pandas as pd
import matplotlib.pyplot as plt

plt.switch_backend('agg')
plt.rcParams['font.sans-serif'] = ['SimHei']
plt.rcParams['axes.unicode_minus'] = False

df = pd.read_csv("out.txt", sep='\t')
values = [df.ix[0, "Control"], df.ix[0, "Case"]]
color_pie = ['#377EB8', '#E41A1C']

fig = plt.figure(figsize=(3.5, 3.5), dpi=100)
plt.pie(values, labels=["Non-CLD", "CLD"], colors=color_pie, autopct="%1.1f%%",
        wedgeprops=dict(width=1, edgecolor='#FFFFFF'),
        )
plt.title(u'诊断慢性肝病', fontdict=dict(
    fontsize=15
))
plt.savefig("predict1.png")
