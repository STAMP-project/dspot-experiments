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


import java.time.Duration;
import org.junit.Assert;
import org.junit.Test;


public class DurationDeserializerIT extends DeserializerTestBase {
    @Test
    public void testDeserialization() throws Exception {
        DurationDeserializerIT.Bean o = deserialize(DurationDeserializerIT.Bean.class, "duration: P2DT3H4M");
        Assert.assertEquals(Duration.parse("P2DT3H4M"), o.duration);
    }

    @Test
    public void testDeserializationAsFloat() throws Exception {
        DurationDeserializerIT.Bean o = deserialize(DurationDeserializerIT.Bean.class, "duration: PT3H44M58.000008374S");
        Assert.assertEquals(Duration.ofSeconds(13498L, 8374), o.duration);
    }

    static class Bean {
        protected Duration duration;

        public void setDuration(Duration duration) {
            this.duration = duration;
        }
    }
}

