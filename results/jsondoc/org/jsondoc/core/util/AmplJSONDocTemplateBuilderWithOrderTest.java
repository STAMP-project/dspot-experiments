

package org.jsondoc.core.util;


public class AmplJSONDocTemplateBuilderWithOrderTest {
    @org.jsondoc.core.annotation.ApiObject(name = "unordered")
    static class Unordered {
        @org.jsondoc.core.annotation.ApiObjectField(name = "xField")
        public java.lang.String x;

        @org.jsondoc.core.annotation.ApiObjectField(name = "aField")
        public java.lang.String a;
    }

    @org.jsondoc.core.annotation.ApiObject(name = "ordered")
    static class Ordered {
        @org.jsondoc.core.annotation.ApiObjectField(name = "xField", order = 1)
        public java.lang.String x;

        @org.jsondoc.core.annotation.ApiObjectField(name = "aField", order = 2)
        public java.lang.String a;

        @org.jsondoc.core.annotation.ApiObjectField(name = "bField", order = 2)
        public java.lang.String b;
    }

    @org.junit.Test
    public void thatTemplateIsMappedToStringCorrectly() throws java.lang.Exception {
        final com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        java.util.Set<java.lang.Class<?>> classes = com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(org.jsondoc.core.util.AmplJSONDocTemplateBuilderWithOrderTest.Unordered.class, org.jsondoc.core.util.AmplJSONDocTemplateBuilderWithOrderTest.Ordered.class);
        java.util.Map<java.lang.String, java.lang.Object> unorderedTemplate = org.jsondoc.core.util.JSONDocTemplateBuilder.build(org.jsondoc.core.util.AmplJSONDocTemplateBuilderWithOrderTest.Unordered.class, classes);
        org.junit.Assert.assertEquals("{\"aField\":\"\",\"xField\":\"\"}", mapper.writeValueAsString(unorderedTemplate));
        java.util.Map<java.lang.String, java.lang.Object> orderedTemplate = org.jsondoc.core.util.JSONDocTemplateBuilder.build(org.jsondoc.core.util.AmplJSONDocTemplateBuilderWithOrderTest.Ordered.class, classes);
        org.junit.Assert.assertEquals("{\"xField\":\"\",\"aField\":\"\",\"bField\":\"\"}", mapper.writeValueAsString(orderedTemplate));
    }
}

