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
package org.apache.shardingsphere.orchestration.internal.registry.config.node;


import ShardingConstant.LOGIC_SCHEMA_NAME;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class ConfigurationNodeTest {
    private final ConfigurationNode configurationNode = new ConfigurationNode("test");

    @Test
    public void assertGetSchemaPath() {
        Assert.assertThat(configurationNode.getRulePath(LOGIC_SCHEMA_NAME), CoreMatchers.is("/test/config/schema/logic_db/rule"));
    }

    @Test
    public void assertGetDataSourcePath() {
        Assert.assertThat(configurationNode.getDataSourcePath(LOGIC_SCHEMA_NAME), CoreMatchers.is("/test/config/schema/logic_db/datasource"));
    }

    @Test
    public void assertGetRulePath() {
        Assert.assertThat(configurationNode.getRulePath(LOGIC_SCHEMA_NAME), CoreMatchers.is("/test/config/schema/logic_db/rule"));
    }

    @Test
    public void assertGetAuthenticationPath() {
        Assert.assertThat(configurationNode.getAuthenticationPath(), CoreMatchers.is("/test/config/authentication"));
    }

    @Test
    public void assertGetPropsPath() {
        Assert.assertThat(configurationNode.getPropsPath(), CoreMatchers.is("/test/config/props"));
    }

    @Test
    public void assertGetSchemaName() {
        Assert.assertThat(configurationNode.getSchemaName("/test/config/schema/logic_db/rule"), CoreMatchers.is(LOGIC_SCHEMA_NAME));
    }
}

