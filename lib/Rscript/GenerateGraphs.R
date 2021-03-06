library(ggplot2) 
library(plyr)


games = read.csv("/Users/fesnault/database.csv", head=TRUE, sep=",")
clearGames = subset(games, whiteElo>1600 & blackElo>1600, result != "-1") 

nbData = length(games[,1])
nbClearData = length(clearGames[,1])
sprintf("INFO: %s parties dans le dataset", nbData)
sprintf("INFO: %s coups moyen joué par partie", mean(games[,3]))
sprintf("INFO: %s coups joués au total", sum(games[,3]))

########################
##     ELO RATING     ##
########################

if(nbClearData > 0) {

whiteElo = clearGames[,1]
blackElo = clearGames[,2]
moves = clearGames[,3]
result = clearGames[,4]
stringResult = revalue(factor(clearGames[,4]), c("0"="White", "1"="Black", "2"="Draw"))

whiteWin = as.numeric(result == 0)
winnerElo = c(1:nbClearData)
higherEloWin = as.numeric((whiteElo > blackElo & result == 0) | (whiteElo < blackElo & result == 1))
eloRating = c(clearGames[,1], clearGames[,2])
nbUniqueEloRating = length(unique(eloRating))

for(i in 1:nbClearData) {
	if(result[i] == 0) {
		winnerElo[i] = whiteElo[i]
	} else if(result[i] == 1) {
		winnerElo[i] = blackElo[i]
	} else {
		winnerElo[i] = max(whiteElo[i],blackElo[i])
	}
}



if(nbUniqueEloRating < 100) {
	sprintf("INFO: Les parties ne contiennent pas d'information sur le classement Elo des joueurs")
} else {
	sprintf("INFO: Analyse de %s Elo Rating", nbUniqueEloRating)
	
	differenceElo = abs(whiteElo-blackElo)
	df1 <- data.frame(differenceElo, moves)
	df2 <- subset(data.frame(differenceElo, higherEloWin, result), result != 2)
	df3 <- subset(data.frame(whiteElo, whiteWin, result), result != 2)
	df4 <- subset(data.frame(winnerElo, stringResult), stringResult != "3")

	p1 = qplot(eloRating, geom="histogram", binwidth = 25) 			+ xlab("Elo Rating") 				+ ylab("Count") 							+ ggtitle("1. Distribution of Elo ratings")			+ xlim(1600, 3000)
	p2 = qplot(differenceElo, geom="histogram", binwidth=25) 		+ xlab("Difference in Elo Rating") 	+ ylab("Count") 							+ ggtitle("2. Difference Elo ratings")				+ xlim(0, 1000)
	p3 = ggplot(df1, aes(differenceElo, moves)) 					+ xlab("Difference in Elo Rating") 	+ ylab("Ply per Game") 						+ ggtitle("3. Ply per Game") 						+ stat_smooth()									+ xlim(0, 1000)
	p4 = ggplot(df2, aes(differenceElo, higherEloWin))				+ xlab("Difference in Elo Rating") 	+ ylab("% Games win by Higher Elo Rating") 	+ ggtitle("4. % Games win by Higher Elo Rating") 	+ stat_smooth(method="glm", family="binomial")	+ xlim(0, 1000)
	p5 = ggplot(df3, aes(whiteElo, whiteWin)) 						+ xlab("Elo Rating") 				+ ylab("% Games win by White Player") 		+ ggtitle("5. % Games win by White Player") 		+ stat_smooth(method="glm", family="binomial")	+ xlim(1600, 2800)
	p6 = ggplot(df4, aes(winnerElo, fill = stringResult)) 			+ xlab("Average Elo Rating of Players") + ylab("% Games Resulting in a Win") 	+ ggtitle("6. % Games Resulting in a Win By Color") + stat_density(aes(y = ..density..), position = "fill", color = "grey") + scale_fill_manual(values=c("#ffffff", "#000000", "#99CCFF", "#cccccc")) + xlim(1600, 2800)
}

}


########################
##     DATE GAME      ##
########################

whiteElo = games[,1]
blackElo = games[,2]
moves = games[,3]
result = games[,4]
date = as.numeric(as.character(games[,5]))
nbUniqueDate = length(unique(date))

whiteWin = as.numeric(result == 0)
winnerElo = c(1:nbData)
higherEloWin = as.numeric((whiteElo > blackElo & result == 0) | (whiteElo < blackElo & result == 1))
stringResult = revalue(factor(games[,4]), c("0"="White", "1"="Black", "2"="Draw"))

for(i in 1:nbData) {
	if(result[i] == 0) {
		winnerElo[i] = whiteElo[i]
	} else if(result[i] == 1) {
		winnerElo[i] = blackElo[i]
	} else {
		winnerElo[i] = max(whiteElo[i],blackElo[i])
	}
}

if(nbUniqueDate < 10) {
	sprintf("INFO: Les parties ne sont pas assez espacées dans le temps pour faire une analyse selon la date")
} else {
	sprintf("INFO: Analyse de %s dates", nbUniqueDate)
	
	df5 <- data.frame(date, moves)   
	df6 = subset(data.frame(date, whiteWin, result), result != 2)
	df7 <- subset(data.frame(date, stringResult), stringResult != "3")

	p7 = qplot(date, geom="histogram", binwidth = 1) 	+ xlim(1850, 2015)	+ xlab("Year") 	+ ylab("Count") 						+ ggtitle("7. Distribution of Date Match")
	p8 = ggplot(df5, aes(date, moves)) 					+ xlim(1950, 2015)	+ xlab("Date") 	+ ylab("Ply per Game") 					+ ggtitle("8. Ply per Game") 					+ stat_smooth()
	p9 = ggplot(df6, aes(date, whiteWin)) 				+ xlim(1850, 2015)	+ xlab("Date") 	+ ylab("% Games win by White Player") 	+ ggtitle("9. % Games win by White Player") 	+ stat_smooth(method="glm", family="binomial")
	p10 = ggplot(df7, aes(date, fill = stringResult)) 	+ xlim(1850, 2015)	+ stat_density(aes(y = ..density..), position = "fill", color = "black") + xlab("Date") + ylab("% Games Resulting in a Win") + ggtitle("10. % Games Resulting in a Win By Color") + scale_fill_manual(values=c("#ffffff", "#000000", "#99CCFF", "#cccccc"))
}

########################
##       OPENING      ##
########################

if(nbUniqueDate > 10) {
	firstMoveWhite = revalue(games[,6], c(
		"a3"="Other",
		"a4"="Other",
		"c3"="Other",
		"d3"="Other",
		"e3"="Other",
		"g4"="Other",
		"h3"="Other",
		"h4"="Other",
		"Nh3"="Other",
		"h6"="Other",
		"f3"="Other",
		"Na3"="Other",
		"Bxc1"="Other",
		"Bxg2"="Other",
		"d5"="Other",
		"Bd3"="Other",
		"Kb8"="Other",
		"Bxg3"="Other",
		"f5"="Other",
		"Nbc6"="Other",
		"exd5"="Other",
		"Nd3"="Other",
		"Bxf3"="Other",
		"Qf5"="Other",
		"Bh3"="Other",
		"Nf5"="Other",
		"Bh4+"="Other",
		"Kh6"="Other",
		"Qf7+"="Other",
		"c7"="Other",
		"Rh1"="Other",
		"Nf4"="Other",
		"Kf2"="Other",
		"Ne5"="Other",
		"Bf4"="Other",
		"Ra3"="Other",
		"Rc7"="Other",
		"Rc1+"="Other",
		"Rg5"="Other",
		"c5"="Other",
		"Rc3"="Other",
		"Qf2+"="Other",
		"dxc7"="Other",
		"Kb3"="Other",
		"Rf8+"="Other",
		"Ne6+"="Other",
		"O-O-O"="Other",
		"Ne4"="Other",
		"e5"="Other",
		"Ng6"="Other")
	)
	                              
	#firstMoveBlack = revalue(games[,7], c("a3"="Other", "a4"="Other", "b3"="Other", "b4"="Other", "c3"="Other", "d3"="Other", "e3"="Other", "f4"="Other", "g3"="Other", "g4"="Other", "h3"="Other", "h4"="Other", "Nc3"="Other", "Nh3"="Other"))
	checkMate = games[,8]
	capturedPiecesRate = as.numeric(games[,9]/moves)
	rookMoveRate = as.numeric(games[,10]/moves)
	knightMoveRate = as.numeric(games[,11]/moves)
	pawnMoveRate = as.numeric(games[,12]/moves) #BUG
	queenMoveRate = as.numeric(games[,13]/moves)
	promotedRate = as.numeric(games[,14])
	kingSideCastlingRate = as.numeric(games[,15])
	queenSideCastlingRate = as.numeric(games[,16])
	#rook = games[,9]
	
	df11 <- data.frame(date, firstMoveWhite) 
	#p11 = ggplot(df11, aes(date, fill = firstMoveWhite)) 	+ xlim(1900, 2015)	+ stat_density(aes(y = ..density..), position = "fill", color = "black") + xlab("Date") + ylab("Count") + ggtitle("11. First Move White")
	p11 = ggplot(df11, aes(date, fill = firstMoveWhite)) + geom_bar(position="fill", binwidth=2.5) + xlim(1950, 2015)
	
	df12 <- data.frame(date, firstMoveWhite) 
	p12 = ggplot(df11, aes(date, fill = firstMoveWhite)) 	+ xlim(1950, 2015)	+ stat_density(aes(y = ..density..), position = "fill", color = "black") + xlab("Date") + ylab("Count") + ggtitle("12. First Move White - Zoom")

	df13 <- data.frame(date, checkMate) 
	p13 = ggplot(df13, aes(date, checkMate)) 				+ xlim(1850, 2015)	+ xlab("Date") 	+ ylab("% Games win by check mate") 	+ ggtitle("13. % Games win by check Mate") 	+ stat_smooth(method="glm", family="binomial")
	
	df14 <- data.frame(date, capturedPiecesRate) 
	p14 = ggplot(df14, aes(date, capturedPiecesRate)) 		+ xlim(1850, 2015)	+ xlab("Date") 	+ ylab("Ratio of Captured Pieces") 	+ ggtitle("14. Ratio of Captured Pieces") 	+ stat_smooth(method="glm", family="binomial")
	
	df15 <- data.frame(date, rookMoveRate) 
	p15 = ggplot(df15, aes(date, rookMoveRate)) 		+ xlim(1850, 2015)	+ xlab("Date") 	+ ylab("Rook Move Rate") 	+ ggtitle("15. Rook Move Rate") 	+ stat_smooth(method="glm", family="binomial")
	
	df16 <- data.frame(date, knightMoveRate) 
	p16 = ggplot(df16, aes(date, knightMoveRate)) 		+ xlim(1850, 2015)	+ xlab("Date") 	+ ylab("Knight Move Rate") 	+ ggtitle("16. Knight Move Rate") 	+ stat_smooth(method="glm", family="binomial")
	
	df17 <- data.frame(date, pawnMoveRate) 
	p17 = ggplot(df17, aes(date, pawnMoveRate)) 		+ xlim(1850, 2015)	+ xlab("Date") 	+ ylab("Pawn Move Rate") 	+ ggtitle("17. Pawn Move Rate") 	+ stat_smooth(method="glm", family="binomial")
	
	df18 <- data.frame(date, queenMoveRate) 
	p18 = ggplot(df18, aes(date, queenMoveRate)) 		+ xlim(1850, 2015)	+ xlab("Date") 	+ ylab("Queen Move Rate") 	+ ggtitle("18. Queen Move Rate") 	+ stat_smooth(method="glm", family="binomial")
	
	df19 <- data.frame(date, promotedRate) 
	p19 = ggplot(df19, aes(date, promotedRate)) 		+ xlim(1850, 2015)	+ xlab("Date") 	+ ylab("Promoted Rate") 	+ ggtitle("19. Promoted Rate") 	+ stat_smooth(method="glm", family="binomial")
	
	df20 <- data.frame(date, kingSideCastlingRate) 
	p20 = ggplot(df20, aes(date, kingSideCastlingRate)) 		+ xlim(1850, 2015)	+ xlab("Date") 	+ ylab("King Side Castling Rate") 	+ ggtitle("20. King Side Castling Rate") 	+ stat_smooth(method="glm", family="binomial")
	
	df21 <- data.frame(date, queenSideCastlingRate) 
	p21 = ggplot(df21, aes(date, queenSideCastlingRate)) 		+ xlim(1850, 2015)	+ xlab("Date") 	+ ylab("Queen Side Castling Rate") 	+ ggtitle("21. Queen Side Castling Rate") 	+ stat_smooth(method="glm", family="binomial")
	
}

if(nbClearData > 0 && nbUniqueEloRating > 100) {
	ggsave("resources/tmp/1-EloRating.svg", plot = p1)
	ggsave("resources/tmp/2-DifferenceEloRating.svg", plot = p2)
	ggsave("resources/tmp/3-PlyPerGame.svg", plot = p3)
	ggsave("resources/tmp/4-GameWinByHogherEloRating.svg", plot = p4)
	ggsave("resources/tmp/5-GamesWinByWhitePlayer.svg", plot = p5)
	ggsave("resources/tmp/6-GameResultingWinByColor.svg", plot = p6)
}
if(nbUniqueDate > 10) {
	ggsave("resources/tmp/7-DistributionOfDateMatch.svg", plot = p7)
	ggsave("resources/tmp/8-PlyPerGame.svg", plot = p8)
	ggsave("resources/tmp/9-GamesWinByWhitePlayer.svg", plot = p9)
	ggsave("resources/tmp/10-GamesResultingWinByColor.svg", plot = p10)
	ggsave("resources/tmp/11-FirstMoveWhite.svg", plot = p11)
	ggsave("resources/tmp/11-FirstMoveWhiteZoom.svg", plot = p12)
	ggsave("resources/tmp/13-GameWinByCheckMate.svg", plot = p13)
	ggsave("resources/tmp/14-RatioCapturedPieces.svg", plot = p14)
	ggsave("resources/tmp/15-RookMoveRate.svg", plot = p15)
	ggsave("resources/tmp/16-KnightMoveRate.svg", plot = p16)
	ggsave("resources/tmp/17-PawnMoveRate.svg", plot = p17)
	ggsave("resources/tmp/18-QueenMoveRate.svg", plot = p18)
	ggsave("resources/tmp/19-PromotedRate.svg", plot = p19)
	ggsave("resources/tmp/20-KingSideCastlingRate.svg", plot = p20)
	ggsave("resources/tmp/21-QueenSideCastlingRate.svg", plot = p21)
}


#> df30 = subset(na.omit(data.frame(date, maxElo)), maxElo >2800 & maxElo<2900)
#> p30 = ggplot(df30, aes(date, maxElo))
#> p30 + geom_point(aes(colour = maxElo))


#> library(plotly)
#> py <- plotly()
#> py$ggplotly(save30)

#openings = read.csv("/Users/fesnault/opening.csv", head=TRUE, sep=",")
#opening = openings[,1]
#library(data.table)
#dt = data.table(df)
#df2 = subset(as.data.frame.matrix(dt[, count := .N, by = opening][count >= 50000]), count > 50000)
#pie <- ggplot(df2, aes(x = factor(1), fill = factor(opening))) + geom_bar(width = 1) + coord_polar(theta="y")

