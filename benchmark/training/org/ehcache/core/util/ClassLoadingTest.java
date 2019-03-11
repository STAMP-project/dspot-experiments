/**
 * Copyright Terracotta, Inc.
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
package org.ehcache.core.util;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class ClassLoadingTest {
    @Test
    public void testDefaultClassLoader() throws Exception {
        ClassLoader originalTccl = Thread.currentThread().getContextClassLoader();
        try {
            String resource = getClass().getName().replace('.', '/').concat(".class");
            ClassLoader thisLoader = getClass().getClassLoader();
            ClassLoader defaultClassLoader = ClassLoading.getDefaultClassLoader();
            Thread.currentThread().setContextClassLoader(null);
            Assert.assertSame(thisLoader.loadClass(getClass().getName()), defaultClassLoader.loadClass(getClass().getName()));
            Assert.assertEquals(thisLoader.getResource(resource), defaultClassLoader.getResource(resource));
            Assert.assertThat(Collections.list(defaultClassLoader.getResources(resource)), Is.is(Collections.list(thisLoader.getResources(resource))));
            Thread.currentThread().setContextClassLoader(new ClassLoadingTest.FindNothingLoader());
            Assert.assertSame(thisLoader.loadClass(getClass().getName()), defaultClassLoader.loadClass(getClass().getName()));
            Assert.assertEquals(thisLoader.getResource(resource), defaultClassLoader.getResource(resource));
            Assert.assertThat(Collections.list(defaultClassLoader.getResources(resource)), Is.is(Collections.list(thisLoader.getResources(resource))));
            URL url = new URL("file:///tmp");
            ClassLoader tc = new ClassLoadingTest.TestClassLoader(url);
            Thread.currentThread().setContextClassLoader(tc);
            Class<?> c = defaultClassLoader.loadClass(getClass().getName());
            Assert.assertNotSame(getClass(), c);
            Assert.assertSame(tc, c.getClassLoader());
            Assert.assertEquals(url, defaultClassLoader.getResource(resource));
            Assert.assertThat(Collections.list(defaultClassLoader.getResources(resource)), contains(url, thisLoader.getResource(resource)));
        } finally {
            Thread.currentThread().setContextClassLoader(originalTccl);
        }
    }

    private static class TestClassLoader extends ClassLoader {
        private final URL url;

        TestClassLoader(URL url) {
            super(null);
            this.url = url;
        }

        @Override
        public Class<?> findClass(String name) throws ClassNotFoundException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            try {
                InputStream is = getClass().getClassLoader().getResourceAsStream(name.replace('.', '/').concat(".class"));
                int read;
                while ((read = is.read(buf)) >= 0) {
                    baos.write(buf, 0, read);
                } 
            } catch (IOException ioe) {
                throw new ClassNotFoundException();
            }
            byte[] data = baos.toByteArray();
            return defineClass(name, data, 0, data.length);
        }

        @Override
        public URL getResource(String name) {
            return url;
        }

        @Override
        public Enumeration<URL> getResources(String name) throws IOException {
            return ClassLoadingTest.enumerationOf(url);
        }
    }

    private static class FindNothingLoader extends ClassLoader {
        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            throw new ClassNotFoundException();
        }

        @Override
        public URL getResource(String name) {
            return null;
        }

        @Override
        public Enumeration<URL> getResources(String name) throws IOException {
            return new Vector<URL>().elements();
        }
    }
}

