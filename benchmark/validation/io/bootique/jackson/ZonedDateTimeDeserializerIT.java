/**
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.bootique.jackson;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;


public class ZonedDateTimeDeserializerIT extends DeserializerTestBase {
    @Test
    public void testDeserialization01() throws Exception {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2017, 9, 2, 10, 15, 30, 0, ZoneOffset.ofHours(1));
        ZonedDateTime value = deserialize(ZonedDateTime.class, (("\"" + (zonedDateTime.toString())) + "\""));
        Assert.assertNotNull("The value should not be null.", value);
        Assert.assertEquals("The value is not correct.", zonedDateTime, value);
    }

    @Test
    public void testDeserialization02() throws Exception {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2017, 9, 2, 10, 15, 30, 0, ZoneOffset.ofHours(1));
        ZonedDateTime value = deserialize(ZonedDateTime.class, "\"2017-09-02T10:15:30+01:00\"");
        Assert.assertNotNull("The value should not be null.", value);
        Assert.assertEquals("The value is not correct.", zonedDateTime, value);
    }

    @Test
    public void testDeserialization03() throws IOException {
        DeserializerTestBase.Bean1 bean1 = deserialize(DeserializerTestBase.Bean1.class, ("a: \"x\"\n" + ("c:\n" + "  zonedDateTime: 2017-09-02T10:15:30+01:00")));
        Assert.assertEquals(ZonedDateTime.of(2017, 9, 2, 10, 15, 30, 0, ZoneOffset.ofHours(1)), bean1.c.zonedDateTime);
    }

    @Test
    public void testDeserializationWithTypeInfo01() throws Exception {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2017, 9, 2, 10, 15, 30, 0, ZoneOffset.ofHours(1));
        ObjectMapper mapper = createMapper();
        mapper.addMixIn(TemporalAccessor.class, MockObjectConfiguration.class);
        TemporalAccessor value = mapper.readValue((("[\"" + (ZonedDateTime.class.getName())) + "\",\"2017-09-02T10:15:30+01:00\"]"), TemporalAccessor.class);
        Assert.assertNotNull("The value should not be null.", value);
        TestCase.assertTrue("The value should be a ZonedDateTime.", (value instanceof ZonedDateTime));
        Assert.assertEquals("The value is not correct.", zonedDateTime, value);
    }
}

