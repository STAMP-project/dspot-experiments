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
package org.apache.shardingsphere.opentracing.hook;


import ShardingTags.COMPONENT_NAME;
import Tags.COMPONENT;
import Tags.DB_STATEMENT;
import Tags.SPAN_KIND;
import Tags.SPAN_KIND_CLIENT;
import io.opentracing.mock.MockSpan;
import java.util.Map;
import org.apache.shardingsphere.core.exception.ShardingException;
import org.apache.shardingsphere.core.metadata.table.ShardingTableMetaData;
import org.apache.shardingsphere.core.parsing.hook.ParsingHook;
import org.apache.shardingsphere.core.parsing.hook.SPIParsingHook;
import org.apache.shardingsphere.core.parsing.parser.sql.SQLStatement;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public final class OpenTracingParsingHookTest extends BaseOpenTracingHookTest {
    private final ParsingHook parsingHook = new SPIParsingHook();

    @Test
    public void assertExecuteSuccess() {
        parsingHook.start("SELECT * FROM XXX;");
        parsingHook.finishSuccess(Mockito.mock(SQLStatement.class), Mockito.mock(ShardingTableMetaData.class));
        MockSpan actual = getActualSpan();
        Assert.assertThat(actual.operationName(), CoreMatchers.is("/Sharding-Sphere/parseSQL/"));
        Map<String, Object> actualTags = actual.tags();
        Assert.assertThat(actualTags.get(COMPONENT.getKey()), CoreMatchers.<Object>is(COMPONENT_NAME));
        Assert.assertThat(actualTags.get(SPAN_KIND.getKey()), CoreMatchers.<Object>is(SPAN_KIND_CLIENT));
        Assert.assertThat(actualTags.get(DB_STATEMENT.getKey()), CoreMatchers.<Object>is("SELECT * FROM XXX;"));
    }

    @Test
    public void assertExecuteFailure() {
        parsingHook.start("SELECT * FROM XXX;");
        parsingHook.finishFailure(new ShardingException("parse SQL error"));
        MockSpan actual = getActualSpan();
        Assert.assertThat(actual.operationName(), CoreMatchers.is("/Sharding-Sphere/parseSQL/"));
        Map<String, Object> actualTags = actual.tags();
        Assert.assertThat(actualTags.get(COMPONENT.getKey()), CoreMatchers.<Object>is(COMPONENT_NAME));
        Assert.assertThat(actualTags.get(SPAN_KIND.getKey()), CoreMatchers.<Object>is(SPAN_KIND_CLIENT));
        Assert.assertThat(actualTags.get(DB_STATEMENT.getKey()), CoreMatchers.<Object>is("SELECT * FROM XXX;"));
        assertSpanError(ShardingException.class, "parse SQL error");
    }
}

