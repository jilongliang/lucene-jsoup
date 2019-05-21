/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50528
Source Host           : localhost:3306
Source Database       : weixin

Target Server Type    : MYSQL
Target Server Version : 50528
File Encoding         : 65001

Date: 2014-11-18 09:20:03
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `form_data`
-- ----------------------------
DROP TABLE IF EXISTS `form_data`;
CREATE TABLE `form_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `form_id` varchar(100) DEFAULT NULL,
  `openid` varchar(100) DEFAULT NULL,
  `user_name` varchar(100) DEFAULT NULL,
  `data_json` text,
  `submit_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of form_data
-- ----------------------------

-- ----------------------------
-- Table structure for `form_info`
-- ----------------------------
DROP TABLE IF EXISTS `form_info`;
CREATE TABLE `form_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(100) DEFAULT NULL,
  `description` varchar(500) DEFAULT NULL,
  `openid` varchar(100) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `model_form` int(11) DEFAULT '0',
  `form_json` text,
  `opend_status` int(11) DEFAULT '0',
  `share_url` varchar(1000) DEFAULT NULL,
  `qr_code` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of form_info
-- ----------------------------
