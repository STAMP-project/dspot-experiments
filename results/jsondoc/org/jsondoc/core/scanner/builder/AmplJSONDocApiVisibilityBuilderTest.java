

package org.jsondoc.core.scanner.builder;


public class AmplJSONDocApiVisibilityBuilderTest {
    org.jsondoc.core.scanner.JSONDocScanner jsondocScanner = new org.jsondoc.core.scanner.DefaultJSONDocScanner();

    @org.jsondoc.core.annotation.Api(name = "test-type-level-visibility-and-stage", description = "Test type level visibility and stage attributes", visibility = org.jsondoc.core.pojo.ApiVisibility.PUBLIC, stage = org.jsondoc.core.pojo.ApiStage.BETA)
    private class Controller {
        @org.jsondoc.core.annotation.ApiMethod(path = "/inherit")
        public void inherit() {
        }

        @org.jsondoc.core.annotation.ApiMethod(path = "/override", visibility = org.jsondoc.core.pojo.ApiVisibility.PRIVATE, stage = org.jsondoc.core.pojo.ApiStage.GA)
        public void override() {
        }
    }

    @org.jsondoc.core.annotation.Api(name = "test-method-level-visibility-and-stage", description = "Test method level visibility and stage attributes")
    private class Controller2 {
        @org.jsondoc.core.annotation.ApiMethod(path = "/only-method", visibility = org.jsondoc.core.pojo.ApiVisibility.PRIVATE, stage = org.jsondoc.core.pojo.ApiStage.DEPRECATED)
        public void testVisibilityAndStage() {
        }
    }

    @org.junit.Test
    public void testApiVisibility() {
        org.jsondoc.core.pojo.ApiDoc apiDoc = jsondocScanner.getApiDocs(com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(org.jsondoc.core.scanner.builder.AmplJSONDocApiVisibilityBuilderTest.Controller.class), org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVisibility.PUBLIC, apiDoc.getVisibility());
        org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiStage.BETA, apiDoc.getStage());
        for (org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc : apiDoc.getMethods()) {
            if (apiMethodDoc.getPath().contains("/inherit")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVisibility.PUBLIC, apiMethodDoc.getVisibility());
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiStage.BETA, apiMethodDoc.getStage());
            }
            if (apiMethodDoc.getPath().contains("/override")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVisibility.PRIVATE, apiMethodDoc.getVisibility());
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiStage.GA, apiMethodDoc.getStage());
            }
        }
        apiDoc = jsondocScanner.getApiDocs(com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(org.jsondoc.core.scanner.builder.AmplJSONDocApiVisibilityBuilderTest.Controller2.class), org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVisibility.UNDEFINED, apiDoc.getVisibility());
        org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiStage.UNDEFINED, apiDoc.getStage());
        for (org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc : apiDoc.getMethods()) {
            if (apiMethodDoc.getPath().contains("/only-method")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVisibility.PRIVATE, apiMethodDoc.getVisibility());
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiStage.DEPRECATED, apiMethodDoc.getStage());
            }
        }
    }
}

