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
package org.apache.shardingsphere.core.constant;


import ShardingPropertiesConstant.EXECUTOR_SIZE;
import ShardingPropertiesConstant.SQL_SHOW;
import java.util.Properties;
import org.apache.shardingsphere.core.constant.properties.ShardingProperties;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class ShardingPropertiesTest {
    private final Properties prop = new Properties();

    private ShardingProperties shardingProperties;

    @Test
    public void assertGetValueForDefaultValue() {
        ShardingProperties shardingProperties = new ShardingProperties(new Properties());
        boolean actualSQLShow = shardingProperties.getValue(SQL_SHOW);
        Assert.assertThat(actualSQLShow, CoreMatchers.is(Boolean.valueOf(SQL_SHOW.getDefaultValue())));
        int executorMaxSize = shardingProperties.getValue(EXECUTOR_SIZE);
        Assert.assertThat(executorMaxSize, CoreMatchers.is(Integer.valueOf(EXECUTOR_SIZE.getDefaultValue())));
    }

    @Test
    public void assertGetValueForBoolean() {
        boolean showSql = shardingProperties.getValue(SQL_SHOW);
        Assert.assertTrue(showSql);
    }

    @Test
    public void assertGetValueForInteger() {
        int actualExecutorMaxSize = shardingProperties.getValue(EXECUTOR_SIZE);
        Assert.assertThat(actualExecutorMaxSize, CoreMatchers.is(10));
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertValidateFailure() {
        Properties prop = new Properties();
        prop.put(SQL_SHOW.getKey(), "error");
        prop.put(EXECUTOR_SIZE.getKey(), "error");
        prop.put("other", "other");
        new ShardingProperties(prop);
    }
}

