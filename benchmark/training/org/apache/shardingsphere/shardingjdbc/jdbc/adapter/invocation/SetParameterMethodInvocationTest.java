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
package org.apache.shardingsphere.shardingjdbc.jdbc.adapter.invocation;


import java.sql.PreparedStatement;
import lombok.SneakyThrows;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class SetParameterMethodInvocationTest {
    @Test
    @SneakyThrows
    public void assertGetValue() {
        SetParameterMethodInvocation actual = new SetParameterMethodInvocation(PreparedStatement.class.getMethod("setInt", int.class, int.class), new Object[]{ 1, 100 }, 100);
        Assert.assertThat(actual.getValue(), CoreMatchers.is(((Object) (100))));
    }

    @Test
    @SneakyThrows
    public void assertChangeValueArgument() {
        SetParameterMethodInvocation actual = new SetParameterMethodInvocation(PreparedStatement.class.getMethod("setInt", int.class, int.class), new Object[]{ 1, 100 }, 100);
        actual.changeValueArgument(200);
        Assert.assertThat(actual.getArguments()[1], CoreMatchers.is(((Object) (200))));
    }
}

