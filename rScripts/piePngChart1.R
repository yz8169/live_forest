# Title     : TODO
# Objective : TODO
# Created by: yz
# Created on: 2019/1/2
library(ggplot2)
data = read.table(quote = "", "out.txt", header = T, com = '', sep = "\t", check.names = F)
color_pie <- c('#377EB8', '#E41A1C')
plotData <- c(data[1, "Control"], data[1, "Case"])
labels<-c('Non-CLD', 'CLD')
# pie_chart <- plot_ly(textposition = 'inside', textinfo = 'label+percent',
# marker = list( line = list(color = '#FFFFFF', width = 1))) %>%
# layout(title = "CLD Diagnosis prediction", titlefont = list(size = 15),
# xaxis = list(showgrid = FALSE, zeroline = FALSE, showticklabels = FALSE),
# yaxis = list(showgrid = FALSE, zeroline = FALSE, showticklabels = FALSE),
# showlegend = F, autosize = F, width = 350, height = 300)
png(file = "predict1.png", width = 300, height = 300)
pie(plotData, labels = labels, main="Pie Chart of Countries",col=color_pie)
dev.off()

