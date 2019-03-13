/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.petra.string;


import NewEnv.Type;
import StringPool.BLANK;
import StringPool.NULL;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.kernel.test.rule.NewEnvTestRule;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.lang.ref.Reference;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;


/**
 *
 *
 * @author Shuyang Zhou
 * @author Manuel de la Pe?a
 */
public class StringBundlerTest {
    @ClassRule
    @Rule
    public static final AggregateTestRule aggregateTestRule = new AggregateTestRule(CodeCoverageAssertor.INSTANCE, NewEnvTestRule.INSTANCE);

    @Test
    public void testAppendBoolean() {
        StringBundler sb = new StringBundler();
        Assert.assertEquals(0, sb.length());
        sb.append(true);
        Assert.assertEquals(4, sb.length());
        Assert.assertEquals("true", sb.toString());
        sb.append(false);
        Assert.assertEquals(9, sb.length());
        Assert.assertEquals("truefalse", sb.toString());
        assertArray(sb, "true", "false");
    }

    @Test
    public void testAppendChar() {
        StringBundler sb = new StringBundler();
        Assert.assertEquals(0, sb.length());
        sb.append('a');
        Assert.assertEquals(1, sb.length());
        Assert.assertEquals("a", sb.toString());
        sb.append('b');
        Assert.assertEquals(2, sb.length());
        Assert.assertEquals("ab", sb.toString());
        sb.append('c');
        Assert.assertEquals(3, sb.length());
        Assert.assertEquals("abc", sb.toString());
        assertArray(sb, "a", "b", "c");
    }

    @Test
    public void testAppendCharArray() {
        StringBundler sb = new StringBundler();
        Assert.assertEquals(0, sb.length());
        sb.append(new char[]{ 'a', 'b' });
        Assert.assertEquals(2, sb.length());
        Assert.assertEquals("ab", sb.toString());
        sb.append(new char[]{ 'c', 'd' });
        Assert.assertEquals(4, sb.length());
        Assert.assertEquals("abcd", sb.toString());
        sb.append(new char[]{ 'e', 'f' });
        Assert.assertEquals(6, sb.length());
        Assert.assertEquals("abcdef", sb.toString());
        assertArray(sb, "ab", "cd", "ef");
    }

    @Test
    public void testAppendDouble() {
        StringBundler sb = new StringBundler();
        Assert.assertEquals(0, sb.length());
        sb.append(1.0);
        Assert.assertEquals(3, sb.length());
        Assert.assertEquals("1.0", sb.toString());
        sb.append(2.1);
        Assert.assertEquals(6, sb.length());
        Assert.assertEquals("1.02.1", sb.toString());
        sb.append(3.2);
        Assert.assertEquals(9, sb.length());
        Assert.assertEquals("1.02.13.2", sb.toString());
        assertArray(sb, "1.0", "2.1", "3.2");
    }

    @Test
    public void testAppendEmptyStringArray() {
        StringBundler sb = new StringBundler();
        sb.append(new String[0]);
        Assert.assertEquals(0, sb.index());
    }

    @Test
    public void testAppendEmptyStringBundler() {
        StringBundler sb = new StringBundler();
        sb.append(new StringBundler());
        Assert.assertEquals(0, sb.index());
    }

    @Test
    public void testAppendFloat() {
        StringBundler sb = new StringBundler();
        Assert.assertEquals(0, sb.length());
        sb.append(1.0F);
        Assert.assertEquals(3, sb.length());
        Assert.assertEquals("1.0", sb.toString());
        sb.append(2.1F);
        Assert.assertEquals(6, sb.length());
        Assert.assertEquals("1.02.1", sb.toString());
        sb.append(3.2F);
        Assert.assertEquals(9, sb.length());
        Assert.assertEquals("1.02.13.2", sb.toString());
        assertArray(sb, "1.0", "2.1", "3.2");
    }

    @Test
    public void testAppendInt() {
        StringBundler sb = new StringBundler();
        Assert.assertEquals(0, sb.length());
        sb.append(1);
        Assert.assertEquals(1, sb.length());
        Assert.assertEquals("1", sb.toString());
        sb.append(2);
        Assert.assertEquals(2, sb.length());
        Assert.assertEquals("12", sb.toString());
        sb.append(3);
        Assert.assertEquals(3, sb.length());
        Assert.assertEquals("123", sb.toString());
        assertArray(sb, "1", "2", "3");
    }

    @Test
    public void testAppendLong() {
        StringBundler sb = new StringBundler();
        Assert.assertEquals(0, sb.length());
        sb.append(1L);
        Assert.assertEquals(1, sb.length());
        Assert.assertEquals("1", sb.toString());
        sb.append(2L);
        Assert.assertEquals(2, sb.length());
        Assert.assertEquals("12", sb.toString());
        sb.append(3L);
        Assert.assertEquals(3, sb.length());
        Assert.assertEquals("123", sb.toString());
        assertArray(sb, "1", "2", "3");
    }

    @Test
    public void testAppendNullCharArray() {
        StringBundler sb = new StringBundler();
        sb.append(((char[]) (null)));
        Assert.assertEquals(1, sb.index());
        Assert.assertEquals("null", sb.stringAt(0));
    }

    @Test
    public void testAppendNullObject() {
        StringBundler sb = new StringBundler();
        sb.append(((Object) (null)));
        Assert.assertEquals(1, sb.index());
        Assert.assertEquals("null", sb.stringAt(0));
    }

    @Test
    public void testAppendNullString() {
        StringBundler sb = new StringBundler();
        sb.append(((String) (null)));
        Assert.assertEquals(1, sb.index());
        Assert.assertEquals(NULL, sb.stringAt(0));
    }

    @Test
    public void testAppendNullStringArray() {
        StringBundler sb = new StringBundler();
        sb.append(((String[]) (null)));
        Assert.assertEquals(0, sb.index());
    }

    @Test
    public void testAppendNullStringBundler() {
        StringBundler sb = new StringBundler();
        sb.append(((StringBundler) (null)));
        Assert.assertEquals(0, sb.index());
    }

    @Test
    public void testAppendStringArrayWithGrowth() {
        StringBundler sb = new StringBundler(2);
        sb.append(new String[]{ "test1", "test2", "test3" });
        Assert.assertEquals(3, sb.index());
        Assert.assertEquals(10, sb.capacity());
        Assert.assertEquals("test1", sb.stringAt(0));
        Assert.assertEquals("test2", sb.stringAt(1));
        Assert.assertEquals("test3", sb.stringAt(2));
        sb = new StringBundler(2);
        sb.append(new String[]{ "test1", "", "test3" });
        Assert.assertEquals(2, sb.index());
        Assert.assertEquals(10, sb.capacity());
        Assert.assertEquals("test1", sb.stringAt(0));
        Assert.assertEquals("test3", sb.stringAt(1));
        sb = new StringBundler(2);
        sb.append(new String[]{ "test1", "test2", null });
        Assert.assertEquals(2, sb.index());
        Assert.assertEquals(10, sb.capacity());
        Assert.assertEquals("test1", sb.stringAt(0));
        Assert.assertEquals("test2", sb.stringAt(1));
    }

    @Test
    public void testAppendStringArrayWithoutGrowth() {
        StringBundler sb = new StringBundler();
        sb.append(new String[]{ "test1", "test2", "test3" });
        Assert.assertEquals(3, sb.index());
        Assert.assertEquals(16, sb.capacity());
        Assert.assertEquals("test1", sb.stringAt(0));
        Assert.assertEquals("test2", sb.stringAt(1));
        Assert.assertEquals("test3", sb.stringAt(2));
        sb = new StringBundler();
        sb.append(new String[]{ "test1", "", "test3" });
        Assert.assertEquals(2, sb.index());
        Assert.assertEquals(16, sb.capacity());
        Assert.assertEquals("test1", sb.stringAt(0));
        Assert.assertEquals("test3", sb.stringAt(1));
        sb = new StringBundler();
        sb.append(new String[]{ "test1", "test2", null });
        Assert.assertEquals(2, sb.index());
        Assert.assertEquals(16, sb.capacity());
        Assert.assertEquals("test1", sb.stringAt(0));
        Assert.assertEquals("test2", sb.stringAt(1));
    }

    @Test
    public void testAppendStringBundlerWithGrowth() {
        StringBundler sb = new StringBundler(2);
        StringBundler testSB = new StringBundler();
        testSB.append("test1");
        testSB.append("test2");
        testSB.append("test3");
        sb.append(testSB);
        Assert.assertEquals(3, sb.index());
        Assert.assertEquals(10, sb.capacity());
        Assert.assertEquals("test3", sb.stringAt(2));
    }

    @Test
    public void testAppendStringBundlerWithoutGrowth() {
        StringBundler sb = new StringBundler();
        StringBundler testSB = new StringBundler();
        testSB.append("test1");
        testSB.append("test2");
        testSB.append("test3");
        sb.append(testSB);
        Assert.assertEquals(3, sb.index());
        Assert.assertEquals(16, sb.capacity());
        Assert.assertEquals("test3", sb.stringAt(2));
    }

    @Test
    public void testAppendStringWithGrowth() {
        StringBundler sb = new StringBundler(2);
        sb.append("test1");
        Assert.assertEquals(1, sb.index());
        Assert.assertEquals(2, sb.capacity());
        Assert.assertEquals("test1", sb.stringAt(0));
        sb.append("test2");
        Assert.assertEquals(2, sb.index());
        Assert.assertEquals(2, sb.capacity());
        Assert.assertEquals("test1", sb.stringAt(0));
        Assert.assertEquals("test2", sb.stringAt(1));
        sb.append("test3");
        Assert.assertEquals(3, sb.index());
        Assert.assertEquals(4, sb.capacity());
        Assert.assertEquals("test1", sb.stringAt(0));
        Assert.assertEquals("test2", sb.stringAt(1));
        Assert.assertEquals("test3", sb.stringAt(2));
    }

    @Test
    public void testAppendStringWithoutGrowing() {
        StringBundler sb = new StringBundler();
        sb.append("test1");
        Assert.assertEquals(1, sb.index());
        Assert.assertEquals(16, sb.capacity());
        Assert.assertEquals("test1", sb.stringAt(0));
        sb.append("test2");
        Assert.assertEquals(2, sb.index());
        Assert.assertEquals(16, sb.capacity());
        Assert.assertEquals("test1", sb.stringAt(0));
        Assert.assertEquals("test2", sb.stringAt(1));
        sb.append("test3");
        Assert.assertEquals(3, sb.index());
        Assert.assertEquals(16, sb.capacity());
        Assert.assertEquals("test1", sb.stringAt(0));
        Assert.assertEquals("test2", sb.stringAt(1));
        Assert.assertEquals("test3", sb.stringAt(2));
    }

    @Test
    public void testConcatObjects() {
        Assert.assertSame("test1", StringBundler.concat(((Object) ("test1"))));
        Assert.assertSame(NULL, StringBundler.concat(new Object[]{ null }));
        Assert.assertEquals("test1test2", StringBundler.concat(((Object) ("test1")), "test2"));
        Assert.assertEquals("test1test2test3", StringBundler.concat("test1", ((Object) ("test2")), "test3"));
        Assert.assertEquals("test1test2test3test4", StringBundler.concat("test1", "test2", "test3", ((Object) ("test4"))));
    }

    @Test
    public void testConcatStrings() {
        Assert.assertSame("test1", StringBundler.concat("test1"));
        Assert.assertSame(NULL, StringBundler.concat(new String[]{ null }));
        Assert.assertEquals("test1test2", StringBundler.concat("test1", "test2"));
        Assert.assertEquals("abcdef", StringBundler.concat("a", "bc", "def"));
        Assert.assertEquals("abcdef", StringBundler.concat("abc", "de", "f"));
        Assert.assertEquals("test1test2test3test4", StringBundler.concat("test1", "test2", "test3", "test4"));
    }

    @Test
    public void testConstructor() {
        StringBundler sb = new StringBundler();
        Assert.assertEquals(0, sb.index());
        Assert.assertEquals(16, sb.capacity());
    }

    @Test
    public void testConstructorWithCapacity() {
        StringBundler sb = new StringBundler(32);
        Assert.assertEquals(0, sb.index());
        Assert.assertEquals(32, sb.capacity());
        sb = new StringBundler(0);
        Assert.assertEquals(0, sb.index());
        Assert.assertEquals(16, sb.capacity());
    }

    @Test
    public void testConstructorWithString() {
        StringBundler sb = new StringBundler("test");
        Assert.assertEquals(1, sb.index());
        Assert.assertEquals("test", sb.stringAt(0));
        Assert.assertEquals(16, sb.capacity());
    }

    @Test
    public void testConstructorWithStringArray() {
        StringBundler sb = new StringBundler(new String[]{ "aa", "bb" });
        Assert.assertEquals(2, sb.index());
        Assert.assertEquals("aa", sb.stringAt(0));
        Assert.assertEquals("bb", sb.stringAt(1));
        Assert.assertEquals(2, sb.capacity());
    }

    @Test
    public void testConstructorWithStringArrayEmpty() {
        StringBundler sb = new StringBundler(new String[]{ "", "bb" });
        Assert.assertEquals(1, sb.index());
        Assert.assertEquals("bb", sb.stringAt(0));
        Assert.assertEquals(2, sb.capacity());
    }

    @Test
    public void testConstructorWithStringArrayExtraSpace() {
        StringBundler sb = new StringBundler(new String[]{ "aa", "bb" }, 3);
        Assert.assertEquals(2, sb.index());
        Assert.assertEquals("aa", sb.stringAt(0));
        Assert.assertEquals("bb", sb.stringAt(1));
        Assert.assertEquals(5, sb.capacity());
    }

    @Test
    public void testConstructorWithStringArrayExtraSpaceEmpty() {
        StringBundler sb = new StringBundler(new String[]{ "", "bb" }, 3);
        Assert.assertEquals(1, sb.index());
        Assert.assertEquals("bb", sb.stringAt(0));
        Assert.assertEquals(5, sb.capacity());
    }

    @Test
    public void testConstructorWithStringArrayExtraSpaceNull() {
        StringBundler sb = new StringBundler(new String[]{ "aa", null }, 3);
        Assert.assertEquals(1, sb.index());
        Assert.assertEquals("aa", sb.stringAt(0));
        Assert.assertEquals(5, sb.capacity());
    }

    @Test
    public void testConstructorWithStringArrayNull() {
        StringBundler sb = new StringBundler(new String[]{ "aa", null });
        Assert.assertEquals(1, sb.index());
        Assert.assertEquals("aa", sb.stringAt(0));
        Assert.assertEquals(2, sb.capacity());
    }

    @Test
    public void testEmptyString() {
        StringBundler sb = new StringBundler();
        sb.append(BLANK);
        Assert.assertEquals(0, sb.index());
    }

    @Test
    public void testSerialization() throws Exception {
        StringBundler sb = new StringBundler();
        sb.append("test1");
        sb.append("test2");
        sb.append("test3");
        ByteArrayOutputStream unsyncByteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(unsyncByteArrayOutputStream)) {
            objectOutputStream.writeObject(sb);
        }
        byte[] bytes = unsyncByteArrayOutputStream.toByteArray();
        ByteArrayInputStream unsyncByteArrayInputStream = new ByteArrayInputStream(bytes);
        StringBundler cloneSB = null;
        try (ObjectInputStream objectInputStream = new ObjectInputStream(unsyncByteArrayInputStream)) {
            cloneSB = ((StringBundler) (objectInputStream.readObject()));
        }
        Assert.assertEquals(sb.capacity(), cloneSB.capacity());
        Assert.assertEquals(sb.index(), cloneSB.index());
        for (int i = 0; i < (sb.index()); i++) {
            Assert.assertEquals(sb.stringAt(i), cloneSB.stringAt(i));
        }
    }

    @Test
    public void testSetIndex() {
        // Negative index
        StringBundler sb = new StringBundler();
        try {
            sb.setIndex((-1));
            Assert.fail();
        } catch (ArrayIndexOutOfBoundsException aioobe) {
        }
        // New index equals current index
        sb = new StringBundler(4);
        sb.append("test1");
        sb.append("test2");
        Assert.assertEquals(2, sb.index());
        Assert.assertEquals("test1", sb.stringAt(0));
        Assert.assertEquals("test2", sb.stringAt(1));
        sb.setIndex(2);
        Assert.assertEquals(2, sb.index());
        Assert.assertEquals("test1", sb.stringAt(0));
        Assert.assertEquals("test2", sb.stringAt(1));
        // New index is larger than the current index but smaller than the array
        // size
        Assert.assertEquals(4, sb.capacity());
        sb.setIndex(4);
        Assert.assertEquals(4, sb.capacity());
        Assert.assertEquals(4, sb.index());
        Assert.assertEquals("test1", sb.stringAt(0));
        Assert.assertEquals("test2", sb.stringAt(1));
        Assert.assertEquals(BLANK, sb.stringAt(2));
        Assert.assertEquals(BLANK, sb.stringAt(3));
        // New index is larger than the current index and array size
        sb.setIndex(6);
        Assert.assertEquals(6, sb.capacity());
        Assert.assertEquals(6, sb.index());
        Assert.assertEquals("test1", sb.stringAt(0));
        Assert.assertEquals("test2", sb.stringAt(1));
        Assert.assertEquals(BLANK, sb.stringAt(2));
        Assert.assertEquals(BLANK, sb.stringAt(3));
        Assert.assertEquals(BLANK, sb.stringAt(4));
        Assert.assertEquals(BLANK, sb.stringAt(5));
        // New index is smaller than current index
        sb.setIndex(1);
        Assert.assertEquals(6, sb.capacity());
        Assert.assertEquals(1, sb.index());
        Assert.assertEquals("test1", sb.stringAt(0));
        try {
            Assert.assertEquals(null, sb.stringAt(1));
            Assert.fail();
        } catch (ArrayIndexOutOfBoundsException aioobe) {
        }
    }

    @Test
    public void testSetStringAtAndStringAt() {
        StringBundler sb = new StringBundler();
        try {
            sb.setStringAt(null, (-1));
            Assert.fail();
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            Assert.assertEquals("Array index out of range: -1", aioobe.getMessage());
        }
        try {
            sb.setStringAt(null, 0);
            Assert.fail();
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            Assert.assertEquals("Array index out of range: 0", aioobe.getMessage());
        }
        try {
            sb.stringAt((-1));
            Assert.fail();
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            Assert.assertEquals("Array index out of range: -1", aioobe.getMessage());
        }
        try {
            sb.stringAt(0);
            Assert.fail();
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            Assert.assertEquals("Array index out of range: 0", aioobe.getMessage());
        }
        sb.append("test1");
        Assert.assertEquals("test1", sb.stringAt(0));
        sb.setStringAt("test2", 0);
        Assert.assertEquals("test2", sb.stringAt(0));
    }

    @NewEnv(type = Type.CLASSLOADER)
    @Test
    public void testStringBuilderEnsureCapacity() {
        int threadLocalBufferLimit = 10;
        String propertyKey = (StringBundler.class.getName()) + ".threadlocal.buffer.limit";
        String propertyValue = System.getProperty(propertyKey);
        System.setProperty(propertyKey, String.valueOf(threadLocalBufferLimit));
        try {
            Assert.assertEquals(Integer.valueOf(threadLocalBufferLimit), ReflectionTestUtil.getFieldValue(StringBundler.class, "_THREAD_LOCAL_BUFFER_LIMIT"));
            StringBundler sb = new StringBundler(4);
            sb.append("test1");
            sb.append("test2");
            sb.append("test3");
            Assert.assertEquals("test1test2test3", sb.toString());
            sb.append("test4");
            Assert.assertEquals("test1test2test3test4", sb.toString());
            sb.setIndex(((sb.index()) - 1));
            Assert.assertEquals("test1test2test3", sb.toString());
            sb.append("test4test5test6test7test8test9test10");
            Assert.assertEquals("test1test2test3test4test5test6test7test8test9test10", sb.toString());
        } finally {
            if (propertyValue == null) {
                System.clearProperty(propertyKey);
            } else {
                System.setProperty(propertyKey, propertyValue);
            }
        }
    }

    @Test
    public void testToString() {
        StringBundler sb = new StringBundler();
        sb.append("test1");
        sb.append("test2");
        sb.append("test3");
        Assert.assertEquals("test1test2test3", sb.toString());
        // StringBuilder
        sb.append("test4");
        Assert.assertEquals("test1test2test3test4", sb.toString());
    }

    @Test
    public void testToStringEmpty() {
        StringBundler sb = new StringBundler();
        Assert.assertEquals(BLANK, sb.toString());
    }

    @NewEnv(type = Type.CLASSLOADER)
    @Test
    public void testToStringWithoutThreadLocalBufferMaxValueLimit() {
        String propertyKey = (StringBundler.class.getName()) + ".threadlocal.buffer.limit";
        String propertyValue = System.getProperty(propertyKey);
        System.setProperty(propertyKey, String.valueOf(Integer.MAX_VALUE));
        testToStringWithoutThreadLocalBuffer(propertyKey, propertyValue);
    }

    @NewEnv(type = Type.CLASSLOADER)
    @Test
    public void testToStringWithoutThreadLocalBufferNoSetting() {
        String propertyKey = (StringBundler.class.getName()) + ".threadlocal.buffer.limit";
        String propertyValue = System.getProperty(propertyKey);
        System.clearProperty(propertyKey);
        testToStringWithoutThreadLocalBuffer(propertyKey, propertyValue);
    }

    @NewEnv(type = Type.CLASSLOADER)
    @Test
    public void testToStringWithoutThreadLocalBufferZeroLimit() {
        String propertyKey = (StringBundler.class.getName()) + ".threadlocal.buffer.limit";
        String propertyValue = System.getProperty(propertyKey);
        System.setProperty(propertyKey, "0");
        testToStringWithoutThreadLocalBuffer(propertyKey, propertyValue);
    }

    @NewEnv(type = Type.CLASSLOADER)
    @Test
    public void testToStringWithThreadLocalBuffer() throws Exception {
        int threadLocalBufferLimit = 3;
        String propertyKey = (StringBundler.class.getName()) + ".threadlocal.buffer.limit";
        String propertyValue = System.getProperty(propertyKey);
        System.setProperty(propertyKey, String.valueOf(threadLocalBufferLimit));
        try {
            Assert.assertEquals(Integer.valueOf(threadLocalBufferLimit), ReflectionTestUtil.getFieldValue(StringBundler.class, "_THREAD_LOCAL_BUFFER_LIMIT"));
            ThreadLocal<Reference<StringBuilder>> threadLocal = ReflectionTestUtil.getFieldValue(StringBundler.class, "_stringBuilderThreadLocal");
            Assert.assertNotNull(threadLocal);
            threadLocal.remove();
            StringBundler sb = new StringBundler();
            sb.append("1");
            sb.append("2");
            sb.append("3");
            sb.append("4");
            Assert.assertEquals("1234", sb.toString());
            Reference<StringBuilder> reference = threadLocal.get();
            StringBuilder stringBuilder = reference.get();
            Assert.assertNotNull(stringBuilder);
            Assert.assertEquals(4, stringBuilder.length());
            sb.append("5");
            Assert.assertEquals("12345", sb.toString());
            reference = threadLocal.get();
            Assert.assertSame(stringBuilder, reference.get());
            Assert.assertEquals(5, stringBuilder.length());
            sb.append("6");
            Assert.assertEquals("123456", sb.toString());
            reference = threadLocal.get();
            Assert.assertSame(stringBuilder, reference.get());
            Assert.assertEquals(6, stringBuilder.length());
        } finally {
            if (propertyValue == null) {
                System.clearProperty(propertyKey);
            } else {
                System.setProperty(propertyKey, propertyValue);
            }
        }
    }

    @Test
    public void testWriteTo() throws IOException {
        StringBundler sb = new StringBundler();
        sb.append("test1");
        sb.append("test2");
        sb.append("test3");
        sb.append("test4");
        sb.append("test5");
        StringWriter stringWriter = new StringWriter();
        sb.writeTo(stringWriter);
        Assert.assertEquals("test1test2test3test4test5", stringWriter.toString());
    }
}

