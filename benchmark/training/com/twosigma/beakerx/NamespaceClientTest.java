/**
 * Copyright 2014 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.twosigma.beakerx;


import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.junit.Test;


public class NamespaceClientTest {
    private NamespaceClient namespaceClient;

    private KernelTest kernel;

    @Test
    public void setData_returnValue() throws Exception {
        // given
        // when
        Object value = namespaceClient.set("x", new Integer(10));
        // then
        assertThat(value).isNotNull();
        assertThat(value).isEqualTo(new Integer(10));
    }

    @Test
    public void setData_setAutotranslationData() throws Exception {
        // given
        // when
        namespaceClient.set("x", new Integer(10));
        // then
        assertThat(kernel.getPublishedMessages()).isNotEmpty();
        Map data = ((Map) (kernel.getPublishedMessages().get(1).getContent().get("data")));
        Map state = ((Map) (data.get("state")));
        assertThat(state.get("name")).isEqualTo("x");
        assertThat(state.get("value")).isEqualTo("10");
        assertThat(state.get("sync")).isEqualTo(Boolean.TRUE);
    }

    @Test
    public void setBigInt() throws Exception {
        // given
        long millis = new Date().getTime();
        long nanos = (millis * 1000) * 1000L;
        List<Map<String, String>> table = Arrays.asList(new HashMap<String, String>() {
            {
                put("time", ((nanos + (7 * 1)) + ""));
                put("next_time", (((nanos + 77) * 1) + ""));
                put("temp", (14.6 + ""));
            }
        }, new HashMap<String, String>() {
            {
                put("time", ((nanos + (7 * 1)) + ""));
                put("next_time", (((nanos + 88) * 2) + ""));
                put("temp", (18.1 + ""));
            }
        }, new HashMap<String, String>() {
            {
                put("time", ((nanos + (7 * 1)) + ""));
                put("next_time", (((nanos + 99) * 3) + ""));
                put("temp", (23.6 + ""));
            }
        });
        // when
        namespaceClient.set("table_with_longs", table);
        // then
        assertThat(kernel.getPublishedMessages()).isNotEmpty();
        Map data = ((Map) (kernel.getPublishedMessages().get(2).getContent().get("data")));
        Map state = ((Map) (data.get("state")));
        assertThat(state.get("name")).isEqualTo("table_with_longs");
        assertThat(NamespaceClientTest.isJSONValid(state.get("value"))).isTrue();
    }

    @Test
    public void setData_sendCommMessage() throws Exception {
        // given
        // when
        namespaceClient.set("x", new Integer(10));
        // then
        assertThat(kernel.getPublishedMessages()).isNotEmpty();
    }

    public static class AutotranslationServiceTestImpl implements AutotranslationService {
        private ConcurrentMap<String, String> beakerx = new ConcurrentHashMap();

        @Override
        public String update(String name, String json) {
            return beakerx.put(name, json);
        }

        @Override
        public String get(String name) {
            return beakerx.get(name);
        }

        @Override
        public String close() {
            return null;
        }

        @Override
        public String getContextAsString() {
            return null;
        }
    }
}

