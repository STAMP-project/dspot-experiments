/**
 * Copyright 2013 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.lang;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.SkipJVM;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class SystemTest {
    @Test
    public void copiesArray() {
        Object a = new Object();
        Object b = new Object();
        Object[] src = new Object[]{ a, b, a };
        Object[] dest = new Object[3];
        System.arraycopy(src, 0, dest, 0, 3);
        Assert.assertSame(a, dest[0]);
        Assert.assertSame(b, dest[1]);
        Assert.assertSame(a, dest[2]);
    }

    @Test
    public void copiesPrimitiveArray() {
        int[] src = new int[]{ 23, 24, 25 };
        int[] dest = new int[3];
        System.arraycopy(src, 0, dest, 0, 3);
        Assert.assertEquals(23, dest[0]);
        Assert.assertEquals(24, dest[1]);
        Assert.assertEquals(25, dest[2]);
    }

    @Test
    public void copiesToSubclassArray() {
        String[] src = new String[]{ "foo", "bar", "baz" };
        Object[] dest = new Object[3];
        System.arraycopy(src, 0, dest, 0, 3);
        Assert.assertEquals("foo", dest[0]);
        Assert.assertEquals("bar", dest[1]);
        Assert.assertEquals("baz", dest[2]);
    }

    @Test
    public void copiesToSuperclassArrayWhenItemsMatch() {
        Object[] src = new Object[]{ "foo", "bar", "baz" };
        String[] dest = new String[3];
        System.arraycopy(src, 0, dest, 0, 3);
        Assert.assertEquals("foo", dest[0]);
        Assert.assertEquals("bar", dest[1]);
        Assert.assertEquals("baz", dest[2]);
    }

    @Test
    public void failsToConverItemsCopyingArray() {
        Object[] src = new Object[]{ "foo", 23, "baz" };
        String[] dest = new String[3];
        try {
            System.arraycopy(src, 0, dest, 0, 3);
            Assert.fail("Exception expected");
        } catch (ArrayStoreException e) {
            Assert.assertEquals("foo", dest[0]);
            Assert.assertNull(dest[1]);
            Assert.assertNull(dest[2]);
        }
    }

    @Test(expected = ArrayStoreException.class)
    public void failsToCopyToUnrelatedReferenceArray() {
        String[] src = new String[]{ "foo", "bar", "baz" };
        Integer[] dest = new Integer[3];
        System.arraycopy(src, 0, dest, 0, 3);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void failsToCopyArraysWithInvalidIndexes() {
        System.arraycopy(new Object[0], 0, new Object[0], 0, 1);
    }

    @Test(expected = ArrayStoreException.class)
    public void failsToCopyArraysWithIncompatibleElements() {
        System.arraycopy(new Object[1], 0, new int[1], 0, 1);
    }

    @Test(expected = NullPointerException.class)
    public void failsToCopyFromNullSource() {
        System.arraycopy(null, 0, new int[1], 0, 1);
    }

    @Test(expected = NullPointerException.class)
    public void failsToCopyToNullTarget() {
        System.arraycopy(new Object[1], 0, null, 0, 1);
    }

    @Test
    public void overridesStdErrAndOut() {
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setErr(new PrintStream(err));
        System.setOut(new PrintStream(out));
        System.out.println("out overridden");
        System.out.flush();
        System.err.println("err overridden");
        System.err.flush();
        Assert.assertEquals("err overridden\n", new String(err.toByteArray()));
        Assert.assertEquals("out overridden\n", new String(out.toByteArray()));
    }

    @Test
    @SkipJVM
    public void propertiesWork() {
        Properties properties = System.getProperties();
        Assert.assertNotNull(properties);
        Assert.assertNotNull(properties.getProperty("java.version"));
        System.setProperty("myprop", "foo");
        Assert.assertNull(properties.getProperty("foo"));
        Assert.assertEquals("foo", System.getProperty("myprop"));
        properties = System.getProperties();
        Assert.assertEquals("foo", properties.getProperty("myprop"));
        Properties newProps = new Properties();
        newProps.setProperty("myprop2", "bar");
        System.setProperties(newProps);
        Assert.assertNotNull(System.getProperty("java.version"));
        Assert.assertNull(System.getProperty("myprop"));
        Assert.assertEquals("bar", System.getProperty("myprop2"));
    }
}

