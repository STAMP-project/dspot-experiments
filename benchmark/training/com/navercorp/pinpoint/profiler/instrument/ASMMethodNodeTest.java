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
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentException;
import com.navercorp.pinpoint.profiler.instrument.interceptor.InterceptorDefinition;
import com.navercorp.pinpoint.profiler.instrument.interceptor.InterceptorDefinitionFactory;
import com.navercorp.pinpoint.profiler.interceptor.registry.DefaultInterceptorRegistryBinder;
import com.navercorp.pinpoint.profiler.interceptor.registry.InterceptorRegistryBinder;
import java.lang.reflect.Method;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;


/**
 *
 *
 * @author jaehong.kim
 */
public class ASMMethodNodeTest {
    private static final InterceptorRegistryBinder interceptorRegistryBinder = new DefaultInterceptorRegistryBinder();

    private final InterceptorDefinitionFactory interceptorDefinitionFactory = new InterceptorDefinitionFactory();

    private final InstrumentContext pluginContext = Mockito.mock(InstrumentContext.class);

    @Test
    public void getter() throws Exception {
        final String targetClassName = "com.navercorp.pinpoint.profiler.instrument.mock.NormalClass";
        final String methodName = "sum";
        ASMClass declaringClass = Mockito.mock(ASMClass.class);
        Mockito.when(declaringClass.getName()).thenReturn(targetClassName);
        EngineComponent engineComponent = Mockito.mock(EngineComponent.class);
        final MethodNode methodNode = ASMClassNodeLoader.get(targetClassName, methodName);
        ASMMethod method = new ASMMethod(engineComponent, pluginContext, declaringClass, methodNode);
        Assert.assertEquals(methodName, method.getName());
        Assert.assertEquals(1, method.getParameterTypes().length);
        Assert.assertEquals("int", method.getParameterTypes()[0]);
        Assert.assertEquals("int", method.getReturnType());
        Assert.assertEquals(1, method.getModifiers());
        Assert.assertEquals(false, method.isConstructor());
        Assert.assertNotNull(method.getDescriptor());
    }

    @Test
    public void addInterceptor() throws Exception {
        final int interceptorId = ASMMethodNodeTest.interceptorRegistryBinder.getInterceptorRegistryAdaptor().addInterceptor(new com.navercorp.pinpoint.profiler.instrument.mock.ArgsArrayInterceptor());
        final String targetClassName = "com.navercorp.pinpoint.profiler.instrument.mock.NormalClass";
        final ASMClass declaringClass = Mockito.mock(ASMClass.class);
        Mockito.when(declaringClass.getName()).thenReturn(targetClassName);
        final EngineComponent engineComponent = Mockito.mock(EngineComponent.class);
        Mockito.when(engineComponent.createInterceptorDefinition(ArgumentMatchers.any(Class.class))).thenAnswer(new Answer<InterceptorDefinition>() {
            @Override
            public InterceptorDefinition answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                Class clazz = ((Class) (arguments[0]));
                return interceptorDefinitionFactory.createInterceptorDefinition(clazz);
            }
        });
        ASMClassNodeLoader.TestClassLoader classLoader = ASMClassNodeLoader.getClassLoader();
        final InstrumentException[] exception = new InstrumentException[1];
        classLoader.setTrace(false);
        classLoader.setVerify(false);
        classLoader.setTargetClassName(targetClassName);
        classLoader.setCallbackHandler(new ASMClassNodeLoader.CallbackHandler() {
            @Override
            public void handle(ClassNode classNode) {
                List<MethodNode> methodNodes = classNode.methods;
                for (MethodNode methodNode : methodNodes) {
                    ASMMethod method = new ASMMethod(engineComponent, pluginContext, declaringClass, methodNode);
                    try {
                        method.addInterceptor(interceptorId);
                    } catch (InstrumentException e) {
                        exception[0] = e;
                        e.printStackTrace();
                    }
                }
            }
        });
        Class<?> clazz = classLoader.loadClass(targetClassName);
        Method method = clazz.getDeclaredMethod("sum", int.class);
        Assert.assertEquals(55, method.invoke(clazz.newInstance(), 10));
        Assert.assertNull(exception[0]);
    }
}

