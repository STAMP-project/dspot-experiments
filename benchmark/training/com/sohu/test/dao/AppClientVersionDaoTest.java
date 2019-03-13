package com.sohu.test.dao;


import com.sohu.cache.dao.AppClientVersionDao;
import com.sohu.cache.entity.AppClientVersion;
import com.sohu.test.BaseTest;
import java.util.List;
import javax.annotation.Resource;
import org.junit.Test;


public class AppClientVersionDaoTest extends BaseTest {
    @Resource
    private AppClientVersionDao appClientVersionDao;

    @Test
    public void testGetByClientIp() {
        List<AppClientVersion> list = appClientVersionDao.getByClientIp("192.168.106.165");
        logger.info("list={}", list);
    }
}

