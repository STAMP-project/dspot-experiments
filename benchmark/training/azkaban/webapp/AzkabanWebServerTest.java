/**
 * Copyright 2017 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package azkaban.webapp;


import azkaban.ServiceProvider;
import azkaban.ServiceProviderTest;
import azkaban.db.DatabaseOperator;
import azkaban.db.H2FileDataSource;
import azkaban.executor.ActiveExecutingFlowsDao;
import azkaban.executor.AlerterHolder;
import azkaban.executor.ExecutionFlowDao;
import azkaban.executor.ExecutionJobDao;
import azkaban.executor.ExecutionLogsDao;
import azkaban.executor.Executor;
import azkaban.executor.ExecutorDao;
import azkaban.executor.ExecutorEventsDao;
import azkaban.executor.ExecutorLoader;
import azkaban.executor.ExecutorManagerAdapter;
import azkaban.executor.FetchActiveFlowDao;
import azkaban.project.ProjectLoader;
import azkaban.project.ProjectManager;
import azkaban.scheduler.QuartzScheduler;
import azkaban.spi.Storage;
import azkaban.trigger.TriggerLoader;
import azkaban.trigger.TriggerManager;
import azkaban.utils.Emailer;
import azkaban.utils.Props;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Assert;
import org.junit.Test;


public class AzkabanWebServerTest {
    public static final String AZKABAN_DB_SQL_PATH = "azkaban-db/src/main/sql";

    private static final Props props = new Props();

    @Test
    public void testInjection() throws Exception {
        final Injector injector = Guice.createInjector(new azkaban.AzkabanCommonModule(AzkabanWebServerTest.props), new AzkabanWebServerModule(AzkabanWebServerTest.props));
        ServiceProvider.SERVICE_PROVIDER.unsetInjector();
        ServiceProvider.SERVICE_PROVIDER.setInjector(injector);
        final ExecutorLoader executorLoader = injector.getInstance(ExecutorLoader.class);
        Assert.assertNotNull(executorLoader);
        final Executor executor = executorLoader.addExecutor("localhost", 60000);
        executor.setActive(true);
        executorLoader.updateExecutor(executor);
        Assert.assertNotNull(injector.getInstance(ExecutionFlowDao.class));
        Assert.assertNotNull(injector.getInstance(DatabaseOperator.class));
        // Test if triggermanager is singletonly guiced. If not, the below test will fail.
        ServiceProviderTest.assertSingleton(ExecutorLoader.class, injector);
        ServiceProviderTest.assertSingleton(ExecutorManagerAdapter.class, injector);
        ServiceProviderTest.assertSingleton(ProjectLoader.class, injector);
        ServiceProviderTest.assertSingleton(ProjectManager.class, injector);
        ServiceProviderTest.assertSingleton(Storage.class, injector);
        ServiceProviderTest.assertSingleton(TriggerLoader.class, injector);
        ServiceProviderTest.assertSingleton(TriggerManager.class, injector);
        ServiceProviderTest.assertSingleton(AlerterHolder.class, injector);
        ServiceProviderTest.assertSingleton(Emailer.class, injector);
        ServiceProviderTest.assertSingleton(ExecutionFlowDao.class, injector);
        ServiceProviderTest.assertSingleton(ExecutorDao.class, injector);
        ServiceProviderTest.assertSingleton(ExecutionJobDao.class, injector);
        ServiceProviderTest.assertSingleton(ExecutionLogsDao.class, injector);
        ServiceProviderTest.assertSingleton(ExecutorEventsDao.class, injector);
        ServiceProviderTest.assertSingleton(ActiveExecutingFlowsDao.class, injector);
        ServiceProviderTest.assertSingleton(FetchActiveFlowDao.class, injector);
        ServiceProviderTest.assertSingleton(AzkabanWebServer.class, injector);
        ServiceProviderTest.assertSingleton(H2FileDataSource.class, injector);
        ServiceProviderTest.assertSingleton(QuartzScheduler.class, injector);
        ServiceProvider.SERVICE_PROVIDER.unsetInjector();
    }
}

