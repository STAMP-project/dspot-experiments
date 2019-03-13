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
package org.apache.shardingsphere.api.config.sharding;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class TableRuleConfigurationTest {
    @Test(expected = IllegalArgumentException.class)
    public void assertConstructorWithoutLogicTable() {
        new TableRuleConfiguration("");
    }

    @Test
    public void assertConstructorWithFullArguments() {
        TableRuleConfiguration actual = new TableRuleConfiguration("tbl", "ds_$->{0..15}.tbl_$->{0..15}");
        Assert.assertThat(actual.getLogicTable(), CoreMatchers.is("tbl"));
        Assert.assertThat(actual.getActualDataNodes(), CoreMatchers.is("ds_$->{0..15}.tbl_$->{0..15}"));
    }
}

