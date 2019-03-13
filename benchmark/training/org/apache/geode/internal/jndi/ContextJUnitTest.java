/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.jndi;


import javax.naming.Context;
import javax.naming.ContextNotEmptyException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.NoPermissionException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests all basic methods of ContextImpl.
 */
public class ContextJUnitTest {
    private Context initialContext;

    private Context gemfireContext;

    private Context envContext;

    private Context dataSourceContext;

    /**
     * Tests inability to create duplicate subcontexts.
     */
    @Test
    public void testSubcontextCreationOfDuplicates() throws NamingException {
        // Try to create duplicate subcontext
        try {
            initialContext.createSubcontext("java:gf");
            Assert.fail();
        } catch (NameAlreadyBoundException expected) {
        }
        // Try to create duplicate subcontext using multi-component name
        try {
            gemfireContext.createSubcontext("env/datasource");
            Assert.fail();
        } catch (NameAlreadyBoundException expected) {
        }
    }

    /**
     * Tests inability to destroy non empty subcontexts.
     */
    @Test
    public void testSubcontextNonEmptyDestruction() throws Exception {
        // Bind some object in ejb subcontext
        dataSourceContext.bind("Test", "Object");
        // Attempt to destroy any subcontext
        try {
            initialContext.destroySubcontext("java:gf");
            Assert.fail();
        } catch (ContextNotEmptyException expected) {
        }
        try {
            initialContext.destroySubcontext("java:gf/env/datasource");
            Assert.fail();
        } catch (ContextNotEmptyException expected) {
        }
        try {
            envContext.destroySubcontext("datasource");
            Assert.fail();
        } catch (ContextNotEmptyException expected) {
        }
    }

    /**
     * Tests ability to destroy empty subcontexts.
     */
    @Test
    public void testSubcontextDestruction() throws Exception {
        // Create three new subcontexts
        dataSourceContext.createSubcontext("sub1");
        dataSourceContext.createSubcontext("sub2");
        envContext.createSubcontext("sub3");
        // Destroy
        initialContext.destroySubcontext("java:gf/env/datasource/sub1");
        dataSourceContext.destroySubcontext("sub2");
        envContext.destroySubcontext("sub3");
        // Perform lookup
        try {
            dataSourceContext.lookup("sub1");
            Assert.fail();
        } catch (NameNotFoundException expected) {
        }
        try {
            envContext.lookup("datasource/sub2");
            Assert.fail();
        } catch (NameNotFoundException expected) {
        }
        try {
            initialContext.lookup("java:gf/sub3");
            Assert.fail();
        } catch (NameNotFoundException expected) {
        }
    }

    /**
     * Tests inability to invoke methods on destroyed subcontexts.
     */
    @Test
    public void testSubcontextInvokingMethodsOnDestroyedContext() throws Exception {
        // Create subcontext and destroy it.
        Context sub = dataSourceContext.createSubcontext("sub4");
        initialContext.destroySubcontext("java:gf/env/datasource/sub4");
        try {
            sub.bind("name", "object");
            Assert.fail();
        } catch (NoPermissionException expected) {
        }
        try {
            sub.unbind("name");
            Assert.fail();
        } catch (NoPermissionException expected) {
        }
        try {
            sub.createSubcontext("sub5");
            Assert.fail();
        } catch (NoPermissionException expected) {
        }
        try {
            sub.destroySubcontext("sub6");
            Assert.fail();
        } catch (NoPermissionException expected) {
        }
        try {
            sub.list("");
            Assert.fail();
        } catch (NoPermissionException expected) {
        }
        try {
            sub.lookup("name");
            Assert.fail();
        } catch (NoPermissionException expected) {
        }
        try {
            sub.composeName("name", "prefix");
            Assert.fail();
        } catch (NoPermissionException expected) {
        }
        try {
            NameParserImpl parser = new NameParserImpl();
            sub.composeName(parser.parse("a"), parser.parse("b"));
            Assert.fail();
        } catch (NoPermissionException expected) {
        }
    }

    /**
     * Tests ability to bind name to object.
     */
    @Test
    public void testBindLookup() throws Exception {
        Object obj1 = new String("Object1");
        Object obj2 = new String("Object2");
        Object obj3 = new String("Object3");
        dataSourceContext.bind("sub21", null);
        dataSourceContext.bind("sub22", obj1);
        initialContext.bind("java:gf/env/sub23", null);
        initialContext.bind("java:gf/env/sub24", obj2);
        // Bind to subcontexts that do not exist
        initialContext.bind("java:gf/env/datasource/sub25/sub26", obj3);
        // Try to lookup
        Assert.assertNull(dataSourceContext.lookup("sub21"));
        Assert.assertSame(dataSourceContext.lookup("sub22"), obj1);
        Assert.assertNull(gemfireContext.lookup("env/sub23"));
        Assert.assertSame(initialContext.lookup("java:gf/env/sub24"), obj2);
        Assert.assertSame(dataSourceContext.lookup("sub25/sub26"), obj3);
    }

    /**
     * Tests ability to unbind names.
     */
    @Test
    public void testUnbind() throws Exception {
        envContext.bind("sub31", null);
        gemfireContext.bind("env/ejb/sub32", new String("UnbindObject"));
        // Unbind
        initialContext.unbind("java:gf/env/sub31");
        dataSourceContext.unbind("sub32");
        try {
            envContext.lookup("sub31");
            Assert.fail();
        } catch (NameNotFoundException expected) {
        }
        try {
            initialContext.lookup("java:gf/env/sub32");
            Assert.fail();
        } catch (NameNotFoundException expected) {
        }
        // Unbind non-existing name
        dataSourceContext.unbind("doesNotExist");
        // Unbind non-existing name, when subcontext does not exists
        try {
            gemfireContext.unbind("env/x/y");
            Assert.fail();
        } catch (NameNotFoundException expected) {
        }
    }

    /**
     * Tests ability to list bindings for a context - specified by name through object reference.
     */
    @Test
    public void testListBindings() throws Exception {
        gemfireContext.bind("env/datasource/sub41", "ListBindings1");
        envContext.bind("sub42", "ListBindings2");
        dataSourceContext.bind("sub43", null);
        // Verify bindings for context specified by reference
        verifyListBindings(envContext, "", "ListBindings1", "ListBindings2");
        // Verify bindings for context specified by name
        verifyListBindings(initialContext, "java:gf/env", "ListBindings1", "ListBindings2");
    }

    @Test
    public void testCompositeName() throws Exception {
        ContextImpl c = new ContextImpl();
        Object o = new Object();
        c.rebind("/a/b/c/", o);
        Assert.assertEquals(c.lookup("a/b/c"), o);
        Assert.assertEquals(c.lookup("///a/b/c///"), o);
    }

    @Test
    public void testLookup() throws Exception {
        ContextImpl ctx = new ContextImpl();
        Object obj = new Object();
        ctx.rebind("a/b/c/d", obj);
        Assert.assertEquals(obj, ctx.lookup("a/b/c/d"));
        ctx.bind("a", obj);
        Assert.assertEquals(obj, ctx.lookup("a"));
    }

    /**
     * Tests "getCompositeName" method
     */
    @Test
    public void testGetCompositeName() throws Exception {
        ContextImpl ctx = new ContextImpl();
        ctx.rebind("a/b/c/d", new Object());
        ContextImpl subCtx;
        subCtx = ((ContextImpl) (ctx.lookup("a")));
        Assert.assertEquals("a", subCtx.getCompoundStringName());
        subCtx = ((ContextImpl) (ctx.lookup("a/b/c")));
        Assert.assertEquals("a/b/c", subCtx.getCompoundStringName());
    }

    /**
     * Tests substitution of '.' with '/' when parsing string names.
     */
    @Test
    public void testTwoSeparatorNames() throws Exception {
        ContextImpl ctx = new ContextImpl();
        Object obj = new Object();
        ctx.bind("a/b.c.d/e", obj);
        Assert.assertEquals(ctx.lookup("a/b/c/d/e"), obj);
        Assert.assertEquals(ctx.lookup("a.b/c.d.e"), obj);
        Assert.assertTrue(((ctx.lookup("a.b.c.d")) instanceof Context));
    }
}

