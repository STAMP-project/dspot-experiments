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


import AclBindingFilter.ANY;
import org.apache.kafka.common.resource.PatternType;
import org.apache.kafka.common.resource.ResourcePatternFilter;
import org.apache.kafka.common.resource.ResourceType;
import org.junit.Assert;
import org.junit.Test;

import static AclOperation.ALL;
import static AclOperation.ANY;
import static AclOperation.READ;
import static AclOperation.UNKNOWN;
import static AclPermissionType.ALLOW;
import static AclPermissionType.DENY;


public class AclBindingTest {
    private static final AclBinding ACL1 = new AclBinding(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.TOPIC, "mytopic", PatternType.LITERAL), new AccessControlEntry("User:ANONYMOUS", "", ALL, ALLOW));

    private static final AclBinding ACL2 = new AclBinding(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.TOPIC, "mytopic", PatternType.LITERAL), new AccessControlEntry("User:*", "", READ, ALLOW));

    private static final AclBinding ACL3 = new AclBinding(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.TOPIC, "mytopic2", PatternType.LITERAL), new AccessControlEntry("User:ANONYMOUS", "127.0.0.1", READ, DENY));

    private static final AclBinding UNKNOWN_ACL = new AclBinding(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.TOPIC, "mytopic2", PatternType.LITERAL), new AccessControlEntry("User:ANONYMOUS", "127.0.0.1", UNKNOWN, DENY));

    private static final AclBindingFilter ANY_ANONYMOUS = new AclBindingFilter(ResourcePatternFilter.ANY, new AccessControlEntryFilter("User:ANONYMOUS", null, ANY, AclPermissionType.ANY));

    private static final AclBindingFilter ANY_DENY = new AclBindingFilter(ResourcePatternFilter.ANY, new AccessControlEntryFilter(null, null, ANY, DENY));

    private static final AclBindingFilter ANY_MYTOPIC = new AclBindingFilter(new ResourcePatternFilter(ResourceType.TOPIC, "mytopic", PatternType.LITERAL), new AccessControlEntryFilter(null, null, ANY, AclPermissionType.ANY));

    @Test
    public void testMatching() {
        Assert.assertEquals(AclBindingTest.ACL1, AclBindingTest.ACL1);
        final AclBinding acl1Copy = new AclBinding(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.TOPIC, "mytopic", PatternType.LITERAL), new AccessControlEntry("User:ANONYMOUS", "", ALL, ALLOW));
        Assert.assertEquals(AclBindingTest.ACL1, acl1Copy);
        Assert.assertEquals(acl1Copy, AclBindingTest.ACL1);
        Assert.assertEquals(AclBindingTest.ACL2, AclBindingTest.ACL2);
        Assert.assertNotEquals(AclBindingTest.ACL1, AclBindingTest.ACL2);
        Assert.assertNotEquals(AclBindingTest.ACL2, AclBindingTest.ACL1);
        Assert.assertTrue(ANY.matches(AclBindingTest.ACL1));
        Assert.assertNotEquals(ANY, AclBindingTest.ACL1);
        Assert.assertTrue(ANY.matches(AclBindingTest.ACL2));
        Assert.assertNotEquals(ANY, AclBindingTest.ACL2);
        Assert.assertTrue(ANY.matches(AclBindingTest.ACL3));
        Assert.assertNotEquals(ANY, AclBindingTest.ACL3);
        Assert.assertEquals(ANY, ANY);
        Assert.assertTrue(AclBindingTest.ANY_ANONYMOUS.matches(AclBindingTest.ACL1));
        Assert.assertNotEquals(AclBindingTest.ANY_ANONYMOUS, AclBindingTest.ACL1);
        Assert.assertFalse(AclBindingTest.ANY_ANONYMOUS.matches(AclBindingTest.ACL2));
        Assert.assertNotEquals(AclBindingTest.ANY_ANONYMOUS, AclBindingTest.ACL2);
        Assert.assertTrue(AclBindingTest.ANY_ANONYMOUS.matches(AclBindingTest.ACL3));
        Assert.assertNotEquals(AclBindingTest.ANY_ANONYMOUS, AclBindingTest.ACL3);
        Assert.assertFalse(AclBindingTest.ANY_DENY.matches(AclBindingTest.ACL1));
        Assert.assertFalse(AclBindingTest.ANY_DENY.matches(AclBindingTest.ACL2));
        Assert.assertTrue(AclBindingTest.ANY_DENY.matches(AclBindingTest.ACL3));
        Assert.assertTrue(AclBindingTest.ANY_MYTOPIC.matches(AclBindingTest.ACL1));
        Assert.assertTrue(AclBindingTest.ANY_MYTOPIC.matches(AclBindingTest.ACL2));
        Assert.assertFalse(AclBindingTest.ANY_MYTOPIC.matches(AclBindingTest.ACL3));
        Assert.assertTrue(AclBindingTest.ANY_ANONYMOUS.matches(AclBindingTest.UNKNOWN_ACL));
        Assert.assertTrue(AclBindingTest.ANY_DENY.matches(AclBindingTest.UNKNOWN_ACL));
        Assert.assertEquals(AclBindingTest.UNKNOWN_ACL, AclBindingTest.UNKNOWN_ACL);
        Assert.assertFalse(AclBindingTest.ANY_MYTOPIC.matches(AclBindingTest.UNKNOWN_ACL));
    }

    @Test
    public void testUnknowns() {
        Assert.assertFalse(AclBindingTest.ACL1.isUnknown());
        Assert.assertFalse(AclBindingTest.ACL2.isUnknown());
        Assert.assertFalse(AclBindingTest.ACL3.isUnknown());
        Assert.assertFalse(AclBindingTest.ANY_ANONYMOUS.isUnknown());
        Assert.assertFalse(AclBindingTest.ANY_DENY.isUnknown());
        Assert.assertFalse(AclBindingTest.ANY_MYTOPIC.isUnknown());
        Assert.assertTrue(AclBindingTest.UNKNOWN_ACL.isUnknown());
    }

    @Test
    public void testMatchesAtMostOne() {
        Assert.assertNull(AclBindingTest.ACL1.toFilter().findIndefiniteField());
        Assert.assertNull(AclBindingTest.ACL2.toFilter().findIndefiniteField());
        Assert.assertNull(AclBindingTest.ACL3.toFilter().findIndefiniteField());
        Assert.assertFalse(AclBindingTest.ANY_ANONYMOUS.matchesAtMostOne());
        Assert.assertFalse(AclBindingTest.ANY_DENY.matchesAtMostOne());
        Assert.assertFalse(AclBindingTest.ANY_MYTOPIC.matchesAtMostOne());
    }

    @Test
    public void shouldNotThrowOnUnknownPatternType() {
        new AclBinding(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.TOPIC, "foo", PatternType.UNKNOWN), AclBindingTest.ACL1.entry());
    }

    @Test
    public void shouldNotThrowOnUnknownResourceType() {
        new AclBinding(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.UNKNOWN, "foo", PatternType.LITERAL), AclBindingTest.ACL1.entry());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnMatchPatternType() {
        new AclBinding(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.TOPIC, "foo", PatternType.MATCH), AclBindingTest.ACL1.entry());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnAnyPatternType() {
        new AclBinding(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.TOPIC, "foo", PatternType.ANY), AclBindingTest.ACL1.entry());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnAnyResourceType() {
        new AclBinding(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.ANY, "foo", PatternType.LITERAL), AclBindingTest.ACL1.entry());
    }
}

