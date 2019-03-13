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


import java.time.Year;
import org.junit.Assert;
import org.junit.Test;


public class YearDeserializerIT extends DeserializerTestBase {
    @Test
    public void testDeserialization_Value() throws Exception {
        Year value = deserialize(Year.class, "1986");
        Assert.assertEquals(Year.of(1986), value);
    }

    @Test
    public void testDeserialization_Object() throws Exception {
        YearDeserializerIT.Bean o = deserialize(YearDeserializerIT.Bean.class, "year: 2017");
        Assert.assertEquals(Year.of(2017), o.year);
    }

    static class Bean {
        protected Year year;

        public void setYear(Year year) {
            this.year = year;
        }
    }
}

