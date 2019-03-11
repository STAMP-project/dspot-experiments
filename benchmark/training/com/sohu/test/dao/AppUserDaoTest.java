package com.sohu.test.dao;


import com.sohu.cache.dao.AppUserDao;
import com.sohu.cache.entity.AppUser;
import com.sohu.test.BaseTest;
import javax.annotation.Resource;
import org.junit.Test;


/**
 * User: lingguo
 * Date: 14-6-10
 * Time: ??11:36
 */
public class AppUserDaoTest extends BaseTest {
    @Resource
    private AppUserDao appUserDao;

    @Test
    public void testAppDao() {
        AppUser appUser = AppUser.buildFrom(null, "11", "fff", "leifu@sohu-inc.com", "13820794024", (-1));
        appUserDao.save(appUser);
        logger.info("{}", appUser);
    }
}

