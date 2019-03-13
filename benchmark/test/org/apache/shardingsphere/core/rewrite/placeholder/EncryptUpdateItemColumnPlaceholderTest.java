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
package org.apache.shardingsphere.core.rewrite.placeholder;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class EncryptUpdateItemColumnPlaceholderTest {
    private EncryptUpdateItemColumnPlaceholder encryptUpdateItemColumnPlaceholder;

    @Test
    public void assertToStringWithPlaceholderWithoutAssistedColumn() {
        encryptUpdateItemColumnPlaceholder = new EncryptUpdateItemColumnPlaceholder("table_x", "column_x");
        Assert.assertThat(encryptUpdateItemColumnPlaceholder.toString(), CoreMatchers.is("column_x = ?"));
    }

    @Test
    public void assertToStringWithoutPlaceholderWithoutAssistedColumn() {
        encryptUpdateItemColumnPlaceholder = new EncryptUpdateItemColumnPlaceholder("table_x", "column_x", 1);
        Assert.assertThat(encryptUpdateItemColumnPlaceholder.toString(), CoreMatchers.is("column_x = 1"));
    }

    @Test
    public void assertToStringWithPlaceholderWithAssistedColumn() {
        encryptUpdateItemColumnPlaceholder = new EncryptUpdateItemColumnPlaceholder("table_x", "column_x", "column_assist");
        Assert.assertThat(encryptUpdateItemColumnPlaceholder.toString(), CoreMatchers.is("column_x = ?, column_assist = ?"));
    }

    @Test
    public void assertToStringWithoutPlaceholderWithAssistedColumn() {
        encryptUpdateItemColumnPlaceholder = new EncryptUpdateItemColumnPlaceholder("table_x", "column_x", "a", "column_assist", 1);
        Assert.assertThat(encryptUpdateItemColumnPlaceholder.toString(), CoreMatchers.is("column_x = 'a', column_assist = 1"));
        Assert.assertThat(encryptUpdateItemColumnPlaceholder.getLogicTableName(), CoreMatchers.is("table_x"));
        Assert.assertThat(encryptUpdateItemColumnPlaceholder.getColumnName(), CoreMatchers.is("column_x"));
        Assert.assertThat(encryptUpdateItemColumnPlaceholder.getAssistedColumnName(), CoreMatchers.is("column_assist"));
        Assert.assertThat(encryptUpdateItemColumnPlaceholder.getColumnValue(), CoreMatchers.is(((Comparable) ("a"))));
        Assert.assertThat(encryptUpdateItemColumnPlaceholder.getAssistedColumnValue(), CoreMatchers.is(((Comparable) (1))));
        Assert.assertThat(encryptUpdateItemColumnPlaceholder.getPlaceholderIndex(), CoreMatchers.is((-1)));
    }
}

