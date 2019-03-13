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
package org.apache.shardingsphere.core.routing;


import com.google.common.base.Optional;
import java.util.Collections;
import org.apache.shardingsphere.core.parsing.parser.sql.dml.insert.InsertStatement;
import org.apache.shardingsphere.core.rule.ShardingRule;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class GeneratedKeyTest {
    @Mock
    private ShardingRule shardingRule;

    @Mock
    private InsertStatement insertStatement;

    @Test
    public void assertGetGenerateKeyWhenCreateWithoutGenerateKeyColumnConfiguration() {
        mockGetGenerateKeyWhenCreate();
        Mockito.when(shardingRule.findGenerateKeyColumnName("tbl")).thenReturn(Optional.<String>absent());
        Assert.assertFalse(GeneratedKey.getGenerateKey(shardingRule, Collections.<Object>singletonList(1), insertStatement).isPresent());
    }

    @Test
    public void assertGetGenerateKeyWhenCreateWithGenerateKeyColumnConfiguration() {
        mockGetGenerateKeyWhenCreate();
        Mockito.when(shardingRule.findGenerateKeyColumnName("tbl")).thenReturn(Optional.of("id"));
        Optional<GeneratedKey> actual = GeneratedKey.getGenerateKey(shardingRule, Collections.<Object>singletonList(1), insertStatement);
        Assert.assertTrue(actual.isPresent());
        Assert.assertThat(actual.get().getGeneratedKeys().size(), CoreMatchers.is(1));
    }

    @Test
    public void assertGetGenerateKeyWhenFind() {
        mockGetGenerateKeyWhenFind();
        Assert.assertTrue(GeneratedKey.getGenerateKey(shardingRule, Collections.<Object>singletonList(1), insertStatement).isPresent());
    }
}

