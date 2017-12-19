CREATE TABLE `sh_config` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `reg_namespace` varchar(64) NOT NULL COMMENT '注册中心的命名空间',
  `reg_id` varchar(64) NOT NULL COMMENT '注册中心名称',
  `reg_server_list` varchar(64) NOT NULL COMMENT '注册中心地址',
  `data_source_name` varchar(64) NOT NULL COMMENT '数据源名称',
  `status` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '同步状态，0表示未同步，1表示已同步，2表示已修改待同步',
  `create_by` varchar(32) NOT NULL COMMENT '创建人',
  `update_by` varchar(32) NOT NULL COMMENT '修改人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_update_time` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='分片配置表';

CREATE TABLE `sh_metadata` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `type` tinyint(3) unsigned NOT NULL DEFAULT '1' COMMENT '数据库类型,1-mysql,2-ms,3-shard',
  `properties` text NOT NULL COMMENT '数据库属性',
  `data_source_name` varchar(64) NOT NULL COMMENT '数据库名称',
  `create_by` varchar(32) NOT NULL COMMENT '创建人',
  `update_by` varchar(32) NOT NULL COMMENT '修改人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_update_time` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='分片数据源表';

