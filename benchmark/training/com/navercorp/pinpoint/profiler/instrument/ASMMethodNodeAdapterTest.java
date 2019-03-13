/**
 * Copyright 2016 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.profiler.instrument;


import com.navercorp.pinpoint.bootstrap.instrument.aspect.JointPoint;
import com.navercorp.pinpoint.bootstrap.instrument.aspect.PointCut;
import com.navercorp.pinpoint.profiler.instrument.interceptor.InterceptorDefinition;
import com.navercorp.pinpoint.profiler.instrument.interceptor.InterceptorDefinitionFactory;
import com.navercorp.pinpoint.profiler.instrument.mock.ArgsArrayInterceptor;
import com.navercorp.pinpoint.profiler.interceptor.registry.DefaultInterceptorRegistryBinder;
import com.navercorp.pinpoint.profiler.interceptor.registry.InterceptorRegistryBinder;
import com.navercorp.pinpoint.profiler.util.JavaAssistUtils;
import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.tree.MethodNode;


public class ASMMethodNodeAdapterTest {
    private static final InterceptorRegistryBinder interceptorRegistryBinder = new DefaultInterceptorRegistryBinder();

    @Test
    public void isVisited() throws Exception {
        // init
        final int interceptorId = ASMMethodNodeAdapterTest.interceptorRegistryBinder.getInterceptorRegistryAdaptor().addInterceptor(new ArgsArrayInterceptor());
        final InterceptorDefinition interceptorDefinition = new InterceptorDefinitionFactory().createInterceptorDefinition(ArgsArrayInterceptor.class);
        final String targetClassName = "com.navercorp.pinpoint.profiler.instrument.mock.ArgsClass";
        final MethodNode methodNode = ASMClassNodeLoader.get(targetClassName, "arg");
        ASMMethodNodeAdapter adapter = new ASMMethodNodeAdapter(JavaAssistUtils.javaNameToJvmName(targetClassName), methodNode);
        Assert.assertEquals(false, adapter.hasInterceptor());
        adapter.addBeforeInterceptor(interceptorId, interceptorDefinition, (-1));
        Assert.assertEquals(true, adapter.hasInterceptor());
    }

    @Test
    public void methodAccess() throws Exception {
        final String targetClassName = "com.navercorp.pinpoint.profiler.instrument.mock.MethodClass";
        final MethodNode methodNode = ASMClassNodeLoader.get(targetClassName, "publicStaticMethod");
        ASMMethodNodeAdapter adapter = new ASMMethodNodeAdapter(JavaAssistUtils.javaNameToJvmName(targetClassName), methodNode);
        Assert.assertEquals(true, adapter.isStatic());
        Assert.assertEquals(false, adapter.isAbstract());
        Assert.assertEquals(false, adapter.isPrivate());
        Assert.assertEquals(false, adapter.isNative());
    }

    @Test
    public void getLineNumber() throws Exception {
        final String targetClassName = "com.navercorp.pinpoint.profiler.instrument.mock.NormalClass";
        final MethodNode methodNode = ASMClassNodeLoader.get(targetClassName, "sum");
        ASMMethodNodeAdapter adapter = new ASMMethodNodeAdapter(JavaAssistUtils.javaNameToJvmName(targetClassName), methodNode);
        Assert.assertEquals(44, adapter.getLineNumber());
    }

    @Test
    public void hasAnnotation() throws Exception {
        final String targetClassName = "com.navercorp.pinpoint.profiler.instrument.mock.AnnotationClass";
        final MethodNode methodNode = ASMClassNodeLoader.get(targetClassName, "pointCut");
        ASMMethodNodeAdapter adapter = new ASMMethodNodeAdapter(JavaAssistUtils.javaNameToJvmName(targetClassName), methodNode);
        Assert.assertEquals(true, adapter.hasAnnotation(PointCut.class));
        Assert.assertEquals(false, adapter.hasAnnotation(JointPoint.class));
    }
}

