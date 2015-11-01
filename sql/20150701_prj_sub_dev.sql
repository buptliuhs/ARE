CREATE DATABASE  IF NOT EXISTS `ade` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `ade`;
-- MySQL dump 10.13  Distrib 5.6.23, for Win64 (x86_64)
--
-- Host: localhost    Database: ade
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
-- Table structure for table `data`
--

DROP TABLE IF EXISTS `data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `data` (
  `project_id` int(11) NOT NULL,
  `subject_id` int(11) NOT NULL,
  `ts` varchar(20) NOT NULL,
  `num` smallint(2) NOT NULL,
  `num_filt` smallint(2) NOT NULL,
  PRIMARY KEY (`project_id`,`subject_id`,`ts`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `data`
--

LOCK TABLES `data` WRITE;
/*!40000 ALTER TABLE `data` DISABLE KEYS */;
/*!40000 ALTER TABLE `data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `device`
--

DROP TABLE IF EXISTS `device`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `device` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `type` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `device`
--

LOCK TABLES `device` WRITE;
/*!40000 ALTER TABLE `device` DISABLE KEYS */;
INSERT INTO `device` VALUES (34,'UoA008',1),(35,'UoA016',1),(36,'UoA091',1),(37,'UoA099',1),(38,'UoA103',1),(39,'UoA107',1),(40,'UoA112',1),(41,'UoA118',1),(42,'UoA136',1),(43,'UoA141',1),(44,'UoA142',1),(45,'UoA145',1),(46,'UoA152',1),(47,'UoA157',1),(48,'UoA158',1),(49,'UoA159',1),(50,'UoA161',1),(51,'UoA162',1),(52,'UoA172',1),(53,'UoA251',1),(54,'UoA255',1),(55,'UoA262',1),(56,'UoA263',1),(57,'UoA267',1);
/*!40000 ALTER TABLE `device` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `project`
--

DROP TABLE IF EXISTS `project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `description` varchar(255) NOT NULL,
  `enabled` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `project`
--

LOCK TABLES `project` WRITE;
/*!40000 ALTER TABLE `project` DISABLE KEYS */;
INSERT INTO `project` VALUES (1,1,'Default Project','System Default Project',1),(10,3,'UoA Study #1','UoA Study #1',1);
/*!40000 ALTER TABLE `project` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subject`
--

DROP TABLE IF EXISTS `subject`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `subject` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `dob` date NOT NULL,
  `height` double NOT NULL,
  `weight` double NOT NULL,
  `gender` tinyint(1) NOT NULL,
  `device_id` int(11) NOT NULL,
  `enabled` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `device_id_UNIQUE` (`device_id`)
) ENGINE=InnoDB AUTO_INCREMENT=142 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subject`
--

LOCK TABLES `subject` WRITE;
/*!40000 ALTER TABLE `subject` DISABLE KEYS */;
INSERT INTO `subject` VALUES (118,10,'UoA008','2015-07-01',100,100,2,34,1),(119,10,'UoA016','2015-07-01',100,100,2,35,1),(120,10,'UoA091','2015-07-01',100,100,2,36,1),(121,10,'UoA099','2015-07-01',100,100,2,37,1),(122,10,'UoA103','2015-07-01',100,100,2,38,1),(123,10,'UoA107','2015-07-01',100,100,2,39,1),(124,10,'UoA112','2015-07-01',100,100,0,40,1),(125,10,'UoA118','2015-07-01',100,100,2,41,1),(126,10,'UoA136','2015-07-01',100,100,0,42,1),(127,10,'UoA141','2015-07-01',100,100,0,43,1),(128,10,'UoA142','2015-07-01',100,100,0,44,1),(129,10,'UoA145','2015-07-01',100,100,0,45,1),(130,10,'UoA152','2015-07-01',100,100,0,46,1),(131,10,'UoA157','2015-07-01',100,100,0,47,1),(132,10,'UoA158','2015-07-01',100,100,0,48,1),(133,10,'UoA159','2015-07-01',100,100,0,49,1),(134,10,'UoA161','2015-07-01',100,100,0,50,1),(135,10,'UoA162','2015-07-01',100,100,0,51,1),(136,10,'UoA172','2015-07-01',100,100,0,52,1),(137,10,'UoA251','2015-07-01',100,100,0,53,1),(138,10,'UoA255','2015-07-01',100,100,0,54,1),(139,10,'UoA262','2015-07-01',100,100,0,55,1),(140,10,'UoA263','2015-07-01',100,100,0,56,1),(141,10,'UoA267','2015-07-01',100,100,0,57,1);
/*!40000 ALTER TABLE `subject` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_device_type`
--

DROP TABLE IF EXISTS `sys_device_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_device_type` (
  `id` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `description` varchar(100) NOT NULL,
  `enabled` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_device_type`
--

LOCK TABLES `sys_device_type` WRITE;
/*!40000 ALTER TABLE `sys_device_type` DISABLE KEYS */;
INSERT INTO `sys_device_type` VALUES (1,'uSense','uSense device',1),(2,'McRoberts','McRoberts device',0);
/*!40000 ALTER TABLE `sys_device_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_role` (
  `id` int(11) NOT NULL,
  `role` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (0,'Administrator'),(1,'Normal User');
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_setting`
--

DROP TABLE IF EXISTS `sys_setting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_setting` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `subject_id` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `description` varchar(100) NOT NULL,
  `value` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_setting`
--

LOCK TABLES `sys_setting` WRITE;
/*!40000 ALTER TABLE `sys_setting` DISABLE KEYS */;
INSERT INTO `sys_setting` VALUES (1,0,'static_dynamic_TH','Energy expenditure threshold (static & active)','5'),(2,0,'f_cut_off_higher','Accelerometer signal cutoff frequency (Hz)','10'),(3,0,'walking_time_TH','Walking time threshold (second)','5'),(4,0,'sit_stand_th_angle','Sit/Stand angle threshold (degree)','15'),(5,0,'sit_lie_th_angle','Sit/Lie angle threshold (degree)','80'),(6,0,'lie_invert_th_angle','Lie/Invert angle threshold (degree)','140'),(7,0,'angular_vel_rotation','Angular velocity used to determine transition btwn Sit & Stand (degree/second)','14');
/*!40000 ALTER TABLE `sys_setting` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL COMMENT 'Email',
  `password` varchar(45) NOT NULL,
  `role` int(11) NOT NULL DEFAULT '1',
  `enabled` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_name_UNIQUE` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'admin','ade123',0,1),(3,'uoa','uoa2015',1,1);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-07-01 10:28:45
