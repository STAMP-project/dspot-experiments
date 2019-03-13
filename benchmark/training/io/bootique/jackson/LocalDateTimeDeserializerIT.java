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


import java.time.LocalDateTime;
import java.time.Month;
import org.junit.Assert;
import org.junit.Test;


public class LocalDateTimeDeserializerIT extends DeserializerTestBase {
    @Test
    public void testDeserialization11() throws Exception {
        LocalDateTimeDeserializerIT.Bean o = deserialize(LocalDateTimeDeserializerIT.Bean.class, "localDateTime: \"1986-01-17T15:43\"");
        Assert.assertEquals(LocalDateTime.of(1986, Month.JANUARY, 17, 15, 43), o.localDateTime);
    }

    @Test
    public void testDeserialization2() throws Exception {
        LocalDateTimeDeserializerIT.Bean o = deserialize(LocalDateTimeDeserializerIT.Bean.class, "localDateTime: \"2013-08-21T09:22:57\"");
        Assert.assertEquals(LocalDateTime.of(2013, Month.AUGUST, 21, 9, 22, 57), o.localDateTime);
    }

    @Test
    public void testDeserialization_Nanoseconds() throws Exception {
        LocalDateTimeDeserializerIT.Bean o = deserialize(LocalDateTimeDeserializerIT.Bean.class, "localDateTime: \"2005-11-05T22:31:05.000829837\"");
        Assert.assertEquals(LocalDateTime.of(2005, Month.NOVEMBER, 5, 22, 31, 5, 829837), o.localDateTime);
    }

    static class Bean {
        protected LocalDateTime localDateTime;

        public void setLocalDateTime(LocalDateTime localDateTime) {
            this.localDateTime = localDateTime;
        }
    }
}

