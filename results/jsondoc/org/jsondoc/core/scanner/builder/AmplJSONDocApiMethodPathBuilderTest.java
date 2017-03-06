

package org.jsondoc.core.scanner.builder;


public class AmplJSONDocApiMethodPathBuilderTest {
    org.jsondoc.core.scanner.JSONDocScanner jsondocScanner = new org.jsondoc.core.scanner.DefaultJSONDocScanner();

    @org.jsondoc.core.annotation.Api(name = "test-path", description = "test-path")
    private class Controller {
        @org.jsondoc.core.annotation.ApiMethod(path = { "/path1" , "/path2" })
        public void path() {
        }
    }

    @org.junit.Test
    public void testPathWithMethodDisplayURI() {
        org.jsondoc.core.pojo.ApiDoc apiDoc = jsondocScanner.getApiDocs(com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(org.jsondoc.core.scanner.builder.AmplJSONDocApiMethodPathBuilderTest.Controller.class), org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        boolean allRight = com.google.common.collect.FluentIterable.from(apiDoc.getMethods()).anyMatch(new com.google.common.base.Predicate<org.jsondoc.core.pojo.ApiMethodDoc>() {
            @java.lang.Override
            public boolean apply(org.jsondoc.core.pojo.ApiMethodDoc input) {
                return (((input.getPath().contains("/path1")) && (input.getPath().contains("/path2"))) && (input.getDisplayedMethodString().contains("/path1"))) && (input.getDisplayedMethodString().contains("/path2"));
            }
        });
        org.junit.Assert.assertTrue(allRight);
    }

    @org.junit.Test
    public void testPathWithMethodDisplayMethod() {
        org.jsondoc.core.pojo.ApiDoc apiDoc = jsondocScanner.getApiDocs(com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(org.jsondoc.core.scanner.builder.AmplJSONDocApiMethodPathBuilderTest.Controller.class), org.jsondoc.core.pojo.JSONDoc.MethodDisplay.METHOD).iterator().next();
        boolean allRight = com.google.common.collect.FluentIterable.from(apiDoc.getMethods()).anyMatch(new com.google.common.base.Predicate<org.jsondoc.core.pojo.ApiMethodDoc>() {
            @java.lang.Override
            public boolean apply(org.jsondoc.core.pojo.ApiMethodDoc input) {
                return (((input.getPath().contains("/path1")) && (input.getPath().contains("/path2"))) && (input.getDisplayedMethodString().contains("path"))) && (!(input.getDisplayedMethodString().contains("/path1")));
            }
        });
        org.junit.Assert.assertTrue(allRight);
    }
}

