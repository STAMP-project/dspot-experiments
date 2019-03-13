/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shiro.guice.aop;


import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.google.inject.spi.Element;
import com.google.inject.spi.Elements;
import com.google.inject.spi.InterceptorBinding;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import org.aopalliance.intercept.MethodInterceptor;
import org.junit.Assert;
import org.junit.Test;


public class ShiroAopModuleTest {
    @Test
    public void testGetAnnotationResolver() {
        final AnnotationResolver annotationResolver = new DefaultAnnotationResolver();
        ShiroAopModule underTest = new ShiroAopModule() {
            @Override
            protected AnnotationResolver createAnnotationResolver() {
                return annotationResolver;
            }

            @Override
            protected void configureDefaultInterceptors(AnnotationResolver resolver) {
                Assert.assertSame(annotationResolver, resolver);
                bind(Object.class).annotatedWith(Names.named("configureDefaultInterceptors"));
            }

            @Override
            protected void configureInterceptors(AnnotationResolver resolver) {
                Assert.assertSame(annotationResolver, resolver);
                bind(Object.class).annotatedWith(Names.named("configureInterceptors"));
            }
        };
        boolean calledDefault = false;
        boolean calledCustom = false;
        for (Element e : Elements.getElements(underTest)) {
            if (e instanceof Binding) {
                Key key = getKey();
                if (((Named.class.isAssignableFrom(key.getAnnotation().annotationType())) && ("configureInterceptors".equals(value()))) && (key.getTypeLiteral().getRawType().equals(Object.class))) {
                    calledCustom = true;
                }
                if (((Named.class.isAssignableFrom(key.getAnnotation().annotationType())) && ("configureDefaultInterceptors".equals(value()))) && (key.getTypeLiteral().getRawType().equals(Object.class))) {
                    calledDefault = true;
                }
            }
        }
    }

    @Test
    public void testBindShiroInterceptor() {
        ShiroAopModule underTest = new ShiroAopModule() {
            @Override
            protected void configureInterceptors(AnnotationResolver resolver) {
                bindShiroInterceptor(new ShiroAopModuleTest.MyAnnotationMethodInterceptor());
            }
        };
        List<Element> elements = Elements.getElements(underTest);
        for (Element element : elements) {
            if (element instanceof InterceptorBinding) {
                InterceptorBinding binding = ((InterceptorBinding) (element));
                Assert.assertTrue(binding.getClassMatcher().matches(getClass()));
                Method method = null;
                Class<? extends Annotation> theAnnotation = null;
                for (Class<? extends Annotation> annotation : protectedMethods.keySet()) {
                    if (binding.getMethodMatcher().matches(protectedMethods.get(annotation))) {
                        method = protectedMethods.get(annotation);
                        theAnnotation = annotation;
                        protectedMethods.remove(annotation);
                        break;
                    }
                }
                if (method == null) {
                    Assert.fail(("Did not expect interceptor binding " + (binding.getInterceptors())));
                }
                List<MethodInterceptor> interceptors = binding.getInterceptors();
                Assert.assertEquals(1, interceptors.size());
                Assert.assertTrue(((interceptors.get(0)) instanceof AopAllianceMethodInterceptorAdapter));
                Assert.assertTrue(interceptorTypes.get(theAnnotation).isInstance(((AopAllianceMethodInterceptorAdapter) (interceptors.get(0))).shiroInterceptor));
            }
        }
        Assert.assertTrue("Not all interceptors were bound.", protectedMethods.isEmpty());
    }

    @Target({ ElementType.TYPE, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    private static @interface MyTestAnnotation {}

    private static class MyAnnotationHandler extends AnnotationHandler {
        /**
         * Constructs an <code>AnnotationHandler</code> who processes annotations of the
         * specified type.  Immediately calls {@link #setAnnotationClass(Class)}.
         *
         * @param annotationClass
         * 		the type of annotation this handler will process.
         */
        public MyAnnotationHandler(Class<? extends Annotation> annotationClass) {
            super(annotationClass);
        }
    }

    private static class MyAnnotationMethodInterceptor extends AnnotationMethodInterceptor {
        public MyAnnotationMethodInterceptor() {
            super(new ShiroAopModuleTest.MyAnnotationHandler(ShiroAopModuleTest.MyTestAnnotation.class));
        }

        public Object invoke(MethodInvocation methodInvocation) throws Throwable {
            return null;
        }
    }

    private Map<Class<? extends Annotation>, Method> protectedMethods;

    private Map<Class<? extends Annotation>, Class<? extends AnnotationMethodInterceptor>> interceptorTypes;
}

