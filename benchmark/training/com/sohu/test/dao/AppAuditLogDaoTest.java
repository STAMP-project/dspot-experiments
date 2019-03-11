package com.sohu.test.dao;


import AppAuditLogTypeEnum.APP_CHECK;
import com.sohu.cache.dao.AppAuditLogDao;
import com.sohu.cache.entity.AppAuditLog;
import com.sohu.test.BaseTest;
import javax.annotation.Resource;
import org.junit.Test;


/**
 *
 *
 * @author leifu
 * @unknown 2014?12?23?
 * @unknown ??9:41:16
 */
public class AppAuditLogDaoTest extends BaseTest {
    @Resource(name = "appAuditLogDao")
    private AppAuditLogDao appAuditLogDao;

    @Test
    public void getAuditByType() {
        Long appAuditId = 75L;
        AppAuditLog appAuditLog = appAuditLogDao.getAuditByType(appAuditId, APP_CHECK.value());
        logger.info("{}", appAuditLog);
    }
}

