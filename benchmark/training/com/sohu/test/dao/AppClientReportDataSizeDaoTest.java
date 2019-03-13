package com.sohu.test.dao;


import com.sohu.cache.dao.AppClientReportDataSizeDao;
import com.sohu.cache.entity.AppClientDataSizeStat;
import com.sohu.test.BaseTest;
import java.util.Date;
import javax.annotation.Resource;
import org.junit.Test;


/**
 * ????????map?????--??
 *
 * @author leifu
 * @unknown 2015?7?13?
 * @unknown ??3:43:20
 */
public class AppClientReportDataSizeDaoTest extends BaseTest {
    @Resource
    private AppClientReportDataSizeDao appClientReportDataSizeDao;

    @Test
    public void testSave() {
        AppClientDataSizeStat stat = new AppClientDataSizeStat();
        stat.setClientIp("10.7.40.201");
        stat.setReportTime(new Date());
        stat.setCollectTime(20150120135000L);
        stat.setCreateTime(new Date());
        stat.setCostMapSize(100);
        stat.setExceptionMapSize(5);
        stat.setValueMapSize(86);
        stat.setCollectMapSize(50);
        appClientReportDataSizeDao.save(stat);
    }
}

