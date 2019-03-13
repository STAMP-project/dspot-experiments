/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.dataformat.csv;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 * This class tests standard unmarshalling
 */
public class CsvUnmarshalTest extends CamelTestSupport {
    private static final String CSV_SAMPLE = "A,B,C\r1,2,3\rone,two,three";

    @EndpointInject(uri = "mock:output")
    MockEndpoint output;

    @EndpointInject(uri = "mock:line")
    MockEndpoint line;

    @Test
    public void shouldUseDefaultFormat() throws Exception {
        output.expectedMessageCount(1);
        template.sendBody("direct:default", CsvUnmarshalTest.CSV_SAMPLE);
        output.assertIsSatisfied();
        List<?> body = assertIsInstanceOf(List.class, output.getExchanges().get(0).getIn().getBody());
        assertEquals(3, body.size());
        assertEquals(Arrays.asList("A", "B", "C"), body.get(0));
        assertEquals(Arrays.asList("1", "2", "3"), body.get(1));
        assertEquals(Arrays.asList("one", "two", "three"), body.get(2));
    }

    @Test
    public void shouldUseDelimiter() throws Exception {
        output.expectedMessageCount(1);
        template.sendBody("direct:delimiter", CsvUnmarshalTest.CSV_SAMPLE.replace(',', '_'));
        output.assertIsSatisfied();
        List<?> body = assertIsInstanceOf(List.class, output.getExchanges().get(0).getIn().getBody());
        assertEquals(Arrays.asList("A", "B", "C"), body.get(0));
        assertEquals(Arrays.asList("1", "2", "3"), body.get(1));
        assertEquals(Arrays.asList("one", "two", "three"), body.get(2));
    }

    @Test
    public void shouldUseLazyLoading() throws Exception {
        line.expectedMessageCount(3);
        template.sendBody("direct:lazy", CsvUnmarshalTest.CSV_SAMPLE);
        line.assertIsSatisfied();
        List body1 = line.getExchanges().get(0).getIn().getBody(List.class);
        List body2 = line.getExchanges().get(1).getIn().getBody(List.class);
        List body3 = line.getExchanges().get(2).getIn().getBody(List.class);
        assertEquals(Arrays.asList("A", "B", "C"), body1);
        assertEquals(Arrays.asList("1", "2", "3"), body2);
        assertEquals(Arrays.asList("one", "two", "three"), body3);
    }

    @Test
    public void shouldUseMaps() throws Exception {
        output.expectedMessageCount(1);
        template.sendBody("direct:map", CsvUnmarshalTest.CSV_SAMPLE);
        output.assertIsSatisfied();
        List<?> body = assertIsInstanceOf(List.class, output.getExchanges().get(0).getIn().getBody());
        assertEquals(2, body.size());
        assertEquals(TestUtils.asMap("A", "1", "B", "2", "C", "3"), body.get(0));
        assertEquals(TestUtils.asMap("A", "one", "B", "two", "C", "three"), body.get(1));
        // should be unordered map
        Map map = ((Map) (body.get(0)));
        assertIsInstanceOf(HashMap.class, map);
    }

    @Test
    public void shouldUseOrderedMaps() throws Exception {
        output.expectedMessageCount(1);
        template.sendBody("direct:orderedmap", CsvUnmarshalTest.CSV_SAMPLE);
        output.assertIsSatisfied();
        List<?> body = assertIsInstanceOf(List.class, output.getExchanges().get(0).getIn().getBody());
        assertEquals(2, body.size());
        assertEquals(TestUtils.asMap("A", "1", "B", "2", "C", "3"), body.get(0));
        assertEquals(TestUtils.asMap("A", "one", "B", "two", "C", "three"), body.get(1));
        Map map = ((Map) (body.get(0)));
        assertIsInstanceOf(LinkedHashMap.class, map);
        Iterator<Map.Entry> it = map.entrySet().iterator();
        Map.Entry e = it.next();
        assertEquals("A", e.getKey());
        assertEquals("1", e.getValue());
        e = it.next();
        assertEquals("B", e.getKey());
        assertEquals("2", e.getValue());
        e = it.next();
        assertEquals("C", e.getKey());
        assertEquals("3", e.getValue());
    }

    @Test
    public void shouldUseLazyLoadingAndMaps() throws Exception {
        line.expectedMessageCount(2);
        template.sendBody("direct:lazy_map", CsvUnmarshalTest.CSV_SAMPLE);
        line.assertIsSatisfied();
        Map map1 = line.getExchanges().get(0).getIn().getBody(Map.class);
        Map map2 = line.getExchanges().get(1).getIn().getBody(Map.class);
        assertEquals(TestUtils.asMap("A", "1", "B", "2", "C", "3"), map1);
        assertEquals(TestUtils.asMap("A", "one", "B", "two", "C", "three"), map2);
    }

    @Test
    public void shouldUseMapsAndHeaders() throws Exception {
        output.expectedMessageCount(1);
        template.sendBody("direct:map_headers", CsvUnmarshalTest.CSV_SAMPLE);
        output.assertIsSatisfied();
        List<?> body = assertIsInstanceOf(List.class, output.getExchanges().get(0).getIn().getBody());
        assertEquals(2, body.size());
        assertEquals(TestUtils.asMap("AA", "1", "BB", "2", "CC", "3"), body.get(0));
        assertEquals(TestUtils.asMap("AA", "one", "BB", "two", "CC", "three"), body.get(1));
    }
}

