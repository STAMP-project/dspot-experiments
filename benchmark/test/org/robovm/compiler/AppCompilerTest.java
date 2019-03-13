/**
 * Copyright (C) 2015 RoboVM AB
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.robovm.compiler;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.robovm.compiler.clazz.Clazz;
import org.robovm.compiler.clazz.Clazzes;
import org.robovm.compiler.clazz.Path;


/**
 *
 *
 * @author Jaroslav Tulach
 */
public class AppCompilerTest {
    @Test
    public void testMetainfServiceImplIsAdded() throws Exception {
        final Path impl1 = new AppCompilerTest.MockPath("META-INF/services/java.lang.Number", "java.lang.Integer");
        Clazzes clazzes = AppCompilerTest.createClazzes(impl1);
        Clazz interfaceClazz = clazzes.load("java/lang/Number");
        Set<Clazz> compiled = new HashSet<>();
        Set<Clazz> queue = new LinkedHashSet<>();
        AppCompiler.addMetaInfImplementations(clazzes, interfaceClazz, compiled, queue);
        Assert.assertEquals(("One item added to queue: " + queue), 1, queue.size());
        Assert.assertTrue(("Integer in queue" + queue), queue.contains(clazzes.load("java/lang/Integer")));
    }

    @Test
    public void testMultipleMetainfServiceImplsAdded() throws Exception {
        final Path impl1 = new AppCompilerTest.MockPath("META-INF/services/java.lang.Number", "java.lang.Integer");
        final Path impl2 = new AppCompilerTest.MockPath("META-INF/services/java.lang.Number", "java.lang.Long");
        Clazzes clazzes = AppCompilerTest.createClazzes(impl1, impl2);
        Clazz interfaceClazz = clazzes.load("java/lang/Number");
        Set<Clazz> compiled = new HashSet<>();
        Set<Clazz> queue = new LinkedHashSet<>();
        AppCompiler.addMetaInfImplementations(clazzes, interfaceClazz, compiled, queue);
        Assert.assertEquals(("Two items added to queue: " + queue), 2, queue.size());
        Assert.assertTrue(("Integer in queue" + queue), queue.contains(clazzes.load("java/lang/Integer")));
        Assert.assertTrue(("Long in queue" + queue), queue.contains(clazzes.load("java/lang/Long")));
    }

    @Test
    public void testMultilineFile() throws Exception {
        final Path impl1 = new AppCompilerTest.MockPath("META-INF/services/java.lang.Number", ("# first register Integer\n" + ((("java.lang.Integer\n" + "# then add Long\n") + "java.lang.Long\n") + "\n\n\n\n")));
        Clazzes clazzes = AppCompilerTest.createClazzes(impl1);
        Clazz interfaceClazz = clazzes.load("java/lang/Number");
        Set<Clazz> compiled = new HashSet<>();
        Set<Clazz> queue = new LinkedHashSet<>();
        AppCompiler.addMetaInfImplementations(clazzes, interfaceClazz, compiled, queue);
        Assert.assertEquals(("Two items added to queue: " + queue), 2, queue.size());
        Assert.assertTrue(("Integer in queue" + queue), queue.contains(clazzes.load("java/lang/Integer")));
        Assert.assertTrue(("Long in queue" + queue), queue.contains(clazzes.load("java/lang/Long")));
    }

    @Test
    public void testMissingImplIsIgnore() throws Exception {
        final Path impl1 = new AppCompilerTest.MockPath("META-INF/services/java.lang.Number", "java.lang.Integer");
        final Path impl2 = new AppCompilerTest.MockPath("META-INF/services/java.lang.Number", "nobody.knows.such.Class");
        Clazzes clazzes = AppCompilerTest.createClazzes(impl1, impl2);
        Clazz interfaceClazz = clazzes.load("java/lang/Number");
        Set<Clazz> compiled = new HashSet<>();
        Set<Clazz> queue = new LinkedHashSet<>();
        AppCompiler.addMetaInfImplementations(clazzes, interfaceClazz, compiled, queue);
        Assert.assertEquals(("Just one item added to queue: " + queue), 1, queue.size());
        Assert.assertTrue(("Integer in queue" + queue), queue.contains(clazzes.load("java/lang/Integer")));
    }

    @Test
    public void allStreamsAreClosedInCaseOfFailure() throws Exception {
        final AppCompilerTest.MockPath impl1 = new AppCompilerTest.MockPath("META-INF/services/java.lang.Number", "java.lang.Integer");
        impl1.toThrow = new IOException();
        final AppCompilerTest.MockPath impl2 = new AppCompilerTest.MockPath("META-INF/services/java.lang.Number", "nobody.knows.such.Class");
        Clazzes clazzes = AppCompilerTest.createClazzes(impl1, impl2);
        Clazz interfaceClazz = clazzes.load("java/lang/Number");
        Set<Clazz> compiled = new HashSet<>();
        Set<Clazz> queue = new LinkedHashSet<>();
        try {
            AppCompiler.addMetaInfImplementations(clazzes, interfaceClazz, compiled, queue);
            Assert.fail("Should throw an exception");
        } catch (IOException ex) {
            Assert.assertSame("Our exception is thrown", impl1.toThrow, ex);
        }
        Assert.assertTrue("First stream is closed", impl1.closed);
        Assert.assertTrue("Second stream is closed", impl2.closed);
    }

    private static final class MockPath implements Path {
        private final String file;

        private final String content;

        private IOException toThrow;

        private boolean closed;

        public MockPath(String file, String content) {
            this.file = file;
            this.content = content;
        }

        @Override
        public int getIndex() {
            throw new UnsupportedOperationException();
        }

        @Override
        public File getFile() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<Clazz> listClasses() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Clazz loadGeneratedClass(String internalName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public File getGeneratedClassFile(String internalName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasChangedSince(long timestamp) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isInBootClasspath() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(String file) {
            return this.file.equals(file);
        }

        @Override
        public InputStream open(String file) throws IOException {
            if (this.file.equals(file)) {
                ByteArrayInputStream is = new ByteArrayInputStream(this.content.getBytes("UTF-8"));
                return new FilterInputStream(is) {
                    @Override
                    public synchronized int read(byte[] b, int off, int len) throws IOException {
                        if ((toThrow) != null) {
                            throw toThrow;
                        }
                        return super.read(b, off, len);
                    }

                    @Override
                    public void close() throws IOException {
                        closed = true;
                        super.close();
                    }
                };
            }
            throw new IOException();
        }
    }
}

