/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hive.config;


import DatabaseType.MYSQL;
import JdbcStorageConfig.DATABASE_TYPE;
import JdbcStorageConfig.JDBC_DRIVER_CLASS;
import JdbcStorageConfig.JDBC_URL;
import JdbcStorageConfig.QUERY;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.hive.storage.jdbc.conf.JdbcStorageConfigManager;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TestJdbcStorageConfigManager {
    @Test
    public void testWithAllRequiredSettingsDefined() throws Exception {
        Properties props = new Properties();
        props.put(DATABASE_TYPE.getPropertyName(), MYSQL.toString());
        props.put(JDBC_URL.getPropertyName(), "jdbc://localhost:3306/hive");
        props.put(QUERY.getPropertyName(), "SELECT col1,col2,col3 FROM sometable");
        props.put(JDBC_DRIVER_CLASS.getPropertyName(), "com.mysql.jdbc.Driver");
        Map<String, String> jobMap = new HashMap<String, String>();
        JdbcStorageConfigManager.copyConfigurationToJob(props, jobMap);
        Assert.assertThat(jobMap, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(jobMap.size(), Matchers.is(Matchers.equalTo(4)));
        Assert.assertThat(jobMap.get(DATABASE_TYPE.getPropertyName()), Matchers.is(Matchers.equalTo("MYSQL")));
        Assert.assertThat(jobMap.get(JDBC_URL.getPropertyName()), Matchers.is(Matchers.equalTo("jdbc://localhost:3306/hive")));
        Assert.assertThat(jobMap.get(QUERY.getPropertyName()), Matchers.is(Matchers.equalTo("SELECT col1,col2,col3 FROM sometable")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithDatabaseTypeMissing() throws Exception {
        Properties props = new Properties();
        props.put(JDBC_URL.getPropertyName(), "jdbc://localhost:3306/hive");
        props.put(QUERY.getPropertyName(), "SELECT col1,col2,col3 FROM sometable");
        Map<String, String> jobMap = new HashMap<String, String>();
        JdbcStorageConfigManager.copyConfigurationToJob(props, jobMap);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithUnknownDatabaseType() throws Exception {
        Properties props = new Properties();
        props.put(DATABASE_TYPE.getPropertyName(), "Postgres");
        props.put(JDBC_URL.getPropertyName(), "jdbc://localhost:3306/hive");
        props.put(QUERY.getPropertyName(), "SELECT col1,col2,col3 FROM sometable");
        Map<String, String> jobMap = new HashMap<String, String>();
        JdbcStorageConfigManager.copyConfigurationToJob(props, jobMap);
    }
}

