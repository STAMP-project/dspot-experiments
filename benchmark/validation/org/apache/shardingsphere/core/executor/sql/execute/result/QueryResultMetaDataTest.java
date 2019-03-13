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
package org.apache.shardingsphere.core.executor.sql.execute.result;


import org.apache.shardingsphere.spi.encrypt.ShardingEncryptor;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class QueryResultMetaDataTest {
    private QueryResultMetaData queryResultMetaData;

    private ShardingEncryptor shardingEncryptor;

    @Test
    public void assertGetColumnCount() {
        Assert.assertThat(queryResultMetaData.getColumnCount(), CoreMatchers.is(1));
    }

    @Test
    public void assertGetColumnLabel() {
        Assert.assertThat(queryResultMetaData.getColumnLabel(1), CoreMatchers.is("label"));
    }

    @Test
    public void assertGetColumnName() {
        Assert.assertThat(queryResultMetaData.getColumnName(1), CoreMatchers.is("column"));
    }

    @Test
    public void assertGetColumnIndex() {
        Assert.assertThat(queryResultMetaData.getColumnIndex("label"), CoreMatchers.is(1));
    }

    @Test
    public void assertGetShardingEncryptor() {
        Assert.assertThat(queryResultMetaData.getShardingEncryptor(1).get(), CoreMatchers.is(shardingEncryptor));
    }
}

