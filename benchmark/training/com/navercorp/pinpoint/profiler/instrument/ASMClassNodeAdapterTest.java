/**
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.profiler.instrument;


import com.navercorp.pinpoint.bootstrap.instrument.InstrumentContext;
import com.navercorp.pinpoint.bootstrap.instrument.aspect.Aspect;
import com.navercorp.pinpoint.profiler.util.JavaAssistUtils;
import java.lang.reflect.Method;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;


public class ASMClassNodeAdapterTest {
    private final InstrumentContext pluginContext = Mockito.mock(InstrumentContext.class);

    @Test
    public void get() {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ASMClassNodeAdapter adapter = ASMClassNodeAdapter.get(pluginContext, classLoader, getClass().getProtectionDomain(), "com/navercorp/pinpoint/profiler/instrument/mock/BaseClass");
        Assert.assertNotNull(adapter);
        adapter = ASMClassNodeAdapter.get(pluginContext, classLoader, getClass().getProtectionDomain(), "com/navercorp/pinpoint/profiler/instrument/mock/NotExistClass");
        Assert.assertNull(adapter);
        // skip code
        adapter = ASMClassNodeAdapter.get(pluginContext, classLoader, getClass().getProtectionDomain(), "com/navercorp/pinpoint/profiler/instrument/mock/BaseClass", true);
        try {
            adapter.getDeclaredMethods();
            Assert.fail("can't throw IllegalStateException");
        } catch (Exception ignored) {
        }
        try {
            adapter.getDeclaredMethod("base", "()");
            Assert.fail("can't throw IllegalStateException");
        } catch (Exception ignored) {
        }
    }

    @Test
    public void getter() {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ASMClassNodeAdapter adapter = ASMClassNodeAdapter.get(pluginContext, classLoader, getClass().getProtectionDomain(), "com/navercorp/pinpoint/profiler/instrument/mock/ExtendedClass");
        // name
        Assert.assertEquals("com/navercorp/pinpoint/profiler/instrument/mock/ExtendedClass", adapter.getInternalName());
        Assert.assertEquals("com.navercorp.pinpoint.profiler.instrument.mock.ExtendedClass", adapter.getName());
        Assert.assertEquals("com/navercorp/pinpoint/profiler/instrument/mock/BaseClass", adapter.getSuperClassInternalName());
        Assert.assertEquals("com.navercorp.pinpoint.profiler.instrument.mock.BaseClass", adapter.getSuperClassName());
        Assert.assertEquals(false, adapter.isInterface());
        Assert.assertEquals(false, adapter.isAnnotation());
        Assert.assertEquals(0, adapter.getInterfaceNames().length);
        // method
        List<ASMMethodNodeAdapter> methods = adapter.getDeclaredMethods();
        Assert.assertEquals(1, methods.size());
        ASMMethodNodeAdapter method = adapter.getDeclaredMethod("extended", "()");
        Assert.assertEquals("extended", method.getName());
        method = adapter.getDeclaredMethod("notExist", "()");
        Assert.assertNull(method);
        // field
        ASMFieldNodeAdapter field = adapter.getField("e", null);
        Assert.assertEquals("e", field.getName());
        field = adapter.getField("e", "Ljava/lang/String;");
        Assert.assertEquals("e", field.getName());
        Assert.assertEquals("Ljava/lang/String;", field.getDesc());
        field = adapter.getField("notExist", null);
        Assert.assertNull(field);
        // interface
        adapter = ASMClassNodeAdapter.get(pluginContext, classLoader, getClass().getProtectionDomain(), "com/navercorp/pinpoint/profiler/instrument/mock/BaseInterface");
        Assert.assertEquals(true, adapter.isInterface());
        // implement
        adapter = ASMClassNodeAdapter.get(pluginContext, classLoader, getClass().getProtectionDomain(), "com/navercorp/pinpoint/profiler/instrument/mock/BaseImplementClass");
        String[] interfaceNames = adapter.getInterfaceNames();
        Assert.assertEquals(1, interfaceNames.length);
        Assert.assertEquals("com.navercorp.pinpoint.profiler.instrument.mock.BaseInterface", interfaceNames[0]);
        // annotation
        adapter = ASMClassNodeAdapter.get(pluginContext, classLoader, getClass().getProtectionDomain(), "com/navercorp/pinpoint/bootstrap/instrument/aspect/Aspect");
        Assert.assertEquals(true, adapter.isAnnotation());
    }

    @Test
    public void addGetter() throws Exception {
        final String targetClassName = "com.navercorp.pinpoint.profiler.instrument.mock.BaseClass";
        final String getterMethodName = "_$PINPOINT$_getValue";
        ASMClassNodeLoader.TestClassLoader classLoader = ASMClassNodeLoader.getClassLoader();
        classLoader.setTargetClassName(targetClassName);
        classLoader.setCallbackHandler(new ASMClassNodeLoader.CallbackHandler() {
            @Override
            public void handle(ClassNode classNode) {
                ASMClassNodeAdapter adapter = new ASMClassNodeAdapter(pluginContext, null, getClass().getProtectionDomain(), classNode);
                ASMFieldNodeAdapter field = adapter.getField("i", null);
                adapter.addGetterMethod(getterMethodName, field);
            }
        });
        Class<?> clazz = classLoader.loadClass(targetClassName);
        Method method = clazz.getDeclaredMethod(getterMethodName);
        Assert.assertEquals(0, method.invoke(clazz.newInstance()));
    }

    @Test
    public void addSetter() throws Exception {
        final String targetClassName = "com.navercorp.pinpoint.profiler.instrument.mock.BaseClass";
        final String setterMethodName = "_$PINPOINT$_setValue";
        ASMClassNodeLoader.TestClassLoader classLoader = ASMClassNodeLoader.getClassLoader();
        classLoader.setTargetClassName(targetClassName);
        classLoader.setCallbackHandler(new ASMClassNodeLoader.CallbackHandler() {
            @Override
            public void handle(ClassNode classNode) {
                ASMClassNodeAdapter adapter = new ASMClassNodeAdapter(pluginContext, null, getClass().getProtectionDomain(), classNode);
                ASMFieldNodeAdapter field = adapter.getField("i", null);
                adapter.addSetterMethod(setterMethodName, field);
            }
        });
        Class<?> clazz = classLoader.loadClass(targetClassName);
        Method method = clazz.getDeclaredMethod(setterMethodName, int.class);
        method.invoke(clazz.newInstance(), 10);
    }

    @Test
    public void addField() throws Exception {
        final String targetClassName = "com.navercorp.pinpoint.profiler.instrument.mock.BaseClass";
        final String accessorClassName = "com.navercorp.pinpoint.profiler.instrument.mock.BaseAccessor";
        final ASMClassNodeLoader.TestClassLoader classLoader = ASMClassNodeLoader.getClassLoader();
        classLoader.setTargetClassName(targetClassName);
        classLoader.setCallbackHandler(new ASMClassNodeLoader.CallbackHandler() {
            @Override
            public void handle(ClassNode classNode) {
                ASMClassNodeAdapter classNodeAdapter = new ASMClassNodeAdapter(pluginContext, null, getClass().getProtectionDomain(), classNode);
                classNodeAdapter.addField(("_$PINPOINT$_" + (JavaAssistUtils.javaClassNameToVariableName(accessorClassName))), Type.getDescriptor(int.class));
                classNodeAdapter.addInterface(accessorClassName);
                ASMFieldNodeAdapter fieldNode = classNodeAdapter.getField(("_$PINPOINT$_" + (JavaAssistUtils.javaClassNameToVariableName(accessorClassName))), null);
                classNodeAdapter.addGetterMethod("_$PINPOINT$_getTraceInt", fieldNode);
                classNodeAdapter.addSetterMethod("_$PINPOINT$_setTraceInt", fieldNode);
            }
        });
        Class<?> clazz = classLoader.loadClass(targetClassName);
        Object instance = clazz.newInstance();
        Method setMethod = clazz.getDeclaredMethod("_$PINPOINT$_setTraceInt", int.class);
        setMethod.invoke(instance, 10);
        Method getMethod = clazz.getDeclaredMethod("_$PINPOINT$_getTraceInt");
        int result = ((Integer) (getMethod.invoke(instance)));
        System.out.println(result);
    }

    @Test
    public void hasAnnotation() throws Exception {
        ASMClassNodeAdapter classNodeAdapter = ASMClassNodeAdapter.get(pluginContext, ASMClassNodeLoader.getClassLoader(), getClass().getProtectionDomain(), "com/navercorp/pinpoint/profiler/instrument/mock/AnnotationClass");
        Assert.assertTrue(classNodeAdapter.hasAnnotation(Aspect.class));
        Assert.assertFalse(classNodeAdapter.hasAnnotation(Override.class));
    }

    @Test
    public void addMethod() throws Exception {
        final MethodNode methodNode = ASMClassNodeLoader.get("com.navercorp.pinpoint.profiler.instrument.mock.ArgsClass", "arg");
        final ASMMethodNodeAdapter adapter = new ASMMethodNodeAdapter("com/navercorp/pinpoint/profiler/instrument/mock/ArgsClass", methodNode);
        final ASMClassNodeLoader.TestClassLoader testClassLoader = ASMClassNodeLoader.getClassLoader();
        final String targetClassName = "com.navercorp.pinpoint.profiler.instrument.mock.BaseClass";
        testClassLoader.setTargetClassName(targetClassName);
        testClassLoader.setCallbackHandler(new ASMClassNodeLoader.CallbackHandler() {
            @Override
            public void handle(ClassNode classNode) {
                ASMClassNodeAdapter classNodeAdapter = new ASMClassNodeAdapter(pluginContext, null, getClass().getProtectionDomain(), classNode);
                classNodeAdapter.copyMethod(adapter);
            }
        });
        Class<?> clazz = testClassLoader.loadClass(targetClassName);
        Method method = clazz.getDeclaredMethod("arg");
        method.invoke(clazz.newInstance());
    }

    @Test
    public void subclassOf() {
        ASMClassNodeAdapter adapter = ASMClassNodeAdapter.get(pluginContext, ASMClassNodeLoader.getClassLoader(), getClass().getProtectionDomain(), "com/navercorp/pinpoint/profiler/instrument/mock/ExtendedClass");
        // self
        Assert.assertEquals(true, adapter.subclassOf("com/navercorp/pinpoint/profiler/instrument/mock/ExtendedClass"));
        // super
        Assert.assertEquals(true, adapter.subclassOf("com/navercorp/pinpoint/profiler/instrument/mock/BaseClass"));
        Assert.assertEquals(true, adapter.subclassOf("java/lang/Object"));
        // not
        Assert.assertEquals(false, adapter.subclassOf("com/navercorp/pinpoint/profiler/instrument/mock/NormalClass"));
    }
}

