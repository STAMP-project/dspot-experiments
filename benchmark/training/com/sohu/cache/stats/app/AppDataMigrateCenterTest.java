package com.sohu.cache.stats.app;


import com.sohu.cache.constant.AppDataMigrateEnum;
import com.sohu.cache.constant.AppDataMigrateResult;
import com.sohu.cache.constant.RedisMigrateToolConstant;
import com.sohu.cache.stats.app.impl.AppDataMigrateCenterImpl;
import com.sohu.test.BaseTest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import javax.annotation.Resource;
import org.junit.Test;


/**
 * ??????
 *
 * @author leifu
 * @unknown 2016-6-8
 * @unknown ??8:53:19
 */
public class AppDataMigrateCenterTest extends BaseTest {
    @Resource(name = "appDataMigrateCenter")
    private AppDataMigrateCenterImpl appDataMigrateCenter;

    private static final String REDIS_SOURCE_PASS = "redisSourcePassWord";

    private static final String REDIS_TARGET_PASS = "redisTargetPassWord";

    @Test
    public void testCheckMigrateMachine() {
        // 1.????????(??)
        // 2.??????redis-migrate-tool(??)
        // 3.?????????redis-migrate-tool(??)
        String migrateMachineIp = "127.0.0.1";
        AppDataMigrateEnum sourceRedisMigrateEnum = AppDataMigrateEnum.REDIS_NODE;
        String sourceServers = "127.0.0.1:6379";
        AppDataMigrateEnum targetRedisMigrateEnum = AppDataMigrateEnum.REDIS_CLUSTER_NODE;
        String targetServers = "127.0.0.1:6380";
        AppDataMigrateResult redisMigrateResult = appDataMigrateCenter.check(migrateMachineIp, sourceRedisMigrateEnum, sourceServers, targetRedisMigrateEnum, targetServers, AppDataMigrateCenterTest.REDIS_SOURCE_PASS, AppDataMigrateCenterTest.REDIS_TARGET_PASS);
        logger.info("===============testCheck start=================");
        logger.info(redisMigrateResult.toString());
        logger.info("===============testCheck end=================");
    }

    @Test
    public void testCheckServers() {
        // 1. ????????(??)
        // 2.1 rdb??????(??)
        // 2.2 redis??????
        String migrateMachineIp = "127.0.0.1";
        // RedisMigrateEnum sourceRedisMigrateEnum =
        // RedisMigrateEnum.REDIS_NODE;
        // String sourceServers = "127.0.0.1:6388";
        AppDataMigrateEnum sourceRedisMigrateEnum = AppDataMigrateEnum.RDB_FILE;
        String sourceServers = "/opt/soft/redis/data/dump-6380.rdb";
        AppDataMigrateEnum targetRedisMigrateEnum = AppDataMigrateEnum.REDIS_CLUSTER_NODE;
        String targetServers = "127.0.0.1:6380";
        AppDataMigrateResult redisMigrateResult = appDataMigrateCenter.check(migrateMachineIp, sourceRedisMigrateEnum, sourceServers, targetRedisMigrateEnum, targetServers, AppDataMigrateCenterTest.REDIS_SOURCE_PASS, AppDataMigrateCenterTest.REDIS_TARGET_PASS);
        logger.info("===============testCheck start=================");
        logger.info(redisMigrateResult.toString());
        logger.info("===============testCheck end=================");
    }

    @Test
    public void testConfigFile() {
        String configConent = getConfigContent();
        logger.info("===============testCheck start=================");
        logger.info(configConent);
        logger.info("===============testCheck end=================");
    }

    @Test
    public void testCreateRemoteFile() {
        String fileName = ("rmt-" + (new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()))) + ".conf";
        String configConent = getConfigContent();
        String migrateMachineIp = "127.0.0.1";
        appDataMigrateCenter.createRemoteFile(migrateMachineIp, fileName, configConent);
    }

    @Test
    public void testMigrateNode() {
        String migrateMachineIp = "127.0.0.1";
        AppDataMigrateEnum sourceRedisMigrateEnum = AppDataMigrateEnum.REDIS_NODE;
        String sourceServers = "127.0.0.1:6379";
        AppDataMigrateEnum targetRedisMigrateEnum = AppDataMigrateEnum.REDIS_CLUSTER_NODE;
        String targetServers = "127.0.0.1:6380";
        boolean isMigrate = appDataMigrateCenter.migrate(migrateMachineIp, sourceRedisMigrateEnum, sourceServers, targetRedisMigrateEnum, targetServers, 10000, 20000, AppDataMigrateCenterTest.REDIS_SOURCE_PASS, AppDataMigrateCenterTest.REDIS_TARGET_PASS, 30000);
        logger.warn("============testMigrate start=============");
        logger.warn("isMigrate:{}", isMigrate);
        logger.warn("============testMigrate end=============");
    }

    @Test
    public void testMigrateRDB() {
        String migrateMachineIp = "127.0.0.1";
        AppDataMigrateEnum sourceRedisMigrateEnum = AppDataMigrateEnum.RDB_FILE;
        String sourceServers = "/opt/soft/redis/data/dump-6379.rdb.back";
        AppDataMigrateEnum targetRedisMigrateEnum = AppDataMigrateEnum.REDIS_NODE;
        String targetServers = "127.0.0.1:6380";
        boolean isMigrate = appDataMigrateCenter.migrate(migrateMachineIp, sourceRedisMigrateEnum, sourceServers, targetRedisMigrateEnum, targetServers, 10000, 20000, AppDataMigrateCenterTest.REDIS_SOURCE_PASS, AppDataMigrateCenterTest.REDIS_TARGET_PASS, 30000);
        logger.warn("============testMigrate start=============");
        logger.warn("isMigrate:{}", isMigrate);
        logger.warn("============testMigrate end=============");
    }

    @Test
    public void testShowMiragteToolProcess() {
        long id = 1;
        Map<RedisMigrateToolConstant, Map<String, Object>> map = appDataMigrateCenter.showMiragteToolProcess(id);
        logger.warn("============testShowMiragteToolProcess start=============");
        for (Map.Entry<RedisMigrateToolConstant, Map<String, Object>> entry : map.entrySet()) {
            logger.info(entry.getKey().getValue());
            for (Map.Entry<String, Object> entry2 : entry.getValue().entrySet()) {
                logger.info(((("\t" + (entry2.getKey())) + "->") + (entry2.getValue())));
            }
        }
        logger.warn("============testShowMiragteToolProcess end=============");
    }
}

