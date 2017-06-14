/**
 * Copyright 2009-2015 the original author or authors.
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


package org.apache.ibatis.type;


public class AmplJdbcTypeTest {
    private static final java.lang.String[] requiredStandardTypeNames = new java.lang.String[]{ "ARRAY" , "BIGINT" , "BINARY" , "BIT" , "BLOB" , "BOOLEAN" , "CHAR" , "CLOB" , "DATALINK" , "DATE" , "DECIMAL" , "DISTINCT" , "DOUBLE" , "FLOAT" , "INTEGER" , "JAVA_OBJECT" , "LONGNVARCHAR" , "LONGVARBINARY" , "LONGVARCHAR" , "NCHAR" , "NCLOB" , "NULL" , "NUMERIC" , "NVARCHAR" , "OTHER" , "REAL" , "REF" , "ROWID" , "SMALLINT" , "SQLXML" , "STRUCT" , "TIME" , "TIMESTAMP" , "TINYINT" , "VARBINARY" , "VARCHAR" };

    @org.junit.Test
    public void shouldHaveRequiredStandardConstants() throws java.lang.Exception {
        for (java.lang.String typeName : org.apache.ibatis.type.AmplJdbcTypeTest.requiredStandardTypeNames) {
            int typeCode = java.sql.Types.class.getField(typeName).getInt(null);
            org.apache.ibatis.type.JdbcType jdbcType = org.apache.ibatis.type.JdbcType.valueOf(typeName);
            org.junit.Assert.assertEquals(typeCode, jdbcType.TYPE_CODE);
        }
    }

    @org.junit.Test
    public void shouldHaveDateTimeOffsetConstant() throws java.lang.Exception {
        org.apache.ibatis.type.JdbcType jdbcType = org.apache.ibatis.type.JdbcType.valueOf("DATETIMEOFFSET");
        org.junit.Assert.assertEquals((-155), jdbcType.TYPE_CODE);
    }

    /* amplification of org.apache.ibatis.type.JdbcTypeTest#shouldHaveDateTimeOffsetConstant */
    @org.junit.Test(timeout = 10000)
    public void shouldHaveDateTimeOffsetConstant_cf7() throws java.lang.Exception {
        org.apache.ibatis.type.JdbcType jdbcType = org.apache.ibatis.type.JdbcType.valueOf("DATETIMEOFFSET");
        // StatementAdderOnAssert create random local variable
        int vc_2 = 1231906488;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_2, 1231906488);
        // StatementAdderOnAssert create null value
        org.apache.ibatis.type.JdbcType vc_0 = (org.apache.ibatis.type.JdbcType)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_0);
        // AssertGenerator replace invocation
        org.apache.ibatis.type.JdbcType o_shouldHaveDateTimeOffsetConstant_cf7__7 = // StatementAdderMethod cloned existing statement
vc_0.forCode(vc_2);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_shouldHaveDateTimeOffsetConstant_cf7__7);
        org.junit.Assert.assertEquals((-155), jdbcType.TYPE_CODE);
    }

    /* amplification of org.apache.ibatis.type.JdbcTypeTest#shouldHaveRequiredStandardConstants */
    @org.junit.Test(timeout = 10000)
    public void shouldHaveRequiredStandardConstants_cf426() throws java.lang.Exception {
        for (java.lang.String typeName : org.apache.ibatis.type.AmplJdbcTypeTest.requiredStandardTypeNames) {
            int typeCode = java.sql.Types.class.getField(typeName).getInt(null);
            org.apache.ibatis.type.JdbcType jdbcType = org.apache.ibatis.type.JdbcType.valueOf(typeName);
            // StatementAdderOnAssert create random local variable
            int vc_17 = 1130414306;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_17, 1130414306);
            // AssertGenerator replace invocation
            org.apache.ibatis.type.JdbcType o_shouldHaveRequiredStandardConstants_cf426__11 = // StatementAdderMethod cloned existing statement
jdbcType.forCode(vc_17);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_shouldHaveRequiredStandardConstants_cf426__11);
            org.junit.Assert.assertEquals(typeCode, jdbcType.TYPE_CODE);
        }
    }

    /* amplification of org.apache.ibatis.type.JdbcTypeTest#shouldHaveRequiredStandardConstants */
    @org.junit.Test(timeout = 10000)
    public void shouldHaveRequiredStandardConstants_cf425() throws java.lang.Exception {
        for (java.lang.String typeName : org.apache.ibatis.type.AmplJdbcTypeTest.requiredStandardTypeNames) {
            int typeCode = java.sql.Types.class.getField(typeName).getInt(null);
            org.apache.ibatis.type.JdbcType jdbcType = org.apache.ibatis.type.JdbcType.valueOf(typeName);
            // AssertGenerator replace invocation
            org.apache.ibatis.type.JdbcType o_shouldHaveRequiredStandardConstants_cf425__9 = // StatementAdderMethod cloned existing statement
jdbcType.forCode(typeCode);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getGenericSuperclass()).getTypeName(), "java.lang.Enum<org.apache.ibatis.type.JdbcType>");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getSuperclass()).getEnumConstants());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getPackage()).getImplementationVersion());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getSuperclass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getSuperclass()).getCanonicalName(), "java.lang.Enum");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getSuperclass()).getTypeName(), "java.lang.Enum");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getSimpleName(), "JdbcType");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getSuperclass()).getSimpleName(), "Enum");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getSuperclass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getPackage()).isSealed());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getSuperclass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getSuperclass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getPackage()).getSpecificationVersion());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getSuperclass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getPackage()).getSpecificationTitle());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getGenericSuperclass()).getOwnerType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getCanonicalName(), "org.apache.ibatis.type.JdbcType");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(o_shouldHaveRequiredStandardConstants_cf425__9.equals(jdbcType));
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getSuperclass()).toGenericString(), "public abstract class java.lang.Enum<E>");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getSuperclass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getPackage()).getImplementationVendor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getName(), "org.apache.ibatis.type.JdbcType");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getSuperclass()).getClassLoader());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getPackage()).getName(), "org.apache.ibatis.type");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getSuperclass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getTypeName(), "org.apache.ibatis.type.JdbcType");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getPackage()).getImplementationTitle());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Package)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getPackage()).getSpecificationVendor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).toGenericString(), "public final enum org.apache.ibatis.type.JdbcType");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getSuperclass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getSuperclass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getSuperclass()).getName(), "java.lang.Enum");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getSuperclass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getSuperclass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getSuperclass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getSuperclass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getSuperclass()).getModifiers(), 1025);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getSuperclass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getSuperclass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((java.lang.Class)((org.apache.ibatis.type.JdbcType)o_shouldHaveRequiredStandardConstants_cf425__9).getDeclaringClass()).getSuperclass()).isInterface());
            org.junit.Assert.assertEquals(typeCode, jdbcType.TYPE_CODE);
        }
    }
}

