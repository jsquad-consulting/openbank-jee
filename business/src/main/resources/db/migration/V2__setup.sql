-- MySQL dump 10.13  Distrib 8.0.17, for Linux (x86_64)
--
-- Host: localhost    Database: mysql
-- ------------------------------------------------------
-- Server version	8.0.17

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `ACCOUNT`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ACCOUNT` (
  `ID` bigint(20) NOT NULL,
  `ACCOUNT_NUMBER` varchar(255) DEFAULT NULL,
  `BALANCE` bigint(20) DEFAULT NULL,
  `CLIENT_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_ACCOUNT_CLIENT_ID` (`CLIENT_ID`),
  CONSTRAINT `FK_ACCOUNT_CLIENT_ID` FOREIGN KEY (`CLIENT_ID`) REFERENCES `CLIENT` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ACCOUNT`
--

LOCK TABLES `ACCOUNT` WRITE;
/*!40000 ALTER TABLE `ACCOUNT` DISABLE KEYS */;
/*!40000 ALTER TABLE `ACCOUNT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ACCOUNTTRANSACTION`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ACCOUNTTRANSACTION` (
  `ID` bigint(20) NOT NULL,
  `MESSAGE` varchar(255) DEFAULT NULL,
  `TRANSACTIONTYPE` varchar(255) DEFAULT NULL,
  `ACCOUNT_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_ACCOUNTTRANSACTION_ACCOUNT_ID` (`ACCOUNT_ID`),
  CONSTRAINT `FK_ACCOUNTTRANSACTION_ACCOUNT_ID` FOREIGN KEY (`ACCOUNT_ID`) REFERENCES `ACCOUNT` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ACCOUNTTRANSACTION`
--

LOCK TABLES `ACCOUNTTRANSACTION` WRITE;
/*!40000 ALTER TABLE `ACCOUNTTRANSACTION` DISABLE KEYS */;
/*!40000 ALTER TABLE `ACCOUNTTRANSACTION` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ACCOUNT_ACCOUNTTRANSACTION`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ACCOUNT_ACCOUNTTRANSACTION` (
  `Account_ID` bigint(20) NOT NULL,
  `accountTransactionSet_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`Account_ID`,`accountTransactionSet_ID`),
  KEY `ACCOUNT_ACCOUNTTRANSACTIONaccountTransactionSet_ID` (`accountTransactionSet_ID`),
  CONSTRAINT `ACCOUNT_ACCOUNTTRANSACTIONaccountTransactionSet_ID` FOREIGN KEY (`accountTransactionSet_ID`) REFERENCES `ACCOUNTTRANSACTION` (`ID`),
  CONSTRAINT `FK_ACCOUNT_ACCOUNTTRANSACTION_Account_ID` FOREIGN KEY (`Account_ID`) REFERENCES `ACCOUNT` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ACCOUNT_ACCOUNTTRANSACTION`
--

LOCK TABLES `ACCOUNT_ACCOUNTTRANSACTION` WRITE;
/*!40000 ALTER TABLE `ACCOUNT_ACCOUNTTRANSACTION` DISABLE KEYS */;
/*!40000 ALTER TABLE `ACCOUNT_ACCOUNTTRANSACTION` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ADDRESS`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ADDRESS` (
  `ID` bigint(20) NOT NULL,
  `COUNTRY` varchar(255) DEFAULT NULL,
  `MUNICIPALITY` varchar(255) DEFAULT NULL,
  `POSTALCODE` int(11) DEFAULT NULL,
  `STREET` varchar(255) DEFAULT NULL,
  `STREETNUMBER` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ADDRESS`
--

LOCK TABLES `ADDRESS` WRITE;
/*!40000 ALTER TABLE `ADDRESS` DISABLE KEYS */;
/*!40000 ALTER TABLE `ADDRESS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ADDRESS_PERSON`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ADDRESS_PERSON` (
  `Address_ID` bigint(20) NOT NULL,
  `personSet_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`Address_ID`,`personSet_ID`),
  KEY `FK_ADDRESS_PERSON_personSet_ID` (`personSet_ID`),
  CONSTRAINT `FK_ADDRESS_PERSON_Address_ID` FOREIGN KEY (`Address_ID`) REFERENCES `ADDRESS` (`ID`),
  CONSTRAINT `FK_ADDRESS_PERSON_personSet_ID` FOREIGN KEY (`personSet_ID`) REFERENCES `PERSON` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ADDRESS_PERSON`
--

LOCK TABLES `ADDRESS_PERSON` WRITE;
/*!40000 ALTER TABLE `ADDRESS_PERSON` DISABLE KEYS */;
/*!40000 ALTER TABLE `ADDRESS_PERSON` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `CLIENT`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `CLIENT` (
  `ID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CLIENT`
--

LOCK TABLES `CLIENT` WRITE;
/*!40000 ALTER TABLE `CLIENT` DISABLE KEYS */;
/*!40000 ALTER TABLE `CLIENT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `CLIENTTYPE`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `CLIENTTYPE` (
  `ID` bigint(20) NOT NULL,
  `CTYPE` varchar(31) DEFAULT NULL,
  `CLIENT_FK` bigint(20) DEFAULT NULL,
  `RATING` bigint(20) DEFAULT NULL,
  `PREMIUMRATING` bigint(20) DEFAULT NULL,
  `SPECIALOFFERS` varchar(255) DEFAULT NULL,
  `COUNTRY` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_CLIENTTYPE_CLIENT_FK` (`CLIENT_FK`),
  CONSTRAINT `FK_CLIENTTYPE_CLIENT_FK` FOREIGN KEY (`CLIENT_FK`) REFERENCES `CLIENT` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CLIENTTYPE`
--

LOCK TABLES `CLIENTTYPE` WRITE;
/*!40000 ALTER TABLE `CLIENTTYPE` DISABLE KEYS */;
/*!40000 ALTER TABLE `CLIENTTYPE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `CLIENT_ACCOUNT`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `CLIENT_ACCOUNT` (
  `Client_ID` bigint(20) NOT NULL,
  `accountSet_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`Client_ID`,`accountSet_ID`),
  KEY `FK_CLIENT_ACCOUNT_accountSet_ID` (`accountSet_ID`),
  CONSTRAINT `FK_CLIENT_ACCOUNT_Client_ID` FOREIGN KEY (`Client_ID`) REFERENCES `CLIENT` (`ID`),
  CONSTRAINT `FK_CLIENT_ACCOUNT_accountSet_ID` FOREIGN KEY (`accountSet_ID`) REFERENCES `ACCOUNT` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CLIENT_ACCOUNT`
--

LOCK TABLES `CLIENT_ACCOUNT` WRITE;
/*!40000 ALTER TABLE `CLIENT_ACCOUNT` DISABLE KEYS */;
/*!40000 ALTER TABLE `CLIENT_ACCOUNT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PERSON`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `PERSON` (
  `ID` bigint(20) NOT NULL,
  `FIRSTNAME` varchar(255) DEFAULT NULL,
  `LASTNAME` varchar(255) DEFAULT NULL,
  `MAIL` varchar(255) DEFAULT NULL,
  `PERSONIDENTIFICATION` varchar(255) DEFAULT NULL,
  `CLIENT_FK` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_PERSON_CLIENT_FK` (`CLIENT_FK`),
  CONSTRAINT `FK_PERSON_CLIENT_FK` FOREIGN KEY (`CLIENT_FK`) REFERENCES `CLIENT` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PERSON`
--

LOCK TABLES `PERSON` WRITE;
/*!40000 ALTER TABLE `PERSON` DISABLE KEYS */;
/*!40000 ALTER TABLE `PERSON` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PERSON_ADDRESS`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `PERSON_ADDRESS` (
  `Person_ID` bigint(20) NOT NULL,
  `addressSet_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`Person_ID`,`addressSet_ID`),
  KEY `FK_PERSON_ADDRESS_addressSet_ID` (`addressSet_ID`),
  CONSTRAINT `FK_PERSON_ADDRESS_Person_ID` FOREIGN KEY (`Person_ID`) REFERENCES `PERSON` (`ID`),
  CONSTRAINT `FK_PERSON_ADDRESS_addressSet_ID` FOREIGN KEY (`addressSet_ID`) REFERENCES `ADDRESS` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PERSON_ADDRESS`
--

LOCK TABLES `PERSON_ADDRESS` WRITE;
/*!40000 ALTER TABLE `PERSON_ADDRESS` DISABLE KEYS */;
/*!40000 ALTER TABLE `PERSON_ADDRESS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQUENCE`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `SEQUENCE` (
  `SEQ_NAME` varchar(50) NOT NULL,
  `SEQ_COUNT` decimal(38,0) DEFAULT NULL,
  PRIMARY KEY (`SEQ_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQUENCE`
--

LOCK TABLES `SEQUENCE` WRITE;
/*!40000 ALTER TABLE `SEQUENCE` DISABLE KEYS */;
INSERT INTO `SEQUENCE` VALUES ('SEQ_GEN',0);
/*!40000 ALTER TABLE `SEQUENCE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SYSTEMPROPERTY`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `SYSTEMPROPERTY` (
  `ID` bigint(20) NOT NULL,
  `NAME` varchar(255) DEFAULT NULL,
  `VALUE` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SYSTEMPROPERTY`
--

LOCK TABLES `SYSTEMPROPERTY` WRITE;
/*!40000 ALTER TABLE `SYSTEMPROPERTY` DISABLE KEYS */;
/*!40000 ALTER TABLE `SYSTEMPROPERTY` ENABLE KEYS */;
UNLOCK TABLES;
