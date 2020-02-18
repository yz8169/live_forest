# Title     : TODO
# Objective : TODO
# Created by: yz
# Created on: 2018/12/27
library(randomForest)
library(magrittr)
library(dplyr)
library(xlsx)
# Load data ---------------------------------------------------------------
rm(list = ls())
dealResult <- function(data){
    for (i in 1 : nrow(data)) {
        if (1 %in% data[i,] | 0 %in% data[i,]) {
            data[i, which.max(data[i,])] <- .999
            data[i, which.min(data[i,])] <- .001
        }
    }
    data
}
load('LiveForest_load.RData')
new_input <- read.xlsx("input.xlsx", 1, check.names = F)
df <- new_input[,c("SampleID","Age","AST","ALT","PLT","Tyr","TCA")]
RF_pred_1 <- predict(RF_3_new, df, type = 'prob') %>% data.frame
RF_pred_1 <- dealResult(RF_pred_1)
RF_pred_2 <- predict(RF_2_new, df, type = 'prob') %>% data.frame
RF_pred_2 <- dealResult(RF_pred_2)
RF_pred_3 <- predict(RF_1_new, df, type = 'prob') %>% data.frame
RF_pred_3 <- dealResult(RF_pred_3)
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
out<-merge(df_final, new_input,by=c("row.names","SampleID","Age","AST","ALT","PLT","Tyr","TCA"),sort=F)
write.table(out, 'out.txt', sep = '\t', quote = F, row.names = FALSE)
