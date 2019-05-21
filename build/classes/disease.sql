/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50528
Source Host           : localhost:3306
Source Database       : weixin

Target Server Type    : MYSQL
Target Server Version : 50528
File Encoding         : 65001

Date: 2014-11-18 09:46:12
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `disease`
-- ----------------------------
DROP TABLE IF EXISTS `disease`;
CREATE TABLE `disease` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `disease` varchar(100) DEFAULT NULL,
  `count` int(11) DEFAULT NULL,
  `category` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for `medicine`
-- ----------------------------
DROP TABLE IF EXISTS `medicine`;
CREATE TABLE `medicine` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `url` varchar(1000) DEFAULT NULL,
  `question` varchar(3000) DEFAULT NULL,
  `answer` varchar(3000) DEFAULT NULL,
  `disease` varchar(100) DEFAULT NULL,
  `category` varchar(100) DEFAULT NULL,
  `type` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8;

