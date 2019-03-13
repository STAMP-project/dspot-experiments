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


import com.google.inject.Provider;
import com.google.inject.util.Providers;
import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.instrument.ClassFilters;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentContext;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentMethod;
import com.navercorp.pinpoint.bootstrap.instrument.MethodFilters;
import com.navercorp.pinpoint.profiler.context.monitor.DataSourceMonitorRegistryService;
import com.navercorp.pinpoint.profiler.instrument.interceptor.InterceptorDefinitionFactory;
import com.navercorp.pinpoint.profiler.interceptor.factory.ExceptionHandlerFactory;
import com.navercorp.pinpoint.profiler.interceptor.registry.DefaultInterceptorRegistryBinder;
import com.navercorp.pinpoint.profiler.interceptor.registry.InterceptorRegistryBinder;
import com.navercorp.pinpoint.profiler.metadata.ApiMetaDataService;
import com.navercorp.pinpoint.profiler.objectfactory.ObjectBinderFactory;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author jaehong.kim
 */
public class ASMClassTest {
    private final InterceptorRegistryBinder interceptorRegistryBinder = new DefaultInterceptorRegistryBinder();

    private final ProfilerConfig profilerConfig = Mockito.mock(ProfilerConfig.class);

    private final Provider<TraceContext> traceContextProvider = Providers.of(Mockito.mock(TraceContext.class));

    private final DataSourceMonitorRegistryService dataSourceMonitorRegistryService = Mockito.mock(DataSourceMonitorRegistryService.class);

    private final Provider<ApiMetaDataService> apiMetaDataService = Providers.of(Mockito.mock(ApiMetaDataService.class));

    private final InstrumentContext pluginContext = Mockito.mock(InstrumentContext.class);

    private final ExceptionHandlerFactory exceptionHandlerFactory = new ExceptionHandlerFactory(false);

    private final ObjectBinderFactory objectBinderFactory = new ObjectBinderFactory(profilerConfig, traceContextProvider, dataSourceMonitorRegistryService, apiMetaDataService, exceptionHandlerFactory);

    private final ScopeFactory scopeFactory = new ScopeFactory();

    private final InterceptorDefinitionFactory interceptorDefinitionFactory = new InterceptorDefinitionFactory();

    private final EngineComponent engineComponent = new DefaultEngineComponent(objectBinderFactory, interceptorRegistryBinder, interceptorDefinitionFactory, apiMetaDataService, scopeFactory);

    @Test
    public void getSuperClass() throws Exception {
        ASMClass clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.BaseClass");
        Assert.assertEquals("java.lang.Object", clazz.getSuperClass());
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.ExtendedClass");
        Assert.assertEquals("com.navercorp.pinpoint.profiler.instrument.mock.BaseClass", clazz.getSuperClass());
        clazz = getClass("java.lang.Object");
        Assert.assertEquals(null, clazz.getSuperClass());
    }

    @Test
    public void isInterface() throws Exception {
        ASMClass clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.BaseClass");
        Assert.assertEquals(false, clazz.isInterface());
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.BaseInterface");
        Assert.assertEquals(true, clazz.isInterface());
    }

    @Test
    public void getName() throws Exception {
        ASMClass clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.BaseClass");
        Assert.assertEquals("com.navercorp.pinpoint.profiler.instrument.mock.BaseClass", clazz.getName());
    }

    @Test
    public void getInterfaces() throws Exception {
        ASMClass clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.BaseClass");
        Assert.assertEquals(0, clazz.getInterfaces().length);
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.BaseInterface");
        Assert.assertEquals(0, clazz.getInterfaces().length);
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.BaseImplementClass");
        Assert.assertEquals(1, clazz.getInterfaces().length);
        Assert.assertEquals("com.navercorp.pinpoint.profiler.instrument.mock.BaseInterface", clazz.getInterfaces()[0]);
    }

    @Test
    public void getDeclaredMethod() throws Exception {
        ASMClass clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.ArgsClass");
        Assert.assertNull(clazz.getDeclaredMethod("notExists"));
        Assert.assertNotNull(clazz.getDeclaredMethod("arg"));
        Assert.assertNotNull(clazz.getDeclaredMethod("argByteType", "byte"));
        Assert.assertNotNull(clazz.getDeclaredMethod("argShortType", "short"));
        Assert.assertNotNull(clazz.getDeclaredMethod("argIntType", "int"));
        Assert.assertNotNull(clazz.getDeclaredMethod("argFloatType", "float"));
        Assert.assertNotNull(clazz.getDeclaredMethod("argDoubleType", "double"));
        Assert.assertNotNull(clazz.getDeclaredMethod("argBooleanType", "boolean"));
        Assert.assertNotNull(clazz.getDeclaredMethod("argCharType", "char"));
        Assert.assertNotNull(clazz.getDeclaredMethod("argByteArrayType", "byte[]"));
        Assert.assertNotNull(clazz.getDeclaredMethod("argShortArrayType", "short[]"));
        Assert.assertNotNull(clazz.getDeclaredMethod("argIntArrayType", "int[]"));
        Assert.assertNotNull(clazz.getDeclaredMethod("argFloatArrayType", "float[]"));
        Assert.assertNotNull(clazz.getDeclaredMethod("argDoubleArrayType", "double[]"));
        Assert.assertNotNull(clazz.getDeclaredMethod("argBooleanArrayType", "boolean[]"));
        Assert.assertNotNull(clazz.getDeclaredMethod("argCharArrayType", "char[]"));
        Assert.assertNotNull(clazz.getDeclaredMethod("argByteArraysType", "byte[][]"));
        Assert.assertNotNull(clazz.getDeclaredMethod("argShortArraysType", "short[][]"));
        Assert.assertNotNull(clazz.getDeclaredMethod("argIntArraysType", "int[][]"));
        Assert.assertNotNull(clazz.getDeclaredMethod("argFloatArraysType", "float[][]"));
        Assert.assertNotNull(clazz.getDeclaredMethod("argDoubleArraysType", "double[][]"));
        Assert.assertNotNull(clazz.getDeclaredMethod("argBooleanArraysType", "boolean[][]"));
        Assert.assertNotNull(clazz.getDeclaredMethod("argCharArraysType", "char[][]"));
        Assert.assertNotNull(clazz.getDeclaredMethod("argAllType", "byte", "short", "int", "float", "double", "boolean", "char", "byte[]", "short[]", "int[]", "float[]", "double[]", "boolean[]", "char[]", "byte[][]", "short[][]", "int[][]", "float[][]", "double[][]", "boolean[][]", "char[][]"));
        Assert.assertNotNull(clazz.getDeclaredMethod("argObject", "java.lang.String", "java.lang.Object", "java.lang.Byte", "java.lang.Short", "java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double", "java.lang.Boolean", "java.lang.Character", "java.lang.String[]", "java.lang.Object[]", "java.lang.Byte[]", "java.lang.Short[]", "java.lang.Integer[]", "java.lang.Long[]", "java.lang.Float[]", "java.lang.Double[]", "java.lang.Boolean[]", "java.lang.Character[]", "java.lang.String[][]", "java.lang.Object[][]", "java.lang.Byte[][]", "java.lang.Short[][]", "java.lang.Integer[][]", "java.lang.Long[][]", "java.lang.Float[][]", "java.lang.Double[][]", "java.lang.Boolean[][]", "java.lang.Character[][]"));
        Assert.assertNotNull(clazz.getDeclaredMethod("argArgs", "java.lang.Object[]"));
        Assert.assertNotNull(clazz.getDeclaredMethod("argInterface", "java.util.Map", "java.util.Map", "java.util.Map"));
        Assert.assertNotNull(clazz.getDeclaredMethod("argEnum", "java.lang.Enum"));
        Assert.assertNotNull(clazz.getDeclaredMethod("argEnumArray", "java.lang.Enum[]"));
        Assert.assertNotNull(clazz.getDeclaredMethod("argEnumArrays", "java.lang.Enum[][]"));
        // find super's method.
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.ExtendedClass");
        Assert.assertNull(clazz.getDeclaredMethod("base"));
    }

    @Test
    public void getDeclaredMethods() throws Exception {
        ASMClass clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.ArgsClass");
        List<InstrumentMethod> methods = clazz.getDeclaredMethods();
        Assert.assertNotNull(methods);
        methods = clazz.getDeclaredMethods(MethodFilters.name("arg"));
        Assert.assertEquals(1, methods.size());
        Assert.assertEquals("arg", methods.get(0).getName());
    }

    @Test
    public void hasDeclaredMethod() throws Exception {
        ASMClass clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.ArgsClass");
        Assert.assertFalse(clazz.hasDeclaredMethod("notExists"));
        Assert.assertTrue(clazz.hasDeclaredMethod("arg"));
        Assert.assertTrue(clazz.hasDeclaredMethod("argByteType", "byte"));
        // find super's method.
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.ExtendedClass");
        Assert.assertFalse(clazz.hasDeclaredMethod("base"));
    }

    @Test
    public void hasMethod() throws Exception {
        // find not exists method.
        ASMClass clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.ArgsClass");
        Assert.assertFalse(clazz.hasMethod("notExists"));
        // find method.
        Assert.assertTrue(clazz.hasMethod("arg"));
        Assert.assertTrue(clazz.hasMethod("argByteType", "byte"));
        // find super's method.
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.ExtendedClass");
        Assert.assertTrue(clazz.hasMethod("base"));
    }

    @Test
    public void hasEnclosingMethod() throws Exception {
        ASMClass clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.BaseClass");
        Assert.assertFalse(clazz.hasEnclosingMethod("notExists"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.NestedClass$3LocalInner");
        Assert.assertTrue(clazz.hasEnclosingMethod("enclosingMethod", "java.lang.String", "int"));
    }

    @Test
    public void hasConstructor() throws Exception {
        ASMClass clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.BaseClass");
        Assert.assertTrue(clazz.hasConstructor());
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.ExtendedClass");
        Assert.assertTrue(clazz.hasConstructor());
        Assert.assertTrue(clazz.hasConstructor("java.lang.String"));
        Assert.assertFalse(clazz.hasConstructor("java.lang.String", "int", "byte"));
    }

    @Test
    public void hasField() throws Exception {
        ASMClass clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.BaseClass");
        Assert.assertTrue(clazz.hasField("s"));
        Assert.assertFalse(clazz.hasField("notExists"));
        Assert.assertTrue(clazz.hasField("b", "boolean"));
        Assert.assertTrue(clazz.hasField("s", "java.lang.String"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.ExtendedClass");
        Assert.assertTrue(clazz.hasField("s"));
        Assert.assertTrue(clazz.hasField("e"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        Assert.assertTrue(clazz.hasField("b"));
        Assert.assertTrue(clazz.hasField("s"));
        Assert.assertTrue(clazz.hasField("i"));
        Assert.assertTrue(clazz.hasField("l"));
        Assert.assertTrue(clazz.hasField("f"));
        Assert.assertTrue(clazz.hasField("d"));
        Assert.assertTrue(clazz.hasField("y"));
        Assert.assertTrue(clazz.hasField("c"));
        Assert.assertTrue(clazz.hasField("bArray"));
        Assert.assertTrue(clazz.hasField("sArray"));
        Assert.assertTrue(clazz.hasField("iArray"));
        Assert.assertTrue(clazz.hasField("lArray"));
        Assert.assertTrue(clazz.hasField("fArray"));
        Assert.assertTrue(clazz.hasField("dArray"));
        Assert.assertTrue(clazz.hasField("yArray"));
        Assert.assertTrue(clazz.hasField("cArray"));
        Assert.assertTrue(clazz.hasField("bArrays"));
        Assert.assertTrue(clazz.hasField("sArrays"));
        Assert.assertTrue(clazz.hasField("iArrays"));
        Assert.assertTrue(clazz.hasField("lArrays"));
        Assert.assertTrue(clazz.hasField("fArrays"));
        Assert.assertTrue(clazz.hasField("dArrays"));
        Assert.assertTrue(clazz.hasField("yArrays"));
        Assert.assertTrue(clazz.hasField("cArrays"));
        Assert.assertTrue(clazz.hasField("str"));
        Assert.assertTrue(clazz.hasField("object"));
        Assert.assertTrue(clazz.hasField("publicStaticFinalStr"));
        Assert.assertTrue(clazz.hasField("volatileInt"));
        Assert.assertTrue(clazz.hasField("transientInt"));
    }

    @Test
    public void addDelegatorMethod() throws Exception {
        ASMClass clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.ExtendedClass");
        clazz.addDelegatorMethod("base");
        Assert.assertNotNull(clazz.getDeclaredMethod("base"));
        // duplicated.
        try {
            clazz.addDelegatorMethod("extended");
            Assert.fail("skip throw exception.");
        } catch (Exception ignored) {
        }
        // not exist.
        try {
            clazz.addDelegatorMethod("notExist");
            Assert.fail("skip throw exception.");
        } catch (Exception ignored) {
        }
        clazz.addDelegatorMethod("getInstance");
        Assert.assertNotNull(clazz.getDeclaredMethod("getInstance"));
    }

    @Test
    public void addField() throws Exception {
        ASMClass clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.BaseClass");
        clazz.addField("com.navercorp.pinpoint.profiler.instrument.mock.accessor.IntAccessor");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_setTraceInt", "int"));
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_getTraceInt"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.BaseClass");
        clazz.addField("com.navercorp.pinpoint.profiler.instrument.mock.accessor.IntArrayAccessor");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_setTraceIntArray", "int[]"));
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_getTraceIntArray"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.BaseClass");
        clazz.addField("com.navercorp.pinpoint.profiler.instrument.mock.accessor.IntArraysAccessor");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_setTraceIntArrays", "int[][]"));
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_getTraceIntArrays"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.BaseClass");
        clazz.addField("com.navercorp.pinpoint.profiler.instrument.mock.accessor.ObjectAccessor");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_setTraceObject", "java.lang.Object"));
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_getTraceObject"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.BaseClass");
        clazz.addField("com.navercorp.pinpoint.profiler.instrument.mock.accessor.ObjectArrayAccessor");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_setTraceObjectArray", "java.lang.Object[]"));
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_getTraceObjectArray"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.BaseClass");
        clazz.addField("com.navercorp.pinpoint.profiler.instrument.mock.accessor.ObjectArraysAccessor");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_setTraceObjectArrays", "java.lang.Object[][]"));
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_getTraceObjectArrays"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.BaseClass");
        clazz.addField("com.navercorp.pinpoint.profiler.instrument.mock.accessor.PublicStrAccessor");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_setTracePublicStr", "java.lang.String"));
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_getTracePublicStr"));
        // skip throw exception.
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.BaseClass");
        clazz.addField("com.navercorp.pinpoint.profiler.instrument.mock.accessor.ThrowExceptionAccessor");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_setTraceDefaultStr", "java.lang.String"));
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_getTraceDefaultStr"));
    }

    @Test
    public void addGetter() throws Exception {
        // TODO super field.
        ASMClass clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.BaseClass");
        clazz.addGetter("com.navercorp.pinpoint.profiler.instrument.mock.BaseGetter", "b");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_isB"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addGetter("com.navercorp.pinpoint.profiler.instrument.mock.getter.FieldIntGetter", "i");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_getInt"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addGetter("com.navercorp.pinpoint.profiler.instrument.mock.getter.FieldIntArrayGetter", "iArray");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_getIntArray"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addGetter("com.navercorp.pinpoint.profiler.instrument.mock.getter.FieldObjectGetter", "object");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_getObject"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addGetter("com.navercorp.pinpoint.profiler.instrument.mock.getter.FieldObjectArrayGetter", "objectArray");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_getObjectArray"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addGetter("com.navercorp.pinpoint.profiler.instrument.mock.getter.FieldObjectArraysGetter", "objectArrays");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_getObjectArrays"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addGetter("com.navercorp.pinpoint.profiler.instrument.mock.getter.FieldEnumGetter", "e");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_getEnum"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addGetter("com.navercorp.pinpoint.profiler.instrument.mock.getter.FieldMapGetter", "map");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_getMap"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addGetter("com.navercorp.pinpoint.profiler.instrument.mock.getter.FieldStrMapGetter", "strMap");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_getStrMap"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addGetter("com.navercorp.pinpoint.profiler.instrument.mock.getter.FieldObjectMapGetter", "objectMap");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_getObjectMap"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addGetter("com.navercorp.pinpoint.profiler.instrument.mock.getter.FieldWildcardMapGetter", "wildcardMap");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_getWildcardMap"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addGetter("com.navercorp.pinpoint.profiler.instrument.mock.getter.FieldDefaultStrGetter", "defaultStr");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_getDefaultStr"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addGetter("com.navercorp.pinpoint.profiler.instrument.mock.getter.FieldDefaultStaticStrGetter", "defaultStaticStr");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_getDefaultStaticStr"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addGetter("com.navercorp.pinpoint.profiler.instrument.mock.getter.FieldDefaultStaticFinalStrGetter", "defaultStaticFinalStr");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_getDefaultStaticFinalStr"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addGetter("com.navercorp.pinpoint.profiler.instrument.mock.getter.FieldPrivateStrGetter", "privateStr");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_getPrivateStr"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addGetter("com.navercorp.pinpoint.profiler.instrument.mock.getter.FieldPrivateStaticStrGetter", "privateStaticStr");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_getPrivateStaticStr"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addGetter("com.navercorp.pinpoint.profiler.instrument.mock.getter.FieldPrivateStaticFinalStrGetter", "privateStaticFinalStr");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_getPrivateStaticFinalStr"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addGetter("com.navercorp.pinpoint.profiler.instrument.mock.getter.FieldProtectedStrGetter", "protectedStr");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_getProtectedStr"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addGetter("com.navercorp.pinpoint.profiler.instrument.mock.getter.FieldProtectedStaticStrGetter", "protectedStaticStr");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_getProtectedStaticStr"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addGetter("com.navercorp.pinpoint.profiler.instrument.mock.getter.FieldProtectedStaticFinalStrGetter", "protectedStaticFinalStr");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_getProtectedStaticFinalStr"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addGetter("com.navercorp.pinpoint.profiler.instrument.mock.getter.FieldPublicStrGetter", "publicStr");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_getPublicStr"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addGetter("com.navercorp.pinpoint.profiler.instrument.mock.getter.FieldPublicStaticStrGetter", "publicStaticStr");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_getPublicStaticStr"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addGetter("com.navercorp.pinpoint.profiler.instrument.mock.getter.FieldPublicStaticFinalStrGetter", "publicStaticFinalStr");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_getPublicStaticFinalStr"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addGetter("com.navercorp.pinpoint.profiler.instrument.mock.getter.FieldVolatileIntGetter", "volatileInt");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_getVolatileInt"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addGetter("com.navercorp.pinpoint.profiler.instrument.mock.getter.FieldTransientIntGetter", "transientInt");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_getTransientInt"));
    }

    @Test
    public void addSetter() throws Exception {
        ASMClass clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.BaseClass");
        clazz.addSetter("com.navercorp.pinpoint.profiler.instrument.mock.BaseSetter", "b");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_setB", "boolean"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addSetter("com.navercorp.pinpoint.profiler.instrument.mock.setter.FieldIntSetter", "i");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_setInt", "int"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addSetter("com.navercorp.pinpoint.profiler.instrument.mock.setter.FieldIntArraySetter", "iArray");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_setIntArray", "int[]"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addSetter("com.navercorp.pinpoint.profiler.instrument.mock.setter.FieldObjectSetter", "object");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_setObject", "java.lang.Object"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addSetter("com.navercorp.pinpoint.profiler.instrument.mock.setter.FieldObjectArraySetter", "objectArray");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_setObjectArray", "java.lang.Object[]"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addSetter("com.navercorp.pinpoint.profiler.instrument.mock.setter.FieldObjectArraysSetter", "objectArrays");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_setObjectArrays", "java.lang.Object[][]"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addSetter("com.navercorp.pinpoint.profiler.instrument.mock.setter.FieldEnumSetter", "e");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_setEnum", "java.lang.Enum"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addSetter("com.navercorp.pinpoint.profiler.instrument.mock.setter.FieldMapSetter", "map");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_setMap", "java.util.Map"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addSetter("com.navercorp.pinpoint.profiler.instrument.mock.setter.FieldStrMapSetter", "strMap");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_setStrMap", "java.util.Map"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addSetter("com.navercorp.pinpoint.profiler.instrument.mock.setter.FieldObjectMapSetter", "objectMap");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_setObjectMap", "java.util.Map"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addSetter("com.navercorp.pinpoint.profiler.instrument.mock.setter.FieldWildcardMapSetter", "wildcardMap");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_setWildcardMap", "java.util.Map"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addSetter("com.navercorp.pinpoint.profiler.instrument.mock.setter.FieldDefaultStrSetter", "defaultStr");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_setDefaultStr", "java.lang.String"));
        try {
            clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
            clazz.addSetter("com.navercorp.pinpoint.profiler.instrument.mock.setter.FieldDefaultStaticStrSetter", "defaultStaticStr");
            Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_setDefaultStaticStr", "java.lang.String"));
            Assert.fail("can't throw exception");
        } catch (Exception ignored) {
        }
        try {
            clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
            clazz.addSetter("com.navercorp.pinpoint.profiler.instrument.mock.setter.FieldDefaultFinalStrSetter", "defaultFinalStr");
            Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_setDefaultFinalStr", "java.lang.String"));
            Assert.fail("can't throw exception");
        } catch (Exception ignored) {
        }
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addSetter("com.navercorp.pinpoint.profiler.instrument.mock.setter.FieldPrivateStrSetter", "privateStr");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_setPrivateStr", "java.lang.String"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addSetter("com.navercorp.pinpoint.profiler.instrument.mock.setter.FieldProtectedStrSetter", "protectedStr");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_setProtectedStr", "java.lang.String"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addSetter("com.navercorp.pinpoint.profiler.instrument.mock.setter.FieldPublicStrSetter", "publicStr");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_setPublicStr", "java.lang.String"));
        try {
            clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
            clazz.addSetter("com.navercorp.pinpoint.profiler.instrument.mock.setter.FieldPublicFinalStrSetter", "publicFinalStr");
            Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_setPublicFinalStr", "java.lang.String"));
            Assert.fail("can't throw exception");
        } catch (Exception ignored) {
        }
        // removeFinal is true
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addSetter("com.navercorp.pinpoint.profiler.instrument.mock.setter.FieldPublicFinalStrSetter", "publicFinalStr", true);
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_setPublicFinalStr", "java.lang.String"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addSetter("com.navercorp.pinpoint.profiler.instrument.mock.setter.FieldVolatileIntSetter", "volatileInt");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_setVolatileInt", "int"));
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.FieldClass");
        clazz.addSetter("com.navercorp.pinpoint.profiler.instrument.mock.setter.FieldTransientIntSetter", "transientInt");
        Assert.assertNotNull(clazz.getDeclaredMethod("_$PINPOINT$_setTransientInt", "int"));
    }

    @Test
    public void addInterceptor() throws Exception {
        ASMClass clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.BaseClass");
        clazz.addInterceptor("com.navercorp.pinpoint.profiler.instrument.mock.BaseAnnotationInterceptor");
    }

    @Test
    public void getNestedClasses() throws Exception {
        ASMClass clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.NestedClass");
        String targetClassName = "com.navercorp.pinpoint.profiler.instrument.mock.NestedClass$StaticNested";
        Assert.assertEquals(1, clazz.getNestedClasses(ClassFilters.name(targetClassName)).size());
        Assert.assertEquals(targetClassName, clazz.getNestedClasses(ClassFilters.name(targetClassName)).get(0).getName());
        targetClassName = "com.navercorp.pinpoint.profiler.instrument.mock.NestedClass$InstanceInner";
        Assert.assertEquals(1, clazz.getNestedClasses(ClassFilters.name(targetClassName)).size());
        Assert.assertEquals(targetClassName, clazz.getNestedClasses(ClassFilters.name(targetClassName)).get(0).getName());
        targetClassName = "com.navercorp.pinpoint.profiler.instrument.mock.NestedClass$1LocalInner";
        Assert.assertEquals(1, clazz.getNestedClasses(ClassFilters.name(targetClassName)).size());
        Assert.assertEquals(targetClassName, clazz.getNestedClasses(ClassFilters.name(targetClassName)).get(0).getName());
        targetClassName = "com.navercorp.pinpoint.profiler.instrument.mock.NestedClass$1";
        Assert.assertEquals(1, clazz.getNestedClasses(ClassFilters.name(targetClassName)).size());
        Assert.assertEquals(targetClassName, clazz.getNestedClasses(ClassFilters.name(targetClassName)).get(0).getName());
        // find enclosing method condition.
        Assert.assertEquals(2, clazz.getNestedClasses(ClassFilters.enclosingMethod("annonymousInnerClass")).size());
        // find interface condition.
        Assert.assertEquals(2, clazz.getNestedClasses(ClassFilters.interfaze("java.util.concurrent.Callable")).size());
        // find enclosing method & interface condition.
        Assert.assertEquals(1, clazz.getNestedClasses(ClassFilters.chain(ClassFilters.enclosingMethod("annonymousInnerClass"), ClassFilters.interfaze("java.util.concurrent.Callable"))).size());
    }

    @Test
    public void isInterceptorable() throws Exception {
        ASMClass clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.BaseInterface");
        Assert.assertFalse(clazz.isInterceptable());
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.BaseClass");
        Assert.assertTrue(clazz.isInterceptable());
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.BaseEnum");
        Assert.assertTrue(clazz.isInterceptable());
        clazz = getClass("com.navercorp.pinpoint.profiler.instrument.mock.BaseEnum");
        Assert.assertTrue(clazz.isInterceptable());
    }
}

