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
package azkaban;


import Constants.ConfigurationKeys.AZKABAN_STORAGE_HDFS_ROOT_URI;
import Constants.ConfigurationKeys.AZKABAN_STORAGE_LOCAL_BASEDIR;
import Constants.ConfigurationKeys.AZKABAN_STORAGE_TYPE;
import Constants.ConfigurationKeys.HADOOP_CONF_DIR_PATH;
import azkaban.db.DatabaseOperator;
import azkaban.project.JdbcProjectImpl;
import azkaban.spi.Storage;
import azkaban.storage.DatabaseStorage;
import azkaban.storage.HdfsStorage;
import azkaban.storage.LocalStorage;
import azkaban.storage.StorageManager;
import azkaban.utils.Props;
import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Test;


public class ServiceProviderTest {
    private static final String AZKABAN_LOCAL_TEST_STORAGE = "AZKABAN_LOCAL_TEST_STORAGE";

    private static final String AZKABAN_TEST_HDFS_STORAGE_TYPE = "HDFS";

    private static final String AZKABAN_TEST_STORAGE_HDFS_URI = "hdfs://test.com:9000/azkaban/";

    @Test
    public void testInjections() throws Exception {
        final Props props = new Props();
        props.put("database.type", "h2");
        props.put("h2.path", "h2");
        props.put(AZKABAN_STORAGE_LOCAL_BASEDIR, ServiceProviderTest.AZKABAN_LOCAL_TEST_STORAGE);
        final Injector injector = Guice.createInjector(new AzkabanCommonModule(props));
        ServiceProvider.SERVICE_PROVIDER.unsetInjector();
        ServiceProvider.SERVICE_PROVIDER.setInjector(injector);
        assertThat(injector.getInstance(JdbcProjectImpl.class)).isNotNull();
        assertThat(injector.getInstance(StorageManager.class)).isNotNull();
        assertThat(injector.getInstance(DatabaseStorage.class)).isNotNull();
        assertThat(injector.getInstance(LocalStorage.class)).isNotNull();
        assertThatThrownBy(() -> injector.getInstance(.class)).isInstanceOf(ConfigurationException.class).hasMessageContaining("Guice configuration errors");
        assertThat(injector.getInstance(Storage.class)).isNotNull();
        assertThat(injector.getInstance(DatabaseOperator.class)).isNotNull();
    }

    @Test
    public void testHadoopInjection() throws Exception {
        final Props props = new Props();
        props.put("database.type", "h2");
        props.put("h2.path", "h2");
        props.put(AZKABAN_STORAGE_TYPE, ServiceProviderTest.AZKABAN_TEST_HDFS_STORAGE_TYPE);
        props.put(HADOOP_CONF_DIR_PATH, "./");
        props.put(AZKABAN_STORAGE_HDFS_ROOT_URI, ServiceProviderTest.AZKABAN_TEST_STORAGE_HDFS_URI);
        final Injector injector = Guice.createInjector(new AzkabanCommonModule(props));
        ServiceProvider.SERVICE_PROVIDER.unsetInjector();
        ServiceProvider.SERVICE_PROVIDER.setInjector(injector);
        ServiceProviderTest.assertSingleton(HdfsStorage.class, injector);
        assertThat(injector.getInstance(Storage.class)).isNotNull();
    }
}

