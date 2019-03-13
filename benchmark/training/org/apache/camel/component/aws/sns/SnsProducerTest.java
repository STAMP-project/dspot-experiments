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
package org.apache.camel.component.aws.sns;


import com.amazonaws.services.sns.model.MessageAttributeValue;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.Exchange;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class SnsProducerTest {
    @Mock
    private Exchange exchange;

    @Mock
    private SnsEndpoint endpoint;

    private SnsProducer producer;

    @Test
    public void translateAttributes() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("key1", null);
        headers.put("key2", "");
        headers.put("key3", "value3");
        Map<String, MessageAttributeValue> translateAttributes = producer.translateAttributes(headers, exchange);
        Assert.assertThat(translateAttributes.size(), CoreMatchers.is(1));
        Assert.assertThat(translateAttributes.get("key3").getDataType(), CoreMatchers.is("String"));
        Assert.assertThat(translateAttributes.get("key3").getStringValue(), CoreMatchers.is("value3"));
    }
}

