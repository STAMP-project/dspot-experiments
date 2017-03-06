

package org.jsondoc.core.scanner.builder;


public class AmplJSONDocApiObjectBuilderTest {
    org.jsondoc.core.scanner.JSONDocScanner jsondocScanner = new org.jsondoc.core.scanner.DefaultJSONDocScanner();

    @org.junit.Test
    public void testApiObjectDocWithHibernateValidator() {
        java.util.Set<org.jsondoc.core.pojo.ApiObjectDoc> apiObjectDocs = jsondocScanner.getApiObjectDocs(com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(org.jsondoc.core.util.pojo.HibernateValidatorPojo.class));
        java.util.Iterator<org.jsondoc.core.pojo.ApiObjectDoc> iterator = apiObjectDocs.iterator();
        org.jsondoc.core.pojo.ApiObjectDoc next = iterator.next();
        java.util.Set<org.jsondoc.core.pojo.ApiObjectFieldDoc> fields = next.getFields();
        for (org.jsondoc.core.pojo.ApiObjectFieldDoc apiObjectFieldDoc : fields) {
            if (apiObjectFieldDoc.getName().equals("id")) {
                java.util.Iterator<java.lang.String> formats = apiObjectFieldDoc.getFormat().iterator();
                org.junit.Assert.assertEquals("a not empty id", formats.next());
                org.junit.Assert.assertEquals("length must be between 2 and 2147483647", formats.next());
                org.junit.Assert.assertEquals("must be less than or equal to 9", formats.next());
            }
        }
    }
}

