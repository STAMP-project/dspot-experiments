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


import LoggerLevel.DEBUG;
import LoggerLevel.TRACE;
import org.elasticsearch.action.admin.indices.cache.clear.ClearIndicesCacheRequestBuilder;
import org.elasticsearch.common.unit.TimeValue;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.utils.log.LogTester;
import org.sonar.server.es.EsTester;


public class ProxyClearCacheRequestBuilderTest {
    @Rule
    public EsTester es = EsTester.create();

    @Rule
    public LogTester logTester = new LogTester();

    @Test
    public void clear_cache() {
        ClearIndicesCacheRequestBuilder requestBuilder = es.client().prepareClearCache();
        requestBuilder.get();
    }

    @Test
    public void to_string() {
        assertThat(es.client().prepareClearCache().toString()).isEqualTo("ES clear cache request");
        assertThat(es.client().prepareClearCache("rules").toString()).isEqualTo("ES clear cache request on indices 'rules'");
        assertThat(es.client().prepareClearCache().setFields("key").toString()).isEqualTo("ES clear cache request on fields 'key'");
        assertThat(es.client().prepareClearCache().setFieldDataCache(true).toString()).isEqualTo("ES clear cache request with field data cache");
        assertThat(es.client().prepareClearCache().setRequestCache(true).toString()).isEqualTo("ES clear cache request with request cache");
    }

    @Test
    public void trace_logs() {
        logTester.setLevel(TRACE);
        ClearIndicesCacheRequestBuilder requestBuilder = es.client().prepareClearCache();
        requestBuilder.get();
        assertThat(logTester.logs()).hasSize(1);
    }

    @Test
    public void no_trace_logs() {
        logTester.setLevel(DEBUG);
        ClearIndicesCacheRequestBuilder requestBuilder = es.client().prepareClearCache();
        requestBuilder.get();
        assertThat(logTester.logs()).isEmpty();
    }

    @Test
    public void get_with_string_timeout_is_not_yet_implemented() {
        try {
            es.client().prepareClearCache().get("1");
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(IllegalStateException.class).hasMessage("Not yet implemented");
        }
    }

    @Test
    public void get_with_time_value_timeout_is_not_yet_implemented() {
        try {
            es.client().prepareClearCache().get(TimeValue.timeValueMinutes(1));
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(IllegalStateException.class).hasMessage("Not yet implemented");
        }
    }

    @Test
    public void execute_should_throw_an_unsupported_operation_exception() {
        try {
            es.client().prepareClearCache().execute();
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(UnsupportedOperationException.class).hasMessage("execute() should not be called as it's used for asynchronous");
        }
    }
}

