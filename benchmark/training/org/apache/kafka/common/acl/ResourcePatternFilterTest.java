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
package org.apache.kafka.common.acl;


import org.apache.kafka.common.resource.PatternType;
import org.apache.kafka.common.resource.ResourceType;
import org.junit.Assert;
import org.junit.Test;


public class ResourcePatternFilterTest {
    @Test
    public void shouldBeUnknownIfResourceTypeUnknown() {
        Assert.assertTrue(isUnknown());
    }

    @Test
    public void shouldBeUnknownIfPatternTypeUnknown() {
        Assert.assertTrue(isUnknown());
    }

    @Test
    public void shouldNotMatchIfDifferentResourceType() {
        Assert.assertFalse(matches(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.GROUP, "Name", PatternType.LITERAL)));
    }

    @Test
    public void shouldNotMatchIfDifferentName() {
        Assert.assertFalse(matches(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.TOPIC, "Name", PatternType.PREFIXED)));
    }

    @Test
    public void shouldNotMatchIfDifferentNameCase() {
        Assert.assertFalse(matches(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.TOPIC, "Name", PatternType.LITERAL)));
    }

    @Test
    public void shouldNotMatchIfDifferentPatternType() {
        Assert.assertFalse(matches(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.TOPIC, "Name", PatternType.PREFIXED)));
    }

    @Test
    public void shouldMatchWhereResourceTypeIsAny() {
        Assert.assertTrue(matches(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.TOPIC, "Name", PatternType.PREFIXED)));
    }

    @Test
    public void shouldMatchWhereResourceNameIsAny() {
        Assert.assertTrue(matches(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.TOPIC, "Name", PatternType.PREFIXED)));
    }

    @Test
    public void shouldMatchWherePatternTypeIsAny() {
        Assert.assertTrue(matches(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.TOPIC, "Name", PatternType.PREFIXED)));
    }

    @Test
    public void shouldMatchWherePatternTypeIsMatch() {
        Assert.assertTrue(matches(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.TOPIC, "Name", PatternType.PREFIXED)));
    }

    @Test
    public void shouldMatchLiteralIfExactMatch() {
        Assert.assertTrue(matches(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.TOPIC, "Name", PatternType.LITERAL)));
    }

    @Test
    public void shouldMatchLiteralIfNameMatchesAndFilterIsOnPatternTypeAny() {
        Assert.assertTrue(matches(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.TOPIC, "Name", PatternType.LITERAL)));
    }

    @Test
    public void shouldMatchLiteralIfNameMatchesAndFilterIsOnPatternTypeMatch() {
        Assert.assertTrue(matches(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.TOPIC, "Name", PatternType.LITERAL)));
    }

    @Test
    public void shouldNotMatchLiteralIfNamePrefixed() {
        Assert.assertFalse(matches(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.TOPIC, "Name", PatternType.LITERAL)));
    }

    @Test
    public void shouldMatchLiteralWildcardIfExactMatch() {
        Assert.assertTrue(matches(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.TOPIC, "*", PatternType.LITERAL)));
    }

    @Test
    public void shouldNotMatchLiteralWildcardAgainstOtherName() {
        Assert.assertFalse(matches(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.TOPIC, "*", PatternType.LITERAL)));
    }

    @Test
    public void shouldNotMatchLiteralWildcardTheWayAround() {
        Assert.assertFalse(matches(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.TOPIC, "Name", PatternType.LITERAL)));
    }

    @Test
    public void shouldNotMatchLiteralWildcardIfFilterHasPatternTypeOfAny() {
        Assert.assertFalse(matches(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.TOPIC, "*", PatternType.LITERAL)));
    }

    @Test
    public void shouldMatchLiteralWildcardIfFilterHasPatternTypeOfMatch() {
        Assert.assertTrue(matches(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.TOPIC, "*", PatternType.LITERAL)));
    }

    @Test
    public void shouldMatchPrefixedIfExactMatch() {
        Assert.assertTrue(matches(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.TOPIC, "Name", PatternType.PREFIXED)));
    }

    @Test
    public void shouldNotMatchIfBothPrefixedAndFilterIsPrefixOfResource() {
        Assert.assertFalse(matches(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.TOPIC, "Name-something", PatternType.PREFIXED)));
    }

    @Test
    public void shouldNotMatchIfBothPrefixedAndResourceIsPrefixOfFilter() {
        Assert.assertFalse(matches(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.TOPIC, "Name", PatternType.PREFIXED)));
    }

    @Test
    public void shouldNotMatchPrefixedIfNamePrefixedAnyFilterTypeIsAny() {
        Assert.assertFalse(matches(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.TOPIC, "Name", PatternType.PREFIXED)));
    }

    @Test
    public void shouldMatchPrefixedIfNamePrefixedAnyFilterTypeIsMatch() {
        Assert.assertTrue(matches(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.TOPIC, "Name", PatternType.PREFIXED)));
    }
}

