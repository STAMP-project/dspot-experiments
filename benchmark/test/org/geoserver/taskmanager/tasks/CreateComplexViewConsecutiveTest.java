/**
 * (c) 2017-2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.taskmanager.tasks;


import java.sql.SQLException;
import org.geoserver.taskmanager.AbstractTaskManagerTest;
import org.geoserver.taskmanager.data.Batch;
import org.geoserver.taskmanager.data.Configuration;
import org.geoserver.taskmanager.data.TaskManagerDao;
import org.geoserver.taskmanager.data.TaskManagerFactory;
import org.geoserver.taskmanager.external.DbSource;
import org.geoserver.taskmanager.schedule.BatchJobService;
import org.geoserver.taskmanager.util.LookupService;
import org.geoserver.taskmanager.util.SqlUtil;
import org.geoserver.taskmanager.util.TaskManagerDataUtil;
import org.geoserver.taskmanager.util.TaskManagerTaskUtil;
import org.junit.Assert;
import org.junit.Test;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Test if we temp values are use for consecutive views.
 *
 * @author Timothy De Bock
 */
public class CreateComplexViewConsecutiveTest extends AbstractTaskManagerTest {
    // configure these constants
    private static final String DB_NAME = "testsourcedb";

    private static final String TABLE_NAME = "gw_beleid.grondwaterlichamen_new";

    private static final String VIEW_NAME = "gw_beleid.vw_grondwaterlichamen_test";

    private static final String DEFINITION = " select * from ${table_name} where gwl like 'BL%'";

    private static final String DEFINITION_STEP2 = " select dataengine_id from ${table_name_step2} where gwl like 'BL%'";

    private static final String VIEW_NAME_STEP2 = "gw_beleid.vw_grondwaterlichamen_from_view";

    // attributes
    private static final String ATT_DB_NAME = "db";

    private static final String ATT_TABLE_NAME = "table_name";

    private static final String ATT_VIEW_NAME = "view_name";

    private static final String ATT_DEFINITION = "definition";

    private static final String ATT_DEFINITION_STEP2 = "definition_step2";

    private static final String ATT_TABLE_NAME_STEP2 = "table_name_step2";

    private static final String ATT_VIEW_NAME_STEP2 = "view_name_step2";

    @Autowired
    private TaskManagerDao dao;

    @Autowired
    private TaskManagerFactory fac;

    @Autowired
    private TaskManagerDataUtil dataUtil;

    @Autowired
    private TaskManagerTaskUtil taskUtil;

    @Autowired
    private BatchJobService bjService;

    @Autowired
    private LookupService<DbSource> dbSources;

    @Autowired
    private Scheduler scheduler;

    private Configuration config;

    private Batch batch;

    @Test
    public void testComplexViewFromOtherView() throws SQLException, SchedulerException {
        dataUtil.setConfigurationAttribute(config, CreateComplexViewConsecutiveTest.ATT_DB_NAME, CreateComplexViewConsecutiveTest.DB_NAME);
        dataUtil.setConfigurationAttribute(config, CreateComplexViewConsecutiveTest.ATT_TABLE_NAME, CreateComplexViewConsecutiveTest.TABLE_NAME);
        dataUtil.setConfigurationAttribute(config, CreateComplexViewConsecutiveTest.ATT_VIEW_NAME, CreateComplexViewConsecutiveTest.VIEW_NAME);
        dataUtil.setConfigurationAttribute(config, CreateComplexViewConsecutiveTest.ATT_DEFINITION, CreateComplexViewConsecutiveTest.DEFINITION);
        dataUtil.setConfigurationAttribute(config, CreateComplexViewConsecutiveTest.ATT_DEFINITION_STEP2, CreateComplexViewConsecutiveTest.DEFINITION_STEP2);
        dataUtil.setConfigurationAttribute(config, CreateComplexViewConsecutiveTest.ATT_VIEW_NAME_STEP2, CreateComplexViewConsecutiveTest.VIEW_NAME_STEP2);
        dataUtil.setConfigurationAttribute(config, CreateComplexViewConsecutiveTest.ATT_TABLE_NAME_STEP2, CreateComplexViewConsecutiveTest.VIEW_NAME);
        config = dao.save(config);
        Trigger trigger = TriggerBuilder.newTrigger().forJob(batch.getId().toString()).startNow().build();
        scheduler.scheduleJob(trigger);
        while ((scheduler.getTriggerState(trigger.getKey())) != (TriggerState.NONE)) {
        } 
        Assert.assertTrue(viewExists(SqlUtil.schema(CreateComplexViewConsecutiveTest.VIEW_NAME), SqlUtil.notQualified(CreateComplexViewConsecutiveTest.VIEW_NAME)));
        Assert.assertFalse(viewExists(SqlUtil.schema(CreateComplexViewConsecutiveTest.VIEW_NAME_STEP2), "_temp%"));
        Assert.assertTrue(viewExists(SqlUtil.schema(CreateComplexViewConsecutiveTest.VIEW_NAME_STEP2), SqlUtil.notQualified(CreateComplexViewConsecutiveTest.VIEW_NAME_STEP2)));
        Assert.assertTrue(taskUtil.cleanup(config));
        Assert.assertFalse(viewExists(SqlUtil.schema(CreateComplexViewConsecutiveTest.VIEW_NAME_STEP2), SqlUtil.notQualified(CreateComplexViewConsecutiveTest.VIEW_NAME_STEP2)));
    }
}

