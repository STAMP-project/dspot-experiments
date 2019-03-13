/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.serialization.json;


import IOUtils.UTF8;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Allard Buijze
 */
public class ByteArrayToJsonNodeConverterTest {
    private ByteArrayToJsonNodeConverter testSubject;

    private ObjectMapper objectMapper;

    @Test
    public void testConvertNodeToBytes() throws Exception {
        final String content = "{\"someKey\":\"someValue\",\"someOther\":true}";
        JsonNode expected = objectMapper.readTree(content);
        Assert.assertEquals(expected, testSubject.convert(content.getBytes(UTF8)));
    }
}

