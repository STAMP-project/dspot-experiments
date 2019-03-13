/**
 * Copyright (C) 2015 Google Inc.
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
package com.google.auto.value.processor;


import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.truth.Expect;
import java.lang.reflect.Modifier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Validates the assumptions AutoValue makes about Guava immutable collection builders. We expect
 * for each public class {@code com.google.common.collect.ImmutableFoo} that:
 *
 * <ul>
 *   <li>it contains a public nested class {@code ImmutableFoo.Builder} with the same type
 *       parameters;
 *   <li>there is a public static method {@code ImmutableFoo.builder()} that returns {@code ImmutableFoo.Builder};
 *   <li>there is a method {@code ImmutableFoo.Builder.build()} that returns {@code ImmutableFoo};
 *   <li>and there is a method in {@code ImmutableFoo.Builder} called either {@code addAll} or
 *       {@code putAll} with a single parameter to which {@code ImmutableFoo} can be assigned.
 * </ul>
 *
 * @author emcmanus@google.com (?amonn McManus)
 */
@RunWith(JUnit4.class)
public class GuavaCollectionBuildersTest {
    private static final ImmutableSet<String> NON_BUILDABLE_COLLECTIONS = ImmutableSet.of("ImmutableCollection");

    @Rule
    public final Expect expect = Expect.create();

    @Test
    public void testImmutableBuilders() throws Exception {
        ClassPath classPath = ClassPath.from(getClass().getClassLoader());
        ImmutableSet<ClassPath.ClassInfo> classes = classPath.getAllClasses();
        int checked = 0;
        for (ClassPath.ClassInfo classInfo : classes) {
            if (((classInfo.getPackageName().equals("com.google.common.collect")) && (classInfo.getSimpleName().startsWith("Immutable"))) && (!(GuavaCollectionBuildersTest.NON_BUILDABLE_COLLECTIONS.contains(classInfo.getSimpleName())))) {
                Class<?> c = Class.forName(classInfo.getName());
                if (Modifier.isPublic(c.getModifiers())) {
                    checked++;
                    checkImmutableClass(c);
                }
            }
        }
        expect.that(checked).isGreaterThan(10);
    }
}

