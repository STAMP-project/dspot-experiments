package com.xxl.job.admin.dao;


import com.xxl.job.admin.core.model.XxlJobLogGlue;
import java.util.List;
import javax.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationcontext-*.xml")
public class XxlJobLogGlueDaoTest {
    @Resource
    private XxlJobLogGlueDao xxlJobLogGlueDao;

    @Test
    public void test() {
        XxlJobLogGlue logGlue = new XxlJobLogGlue();
        logGlue.setJobId(1);
        logGlue.setGlueType("1");
        logGlue.setGlueSource("1");
        logGlue.setGlueRemark("1");
        int ret = xxlJobLogGlueDao.save(logGlue);
        List<XxlJobLogGlue> list = xxlJobLogGlueDao.findByJobId(1);
        int ret2 = xxlJobLogGlueDao.removeOld(1, 1);
        int ret3 = xxlJobLogGlueDao.deleteByJobId(1);
    }
}

