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
import org.apache.shardingsphere.core.constant.properties.ShardingPropertiesConstant;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ShardingPropertiesConstantTest {
    @Test
    public void assertFindByKey() {
        Assert.assertThat(ShardingPropertiesConstant.findByKey("sql.show"), CoreMatchers.is(SQL_SHOW));
        Assert.assertThat(ShardingPropertiesConstant.findByKey("executor.size"), CoreMatchers.is(EXECUTOR_SIZE));
    }

    @Test
    public void assertFindByKeyWhenNotFound() {
        Assert.assertNull(ShardingPropertiesConstant.findByKey("empty"));
    }
}

