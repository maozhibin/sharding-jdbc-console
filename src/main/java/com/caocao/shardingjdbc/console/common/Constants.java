package com.caocao.shardingjdbc.console.common;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author liuke1@geely.com
 * @version 1.0
 * @since v1.0 2017/11/16 10:20
 */
public class Constants {
  //数据库类型,1-mysql,2-ms,3-shard'
  public static final String MYSQL = "mysql";
  public static final String MASTER_SLAVE = "master-slave";
  public static final String SHARDING = "sharding";

  public static final Byte MYSQL_INTERGER = 1;
  public static final Byte MASTER_SLAVE_INTERGER = 2;
  public static final Byte SHARDING_INTERGER = 3;

  public static final String ADD_INFO = "add";
  public static final String EDIT_INFO = "edit";

}
