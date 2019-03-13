/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.common.extension;


import Constants.GROUP_KEY;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.activate.ActivateExt1;
import org.apache.dubbo.common.extension.activate.impl.ActivateExt1Impl1;
import org.apache.dubbo.common.extension.activate.impl.GroupActivateExtImpl;
import org.apache.dubbo.common.extension.activate.impl.OldActivateExt1Impl2;
import org.apache.dubbo.common.extension.activate.impl.OldActivateExt1Impl3;
import org.apache.dubbo.common.extension.activate.impl.OrderActivateExtImpl1;
import org.apache.dubbo.common.extension.activate.impl.OrderActivateExtImpl2;
import org.apache.dubbo.common.extension.activate.impl.ValueActivateExtImpl;
import org.apache.dubbo.common.extension.ext1.SimpleExt;
import org.apache.dubbo.common.extension.ext1.impl.SimpleExtImpl1;
import org.apache.dubbo.common.extension.ext1.impl.SimpleExtImpl2;
import org.apache.dubbo.common.extension.ext2.Ext2;
import org.apache.dubbo.common.extension.ext6_wrap.WrappedExt;
import org.apache.dubbo.common.extension.ext6_wrap.impl.Ext5Wrapper1;
import org.apache.dubbo.common.extension.ext6_wrap.impl.Ext5Wrapper2;
import org.apache.dubbo.common.extension.ext7.InitErrorExt;
import org.apache.dubbo.common.extension.ext8_add.AddExt1;
import org.apache.dubbo.common.extension.ext8_add.AddExt2;
import org.apache.dubbo.common.extension.ext8_add.AddExt3;
import org.apache.dubbo.common.extension.ext8_add.AddExt4;
import org.apache.dubbo.common.extension.ext8_add.impl.AddExt1Impl1;
import org.apache.dubbo.common.extension.ext8_add.impl.AddExt1_ManualAdaptive;
import org.apache.dubbo.common.extension.ext8_add.impl.AddExt1_ManualAdd1;
import org.apache.dubbo.common.extension.ext8_add.impl.AddExt1_ManualAdd2;
import org.apache.dubbo.common.extension.ext8_add.impl.AddExt2_ManualAdaptive;
import org.apache.dubbo.common.extension.ext8_add.impl.AddExt3_ManualAdaptive;
import org.apache.dubbo.common.extension.ext8_add.impl.AddExt4_ManualAdaptive;
import org.apache.dubbo.common.extension.ext9_empty.Ext9Empty;
import org.apache.dubbo.common.extension.ext9_empty.impl.Ext9EmptyImpl;
import org.apache.dubbo.common.extension.injection.InjectExt;
import org.apache.dubbo.common.extension.injection.impl.InjectExtImpl;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ExtensionLoaderTest {
    @Test
    public void test_getExtensionLoader_Null() throws Exception {
        try {
            ExtensionLoader.getExtensionLoader(null);
            Assertions.fail();
        } catch (IllegalArgumentException expected) {
            MatcherAssert.assertThat(expected.getMessage(), Matchers.containsString("Extension type == null"));
        }
    }

    @Test
    public void test_getExtensionLoader_NotInterface() throws Exception {
        try {
            ExtensionLoader.getExtensionLoader(ExtensionLoaderTest.class);
            Assertions.fail();
        } catch (IllegalArgumentException expected) {
            MatcherAssert.assertThat(expected.getMessage(), Matchers.containsString("Extension type (class org.apache.dubbo.common.extension.ExtensionLoaderTest) is not an interface"));
        }
    }

    @Test
    public void test_getExtensionLoader_NotSpiAnnotation() throws Exception {
        try {
            ExtensionLoader.getExtensionLoader(NoSpiExt.class);
            Assertions.fail();
        } catch (IllegalArgumentException expected) {
            MatcherAssert.assertThat(expected.getMessage(), CoreMatchers.allOf(Matchers.containsString("org.apache.dubbo.common.extension.NoSpiExt"), Matchers.containsString("is not an extension"), Matchers.containsString("NOT annotated with @SPI")));
        }
    }

    @Test
    public void test_getDefaultExtension() throws Exception {
        SimpleExt ext = ExtensionLoader.getExtensionLoader(SimpleExt.class).getDefaultExtension();
        MatcherAssert.assertThat(ext, CoreMatchers.instanceOf(SimpleExtImpl1.class));
        String name = ExtensionLoader.getExtensionLoader(SimpleExt.class).getDefaultExtensionName();
        Assertions.assertEquals("impl1", name);
    }

    @Test
    public void test_getDefaultExtension_NULL() throws Exception {
        Ext2 ext = ExtensionLoader.getExtensionLoader(Ext2.class).getDefaultExtension();
        Assertions.assertNull(ext);
        String name = ExtensionLoader.getExtensionLoader(Ext2.class).getDefaultExtensionName();
        Assertions.assertNull(name);
    }

    @Test
    public void test_getExtension() throws Exception {
        Assertions.assertTrue(((ExtensionLoader.getExtensionLoader(SimpleExt.class).getExtension("impl1")) instanceof SimpleExtImpl1));
        Assertions.assertTrue(((ExtensionLoader.getExtensionLoader(SimpleExt.class).getExtension("impl2")) instanceof SimpleExtImpl2));
    }

    @Test
    public void test_getExtension_WithWrapper() throws Exception {
        WrappedExt impl1 = ExtensionLoader.getExtensionLoader(WrappedExt.class).getExtension("impl1");
        MatcherAssert.assertThat(impl1, CoreMatchers.anyOf(CoreMatchers.instanceOf(Ext5Wrapper1.class), CoreMatchers.instanceOf(Ext5Wrapper2.class)));
        WrappedExt impl2 = ExtensionLoader.getExtensionLoader(WrappedExt.class).getExtension("impl2");
        MatcherAssert.assertThat(impl2, CoreMatchers.anyOf(CoreMatchers.instanceOf(Ext5Wrapper1.class), CoreMatchers.instanceOf(Ext5Wrapper2.class)));
        URL url = new URL("p1", "1.2.3.4", 1010, "path1");
        int echoCount1 = Ext5Wrapper1.echoCount.get();
        int echoCount2 = Ext5Wrapper2.echoCount.get();
        Assertions.assertEquals("Ext5Impl1-echo", impl1.echo(url, "ha"));
        Assertions.assertEquals((echoCount1 + 1), Ext5Wrapper1.echoCount.get());
        Assertions.assertEquals((echoCount2 + 1), Ext5Wrapper2.echoCount.get());
    }

    @Test
    public void test_getExtension_ExceptionNoExtension() throws Exception {
        try {
            ExtensionLoader.getExtensionLoader(SimpleExt.class).getExtension("XXX");
            Assertions.fail();
        } catch (IllegalStateException expected) {
            MatcherAssert.assertThat(expected.getMessage(), Matchers.containsString("No such extension org.apache.dubbo.common.extension.ext1.SimpleExt by name XXX"));
        }
    }

    @Test
    public void test_getExtension_ExceptionNoExtension_WrapperNotAffactName() throws Exception {
        try {
            ExtensionLoader.getExtensionLoader(WrappedExt.class).getExtension("XXX");
            Assertions.fail();
        } catch (IllegalStateException expected) {
            MatcherAssert.assertThat(expected.getMessage(), Matchers.containsString("No such extension org.apache.dubbo.common.extension.ext6_wrap.WrappedExt by name XXX"));
        }
    }

    @Test
    public void test_getExtension_ExceptionNullArg() throws Exception {
        try {
            ExtensionLoader.getExtensionLoader(SimpleExt.class).getExtension(null);
            Assertions.fail();
        } catch (IllegalArgumentException expected) {
            MatcherAssert.assertThat(expected.getMessage(), Matchers.containsString("Extension name == null"));
        }
    }

    @Test
    public void test_hasExtension() throws Exception {
        Assertions.assertTrue(ExtensionLoader.getExtensionLoader(SimpleExt.class).hasExtension("impl1"));
        Assertions.assertFalse(ExtensionLoader.getExtensionLoader(SimpleExt.class).hasExtension("impl1,impl2"));
        Assertions.assertFalse(ExtensionLoader.getExtensionLoader(SimpleExt.class).hasExtension("xxx"));
        try {
            ExtensionLoader.getExtensionLoader(SimpleExt.class).hasExtension(null);
            Assertions.fail();
        } catch (IllegalArgumentException expected) {
            MatcherAssert.assertThat(expected.getMessage(), Matchers.containsString("Extension name == null"));
        }
    }

    @Test
    public void test_hasExtension_wrapperIsNotExt() throws Exception {
        Assertions.assertTrue(ExtensionLoader.getExtensionLoader(WrappedExt.class).hasExtension("impl1"));
        Assertions.assertFalse(ExtensionLoader.getExtensionLoader(WrappedExt.class).hasExtension("impl1,impl2"));
        Assertions.assertFalse(ExtensionLoader.getExtensionLoader(WrappedExt.class).hasExtension("xxx"));
        Assertions.assertFalse(ExtensionLoader.getExtensionLoader(WrappedExt.class).hasExtension("wrapper1"));
        try {
            ExtensionLoader.getExtensionLoader(WrappedExt.class).hasExtension(null);
            Assertions.fail();
        } catch (IllegalArgumentException expected) {
            MatcherAssert.assertThat(expected.getMessage(), Matchers.containsString("Extension name == null"));
        }
    }

    @Test
    public void test_getSupportedExtensions() throws Exception {
        Set<String> exts = ExtensionLoader.getExtensionLoader(SimpleExt.class).getSupportedExtensions();
        Set<String> expected = new HashSet<String>();
        expected.add("impl1");
        expected.add("impl2");
        expected.add("impl3");
        Assertions.assertEquals(expected, exts);
    }

    @Test
    public void test_getSupportedExtensions_wrapperIsNotExt() throws Exception {
        Set<String> exts = ExtensionLoader.getExtensionLoader(WrappedExt.class).getSupportedExtensions();
        Set<String> expected = new HashSet<String>();
        expected.add("impl1");
        expected.add("impl2");
        Assertions.assertEquals(expected, exts);
    }

    @Test
    public void test_AddExtension() throws Exception {
        try {
            ExtensionLoader.getExtensionLoader(AddExt1.class).getExtension("Manual1");
            Assertions.fail();
        } catch (IllegalStateException expected) {
            MatcherAssert.assertThat(expected.getMessage(), Matchers.containsString("No such extension org.apache.dubbo.common.extension.ext8_add.AddExt1 by name Manual"));
        }
        ExtensionLoader.getExtensionLoader(AddExt1.class).addExtension("Manual1", AddExt1_ManualAdd1.class);
        AddExt1 ext = ExtensionLoader.getExtensionLoader(AddExt1.class).getExtension("Manual1");
        MatcherAssert.assertThat(ext, CoreMatchers.instanceOf(AddExt1_ManualAdd1.class));
        Assertions.assertEquals("Manual1", ExtensionLoader.getExtensionLoader(AddExt1.class).getExtensionName(AddExt1_ManualAdd1.class));
    }

    @Test
    public void test_AddExtension_NoExtend() throws Exception {
        // ExtensionLoader.getExtensionLoader(Ext9Empty.class).getSupportedExtensions();
        ExtensionLoader.getExtensionLoader(Ext9Empty.class).addExtension("ext9", Ext9EmptyImpl.class);
        Ext9Empty ext = ExtensionLoader.getExtensionLoader(Ext9Empty.class).getExtension("ext9");
        MatcherAssert.assertThat(ext, CoreMatchers.instanceOf(Ext9Empty.class));
        Assertions.assertEquals("ext9", ExtensionLoader.getExtensionLoader(Ext9Empty.class).getExtensionName(Ext9EmptyImpl.class));
    }

    @Test
    public void test_AddExtension_ExceptionWhenExistedExtension() throws Exception {
        SimpleExt ext = ExtensionLoader.getExtensionLoader(SimpleExt.class).getExtension("impl1");
        try {
            ExtensionLoader.getExtensionLoader(AddExt1.class).addExtension("impl1", AddExt1_ManualAdd1.class);
            Assertions.fail();
        } catch (IllegalStateException expected) {
            MatcherAssert.assertThat(expected.getMessage(), Matchers.containsString("Extension name impl1 already exists (Extension interface org.apache.dubbo.common.extension.ext8_add.AddExt1)!"));
        }
    }

    @Test
    public void test_AddExtension_Adaptive() throws Exception {
        ExtensionLoader<AddExt2> loader = ExtensionLoader.getExtensionLoader(AddExt2.class);
        loader.addExtension(null, AddExt2_ManualAdaptive.class);
        AddExt2 adaptive = loader.getAdaptiveExtension();
        Assertions.assertTrue((adaptive instanceof AddExt2_ManualAdaptive));
    }

    @Test
    public void test_AddExtension_Adaptive_ExceptionWhenExistedAdaptive() throws Exception {
        ExtensionLoader<AddExt1> loader = ExtensionLoader.getExtensionLoader(AddExt1.class);
        loader.getAdaptiveExtension();
        try {
            loader.addExtension(null, AddExt1_ManualAdaptive.class);
            Assertions.fail();
        } catch (IllegalStateException expected) {
            MatcherAssert.assertThat(expected.getMessage(), Matchers.containsString("Adaptive Extension already exists (Extension interface org.apache.dubbo.common.extension.ext8_add.AddExt1)!"));
        }
    }

    @Test
    public void test_replaceExtension() throws Exception {
        try {
            ExtensionLoader.getExtensionLoader(AddExt1.class).getExtension("Manual2");
            Assertions.fail();
        } catch (IllegalStateException expected) {
            MatcherAssert.assertThat(expected.getMessage(), Matchers.containsString("No such extension org.apache.dubbo.common.extension.ext8_add.AddExt1 by name Manual"));
        }
        {
            AddExt1 ext = ExtensionLoader.getExtensionLoader(AddExt1.class).getExtension("impl1");
            MatcherAssert.assertThat(ext, CoreMatchers.instanceOf(AddExt1Impl1.class));
            Assertions.assertEquals("impl1", ExtensionLoader.getExtensionLoader(AddExt1.class).getExtensionName(AddExt1Impl1.class));
        }
        {
            ExtensionLoader.getExtensionLoader(AddExt1.class).replaceExtension("impl1", AddExt1_ManualAdd2.class);
            AddExt1 ext = ExtensionLoader.getExtensionLoader(AddExt1.class).getExtension("impl1");
            MatcherAssert.assertThat(ext, CoreMatchers.instanceOf(AddExt1_ManualAdd2.class));
            Assertions.assertEquals("impl1", ExtensionLoader.getExtensionLoader(AddExt1.class).getExtensionName(AddExt1_ManualAdd2.class));
        }
    }

    @Test
    public void test_replaceExtension_Adaptive() throws Exception {
        ExtensionLoader<AddExt3> loader = ExtensionLoader.getExtensionLoader(AddExt3.class);
        AddExt3 adaptive = loader.getAdaptiveExtension();
        Assertions.assertFalse((adaptive instanceof AddExt3_ManualAdaptive));
        loader.replaceExtension(null, AddExt3_ManualAdaptive.class);
        adaptive = loader.getAdaptiveExtension();
        Assertions.assertTrue((adaptive instanceof AddExt3_ManualAdaptive));
    }

    @Test
    public void test_replaceExtension_ExceptionWhenNotExistedExtension() throws Exception {
        AddExt1 ext = ExtensionLoader.getExtensionLoader(AddExt1.class).getExtension("impl1");
        try {
            ExtensionLoader.getExtensionLoader(AddExt1.class).replaceExtension("NotExistedExtension", AddExt1_ManualAdd1.class);
            Assertions.fail();
        } catch (IllegalStateException expected) {
            MatcherAssert.assertThat(expected.getMessage(), Matchers.containsString("Extension name NotExistedExtension doesn't exist (Extension interface org.apache.dubbo.common.extension.ext8_add.AddExt1)"));
        }
    }

    @Test
    public void test_replaceExtension_Adaptive_ExceptionWhenNotExistedExtension() throws Exception {
        ExtensionLoader<AddExt4> loader = ExtensionLoader.getExtensionLoader(AddExt4.class);
        try {
            loader.replaceExtension(null, AddExt4_ManualAdaptive.class);
            Assertions.fail();
        } catch (IllegalStateException expected) {
            MatcherAssert.assertThat(expected.getMessage(), Matchers.containsString("Adaptive Extension doesn't exist (Extension interface org.apache.dubbo.common.extension.ext8_add.AddExt4)"));
        }
    }

    @Test
    public void test_InitError() throws Exception {
        ExtensionLoader<InitErrorExt> loader = ExtensionLoader.getExtensionLoader(InitErrorExt.class);
        loader.getExtension("ok");
        try {
            loader.getExtension("error");
            Assertions.fail();
        } catch (IllegalStateException expected) {
            MatcherAssert.assertThat(expected.getMessage(), Matchers.containsString("Failed to load extension class (interface: interface org.apache.dubbo.common.extension.ext7.InitErrorExt"));
            MatcherAssert.assertThat(expected.getCause(), CoreMatchers.instanceOf(ExceptionInInitializerError.class));
        }
    }

    @Test
    public void testLoadActivateExtension() throws Exception {
        // test default
        URL url = URL.valueOf("test://localhost/test");
        List<ActivateExt1> list = ExtensionLoader.getExtensionLoader(ActivateExt1.class).getActivateExtension(url, new String[]{  }, "default_group");
        Assertions.assertEquals(1, list.size());
        Assertions.assertTrue(((list.get(0).getClass()) == (ActivateExt1Impl1.class)));
        // test group
        url = url.addParameter(GROUP_KEY, "group1");
        list = ExtensionLoader.getExtensionLoader(ActivateExt1.class).getActivateExtension(url, new String[]{  }, "group1");
        Assertions.assertEquals(1, list.size());
        Assertions.assertTrue(((list.get(0).getClass()) == (GroupActivateExtImpl.class)));
        // test old @Activate group
        url = url.addParameter(GROUP_KEY, "old_group");
        list = ExtensionLoader.getExtensionLoader(ActivateExt1.class).getActivateExtension(url, new String[]{  }, "old_group");
        Assertions.assertEquals(2, list.size());
        Assertions.assertTrue((((list.get(0).getClass()) == (OldActivateExt1Impl2.class)) || ((list.get(0).getClass()) == (OldActivateExt1Impl3.class))));
        // test value
        url = url.removeParameter(GROUP_KEY);
        url = url.addParameter(GROUP_KEY, "value");
        url = url.addParameter("value", "value");
        list = ExtensionLoader.getExtensionLoader(ActivateExt1.class).getActivateExtension(url, new String[]{  }, "value");
        Assertions.assertEquals(1, list.size());
        Assertions.assertTrue(((list.get(0).getClass()) == (ValueActivateExtImpl.class)));
        // test order
        url = URL.valueOf("test://localhost/test");
        url = url.addParameter(GROUP_KEY, "order");
        list = ExtensionLoader.getExtensionLoader(ActivateExt1.class).getActivateExtension(url, new String[]{  }, "order");
        Assertions.assertEquals(2, list.size());
        Assertions.assertTrue(((list.get(0).getClass()) == (OrderActivateExtImpl1.class)));
        Assertions.assertTrue(((list.get(1).getClass()) == (OrderActivateExtImpl2.class)));
    }

    @Test
    public void testLoadDefaultActivateExtension() throws Exception {
        // test default
        URL url = URL.valueOf("test://localhost/test?ext=order1,default");
        List<ActivateExt1> list = ExtensionLoader.getExtensionLoader(ActivateExt1.class).getActivateExtension(url, "ext", "default_group");
        Assertions.assertEquals(2, list.size());
        Assertions.assertTrue(((list.get(0).getClass()) == (OrderActivateExtImpl1.class)));
        Assertions.assertTrue(((list.get(1).getClass()) == (ActivateExt1Impl1.class)));
        url = URL.valueOf("test://localhost/test?ext=default,order1");
        list = ExtensionLoader.getExtensionLoader(ActivateExt1.class).getActivateExtension(url, "ext", "default_group");
        Assertions.assertEquals(2, list.size());
        Assertions.assertTrue(((list.get(0).getClass()) == (ActivateExt1Impl1.class)));
        Assertions.assertTrue(((list.get(1).getClass()) == (OrderActivateExtImpl1.class)));
    }

    @Test
    public void testInjectExtension() {
        // test default
        InjectExt injectExt = ExtensionLoader.getExtensionLoader(InjectExt.class).getExtension("injection");
        InjectExtImpl injectExtImpl = ((InjectExtImpl) (injectExt));
        Assertions.assertNotNull(injectExtImpl.getSimpleExt());
        Assertions.assertNull(injectExtImpl.getSimpleExt1());
        Assertions.assertNull(injectExtImpl.getGenericType());
    }
}

