CREATE TABLE IF NOT EXISTS `SQLBans_bans` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` tinyint(4) NOT NULL,
  `info` varchar(16) NOT NULL,
  `reason` tinytext NOT NULL,
  `admin` varchar(16) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `server` varchar(32) NOT NULL DEFAULT 'default',
  `banlength` int(11) NOT NULL DEFAULT '0',
  `isbanned` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `username` (`info`),
  KEY `server` (`server`),
  KEY `type` (`type`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;