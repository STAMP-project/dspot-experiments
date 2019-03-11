/**
 * Copyright 2010-2012 VMware and contributors
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
package org.springsource.loaded.test;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springsource.loaded.SystemClassReflectionRewriter;
import org.springsource.loaded.SystemClassReflectionRewriter.RewriteResult;


/**
 * Tests for checking how reflective calls in system classes are rewritten.
 *
 * @author Andy Clement
 */
public class SystemClassReflectionRewriterTests extends SpringLoadedTests {
    /**
     * First test. Here we are simulating a System class that is making a call to Class.getDeclaredFields. The
     * invocation is rewritten to go via a helper method generated into the target which uses a field settable from
     * outside. The aim is that when SpringLoaded is initialized far enough it can set the fields in these types,
     * effectively plugging in the reflective interceptor system.
     */
    @Test
    public void jlClass_getDeclaredFields() throws Exception {
        byte[] classbytes = loadBytesForClass("system.One");
        RewriteResult rr = SystemClassReflectionRewriter.rewrite("system.One", classbytes);
        byte[] newbytes = rr.bytes;
        Class<?> clazz = loadit("system.One", newbytes);
        // Check the new field and method are in the type:
        // @formatter:off
        Assert.assertEquals(("CLASS: system/One v50 0x0021(public synchronized) super java/lang/Object\n" + (((((("SOURCE: One.java null\n" + "FIELD 0x0009(public static) __sljlcgdfs Ljava/lang/reflect/Method;\n") + "METHOD: 0x0001(public) <init>()V\n") + "METHOD: 0x0001(public) runIt()Ljava/lang/String;\n") + "METHOD: 0x0001(public) fs()[Ljava/lang/reflect/Field;\n") + "METHOD: 0x000a(private static) __sljlcgdfs(Ljava/lang/Class;)[Ljava/lang/reflect/Field;\n") + "\n")), toStringClass(newbytes));
        // @formatter:on
        Object value = run(clazz, "runIt");
        // Check that without the field initialized, things behave as expected
        Assert.assertEquals("complete:fields:null?false fields:size=1", value);
        Assert.assertEquals(0, SystemClassReflectionRewriterTests.callcount);
        // Set the field
        Method m = SystemClassReflectionRewriterTests.class.getDeclaredMethod("helper", Class.class);
        Assert.assertNotNull(m);
        clazz.getDeclaredField(jlcgdfs).set(null, m);
        // Now re-run, should be intercepted to call our helper
        value = run(clazz, "runIt");
        Assert.assertEquals("complete:fields:null?true", value);
        // Check the correct amount of rewriting went on
        Assert.assertTrue((((rr.bits) & (JLC_GETDECLAREDFIELDS)) != 0));
        Assert.assertTrue((((rr.bits) & (~(JLC_GETDECLAREDFIELDS))) == 0));
        Assert.assertEquals(1, SystemClassReflectionRewriterTests.callcount);
        Assert.assertEquals("Class.getDeclaredFields()", rr.summarize());
    }

    @Test
    public void jlClass_getDeclaredField() throws Exception {
        byte[] classbytes = loadBytesForClass("system.Two");
        RewriteResult rr = SystemClassReflectionRewriter.rewrite("system.Two", classbytes);
        byte[] newbytes = rr.bytes;
        Class<?> clazz = loadit("system.Two", newbytes);
        // Check the new field and method are in the type:
        // @formatter:off
        Assert.assertEquals(("CLASS: system/Two v50 0x0021(public synchronized) super java/lang/Object\n" + ((((((("SOURCE: Two.java null\n" + "FIELD 0x0000() s Ljava/lang/String;\n") + "FIELD 0x0009(public static) __sljlcgdf Ljava/lang/reflect/Method;\n") + "METHOD: 0x0001(public) <init>()V\n") + "METHOD: 0x0001(public) runIt()Ljava/lang/String; java/lang/Exception\n") + "METHOD: 0x0001(public) f(Ljava/lang/String;)Ljava/lang/reflect/Field; java/lang/NoSuchFieldException\n") + "METHOD: 0x000a(private static) __sljlcgdf(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/reflect/Field; java/lang/NoSuchFieldException\n") + "\n")), toStringClass(newbytes));
        // @formatter:on
        Object value = run(clazz, "runIt");
        // Check that without the field initialized, things behave as expected
        Assert.assertEquals("complete:field?java.lang.String system.Two.s nsfe", value);
        Assert.assertEquals(0, SystemClassReflectionRewriterTests.callcount);
        // Set the field
        Method m = SystemClassReflectionRewriterTests.class.getDeclaredMethod("helper2", Class.class, String.class);
        Assert.assertNotNull(m);
        clazz.getDeclaredField(jlcgdf).set(null, m);
        // Now re-run, should be intercepted to call our helper
        value = run(clazz, "runIt");
        Assert.assertEquals("complete:field?null nsfe", value);
        // Check the correct amount of rewriting went on
        Assert.assertTrue((((rr.bits) & (JLC_GETDECLAREDFIELD)) != 0));
        Assert.assertTrue((((rr.bits) & (~(JLC_GETDECLAREDFIELD))) == 0));
        Assert.assertEquals(2, SystemClassReflectionRewriterTests.callcount);
        Assert.assertEquals(2, SystemClassReflectionRewriterTests.events.size());
        Assert.assertEquals("helper2(system.Two,s)", SystemClassReflectionRewriterTests.events.get(0));
        Assert.assertEquals("helper2(system.Two,foo)", SystemClassReflectionRewriterTests.events.get(1));
        Assert.assertEquals("Class.getDeclaredField()", rr.summarize());
    }

    @Test
    public void jlClass_getField() throws Exception {
        byte[] classbytes = loadBytesForClass("system.Three");
        RewriteResult rr = SystemClassReflectionRewriter.rewrite("system.Three", classbytes);
        byte[] newbytes = rr.bytes;
        Class<?> clazz = loadit("system.Three", newbytes);
        // Check the new field and method are in the type:
        // @formatter:off
        Assert.assertEquals(("CLASS: system/Three v50 0x0021(public synchronized) super java/lang/Object\n" + ((((((("SOURCE: Three.java null\n" + "FIELD 0x0001(public) s Ljava/lang/String;\n") + "FIELD 0x0009(public static) __sljlcgf Ljava/lang/reflect/Method;\n") + "METHOD: 0x0001(public) <init>()V\n") + "METHOD: 0x0001(public) runIt()Ljava/lang/String; java/lang/Exception\n") + "METHOD: 0x0001(public) f(Ljava/lang/String;)Ljava/lang/reflect/Field; java/lang/NoSuchFieldException\n") + "METHOD: 0x000a(private static) __sljlcgf(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/reflect/Field; java/lang/NoSuchFieldException\n") + "\n")), toStringClass(newbytes));
        // @formatter:on
        Object value = run(clazz, "runIt");
        // Check that without the field initialized, things behave as expected
        Assert.assertEquals("complete:field?public java.lang.String system.Three.s nsfe", value);
        Assert.assertEquals(0, SystemClassReflectionRewriterTests.callcount);
        // Set the field
        Method m = SystemClassReflectionRewriterTests.class.getDeclaredMethod("helper2", Class.class, String.class);
        Assert.assertNotNull(m);
        clazz.getDeclaredField(jlcgf).set(null, m);
        // Now re-run, should be intercepted to call our helper
        value = run(clazz, "runIt");
        Assert.assertEquals("complete:field?null nsfe", value);
        // Check the correct amount of rewriting went on
        Assert.assertTrue((((rr.bits) & (JLC_GETFIELD)) != 0));
        Assert.assertTrue((((rr.bits) & (~(JLC_GETFIELD))) == 0));
        Assert.assertEquals(2, SystemClassReflectionRewriterTests.callcount);
        Assert.assertEquals(2, SystemClassReflectionRewriterTests.events.size());
        Assert.assertEquals("helper2(system.Three,s)", SystemClassReflectionRewriterTests.events.get(0));
        Assert.assertEquals("helper2(system.Three,foo)", SystemClassReflectionRewriterTests.events.get(1));
        Assert.assertEquals("Class.getField()", rr.summarize());
    }

    @Test
    public void jlClass_getDeclaredMethods() throws Exception {
        byte[] classbytes = loadBytesForClass("system.Four");
        RewriteResult rr = SystemClassReflectionRewriter.rewrite("system.Four", classbytes);
        byte[] newbytes = rr.bytes;
        Class<?> clazz = loadit("system.Four", newbytes);
        // Check the new field and method are in the type:
        // @formatter:off
        Assert.assertEquals(("CLASS: system/Four v50 0x0021(public synchronized) super java/lang/Object\n" + (((((("SOURCE: Four.java null\n" + "FIELD 0x0009(public static) __sljlcgdms Ljava/lang/reflect/Method;\n") + "METHOD: 0x0001(public) <init>()V\n") + "METHOD: 0x0001(public) runIt()Ljava/lang/String;\n") + "METHOD: 0x0001(public) ms()[Ljava/lang/reflect/Method;\n") + "METHOD: 0x000a(private static) __sljlcgdms(Ljava/lang/Class;)[Ljava/lang/reflect/Method;\n") + "\n")), toStringClass(newbytes));
        // @formatter:on
        Object value = run(clazz, "runIt");
        // Check that without the field initialized, things behave as expected
        Assert.assertEquals("complete:methods:null?false methods:size=3", value);
        Assert.assertEquals(0, SystemClassReflectionRewriterTests.callcount);
        // Set the field
        Method m = SystemClassReflectionRewriterTests.class.getDeclaredMethod("helper3", Class.class);
        Assert.assertNotNull(m);
        clazz.getDeclaredField(jlcgdms).set(null, m);
        // Now re-run, should be intercepted to call our helper
        value = run(clazz, "runIt");
        Assert.assertEquals("complete:methods:null?true", value);
        // Check the correct amount of rewriting went on
        Assert.assertTrue((((rr.bits) & (JLC_GETDECLAREDMETHODS)) != 0));
        Assert.assertTrue((((rr.bits) & (~(JLC_GETDECLAREDMETHODS))) == 0));
        Assert.assertEquals(1, SystemClassReflectionRewriterTests.callcount);
        Assert.assertEquals("Class.getDeclaredMethods()", rr.summarize());
    }

    @Test
    public void jlClass_getDeclaredConstructors() throws Exception {
        byte[] classbytes = loadBytesForClass("system.Ten");
        RewriteResult rr = SystemClassReflectionRewriter.rewrite("system.Ten", classbytes);
        byte[] newbytes = rr.bytes;
        Class<?> clazz = loadit("system.Ten", newbytes);
        // Check the new field and method are in the type:
        // @formatter:off
        Assert.assertEquals(("CLASS: system/Ten v50 0x0021(public synchronized) super java/lang/Object\n" + (((((("SOURCE: Ten.java null\n" + "FIELD 0x0009(public static) __sljlcgdcs Ljava/lang/reflect/Method;\n") + "METHOD: 0x0001(public) <init>()V\n") + "METHOD: 0x0001(public) runIt()Ljava/lang/String;\n") + "METHOD: 0x0001(public) cs()[Ljava/lang/reflect/Constructor;\n") + "METHOD: 0x000a(private static) __sljlcgdcs(Ljava/lang/Class;)[Ljava/lang/reflect/Constructor;\n") + "\n")), toStringClass(newbytes));
        // @formatter:on
        Object value = run(clazz, "runIt");
        // Check that without the field initialized, things behave as expected
        Assert.assertEquals("complete:constructors:null?false constructors:size=1", value);
        Assert.assertEquals(0, SystemClassReflectionRewriterTests.callcount);
        // Set the field
        Method m = SystemClassReflectionRewriterTests.class.getDeclaredMethod("helper4", Class.class);
        Assert.assertNotNull(m);
        clazz.getDeclaredField(jlcGetDeclaredConstructorsMember).set(null, m);
        // Now re-run, should be intercepted to call our helper
        value = run(clazz, "runIt");
        Assert.assertEquals("complete:constructors:null?true", value);
        // Check the correct amount of rewriting went on
        Assert.assertTrue((((rr.bits) & (JLC_GETDECLAREDCONSTRUCTORS)) != 0));
        Assert.assertTrue((((rr.bits) & (~(JLC_GETDECLAREDCONSTRUCTORS))) == 0));
        Assert.assertEquals(1, SystemClassReflectionRewriterTests.callcount);
        Assert.assertEquals("Class.getDeclaredConstructors()", rr.summarize());
    }

    @Test
    public void jlrMethod_Invoke() throws Exception {
        byte[] classbytes = loadBytesForClass("system.Eleven");
        RewriteResult rr = SystemClassReflectionRewriter.rewrite("system.Eleven", classbytes);
        byte[] newbytes = rr.bytes;
        Class<?> clazz = loadit("system.Eleven", newbytes);
        // Check the new field and method are in the type:
        // @formatter:off
        Assert.assertEquals(("CLASS: system/Eleven v50 0x0021(public synchronized) super java/lang/Object\n" + ((((((((("SOURCE: Eleven.java null\n" + "FIELD 0x0009(public static) __sljlcgdm Ljava/lang/reflect/Method;\n") + "FIELD 0x0009(public static) __sljlrmi Ljava/lang/reflect/Method;\n") + "METHOD: 0x0001(public) <init>()V\n") + "METHOD: 0x0001(public) runIt()Ljava/lang/String; java/lang/Exception\n") + "METHOD: 0x0089(public static) invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object; java/lang/Exception\n") + "METHOD: 0x0001(public) foo(ILjava/lang/String;)Ljava/lang/String;\n") + "METHOD: 0x008a(private static) __sljlcgdm(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method; java/lang/NoSuchMethodException\n") + "METHOD: 0x000a(private static) __sljlrmi(Ljava/lang/reflect/Method;Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object; java/lang/IllegalAccessException java/lang/reflect/InvocationTargetException\n") + "\n")), toStringClass(newbytes));
        // @formatter:on
        Object value = run(clazz, "runIt");
        // Check that without the field initialized, things behave as expected
        Assert.assertEquals("complete:obj=i=12:s=abc", value);
        Assert.assertEquals(0, SystemClassReflectionRewriterTests.callcount);
        // Set the field
        Method m = SystemClassReflectionRewriterTests.class.getDeclaredMethod("helperJLRMI_0", Class.class, String.class, Class[].class);
        Assert.assertNotNull(m);
        clazz.getDeclaredField(jlcgdm).set(null, m);
        m = SystemClassReflectionRewriterTests.class.getDeclaredMethod("helperJLRMI", Method.class, Object.class, Object[].class);
        Assert.assertNotNull(m);
        clazz.getDeclaredField(jlrmInvokeMember).set(null, m);
        // Now re-run, should be intercepted to call our helper
        value = run(clazz, "runIt");
        Assert.assertEquals("complete:obj=null", value);
        // Check the correct amount of rewriting went on
        Assert.assertTrue((((rr.bits) & ((JLC_GETDECLAREDMETHOD) | (JLRM_INVOKE))) != 0));
        Assert.assertTrue((((rr.bits) & (~((JLC_GETDECLAREDMETHOD) | (JLRM_INVOKE)))) == 0));
        Assert.assertEquals(1, SystemClassReflectionRewriterTests.callcount);
        Assert.assertEquals("Class.getDeclaredMethod() Method.invoke()", rr.summarize());
    }

    @Test
    public void jlClass_getDeclaredMethod() throws Exception {
        byte[] classbytes = loadBytesForClass("system.Five");
        RewriteResult rr = SystemClassReflectionRewriter.rewrite("system.Five", classbytes);
        byte[] newbytes = rr.bytes;
        Class<?> clazz = loadit("system.Five", newbytes);
        // Check the new field and method are in the type:
        // @formatter:off
        Assert.assertEquals(("CLASS: system/Five v50 0x0021(public synchronized) super java/lang/Object\n" + ((((((("SOURCE: Five.java null\n" + "FIELD 0x0000() s Ljava/lang/String;\n") + "FIELD 0x0009(public static) __sljlcgdm Ljava/lang/reflect/Method;\n") + "METHOD: 0x0001(public) <init>()V\n") + "METHOD: 0x0001(public) runIt()Ljava/lang/String; java/lang/Exception\n") + "METHOD: 0x0001(public) m(Ljava/lang/String;)Ljava/lang/reflect/Method; java/lang/NoSuchMethodException\n") + "METHOD: 0x008a(private static) __sljlcgdm(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method; java/lang/NoSuchMethodException\n") + "\n")), toStringClass(newbytes));
        // @formatter:on
        Object value = run(clazz, "runIt");
        // Check that without the field initialized, things behave as expected
        Assert.assertEquals("complete:method?public java.lang.String system.Five.runIt() throws java.lang.Exception nsme", value);
        Assert.assertEquals(0, SystemClassReflectionRewriterTests.callcount);
        // Set the field
        Method m = SystemClassReflectionRewriterTests.class.getDeclaredMethod("helper4", Class.class, String.class, Class[].class);
        Assert.assertNotNull(m);
        clazz.getDeclaredField(jlcgdm).set(null, m);
        // Now re-run, should be intercepted to call our helper
        value = run(clazz, "runIt");
        Assert.assertEquals("complete:method?null nsme", value);
        // Check the correct amount of rewriting went on
        Assert.assertTrue((((rr.bits) & (JLC_GETDECLAREDMETHOD)) != 0));
        Assert.assertTrue((((rr.bits) & (~(JLC_GETDECLAREDMETHOD))) == 0));
        Assert.assertEquals(2, SystemClassReflectionRewriterTests.callcount);
        Assert.assertEquals(2, SystemClassReflectionRewriterTests.events.size());
        Assert.assertEquals("helper4(system.Five,runIt)", SystemClassReflectionRewriterTests.events.get(0));
        Assert.assertEquals("helper4(system.Five,foobar)", SystemClassReflectionRewriterTests.events.get(1));
        Assert.assertEquals("Class.getDeclaredMethod()", rr.summarize());
    }

    @Test
    public void jlClass_getMethod() throws Exception {
        byte[] classbytes = loadBytesForClass("system.Six");
        RewriteResult rr = SystemClassReflectionRewriter.rewrite("system.Six", classbytes);
        byte[] newbytes = rr.bytes;
        Class<?> clazz = loadit("system.Six", newbytes);
        // Check the new field and method are in the type:
        // @formatter:off
        Assert.assertEquals(("CLASS: system/Six v50 0x0021(public synchronized) super java/lang/Object\n" + ((((((("SOURCE: Six.java null\n" + "FIELD 0x0001(public) s Ljava/lang/String;\n") + "FIELD 0x0009(public static) __sljlcgm Ljava/lang/reflect/Method;\n") + "METHOD: 0x0001(public) <init>()V\n") + "METHOD: 0x0001(public) runIt()Ljava/lang/String; java/lang/Exception\n") + "METHOD: 0x0001(public) m(Ljava/lang/String;)Ljava/lang/reflect/Method; java/lang/NoSuchMethodException\n") + "METHOD: 0x008a(private static) __sljlcgm(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method; java/lang/NoSuchMethodException\n") + "\n")), toStringClass(newbytes));
        // @formatter:on
        Object value = run(clazz, "runIt");
        // Check that without the field initialized, things behave as expected
        Assert.assertEquals("complete:method?public java.lang.String system.Six.runIt() throws java.lang.Exception nsme", value);
        Assert.assertEquals(0, SystemClassReflectionRewriterTests.callcount);
        // Set the field
        Method m = SystemClassReflectionRewriterTests.class.getDeclaredMethod("helper4", Class.class, String.class, Class[].class);
        Assert.assertNotNull(m);
        clazz.getDeclaredField(jlcgm).set(null, m);
        // Now re-run, should be intercepted to call our helper
        value = run(clazz, "runIt");
        Assert.assertEquals("complete:method?null unexpectedly_didn't_fail", value);
        // Check the correct amount of rewriting went on
        Assert.assertTrue((((rr.bits) & (JLC_GETMETHOD)) != 0));
        Assert.assertTrue((((rr.bits) & (~(JLC_GETMETHOD))) == 0));
        Assert.assertEquals(2, SystemClassReflectionRewriterTests.callcount);
        Assert.assertEquals(2, SystemClassReflectionRewriterTests.events.size());
        Assert.assertEquals("helper4(system.Six,runIt)", SystemClassReflectionRewriterTests.events.get(0));
        Assert.assertEquals("helper4(system.Six,foo)", SystemClassReflectionRewriterTests.events.get(1));
        Assert.assertEquals("Class.getMethod()", rr.summarize());
    }

    @Test
    public void jlClass_getDeclaredConstructor() throws Exception {
        byte[] classbytes = loadBytesForClass("system.Seven");
        RewriteResult rr = SystemClassReflectionRewriter.rewrite("system.Seven", classbytes);
        byte[] newbytes = rr.bytes;
        Class<?> clazz = loadit("system.Seven", newbytes);
        // Check the new field and method are in the type:
        // @formatter:off
        Assert.assertEquals(("CLASS: system/Seven v50 0x0021(public synchronized) super java/lang/Object\n" + ((((((("SOURCE: Seven.java null\n" + "FIELD 0x0009(public static) __sljlcgdc Ljava/lang/reflect/Method;\n") + "METHOD: 0x0001(public) <init>()V\n") + "METHOD: 0x0001(public) <init>(Ljava/lang/String;)V\n") + "METHOD: 0x0001(public) runIt()Ljava/lang/String; java/lang/Exception\n") + "METHOD: 0x0081(public) m([Ljava/lang/Class;)Ljava/lang/reflect/Constructor; java/lang/NoSuchMethodException\n") + "METHOD: 0x008a(private static) __sljlcgdc(Ljava/lang/Class;[Ljava/lang/Class;)Ljava/lang/reflect/Constructor; java/lang/NoSuchMethodException\n") + "\n")), toStringClass(newbytes));
        // @formatter:on
        Object value = run(clazz, "runIt");
        // Check that without the field initialized, things behave as expected
        Assert.assertEquals("complete:defaultctor?public system.Seven() stringctor?public system.Seven(java.lang.String) nsme", value);
        Assert.assertEquals(0, SystemClassReflectionRewriterTests.callcount);
        // Set the field
        Method m = SystemClassReflectionRewriterTests.class.getDeclaredMethod("helper5", Class.class, Class[].class);
        Assert.assertNotNull(m);
        clazz.getDeclaredField(jlcgdc).set(null, m);
        // Now re-run, should be intercepted to call our helper
        value = run(clazz, "runIt");
        Assert.assertEquals("complete:defaultctor?null stringctor?null nsme", value);
        // Check the correct amount of rewriting went on
        Assert.assertTrue((((rr.bits) & (JLC_GETDECLAREDCONSTRUCTOR)) != 0));
        Assert.assertTrue((((rr.bits) & (~(JLC_GETDECLAREDCONSTRUCTOR))) == 0));
        Assert.assertEquals(3, SystemClassReflectionRewriterTests.callcount);
        Assert.assertEquals(3, SystemClassReflectionRewriterTests.events.size());
        Assert.assertEquals("helper5(system.Seven)", SystemClassReflectionRewriterTests.events.get(0));
        Assert.assertEquals("helper5(system.Seven)", SystemClassReflectionRewriterTests.events.get(1));
        Assert.assertEquals("helper5(system.Seven)", SystemClassReflectionRewriterTests.events.get(2));
        Assert.assertEquals("Class.getDeclaredConstructor()", rr.summarize());
    }

    @Test
    public void jlrField_getLong() throws Exception {
        byte[] classbytes = loadBytesForClass("system.Thirteen");
        RewriteResult rr = SystemClassReflectionRewriter.rewrite("system.Thirteen", classbytes);
        byte[] newbytes = rr.bytes;
        Class<?> clazz = loadit("system.Thirteen", newbytes);
        // Check the new field and method are in the type:
        // @formatter:off
        Assert.assertEquals(("CLASS: system/Thirteen v50 0x0021(public synchronized) super java/lang/Object\n" + ((((((((("SOURCE: Thirteen.java null\n" + "FIELD 0x0001(public) foo J\n") + "FIELD 0x0009(public static) __sljlcgf Ljava/lang/reflect/Method;\n") + "FIELD 0x0009(public static) __sljlrfgl Ljava/lang/reflect/Method;\n") + "METHOD: 0x0001(public) <init>()V\n") + "METHOD: 0x0001(public) runIt()Ljava/lang/String; java/lang/Exception\n") + "METHOD: 0x0001(public) gf()Ljava/lang/Long; java/lang/Exception\n") + "METHOD: 0x000a(private static) __sljlcgf(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/reflect/Field; java/lang/NoSuchFieldException\n") + "METHOD: 0x000a(private static) __sljlrfgl(Ljava/lang/reflect/Field;Ljava/lang/Object;)J java/lang/IllegalAccessException java/lang/IllegalArgumentException\n") + "\n")), toStringClass(newbytes));
        // @formatter:on
        Object value = run(clazz, "runIt");
        // Check that without the field initialized, things behave as expected
        Assert.assertEquals("complete:value=42", value);
        Assert.assertEquals(0, SystemClassReflectionRewriterTests.callcount);
        // Set the field
        Method m = SystemClassReflectionRewriterTests.class.getDeclaredMethod("helperJLRFGL_0", Class.class, String.class);
        Assert.assertNotNull(m);
        clazz.getDeclaredField(jlcgf).set(null, m);
        m = SystemClassReflectionRewriterTests.class.getDeclaredMethod("helperJLRFGL", Field.class, Object.class);
        Assert.assertNotNull(m);
        clazz.getDeclaredField(jlrfGetLongMember).set(null, m);
        // Now re-run, should be intercepted to call our helper
        value = run(clazz, "runIt");
        Assert.assertEquals("complete:value=0", value);
        // Check the correct amount of rewriting went on
        Assert.assertTrue((((rr.bits) & ((JLRF_GETLONG) | (JLC_GETFIELD))) != 0));
        Assert.assertTrue((((rr.bits) & (~((JLRF_GETLONG) | (JLC_GETFIELD)))) == 0));
        Assert.assertEquals(1, SystemClassReflectionRewriterTests.callcount);
        Assert.assertEquals("Class.getField() Field.getLong()", rr.summarize());
    }

    @Test
    public void jlrField_get() throws Exception {
        byte[] classbytes = loadBytesForClass("system.Twelve");
        RewriteResult rr = SystemClassReflectionRewriter.rewrite("system.Twelve", classbytes);
        byte[] newbytes = rr.bytes;
        Class<?> clazz = loadit("system.Twelve", newbytes);
        // Check the new field and method are in the type:
        // @formatter:off
        Assert.assertEquals(("CLASS: system/Twelve v50 0x0021(public synchronized) super java/lang/Object\n" + ((((((((("SOURCE: Twelve.java null\n" + "FIELD 0x0001(public) foo Ljava/lang/String;\n") + "FIELD 0x0009(public static) __sljlcgf Ljava/lang/reflect/Method;\n") + "FIELD 0x0009(public static) __sljlrfg Ljava/lang/reflect/Method;\n") + "METHOD: 0x0001(public) <init>()V\n") + "METHOD: 0x0001(public) runIt()Ljava/lang/String; java/lang/Exception\n") + "METHOD: 0x0001(public) gf()Ljava/lang/Object; java/lang/Exception\n") + "METHOD: 0x000a(private static) __sljlcgf(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/reflect/Field; java/lang/NoSuchFieldException\n") + "METHOD: 0x000a(private static) __sljlrfg(Ljava/lang/reflect/Field;Ljava/lang/Object;)Ljava/lang/Object; java/lang/IllegalAccessException java/lang/IllegalArgumentException\n") + "\n")), toStringClass(newbytes));
        // @formatter:on
        Object value = run(clazz, "runIt");
        // Check that without the field initialized, things behave as expected
        Assert.assertEquals("complete:value=abc", value);
        Assert.assertEquals(0, SystemClassReflectionRewriterTests.callcount);
        // Set the field
        Method m = SystemClassReflectionRewriterTests.class.getDeclaredMethod("helperJLRFG_0", Class.class, String.class);
        Assert.assertNotNull(m);
        clazz.getDeclaredField(jlcgf).set(null, m);
        m = SystemClassReflectionRewriterTests.class.getDeclaredMethod("helperJLRFG", Field.class, Object.class);
        Assert.assertNotNull(m);
        clazz.getDeclaredField(jlrfGetMember).set(null, m);
        // Now re-run, should be intercepted to call our helper
        value = run(clazz, "runIt");
        Assert.assertEquals("complete:value=null", value);
        // Check the correct amount of rewriting went on
        Assert.assertTrue((((rr.bits) & ((JLRF_GET) | (JLC_GETFIELD))) != 0));
        Assert.assertTrue((((rr.bits) & (~((JLRF_GET) | (JLC_GETFIELD)))) == 0));
        Assert.assertEquals(1, SystemClassReflectionRewriterTests.callcount);
        Assert.assertEquals("Class.getField() Field.get()", rr.summarize());
    }

    @Test
    public void jlClass_getModifiers() throws Exception {
        byte[] classbytes = loadBytesForClass("system.Eight");
        RewriteResult rr = SystemClassReflectionRewriter.rewrite("system.Eight", classbytes);
        byte[] newbytes = rr.bytes;
        Class<?> clazz = loadit("system.Eight", newbytes);
        // Check the new field and method are in the type:
        // @formatter:off
        Assert.assertEquals(("CLASS: system/Eight v50 0x0021(public synchronized) super java/lang/Object\n" + (((((((("SOURCE: Eight.java null\n" + "INNERCLASS: system/Eight$Inner system/Eight Inner 2\n") + "FIELD 0x0009(public static) __sljlcgmods Ljava/lang/reflect/Method;\n") + "METHOD: 0x0001(public) <init>()V\n") + "METHOD: 0x0001(public) <init>(Ljava/lang/String;)V\n") + "METHOD: 0x0001(public) runIt()Ljava/lang/String; java/lang/Exception\n") + "METHOD: 0x0001(public) m(Ljava/lang/Class;)I\n") + "METHOD: 0x000a(private static) __sljlcgmods(Ljava/lang/Class;)I\n") + "\n")), toStringClass(newbytes));
        // @formatter:on
        Object value = run(clazz, "runIt");
        // Check that without the field initialized, things behave as expected
        Assert.assertEquals("complete:mods?1 mods?0 mods?2", value);
        Assert.assertEquals(0, SystemClassReflectionRewriterTests.callcount);
        // Set the field
        Method m = SystemClassReflectionRewriterTests.class.getDeclaredMethod("helper6", Class.class);
        // m = ReflectiveInterceptor.class.getDeclaredMethod("jlClassGetDeclaredField", Class.class, String.class);
        Assert.assertNotNull(m);
        clazz.getDeclaredField(jlcgmods).set(null, m);
        // Now re-run, should be intercepted to call our helper
        value = run(clazz, "runIt");
        Assert.assertEquals("complete:mods?1 mods?0 mods?2", value);
        // Check the correct amount of rewriting went on
        Assert.assertTrue((((rr.bits) & (JLC_GETMODIFIERS)) != 0));
        Assert.assertTrue((((rr.bits) & (~(JLC_GETMODIFIERS))) == 0));
        Assert.assertEquals(3, SystemClassReflectionRewriterTests.callcount);
        Assert.assertEquals(3, SystemClassReflectionRewriterTests.events.size());
        Assert.assertEquals("helper6(system.Eight)", SystemClassReflectionRewriterTests.events.get(0));
        Assert.assertEquals("helper6(system.DefaultVis)", SystemClassReflectionRewriterTests.events.get(1));
        Assert.assertEquals("helper6(system.Eight$Inner)", SystemClassReflectionRewriterTests.events.get(2));
        Assert.assertEquals("Class.getModifiers()", rr.summarize());
    }

    @Test
    public void jlClass_getMethods() throws Exception {
        byte[] classbytes = loadBytesForClass("system.Nine");
        RewriteResult rr = SystemClassReflectionRewriter.rewrite("system.Nine", classbytes);
        byte[] newbytes = rr.bytes;
        Class<?> clazz = loadit("system.Nine", newbytes);
        // Check the new field and method are in the type:
        // @formatter:off
        Assert.assertEquals(("CLASS: system/Nine v50 0x0021(public synchronized) super java/lang/Object\n" + (((((("SOURCE: Nine.java null\n" + "FIELD 0x0009(public static) __sljlcgms Ljava/lang/reflect/Method;\n") + "METHOD: 0x0001(public) <init>()V\n") + "METHOD: 0x0001(public) runIt()Ljava/lang/String;\n") + "METHOD: 0x0001(public) ms()[Ljava/lang/reflect/Method;\n") + "METHOD: 0x000a(private static) __sljlcgms(Ljava/lang/Class;)[Ljava/lang/reflect/Method;\n") + "\n")), toStringClass(newbytes));
        // @formatter:on
        Object value = run(clazz, "runIt");
        // Check that without the field initialized, things behave as expected
        Assert.assertEquals("complete:methods:null?false methods:size=11", value);
        Assert.assertEquals(0, SystemClassReflectionRewriterTests.callcount);
        // Set the field
        Method m = SystemClassReflectionRewriterTests.class.getDeclaredMethod("helperGMs", Class.class);
        Assert.assertNotNull(m);
        clazz.getDeclaredField(jlcgms).set(null, m);
        m = SystemClassReflectionRewriterTests.class.getDeclaredMethod("helperGMs", Class.class);
        Assert.assertNotNull(m);
        clazz.getDeclaredField(jlcgms).set(null, m);
        // Now re-run, should be intercepted to call our helper
        value = run(clazz, "runIt");
        Assert.assertEquals("complete:methods:null?true", value);
        // Check the correct amount of rewriting went on
        Assert.assertTrue((((rr.bits) & (JLC_GETMETHODS)) != 0));
        Assert.assertTrue((((rr.bits) & (~(JLC_GETMETHODS))) == 0));
        Assert.assertEquals(1, SystemClassReflectionRewriterTests.callcount);
        Assert.assertEquals("Class.getMethods()", rr.summarize());
    }

    // ---
    static int callcount;

    static List<String> events = new ArrayList<String>();

    public String string = "wibble";

    public long lll = 99L;
}

