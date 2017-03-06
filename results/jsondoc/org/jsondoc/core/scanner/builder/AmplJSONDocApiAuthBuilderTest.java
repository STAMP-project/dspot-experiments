

package org.jsondoc.core.scanner.builder;


public class AmplJSONDocApiAuthBuilderTest {
    org.jsondoc.core.scanner.JSONDocScanner jsondocScanner = new org.jsondoc.core.scanner.DefaultJSONDocScanner();

    @org.jsondoc.core.annotation.Api(name = "test-token-auth", description = "Test token auth")
    @org.jsondoc.core.annotation.ApiAuthToken(roles = { "" }, testtokens = { "abc" , "cde" })
    private class Controller {
        @org.jsondoc.core.annotation.ApiMethod(path = "/inherit")
        public void inherit() {
        }

        @org.jsondoc.core.annotation.ApiMethod(path = "/override")
        @org.jsondoc.core.annotation.ApiAuthToken(roles = { "" }, scheme = "Bearer", testtokens = { "xyz" })
        public void override() {
        }
    }

    @org.junit.Test
    public void testApiAuthToken() {
        org.jsondoc.core.pojo.ApiDoc apiDoc = jsondocScanner.getApiDocs(com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(org.jsondoc.core.scanner.builder.AmplJSONDocApiAuthBuilderTest.Controller.class), org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        org.junit.Assert.assertEquals("TOKEN", apiDoc.getAuth().getType());
        org.junit.Assert.assertEquals("", apiDoc.getAuth().getScheme());
        org.junit.Assert.assertEquals("abc", apiDoc.getAuth().getTesttokens().iterator().next());
        for (org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc : apiDoc.getMethods()) {
            if (apiMethodDoc.getPath().contains("/inherit")) {
                org.junit.Assert.assertEquals("TOKEN", apiMethodDoc.getAuth().getType());
                org.junit.Assert.assertEquals("", apiMethodDoc.getAuth().getScheme());
                org.junit.Assert.assertEquals("abc", apiMethodDoc.getAuth().getTesttokens().iterator().next());
            }
            if (apiMethodDoc.getPath().contains("/override")) {
                org.junit.Assert.assertEquals("TOKEN", apiMethodDoc.getAuth().getType());
                org.junit.Assert.assertEquals("Bearer", apiMethodDoc.getAuth().getScheme());
                org.junit.Assert.assertEquals("xyz", apiMethodDoc.getAuth().getTesttokens().iterator().next());
            }
        }
    }
}

