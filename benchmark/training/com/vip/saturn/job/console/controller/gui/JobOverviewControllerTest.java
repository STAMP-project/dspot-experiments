package com.vip.saturn.job.console.controller.gui;


import com.vip.saturn.job.console.domain.JobConfig;
import com.vip.saturn.job.console.utils.PageableUtil;
import java.util.List;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;


public class JobOverviewControllerTest {
    private JobOverviewControllerTest.TestJobOverviewController controller = new JobOverviewControllerTest.TestJobOverviewController();

    @Test
    public void getJobSubListByPage() {
        List<JobConfig> configs = Lists.newArrayList(buildJobConfig("job1"), buildJobConfig("job2"), buildJobConfig("job3"));
        List<JobConfig> result = controller.getJobSubListByPage(configs, PageableUtil.generatePageble(1, 2));
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("job1", result.get(0).getJobName());
        Assert.assertEquals("job2", result.get(1).getJobName());
        result = controller.getJobSubListByPage(configs, PageableUtil.generatePageble(2, 2));
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("job3", result.get(0).getJobName());
        result = controller.getJobSubListByPage(configs, PageableUtil.generatePageble(3, 2));
        Assert.assertEquals(0, result.size());
        result = controller.getJobSubListByPage(configs, PageableUtil.generatePageble(0, 2));
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("job1", result.get(0).getJobName());
        Assert.assertEquals("job2", result.get(1).getJobName());
        result = controller.getJobSubListByPage(configs, PageableUtil.generatePageble((-1), 2));
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("job1", result.get(0).getJobName());
        Assert.assertEquals("job2", result.get(1).getJobName());
        result = controller.getJobSubListByPage(configs, PageableUtil.generatePageble(1, 5));
        Assert.assertEquals(3, result.size());
        Assert.assertEquals("job1", result.get(0).getJobName());
        Assert.assertEquals("job2", result.get(1).getJobName());
        Assert.assertEquals("job3", result.get(2).getJobName());
        result = controller.getJobSubListByPage(configs, PageableUtil.generatePageble(1, 1));
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("job1", result.get(0).getJobName());
        result = controller.getJobSubListByPage(configs, PageableUtil.generatePageble(2, 1));
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("job2", result.get(0).getJobName());
        result = controller.getJobSubListByPage(configs, PageableUtil.generatePageble(1, (-1)));
        Assert.assertEquals(3, result.size());
    }

    static final class TestJobOverviewController extends JobOverviewController {}
}

