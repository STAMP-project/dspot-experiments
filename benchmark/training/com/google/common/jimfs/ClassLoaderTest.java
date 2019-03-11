/**
 * Copyright 2015 Google Inc.
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
package com.google.common.jimfs;


import com.google.common.base.MoreObjects;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests behavior when user code loads Jimfs in a separate class loader from the system class
 * loader (which is what {@link FileSystemProvider#installedProviders()} uses to load
 * {@link FileSystemProvider}s as services from the classpath).
 *
 * @author Colin Decker
 */
@RunWith(JUnit4.class)
public class ClassLoaderTest {
    @Test
    public void separateClassLoader() throws Exception {
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader systemLoader = ClassLoader.getSystemClassLoader();
        ClassLoader loader = MoreObjects.firstNonNull(contextLoader, systemLoader);
        if (loader instanceof URLClassLoader) {
            // Anything we can do if it isn't a URLClassLoader?
            URLClassLoader urlLoader = ((URLClassLoader) (loader));
            ClassLoader separateLoader = new URLClassLoader(urlLoader.getURLs(), systemLoader.getParent());// either null or the boostrap loader

            Thread.currentThread().setContextClassLoader(separateLoader);
            try {
                Class<?> thisClass = separateLoader.loadClass(getClass().getName());
                Method createFileSystem = thisClass.getDeclaredMethod("createFileSystem");
                // First, the call to Jimfs.newFileSystem in createFileSystem needs to succeed
                Object fs = createFileSystem.invoke(null);
                // Next, some sanity checks:
                // The file system is a JimfsFileSystem
                Assert.assertEquals("com.google.common.jimfs.JimfsFileSystem", fs.getClass().getName());
                // But it is not seen as an instance of JimfsFileSystem here because it was loaded by a
                // different ClassLoader
                Assert.assertFalse((fs instanceof JimfsFileSystem));
                // But it should be an instance of FileSystem regardless, which is the important thing.
                Assert.assertTrue((fs instanceof FileSystem));
                // And normal file operations should work on it despite its provenance from a different
                // ClassLoader
                ClassLoaderTest.writeAndRead(((FileSystem) (fs)), "bar.txt", "blah blah");
                // And for the heck of it, test the contents of the file that was created in
                // createFileSystem too
                Assert.assertEquals("blah", Files.readAllLines(((FileSystem) (fs)).getPath("foo.txt"), StandardCharsets.UTF_8).get(0));
            } finally {
                Thread.currentThread().setContextClassLoader(contextLoader);
            }
        }
    }
}

