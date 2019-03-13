/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.harmony.annotation.tests.java.lang.annotation;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;


/**
 * Test case of java.lang.annotation.Annotation
 */
public class AnnotationTest extends TestCase {
    public void test_annotationType() {
        Annotation[] annotations = AnnotatedClass.class.getDeclaredAnnotations();
        TestCase.assertEquals(1, annotations.length);
        Annotation anno = annotations[0];
        TestCase.assertEquals(TestAnnotation1.class, anno.annotationType());
    }

    public void test_equals() throws Exception {
        // test type
        Method m1 = AnnotatedClass2.class.getDeclaredMethod("a", new Class[]{  });
        Method m2 = AnnotatedClass2.class.getDeclaredMethod("b", new Class[]{  });
        TestCase.assertFalse("other annotation class type", m1.getDeclaredAnnotations()[0].equals(m2.getDeclaredAnnotations()[0]));
        // test equality / non equality for base types and compound types
        List<Method> methods = Arrays.asList(AnnotatedClass.class.getDeclaredMethods());
        Map<String, List<Method>> eqs = new HashMap<String, List<Method>>();
        Map<String, List<Method>> neqs = new HashMap<String, List<Method>>();
        for (Method m : methods) {
            String name = m.getName();
            // System.out.println("name "+name);
            Map<String, List<Method>> curT = ((name.charAt(0)) == 'e') ? eqs : neqs;
            String testNum = name.substring(1, 3);// 01

            List<Method> mlist = curT.get(testNum);
            if (mlist == null) {
                mlist = new ArrayList<Method>();
                curT.put(testNum, mlist);
            }
            mlist.add(AnnotatedClass.class.getDeclaredMethod(name, new Class[]{  }));
        }
        for (List<Method> eqList : eqs.values()) {
            for (int i = 0; i < ((eqList.size()) - 1); i++) {
                for (int j = i + 1; j < (eqList.size()); j++) {
                    Method me1 = eqList.get(i);
                    Method me2 = eqList.get(j);
                    // System.out.println("eq test for "+me1.getName()+", "+me2.getName());
                    Annotation a1 = me1.getDeclaredAnnotations()[0];
                    Annotation a2 = me2.getDeclaredAnnotations()[0];
                    TestCase.assertEquals(((("must be equal : method1:" + (me1.getName())) + ", method2: ") + (me2.getName())), a1, a2);
                    TestCase.assertEquals("same hashcode", a1.hashCode(), a2.hashCode());
                }
            }
        }
        for (List<Method> eqList : neqs.values()) {
            for (int i = 0; i < ((eqList.size()) - 1); i++) {
                for (int j = i + 1; j < (eqList.size()); j++) {
                    Method me1 = eqList.get(i);
                    Method me2 = eqList.get(j);
                    Annotation a1 = me1.getDeclaredAnnotations()[0];
                    Annotation a2 = me2.getDeclaredAnnotations()[0];
                    // System.out.println("ne test for "+me1.getName()+", "+me2.getName());
                    TestCase.assertFalse(((("must not be equal : method1:" + (me1.getName())) + ", method2: ") + (me2.getName())), a1.equals(a2));
                    if ((a1.hashCode()) != (a2.hashCode())) {
                        TestCase.assertFalse("not same hashcode -> not equals", a1.equals(a2));
                    }
                }
            }
        }
    }

    public void test_hashCode() throws NoSuchMethodException, SecurityException {
        Annotation a1 = AnnotatedClass.class.getDeclaredAnnotations()[0];
        TestCase.assertEquals(a1.hashCode(), ((127 * ("value".hashCode())) ^ ("foobar".hashCode())));
        // i+= 127 *(key.hashCode() ^ memberValHashCode(value);
        Method m1 = AnnotatedClass.class.getDeclaredMethod("e34c", new Class[]{  });
        int arrHc = Arrays.hashCode(new Object[]{  });
        /* TestAnnotation3[] arrAnno() default {};
        String[] arrString() default {};
        Class[] arrClass() default {};
        TestEnum1[] arrEnum() default {};
         */
        TestCase.assertEquals((((((127 * ("arrAnno".hashCode())) ^ arrHc) + ((127 * ("arrString".hashCode())) ^ arrHc)) + ((127 * ("arrClass".hashCode())) ^ arrHc)) + ((127 * ("arrEnum".hashCode())) ^ arrHc)), m1.getDeclaredAnnotations()[0].hashCode());
        Method m2 = AnnotatedClass3.class.getDeclaredMethod("a", new Class[]{  });
        TestCase.assertEquals(((127 * ("i".hashCode())) ^ 12345), m2.getDeclaredAnnotations()[0].hashCode());
    }

    public static void test35304() throws Exception {
        Class c = AnnotationTest.class;
        Class[] parameterTypes = new Class[]{ String.class, String.class };
        Annotation[][] annotations = c.getDeclaredMethod("test35304_method", parameterTypes).getParameterAnnotations();
        TestCase.assertEquals(2, annotations.length);// Two parameters.

        TestCase.assertEquals(0, annotations[0].length);// No annotations on the first.

        TestCase.assertEquals(1, annotations[1].length);// One annotation on the second.

    }
}

