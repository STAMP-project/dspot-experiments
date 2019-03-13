/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.core.metadata.datasource.dialect;


import org.apache.shardingsphere.core.exception.ShardingException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class SQLServerDataSourceMetaDataTest {
    @Test
    public void assertGetALLProperties() {
        SQLServerDataSourceMetaData actual = new SQLServerDataSourceMetaData("jdbc:microsoft:sqlserver://127.0.0.1:9999;DatabaseName=ds_0");
        Assert.assertThat(actual.getHostName(), CoreMatchers.is("127.0.0.1"));
        Assert.assertThat(actual.getPort(), CoreMatchers.is(9999));
        Assert.assertThat(actual.getSchemaName(), CoreMatchers.is("ds_0"));
    }

    @Test
    public void assertGetALLPropertiesWithDefaultPort() {
        SQLServerDataSourceMetaData actual = new SQLServerDataSourceMetaData("jdbc:microsoft:sqlserver://127.0.0.1;DatabaseName=ds_0");
        Assert.assertThat(actual.getHostName(), CoreMatchers.is("127.0.0.1"));
        Assert.assertThat(actual.getPort(), CoreMatchers.is(1433));
        Assert.assertThat(actual.getSchemaName(), CoreMatchers.is("ds_0"));
    }

    @Test
    public void assertGetPropertiesWithMinus() {
        SQLServerDataSourceMetaData actual = new SQLServerDataSourceMetaData("jdbc:microsoft:sqlserver://host-0;DatabaseName=ds-0");
        Assert.assertThat(actual.getHostName(), CoreMatchers.is("host-0"));
        Assert.assertThat(actual.getPort(), CoreMatchers.is(1433));
        Assert.assertThat(actual.getSchemaName(), CoreMatchers.is("ds-0"));
    }

    @Test(expected = ShardingException.class)
    public void assertGetALLPropertiesFailure() {
        new SQLServerDataSourceMetaData("jdbc:postgresql:xxxxxxxx");
    }

    @Test
    public void assertIsInSameDatabaseInstance() {
        SQLServerDataSourceMetaData target = new SQLServerDataSourceMetaData("jdbc:microsoft:sqlserver://127.0.0.1;DatabaseName=ds_0");
        SQLServerDataSourceMetaData actual = new SQLServerDataSourceMetaData("jdbc:microsoft:sqlserver://127.0.0.1:1433;DatabaseName=ds_0");
        Assert.assertThat(actual.isInSameDatabaseInstance(target), CoreMatchers.is(true));
    }
}

