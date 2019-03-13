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


import org.apache.shiro.authz.Permission;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @since 5.10.0
 */
public class ActiveMQPermissionResolverTest {
    @Test
    public void testDefault() {
        ActiveMQPermissionResolver resolver = new ActiveMQPermissionResolver();
        Assert.assertTrue(resolver.isCaseSensitive());
        Permission p = resolver.resolvePermission("Foo:Bar");
        Assert.assertNotNull(p);
        Assert.assertTrue((p instanceof ActiveMQWildcardPermission));
        Assert.assertTrue(p.implies(new ActiveMQWildcardPermission("Foo:Bar")));
        Assert.assertFalse(p.implies(new ActiveMQWildcardPermission("foo:bar")));
    }

    @Test
    public void testCaseInsensitive() {
        ActiveMQPermissionResolver resolver = new ActiveMQPermissionResolver();
        resolver.setCaseSensitive(false);
        Assert.assertFalse(resolver.isCaseSensitive());
        Permission p = resolver.resolvePermission("Foo:Bar");
        Assert.assertNotNull(p);
        Assert.assertTrue((p instanceof ActiveMQWildcardPermission));
        Assert.assertTrue(p.implies(new ActiveMQWildcardPermission("foo:bar")));
        Assert.assertTrue(p.implies(new ActiveMQWildcardPermission("Foo:Bar", true)));
    }
}

