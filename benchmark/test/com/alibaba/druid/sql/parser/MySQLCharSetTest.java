package com.alibaba.druid.sql.parser;


import junit.framework.TestCase;


/**
 * Created by tianzhen.wtz on 2015/10/31.
 * ????
 */
public class MySQLCharSetTest extends TestCase {
    public void testCreateCharset() {
        String targetSql = "CREATE TABLE `test_idb`.`acct_certificate` (\n" + (((((((("    `id` bigint(20) NOT NULL auto_increment COMMENT \'\',\n" + "    `nodeid` varchar(5) CHARSET `gbk` COLLATE `gbk_chinese_ci` NULL COMMENT \'\',\n") + "    `certificatetype` char(1) CHARSET `gbk` COLLATE `gbk_chinese_ci` NULL COMMENT \'\',\n") + "    `certificateno` varchar(32) CHARSET `gbk` COLLATE `gbk_chinese_ci` NULL COMMENT \'\',\n") + "    PRIMARY KEY(`id`),\n") + "    INDEX `id_acct_certificate_nodeid`(`nodeid`),\n") + "    INDEX `id_acct_certificate_certificateno`(`certificateno`)\n") + "\n") + ") engine= InnoDB DEFAULT CHARSET= `gbk` DEFAULT COLLATE `gbk_chinese_ci` comment= '' ;");
        String resultSql = "CREATE TABLE `test_idb`.`acct_certificate` (\n" + ((((((("\t`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT \'\', \n" + "\t`nodeid` varchar(5) CHARSET `gbk`  COLLATE `gbk_chinese_ci` NULL COMMENT \'\', \n") + "\t`certificatetype` char(1) CHARSET `gbk`  COLLATE `gbk_chinese_ci` NULL COMMENT \'\', \n") + "\t`certificateno` varchar(32) CHARSET `gbk`  COLLATE `gbk_chinese_ci` NULL COMMENT \'\', \n") + "\tPRIMARY KEY (`id`), \n") + "\tINDEX `id_acct_certificate_nodeid`(`nodeid`), \n") + "\tINDEX `id_acct_certificate_certificateno`(`certificateno`)\n") + ") ENGINE = InnoDB CHARSET = `gbk` COLLATE = `gbk_chinese_ci` COMMENT = ''");
        equal(targetSql, resultSql);
    }

    public void testAlterCharset() {
        String targetSql = "ALTER TABLE acct_certificate MODIFY COLUMN `nodeid` varchar(5) CHARSET `gbk` COLLATE `gbk_chinese_ci` NULL COMMENT ''";
        String resultSql = "ALTER TABLE acct_certificate\n" + "\tMODIFY COLUMN `nodeid` varchar(5) CHARSET `gbk`  COLLATE `gbk_chinese_ci` NULL COMMENT \'\'";
        equal(targetSql, resultSql);
    }
}

