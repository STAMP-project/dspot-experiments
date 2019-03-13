/**
 * Copyright (C) 2014 Google Inc.
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
package com.google.auto.value;


import com.google.auto.value.enums.MyEnum;
import com.google.common.testing.EqualsTester;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 *
 *
 * @author emcmanus@google.com (?amonn McManus)
 */
@RunWith(JUnit4.class)
public class AutoAnnotationDefaultsTest {
    @Retention(RetentionPolicy.RUNTIME)
    @interface EverythingWithDefaults {
        byte aByte() default 1;

        short aShort() default 2;

        int anInt() default 3;

        long aLong() default Long.MAX_VALUE;

        float aFloat() default Float.MAX_VALUE;

        double aDouble() default -(Double.MAX_VALUE);

        char aChar() default '#';

        boolean aBoolean() default true;

        String aString() default "maybe\nmaybe not\n";

        // ensure the formatter doesn't eat spaces
        String spaces() default "  ( x ) , ( y )";

        MyEnum anEnum() default MyEnum.TWO;

        byte[] bytes() default { -1, 0, 1 };

        short[] shorts() default { -2, 0, 2 };

        int[] ints() default {  };

        long[] longs() default { -(Long.MAX_VALUE) };

        float[] floats() default { -(Float.MIN_VALUE), Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NaN };

        double[] doubles() default { -(Double.MIN_VALUE), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NaN };

        char[] chars() default { 'f', '\n', '\uffef' };

        boolean[] booleans() default { false, true };

        String[] strings() default { "", "\uffef\n" };

        MyEnum[] enums() default { MyEnum.ONE, MyEnum.TWO };
    }

    @Test
    public void testDefaults() throws Exception {
        @AutoAnnotationDefaultsTest.EverythingWithDefaults
        class Annotated {}
        AutoAnnotationDefaultsTest.EverythingWithDefaults expected = Annotated.class.getAnnotation(AutoAnnotationDefaultsTest.EverythingWithDefaults.class);
        AutoAnnotationDefaultsTest.EverythingWithDefaults actual = AutoAnnotationDefaultsTest.newEverythingWithDefaults();
        // Iterate over the annotation members to see if any differ. We could just compare expected and
        // actual but if the comparison failed it could be hard to see exactly what differed.
        StringBuilder differencesBuilder = new StringBuilder();
        for (Method member : AutoAnnotationDefaultsTest.EverythingWithDefaults.class.getDeclaredMethods()) {
            String name = member.getName();
            Object expectedValue = member.invoke(expected);
            Object actualValue = member.invoke(actual);
            if (!(AutoAnnotationDefaultsTest.equal(expectedValue, actualValue))) {
                differencesBuilder.append("For ").append(name).append(" expected <").append(AutoAnnotationDefaultsTest.string(expectedValue)).append("> but was <").append(AutoAnnotationDefaultsTest.string(actualValue)).append(">\n");
            }
        }
        String differences = differencesBuilder.toString();
        Assert.assertTrue(differences, differences.isEmpty());
        // All the members were the same. Check that the equals and hashCode results say so too.
        new EqualsTester().addEqualityGroup(expected, actual).testEquals();
    }
}

