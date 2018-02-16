CREATE TABLE `scrappedData` (
 `dataID` int(11) NOT NULL AUTO_INCREMENT,
 `articleTitle` varchar(1024) DEFAULT NULL,
 `articleDate` timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
 `articleSource` varchar(50) DEFAULT NULL,
 `scrappedAt` timestamp(6) NOT NULL DEFAULT '0000-00-00 00:00:00.000000',
 PRIMARY KEY (`dataID`)
) ENGINE=InnoDB AUTO_INCREMENT=146 DEFAULT CHARSET=latin1