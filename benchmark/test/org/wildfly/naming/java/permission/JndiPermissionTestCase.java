/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2016, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.wildfly.naming.java.permission;


import JndiPermission.ACTION_ALL;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Enumeration;
import org.junit.Assert;
import org.junit.Test;


/**
 * Big ol' JNDI permission test case.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public class JndiPermissionTestCase {
    @Test
    public void testNameImplies() {
        // check the compat <<ALL BINDINGS>> name
        Assert.assertEquals(new JndiPermission("<<ALL BINDINGS>>", "*"), new JndiPermission("-", "*"));
        // check the root - name
        Assert.assertTrue(new JndiPermission("-", "*").implies(new JndiPermission("-", "*")));
        Assert.assertTrue(new JndiPermission("-", "*").implies(new JndiPermission("", "*")));
        Assert.assertTrue(new JndiPermission("-", "*").implies(new JndiPermission("foo", "*")));
        Assert.assertTrue(new JndiPermission("-", "*").implies(new JndiPermission("/foo", "*")));
        Assert.assertTrue(new JndiPermission("-", "*").implies(new JndiPermission("foo/", "*")));
        Assert.assertTrue(new JndiPermission("-", "*").implies(new JndiPermission("foo/bar/baz/zap", "*")));
        Assert.assertTrue(new JndiPermission("-", "*").implies(new JndiPermission("java:foo", "*")));
        // check the non-root - name
        Assert.assertTrue(new JndiPermission("/-", "*").implies(new JndiPermission("/-", "*")));
        Assert.assertTrue(new JndiPermission("/-", "*").implies(new JndiPermission("/", "*")));
        Assert.assertTrue(new JndiPermission("/-", "*").implies(new JndiPermission("//", "*")));
        Assert.assertTrue(new JndiPermission("/-", "*").implies(new JndiPermission("////", "*")));
        Assert.assertTrue(new JndiPermission("/-", "*").implies(new JndiPermission("/foo", "*")));
        Assert.assertTrue(new JndiPermission("/-", "*").implies(new JndiPermission("/foo", "*")));
        Assert.assertTrue(new JndiPermission("/-", "*").implies(new JndiPermission("/foo/", "*")));
        Assert.assertTrue(new JndiPermission("/-", "*").implies(new JndiPermission("/foo/bar/baz/zap", "*")));
        Assert.assertTrue(new JndiPermission("/-", "*").implies(new JndiPermission("java:/foo", "*")));
        Assert.assertTrue(new JndiPermission("foo/-", "*").implies(new JndiPermission("foo/-", "*")));
        Assert.assertTrue(new JndiPermission("foo/-", "*").implies(new JndiPermission("foo/foo", "*")));
        Assert.assertTrue(new JndiPermission("foo/-", "*").implies(new JndiPermission("foo/foo", "*")));
        Assert.assertTrue(new JndiPermission("foo/-", "*").implies(new JndiPermission("foo/foo/", "*")));
        Assert.assertTrue(new JndiPermission("foo/-", "*").implies(new JndiPermission("foo/foo/bar/baz/zap", "*")));
        Assert.assertTrue(new JndiPermission("foo/-", "*").implies(new JndiPermission("java:foo/foo", "*")));
        // check the * name
        Assert.assertTrue(new JndiPermission("*", "*").implies(new JndiPermission("", "*")));
        Assert.assertTrue(new JndiPermission("*", "*").implies(new JndiPermission("foo", "*")));
        Assert.assertFalse(new JndiPermission("*", "*").implies(new JndiPermission("foo/bar", "*")));
        Assert.assertFalse(new JndiPermission("*", "*").implies(new JndiPermission("foo/", "*")));
        Assert.assertFalse(new JndiPermission("*", "*").implies(new JndiPermission("/foo", "*")));
        Assert.assertTrue(new JndiPermission("*/*", "*").implies(new JndiPermission("/foo", "*")));
        Assert.assertTrue(new JndiPermission("/*", "*").implies(new JndiPermission("/foo", "*")));
        Assert.assertTrue(new JndiPermission("*/foo", "*").implies(new JndiPermission("/foo", "*")));
        // check java: support
        Assert.assertEquals(new JndiPermission("java:", "*"), new JndiPermission("", "*"));
        Assert.assertEquals(new JndiPermission("java:/", "*"), new JndiPermission("/", "*"));
        Assert.assertEquals(new JndiPermission("java:-", "*"), new JndiPermission("-", "*"));
        Assert.assertEquals(new JndiPermission("java:*", "*"), new JndiPermission("*", "*"));
    }

    @Test
    public void testActions() {
        Assert.assertEquals(new JndiPermission("foo", "*"), new JndiPermission("foo", "all"));
        Assert.assertEquals(new JndiPermission("foo", "*"), new JndiPermission("foo", "lookup,bind,rebind,unbind,list,listBindings,createSubcontext,destroySubcontext,addNamingListener"));
        Assert.assertEquals(new JndiPermission("foo", "*"), new JndiPermission("foo", "unbind,list,listBindings,createSubcontext,destroySubcontext,addNamingListener,lookup,bind,rebind"));
        Assert.assertTrue(new JndiPermission("foo", "*").implies(new JndiPermission("foo", "lookup")));
        Assert.assertTrue(new JndiPermission("foo", "").implies(new JndiPermission("foo", "")));
        Assert.assertTrue(new JndiPermission("foo", "*").implies(new JndiPermission("foo", "")));
        Assert.assertFalse(new JndiPermission("foo", "").implies(new JndiPermission("foo", "bind")));
        Assert.assertTrue(new JndiPermission("foo", "").withActions("bind").implies(new JndiPermission("foo", "bind")));
        Assert.assertFalse(new JndiPermission("foo", "unbind").withoutActions("unbind").implies(new JndiPermission("foo", "unbind")));
    }

    @Test
    public void testCollection() {
        final PermissionCollection permissionCollection = new JndiPermission("", "").newPermissionCollection();
        Enumeration<Permission> e;
        permissionCollection.add(new JndiPermission("foo/bar", "lookup,bind"));
        Assert.assertTrue(permissionCollection.implies(new JndiPermission("foo/bar", "lookup,bind")));
        Assert.assertFalse(permissionCollection.implies(new JndiPermission("foo/bar", "lookup,bind,unbind")));
        Assert.assertFalse(permissionCollection.implies(new JndiPermission("foo/bar", "unbind")));
        Assert.assertNotNull((e = permissionCollection.elements()));
        Assert.assertTrue(e.hasMoreElements());
        Assert.assertEquals(new JndiPermission("foo/bar", "lookup,bind"), e.nextElement());
        Assert.assertFalse(e.hasMoreElements());
        permissionCollection.add(new JndiPermission("foo/bar", "unbind"));
        Assert.assertTrue(permissionCollection.implies(new JndiPermission("foo/bar", "lookup,bind")));
        Assert.assertTrue(permissionCollection.implies(new JndiPermission("foo/bar", "lookup,bind,unbind")));
        Assert.assertTrue(permissionCollection.implies(new JndiPermission("foo/bar", "unbind")));
        Assert.assertNotNull((e = permissionCollection.elements()));
        Assert.assertTrue(e.hasMoreElements());
        Assert.assertEquals(new JndiPermission("foo/bar", "lookup,bind,unbind"), e.nextElement());
        Assert.assertFalse(e.hasMoreElements());
        permissionCollection.add(new JndiPermission("-", "lookup"));
        Assert.assertTrue(permissionCollection.implies(new JndiPermission("foo/bar", "lookup,bind")));
        Assert.assertTrue(permissionCollection.implies(new JndiPermission("foo/bar", "lookup,bind,unbind")));
        Assert.assertTrue(permissionCollection.implies(new JndiPermission("foo/bar", "unbind")));
        Assert.assertTrue(permissionCollection.implies(new JndiPermission("baz/zap", "lookup")));
        Assert.assertTrue(permissionCollection.implies(new JndiPermission("", "lookup")));
        Assert.assertFalse(permissionCollection.implies(new JndiPermission("baz/zap", "lookup,bind,unbind")));
        Assert.assertFalse(permissionCollection.implies(new JndiPermission("baz/zap", "unbind")));
        Assert.assertNotNull((e = permissionCollection.elements()));
        Assert.assertTrue(e.hasMoreElements());
        Assert.assertEquals(new JndiPermission("foo/bar", "lookup,bind,unbind"), e.nextElement());
        Assert.assertTrue(e.hasMoreElements());
        Assert.assertEquals(new JndiPermission("-", "lookup"), e.nextElement());
        Assert.assertFalse(e.hasMoreElements());
        permissionCollection.add(new JndiPermission("-", "bind,unbind"));
        Assert.assertTrue(permissionCollection.implies(new JndiPermission("foo/bar", "lookup,bind")));
        Assert.assertTrue(permissionCollection.implies(new JndiPermission("foo/bar", "lookup,bind,unbind")));
        Assert.assertTrue(permissionCollection.implies(new JndiPermission("foo/bar", "unbind")));
        Assert.assertTrue(permissionCollection.implies(new JndiPermission("baz/zap", "lookup")));
        Assert.assertTrue(permissionCollection.implies(new JndiPermission("", "lookup")));
        Assert.assertTrue(permissionCollection.implies(new JndiPermission("baz/zap", "lookup,bind,unbind")));
        Assert.assertTrue(permissionCollection.implies(new JndiPermission("baz/zap", "unbind")));
        Assert.assertNotNull((e = permissionCollection.elements()));
        Assert.assertTrue(e.hasMoreElements());
        Assert.assertEquals(new JndiPermission("-", "lookup,bind,unbind"), e.nextElement());
        Assert.assertFalse(e.hasMoreElements());
    }

    @Test
    public void testSecurity() {
        Assert.assertEquals(new JndiPermission("-", Integer.MAX_VALUE).getActionBits(), ACTION_ALL);
        Assert.assertEquals(new JndiPermission("-", Integer.MAX_VALUE), new JndiPermission("-", "*"));
    }

    @Test
    public void testSerialization() {
        final JndiPermission jndiPermission = new JndiPermission("foo/blap/-", "bind,lookup");
        Assert.assertEquals(jndiPermission, readResolve());
    }

    @Test
    public void testCollectionSecurity() {
        final PermissionCollection permissionCollection = new JndiPermission("", "").newPermissionCollection();
        permissionCollection.add(new JndiPermission("foo/bar", "unbind,rebind"));
        permissionCollection.setReadOnly();
        try {
            permissionCollection.add(new JndiPermission("fob/baz", "unbind,rebind"));
            Assert.fail("Expected exception");
        } catch (SecurityException ignored) {
        }
    }

    @Test
    public void testCollectionSerialization() {
        final PermissionCollection permissionCollection = new JndiPermission("", "").newPermissionCollection();
        permissionCollection.add(new JndiPermission("foo/bar", "createSubcontext,rebind"));
        permissionCollection.add(new JndiPermission("foo", "addNamingListener"));
        permissionCollection.add(new JndiPermission("-", "lookup,rebind"));
        final PermissionCollection other = ((PermissionCollection) (readResolve()));
        Enumeration<Permission> e;
        Assert.assertNotNull((e = other.elements()));
        Assert.assertTrue(e.hasMoreElements());
        Assert.assertEquals(new JndiPermission("foo/bar", "createSubcontext,rebind"), e.nextElement());
        Assert.assertTrue(e.hasMoreElements());
        Assert.assertEquals(new JndiPermission("foo", "addNamingListener"), e.nextElement());
        Assert.assertTrue(e.hasMoreElements());
        Assert.assertEquals(new JndiPermission("-", "lookup,rebind"), e.nextElement());
        Assert.assertFalse(e.hasMoreElements());
    }
}

