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
package org.apache.camel.opentracing.propagation;


import java.util.Iterator;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class CamelMessagingHeadersExtractAdapterTest {
    private Map<String, Object> map;

    @Test
    public void noProperties() {
        CamelMessagingHeadersExtractAdapter adapter = new CamelMessagingHeadersExtractAdapter(map, true);
        Iterator<Map.Entry<String, String>> iterator = adapter.iterator();
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void oneProperty() {
        map.put("key", "value");
        CamelMessagingHeadersExtractAdapter adapter = new CamelMessagingHeadersExtractAdapter(map, true);
        Iterator<Map.Entry<String, String>> iterator = adapter.iterator();
        Map.Entry<String, String> entry = iterator.next();
        Assert.assertEquals("key", entry.getKey());
        Assert.assertEquals("value", entry.getValue());
    }

    @Test
    public void propertyWithDash() {
        map.put((((((CamelMessagingHeadersInjectAdapter.JMS_DASH) + "key") + (CamelMessagingHeadersInjectAdapter.JMS_DASH)) + "1") + (CamelMessagingHeadersInjectAdapter.JMS_DASH)), "value1");
        CamelMessagingHeadersExtractAdapter adapter = new CamelMessagingHeadersExtractAdapter(map, true);
        Iterator<Map.Entry<String, String>> iterator = adapter.iterator();
        Map.Entry<String, String> entry = iterator.next();
        Assert.assertEquals("-key-1-", entry.getKey());
        Assert.assertEquals("value1", entry.getValue());
    }

    @Test
    public void propertyWithoutDashEncoding() {
        map.put((((((CamelMessagingHeadersInjectAdapter.JMS_DASH) + "key") + (CamelMessagingHeadersInjectAdapter.JMS_DASH)) + "1") + (CamelMessagingHeadersInjectAdapter.JMS_DASH)), "value1");
        CamelMessagingHeadersExtractAdapter adapter = new CamelMessagingHeadersExtractAdapter(map, false);
        Iterator<Map.Entry<String, String>> iterator = adapter.iterator();
        Map.Entry<String, String> entry = iterator.next();
        Assert.assertEquals((((((CamelMessagingHeadersInjectAdapter.JMS_DASH) + "key") + (CamelMessagingHeadersInjectAdapter.JMS_DASH)) + "1") + (CamelMessagingHeadersInjectAdapter.JMS_DASH)), entry.getKey());
    }
}

