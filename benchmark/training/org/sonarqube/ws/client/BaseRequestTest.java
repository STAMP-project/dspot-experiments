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
package org.sonarqube.ws.client;


import MediaTypes.JSON;
import MediaTypes.PROTOBUF;
import WsRequest.Method.GET;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static Method.GET;


public class BaseRequestTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private BaseRequestTest.FakeRequest underTest = new BaseRequestTest.FakeRequest("api/foo");

    @Test
    public void test_defaults() {
        assertThat(underTest.getMethod()).isEqualTo(GET);
        assertThat(getParams()).isEmpty();
        assertThat(getMediaType()).isEqualTo(JSON);
        assertThat(getPath()).isEqualTo("api/foo");
    }

    @Test
    public void setMediaType() {
        underTest.setMediaType(PROTOBUF);
        assertThat(getMediaType()).isEqualTo(PROTOBUF);
    }

    @Test
    public void keep_order_of_params() {
        assertThat(getParams()).isEmpty();
        assertThat(getParameters().getKeys()).isEmpty();
        underTest.setParam("keyB", "b");
        assertThat(getParams()).containsExactly(entry("keyB", "b"));
        assertParameters(entry("keyB", "b"));
        assertMultiValueParameters(entry("keyB", Collections.singletonList("b")));
        underTest.setParam("keyA", "a");
        assertThat(getParams()).containsExactly(entry("keyB", "b"), entry("keyA", "a"));
        assertParameters(entry("keyB", "b"), entry("keyA", "a"));
        assertMultiValueParameters(entry("keyB", Collections.singletonList("b")), entry("keyA", Collections.singletonList("a")));
        underTest.setParam("keyC", ImmutableList.of("c1", "c2", "c3"));
        assertParameters(entry("keyB", "b"), entry("keyA", "a"), entry("keyC", "c1"));
        assertMultiValueParameters(entry("keyB", Collections.singletonList("b")), entry("keyA", Collections.singletonList("a")), entry("keyC", ImmutableList.of("c1", "c2", "c3")));
    }

    @Test
    public void skip_null_value_in_multi_param() {
        underTest.setParam("key", Lists.newArrayList("v1", null, "", "v3"));
        assertMultiValueParameters(entry("key", Arrays.asList("v1", "", "v3")));
    }

    @Test
    public void null_param_value() {
        Boolean nullBool = null;
        underTest.setParam("key", nullBool);
        assertThat(getParams()).isEmpty();
    }

    @Test
    public void fail_if_null_param_key() {
        expectedException.expect(IllegalArgumentException.class);
        setParam(null, "val");
    }

    @Test
    public void headers_are_empty_by_default() {
        assertThat(getHeaders().getNames()).isEmpty();
    }

    @Test
    public void set_and_get_headers() {
        setHeader("foo", "fooz");
        setHeader("bar", "barz");
        assertThat(getHeaders().getNames()).containsExactlyInAnyOrder("foo", "bar");
        assertThat(getHeaders().getValue("foo")).hasValue("fooz");
        assertThat(getHeaders().getValue("bar")).hasValue("barz");
        assertThat(getHeaders().getValue("xxx")).isEmpty();
    }

    private static class FakeRequest extends BaseRequest<BaseRequestTest.FakeRequest> {
        FakeRequest(String path) {
            super(path);
        }

        @Override
        public Method getMethod() {
            return GET;
        }
    }
}

