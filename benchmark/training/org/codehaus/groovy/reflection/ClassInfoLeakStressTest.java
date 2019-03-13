/**
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.codehaus.groovy.reflection;


import groovy.lang.GroovyClassLoader;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import org.apache.groovy.stress.util.GCUtils;
import org.codehaus.groovy.util.ReferenceBundle;
import org.junit.Assert;
import org.junit.Test;


public class ClassInfoLeakStressTest {
    private static final int NUM_OBJECTS = 3101;

    private static ReferenceBundle bundle = ReferenceBundle.getWeakBundle();

    private ReferenceQueue<ClassLoader> classLoaderQueue = new ReferenceQueue<ClassLoader>();

    private ReferenceQueue<Class<?>> classQueue = new ReferenceQueue<Class<?>>();

    private ReferenceQueue<ClassInfo> classInfoQueue = new ReferenceQueue<ClassInfo>();

    // Used to keep a hard reference to the References so they are not collected
    private List<Reference<?>> refList = new ArrayList<Reference<?>>(((ClassInfoLeakStressTest.NUM_OBJECTS) * 3));

    @Test
    public void testLeak() {
        Assert.assertFalse(Boolean.getBoolean("groovy.use.classvalue"));
        for (int i = 0; i < (ClassInfoLeakStressTest.NUM_OBJECTS); i++) {
            GroovyClassLoader gcl = new GroovyClassLoader();
            Class scriptClass = gcl.parseClass(("int myvar = " + i));
            ClassInfo ci = ClassInfo.getClassInfo(scriptClass);
            Reference<ClassLoader> classLoaderRef = new WeakReference<ClassLoader>(gcl, classLoaderQueue);
            Reference<Class<?>> classRef = new WeakReference<Class<?>>(scriptClass, classQueue);
            Reference<ClassInfo> classInfoRef = new WeakReference<ClassInfo>(ci, classInfoQueue);
            refList.add(classLoaderRef);
            refList.add(classRef);
            refList.add(classInfoRef);
            gcl = null;
            scriptClass = null;
            ci = null;
            GCUtils.gc();
        }
        // Add new class to help evict the last collected entry
        GroovyClassLoader gcl = new GroovyClassLoader();
        Class scriptClass = gcl.parseClass("int myvar = 7777");
        ClassInfo ci = ClassInfo.getClassInfo(scriptClass);
        GCUtils.gc();
        // All objects should have been collected
        Assert.assertEquals("GroovyClassLoaders not collected by GC", ClassInfoLeakStressTest.NUM_OBJECTS, queueSize(classLoaderQueue));
        Assert.assertEquals("Script Classes not collected by GC", ClassInfoLeakStressTest.NUM_OBJECTS, queueSize(classQueue));
        int ciSize = queueSize(classInfoQueue);
        Assert.assertEquals((((("ClassInfo objects [" + ciSize) + "] collected by GC, expected [") + (ClassInfoLeakStressTest.NUM_OBJECTS)) + "]"), ClassInfoLeakStressTest.NUM_OBJECTS, ciSize);
    }
}

