package com.sohu.cache.stats.app;


import ConstUtils.CACHE_TYPE_REDIS_CLUSTER;
import com.sohu.cache.constant.ImportAppResult;
import com.sohu.cache.entity.AppDesc;
import com.sohu.test.BaseTest;
import java.util.Date;
import javax.annotation.Resource;
import org.junit.Test;


/**
 * ??????
 *
 * @author leifu
 * @unknown 2016-4-16
 * @unknown ??5:55:38
 */
public class ImportAppCenterTest extends BaseTest {
    @Resource(name = "importAppCenter")
    private ImportAppCenter importAppCenter;

    @Test
    public void testImport() {
        // ????
        AppDesc appDesc = new AppDesc();
        appDesc.setName("my-old-sentinel");
        appDesc.setIntro("my-old-sentinel desc");
        appDesc.setOfficer("??");
        appDesc.setCreateTime(new Date());
        appDesc.setPassedTime(new Date());
        appDesc.setIsTest(1);
        appDesc.setType(CACHE_TYPE_REDIS_CLUSTER);
        appDesc.setMemAlertValue(80);
        appDesc.setStatus(2);
        appDesc.setUserId(1);
        appDesc.setVerId(1);
        // ????
        String appInstanceInfo = "10.10.53.159:7000:512\n" + ((("10.10.53.159:7001:512\n" + "10.10.53.159:26379:mymaster\n") + "10.10.53.159:26380:mymaster\n") + "10.10.53.159:26381:mymaster");
        boolean result = importAppCenter.importAppAndInstance(appDesc, appInstanceInfo);
        logger.info("result: {}", result);
    }

    /**
     * ?????
     */
    @Test
    public void testCheckAppDuplicateName() {
        // ????
        AppDesc appDesc = new AppDesc();
        appDesc.setName("redis-cluster-test");
        // ????
        String appInstanceInfo = "";
        ImportAppResult importAppResult = importAppCenter.check(appDesc, appInstanceInfo);
        logger.info("importAppResult: {}", importAppResult);
    }

    /**
     * ????????
     */
    @Test
    public void testCheckEmptyAppInstanceInfo1() {
        // ????
        AppDesc appDesc = new AppDesc();
        appDesc.setName("carlosfu1");
        // ????
        String appInstanceInfo = "";
        ImportAppResult importAppResult = importAppCenter.check(appDesc, appInstanceInfo);
        logger.info("importAppResult: {}", importAppResult);
    }

    /**
     * ???????????1
     */
    @Test
    public void testCheckWrongFormatAppInstanceInfo2() {
        // ????
        AppDesc appDesc = new AppDesc();
        appDesc.setName("carlosfu2");
        // ????
        String appInstanceInfo = "\n10.10.53.159:6379:1024";
        ImportAppResult importAppResult = importAppCenter.check(appDesc, appInstanceInfo);
        logger.info("importAppResult: {}", importAppResult);
    }

    /**
     * ???????????2
     */
    @Test
    public void testCheckWrongFormatAppInstanceInfo3() {
        // ????
        AppDesc appDesc = new AppDesc();
        appDesc.setName("carlosfu3");
        // ????
        String appInstanceInfo = "10.10.53.159:6379";
        ImportAppResult importAppResult = importAppCenter.check(appDesc, appInstanceInfo);
        logger.info("importAppResult: {}", importAppResult);
    }

    /**
     * ???????????4
     */
    @Test
    public void testCheckWrongFormatAppInstanceInfo4() {
        // ????
        AppDesc appDesc = new AppDesc();
        appDesc.setName("carlosfu4");
        // ????
        String appInstanceInfo = "10.10.10.10:6379:1024";
        ImportAppResult importAppResult = importAppCenter.check(appDesc, appInstanceInfo);
        logger.info("importAppResult: {}", importAppResult);
    }

    /**
     * ???????????5
     */
    @Test
    public void testCheckWrongFormatAppInstanceInfo5() {
        // ????
        AppDesc appDesc = new AppDesc();
        appDesc.setName("carlosfu5");
        // ????
        String appInstanceInfo = "10.10.53.162:ab:1024";
        ImportAppResult importAppResult = importAppCenter.check(appDesc, appInstanceInfo);
        logger.info("importAppResult: {}", importAppResult);
    }

    /**
     * ????????
     */
    @Test
    public void testCheckExistInstanceInfo() {
        // ????
        AppDesc appDesc = new AppDesc();
        appDesc.setName("carlosfu6");
        // ????
        String appInstanceInfo = "10.10.53.162:6379:1024";
        ImportAppResult importAppResult = importAppCenter.check(appDesc, appInstanceInfo);
        logger.info("importAppResult: {}", importAppResult);
    }

    /**
     * ????????
     */
    @Test
    public void testCheckNotRunInstance() {
        // ????
        AppDesc appDesc = new AppDesc();
        appDesc.setName("carlosfu7");
        // ????
        String appInstanceInfo = "10.10.53.162:6399:1024";
        ImportAppResult importAppResult = importAppCenter.check(appDesc, appInstanceInfo);
        logger.info("importAppResult: {}", importAppResult);
    }

    /**
     * ??????
     */
    @Test
    public void testCheckWrongMaxMemory() {
        // ????
        AppDesc appDesc = new AppDesc();
        appDesc.setName("carlosfu8");
        // ????
        String appInstanceInfo = "10.10.53.159:6379:aa";
        ImportAppResult importAppResult = importAppCenter.check(appDesc, appInstanceInfo);
        logger.info("importAppResult: {}", importAppResult);
    }

    /**
     *
     */
    @Test
    public void testCheckDataNode() {
        // ????
        AppDesc appDesc = new AppDesc();
        appDesc.setName("carlosfu9");
        // ????
        String appInstanceInfo = "10.10.53.159:6379:1024";
        ImportAppResult importAppResult = importAppCenter.check(appDesc, appInstanceInfo);
        logger.info("importAppResult: {}", importAppResult);
    }

    /**
     * ??sentinel?masterName
     */
    @Test
    public void testCheckSentinelNodeMasterName() {
        // ????
        AppDesc appDesc = new AppDesc();
        appDesc.setName("carlosfu10");
        // ????
        String appInstanceInfo = "10.10.53.159:26379:1024";
        ImportAppResult importAppResult = importAppCenter.check(appDesc, appInstanceInfo);
        logger.info("importAppResult: {}", importAppResult);
    }

    /**
     * ??sentinel??
     */
    @Test
    public void testCheckSentinelNode() {
        // ????
        AppDesc appDesc = new AppDesc();
        appDesc.setName("carlosfu11");
        // ????
        String appInstanceInfo = "10.10.53.159:26379:mymaster";
        ImportAppResult importAppResult = importAppCenter.check(appDesc, appInstanceInfo);
        logger.info("importAppResult: {}", importAppResult);
    }

    /**
     * ??sentinel??
     */
    @Test
    public void testCheckSentinelAllNodes() {
        // ????
        AppDesc appDesc = new AppDesc();
        appDesc.setName("carlosfu12");
        // ????
        String appInstanceInfo = "10.10.53.159:7000:512\n" + ((("10.10.53.159:7001:512\n" + "10.10.53.159:26379:mymaster\n") + "10.10.53.159:26380:mymaster\n") + "10.10.53.159:26381:mymaster");
        ImportAppResult importAppResult = importAppCenter.check(appDesc, appInstanceInfo);
        logger.info("importAppResult: {}", importAppResult);
    }

    @Test
    public void testCheckClusterNodes() {
        // ????
        AppDesc appDesc = new AppDesc();
        appDesc.setName("carlosfu13");
        // ????
        String appInstanceInfo = "10.10.53.159:8000:512\n" + (((("10.10.53.159:8001:512\n" + "10.10.53.159:8002:512\n") + "10.10.53.159:8003:512\n") + "10.10.53.159:8004:512\n") + "10.10.53.159:8005:512\n");
        ImportAppResult importAppResult = importAppCenter.check(appDesc, appInstanceInfo);
        logger.info("importAppResult: {}", importAppResult);
    }
}

