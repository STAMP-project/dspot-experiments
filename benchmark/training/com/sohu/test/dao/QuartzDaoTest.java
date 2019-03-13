package com.sohu.test.dao;


import ConstUtils.REDIS_JOB_GROUP;
import com.sohu.cache.dao.QuartzDao;
import com.sohu.cache.entity.TriggerInfo;
import com.sohu.test.BaseTest;
import java.util.List;
import javax.annotation.Resource;
import org.junit.Test;


/**
 *
 *
 * @unknown lingguo
 * @unknown 2014/10/13 16:02
 */
public class QuartzDaoTest extends BaseTest {
    @Resource
    private QuartzDao quartzDao;

    @Test
    public void testGetTriggersByJobGroup() {
        List<TriggerInfo> triggers = quartzDao.getTriggersByJobGroup(REDIS_JOB_GROUP);
        for (TriggerInfo info : triggers) {
            logger.info("info: {}", info);
        }
    }

    @Test
    public void testGetAllTriggers() {
        List<TriggerInfo> triggers = quartzDao.getAllTriggers();
        for (TriggerInfo info : triggers) {
            logger.info("{}", info);
        }
    }

    @Test
    public void testSearchTriggerByNameOrGroup() {
        String queryString = "10078";
        List<TriggerInfo> triggers = quartzDao.searchTriggerByNameOrGroup(queryString);
        for (TriggerInfo info : triggers) {
            logger.info("info: {}, {}", info, queryString);
        }
    }
}

