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


import io.bootique.value.Duration;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;


public class DurationValueYamlDeserializerIT extends DeserializerTestBase {
    @Test
    public void testDeserialization_AsString() throws IOException {
        Duration d = deserialize(Duration.class, "5s");
        Assert.assertEquals(ofSeconds(5), d.getDuration());
    }

    @Test
    public void testDeserialization_AsString_Object() throws IOException {
        DurationValueYamlDeserializerIT.Bean b = deserialize(DurationValueYamlDeserializerIT.Bean.class, "duration: '5 min'");
        Assert.assertEquals(ofMinutes(5), b.duration.getDuration());
    }

    @Test
    public void testDeserialization_AsLongMs() throws IOException {
        Duration d = deserialize(Duration.class, "500");
        Assert.assertEquals(ofMillis(500), d.getDuration());
    }

    @Test
    public void testDeserialization_AsLong_Object() throws IOException {
        DurationValueYamlDeserializerIT.Bean b = deserialize(DurationValueYamlDeserializerIT.Bean.class, "duration: 500");
        Assert.assertEquals(ofMillis(500), b.duration.getDuration());
    }

    static class Bean {
        protected Duration duration;

        public void setDuration(Duration duration) {
            this.duration = duration;
        }
    }
}

