-- MySQL dump 10.13  Distrib 5.6.23, for Win64 (x86_64)
--
-- Host: localhost    Database: are
-- ------------------------------------------------------
-- Server version	5.6.24-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `sys_device_type`
--

DROP TABLE IF EXISTS `sys_device_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_device_type` (
  `id` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `file_name_pattern` varchar(255) NOT NULL,
  `file_name_sample` varchar(100) NOT NULL,
  `device_name_index` int(11) NOT NULL,
  `time_index` varchar(45) NOT NULL,
  `converter_class` varchar(255) NOT NULL,
  `description` varchar(100) NOT NULL,
  `enabled` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_device_type`
--

LOCK TABLES `sys_device_type` WRITE;
/*!40000 ALTER TABLE `sys_device_type` DISABLE KEYS */;
INSERT INTO `sys_device_type` VALUES (0,'Unified','(\\d+)_(\\d+)_(\\d+)_(\\d+)_(\\d+)_([a-zA-Z0-9]+)\\.csv','2015_07_29_13_10_D001.csv',6,'1,2,3,4,5','uoa.are.data.UnifiedDataConverter','Unified device',1),(1,'uSense','([a-zA-Z0-9]+)_(\\d+)_(\\d+)_(\\d+)_(\\d+)_(\\d+)_([a-zA-Z0-9]+)\\.txt','ID008_29_7_2015_13_10_UoA998.txt',7,'4,3,2,5,6','uoa.are.data.USenseDataConverter','uSense device',1);
/*!40000 ALTER TABLE `sys_device_type` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-08-14 17:24:25
