-- phpMyAdmin SQL Dump
-- version 4.7.4
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Feb 22, 2018 at 02:38 PM
-- Server version: 10.1.28-MariaDB
-- PHP Version: 7.1.11

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `redditData`
--

-- --------------------------------------------------------

--
-- Table structure for table `redditComment`
--

CREATE TABLE `redditComment` (
  `commentID` int(11) NOT NULL,
  `redditPost_postID` int(11) NOT NULL,
  `commentData` varchar(2048) DEFAULT NULL,
  `commentSentiment` varchar(15) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `redditPost`
--

CREATE TABLE `redditPost` (
  `postID` int(11) NOT NULL,
  `postTitle` varchar(1024) DEFAULT NULL,
  `postLink` varchar(255) DEFAULT NULL,
  `postSentiment` varchar(15) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `redditComment`
--
ALTER TABLE `redditComment`
  ADD PRIMARY KEY (`commentID`,`redditPost_postID`),
  ADD KEY `fk_redditComment_redditPost_idx` (`redditPost_postID`);

--
-- Indexes for table `redditPost`
--
ALTER TABLE `redditPost`
  ADD PRIMARY KEY (`postID`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `redditComment`
--
ALTER TABLE `redditComment`
  ADD CONSTRAINT `fk_redditComment_redditPost` FOREIGN KEY (`redditPost_postID`) REFERENCES `redditpost` (`postID`) ON DELETE NO ACTION ON UPDATE NO ACTION;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
