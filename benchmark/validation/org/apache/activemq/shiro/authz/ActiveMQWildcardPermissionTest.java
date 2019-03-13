/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.shiro.authz;


import java.util.List;
import java.util.Set;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @since 5.10.0
 */
public class ActiveMQWildcardPermissionTest {
    @Test
    public void testNotWildcardPermission() {
        ActiveMQWildcardPermission perm = new ActiveMQWildcardPermission("topic:TEST:*");
        Permission dummy = new Permission() {
            @Override
            public boolean implies(Permission p) {
                return false;
            }
        };
        Assert.assertFalse(perm.implies(dummy));
    }

    @Test
    public void testIntrapartWildcard() {
        ActiveMQWildcardPermission superset = new ActiveMQWildcardPermission("topic:ActiveMQ.Advisory.*:read");
        ActiveMQWildcardPermission subset = new ActiveMQWildcardPermission("topic:ActiveMQ.Advisory.Topic:read");
        Assert.assertTrue(superset.implies(subset));
        Assert.assertFalse(subset.implies(superset));
    }

    @Test
    public void testMatches() {
        ActiveMQWildcardPermissionTest.assertMatch("x", "x");
        ActiveMQWildcardPermissionTest.assertNoMatch("x", "y");
        ActiveMQWildcardPermissionTest.assertMatch("xx", "xx");
        ActiveMQWildcardPermissionTest.assertNoMatch("xy", "xz");
        ActiveMQWildcardPermissionTest.assertMatch("?", "x");
        ActiveMQWildcardPermissionTest.assertMatch("x?", "xy");
        ActiveMQWildcardPermissionTest.assertMatch("?y", "xy");
        ActiveMQWildcardPermissionTest.assertMatch("x?z", "xyz");
        ActiveMQWildcardPermissionTest.assertMatch("*", "x");
        ActiveMQWildcardPermissionTest.assertMatch("x*", "x");
        ActiveMQWildcardPermissionTest.assertMatch("x*", "xy");
        ActiveMQWildcardPermissionTest.assertMatch("xy*", "xy");
        ActiveMQWildcardPermissionTest.assertMatch("xy*", "xyz");
        ActiveMQWildcardPermissionTest.assertMatch("*x", "x");
        ActiveMQWildcardPermissionTest.assertNoMatch("*x", "y");
        ActiveMQWildcardPermissionTest.assertMatch("*x", "wx");
        ActiveMQWildcardPermissionTest.assertNoMatch("*x", "wz");
        ActiveMQWildcardPermissionTest.assertMatch("*x", "vwx");
        ActiveMQWildcardPermissionTest.assertMatch("x*z", "xz");
        ActiveMQWildcardPermissionTest.assertMatch("x*z", "xyz");
        ActiveMQWildcardPermissionTest.assertMatch("x*z", "xyyz");
        ActiveMQWildcardPermissionTest.assertNoMatch("ab*t?z", "abz");
        ActiveMQWildcardPermissionTest.assertNoMatch("ab*d*yz", "abcdz");
        ActiveMQWildcardPermissionTest.assertMatch("ab**cd**ef*yz", "abcdefyz");
        ActiveMQWildcardPermissionTest.assertMatch("a*c?*z", "abcxyz");
        ActiveMQWildcardPermissionTest.assertMatch("a*cd*z", "abcdxyz");
        ActiveMQWildcardPermissionTest.assertMatch("*", "x:x");
        ActiveMQWildcardPermissionTest.assertMatch("*", "x:x:x");
        ActiveMQWildcardPermissionTest.assertMatch("x", "x:y");
        ActiveMQWildcardPermissionTest.assertMatch("x", "x:y:z");
        ActiveMQWildcardPermissionTest.assertMatch("foo?armat*", "foobarmatches");
        ActiveMQWildcardPermissionTest.assertMatch("f*", "f");
        ActiveMQWildcardPermissionTest.assertNoMatch("foo", "f");
        ActiveMQWildcardPermissionTest.assertMatch("fo*b", "foob");
        ActiveMQWildcardPermissionTest.assertNoMatch("fo*b*r", "fooba");
        ActiveMQWildcardPermissionTest.assertNoMatch("foo*", "f");
        ActiveMQWildcardPermissionTest.assertMatch("t*k?ou", "thankyou");
        ActiveMQWildcardPermissionTest.assertMatch("he*l*world", "helloworld");
        ActiveMQWildcardPermissionTest.assertNoMatch("foo", "foob");
        ActiveMQWildcardPermissionTest.assertMatch("*:ActiveMQ.Advisory", "foo:ActiveMQ.Advisory");
        ActiveMQWildcardPermissionTest.assertNoMatch("*:ActiveMQ.Advisory", "foo:ActiveMQ.Advisory.");
        ActiveMQWildcardPermissionTest.assertMatch("*:ActiveMQ.Advisory*", "foo:ActiveMQ.Advisory");
        ActiveMQWildcardPermissionTest.assertMatch("*:ActiveMQ.Advisory*", "foo:ActiveMQ.Advisory.");
        ActiveMQWildcardPermissionTest.assertMatch("*:ActiveMQ.Advisory.*", "foo:ActiveMQ.Advisory.Connection");
        ActiveMQWildcardPermissionTest.assertMatch("*:ActiveMQ.Advisory*:read", "foo:ActiveMQ.Advisory.Connection:read");
        ActiveMQWildcardPermissionTest.assertNoMatch("*:ActiveMQ.Advisory*:read", "foo:ActiveMQ.Advisory.Connection:write");
        ActiveMQWildcardPermissionTest.assertMatch("*:ActiveMQ.Advisory*:*", "foo:ActiveMQ.Advisory.Connection:read");
        ActiveMQWildcardPermissionTest.assertMatch("*:ActiveMQ.Advisory*:*", "foo:ActiveMQ.Advisory.");
        ActiveMQWildcardPermissionTest.assertMatch("topic", "topic:TEST:*");
        ActiveMQWildcardPermissionTest.assertNoMatch("*:ActiveMQ*", "topic:TEST:*");
        ActiveMQWildcardPermissionTest.assertMatch("topic:ActiveMQ.Advisory*", "topic:ActiveMQ.Advisory.Connection:create");
        ActiveMQWildcardPermissionTest.assertMatch("foo?ar", "foobar");
    }

    @Test(expected = IllegalStateException.class)
    public void testGetPartsByReflectionThrowingException() {
        ActiveMQWildcardPermission perm = new ActiveMQWildcardPermission("foo:bar") {
            @Override
            protected List<Set<String>> doGetPartsByReflection(WildcardPermission wp) throws Exception {
                throw new RuntimeException("Testing failure");
            }
        };
        WildcardPermission otherPerm = new WildcardPermission("foo:bar:baz");
        perm.implies(otherPerm);
    }

    @Test
    public void testImpliesWithExtraParts() {
        ActiveMQWildcardPermission perm1 = new ActiveMQWildcardPermission("foo:bar:baz");
        ActiveMQWildcardPermission perm2 = new ActiveMQWildcardPermission("foo:bar");
        Assert.assertFalse(perm1.implies(perm2));
    }
}

