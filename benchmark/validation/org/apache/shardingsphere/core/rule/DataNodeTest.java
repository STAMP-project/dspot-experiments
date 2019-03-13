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
package org.apache.shardingsphere.core.rule;


import org.apache.shardingsphere.core.exception.ShardingConfigurationException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class DataNodeTest {
    @Test
    public void assertNewValidDataNode() {
        DataNode dataNode = new DataNode("ds_0.tbl_0");
        Assert.assertThat(dataNode.getDataSourceName(), CoreMatchers.is("ds_0"));
        Assert.assertThat(dataNode.getTableName(), CoreMatchers.is("tbl_0"));
    }

    @Test(expected = ShardingConfigurationException.class)
    public void assertNewInValidDataNodeWithoutDelimiter() {
        new DataNode("ds_0tbl_0");
    }

    @Test(expected = ShardingConfigurationException.class)
    public void assertNewInValidDataNodeWithTwoDelimiters() {
        new DataNode("ds_0.tbl_0.tbl_1");
    }

    @Test(expected = ShardingConfigurationException.class)
    public void assertNewValidDataNodeWithInvalidDelimiter() {
        new DataNode("ds_0,tbl_0");
    }

    @Test
    public void assertEquals() {
        DataNode dataNode = new DataNode("ds_0.tbl_0");
        Assert.assertTrue(dataNode.equals(new DataNode("ds_0.tbl_0")));
        Assert.assertTrue(dataNode.equals(dataNode));
        Assert.assertFalse(dataNode.equals(new DataNode("ds_0.tbl_1")));
        Assert.assertFalse(dataNode.equals(null));
    }

    @Test
    public void assertHashCode() {
        Assert.assertThat(new DataNode("ds_0.tbl_0").hashCode(), CoreMatchers.is(new DataNode("ds_0.tbl_0").hashCode()));
    }

    @Test
    public void assertToString() {
        Assert.assertThat(new DataNode("ds_0.tbl_0").toString(), CoreMatchers.is("DataNode(dataSourceName=ds_0, tableName=tbl_0)"));
    }
}

