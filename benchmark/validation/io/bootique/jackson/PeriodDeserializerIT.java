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


import java.io.IOException;
import java.time.Period;
import org.junit.Assert;
import org.junit.Test;


public class PeriodDeserializerIT extends DeserializerTestBase {
    @Test
    public void testDeserialization_Value1() throws Exception {
        Period p = deserialize(Period.class, "\"P5Y2M3D\"");
        Assert.assertEquals(Period.of(5, 2, 3), p);
    }

    @Test
    public void testDeserialization_Value2() throws Exception {
        Period p = deserialize(Period.class, "\"P5Y8M3D\"");
        Assert.assertEquals(Period.of(5, 8, 3), p);
    }

    @Test
    public void testDeserialization_Object() throws IOException {
        PeriodDeserializerIT.Bean p = deserialize(PeriodDeserializerIT.Bean.class, "period: P5Y8M3D");
        Assert.assertEquals(Period.of(5, 8, 3), p.period);
    }

    static class Bean {
        protected Period period;

        public void setPeriod(Period period) {
            this.period = period;
        }
    }
}

