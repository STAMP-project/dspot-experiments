/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.config;


import ConfigResource.Type;
import ConfigResource.Type.BROKER;
import ConfigResource.Type.TOPIC;
import ConfigResource.Type.UNKNOWN;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class ConfigResourceTest {
    @Test
    public void shouldGetTypeFromId() {
        Assert.assertEquals(TOPIC, Type.forId(((byte) (2))));
        Assert.assertEquals(BROKER, Type.forId(((byte) (4))));
    }

    @Test
    public void shouldReturnUnknownForUnknownCode() {
        Assert.assertEquals(UNKNOWN, Type.forId(((byte) (-1))));
        Assert.assertEquals(UNKNOWN, Type.forId(((byte) (0))));
        Assert.assertEquals(UNKNOWN, Type.forId(((byte) (1))));
    }

    @Test
    public void shouldRoundTripEveryType() {
        Arrays.stream(Type.values()).forEach(( type) -> assertEquals(type.toString(), type, ConfigResource.Type.forId(type.id())));
    }
}

