/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.aop.support;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.tests.aop.interceptor.NopInterceptor;
import org.springframework.tests.aop.interceptor.SerializableNopInterceptor;
import org.springframework.tests.sample.beans.Person;
import org.springframework.util.SerializationTestUtils;


/**
 *
 *
 * @author Rod Johnson
 * @author Chris Beams
 */
public class NameMatchMethodPointcutTests {
    protected NameMatchMethodPointcut pc;

    protected Person proxied;

    protected SerializableNopInterceptor nop;

    @Test
    public void testMatchingOnly() {
        // Can't do exact matching through isMatch
        Assert.assertTrue(pc.isMatch("echo", "ech*"));
        Assert.assertTrue(pc.isMatch("setName", "setN*"));
        Assert.assertTrue(pc.isMatch("setName", "set*"));
        Assert.assertFalse(pc.isMatch("getName", "set*"));
        Assert.assertFalse(pc.isMatch("setName", "set"));
        Assert.assertTrue(pc.isMatch("testing", "*ing"));
    }

    @Test
    public void testEmpty() throws Throwable {
        Assert.assertEquals(0, nop.getCount());
        proxied.getName();
        proxied.setName("");
        proxied.echo(null);
        Assert.assertEquals(0, nop.getCount());
    }

    @Test
    public void testMatchOneMethod() throws Throwable {
        pc.addMethodName("echo");
        pc.addMethodName("set*");
        Assert.assertEquals(0, nop.getCount());
        proxied.getName();
        proxied.getName();
        Assert.assertEquals(0, nop.getCount());
        proxied.echo(null);
        Assert.assertEquals(1, nop.getCount());
        proxied.setName("");
        Assert.assertEquals(2, nop.getCount());
        proxied.setAge(25);
        Assert.assertEquals(25, proxied.getAge());
        Assert.assertEquals(3, nop.getCount());
    }

    @Test
    public void testSets() throws Throwable {
        pc.setMappedNames("set*", "echo");
        Assert.assertEquals(0, nop.getCount());
        proxied.getName();
        proxied.setName("");
        Assert.assertEquals(1, nop.getCount());
        proxied.echo(null);
        Assert.assertEquals(2, nop.getCount());
    }

    @Test
    public void testSerializable() throws Throwable {
        testSets();
        // Count is now 2
        Person p2 = ((Person) (SerializationTestUtils.serializeAndDeserialize(proxied)));
        NopInterceptor nop2 = ((NopInterceptor) (getAdvisors()[0].getAdvice()));
        p2.getName();
        Assert.assertEquals(2, nop2.getCount());
        p2.echo(null);
        Assert.assertEquals(3, nop2.getCount());
    }

    @Test
    public void testEqualsAndHashCode() {
        NameMatchMethodPointcut pc1 = new NameMatchMethodPointcut();
        NameMatchMethodPointcut pc2 = new NameMatchMethodPointcut();
        String foo = "foo";
        Assert.assertEquals(pc1, pc2);
        Assert.assertEquals(pc1.hashCode(), pc2.hashCode());
        pc1.setMappedName(foo);
        Assert.assertFalse(pc1.equals(pc2));
        Assert.assertTrue(((pc1.hashCode()) != (pc2.hashCode())));
        pc2.setMappedName(foo);
        Assert.assertEquals(pc1, pc2);
        Assert.assertEquals(pc1.hashCode(), pc2.hashCode());
    }
}

