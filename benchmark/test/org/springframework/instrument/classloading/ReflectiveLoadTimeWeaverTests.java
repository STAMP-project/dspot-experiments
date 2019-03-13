/**
 * Copyright 2002-2012 the original author or authors.
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
package org.springframework.instrument.classloading;


import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for the {@link ReflectiveLoadTimeWeaver} class.
 *
 * @author Rick Evans
 * @author Chris Beams
 */
public class ReflectiveLoadTimeWeaverTests {
    @Test(expected = IllegalArgumentException.class)
    public void testCtorWithNullClassLoader() {
        new ReflectiveLoadTimeWeaver(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testCtorWithClassLoaderThatDoesNotExposeAnAddTransformerMethod() {
        new ReflectiveLoadTimeWeaver(getClass().getClassLoader());
    }

    @Test
    public void testCtorWithClassLoaderThatDoesNotExposeAGetThrowawayClassLoaderMethodIsOkay() {
        ReflectiveLoadTimeWeaverTests.JustAddTransformerClassLoader classLoader = new ReflectiveLoadTimeWeaverTests.JustAddTransformerClassLoader();
        ReflectiveLoadTimeWeaver weaver = new ReflectiveLoadTimeWeaver(classLoader);
        weaver.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                return "CAFEDEAD".getBytes();
            }
        });
        Assert.assertEquals(1, classLoader.getNumTimesGetThrowawayClassLoaderCalled());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddTransformerWithNullTransformer() {
        new ReflectiveLoadTimeWeaver(new ReflectiveLoadTimeWeaverTests.JustAddTransformerClassLoader()).addTransformer(null);
    }

    @Test
    public void testGetThrowawayClassLoaderWithClassLoaderThatDoesNotExposeAGetThrowawayClassLoaderMethodYieldsFallbackClassLoader() {
        ReflectiveLoadTimeWeaver weaver = new ReflectiveLoadTimeWeaver(new ReflectiveLoadTimeWeaverTests.JustAddTransformerClassLoader());
        ClassLoader throwawayClassLoader = weaver.getThrowawayClassLoader();
        Assert.assertNotNull(throwawayClassLoader);
    }

    @Test
    public void testGetThrowawayClassLoaderWithTotallyCompliantClassLoader() {
        ReflectiveLoadTimeWeaverTests.TotallyCompliantClassLoader classLoader = new ReflectiveLoadTimeWeaverTests.TotallyCompliantClassLoader();
        ReflectiveLoadTimeWeaver weaver = new ReflectiveLoadTimeWeaver(classLoader);
        ClassLoader throwawayClassLoader = weaver.getThrowawayClassLoader();
        Assert.assertNotNull(throwawayClassLoader);
        Assert.assertEquals(1, classLoader.getNumTimesGetThrowawayClassLoaderCalled());
    }

    public static class JustAddTransformerClassLoader extends ClassLoader {
        private int numTimesAddTransformerCalled = 0;

        public int getNumTimesGetThrowawayClassLoaderCalled() {
            return this.numTimesAddTransformerCalled;
        }

        public void addTransformer(ClassFileTransformer transformer) {
            ++(this.numTimesAddTransformerCalled);
        }
    }

    public static final class TotallyCompliantClassLoader extends ReflectiveLoadTimeWeaverTests.JustAddTransformerClassLoader {
        private int numTimesGetThrowawayClassLoaderCalled = 0;

        @Override
        public int getNumTimesGetThrowawayClassLoaderCalled() {
            return this.numTimesGetThrowawayClassLoaderCalled;
        }

        public ClassLoader getThrowawayClassLoader() {
            ++(this.numTimesGetThrowawayClassLoaderCalled);
            return getClass().getClassLoader();
        }
    }
}

