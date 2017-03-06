

package org.jsondoc.core.util;


public class AmplJSONDocTemplateBuilderTest {
    @org.junit.Test
    public void testTemplate() throws java.io.IOException, java.lang.IllegalAccessException, java.lang.IllegalArgumentException, java.lang.InstantiationException {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        java.util.Set<java.lang.Class<?>> classes = com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(org.jsondoc.core.util.pojo.TemplateObject.class);
        java.util.Map<java.lang.String, java.lang.Object> template = org.jsondoc.core.util.JSONDocTemplateBuilder.build(org.jsondoc.core.util.pojo.TemplateObject.class, classes);
        org.junit.Assert.assertEquals(0, template.get("my_id"));
        org.junit.Assert.assertEquals(0, template.get("idint"));
        org.junit.Assert.assertEquals(0, template.get("idlong"));
        org.junit.Assert.assertEquals("", template.get("name"));
        org.junit.Assert.assertEquals("", template.get("gender"));
        org.junit.Assert.assertEquals(true, template.get("bool"));
        org.junit.Assert.assertEquals(new java.util.ArrayList(), template.get("intarrarr"));
        org.junit.Assert.assertEquals(new org.jsondoc.core.pojo.JSONDocTemplate(), template.get("sub_obj"));
        org.junit.Assert.assertEquals(new java.util.ArrayList(), template.get("untypedlist"));
        org.junit.Assert.assertEquals(new java.util.ArrayList(), template.get("subsubobjarr"));
        org.junit.Assert.assertEquals(new java.util.ArrayList(), template.get("stringlist"));
        org.junit.Assert.assertEquals(new java.util.ArrayList(), template.get("stringarrarr"));
        org.junit.Assert.assertEquals(new java.util.ArrayList(), template.get("integerarr"));
        org.junit.Assert.assertEquals(new java.util.ArrayList(), template.get("stringarr"));
        org.junit.Assert.assertEquals(new java.util.ArrayList(), template.get("intarr"));
        org.junit.Assert.assertEquals(new java.util.ArrayList(), template.get("subobjlist"));
        org.junit.Assert.assertEquals(new java.util.ArrayList(), template.get("wildcardlist"));
        org.junit.Assert.assertEquals(new java.util.ArrayList(), template.get("longlist"));
        org.junit.Assert.assertEquals("", template.get("namechar"));
        org.junit.Assert.assertEquals(new java.util.HashMap(), template.get("map"));
        org.junit.Assert.assertEquals(new java.util.HashMap(), template.get("mapstringinteger"));
        org.junit.Assert.assertEquals(new java.util.HashMap(), template.get("mapsubobjinteger"));
        org.junit.Assert.assertEquals(new java.util.HashMap(), template.get("mapintegersubobj"));
        org.junit.Assert.assertEquals(new java.util.HashMap(), template.get("mapintegerlistsubsubobj"));
        java.lang.System.out.println(mapper.writeValueAsString(template));
    }
}

