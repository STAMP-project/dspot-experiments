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
package org.apache.shardingsphere.core.parsing.parser.constant;


import DerivedColumn.AVG_COUNT_ALIAS;
import DerivedColumn.AVG_SUM_ALIAS;
import DerivedColumn.GROUP_BY_ALIAS;
import DerivedColumn.ORDER_BY_ALIAS;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class DerivedColumnTest {
    @Test
    public void assertGetDerivedColumnAlias() {
        Assert.assertThat(AVG_COUNT_ALIAS.getDerivedColumnAlias(0), CoreMatchers.is("AVG_DERIVED_COUNT_0"));
        Assert.assertThat(AVG_SUM_ALIAS.getDerivedColumnAlias(1), CoreMatchers.is("AVG_DERIVED_SUM_1"));
        Assert.assertThat(ORDER_BY_ALIAS.getDerivedColumnAlias(0), CoreMatchers.is("ORDER_BY_DERIVED_0"));
        Assert.assertThat(GROUP_BY_ALIAS.getDerivedColumnAlias(1), CoreMatchers.is("GROUP_BY_DERIVED_1"));
    }

    @Test
    public void assertIsDerivedColumn() {
        Assert.assertTrue(DerivedColumn.isDerivedColumn("AVG_DERIVED_COUNT_0"));
        Assert.assertTrue(DerivedColumn.isDerivedColumn("AVG_DERIVED_SUM_1"));
        Assert.assertTrue(DerivedColumn.isDerivedColumn("ORDER_BY_DERIVED_0"));
        Assert.assertTrue(DerivedColumn.isDerivedColumn("GROUP_BY_DERIVED_1"));
    }

    @Test
    public void assertIsNotDerivedColumn() {
        Assert.assertFalse(DerivedColumn.isDerivedColumn("OTHER_DERIVED_COLUMN_0"));
    }
}

