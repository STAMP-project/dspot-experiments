

package org.jsondoc.core.util;


public class AmplStackOverflowTemplateBuilderTest {
    private com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    @org.junit.Test
    public void testTemplate() throws com.fasterxml.jackson.core.JsonGenerationException, com.fasterxml.jackson.databind.JsonMappingException, java.io.IOException, java.lang.IllegalAccessException, java.lang.IllegalArgumentException, java.lang.InstantiationException {
        java.util.Set<java.lang.Class<?>> classes = com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(org.jsondoc.core.util.pojo.StackOverflowTemplateSelf.class, org.jsondoc.core.util.pojo.StackOverflowTemplateObjectOne.class, org.jsondoc.core.util.pojo.StackOverflowTemplateObjectTwo.class);
        org.jsondoc.core.util.pojo.StackOverflowTemplateSelf objectSelf = new org.jsondoc.core.util.pojo.StackOverflowTemplateSelf();
        java.util.Map<java.lang.String, java.lang.Object> template = org.jsondoc.core.util.JSONDocTemplateBuilder.build(objectSelf.getClass(), classes);
        java.lang.System.out.println(mapper.writeValueAsString(template));
        org.jsondoc.core.util.pojo.StackOverflowTemplateObjectOne objectOne = new org.jsondoc.core.util.pojo.StackOverflowTemplateObjectOne();
        template = org.jsondoc.core.util.JSONDocTemplateBuilder.build(objectOne.getClass(), classes);
        java.lang.System.out.println(mapper.writeValueAsString(template));
        org.jsondoc.core.util.pojo.StackOverflowTemplateObjectTwo objectTwo = new org.jsondoc.core.util.pojo.StackOverflowTemplateObjectTwo();
        template = org.jsondoc.core.util.JSONDocTemplateBuilder.build(objectTwo.getClass(), classes);
        java.lang.System.out.println(mapper.writeValueAsString(template));
    }

    @org.junit.Test
    public void typeOneTwo() throws com.fasterxml.jackson.core.JsonGenerationException, com.fasterxml.jackson.databind.JsonMappingException, java.io.IOException {
        java.util.Set<java.lang.Class<?>> classes = com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(org.jsondoc.core.util.pojo.NotAnnotatedStackOverflowObjectOne.class, org.jsondoc.core.util.pojo.NotAnnotatedStackOverflowObjectTwo.class);
        org.jsondoc.core.util.pojo.NotAnnotatedStackOverflowObjectOne typeOne = new org.jsondoc.core.util.pojo.NotAnnotatedStackOverflowObjectOne();
        java.util.Map<java.lang.String, java.lang.Object> template = org.jsondoc.core.util.JSONDocTemplateBuilder.build(typeOne.getClass(), classes);
        java.lang.System.out.println(mapper.writeValueAsString(template));
    }
}

