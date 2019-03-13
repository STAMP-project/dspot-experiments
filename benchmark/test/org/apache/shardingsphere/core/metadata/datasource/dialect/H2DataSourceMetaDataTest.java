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


public class H2DataSourceMetaDataTest {
    @Test
    public void assertGetALLProperties() {
        H2DataSourceMetaData actual = new H2DataSourceMetaData("jdbc:h2:mem:ds_0;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;MODE=MySQL");
        Assert.assertThat(actual.getHostName(), CoreMatchers.is("mem"));
        Assert.assertThat(actual.getPort(), CoreMatchers.is((-1)));
        Assert.assertThat(actual.getSchemaName(), CoreMatchers.is("ds_0"));
    }

    @Test
    public void assertGetPropertiesWithMinus() {
        H2DataSourceMetaData actual = new H2DataSourceMetaData("jdbc:h2:~:ds-0;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;MODE=MySQL");
        Assert.assertThat(actual.getHostName(), CoreMatchers.is("~"));
        Assert.assertThat(actual.getPort(), CoreMatchers.is((-1)));
        Assert.assertThat(actual.getSchemaName(), CoreMatchers.is("ds-0"));
    }

    @Test(expected = ShardingException.class)
    public void assertGetALLPropertiesFailure() {
        new H2DataSourceMetaData("jdbc:h2:file:/data/sample");
    }

    @Test
    public void assertIsInSameDatabaseInstance() {
        H2DataSourceMetaData target = new H2DataSourceMetaData("jdbc:h2:~/ds_0;MODE=MYSQL");
        H2DataSourceMetaData actual = new H2DataSourceMetaData("jdbc:h2:mem:ds_0;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;MODE=MySQL");
        Assert.assertThat(actual.isInSameDatabaseInstance(target), CoreMatchers.is(false));
    }
}

