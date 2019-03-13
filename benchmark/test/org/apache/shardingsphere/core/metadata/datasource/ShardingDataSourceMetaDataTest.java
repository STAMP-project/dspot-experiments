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
package org.apache.shardingsphere.core.metadata.datasource;


import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;


public class ShardingDataSourceMetaDataTest {
    private ShardingDataSourceMetaData masterSlaveShardingDataSourceMetaData;

    private ShardingDataSourceMetaData shardingDataSourceMetaData;

    @Test
    public void assertGetAllInstanceDataSourceNamesForMasterSlaveShardingRule() {
        Assert.assertEquals(masterSlaveShardingDataSourceMetaData.getAllInstanceDataSourceNames(), Lists.newArrayList("single", "ms_2"));
    }

    @Test
    public void assertGetAllInstanceDataSourceNamesForShardingRule() {
        Assert.assertEquals(shardingDataSourceMetaData.getAllInstanceDataSourceNames(), Lists.newArrayList("ds_0"));
    }

    @Test
    public void assertGetActualSchemaNameForMasterSlaveShardingRule() {
        Assert.assertEquals(masterSlaveShardingDataSourceMetaData.getActualDataSourceMetaData("ms_0").getSchemaName(), "master_0");
    }

    @Test
    public void assertGetActualSchemaNameForShardingRule() {
        Assert.assertEquals(shardingDataSourceMetaData.getActualDataSourceMetaData("ds_0").getSchemaName(), "db_0");
    }
}

