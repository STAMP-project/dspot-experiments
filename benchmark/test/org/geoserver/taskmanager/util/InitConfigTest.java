package org.geoserver.taskmanager.util;


import java.util.List;
import org.geoserver.taskmanager.AbstractTaskManagerTest;
import org.geoserver.taskmanager.data.Attribute;
import org.geoserver.taskmanager.data.Configuration;
import org.geoserver.taskmanager.data.Parameter;
import org.geoserver.taskmanager.data.TaskManagerDao;
import org.geoserver.taskmanager.data.TaskManagerFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class InitConfigTest extends AbstractTaskManagerTest {
    private static final String ATT_DUMMY1 = "dummy1";

    private static final String ATT_DUMMY2 = "dummy2";

    private static final String ATT_DUMMY3 = "dummy3";

    @Autowired
    private TaskManagerDao dao;

    @Autowired
    private TaskManagerFactory fac;

    @Autowired
    private TaskManagerDataUtil util;

    @Autowired
    private TaskManagerTaskUtil taskUtil;

    @Autowired
    private InitConfigUtil initUtil;

    private Configuration config;

    @Test
    public void testInitConfig() {
        Assert.assertEquals(config.getBatches().get("@Initialize"), InitConfigUtil.getInitBatch(config));
        Assert.assertTrue(initUtil.isInitConfig(config));
        Configuration initConfig = initUtil.wrap(config);
        Assert.assertNotEquals(config, initConfig);
        Assert.assertEquals(config, InitConfigUtil.unwrap(initConfig));
        Assert.assertEquals(config.getId(), initConfig.getId());
        Assert.assertEquals(config.getName(), initConfig.getName());
        Assert.assertEquals(2, config.getTasks().size());
        Assert.assertEquals(1, initConfig.getTasks().size());
        Assert.assertEquals(2, config.getBatches().size());
        Assert.assertEquals(1, initConfig.getBatches().size());
        Attribute attDummy2 = config.getAttributes().get(InitConfigTest.ATT_DUMMY2);
        List<Parameter> params = util.getAssociatedParameters(attDummy2, config);
        Assert.assertEquals(2, params.size());
        Assert.assertEquals(config.getTasks().get("task1"), params.get(0).getTask());
        Assert.assertEquals(config.getTasks().get("task2"), params.get(1).getTask());
        params = util.getAssociatedParameters(attDummy2, initConfig);
        Assert.assertEquals(1, params.size());
        Assert.assertEquals(config.getTasks().get("task1"), params.get(0).getTask());
        Assert.assertFalse(taskUtil.validate(config).isEmpty());
        Assert.assertTrue(taskUtil.validate(initConfig).isEmpty());
        Assert.assertEquals(1, taskUtil.getActionsForAttribute(attDummy2, config).size());
        Assert.assertEquals(1, taskUtil.getActionsForAttribute(attDummy2, initConfig).size());
        Assert.assertEquals(2, taskUtil.getTypesForAttribute(attDummy2, config).size());
        Assert.assertEquals(1, taskUtil.getTypesForAttribute(attDummy2, initConfig).size());
    }
}

