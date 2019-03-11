/**
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.codegen;


import com.SomeClass;
import java.io.IOException;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class ClassPathUtilsTest {
    @Test
    public void scanPackage() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Set<Class<?>> classes = ClassPathUtils.scanPackage(classLoader, SomeClass.class.getPackage());
        Assert.assertFalse(classes.isEmpty());
    }

    @Test
    public void scanPackage_check_initialized() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Set<Class<?>> classes = ClassPathUtils.scanPackage(classLoader, getClass().getPackage());
        Assert.assertFalse(classes.isEmpty());
        Assert.assertEquals("XXX", SomeOtherClass2.property);
    }

    @Test
    public void safeClassForName() {
        Assert.assertNull(safeForName("com.sun.nio.file.ExtendedOpenOption"));
        Assert.assertNotNull(safeForName("com.suntanning.ShouldBeLoaded"));
        Assert.assertNotNull(safeForName("com.applejuice.ShouldBeLoaded"));
    }
}

