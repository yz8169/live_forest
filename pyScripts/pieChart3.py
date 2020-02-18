# -*- coding: UTF-8 -*-
import pandas as pd
import argparse
import plotly.graph_objs as go
import plotly


def str2file(file, str):
    f = open(file, 'w')
    f.write(str)
    f.close()


df = pd.read_csv("out.txt", sep='\t')
values = [df.ix[0, "Early"], df.ix[0, "Late"]]
color_pie = ['#377EB8', '#E41A1C']
bar = go.Pie(
    labels=["Early", "Late"],
    values=values,
    textposition="inside",
    textinfo='label+percent',
    marker=dict(
        colors=color_pie,
        line=dict(color='#FFFFFF', width=1))
)
data = [bar]
layout = go.Layout(
    width=750,
    height=450,
    title="区分早期和晚期肝纤维化",
    titlefont=dict(size=15),
    hovermode="closest",
    xaxis=dict(
        showgrid=False,
        zeroline=False,
        showticklabels=False,
    ),
    yaxis=dict(
        showgrid=False,
        zeroline=False,
        showticklabels=False,
    ),
    showlegend=False,
    autosize=False,

)
config = dict(
    displaylogo=False,
    showLink=False,
    modeBarButtonsToRemove=["sendData"],
    sendData=False,
    staticPlot=False,
    displayModeBar=False,

)
fig = dict(data=data, layout=layout)
# plotly.offline.plot(fig, filename="basic-scatter.html", config=config)
a = plotly.offline.plot(fig, filename="basic-scatter.html", config=config, output_type="div", include_plotlyjs=False)
str2file("div3.txt", a)
