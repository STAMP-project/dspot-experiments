package com.sohu.cache.dao;


import com.sohu.cache.entity.InstanceFault;
import com.sohu.test.BaseTest;
import java.util.List;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;


/**
 * Created by yijunzhang on 14-12-29.
 */
public class InstanceFaultDaoTest extends BaseTest {
    @Resource
    private InstanceFaultDao instanceFaultDao;

    @Resource
    private JdbcTemplate jdbcTemplate;

    private int appId = 10170;

    private int instId = 702;

    @Test
    public void testGetListByInstId() throws Exception {
        List<InstanceFault> list = instanceFaultDao.getListByInstId(instId);
        Assert.assertTrue(((list.size()) > 0));
    }

    @Test
    public void testGetListByAppId() throws Exception {
        List<InstanceFault> list = instanceFaultDao.getListByAppId(appId);
        Assert.assertTrue(((list.size()) > 0));
    }

    @Test
    public void cleanUp() {
        jdbcTemplate.update(("delete from instance_fault where app_id=" + (appId)));
    }
}

