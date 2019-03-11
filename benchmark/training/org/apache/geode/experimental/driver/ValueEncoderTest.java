/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.experimental.driver;


import org.apache.geode.test.junit.categories.ClientServerTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ ClientServerTest.class })
public class ValueEncoderTest {
    /**
     * a JSON document
     */
    private static final String jsonDocument = ((((((((("{" + (System.lineSeparator())) + "  \"name\" : \"Charlemagne\",") + (System.lineSeparator())) + "  \"age\" : 1276,") + (System.lineSeparator())) + "  \"nationality\" : \"french\",") + (System.lineSeparator())) + "  \"emailAddress\" : \"none\"") + (System.lineSeparator())) + "}";

    private final ValueEncoder valueEncoder = new ValueEncoder(new NoOpSerializer());

    @Test
    public void encodeAndDecode() throws Exception {
        final Object[] objects = new Object[]{ 37, ((short) (37)), ((byte) (37)), 37L, 37.0, 37.0F, true, "hello, world", null, JSONWrapper.wrapJSON(ValueEncoderTest.jsonDocument) };
        for (Object object : objects) {
            Assert.assertEquals(object, valueEncoder.decodeValue(valueEncoder.encodeValue(object)));
        }
        final byte[] bytes = new byte[]{ ((byte) (222)), ((byte) (173)), ((byte) (190)), ((byte) (239)) };
        Assert.assertArrayEquals(bytes, ((byte[]) (valueEncoder.decodeValue(valueEncoder.encodeValue(bytes)))));
    }
}

