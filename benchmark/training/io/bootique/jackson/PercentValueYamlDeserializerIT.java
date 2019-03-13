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


import io.bootique.value.Percent;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class PercentValueYamlDeserializerIT extends DeserializerTestBase {
    @Test
    public void testDeserialization_AsPercent() throws Exception {
        Percent p = deserialize(Percent.class, "5%");
        Assert.assertEquals(5, p.getPercent(), 1.0E-5);
    }

    @Test
    public void testDeserialization_AsDouble() throws Exception {
        Percent p = deserialize(Percent.class, "5.0");
        Assert.assertEquals(500.0, p.getPercent(), 1.0E-5);
    }

    @Test
    public void testDeserialization_AsInt() throws Exception {
        Percent p = deserialize(Percent.class, "5");
        Assert.assertEquals(500.0, p.getPercent(), 1.0E-5);
    }

    @Test
    public void testDeserialization_Object() throws IOException {
        PercentValueYamlDeserializerIT.Bean p = deserialize(PercentValueYamlDeserializerIT.Bean.class, "percent: -5.%");
        Assert.assertEquals((-5.0), p.percent.getPercent(), 1.0E-5);
    }

    static class Bean {
        protected Percent percent;

        public void setPercent(Percent percent) {
            this.percent = percent;
        }
    }
}

