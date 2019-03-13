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
package azkaban.execapp;


import Constants.ConfigurationKeys.AZKABAN_STORAGE_LOCAL_BASEDIR;
import azkaban.ServiceProviderTest;
import azkaban.executor.AlerterHolder;
import azkaban.utils.Emailer;
import azkaban.utils.Props;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Assert;
import org.junit.Test;


public class AzkabanExecutorServerTest {
    public static final String AZKABAN_LOCAL_TEST_STORAGE = "AZKABAN_LOCAL_TEST_STORAGE";

    public static final String AZKABAN_DB_SQL_PATH = "azkaban-db/src/main/sql";

    private static final Props props = new Props();

    @Test
    public void testInjection() throws Exception {
        AzkabanExecutorServerTest.props.put(AZKABAN_STORAGE_LOCAL_BASEDIR, AzkabanExecutorServerTest.AZKABAN_LOCAL_TEST_STORAGE);
        final Injector injector = Guice.createInjector(new azkaban.AzkabanCommonModule(AzkabanExecutorServerTest.props), new AzkabanExecServerModule());
        Assert.assertNotNull(injector.getInstance(Emailer.class));
        Assert.assertNotNull(injector.getInstance(AlerterHolder.class));
        ServiceProviderTest.assertSingleton(TriggerManager.class, injector);
        ServiceProviderTest.assertSingleton(FlowRunnerManager.class, injector);
        ServiceProviderTest.assertSingleton(AzkabanExecutorServer.class, injector);
    }
}

