/**
 * Copyright 2009-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */


package org.apache.ibatis.type.usesjava8;


/**
 * Tests for auto-detect type handlers of mybatis-typehandlers-jsr310.
 *
 * @author Kazuki Shimizu
 */
public class AmplJsr310TypeHandlerRegistryTest {
    private org.apache.ibatis.type.TypeHandlerRegistry typeHandlerRegistry;

    @org.junit.Before
    public void setup() {
        typeHandlerRegistry = new org.apache.ibatis.type.TypeHandlerRegistry();
    }

    @org.junit.Test
    public void testFor_v1_0_0() throws java.lang.ClassNotFoundException {
        org.junit.Assert.assertThat(getTypeHandler("java.time.Instant"), org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.InstantTypeHandler.class));
        org.junit.Assert.assertThat(getTypeHandler("java.time.LocalDateTime"), org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.LocalDateTimeTypeHandler.class));
        org.junit.Assert.assertThat(getTypeHandler("java.time.LocalDate"), org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.LocalDateTypeHandler.class));
        org.junit.Assert.assertThat(getTypeHandler("java.time.LocalTime"), org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.LocalTimeTypeHandler.class));
        org.junit.Assert.assertThat(getTypeHandler("java.time.OffsetDateTime"), org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.OffsetDateTimeTypeHandler.class));
        org.junit.Assert.assertThat(getTypeHandler("java.time.OffsetTime"), org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.OffsetTimeTypeHandler.class));
        org.junit.Assert.assertThat(getTypeHandler("java.time.ZonedDateTime"), org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.ZonedDateTimeTypeHandler.class));
    }

    @org.junit.Test
    public void testFor_v1_0_1() throws java.lang.ClassNotFoundException {
        org.junit.Assert.assertThat(getTypeHandler("java.time.Month"), org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.MonthTypeHandler.class));
        org.junit.Assert.assertThat(getTypeHandler("java.time.Year"), org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.YearTypeHandler.class));
    }

    @org.junit.Test
    public void testFor_v1_0_2() throws java.lang.ClassNotFoundException {
        org.junit.Assert.assertThat(getTypeHandler("java.time.YearMonth"), org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.YearMonthTypeHandler.class));
        org.junit.Assert.assertThat(getTypeHandler("java.time.chrono.JapaneseDate"), org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.JapaneseDateTypeHandler.class));
    }

    private org.apache.ibatis.type.TypeHandler<?> getTypeHandler(java.lang.String fqcn) throws java.lang.ClassNotFoundException {
        return typeHandlerRegistry.getTypeHandler(org.apache.ibatis.io.Resources.classForName(fqcn));
    }

    /* amplification of org.apache.ibatis.type.usesjava8.Jsr310TypeHandlerRegistryTest#testFor_v1_0_0 */
    @org.junit.Test
    public void testFor_v1_0_0_literalMutation8_failAssert7() throws java.lang.ClassNotFoundException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_19_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.ZonedDateTimeTypeHandler.class);
            // MethodAssertGenerator build local variable
            Object o_16_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.OffsetTimeTypeHandler.class);
            // MethodAssertGenerator build local variable
            Object o_13_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.OffsetDateTimeTypeHandler.class);
            // MethodAssertGenerator build local variable
            Object o_10_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.LocalTimeTypeHandler.class);
            // MethodAssertGenerator build local variable
            Object o_7_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.LocalDateTypeHandler.class);
            // MethodAssertGenerator build local variable
            Object o_4_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.LocalDateTimeTypeHandler.class);
            // MethodAssertGenerator build local variable
            Object o_1_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.InstantTypeHandler.class);
            // MethodAssertGenerator build local variable
            Object o_1_0 = getTypeHandler("java.time.Instant");
            // MethodAssertGenerator build local variable
            Object o_4_0 = getTypeHandler("[gpbL[{$QV5:Wz2[|+mr6#-");
            // MethodAssertGenerator build local variable
            Object o_7_0 = getTypeHandler("java.time.LocalDate");
            // MethodAssertGenerator build local variable
            Object o_10_0 = getTypeHandler("java.time.LocalTime");
            // MethodAssertGenerator build local variable
            Object o_13_0 = getTypeHandler("java.time.OffsetDateTime");
            // MethodAssertGenerator build local variable
            Object o_16_0 = getTypeHandler("java.time.OffsetTime");
            // MethodAssertGenerator build local variable
            Object o_19_0 = getTypeHandler("java.time.ZonedDateTime");
            org.junit.Assert.fail("testFor_v1_0_0_literalMutation8 should have thrown ClassNotFoundException");
        } catch (java.lang.ClassNotFoundException eee) {
        }
    }

    /* amplification of org.apache.ibatis.type.usesjava8.Jsr310TypeHandlerRegistryTest#testFor_v1_0_0 */
    @org.junit.Test
    public void testFor_v1_0_0_literalMutation32_failAssert31_literalMutation897() throws java.lang.ClassNotFoundException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_19_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.ZonedDateTimeTypeHandler.class);
            // MethodAssertGenerator build local variable
            Object o_16_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.OffsetTimeTypeHandler.class);
            // MethodAssertGenerator build local variable
            Object o_13_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.OffsetDateTimeTypeHandler.class);
            // MethodAssertGenerator build local variable
            Object o_10_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.LocalTimeTypeHandler.class);
            // MethodAssertGenerator build local variable
            Object o_7_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.LocalDateTypeHandler.class);
            // MethodAssertGenerator build local variable
            Object o_4_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.LocalDateTimeTypeHandler.class);
            // MethodAssertGenerator build local variable
            Object o_1_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.InstantTypeHandler.class);
            // MethodAssertGenerator build local variable
            Object o_1_0 = getTypeHandler("java.time.Instant");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).toGenericString(), "public class java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getGenericSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getTypeName(), "java.time.Instant");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getName(), "java.time.Instant");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getModifiers(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getModifiers(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).toGenericString(), "public final class java.time.Instant");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getPackage()).getImplementationVersion(), "1.8.0_121");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getPackage()).getSpecificationVersion(), "1.8");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getTypeName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSimpleName(), "Instant");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getPackage()).getImplementationVendor(), "Oracle Corporation");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.security.ProtectionDomain)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getProtectionDomain()).getCodeSource());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.security.ProtectionDomain)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getProtectionDomain()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getTypeName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getSimpleName(), "Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getGenericSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getModifiers(), 17);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getPackage()).getImplementationTitle(), "Java Runtime Environment");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getSimpleName(), "Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).toGenericString(), "public class java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getCanonicalName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getAnnotatedSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getPackage()).getSpecificationVendor(), "Oracle Corporation");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getAnnotatedSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getCanonicalName(), "java.time.Instant");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getPackage()).getSpecificationTitle(), "Java Platform API Specification");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getPackage()).getName(), "java.time");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getPackage()).isSealed());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getCanonicalName(), "java.lang.Object");
            // MethodAssertGenerator build local variable
            Object o_4_0 = getTypeHandler("java.time.LocalDateTime");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getPackage()).getImplementationTitle(), "Java Runtime Environment");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.security.ProtectionDomain)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getProtectionDomain()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getPackage()).getSpecificationVendor(), "Oracle Corporation");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getPackage()).getImplementationVersion(), "1.8.0_121");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSimpleName(), "LocalDateTime");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getTypeName(), "java.time.LocalDateTime");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getAnnotatedSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getGenericSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getSimpleName(), "Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getPackage()).isSealed());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getPackage()).getImplementationVendor(), "Oracle Corporation");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getModifiers(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getPackage()).getSpecificationVersion(), "1.8");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getPackage()).getSpecificationTitle(), "Java Platform API Specification");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getModifiers(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.security.ProtectionDomain)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getProtectionDomain()).getCodeSource());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getCanonicalName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).toGenericString(), "public class java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).toGenericString(), "public final class java.time.LocalDateTime");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getPackage()).getName(), "java.time");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getSimpleName(), "Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).toGenericString(), "public class java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getGenericSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getCanonicalName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getTypeName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getModifiers(), 17);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getCanonicalName(), "java.time.LocalDateTime");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getName(), "java.time.LocalDateTime");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getAnnotatedSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getTypeName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).isMemberClass());
            // MethodAssertGenerator build local variable
            Object o_7_0 = getTypeHandler("java.time.LocalDate");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getCanonicalName(), "java.time.LocalDate");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getAnnotatedSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getTypeName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getModifiers(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getPackage()).getName(), "java.time");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).toGenericString(), "public class java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getTypeName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getPackage()).getImplementationVersion(), "1.8.0_121");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getName(), "java.time.LocalDate");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).toGenericString(), "public class java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getTypeName(), "java.time.LocalDate");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.security.ProtectionDomain)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getProtectionDomain()).getCodeSource());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getSimpleName(), "Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getPackage()).getImplementationTitle(), "Java Runtime Environment");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getModifiers(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getPackage()).getSpecificationVendor(), "Oracle Corporation");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getModifiers(), 17);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getPackage()).isSealed());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getAnnotatedSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getPackage()).getSpecificationVersion(), "1.8");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getGenericSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).toGenericString(), "public final class java.time.LocalDate");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.security.ProtectionDomain)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getProtectionDomain()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getCanonicalName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSimpleName(), "LocalDate");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getPackage()).getImplementationVendor(), "Oracle Corporation");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getPackage()).getSpecificationTitle(), "Java Platform API Specification");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getCanonicalName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getGenericSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getSimpleName(), "Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getEnumConstants());
            // MethodAssertGenerator build local variable
            Object o_10_0 = getTypeHandler("java.time.LocalTime");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).getModifiers(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).getCanonicalName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).getSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).getAnnotatedSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).getSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).getName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).getCanonicalName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).toGenericString(), "public class java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.security.ProtectionDomain)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getProtectionDomain()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getModifiers(), 17);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getTypeName(), "java.time.LocalTime");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).getGenericSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getPackage()).getName(), "java.time");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getName(), "java.time.LocalTime");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getPackage()).getImplementationTitle(), "Java Runtime Environment");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).getSimpleName(), "Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).getTypeName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getCanonicalName(), "java.time.LocalTime");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSimpleName(), "LocalTime");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).getTypeName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).getName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getPackage()).getSpecificationVersion(), "1.8");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getPackage()).getImplementationVendor(), "Oracle Corporation");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getPackage()).getImplementationVersion(), "1.8.0_121");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getPackage()).getSpecificationTitle(), "Java Platform API Specification");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).getAnnotatedSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).getModifiers(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).toGenericString(), "public class java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getPackage()).isSealed());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).getSimpleName(), "Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.security.ProtectionDomain)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getProtectionDomain()).getCodeSource());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).toGenericString(), "public final class java.time.LocalTime");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getPackage()).getSpecificationVendor(), "Oracle Corporation");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).getGenericSuperclass());
            // MethodAssertGenerator build local variable
            Object o_13_0 = getTypeHandler("java.time.OffsetTime");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getCanonicalName(), "java.time.OffsetTime");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getSuperclass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getSuperclass()).getSimpleName(), "Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.security.ProtectionDomain)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getProtectionDomain()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getGenericSuperclass()).getName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getGenericSuperclass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getName(), "java.time.OffsetTime");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getGenericSuperclass()).getModifiers(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getGenericSuperclass()).getCanonicalName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getGenericSuperclass()).getGenericSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getGenericSuperclass()).getTypeName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getSuperclass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getGenericSuperclass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getSuperclass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getSuperclass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getPackage()).isSealed());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getPackage()).getSpecificationVendor(), "Oracle Corporation");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getGenericSuperclass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getGenericSuperclass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getSuperclass()).getTypeName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getSuperclass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getSuperclass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getSuperclass()).getModifiers(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getGenericSuperclass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getSuperclass()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getGenericSuperclass()).getAnnotatedSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getGenericSuperclass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getGenericSuperclass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getSuperclass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getTypeName(), "java.time.OffsetTime");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getSuperclass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getSimpleName(), "OffsetTime");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getSuperclass()).toGenericString(), "public class java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getGenericSuperclass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getGenericSuperclass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getSuperclass()).getCanonicalName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getGenericSuperclass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getModifiers(), 17);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getPackage()).getSpecificationVersion(), "1.8");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getSuperclass()).getName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getSuperclass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getSuperclass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getGenericSuperclass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getSuperclass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getPackage()).getName(), "java.time");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).toGenericString(), "public final class java.time.OffsetTime");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getGenericSuperclass()).toGenericString(), "public class java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getGenericSuperclass()).getSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getSuperclass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getSuperclass()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getSuperclass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getPackage()).getImplementationVendor(), "Oracle Corporation");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getSuperclass()).getAnnotatedSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getGenericSuperclass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getGenericSuperclass()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getPackage()).getSpecificationTitle(), "Java Platform API Specification");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getSuperclass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getPackage()).getImplementationTitle(), "Java Runtime Environment");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getSuperclass()).getSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getSuperclass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getGenericSuperclass()).getSimpleName(), "Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getGenericSuperclass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getPackage()).getImplementationVersion(), "1.8.0_121");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getGenericSuperclass()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getGenericSuperclass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getGenericSuperclass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getSuperclass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getGenericSuperclass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getSuperclass()).getGenericSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.security.ProtectionDomain)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_13_0).getRawType()).getProtectionDomain()).getCodeSource());
            // MethodAssertGenerator build local variable
            Object o_16_0 = getTypeHandler("java.time.OffsetTime");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getSimpleName(), "OffsetTime");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getSuperclass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getSuperclass()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getSuperclass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getSuperclass()).getAnnotatedSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getSuperclass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getSuperclass()).getCanonicalName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getGenericSuperclass()).getCanonicalName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getSuperclass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getSuperclass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getGenericSuperclass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getSuperclass()).getModifiers(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getGenericSuperclass()).getAnnotatedSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getGenericSuperclass()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getSuperclass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getPackage()).getImplementationVersion(), "1.8.0_121");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getPackage()).getSpecificationVersion(), "1.8");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getGenericSuperclass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getSuperclass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getSuperclass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getGenericSuperclass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getGenericSuperclass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getCanonicalName(), "java.time.OffsetTime");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getSuperclass()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_16_0.equals(o_13_0));
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getGenericSuperclass()).getGenericSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getGenericSuperclass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getGenericSuperclass()).toGenericString(), "public class java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getSuperclass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getGenericSuperclass()).getTypeName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getGenericSuperclass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getPackage()).getImplementationVendor(), "Oracle Corporation");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getSuperclass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getGenericSuperclass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getPackage()).getImplementationTitle(), "Java Runtime Environment");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getTypeName(), "java.time.OffsetTime");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getSuperclass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getSuperclass()).getName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getGenericSuperclass()).getSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getGenericSuperclass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getGenericSuperclass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.security.ProtectionDomain)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getProtectionDomain()).getCodeSource());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getSuperclass()).getTypeName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getGenericSuperclass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getSuperclass()).getSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getPackage()).getName(), "java.time");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getGenericSuperclass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getGenericSuperclass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getSuperclass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getGenericSuperclass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getSuperclass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getSuperclass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getGenericSuperclass()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getModifiers(), 17);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getGenericSuperclass()).getModifiers(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getPackage()).isSealed());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getGenericSuperclass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getGenericSuperclass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getSuperclass()).getGenericSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getSuperclass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getGenericSuperclass()).getSimpleName(), "Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getSuperclass()).getSimpleName(), "Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getGenericSuperclass()).getName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getPackage()).getSpecificationVendor(), "Oracle Corporation");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.security.ProtectionDomain)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getProtectionDomain()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getPackage()).getSpecificationTitle(), "Java Platform API Specification");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getSuperclass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getGenericSuperclass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getName(), "java.time.OffsetTime");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).getSuperclass()).toGenericString(), "public class java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.OffsetTimeTypeHandler)o_16_0).getRawType()).toGenericString(), "public final class java.time.OffsetTime");
            // MethodAssertGenerator build local variable
            Object o_19_0 = getTypeHandler("java.ime.ZonedDateTime");
            org.junit.Assert.fail("testFor_v1_0_0_literalMutation32 should have thrown ClassNotFoundException");
        } catch (java.lang.ClassNotFoundException eee) {
        }
    }

    /* amplification of org.apache.ibatis.type.usesjava8.Jsr310TypeHandlerRegistryTest#testFor_v1_0_0 */
    @org.junit.Test
    public void testFor_v1_0_0_literalMutation18_failAssert17_literalMutation509() throws java.lang.ClassNotFoundException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_19_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.ZonedDateTimeTypeHandler.class);
            // MethodAssertGenerator build local variable
            Object o_16_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.OffsetTimeTypeHandler.class);
            // MethodAssertGenerator build local variable
            Object o_13_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.OffsetDateTimeTypeHandler.class);
            // MethodAssertGenerator build local variable
            Object o_10_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.LocalTimeTypeHandler.class);
            // MethodAssertGenerator build local variable
            Object o_7_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.LocalDateTypeHandler.class);
            // MethodAssertGenerator build local variable
            Object o_4_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.LocalDateTimeTypeHandler.class);
            // MethodAssertGenerator build local variable
            Object o_1_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.InstantTypeHandler.class);
            // MethodAssertGenerator build local variable
            Object o_1_0 = getTypeHandler("java.time.Instant");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).toGenericString(), "public class java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getGenericSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getTypeName(), "java.time.Instant");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getName(), "java.time.Instant");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getModifiers(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getModifiers(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).toGenericString(), "public final class java.time.Instant");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getPackage()).getImplementationVersion(), "1.8.0_121");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getPackage()).getSpecificationVersion(), "1.8");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getTypeName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSimpleName(), "Instant");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getPackage()).getImplementationVendor(), "Oracle Corporation");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.security.ProtectionDomain)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getProtectionDomain()).getCodeSource());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.security.ProtectionDomain)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getProtectionDomain()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getTypeName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getSimpleName(), "Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getGenericSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getModifiers(), 17);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getPackage()).getImplementationTitle(), "Java Runtime Environment");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getSimpleName(), "Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).toGenericString(), "public class java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getCanonicalName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getAnnotatedSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getPackage()).getSpecificationVendor(), "Oracle Corporation");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getAnnotatedSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getCanonicalName(), "java.time.Instant");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getPackage()).getSpecificationTitle(), "Java Platform API Specification");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getPackage()).getName(), "java.time");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getPackage()).isSealed());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getCanonicalName(), "java.lang.Object");
            // MethodAssertGenerator build local variable
            Object o_4_0 = getTypeHandler("java.time.LocalDateTime");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getPackage()).getImplementationTitle(), "Java Runtime Environment");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.security.ProtectionDomain)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getProtectionDomain()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getPackage()).getSpecificationVendor(), "Oracle Corporation");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getPackage()).getImplementationVersion(), "1.8.0_121");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSimpleName(), "LocalDateTime");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getTypeName(), "java.time.LocalDateTime");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getAnnotatedSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getGenericSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getSimpleName(), "Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getPackage()).isSealed());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getPackage()).getImplementationVendor(), "Oracle Corporation");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getModifiers(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getPackage()).getSpecificationVersion(), "1.8");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getPackage()).getSpecificationTitle(), "Java Platform API Specification");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getModifiers(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.security.ProtectionDomain)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getProtectionDomain()).getCodeSource());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getCanonicalName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).toGenericString(), "public class java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).toGenericString(), "public final class java.time.LocalDateTime");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getPackage()).getName(), "java.time");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getSimpleName(), "Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).toGenericString(), "public class java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getGenericSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getCanonicalName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getTypeName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getModifiers(), 17);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getCanonicalName(), "java.time.LocalDateTime");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getName(), "java.time.LocalDateTime");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getAnnotatedSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getTypeName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).isMemberClass());
            // MethodAssertGenerator build local variable
            Object o_7_0 = getTypeHandler("java.time.LocalDate");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getCanonicalName(), "java.time.LocalDate");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getAnnotatedSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getTypeName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getModifiers(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getPackage()).getName(), "java.time");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).toGenericString(), "public class java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getTypeName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getPackage()).getImplementationVersion(), "1.8.0_121");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getName(), "java.time.LocalDate");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).toGenericString(), "public class java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getTypeName(), "java.time.LocalDate");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.security.ProtectionDomain)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getProtectionDomain()).getCodeSource());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getSimpleName(), "Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getPackage()).getImplementationTitle(), "Java Runtime Environment");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getModifiers(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getPackage()).getSpecificationVendor(), "Oracle Corporation");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getModifiers(), 17);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getPackage()).isSealed());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getAnnotatedSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getPackage()).getSpecificationVersion(), "1.8");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getGenericSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).toGenericString(), "public final class java.time.LocalDate");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.security.ProtectionDomain)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getProtectionDomain()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getCanonicalName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSimpleName(), "LocalDate");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getPackage()).getImplementationVendor(), "Oracle Corporation");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getPackage()).getSpecificationTitle(), "Java Platform API Specification");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getCanonicalName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getGenericSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getSimpleName(), "Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getEnumConstants());
            // MethodAssertGenerator build local variable
            Object o_10_0 = getTypeHandler("_M;0L`A=SO/woO!OKS@");
            // MethodAssertGenerator build local variable
            Object o_13_0 = getTypeHandler("java.time.OffsetDateTime");
            // MethodAssertGenerator build local variable
            Object o_16_0 = getTypeHandler("java.time.OffsetTime");
            // MethodAssertGenerator build local variable
            Object o_19_0 = getTypeHandler("java.time.ZonedDateTime");
            org.junit.Assert.fail("testFor_v1_0_0_literalMutation18 should have thrown ClassNotFoundException");
        } catch (java.lang.ClassNotFoundException eee) {
        }
    }

    /* amplification of org.apache.ibatis.type.usesjava8.Jsr310TypeHandlerRegistryTest#testFor_v1_0_0 */
    @org.junit.Test
    public void testFor_v1_0_0_literalMutation35_failAssert34_literalMutation1003_literalMutation3201_failAssert14() throws java.lang.ClassNotFoundException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // MethodAssertGenerator build local variable
                Object o_700_1 = 1;
                // MethodAssertGenerator build local variable
                Object o_610_1 = 17;
                // MethodAssertGenerator build local variable
                Object o_564_1 = 1;
                // MethodAssertGenerator build local variable
                Object o_491_1 = 17;
                // MethodAssertGenerator build local variable
                Object o_479_1 = 1;
                // MethodAssertGenerator build local variable
                Object o_413_1 = 1;
                // MethodAssertGenerator build local variable
                Object o_342_1 = 17;
                // MethodAssertGenerator build local variable
                Object o_276_1 = 1;
                // MethodAssertGenerator build local variable
                Object o_270_1 = 1;
                // MethodAssertGenerator build local variable
                Object o_121_1 = 17;
                // MethodAssertGenerator build local variable
                Object o_61_1 = 1;
                // MethodAssertGenerator build local variable
                Object o_55_1 = 1;
                // MethodAssertGenerator build local variable
                Object o_19_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.ZonedDateTimeTypeHandler.class);
                // MethodAssertGenerator build local variable
                Object o_16_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.OffsetTimeTypeHandler.class);
                // MethodAssertGenerator build local variable
                Object o_13_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.OffsetDateTimeTypeHandler.class);
                // MethodAssertGenerator build local variable
                Object o_10_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.LocalTimeTypeHandler.class);
                // MethodAssertGenerator build local variable
                Object o_7_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.LocalDateTypeHandler.class);
                // MethodAssertGenerator build local variable
                Object o_4_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.LocalDateTimeTypeHandler.class);
                // MethodAssertGenerator build local variable
                Object o_1_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.InstantTypeHandler.class);
                // MethodAssertGenerator build local variable
                Object o_1_0 = getTypeHandler("java.time.Instant");
                // MethodAssertGenerator build local variable
                Object o_27_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).toGenericString();
                // MethodAssertGenerator build local variable
                Object o_29_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getGenericSuperclass();
                // MethodAssertGenerator build local variable
                Object o_31_0 = ((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).isAnnotation();
                // MethodAssertGenerator build local variable
                Object o_33_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getName();
                // MethodAssertGenerator build local variable
                Object o_35_0 = ((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).isMemberClass();
                // MethodAssertGenerator build local variable
                Object o_37_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).isEnum();
                // MethodAssertGenerator build local variable
                Object o_39_0 = ((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSigners();
                // MethodAssertGenerator build local variable
                Object o_41_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getEnclosingClass();
                // MethodAssertGenerator build local variable
                Object o_43_0 = ((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getTypeName();
                // MethodAssertGenerator build local variable
                Object o_45_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getComponentType();
                // MethodAssertGenerator build local variable
                Object o_47_0 = ((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getName();
                // MethodAssertGenerator build local variable
                Object o_49_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getSuperclass();
                // MethodAssertGenerator build local variable
                Object o_51_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getDeclaringClass();
                // MethodAssertGenerator build local variable
                Object o_53_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getSigners();
                // MethodAssertGenerator build local variable
                Object o_55_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getModifiers();
                // MethodAssertGenerator build local variable
                Object o_57_0 = ((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).isArray();
                // MethodAssertGenerator build local variable
                Object o_59_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isMemberClass();
                // MethodAssertGenerator build local variable
                Object o_61_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getModifiers();
                // MethodAssertGenerator build local variable
                Object o_63_0 = ((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).toGenericString();
                // MethodAssertGenerator build local variable
                Object o_65_0 = ((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getPackage()).getImplementationVersion();
                // MethodAssertGenerator build local variable
                Object o_67_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).desiredAssertionStatus();
                // MethodAssertGenerator build local variable
                Object o_69_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).isPrimitive();
                // MethodAssertGenerator build local variable
                Object o_71_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getEnclosingMethod();
                // MethodAssertGenerator build local variable
                Object o_73_0 = ((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getEnclosingMethod();
                // MethodAssertGenerator build local variable
                Object o_75_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getEnclosingConstructor();
                // MethodAssertGenerator build local variable
                Object o_77_0 = ((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).isPrimitive();
                // MethodAssertGenerator build local variable
                Object o_79_0 = ((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getPackage()).getSpecificationVersion();
                // MethodAssertGenerator build local variable
                Object o_81_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getEnumConstants();
                // MethodAssertGenerator build local variable
                Object o_83_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).isLocalClass();
                // MethodAssertGenerator build local variable
                Object o_85_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).isAnonymousClass();
                // MethodAssertGenerator build local variable
                Object o_87_0 = ((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getEnumConstants();
                // MethodAssertGenerator build local variable
                Object o_89_0 = ((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).isAnonymousClass();
                // MethodAssertGenerator build local variable
                Object o_91_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).isInterface();
                // MethodAssertGenerator build local variable
                Object o_93_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isSynthetic();
                // MethodAssertGenerator build local variable
                Object o_95_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getTypeName();
                // MethodAssertGenerator build local variable
                Object o_97_0 = ((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSimpleName();
                // MethodAssertGenerator build local variable
                Object o_99_0 = ((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getPackage()).getImplementationVendor();
                // MethodAssertGenerator build local variable
                Object o_101_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).isArray();
                // MethodAssertGenerator build local variable
                Object o_103_0 = ((java.security.ProtectionDomain)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getProtectionDomain()).getCodeSource();
                // MethodAssertGenerator build local variable
                Object o_105_0 = ((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getComponentType();
                // MethodAssertGenerator build local variable
                Object o_107_0 = ((java.security.ProtectionDomain)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getProtectionDomain()).getClassLoader();
                // MethodAssertGenerator build local variable
                Object o_109_0 = ((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).isInterface();
                // MethodAssertGenerator build local variable
                Object o_111_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getName();
                // MethodAssertGenerator build local variable
                Object o_113_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).isSynthetic();
                // MethodAssertGenerator build local variable
                Object o_115_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getTypeName();
                // MethodAssertGenerator build local variable
                Object o_117_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getSimpleName();
                // MethodAssertGenerator build local variable
                Object o_119_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getGenericSuperclass();
                // MethodAssertGenerator build local variable
                Object o_121_0 = ((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getModifiers();
                // MethodAssertGenerator build local variable
                Object o_123_0 = ((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).isSynthetic();
                // MethodAssertGenerator build local variable
                Object o_125_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).isAnnotation();
                // MethodAssertGenerator build local variable
                Object o_127_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isAnonymousClass();
                // MethodAssertGenerator build local variable
                Object o_129_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).desiredAssertionStatus();
                // MethodAssertGenerator build local variable
                Object o_131_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getEnclosingMethod();
                // MethodAssertGenerator build local variable
                Object o_133_0 = ((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getPackage()).getImplementationTitle();
                // MethodAssertGenerator build local variable
                Object o_135_0 = ((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getClassLoader();
                // MethodAssertGenerator build local variable
                Object o_137_0 = ((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).desiredAssertionStatus();
                // MethodAssertGenerator build local variable
                Object o_139_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getEnumConstants();
                // MethodAssertGenerator build local variable
                Object o_141_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getSigners();
                // MethodAssertGenerator build local variable
                Object o_143_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getSimpleName();
                // MethodAssertGenerator build local variable
                Object o_145_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isInterface();
                // MethodAssertGenerator build local variable
                Object o_147_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getDeclaringClass();
                // MethodAssertGenerator build local variable
                Object o_149_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getSuperclass();
                // MethodAssertGenerator build local variable
                Object o_151_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).toGenericString();
                // MethodAssertGenerator build local variable
                Object o_153_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isPrimitive();
                // MethodAssertGenerator build local variable
                Object o_155_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getCanonicalName();
                // MethodAssertGenerator build local variable
                Object o_157_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getAnnotatedSuperclass();
                // MethodAssertGenerator build local variable
                Object o_159_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getComponentType();
                // MethodAssertGenerator build local variable
                Object o_161_0 = ((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getEnclosingClass();
                // MethodAssertGenerator build local variable
                Object o_163_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getEnclosingConstructor();
                // MethodAssertGenerator build local variable
                Object o_165_0 = ((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getPackage()).getSpecificationVendor();
                // MethodAssertGenerator build local variable
                Object o_167_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getAnnotatedSuperclass();
                // MethodAssertGenerator build local variable
                Object o_169_0 = ((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getDeclaringClass();
                // MethodAssertGenerator build local variable
                Object o_171_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getClassLoader();
                // MethodAssertGenerator build local variable
                Object o_173_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).isMemberClass();
                // MethodAssertGenerator build local variable
                Object o_175_0 = ((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getCanonicalName();
                // MethodAssertGenerator build local variable
                Object o_177_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isLocalClass();
                // MethodAssertGenerator build local variable
                Object o_179_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getClassLoader();
                // MethodAssertGenerator build local variable
                Object o_181_0 = ((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getPackage()).getSpecificationTitle();
                // MethodAssertGenerator build local variable
                Object o_183_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isArray();
                // MethodAssertGenerator build local variable
                Object o_185_0 = ((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getPackage()).getName();
                // MethodAssertGenerator build local variable
                Object o_187_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isAnnotation();
                // MethodAssertGenerator build local variable
                Object o_189_0 = ((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).isEnum();
                // MethodAssertGenerator build local variable
                Object o_191_0 = ((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getPackage()).isSealed();
                // MethodAssertGenerator build local variable
                Object o_193_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getEnclosingClass();
                // MethodAssertGenerator build local variable
                Object o_195_0 = ((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).isLocalClass();
                // MethodAssertGenerator build local variable
                Object o_197_0 = ((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getEnclosingConstructor();
                // MethodAssertGenerator build local variable
                Object o_199_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isEnum();
                // MethodAssertGenerator build local variable
                Object o_201_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.InstantTypeHandler)o_1_0).getRawType()).getSuperclass()).getCanonicalName();
                // MethodAssertGenerator build local variable
                Object o_4_0 = getTypeHandler("java.time.LocalDateTime");
                // MethodAssertGenerator build local variable
                Object o_206_0 = ((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getPackage()).getImplementationTitle();
                // MethodAssertGenerator build local variable
                Object o_208_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).isArray();
                // MethodAssertGenerator build local variable
                Object o_210_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).isPrimitive();
                // MethodAssertGenerator build local variable
                Object o_212_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).isEnum();
                // MethodAssertGenerator build local variable
                Object o_214_0 = ((java.security.ProtectionDomain)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getProtectionDomain()).getClassLoader();
                // MethodAssertGenerator build local variable
                Object o_216_0 = ((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getPackage()).getSpecificationVendor();
                // MethodAssertGenerator build local variable
                Object o_218_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getDeclaringClass();
                // MethodAssertGenerator build local variable
                Object o_220_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getEnclosingClass();
                // MethodAssertGenerator build local variable
                Object o_222_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getEnclosingConstructor();
                // MethodAssertGenerator build local variable
                Object o_224_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).isLocalClass();
                // MethodAssertGenerator build local variable
                Object o_226_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).desiredAssertionStatus();
                // MethodAssertGenerator build local variable
                Object o_228_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getSigners();
                // MethodAssertGenerator build local variable
                Object o_230_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getDeclaringClass();
                // MethodAssertGenerator build local variable
                Object o_232_0 = ((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getPackage()).getImplementationVersion();
                // MethodAssertGenerator build local variable
                Object o_234_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSimpleName();
                // MethodAssertGenerator build local variable
                Object o_236_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).isAnnotation();
                // MethodAssertGenerator build local variable
                Object o_238_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getClassLoader();
                // MethodAssertGenerator build local variable
                Object o_240_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getSuperclass();
                // MethodAssertGenerator build local variable
                Object o_242_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getTypeName();
                // MethodAssertGenerator build local variable
                Object o_244_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getAnnotatedSuperclass();
                // MethodAssertGenerator build local variable
                Object o_246_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getGenericSuperclass();
                // MethodAssertGenerator build local variable
                Object o_248_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).desiredAssertionStatus();
                // MethodAssertGenerator build local variable
                Object o_250_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getSimpleName();
                // MethodAssertGenerator build local variable
                Object o_252_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getEnclosingMethod();
                // MethodAssertGenerator build local variable
                Object o_254_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).isEnum();
                // MethodAssertGenerator build local variable
                Object o_256_0 = ((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getPackage()).isSealed();
                // MethodAssertGenerator build local variable
                Object o_258_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).isAnonymousClass();
                // MethodAssertGenerator build local variable
                Object o_260_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getEnumConstants();
                // MethodAssertGenerator build local variable
                Object o_262_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).isMemberClass();
                // MethodAssertGenerator build local variable
                Object o_264_0 = ((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getPackage()).getImplementationVendor();
                // MethodAssertGenerator build local variable
                Object o_266_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getName();
                // MethodAssertGenerator build local variable
                Object o_268_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getComponentType();
                // MethodAssertGenerator build local variable
                Object o_270_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getModifiers();
                // MethodAssertGenerator build local variable
                Object o_272_0 = ((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getPackage()).getSpecificationVersion();
                // MethodAssertGenerator build local variable
                Object o_274_0 = ((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getPackage()).getSpecificationTitle();
                // MethodAssertGenerator build local variable
                Object o_276_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getModifiers();
                // MethodAssertGenerator build local variable
                Object o_278_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).isAnnotation();
                // MethodAssertGenerator build local variable
                Object o_280_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).isArray();
                // MethodAssertGenerator build local variable
                Object o_282_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).isEnum();
                // MethodAssertGenerator build local variable
                Object o_284_0 = ((java.security.ProtectionDomain)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getProtectionDomain()).getCodeSource();
                // MethodAssertGenerator build local variable
                Object o_286_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getComponentType();
                // MethodAssertGenerator build local variable
                Object o_288_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).isAnonymousClass();
                // MethodAssertGenerator build local variable
                Object o_290_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).isPrimitive();
                // MethodAssertGenerator build local variable
                Object o_292_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).isAnonymousClass();
                // MethodAssertGenerator build local variable
                Object o_294_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getCanonicalName();
                // MethodAssertGenerator build local variable
                Object o_296_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getEnumConstants();
                // MethodAssertGenerator build local variable
                Object o_298_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).isMemberClass();
                // MethodAssertGenerator build local variable
                Object o_300_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).toGenericString();
                // MethodAssertGenerator build local variable
                Object o_302_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).desiredAssertionStatus();
                // MethodAssertGenerator build local variable
                Object o_304_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSigners();
                // MethodAssertGenerator build local variable
                Object o_306_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).toGenericString();
                // MethodAssertGenerator build local variable
                Object o_308_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getSigners();
                // MethodAssertGenerator build local variable
                Object o_310_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getName();
                // MethodAssertGenerator build local variable
                Object o_312_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).isAnnotation();
                // MethodAssertGenerator build local variable
                Object o_314_0 = ((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getPackage()).getName();
                // MethodAssertGenerator build local variable
                Object o_316_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getComponentType();
                // MethodAssertGenerator build local variable
                Object o_318_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).isSynthetic();
                // MethodAssertGenerator build local variable
                Object o_320_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getSimpleName();
                // MethodAssertGenerator build local variable
                Object o_322_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getEnclosingConstructor();
                // MethodAssertGenerator build local variable
                Object o_324_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getEnclosingMethod();
                // MethodAssertGenerator build local variable
                Object o_326_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).isArray();
                // MethodAssertGenerator build local variable
                Object o_328_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).toGenericString();
                // MethodAssertGenerator build local variable
                Object o_330_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getDeclaringClass();
                // MethodAssertGenerator build local variable
                Object o_332_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getGenericSuperclass();
                // MethodAssertGenerator build local variable
                Object o_334_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getEnclosingConstructor();
                // MethodAssertGenerator build local variable
                Object o_336_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getEnumConstants();
                // MethodAssertGenerator build local variable
                Object o_338_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getCanonicalName();
                // MethodAssertGenerator build local variable
                Object o_340_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getTypeName();
                // MethodAssertGenerator build local variable
                Object o_342_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getModifiers();
                // MethodAssertGenerator build local variable
                Object o_344_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getCanonicalName();
                // MethodAssertGenerator build local variable
                Object o_346_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getEnclosingClass();
                // MethodAssertGenerator build local variable
                Object o_348_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).isInterface();
                // MethodAssertGenerator build local variable
                Object o_350_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getName();
                // MethodAssertGenerator build local variable
                Object o_352_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).isSynthetic();
                // MethodAssertGenerator build local variable
                Object o_354_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getClassLoader();
                // MethodAssertGenerator build local variable
                Object o_356_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getClassLoader();
                // MethodAssertGenerator build local variable
                Object o_358_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getAnnotatedSuperclass();
                // MethodAssertGenerator build local variable
                Object o_360_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getTypeName();
                // MethodAssertGenerator build local variable
                Object o_362_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).isSynthetic();
                // MethodAssertGenerator build local variable
                Object o_364_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).isInterface();
                // MethodAssertGenerator build local variable
                Object o_366_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).getEnclosingClass();
                // MethodAssertGenerator build local variable
                Object o_368_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).isInterface();
                // MethodAssertGenerator build local variable
                Object o_370_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getSuperclass();
                // MethodAssertGenerator build local variable
                Object o_372_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).isPrimitive();
                // MethodAssertGenerator build local variable
                Object o_374_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).isLocalClass();
                // MethodAssertGenerator build local variable
                Object o_376_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getSuperclass()).isLocalClass();
                // MethodAssertGenerator build local variable
                Object o_378_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).getGenericSuperclass()).getEnclosingMethod();
                // MethodAssertGenerator build local variable
                Object o_380_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTimeTypeHandler)o_4_0).getRawType()).isMemberClass();
                // MethodAssertGenerator build local variable
                Object o_7_0 = getTypeHandler("java.time.LocalDate");
                // MethodAssertGenerator build local variable
                Object o_385_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).isAnonymousClass();
                // MethodAssertGenerator build local variable
                Object o_387_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getSuperclass();
                // MethodAssertGenerator build local variable
                Object o_389_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getCanonicalName();
                // MethodAssertGenerator build local variable
                Object o_391_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getEnumConstants();
                // MethodAssertGenerator build local variable
                Object o_393_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getComponentType();
                // MethodAssertGenerator build local variable
                Object o_395_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).isMemberClass();
                // MethodAssertGenerator build local variable
                Object o_397_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).isInterface();
                // MethodAssertGenerator build local variable
                Object o_399_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).isLocalClass();
                // MethodAssertGenerator build local variable
                Object o_401_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getAnnotatedSuperclass();
                // MethodAssertGenerator build local variable
                Object o_403_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getComponentType();
                // MethodAssertGenerator build local variable
                Object o_405_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getTypeName();
                // MethodAssertGenerator build local variable
                Object o_407_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).isMemberClass();
                // MethodAssertGenerator build local variable
                Object o_409_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getEnclosingConstructor();
                // MethodAssertGenerator build local variable
                Object o_411_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).isArray();
                // MethodAssertGenerator build local variable
                Object o_413_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getModifiers();
                // MethodAssertGenerator build local variable
                Object o_415_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getClassLoader();
                // MethodAssertGenerator build local variable
                Object o_417_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getDeclaringClass();
                // MethodAssertGenerator build local variable
                Object o_419_0 = ((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getPackage()).getName();
                // MethodAssertGenerator build local variable
                Object o_421_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).isSynthetic();
                // MethodAssertGenerator build local variable
                Object o_423_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).toGenericString();
                // MethodAssertGenerator build local variable
                Object o_425_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getEnclosingClass();
                // MethodAssertGenerator build local variable
                Object o_427_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getEnclosingClass();
                // MethodAssertGenerator build local variable
                Object o_429_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getTypeName();
                // MethodAssertGenerator build local variable
                Object o_431_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getDeclaringClass();
                // MethodAssertGenerator build local variable
                Object o_433_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getSuperclass();
                // MethodAssertGenerator build local variable
                Object o_435_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).isInterface();
                // MethodAssertGenerator build local variable
                Object o_437_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getSigners();
                // MethodAssertGenerator build local variable
                Object o_439_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getEnclosingConstructor();
                // MethodAssertGenerator build local variable
                Object o_441_0 = ((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getPackage()).getImplementationVersion();
                // MethodAssertGenerator build local variable
                Object o_443_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getClassLoader();
                // MethodAssertGenerator build local variable
                Object o_445_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).isEnum();
                // MethodAssertGenerator build local variable
                Object o_447_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).isSynthetic();
                // MethodAssertGenerator build local variable
                Object o_449_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getEnclosingMethod();
                // MethodAssertGenerator build local variable
                Object o_451_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).isAnnotation();
                // MethodAssertGenerator build local variable
                Object o_453_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).isAnnotation();
                // MethodAssertGenerator build local variable
                Object o_455_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getName();
                // MethodAssertGenerator build local variable
                Object o_457_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).isSynthetic();
                // MethodAssertGenerator build local variable
                Object o_459_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getName();
                // MethodAssertGenerator build local variable
                Object o_461_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).toGenericString();
                // MethodAssertGenerator build local variable
                Object o_463_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).isEnum();
                // MethodAssertGenerator build local variable
                Object o_465_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).isEnum();
                // MethodAssertGenerator build local variable
                Object o_467_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).desiredAssertionStatus();
                // MethodAssertGenerator build local variable
                Object o_469_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getTypeName();
                // MethodAssertGenerator build local variable
                Object o_471_0 = ((java.security.ProtectionDomain)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getProtectionDomain()).getCodeSource();
                // MethodAssertGenerator build local variable
                Object o_473_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getSimpleName();
                // MethodAssertGenerator build local variable
                Object o_475_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).isLocalClass();
                // MethodAssertGenerator build local variable
                Object o_477_0 = ((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getPackage()).getImplementationTitle();
                // MethodAssertGenerator build local variable
                Object o_479_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getModifiers();
                // MethodAssertGenerator build local variable
                Object o_481_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getClassLoader();
                // MethodAssertGenerator build local variable
                Object o_483_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getEnclosingConstructor();
                // MethodAssertGenerator build local variable
                Object o_485_0 = ((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getPackage()).getSpecificationVendor();
                // MethodAssertGenerator build local variable
                Object o_487_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).isAnonymousClass();
                // MethodAssertGenerator build local variable
                Object o_489_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).isLocalClass();
                // MethodAssertGenerator build local variable
                Object o_491_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getModifiers();
                // MethodAssertGenerator build local variable
                Object o_493_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getName();
                // MethodAssertGenerator build local variable
                Object o_495_0 = ((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getPackage()).isSealed();
                // MethodAssertGenerator build local variable
                Object o_497_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).isPrimitive();
                // MethodAssertGenerator build local variable
                Object o_499_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getEnumConstants();
                // MethodAssertGenerator build local variable
                Object o_501_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getAnnotatedSuperclass();
                // MethodAssertGenerator build local variable
                Object o_503_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).isArray();
                // MethodAssertGenerator build local variable
                Object o_505_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).isArray();
                // MethodAssertGenerator build local variable
                Object o_507_0 = ((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getPackage()).getSpecificationVersion();
                // MethodAssertGenerator build local variable
                Object o_509_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getGenericSuperclass();
                // MethodAssertGenerator build local variable
                Object o_511_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).isPrimitive();
                // MethodAssertGenerator build local variable
                Object o_513_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getEnclosingMethod();
                // MethodAssertGenerator build local variable
                Object o_515_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).toGenericString();
                // MethodAssertGenerator build local variable
                Object o_517_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).desiredAssertionStatus();
                // MethodAssertGenerator build local variable
                Object o_519_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).isInterface();
                // MethodAssertGenerator build local variable
                Object o_521_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getEnclosingClass();
                // MethodAssertGenerator build local variable
                Object o_523_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).desiredAssertionStatus();
                // MethodAssertGenerator build local variable
                Object o_525_0 = ((java.security.ProtectionDomain)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getProtectionDomain()).getClassLoader();
                // MethodAssertGenerator build local variable
                Object o_527_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getCanonicalName();
                // MethodAssertGenerator build local variable
                Object o_529_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).isAnnotation();
                // MethodAssertGenerator build local variable
                Object o_531_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSimpleName();
                // MethodAssertGenerator build local variable
                Object o_533_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).isMemberClass();
                // MethodAssertGenerator build local variable
                Object o_535_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getComponentType();
                // MethodAssertGenerator build local variable
                Object o_537_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSigners();
                // MethodAssertGenerator build local variable
                Object o_539_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getEnclosingMethod();
                // MethodAssertGenerator build local variable
                Object o_541_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).isPrimitive();
                // MethodAssertGenerator build local variable
                Object o_543_0 = ((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getPackage()).getImplementationVendor();
                // MethodAssertGenerator build local variable
                Object o_545_0 = ((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getPackage()).getSpecificationTitle();
                // MethodAssertGenerator build local variable
                Object o_547_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getCanonicalName();
                // MethodAssertGenerator build local variable
                Object o_549_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getGenericSuperclass();
                // MethodAssertGenerator build local variable
                Object o_551_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getDeclaringClass();
                // MethodAssertGenerator build local variable
                Object o_553_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).isAnonymousClass();
                // MethodAssertGenerator build local variable
                Object o_555_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getGenericSuperclass()).getSimpleName();
                // MethodAssertGenerator build local variable
                Object o_557_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getSuperclass()).getSigners();
                // MethodAssertGenerator build local variable
                Object o_559_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalDateTypeHandler)o_7_0).getRawType()).getEnumConstants();
                // MethodAssertGenerator build local variable
                Object o_10_0 = getTypeHandler("java.time.OffsetTime");
                // MethodAssertGenerator build local variable
                Object o_564_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).getModifiers();
                // MethodAssertGenerator build local variable
                Object o_566_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).getCanonicalName();
                // MethodAssertGenerator build local variable
                Object o_568_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).isInterface();
                // MethodAssertGenerator build local variable
                Object o_570_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).isLocalClass();
                // MethodAssertGenerator build local variable
                Object o_572_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).getSuperclass();
                // MethodAssertGenerator build local variable
                Object o_574_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).getAnnotatedSuperclass();
                // MethodAssertGenerator build local variable
                Object o_576_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).isAnonymousClass();
                // MethodAssertGenerator build local variable
                Object o_578_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).getEnclosingMethod();
                // MethodAssertGenerator build local variable
                Object o_580_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).getComponentType();
                // MethodAssertGenerator build local variable
                Object o_582_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).getSuperclass();
                // MethodAssertGenerator build local variable
                Object o_584_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).desiredAssertionStatus();
                // MethodAssertGenerator build local variable
                Object o_586_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getDeclaringClass();
                // MethodAssertGenerator build local variable
                Object o_588_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).getName();
                // MethodAssertGenerator build local variable
                Object o_590_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).getCanonicalName();
                // MethodAssertGenerator build local variable
                Object o_592_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).toGenericString();
                // MethodAssertGenerator build local variable
                Object o_594_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).getEnumConstants();
                // MethodAssertGenerator build local variable
                Object o_596_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).getClassLoader();
                // MethodAssertGenerator build local variable
                Object o_598_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).isPrimitive();
                // MethodAssertGenerator build local variable
                Object o_600_0 = ((java.security.ProtectionDomain)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getProtectionDomain()).getClassLoader();
                // MethodAssertGenerator build local variable
                Object o_602_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).isSynthetic();
                // MethodAssertGenerator build local variable
                Object o_604_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).desiredAssertionStatus();
                // MethodAssertGenerator build local variable
                Object o_606_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).getComponentType();
                // MethodAssertGenerator build local variable
                Object o_608_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).isAnnotation();
                // MethodAssertGenerator build local variable
                Object o_610_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getModifiers();
                // MethodAssertGenerator build local variable
                Object o_612_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getTypeName();
                // MethodAssertGenerator build local variable
                Object o_614_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).isMemberClass();
                // MethodAssertGenerator build local variable
                Object o_616_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).getEnclosingConstructor();
                // MethodAssertGenerator build local variable
                Object o_618_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).isLocalClass();
                // MethodAssertGenerator build local variable
                Object o_620_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).getGenericSuperclass();
                // MethodAssertGenerator build local variable
                Object o_622_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).isPrimitive();
                // MethodAssertGenerator build local variable
                Object o_624_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).desiredAssertionStatus();
                // MethodAssertGenerator build local variable
                Object o_626_0 = ((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getPackage()).getName();
                // MethodAssertGenerator build local variable
                Object o_628_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getName();
                // MethodAssertGenerator build local variable
                Object o_630_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).isInterface();
                // MethodAssertGenerator build local variable
                Object o_632_0 = ((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getPackage()).getImplementationTitle();
                // MethodAssertGenerator build local variable
                Object o_634_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).getEnumConstants();
                // MethodAssertGenerator build local variable
                Object o_636_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).isSynthetic();
                // MethodAssertGenerator build local variable
                Object o_638_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).isAnonymousClass();
                // MethodAssertGenerator build local variable
                Object o_640_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).getSimpleName();
                // MethodAssertGenerator build local variable
                Object o_642_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).getEnclosingMethod();
                // MethodAssertGenerator build local variable
                Object o_644_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).isAnnotation();
                // MethodAssertGenerator build local variable
                Object o_646_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).getTypeName();
                // MethodAssertGenerator build local variable
                Object o_648_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getCanonicalName();
                // MethodAssertGenerator build local variable
                Object o_650_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSimpleName();
                // MethodAssertGenerator build local variable
                Object o_652_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).isArray();
                // MethodAssertGenerator build local variable
                Object o_654_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).getTypeName();
                // MethodAssertGenerator build local variable
                Object o_656_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).getName();
                // MethodAssertGenerator build local variable
                Object o_658_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).isArray();
                // MethodAssertGenerator build local variable
                Object o_660_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).getDeclaringClass();
                // MethodAssertGenerator build local variable
                Object o_662_0 = ((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getPackage()).getSpecificationVersion();
                // MethodAssertGenerator build local variable
                Object o_664_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).getEnclosingClass();
                // MethodAssertGenerator build local variable
                Object o_666_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getEnclosingConstructor();
                // MethodAssertGenerator build local variable
                Object o_668_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).getDeclaringClass();
                // MethodAssertGenerator build local variable
                Object o_670_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getEnumConstants();
                // MethodAssertGenerator build local variable
                Object o_672_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).getClassLoader();
                // MethodAssertGenerator build local variable
                Object o_674_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).isSynthetic();
                // MethodAssertGenerator build local variable
                Object o_676_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).isAnonymousClass();
                // MethodAssertGenerator build local variable
                Object o_678_0 = ((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getPackage()).getImplementationVendor();
                // MethodAssertGenerator build local variable
                Object o_680_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).isMemberClass();
                // MethodAssertGenerator build local variable
                Object o_682_0 = ((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getPackage()).getImplementationVersion();
                // MethodAssertGenerator build local variable
                Object o_684_0 = ((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getPackage()).getSpecificationTitle();
                // MethodAssertGenerator build local variable
                Object o_686_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).isEnum();
                // MethodAssertGenerator build local variable
                Object o_688_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getComponentType();
                // MethodAssertGenerator build local variable
                Object o_690_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).isLocalClass();
                // MethodAssertGenerator build local variable
                Object o_692_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).isMemberClass();
                // MethodAssertGenerator build local variable
                Object o_694_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).isArray();
                // MethodAssertGenerator build local variable
                Object o_696_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).getAnnotatedSuperclass();
                // MethodAssertGenerator build local variable
                Object o_698_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).isEnum();
                // MethodAssertGenerator build local variable
                Object o_700_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).getModifiers();
                // MethodAssertGenerator build local variable
                Object o_702_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getClassLoader();
                // MethodAssertGenerator build local variable
                Object o_704_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).getSigners();
                // MethodAssertGenerator build local variable
                Object o_706_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).isInterface();
                // MethodAssertGenerator build local variable
                Object o_708_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSigners();
                // MethodAssertGenerator build local variable
                Object o_710_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).isAnnotation();
                // MethodAssertGenerator build local variable
                Object o_712_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).toGenericString();
                // MethodAssertGenerator build local variable
                Object o_714_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).getEnclosingClass();
                // MethodAssertGenerator build local variable
                Object o_716_0 = ((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getPackage()).isSealed();
                // MethodAssertGenerator build local variable
                Object o_718_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).getEnclosingConstructor();
                // MethodAssertGenerator build local variable
                Object o_720_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).isPrimitive();
                // MethodAssertGenerator build local variable
                Object o_722_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getEnclosingClass();
                // MethodAssertGenerator build local variable
                Object o_724_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).getSimpleName();
                // MethodAssertGenerator build local variable
                Object o_726_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).isEnum();
                // MethodAssertGenerator build local variable
                Object o_728_0 = ((java.security.ProtectionDomain)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getProtectionDomain()).getCodeSource();
                // MethodAssertGenerator build local variable
                Object o_730_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).toGenericString();
                // MethodAssertGenerator build local variable
                Object o_732_0 = ((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getEnclosingMethod();
                // MethodAssertGenerator build local variable
                Object o_734_0 = ((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getPackage()).getSpecificationVendor();
                // MethodAssertGenerator build local variable
                Object o_736_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getGenericSuperclass()).getSigners();
                // MethodAssertGenerator build local variable
                Object o_738_0 = ((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.LocalTimeTypeHandler)o_10_0).getRawType()).getSuperclass()).getGenericSuperclass();
                // MethodAssertGenerator build local variable
                Object o_13_0 = getTypeHandler("*&blaH h/,{SF+X9hOt_}hJs");
                // MethodAssertGenerator build local variable
                Object o_16_0 = getTypeHandler("java.time.OffsetTime");
                // MethodAssertGenerator build local variable
                Object o_19_0 = getTypeHandler("java.time.1ZonedDateTime");
                org.junit.Assert.fail("testFor_v1_0_0_literalMutation35 should have thrown ClassNotFoundException");
            } catch (java.lang.ClassNotFoundException eee) {
            }
            org.junit.Assert.fail("testFor_v1_0_0_literalMutation35_failAssert34_literalMutation1003_literalMutation3201 should have thrown ClassCastException");
        } catch (java.lang.ClassCastException eee) {
        }
    }

    /* amplification of org.apache.ibatis.type.usesjava8.Jsr310TypeHandlerRegistryTest#testFor_v1_0_1 */
    @org.junit.Test
    public void testFor_v1_0_1_literalMutation6308_failAssert8() throws java.lang.ClassNotFoundException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_4_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.YearTypeHandler.class);
            // MethodAssertGenerator build local variable
            Object o_1_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.MonthTypeHandler.class);
            // MethodAssertGenerator build local variable
            Object o_1_0 = getTypeHandler("java.time.Month");
            // MethodAssertGenerator build local variable
            Object o_4_0 = getTypeHandler("java.tim.Year");
            org.junit.Assert.fail("testFor_v1_0_1_literalMutation6308 should have thrown ClassNotFoundException");
        } catch (java.lang.ClassNotFoundException eee) {
        }
    }

    /* amplification of org.apache.ibatis.type.usesjava8.Jsr310TypeHandlerRegistryTest#testFor_v1_0_1 */
    @org.junit.Test
    public void testFor_v1_0_1_literalMutation6309_failAssert9_literalMutation6386() throws java.lang.ClassNotFoundException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_4_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.YearTypeHandler.class);
            // MethodAssertGenerator build local variable
            Object o_1_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.MonthTypeHandler.class);
            // MethodAssertGenerator build local variable
            Object o_1_0 = getTypeHandler("java.time.Month");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getName(), "java.time.Month");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getPackage()).getSpecificationVersion(), "1.8");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getTypeName(), "java.time.Month");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getTypeName(), "java.lang.Enum<java.time.Month>");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getCanonicalName(), "java.time.Month");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getPackage()).isSealed());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getPackage()).getImplementationVendor(), "Oracle Corporation");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getSuperclass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getSuperclass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getSuperclass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getPackage()).getSpecificationTitle(), "Java Platform API Specification");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getSuperclass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getSuperclass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getSimpleName(), "Month");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getSuperclass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).toGenericString(), "public final enum java.time.Month");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getSuperclass()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getSuperclass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getSuperclass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.security.ProtectionDomain)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getProtectionDomain()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getSuperclass()).getName(), "java.lang.Enum");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getOwnerType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getSuperclass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getSuperclass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getModifiers(), 16401);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getPackage()).getSpecificationVendor(), "Oracle Corporation");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getSuperclass()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getSuperclass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.security.ProtectionDomain)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getProtectionDomain()).getCodeSource());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getSuperclass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getPackage()).getImplementationTitle(), "Java Runtime Environment");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getSuperclass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getSuperclass()).getCanonicalName(), "java.lang.Enum");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getSuperclass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getSuperclass()).getSimpleName(), "Enum");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getSuperclass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getSuperclass()).toGenericString(), "public abstract class java.lang.Enum<E>");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getSuperclass()).getModifiers(), 1025);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getPackage()).getImplementationVersion(), "1.8.0_121");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getPackage()).getName(), "java.time");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getSuperclass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.MonthTypeHandler)o_1_0).getRawType()).getSuperclass()).getTypeName(), "java.lang.Enum");
            // MethodAssertGenerator build local variable
            Object o_4_0 = getTypeHandler("java.i[me.Year");
            org.junit.Assert.fail("testFor_v1_0_1_literalMutation6309 should have thrown ClassNotFoundException");
        } catch (java.lang.ClassNotFoundException eee) {
        }
    }

    /* amplification of org.apache.ibatis.type.usesjava8.Jsr310TypeHandlerRegistryTest#testFor_v1_0_2 */
    @org.junit.Test
    public void testFor_v1_0_2_literalMutation6593_failAssert3() throws java.lang.ClassNotFoundException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_4_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.JapaneseDateTypeHandler.class);
            // MethodAssertGenerator build local variable
            Object o_1_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.YearMonthTypeHandler.class);
            // MethodAssertGenerator build local variable
            Object o_1_0 = getTypeHandler("&Le^N=TL%[AC(f%+#ne");
            // MethodAssertGenerator build local variable
            Object o_4_0 = getTypeHandler("java.time.chrono.JapaneseDate");
            org.junit.Assert.fail("testFor_v1_0_2_literalMutation6593 should have thrown ClassNotFoundException");
        } catch (java.lang.ClassNotFoundException eee) {
        }
    }

    /* amplification of org.apache.ibatis.type.usesjava8.Jsr310TypeHandlerRegistryTest#testFor_v1_0_2 */
    @org.junit.Test
    public void testFor_v1_0_2_literalMutation6599_failAssert9_literalMutation6679() throws java.lang.ClassNotFoundException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_4_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.JapaneseDateTypeHandler.class);
            // MethodAssertGenerator build local variable
            Object o_1_1 = org.hamcrest.core.IsInstanceOf.instanceOf(org.apache.ibatis.type.YearMonthTypeHandler.class);
            // MethodAssertGenerator build local variable
            Object o_1_0 = getTypeHandler("java.time.YearMonth");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getAnnotatedSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getSuperclass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getSuperclass()).getAnnotatedSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getSuperclass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getSuperclass()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getGenericSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getPackage()).getImplementationVersion(), "1.8.0_121");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getSuperclass()).getGenericSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getSuperclass()).getName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getSuperclass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getSuperclass()).toGenericString(), "public class java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getCanonicalName(), "java.time.YearMonth");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).toGenericString(), "public final class java.time.YearMonth");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getSuperclass()).getSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getSuperclass()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getSuperclass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getSuperclass()).getModifiers(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getPackage()).getSpecificationVendor(), "Oracle Corporation");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getSuperclass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getModifiers(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getSuperclass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getSuperclass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getName(), "java.time.YearMonth");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getSuperclass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getSuperclass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).toGenericString(), "public class java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getSuperclass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getModifiers(), 17);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getSuperclass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getSuperclass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getPackage()).getSpecificationTitle(), "Java Platform API Specification");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getSimpleName(), "Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getSuperclass()).getTypeName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getTypeName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getSuperclass()).getSimpleName(), "Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getSuperclass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getSuperclass()).getCanonicalName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getTypeName(), "java.time.YearMonth");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getSimpleName(), "YearMonth");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getPackage()).getImplementationVendor(), "Oracle Corporation");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.security.ProtectionDomain)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getProtectionDomain()).getCodeSource());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getPackage()).getImplementationTitle(), "Java Runtime Environment");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getSuperclass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getSuperclass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).getCanonicalName(), "java.lang.Object");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getPackage()).isSealed());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getGenericSuperclass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getSuperclass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getPackage()).getSpecificationVersion(), "1.8");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getSuperclass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.security.ProtectionDomain)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getProtectionDomain()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.YearMonthTypeHandler)o_1_0).getRawType()).getPackage()).getName(), "java.time");
            // MethodAssertGenerator build local variable
            Object o_4_0 = getTypeHandler("p%`oX|ueX-@^SC wj[laX)Rfs=(Z");
            org.junit.Assert.fail("testFor_v1_0_2_literalMutation6599 should have thrown ClassNotFoundException");
        } catch (java.lang.ClassNotFoundException eee) {
        }
    }
}

