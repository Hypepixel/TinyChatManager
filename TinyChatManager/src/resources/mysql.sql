CREATE TABLE IF NOT EXISTS `%p%users` (
	`uuid`				CHAR(36)		NOT NULL,
	`group`				VARCHAR(64) 	NULL DEFAULT NULL,
	`force_group`		BOOLEAN 		NULL DEFAULT NULL,
	`subgroup`			VARCHAR(64) 	NULL DEFAULT NULL,
	`custom_prefix`		VARCHAR(128) 	NULL DEFAULT NULL,
	`custom_suffix`		VARCHAR(128) 	NULL DEFAULT NULL,
	`gender`			VARCHAR(32) 	NULL DEFAULT NULL,
	`chat_color`		CHAR(2) 		NULL DEFAULT NULL,
	`chat_formatting`	CHAR(2) 		NULL DEFAULT NULL,
	PRIMARY KEY (`uuid`)
) ENGINE = InnoDB CHARSET = utf8 COLLATE utf8_bin;

CREATE TABLE IF NOT EXISTS `%p%groups` (
	`group`				VARCHAR(64) 	NOT NULL,
	`prefix`			VARCHAR(128) 	default NULL null,
	`suffix` 			VARCHAR(128) 	default NULL null,
	`chat_color` 		CHAR(2) 		default NULL null,
	`chat_formatting` 	CHAR(2) 		default NULL null,
	`join_msg` 			VARCHAR(255) 	default NULL null,
	`quit_msg` 			VARCHAR(255) 	default NULL null,
	PRIMARY KEY (`group`)
)ENGINE = InnoDB CHARSET = utf8 COLLATE utf8_bin;

CREATE TABLE IF NOT EXISTS `%p%subgroups` (
	`group`				VARCHAR(64)		NOT NULL,
	`prefix`			VARCHAR(128)	default NULL null,
	`suffix`			VARCHAR(128)	default NULL null,
	PRIMARY KEY (`group`)
) ENGINE = InnoDB CHARSET = utf8 COLLATE utf8_bin;