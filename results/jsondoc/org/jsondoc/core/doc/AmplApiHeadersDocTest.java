

package org.jsondoc.core.doc;


public class AmplApiHeadersDocTest {
    private org.jsondoc.core.scanner.JSONDocScanner jsondocScanner = new org.jsondoc.core.scanner.DefaultJSONDocScanner();

    @org.jsondoc.core.annotation.Api(description = "ApiHeadersController", name = "ApiHeadersController")
    @org.jsondoc.core.annotation.ApiHeaders(headers = { @org.jsondoc.core.annotation.ApiHeader(name = "H1", description = "h1-description")
     , @org.jsondoc.core.annotation.ApiHeader(name = "H2", description = "h2-description") })
    private class ApiHeadersController {
        @org.jsondoc.core.annotation.ApiMethod(path = "/api-headers-controller-method-one")
        public void apiHeadersMethodOne() {
        }

        // this is a duplicate of the one at the class level, it will not be taken into account when building the doc
        @org.jsondoc.core.annotation.ApiMethod(path = "/api-headers-controller-method-two")
        @org.jsondoc.core.annotation.ApiHeaders(headers = { @org.jsondoc.core.annotation.ApiHeader(name = "H4", description = "h4-description")
         , @org.jsondoc.core.annotation.ApiHeader(name = "H1", description = "h1-description") })
        public void apiHeadersMethodTwo() {
        }
    }

    @org.junit.Test
    public void testApiHeadersOnClass() {
        final org.jsondoc.core.pojo.ApiDoc apiDoc = jsondocScanner.getApiDocs(com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(org.jsondoc.core.doc.AmplApiHeadersDocTest.ApiHeadersController.class), org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        org.junit.Assert.assertEquals("ApiHeadersController", apiDoc.getName());
        for (org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc : apiDoc.getMethods()) {
            if (apiMethodDoc.getPath().contains("/api-headers-controller-method-one")) {
                org.junit.Assert.assertEquals(2, apiMethodDoc.getHeaders().size());
            }
            if (apiMethodDoc.getPath().contains("/api-headers-controller-method-two")) {
                org.junit.Assert.assertEquals(3, apiMethodDoc.getHeaders().size());
            }
        }
    }
}

