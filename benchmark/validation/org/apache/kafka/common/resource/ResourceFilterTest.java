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
package org.apache.kafka.common.resource;


import org.junit.Assert;
import org.junit.Test;


public class ResourceFilterTest {
    @Test
    public void shouldNotMatchIfDifferentResourceType() {
        Assert.assertFalse(matches(new Resource(ResourceType.GROUP, "Name")));
    }

    @Test
    public void shouldNotMatchIfDifferentName() {
        Assert.assertFalse(matches(new Resource(ResourceType.TOPIC, "Name")));
    }

    @Test
    public void shouldNotMatchIfDifferentNameCase() {
        Assert.assertFalse(matches(new Resource(ResourceType.TOPIC, "Name")));
    }

    @Test
    public void shouldMatchWhereResourceTypeIsAny() {
        Assert.assertTrue(matches(new Resource(ResourceType.TOPIC, "Name")));
    }

    @Test
    public void shouldMatchWhereResourceNameIsAny() {
        Assert.assertTrue(matches(new Resource(ResourceType.TOPIC, "Name")));
    }

    @Test
    public void shouldMatchIfExactMatch() {
        Assert.assertTrue(matches(new Resource(ResourceType.TOPIC, "Name")));
    }

    @Test
    public void shouldMatchWildcardIfExactMatch() {
        Assert.assertTrue(matches(new Resource(ResourceType.TOPIC, "*")));
    }

    @Test
    public void shouldNotMatchWildcardAgainstOtherName() {
        Assert.assertFalse(matches(new Resource(ResourceType.TOPIC, "*")));
    }

    @Test
    public void shouldNotMatchLiteralWildcardTheWayAround() {
        Assert.assertFalse(matches(new Resource(ResourceType.TOPIC, "Name")));
    }
}

