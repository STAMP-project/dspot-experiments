package com.sohu.test.dao;


import ConstUtils.CACHE_REDIS_STANDALONE;
import ConstUtils.CACHE_TYPE_REDIS_CLUSTER;
import com.sohu.cache.dao.InstanceConfigDao;
import com.sohu.cache.entity.InstanceConfig;
import com.sohu.cache.util.ConstUtils;
import com.sohu.test.BaseTest;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.junit.Test;


/**
 * ??????
 *
 * @author leifu
 * @unknown 2016?6?22?
 * @unknown ??5:55:51
 */
public class InstanceConfigDaoTest extends BaseTest {
    @Resource
    private InstanceConfigDao instanceConfigDao;

    @Test
    public void testGetByType() {
        List<InstanceConfig> instanceConfigTemplateList = instanceConfigDao.getByType(CACHE_REDIS_STANDALONE);
        for (InstanceConfig instanceConfigTemplate : instanceConfigTemplateList) {
            logger.info(instanceConfigTemplate.toString());
        }
    }

    @Test
    public void testById() {
        long id = 1;
        InstanceConfig instanceConfig = instanceConfigDao.getById(id);
        logger.info("===========testById start==============");
        logger.info(instanceConfig.toString());
        logger.info("===========testById end==============");
    }

    @Test
    public void testByConfigKeyAndType() {
        String configKey = "port";
        int type = ConstUtils.CACHE_REDIS_STANDALONE;
        InstanceConfig instanceConfig = instanceConfigDao.getByConfigKeyAndType(configKey, type);
        logger.info("===========testById start==============");
        logger.info(instanceConfig.toString());
        logger.info("===========testById end==============");
    }

    @Test
    public void testSaveOrUpdate() {
        InstanceConfig instanceConfig = new InstanceConfig();
        instanceConfig.setConfigKey("hello");
        instanceConfig.setConfigValue("world");
        instanceConfig.setInfo("info");
        instanceConfig.setStatus(1);
        instanceConfig.setType(CACHE_TYPE_REDIS_CLUSTER);
        instanceConfig.setUpdateTime(new Date());
        logger.info("===========testSaveOrUpdate start==============");
        instanceConfigDao.saveOrUpdate(instanceConfig);
        logger.info("===========testSaveOrUpdate end==============");
    }

    @Test
    public void testUpdateStatus() {
        long id = 1;
        int status = 0;
        logger.info("===========testUpdateStatus start==============");
        instanceConfigDao.updateStatus(id, status);
        logger.info("===========testUpdateStatus end==============");
    }
}

