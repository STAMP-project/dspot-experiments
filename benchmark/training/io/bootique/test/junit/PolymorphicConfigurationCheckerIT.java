/**
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.bootique.test.junit;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.bootique.config.PolymorphicConfiguration;
import org.junit.Test;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;


public class PolymorphicConfigurationCheckerIT {
    @Test(expected = AssertionError.class)
    public void test_NotInServiceLoader() {
        // intentionally tricking Java type boundary checks
        Class c1 = PolymorphicConfigurationCheckerIT.C1.class;
        Class c2 = PolymorphicConfigurationCheckerIT.C2.class;
        PolymorphicConfigurationChecker.testNoDefault(c1, c2);
    }

    @Test(expected = AssertionError.class)
    public void testNoDefault_NotInServiceLoader() {
        // intentionally tricking Java type boundary checks
        Class c1 = PolymorphicConfigurationCheckerIT.C1.class;
        Class c2 = PolymorphicConfigurationCheckerIT.C2.class;
        PolymorphicConfigurationChecker.testNoDefault(c1, c2);
    }

    @Test
    public void test_Success() {
        PolymorphicConfigurationChecker.test(PolymorphicConfigurationCheckerIT.C3.class, PolymorphicConfigurationCheckerIT.C4.class, PolymorphicConfigurationCheckerIT.C5.class);
    }

    @Test
    public void test_Success_AbstractSuper() {
        PolymorphicConfigurationChecker.test(PolymorphicConfigurationCheckerIT.C12.class, PolymorphicConfigurationCheckerIT.C13.class);
    }

    @Test
    public void testNoDefault_Success() {
        PolymorphicConfigurationChecker.testNoDefault(PolymorphicConfigurationCheckerIT.C6.class, PolymorphicConfigurationCheckerIT.C7.class, PolymorphicConfigurationCheckerIT.C8.class);
    }

    @Test(expected = AssertionError.class)
    public void testNoDefault_BadDefault() {
        PolymorphicConfigurationChecker.testNoDefault(PolymorphicConfigurationCheckerIT.C9.class, PolymorphicConfigurationCheckerIT.C11.class);
    }

    public static class C1 {}

    public static class C2 extends PolymorphicConfigurationCheckerIT.C1 {}

    @JsonTypeInfo(use = NAME, defaultImpl = PolymorphicConfigurationCheckerIT.C4.class)
    @JsonTypeName("c3")
    public static class C3 implements PolymorphicConfiguration {}

    @JsonTypeName("c4")
    public static class C4 extends PolymorphicConfigurationCheckerIT.C3 {}

    @JsonTypeName("c5")
    public static class C5 extends PolymorphicConfigurationCheckerIT.C3 {}

    @JsonTypeInfo(use = NAME)
    @JsonTypeName("c6")
    public static class C6 implements PolymorphicConfiguration {}

    @JsonTypeName("c7")
    public static class C7 extends PolymorphicConfigurationCheckerIT.C6 {}

    @JsonTypeName("c8")
    public static class C8 extends PolymorphicConfigurationCheckerIT.C6 {}

    @JsonTypeInfo(use = NAME, defaultImpl = PolymorphicConfigurationCheckerIT.C10.class)
    public static class C9 implements PolymorphicConfiguration {}

    @JsonTypeName("c10")
    public static class C10 extends PolymorphicConfigurationCheckerIT.C9 {}

    @JsonTypeName("c11")
    public static class C11 extends PolymorphicConfigurationCheckerIT.C9 {}

    @JsonTypeInfo(use = NAME, defaultImpl = PolymorphicConfigurationCheckerIT.C13.class)
    public abstract static class C12 implements PolymorphicConfiguration {}

    @JsonTypeName("c13")
    public static class C13 extends PolymorphicConfigurationCheckerIT.C12 {}
}

