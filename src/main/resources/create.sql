CREATE TABLE IF NOT EXISTS `%s` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` tinyint(4) NOT NULL,
  `username` varchar(16) NOT NULL DEFAULT '',
  `ip` varbinary(16) NOT NULL DEFAULT '0',
  `reason` varchar(255) NOT NULL DEFAULT '',
  `admin` varchar(16) NOT NULL DEFAULT 'CONSOLE',
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `server` varchar(32) NOT NULL DEFAULT 'default',
  `banlength` int(11) NOT NULL DEFAULT '0',
  `isbanned` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `info` (`info`),
  KEY `server` (`server`),
  KEY `type` (`type`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `%s` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(16) NOT NULL,
  `ip` varbinary(16) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `server` varchar(32) NOT NULL DEFAULT 'default',
  PRIMARY KEY (`id`),
  KEY `username` (`username`),
  KEY `ip` (`ip`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;