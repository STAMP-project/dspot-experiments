/**
 * This file is part of ClassGraph.
 *
 * Author: Luke Hutchison
 *
 * Hosted at: https://github.com/classgraph/classgraph
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Luke Hutchison
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.classgraph.issues.issue209;


import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import java.net.URL;
import java.net.URLClassLoader;
import org.junit.Test;


/**
 * The Class Issue209Test.
 */
public class Issue209Test {
    /**
     * Test spring boot jar with lib jars.
     */
    @Test
    public void testSpringBootJarWithLibJars() {
        try (ScanResult result = // 
        // 
        // 
        new ClassGraph().whitelistPackages("org.springframework.boot.loader.util", "com.foo", "issue209lib").overrideClassLoaders(new URLClassLoader(new URL[]{ Issue209Test.class.getClassLoader().getResource("issue209.jar") })).scan()) {
            // Test reading from /
            // Test reading from /BOOT-INF/classes
            // Test reading from /BOOT-INF/lib/*.jar
            assertThat(result.getAllClasses().getNames()).containsExactlyInAnyOrder("org.springframework.boot.loader.util.SystemPropertyUtils", "com.foo.externalApp.ExternalAppApplication", "com.foo.externalApp.SomeClass", "issue209lib.Issue209Lib");
            // Test classloading
            assertThat(result.getClassInfo("org.springframework.boot.loader.util.SystemPropertyUtils").loadClass()).isNotNull();
            assertThat(result.getClassInfo("com.foo.externalApp.ExternalAppApplication").loadClass()).isNotNull();
            assertThat(result.getClassInfo("com.foo.externalApp.SomeClass").loadClass()).isNotNull();
            assertThat(result.getClassInfo("issue209lib.Issue209Lib").loadClass()).isNotNull();
        }
    }
}

