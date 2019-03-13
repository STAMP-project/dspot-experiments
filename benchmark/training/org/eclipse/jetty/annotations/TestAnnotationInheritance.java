/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.annotations;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.annotations.AnnotationParser.AbstractHandler;
import org.eclipse.jetty.annotations.AnnotationParser.ClassInfo;
import org.eclipse.jetty.annotations.AnnotationParser.FieldInfo;
import org.eclipse.jetty.annotations.AnnotationParser.MethodInfo;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 */
public class TestAnnotationInheritance {
    List<String> classNames = new ArrayList<String>();

    class SampleHandler extends AbstractHandler {
        public final List<String> annotatedClassNames = new ArrayList<String>();

        public final List<String> annotatedMethods = new ArrayList<String>();

        public final List<String> annotatedFields = new ArrayList<String>();

        @Override
        public void handle(ClassInfo info, String annotation) {
            if ((annotation == null) || (!("org.eclipse.jetty.annotations.Sample".equals(annotation))))
                return;

            annotatedClassNames.add(info.getClassName());
        }

        @Override
        public void handle(FieldInfo info, String annotation) {
            if ((annotation == null) || (!("org.eclipse.jetty.annotations.Sample".equals(annotation))))
                return;

            annotatedFields.add((((info.getClassInfo().getClassName()) + ".") + (info.getFieldName())));
        }

        @Override
        public void handle(MethodInfo info, String annotation) {
            if ((annotation == null) || (!("org.eclipse.jetty.annotations.Sample".equals(annotation))))
                return;

            annotatedMethods.add((((info.getClassInfo().getClassName()) + ".") + (info.getMethodName())));
        }

        @Override
        public String toString() {
            return ((annotatedClassNames.toString()) + (annotatedMethods)) + (annotatedFields);
        }
    }

    @Test
    public void testParseClassNames() throws Exception {
        classNames.add(ClassA.class.getName());
        classNames.add(ClassB.class.getName());
        TestAnnotationInheritance.SampleHandler handler = new TestAnnotationInheritance.SampleHandler();
        AnnotationParser parser = new AnnotationParser();
        parser.parse(Collections.singleton(handler), classNames);
        // check we got  2 class annotations
        Assertions.assertEquals(2, handler.annotatedClassNames.size());
        // check we got all annotated methods on each class
        Assertions.assertEquals(7, handler.annotatedMethods.size());
        Assertions.assertTrue(handler.annotatedMethods.contains("org.eclipse.jetty.annotations.ClassA.a"));
        Assertions.assertTrue(handler.annotatedMethods.contains("org.eclipse.jetty.annotations.ClassA.b"));
        Assertions.assertTrue(handler.annotatedMethods.contains("org.eclipse.jetty.annotations.ClassA.c"));
        Assertions.assertTrue(handler.annotatedMethods.contains("org.eclipse.jetty.annotations.ClassA.d"));
        Assertions.assertTrue(handler.annotatedMethods.contains("org.eclipse.jetty.annotations.ClassA.l"));
        Assertions.assertTrue(handler.annotatedMethods.contains("org.eclipse.jetty.annotations.ClassB.a"));
        Assertions.assertTrue(handler.annotatedMethods.contains("org.eclipse.jetty.annotations.ClassB.c"));
        // check we got all annotated fields on each class
        Assertions.assertEquals(1, handler.annotatedFields.size());
        Assertions.assertEquals("org.eclipse.jetty.annotations.ClassA.m", handler.annotatedFields.get(0));
    }

    @Test
    public void testParseClass() throws Exception {
        TestAnnotationInheritance.SampleHandler handler = new TestAnnotationInheritance.SampleHandler();
        AnnotationParser parser = new AnnotationParser();
        parser.parse(Collections.singleton(handler), ClassB.class, true);
        // check we got  2 class annotations
        Assertions.assertEquals(2, handler.annotatedClassNames.size());
        // check we got all annotated methods on each class
        Assertions.assertEquals(7, handler.annotatedMethods.size());
        Assertions.assertTrue(handler.annotatedMethods.contains("org.eclipse.jetty.annotations.ClassA.a"));
        Assertions.assertTrue(handler.annotatedMethods.contains("org.eclipse.jetty.annotations.ClassA.b"));
        Assertions.assertTrue(handler.annotatedMethods.contains("org.eclipse.jetty.annotations.ClassA.c"));
        Assertions.assertTrue(handler.annotatedMethods.contains("org.eclipse.jetty.annotations.ClassA.d"));
        Assertions.assertTrue(handler.annotatedMethods.contains("org.eclipse.jetty.annotations.ClassA.l"));
        Assertions.assertTrue(handler.annotatedMethods.contains("org.eclipse.jetty.annotations.ClassB.a"));
        Assertions.assertTrue(handler.annotatedMethods.contains("org.eclipse.jetty.annotations.ClassB.c"));
        // check we got all annotated fields on each class
        Assertions.assertEquals(1, handler.annotatedFields.size());
        Assertions.assertEquals("org.eclipse.jetty.annotations.ClassA.m", handler.annotatedFields.get(0));
    }

    @Test
    public void testTypeInheritanceHandling() throws Exception {
        Map<String, Set<String>> map = new ConcurrentHashMap<>();
        AnnotationParser parser = new AnnotationParser();
        ClassInheritanceHandler handler = new ClassInheritanceHandler(map);
        class Foo implements InterfaceD {}
        classNames.clear();
        classNames.add(ClassA.class.getName());
        classNames.add(ClassB.class.getName());
        classNames.add(InterfaceD.class.getName());
        classNames.add(Foo.class.getName());
        parser.parse(Collections.singleton(handler), classNames);
        Assertions.assertNotNull(map);
        Assertions.assertFalse(map.isEmpty());
        Assertions.assertEquals(2, map.size());
        MatcherAssert.assertThat(map, Matchers.hasKey("org.eclipse.jetty.annotations.ClassA"));
        MatcherAssert.assertThat(map, Matchers.hasKey("org.eclipse.jetty.annotations.InterfaceD"));
        Set<String> classes = map.get("org.eclipse.jetty.annotations.ClassA");
        MatcherAssert.assertThat(classes, Matchers.contains("org.eclipse.jetty.annotations.ClassB"));
        classes = map.get("org.eclipse.jetty.annotations.InterfaceD");
        MatcherAssert.assertThat(classes, Matchers.containsInAnyOrder("org.eclipse.jetty.annotations.ClassB", Foo.class.getName()));
    }
}

