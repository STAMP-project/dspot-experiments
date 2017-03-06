

package org.jsondoc.core.util;


public class AmplJSONDocTypeBuilderTest {
    private com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    public java.lang.String getString() {
        return null;
    }

    public java.lang.Integer getInteger() {
        return null;
    }

    public java.lang.Long getLong() {
        return null;
    }

    public int getInt() {
        return 0;
    }

    public long getlong() {
        return 0L;
    }

    public java.util.List<java.lang.String> getListString() {
        return null;
    }

    public java.util.List<java.util.Set<java.lang.String>> getListSetString() {
        return null;
    }

    public java.lang.String[] getStringArray() {
        return null;
    }

    public java.lang.Integer[] getIntegerArray() {
        return null;
    }

    public java.util.List<java.lang.String>[] getListOfStringArray() {
        return null;
    }

    public java.util.Set<java.lang.String>[] getSetOfStringArray() {
        return null;
    }

    public java.util.List getList() {
        return null;
    }

    public java.util.List<?> getListOfWildcard() {
        return null;
    }

    public java.util.List<?>[] getListOfWildcardArray() {
        return null;
    }

    public java.util.List[] getListArray() {
        return null;
    }

    public java.util.Set[] getSetArray() {
        return null;
    }

    public java.util.Map getMap() {
        return null;
    }

    public java.util.HashMap getHashMap() {
        return null;
    }

    public java.util.Map<java.lang.String, java.lang.Integer> getMapStringInteger() {
        return null;
    }

    public java.util.Map<java.util.List<java.lang.String>, java.lang.Integer> getMapListOfStringInteger() {
        return null;
    }

    public java.util.Map<java.lang.String, java.util.Set<java.lang.Integer>> getMapStringSetOfInteger() {
        return null;
    }

    public java.util.Map<java.util.List<java.lang.String>, java.util.Set<java.lang.Integer>> getMapListOfStringSetOfInteger() {
        return null;
    }

    public java.util.Map<java.util.List<java.util.Set<java.lang.String>>, java.util.Set<java.lang.Integer>> getMapListOfSetOfStringSetOfInteger() {
        return null;
    }

    public java.util.Map<?, java.lang.Integer> getMapWildcardInteger() {
        return null;
    }

    public java.util.Map<?, ?> getMapWildcardWildcard() {
        return null;
    }

    public java.util.Map<java.util.List<?>, ?> getMapListOfWildcardWildcard() {
        return null;
    }

    public java.util.Map<java.util.Map, java.lang.Integer> getMapMapInteger() {
        return null;
    }

    public java.util.Map<java.util.Map<java.lang.String, java.lang.Long>, java.lang.Integer> getMapMapStringLongInteger() {
        return null;
    }

    public org.springframework.http.ResponseEntity<java.lang.String> getResponseEntityString() {
        return null;
    }

    public org.springframework.http.ResponseEntity<java.util.List<java.lang.String>> getResponseEntityListOfString() {
        return null;
    }

    public java.util.List<org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.ParentPojo> getParentPojoList() {
        return null;
    }

    public <T> org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.FooPojo<T> getSpecializedWGenericsPojo() {
        return null;
    }

    @org.jsondoc.core.annotation.ApiObject(name = "my_parent")
    class ParentPojo {    }

    @org.jsondoc.core.annotation.ApiObject(name = "fooPojo", group = "foo")
    public class FooPojo<K> {
        @org.jsondoc.core.annotation.ApiObjectField
        private K fooField;
    }

    @org.junit.Test
    public void testReflex() throws com.fasterxml.jackson.core.JsonGenerationException, com.fasterxml.jackson.databind.JsonMappingException, java.io.IOException, java.lang.ClassNotFoundException, java.lang.NoSuchMethodException, java.lang.SecurityException {
        mapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);
        org.jsondoc.core.util.JSONDocType jsonDocType = new org.jsondoc.core.util.JSONDocType();
        java.lang.reflect.Method method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getString");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("string", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getInteger");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("integer", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getInt");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("int", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getLong");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("long", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getlong");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("long", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getListString");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("list of string", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getListSetString");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("list of set of string", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getStringArray");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("array of string", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getIntegerArray");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("array of integer", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getListOfStringArray");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("array of list of string", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getSetOfStringArray");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("array of set of string", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getList");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("list", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getListOfWildcard");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("list of wildcard", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getListOfWildcardArray");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("array of list of wildcard", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getListArray");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("array of list", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getSetArray");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("array of set", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getMap");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("map", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getHashMap");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("hashmap", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getMapStringInteger");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("map[string, integer]", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getMapListOfStringInteger");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("map[list of string, integer]", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getMapStringSetOfInteger");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("map[string, set of integer]", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getMapListOfStringSetOfInteger");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("map[list of string, set of integer]", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getMapListOfSetOfStringSetOfInteger");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("map[list of set of string, set of integer]", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getMapWildcardInteger");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("map[wildcard, integer]", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getMapWildcardWildcard");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("map[wildcard, wildcard]", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getMapListOfWildcardWildcard");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("map[list of wildcard, wildcard]", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getMapMapInteger");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("map[map, integer]", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getMapMapStringLongInteger");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("map[map[string, long], integer]", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getResponseEntityString");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("responseentity of string", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getResponseEntityListOfString");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("responseentity of list of string", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getParentPojoList");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("list of my_parent", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getSpecializedWGenericsPojo");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("fooPojo of T", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
    }

    /* amplification of org.jsondoc.core.util.JSONDocTypeBuilderTest#testReflex */
    @org.junit.Test(timeout = 10000)
    public void testReflex_cf308_cf3243() throws com.fasterxml.jackson.core.JsonGenerationException, com.fasterxml.jackson.databind.JsonMappingException, java.io.IOException, java.lang.ClassNotFoundException, java.lang.NoSuchMethodException, java.lang.SecurityException {
        mapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);
        org.jsondoc.core.util.JSONDocType jsonDocType = new org.jsondoc.core.util.JSONDocType();
        java.lang.reflect.Method method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getString");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("string", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getInteger");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("integer", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getInt");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("int", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getLong");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("long", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getlong");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("long", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getListString");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("list of string", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getListSetString");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("list of set of string", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getStringArray");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("array of string", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getIntegerArray");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("array of integer", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getListOfStringArray");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("array of list of string", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getSetOfStringArray");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("array of set of string", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getList");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("list", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getListOfWildcard");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("list of wildcard", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getListOfWildcardArray");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("array of list of wildcard", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getListArray");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("array of list", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getSetArray");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("array of set", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getMap");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("map", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getHashMap");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("hashmap", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getMapStringInteger");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("map[string, integer]", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getMapListOfStringInteger");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("map[list of string, integer]", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getMapStringSetOfInteger");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("map[string, set of integer]", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getMapListOfStringSetOfInteger");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("map[list of string, set of integer]", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getMapListOfSetOfStringSetOfInteger");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("map[list of set of string, set of integer]", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getMapWildcardInteger");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("map[wildcard, integer]", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getMapWildcardWildcard");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("map[wildcard, wildcard]", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getMapListOfWildcardWildcard");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("map[list of wildcard, wildcard]", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getMapMapInteger");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("map[map, integer]", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getMapMapStringLongInteger");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("map[map[string, long], integer]", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getResponseEntityString");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("responseentity of string", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getResponseEntityListOfString");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("responseentity of list of string", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getParentPojoList");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        org.junit.Assert.assertEquals("list of my_parent", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
        jsonDocType = new org.jsondoc.core.util.JSONDocType();
        method = org.jsondoc.core.util.AmplJSONDocTypeBuilderTest.class.getMethod("getSpecializedWGenericsPojo");
        org.jsondoc.core.util.JSONDocTypeBuilder.build(jsonDocType, method.getReturnType(), method.getGenericReturnType());
        java.lang.System.out.println(mapper.writeValueAsString(jsonDocType));
        java.lang.System.out.println(jsonDocType.getOneLineText());
        // StatementAdderOnAssert create null value
        java.lang.String vc_10 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_10);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_10);
        // StatementAdderOnAssert create random local variable
        org.jsondoc.core.util.JSONDocType vc_9 = new org.jsondoc.core.util.JSONDocType();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.util.JSONDocType)vc_9).getOneLineText(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.util.JSONDocType)vc_9).getMapKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.util.JSONDocType)vc_9).getMapValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.util.JSONDocType)vc_9).getOneLineText(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.util.JSONDocType)vc_9).getMapKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.util.JSONDocType)vc_9).getMapValue());
        // StatementAdderMethod cloned existing statement
        vc_9.addItemToType(vc_10);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.util.JSONDocType)vc_9).getOneLineText(), "null");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.util.JSONDocType)vc_9).getMapKey());
        // AssertGenerator add assertion
        java.util.LinkedList collection_699320197 = new java.util.LinkedList<Object>();
	collection_699320197.add(null);
	org.junit.Assert.assertEquals(collection_699320197, ((org.jsondoc.core.util.JSONDocType)vc_9).getType());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.util.JSONDocType)vc_9).getMapValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.util.JSONDocType)vc_9).getOneLineText(), "null");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.util.JSONDocType)vc_9).getMapKey());
        // AssertGenerator add assertion
        java.util.LinkedList collection_1332616168 = new java.util.LinkedList<Object>();
	collection_1332616168.add(null);
	org.junit.Assert.assertEquals(collection_1332616168, ((org.jsondoc.core.util.JSONDocType)vc_9).getType());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.util.JSONDocType)vc_9).getMapValue());
        // StatementAdderOnAssert create null value
        java.util.List<java.lang.String> vc_158 = (java.util.List)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_158);
        // StatementAdderOnAssert create random local variable
        org.jsondoc.core.util.JSONDocType vc_157 = new org.jsondoc.core.util.JSONDocType();
        // AssertGenerator add assertion
        java.util.LinkedList collection_171491140 = new java.util.LinkedList<Object>();
	org.junit.Assert.assertEquals(collection_171491140, ((org.jsondoc.core.util.JSONDocType)vc_157).getType());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.util.JSONDocType)vc_157).getMapValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.util.JSONDocType)vc_157).getMapKey());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.util.JSONDocType)vc_157).getOneLineText(), "");
        // StatementAdderMethod cloned existing statement
        vc_157.setType(vc_158);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.util.JSONDocType)vc_157).getType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.util.JSONDocType)vc_157).getMapValue());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.util.JSONDocType)vc_157).getMapKey());
        org.junit.Assert.assertEquals("fooPojo of T", jsonDocType.getOneLineText());
        java.lang.System.out.println("---------------------------");
    }
}

