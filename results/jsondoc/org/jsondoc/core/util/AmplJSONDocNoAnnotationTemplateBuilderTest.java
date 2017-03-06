

package org.jsondoc.core.util;


public class AmplJSONDocNoAnnotationTemplateBuilderTest {
    @org.junit.Test
    public void testTemplate() throws java.io.IOException, java.lang.IllegalAccessException, java.lang.IllegalArgumentException, java.lang.InstantiationException {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        java.util.Set<java.lang.Class<?>> classes = com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(org.jsondoc.core.util.pojo.NoAnnotationPojo.class);
        java.util.Map<java.lang.String, java.lang.Object> template = org.jsondoc.core.util.JSONDocTemplateBuilder.build(org.jsondoc.core.util.pojo.NoAnnotationPojo.class, classes);
        java.lang.System.out.println(mapper.writeValueAsString(template));
    }
}

