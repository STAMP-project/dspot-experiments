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
import org.elasticsearch.common.unit.TimeValue;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.utils.log.LogTester;
import org.sonar.server.es.EsTester;
import org.sonar.server.es.FakeIndexDefinition;


public class ProxyIndicesExistsRequestBuilderTest {
    @Rule
    public EsTester es = EsTester.createCustom(new FakeIndexDefinition());

    @Rule
    public LogTester logTester = new LogTester();

    @Test
    public void exists() {
        assertThat(es.client().prepareIndicesExist(FakeIndexDefinition.INDEX).get().isExists()).isTrue();
        assertThat(es.client().prepareIndicesExist("unknown").get().isExists()).isFalse();
    }

    @Test
    public void trace_logs() {
        logTester.setLevel(TRACE);
        es.client().prepareIndicesExist(FakeIndexDefinition.INDEX).get();
        assertThat(logTester.logs(TRACE)).hasSize(1);
    }

    @Test
    public void fail_to_exists() {
        try {
            es.client().prepareIndicesExist().get();
            // expected to fail because elasticsearch is not correctly configured, but that does not matter
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(IllegalStateException.class);
            assertThat(e.getMessage()).contains("Fail to execute ES indices exists request");
        }
    }

    @Test
    public void to_string() {
        assertThat(es.client().prepareIndicesExist(FakeIndexDefinition.INDEX).toString()).isEqualTo("ES indices exists request on indices 'fakes'");
    }

    @Test
    public void get_with_string_timeout_is_not_yet_implemented() {
        try {
            es.client().prepareIndicesExist().get("1");
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(IllegalStateException.class).hasMessage("Not yet implemented");
        }
    }

    @Test
    public void get_with_time_value_timeout_is_not_yet_implemented() {
        try {
            es.client().prepareIndicesExist().get(TimeValue.timeValueMinutes(1));
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(IllegalStateException.class).hasMessage("Not yet implemented");
        }
    }

    @Test
    public void execute_should_throw_an_unsupported_operation_exception() {
        try {
            es.client().prepareIndicesExist().execute();
            Assert.fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(UnsupportedOperationException.class).hasMessage("execute() should not be called as it's used for asynchronous");
        }
    }
}

