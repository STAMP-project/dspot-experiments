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
package io.github.classgraph.issues.issue153;


import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationParameterValue;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.junit.Test;


/**
 * The Class Issue153Test.
 */
@Issue153Test.StringAnnotation("classlabel")
@Issue153Test.TwoParamAnnotation(value1 = 'x', value2 = { 1, 2, 3 })
@Issue153Test.EnumAnnotation(Issue153Test.FruitEnum.BANANA)
@Issue153Test.NestedAnnotation({ @Issue153Test.StringAnnotation("one"), @Issue153Test.StringAnnotation("two") })
@Issue153Test.ClassRefAnnotation(Issue153Test.class)
public class Issue153Test {
    /**
     * The Interface StringAnnotation.
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface StringAnnotation {
        /**
         * Value.
         *
         * @return the string
         */
        String value();
    }

    /**
     * The Interface TwoParamAnnotation.
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TwoParamAnnotation {
        /**
         * Value 1.
         *
         * @return the char
         */
        char value1();

        /**
         * Value 2.
         *
         * @return the int[]
         */
        int[] value2();
    }

    /**
     * The Enum FruitEnum.
     */
    public enum FruitEnum {

        /**
         * The apple.
         */
        APPLE,
        /**
         * The banana.
         */
        BANANA;}

    /**
     * The Interface EnumAnnotation.
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface EnumAnnotation {
        /**
         * Value.
         *
         * @return the fruit enum
         */
        Issue153Test.FruitEnum value();
    }

    /**
     * The Interface NestedAnnotation.
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface NestedAnnotation {
        /**
         * Value.
         *
         * @return the string annotation[]
         */
        Issue153Test.StringAnnotation[] value();
    }

    /**
     * The Interface ClassRefAnnotation.
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ClassRefAnnotation {
        /**
         * Value.
         *
         * @return the class
         */
        Class<?> value();
    }

    /**
     * The Constant testField.
     */
    @Issue153Test.StringAnnotation("fieldlabel")
    public static final Issue153Test.FruitEnum testField = Issue153Test.FruitEnum.BANANA;

    /**
     * The Interface AnnotationWithAndWithoutDefaultValue.
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface AnnotationWithAndWithoutDefaultValue {
        /**
         * Value without default.
         *
         * @return the string
         */
        String valueWithoutDefault();

        /**
         * Value with default.
         *
         * @return the int
         */
        int valueWithDefault() default 5;
    }

    /**
     * The Interface AnnotationWithOnlyDefaultValue.
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface AnnotationWithOnlyDefaultValue {
        /**
         * Value.
         *
         * @return the int
         */
        int value() default 6;
    }

    /**
     * The test field with and witout default.
     */
    @Issue153Test.AnnotationWithAndWithoutDefaultValue(valueWithoutDefault = "x")
    public static int testFieldWithAndWitoutDefault;

    /**
     * The test field with only default.
     */
    @Issue153Test.AnnotationWithOnlyDefaultValue
    public static int testFieldWithOnlyDefault;

    /**
     * The Constant pkg.
     */
    private static final String pkg = Issue153Test.class.getPackage().getName();

    /**
     * Class annotation parameters.
     */
    @Test
    public void classAnnotationParameters() {
        try (ScanResult scanResult = // 
        // 
        // 
        // 
        // 
        new ClassGraph().whitelistPackages(Issue153Test.pkg).enableMethodInfo().enableFieldInfo().enableAnnotationInfo().scan()) {
            final ClassInfo classInfo = // 
            scanResult.getClassInfo(Issue153Test.class.getName());
            // Read class annotation parameters
            // 
            // 
            // 
            // 
            // 
            assertThat(classInfo.getAnnotationInfo().getAsStrings()).containsExactlyInAnyOrder((("@" + (Issue153Test.StringAnnotation.class.getName())) + "(\"classlabel\")"), (("@" + (Issue153Test.TwoParamAnnotation.class.getName())) + "(value1='x', value2=[1, 2, 3])"), ((((("@" + (Issue153Test.EnumAnnotation.class.getName())) + "(") + (Issue153Test.FruitEnum.class.getName())) + ".BANANA") + ")"), (((((("@" + (Issue153Test.NestedAnnotation.class.getName())) + "([@") + (Issue153Test.StringAnnotation.class.getName())) + "(\"one\"), @") + (Issue153Test.StringAnnotation.class.getName())) + "(\"two\")])"), (((("@" + (Issue153Test.ClassRefAnnotation.class.getName())) + "(class ") + (Issue153Test.class.getName())) + ")"));
            // 
            assertThat(classInfo.getFieldInfo("testField").getAnnotationInfo().getAsStrings()).containsExactly((("@" + (Issue153Test.StringAnnotation.class.getName())) + "(\"fieldlabel\")"));
            // 
            assertThat(classInfo.getMethodInfo("testMethod").get(0).getAnnotationInfo().getAsStrings()).containsExactly((("@" + (Issue153Test.StringAnnotation.class.getName())) + "(\"methodlabel\")"));
            // 
            assertThat(classInfo.getFieldInfo("testFieldWithAndWitoutDefault").getAnnotationInfo().getAsStrings()).containsExactly((("@" + (Issue153Test.AnnotationWithAndWithoutDefaultValue.class.getName())) + "(valueWithoutDefault=\"x\", valueWithDefault=5)"));
            // 
            assertThat(classInfo.getFieldInfo("testFieldWithOnlyDefault").getAnnotationInfo().getAsStrings()).containsExactly((("@" + (Issue153Test.AnnotationWithOnlyDefaultValue.class.getName())) + "(6)"));
            // Make sure enum constants can be instantiated
            final AnnotationInfo annotation2 = classInfo.getAnnotationInfo().get(Issue153Test.EnumAnnotation.class.getName());
            final AnnotationParameterValue annotationParam0 = annotation2.getParameterValues().get(0);
            final Object bananaRef = loadClassAndReturnEnumValue();
            assertThat(bananaRef.getClass()).isEqualTo(Issue153Test.FruitEnum.class);
            assertThat(bananaRef.toString()).isEqualTo(Issue153Test.FruitEnum.BANANA.toString());
        }
    }
}

