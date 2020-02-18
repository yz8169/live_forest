# Title     : TODO
# Objective : TODO
# Created by: yz
# Created on: 2018/12/19
library(randomForest)
library(magrittr)
library(dplyr)
rm(list = ls())
load('LiveForest_load.RData')
df <- read.csv("input.txt")
RF_pred_1 <- predict(RF_3_new, df, type = 'prob')
RF_pred_2 <- predict(RF_2_new, df, type = 'prob')
RF_pred_3 <- predict(RF_1_new, df, type = 'prob')
df_final <- data.frame(df, RF_pred_1, RF_pred_2, RF_pred_3)
df_final$results <- NA
for (i in 1 : nrow(df_final)) {
    if (df_final$Control[i] > 0.5) {
        df_final$results[i] <- "Normal"
    } else if (df_final$Cirrhosis[i] >= 0.5) {
        df_final$results[i] <- "Cirrhosis"
    } else if (df_final$Early[i] > 0.5) {
        df_final$results[i] <- "Early Fibrosis"
    } else if (df_final$Late[i] >= 0.5) {
        df_final$results[i] <- "Late Fibrosis"
    }
}
df_final <- df_final[, c('SampleID', 'Age', 'AST', 'ALT', 'PLT', 'Tyr', 'TCA', 'results')]
write.table(df_final, 'out.txt', sep = '\t', quote = F,row.names=FALSE)

