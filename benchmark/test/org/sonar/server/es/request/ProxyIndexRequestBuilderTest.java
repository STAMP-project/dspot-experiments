/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.es.request;


import LoggerLevel.TRACE;
import Result.CREATED;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.utils.log.LogTester;
import org.sonar.server.es.EsTester;
import org.sonar.server.es.FakeIndexDefinition;
import org.sonar.server.es.IndexType;


public class ProxyIndexRequestBuilderTest {
    @Rule
    public EsTester es = EsTester.createCustom(new FakeIndexDefinition());

    @Rule
    public LogTester logTester = new LogTester();

    @Test
    public void index_with_index_type_and_id() {
        IndexResponse response = es.client().prepareIndex(FakeIndexDefinition.INDEX_TYPE_FAKE).setSource(getFields()).get();
        assertThat(response.getResult()).isSameAs(CREATED);
    }

    @Test
    public void trace_logs() {
        logTester.setLevel(TRACE);
        IndexResponse response = es.client().prepareIndex(FakeIndexDefinition.INDEX_TYPE_FAKE).setSource(getFields()).get();
        assertThat(response.getResult()).isSameAs(CREATED);
        assertThat(logTester.logs(TRACE)).hasSize(1);
    }

    @Test
    public void fail_if_bad_query() {
        IndexRequestBuilder requestBuilder = es.client().prepareIndex(new IndexType("unknownIndex", "unknownType"));
        try {
            requestBuilder.get();
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(IllegalStateException.class);
            assertThat(e.getMessage()).contains("Fail to execute ES index request for key 'null' on index 'unknownIndex' on type 'unknownType'");
        }
    }

    @Test
    public void fail_if_bad_query_with_basic_profiling() {
        IndexRequestBuilder requestBuilder = es.client().prepareIndex(new IndexType("unknownIndex", "unknownType"));
        try {
            requestBuilder.get();
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(IllegalStateException.class);
            assertThat(e.getMessage()).contains("Fail to execute ES index request for key 'null' on index 'unknownIndex' on type 'unknownType'");
        }
    }

    @Test
    public void get_with_string_timeout_is_not_yet_implemented() {
        try {
            es.client().prepareIndex(FakeIndexDefinition.INDEX_TYPE_FAKE).get("1");
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(IllegalStateException.class).hasMessage("Not yet implemented");
        }
    }

    @Test
    public void get_with_time_value_timeout_is_not_yet_implemented() {
        try {
            es.client().prepareIndex(FakeIndexDefinition.INDEX_TYPE_FAKE).get(TimeValue.timeValueMinutes(1));
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(IllegalStateException.class).hasMessage("Not yet implemented");
        }
    }

    @Test
    public void do_not_support_execute_method() {
        try {
            es.client().prepareIndex(FakeIndexDefinition.INDEX_TYPE_FAKE).execute();
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(UnsupportedOperationException.class).hasMessage("execute() should not be called as it's used for asynchronous");
        }
    }
}

