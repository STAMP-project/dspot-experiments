/**
 * Copyright (C) 2011 The Android Open Source Project
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
package libcore.java.lang.reflect.annotations;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import junit.framework.TestCase;


/* J2ObjC removed.
import dalvik.system.VMRuntime;
 */
/**
 * Tests for the behavior of Annotation instances at runtime.
 */
/* J2ObjC removed.
public void testRetentionPolicy() {
// b/29500035
int savedTargetSdkVersion = VMRuntime.getRuntime().getTargetSdkVersion();
try {
// Test N and later behavior
VMRuntime.getRuntime().setTargetSdkVersion(24);
Annotation classRetentionAnnotation =
RetentionAnnotations.class.getAnnotation(ClassRetentionAnnotation.class);
assertNull(classRetentionAnnotation);

// Test pre-N behavior
VMRuntime.getRuntime().setTargetSdkVersion(23);
classRetentionAnnotation =
RetentionAnnotations.class.getAnnotation(ClassRetentionAnnotation.class);
assertNotNull(classRetentionAnnotation);
} finally {
VMRuntime.getRuntime().setTargetSdkVersion(savedTargetSdkVersion);
}
assertNotNull(RetentionAnnotations.class.getAnnotation(RuntimeRetentionAnnotation.class));
assertNull(RetentionAnnotations.class.getAnnotation(SourceRetentionAnnotation.class));
}
 */
public class AnnotationsTest extends TestCase {
    enum Breakfast {

        WAFFLES,
        PANCAKES;}

    @Retention(RetentionPolicy.RUNTIME)
    public @interface HasDefaultsAnnotation {
        byte a() default 5;

        short b() default 6;

        int c() default 7;

        long d() default 8;

        float e() default 9.0F;

        double f() default 10.0;

        char g() default 'k';

        boolean h() default true;

        AnnotationsTest.Breakfast i() default AnnotationsTest.Breakfast.WAFFLES;

        AnnotatedElementTestSupport.AnnotationA j() default @AnnotatedElementTestSupport.AnnotationA;

        String k() default "maple";

        Class l() default AnnotatedElementTestSupport.AnnotationB.class;

        int[] m() default { 1, 2, 3 };

        AnnotationsTest.Breakfast[] n() default { AnnotationsTest.Breakfast.WAFFLES, AnnotationsTest.Breakfast.PANCAKES };

        AnnotationsTest.Breakfast o();

        int p();
    }

    public void testAnnotationDefaults() throws Exception {
        TestCase.assertEquals(((byte) (5)), AnnotationsTest.defaultValue("a"));
        TestCase.assertEquals(((short) (6)), AnnotationsTest.defaultValue("b"));
        TestCase.assertEquals(7, AnnotationsTest.defaultValue("c"));
        TestCase.assertEquals(8L, AnnotationsTest.defaultValue("d"));
        TestCase.assertEquals(9.0F, AnnotationsTest.defaultValue("e"));
        TestCase.assertEquals(10.0, AnnotationsTest.defaultValue("f"));
        TestCase.assertEquals('k', AnnotationsTest.defaultValue("g"));
        TestCase.assertEquals(true, AnnotationsTest.defaultValue("h"));
        TestCase.assertEquals(AnnotationsTest.Breakfast.WAFFLES, AnnotationsTest.defaultValue("i"));
        TestCase.assertEquals((("@" + (AnnotatedElementTestSupport.AnnotationA.class.getName())) + "()"), AnnotationsTest.defaultValue("j").toString());
        TestCase.assertEquals("maple", AnnotationsTest.defaultValue("k"));
        TestCase.assertEquals(AnnotatedElementTestSupport.AnnotationB.class, AnnotationsTest.defaultValue("l"));
        TestCase.assertEquals("[1, 2, 3]", Arrays.toString(((int[]) (AnnotationsTest.defaultValue("m")))));
        TestCase.assertEquals("[WAFFLES, PANCAKES]", Arrays.toString(((AnnotationsTest.Breakfast[]) (AnnotationsTest.defaultValue("n")))));
        TestCase.assertEquals(null, AnnotationsTest.defaultValue("o"));
        TestCase.assertEquals(null, AnnotationsTest.defaultValue("p"));
    }

    @Retention(RetentionPolicy.CLASS)
    public @interface ClassRetentionAnnotation {}

    @Retention(RetentionPolicy.RUNTIME)
    public @interface RuntimeRetentionAnnotation {}

    @Retention(RetentionPolicy.SOURCE)
    public @interface SourceRetentionAnnotation {}

    @AnnotationsTest.ClassRetentionAnnotation
    @AnnotationsTest.RuntimeRetentionAnnotation
    @AnnotationsTest.SourceRetentionAnnotation
    public static class RetentionAnnotations {}
}

