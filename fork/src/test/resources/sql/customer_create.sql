drop table if exists `customer` ;
CREATE TABLE `customer` (  `id` int(11) primary key NOT NULL AUTO_INCREMENT,  `name` varchar(255) not null,  `contact` varchar(255) not null,  `telephone` varchar(255),  `email` varchar(255),  `remark` text) ENGINE=InnoDB DEFAULT CHARSET=utf8;