

package org.jsondoc.core.doc;


public class AmplApiDocTest {
    private org.jsondoc.core.scanner.JSONDocScanner jsondocScanner = new org.jsondoc.core.scanner.DefaultJSONDocScanner();

    @org.jsondoc.core.annotation.Api(description = "An interface controller", name = "interface-controller")
    private interface InterfaceController {
        @org.jsondoc.core.annotation.ApiMethod(path = "/interface", verb = org.jsondoc.core.pojo.ApiVerb.GET)
        public java.lang.String inter();
    }

    @java.lang.SuppressWarnings(value = "unused")
    private class InterfaceControllerImpl implements org.jsondoc.core.doc.AmplApiDocTest.InterfaceController {
        @java.lang.Override
        public java.lang.String inter() {
            return null;
        }
    }

    @org.jsondoc.core.annotation.Api(name = "test-controller", description = "a-test-controller")
    @org.jsondoc.core.annotation.ApiVersion(since = "1.0", until = "2.12")
    @org.jsondoc.core.annotation.ApiAuthNone
    private class TestController {
        @org.jsondoc.core.annotation.ApiMethod(path = "/name", verb = org.jsondoc.core.pojo.ApiVerb.GET, description = "a-test-method")
        @org.jsondoc.core.annotation.ApiResponseObject
        public java.lang.String name(@org.jsondoc.core.annotation.ApiPathParam(name = "name")
        java.lang.String name, @org.jsondoc.core.annotation.ApiBodyObject
        java.lang.String body) {
            return null;
        }

        @org.jsondoc.core.annotation.ApiMethod(path = "/age", verb = org.jsondoc.core.pojo.ApiVerb.GET, description = "a-test-method", responsestatuscode = "204")
        @org.jsondoc.core.annotation.ApiResponseObject
        public java.lang.Integer age(@org.jsondoc.core.annotation.ApiPathParam(name = "age")
        java.lang.Integer age, @org.jsondoc.core.annotation.ApiBodyObject
        java.lang.Integer body) {
            return null;
        }

        @org.jsondoc.core.annotation.ApiMethod(path = "/avg", verb = org.jsondoc.core.pojo.ApiVerb.GET, description = "a-test-method")
        @org.jsondoc.core.annotation.ApiResponseObject
        public java.lang.Long avg(@org.jsondoc.core.annotation.ApiPathParam(name = "avg")
        java.lang.Long avg, @org.jsondoc.core.annotation.ApiBodyObject
        java.lang.Long body) {
            return null;
        }

        @org.jsondoc.core.annotation.ApiMethod(path = "/map", verb = org.jsondoc.core.pojo.ApiVerb.GET, description = "a-test-method")
        @org.jsondoc.core.annotation.ApiResponseObject
        public java.util.Map<java.lang.String, java.lang.Integer> map(@org.jsondoc.core.annotation.ApiPathParam(name = "map")
        java.util.Map<java.lang.String, java.lang.Integer> map, @org.jsondoc.core.annotation.ApiBodyObject
        java.util.Map<java.lang.String, java.lang.Integer> body) {
            return null;
        }

        @java.lang.SuppressWarnings(value = "rawtypes")
        @org.jsondoc.core.annotation.ApiMethod(path = "/unparametrizedList", verb = org.jsondoc.core.pojo.ApiVerb.GET, description = "a-test-method")
        @org.jsondoc.core.annotation.ApiResponseObject
        public java.util.List unparametrizedList(@org.jsondoc.core.annotation.ApiPathParam(name = "unparametrizedList")
        java.util.List unparametrizedList, @org.jsondoc.core.annotation.ApiBodyObject
        java.util.List body) {
            return null;
        }

        @org.jsondoc.core.annotation.ApiMethod(path = "/parametrizedList", verb = org.jsondoc.core.pojo.ApiVerb.GET, description = "a-test-method")
        @org.jsondoc.core.annotation.ApiResponseObject
        public java.util.List<java.lang.String> parametrizedList(@org.jsondoc.core.annotation.ApiPathParam(name = "parametrizedList")
        java.util.List<java.lang.String> parametrizedList, @org.jsondoc.core.annotation.ApiBodyObject
        java.util.List<java.lang.String> body) {
            return null;
        }

        @org.jsondoc.core.annotation.ApiMethod(path = "/wildcardParametrizedList", verb = org.jsondoc.core.pojo.ApiVerb.GET, description = "a-test-method")
        @org.jsondoc.core.annotation.ApiResponseObject
        public java.util.List<?> wildcardParametrizedList(@org.jsondoc.core.annotation.ApiPathParam(name = "wildcardParametrizedList")
        java.util.List<?> wildcardParametrizedList, @org.jsondoc.core.annotation.ApiBodyObject
        java.util.List<?> body) {
            return null;
        }

        @org.jsondoc.core.annotation.ApiMethod(path = "/LongArray", verb = org.jsondoc.core.pojo.ApiVerb.GET, description = "a-test-method")
        @org.jsondoc.core.annotation.ApiResponseObject
        public java.lang.Long[] LongArray(@org.jsondoc.core.annotation.ApiPathParam(name = "LongArray")
        java.lang.Long[] LongArray, @org.jsondoc.core.annotation.ApiBodyObject
        java.lang.Long[] body) {
            return null;
        }

        @org.jsondoc.core.annotation.ApiMethod(path = "/longArray", verb = org.jsondoc.core.pojo.ApiVerb.GET, description = "a-test-method")
        @org.jsondoc.core.annotation.ApiResponseObject
        public long[] longArray(@org.jsondoc.core.annotation.ApiPathParam(name = "longArray")
        long[] LongArray, @org.jsondoc.core.annotation.ApiBodyObject
        long[] body) {
            return null;
        }

        @org.jsondoc.core.annotation.ApiMethod(path = "/version", verb = org.jsondoc.core.pojo.ApiVerb.GET, description = "a-test-method for api version feature")
        @org.jsondoc.core.annotation.ApiVersion(since = "1.0", until = "2.12")
        @org.jsondoc.core.annotation.ApiResponseObject
        public java.lang.String version(@org.jsondoc.core.annotation.ApiPathParam(name = "version")
        java.lang.String version, @org.jsondoc.core.annotation.ApiBodyObject
        java.lang.String body) {
            return null;
        }

        @org.jsondoc.core.annotation.ApiMethod(path = "/child", description = "A method returning a child", verb = org.jsondoc.core.pojo.ApiVerb.GET)
        @org.jsondoc.core.annotation.ApiResponseObject
        public org.jsondoc.core.util.pojo.Child child(@org.jsondoc.core.annotation.ApiPathParam(name = "child")
        org.jsondoc.core.util.pojo.Child child, @org.jsondoc.core.annotation.ApiBodyObject
        org.jsondoc.core.util.pojo.Child body) {
            return null;
        }

        @org.jsondoc.core.annotation.ApiMethod(path = "/pizza", description = "A method returning a pizza", verb = org.jsondoc.core.pojo.ApiVerb.GET)
        @org.jsondoc.core.annotation.ApiResponseObject
        public org.jsondoc.core.util.pojo.Pizza pizza(@org.jsondoc.core.annotation.ApiPathParam(name = "pizza")
        org.jsondoc.core.util.pojo.Pizza pizza, @org.jsondoc.core.annotation.ApiBodyObject
        org.jsondoc.core.util.pojo.Pizza body) {
            return null;
        }

        @org.jsondoc.core.annotation.ApiMethod(path = "/multiple-request-methods", verb = { org.jsondoc.core.pojo.ApiVerb.GET , org.jsondoc.core.pojo.ApiVerb.POST }, description = "a-test-method-with-multiple-request-methods")
        @org.jsondoc.core.annotation.ApiResponseObject
        public java.lang.Integer multipleRequestMethods(@org.jsondoc.core.annotation.ApiPathParam(name = "multiple-request-methods")
        java.lang.Integer multipleRequestMethods, @org.jsondoc.core.annotation.ApiBodyObject
        java.lang.Integer body) {
            return null;
        }
    }

    @org.jsondoc.core.annotation.Api(name = "test-controller-with-basic-auth", description = "a-test-controller with basic auth annotation")
    @org.jsondoc.core.annotation.ApiAuthBasic(roles = { "ROLE_USER" , "ROLE_ADMIN" }, testusers = { @org.jsondoc.core.annotation.ApiAuthBasicUser(username = "test-username", password = "test-password") })
    private class TestControllerWithBasicAuth {
        @org.jsondoc.core.annotation.ApiMethod(path = "/basicAuth", description = "A method with basic auth", verb = org.jsondoc.core.pojo.ApiVerb.GET)
        @org.jsondoc.core.annotation.ApiAuthBasic(roles = { "ROLE_USER" }, testusers = { @org.jsondoc.core.annotation.ApiAuthBasicUser(username = "test-username", password = "test-password") })
        public java.lang.String basicAuth() {
            return null;
        }

        @org.jsondoc.core.annotation.ApiMethod(path = "/noAuth", description = "A method with no auth", verb = org.jsondoc.core.pojo.ApiVerb.GET)
        @org.jsondoc.core.annotation.ApiAuthNone
        public java.lang.String noAuth() {
            return null;
        }

        @org.jsondoc.core.annotation.ApiMethod(path = "/undefinedAuthWithAuthOnClass", description = "A method with undefined auth but with auth info on class declaration", verb = org.jsondoc.core.pojo.ApiVerb.GET)
        public java.lang.String undefinedAuthWithAuthOnClass() {
            return null;
        }
    }

    @org.jsondoc.core.annotation.Api(name = "test-controller-with-no-auth-annotation", description = "a-test-controller with no auth annotation")
    private class TestControllerWithNoAuthAnnotation {
        @org.jsondoc.core.annotation.ApiMethod(path = "/basicAuth", description = "A method with basic auth", verb = org.jsondoc.core.pojo.ApiVerb.GET)
        @org.jsondoc.core.annotation.ApiAuthBasic(roles = { "ROLE_USER" }, testusers = { @org.jsondoc.core.annotation.ApiAuthBasicUser(username = "test-username", password = "test-password") })
        public java.lang.String basicAuth() {
            return null;
        }

        @org.jsondoc.core.annotation.ApiMethod(path = "/noAuth", description = "A method with no auth", verb = org.jsondoc.core.pojo.ApiVerb.GET)
        @org.jsondoc.core.annotation.ApiAuthNone
        public java.lang.String noAuth() {
            return null;
        }

        @org.jsondoc.core.annotation.ApiMethod(path = "/undefinedAuthWithoutAuthOnClass", description = "A method with undefined auth and without auth info on class declaration", verb = org.jsondoc.core.pojo.ApiVerb.GET)
        public java.lang.String undefinedAuthWithoutAuthOnClass() {
            return null;
        }
    }

    @org.jsondoc.core.annotation.Api(name = "test-old-style-servlets", description = "a-test-old-style-servlet")
    private class TestOldStyleServlets {
        @org.jsondoc.core.annotation.ApiMethod(path = "/oldStyle", description = "A method params on method level", verb = org.jsondoc.core.pojo.ApiVerb.GET)
        @org.jsondoc.core.annotation.ApiParams(pathparams = { @org.jsondoc.core.annotation.ApiPathParam(name = "name", clazz = java.lang.String.class) })
        public java.lang.String oldStyle() {
            return null;
        }

        @org.jsondoc.core.annotation.ApiMethod(path = "/oldStyleWithList", description = "A method params on method level", verb = org.jsondoc.core.pojo.ApiVerb.GET)
        @org.jsondoc.core.annotation.ApiParams(pathparams = { @org.jsondoc.core.annotation.ApiPathParam(name = "name", clazz = java.util.List.class) })
        public java.lang.String oldStyleWithList() {
            return null;
        }

        @org.jsondoc.core.annotation.ApiMethod(path = "/oldStyleWithMap", description = "A method params on method level", verb = org.jsondoc.core.pojo.ApiVerb.GET)
        @org.jsondoc.core.annotation.ApiParams(pathparams = { @org.jsondoc.core.annotation.ApiPathParam(name = "name", clazz = java.util.Map.class) })
        public java.lang.String oldStyleWithMap() {
            return null;
        }

        @org.jsondoc.core.annotation.ApiMethod(path = "/oldStyleMixed", description = "A method params on method level", verb = org.jsondoc.core.pojo.ApiVerb.GET)
        @org.jsondoc.core.annotation.ApiParams(pathparams = { @org.jsondoc.core.annotation.ApiPathParam(name = "name", clazz = java.lang.String.class)
         , @org.jsondoc.core.annotation.ApiPathParam(name = "age", clazz = java.lang.Integer.class)
         , @org.jsondoc.core.annotation.ApiPathParam(name = "undefined") }, queryparams = { @org.jsondoc.core.annotation.ApiQueryParam(name = "q", clazz = java.lang.String.class, defaultvalue = "qTest") })
        public java.lang.String oldStyleMixed(@org.jsondoc.core.annotation.ApiPathParam(name = "age")
        java.lang.Integer age) {
            return null;
        }

        @org.jsondoc.core.annotation.ApiMethod(path = "/oldStyleResponseObject", description = "A method with populated ApiResponseObject annotation", verb = org.jsondoc.core.pojo.ApiVerb.GET)
        @org.jsondoc.core.annotation.ApiResponseObject(clazz = java.util.List.class)
        public void oldStyleResponseObject() {
            return ;
        }

        @org.jsondoc.core.annotation.ApiMethod(path = "/oldStyleBodyObject", description = "A method with populated ApiBodyObject annotation", verb = org.jsondoc.core.pojo.ApiVerb.GET)
        @org.jsondoc.core.annotation.ApiBodyObject(clazz = java.util.List.class)
        public void oldStyleBodyObject() {
            return ;
        }
    }

    @org.jsondoc.core.annotation.Api(name = "test-errors-warnings-hints", description = "a-test-for-incomplete-documentation")
    private class TestErrorsAndWarningsAndHints {
        @org.jsondoc.core.annotation.ApiMethod
        public java.lang.String oldStyle() {
            return null;
        }
    }

    @org.jsondoc.core.annotation.Api(name = "test-errors-warnings-hints-method-display-as-summary", description = "a-test-for-incomplete-documentation-for-method-display-summary")
    private class TestErrorsAndWarningsAndHintsMethodSummary {
        @org.jsondoc.core.annotation.ApiMethod
        public java.lang.String summary() {
            return null;
        }
    }

    @org.jsondoc.core.annotation.Api(name = "test-declared-methods", description = "a-test-for-declared-methods")
    private class TestDeclaredMethods {
        @org.jsondoc.core.annotation.ApiMethod(path = "/protectedMethod")
        protected java.lang.String protectedMethod() {
            return null;
        }

        @org.jsondoc.core.annotation.ApiMethod(path = "/privateMethod")
        private java.lang.String privateMethod() {
            return null;
        }
    }

    @org.jsondoc.core.annotation.Api(name = "ISSUE-110", description = "ISSUE-110")
    private class TestMultipleParamsWithSameMethod {
        @org.jsondoc.core.annotation.ApiMethod(path = "/search", description = "search one by title")
        @org.jsondoc.core.annotation.ApiResponseObject
        public java.util.List findByTitle(@org.jsondoc.core.annotation.ApiQueryParam(name = "title")
        java.lang.String title) {
            return null;
        }

        @org.jsondoc.core.annotation.ApiMethod(path = "/search", description = "search one by content")
        @org.jsondoc.core.annotation.ApiResponseObject
        public java.util.List findByContent(@org.jsondoc.core.annotation.ApiQueryParam(name = "content")
        java.lang.String content) {
            return null;
        }

        @org.jsondoc.core.annotation.ApiMethod(path = "/search", description = "search one by content and field")
        @org.jsondoc.core.annotation.ApiResponseObject
        public java.util.List findByContent(@org.jsondoc.core.annotation.ApiQueryParam(name = "content")
        java.lang.String content, @org.jsondoc.core.annotation.ApiQueryParam(name = "field")
        java.lang.String field) {
            return null;
        }
    }

    @org.junit.Test
    public void testApiErrorsDoc() throws java.lang.Exception {
        final org.jsondoc.core.pojo.ApiDoc apiDoc = jsondocScanner.getApiDocs(com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(org.jsondoc.core.util.controller.Test3Controller.class), org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        final java.util.Set<org.jsondoc.core.pojo.ApiMethodDoc> methods = apiDoc.getMethods();
        final org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc = methods.iterator().next();
        final java.util.List<org.jsondoc.core.pojo.ApiErrorDoc> apiErrors = apiMethodDoc.getApierrors();
        org.junit.Assert.assertEquals(1, methods.size());
        org.junit.Assert.assertEquals(3, apiErrors.size());
        org.junit.Assert.assertEquals("1000", apiErrors.get(0).getCode());
        org.junit.Assert.assertEquals("method-level annotation should be applied", "A test error #1", apiErrors.get(0).getDescription());
        org.junit.Assert.assertEquals("2000", apiErrors.get(1).getCode());
        org.junit.Assert.assertEquals("400", apiErrors.get(2).getCode());
    }

    @org.junit.Test
    public void testApiDoc() {
        java.util.Set<java.lang.Class<?>> classes = new java.util.HashSet<java.lang.Class<?>>();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.TestController.class);
        org.jsondoc.core.pojo.ApiDoc apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        org.junit.Assert.assertEquals("test-controller", apiDoc.getName());
        org.junit.Assert.assertEquals("a-test-controller", apiDoc.getDescription());
        org.junit.Assert.assertEquals("1.0", apiDoc.getSupportedversions().getSince());
        org.junit.Assert.assertEquals("2.12", apiDoc.getSupportedversions().getUntil());
        org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiAuthType.NONE.name(), apiDoc.getAuth().getType());
        org.junit.Assert.assertEquals(org.jsondoc.core.scanner.DefaultJSONDocScanner.ANONYMOUS, apiDoc.getAuth().getRoles().get(0));
        for (org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc : apiDoc.getMethods()) {
            if (apiMethodDoc.getPath().contains("/name")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("string", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("string", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("200 - OK", apiMethodDoc.getResponsestatuscode());
                for (org.jsondoc.core.pojo.ApiParamDoc apiParamDoc : apiMethodDoc.getPathparameters()) {
                    if (apiParamDoc.getName().equals("name")) {
                        org.junit.Assert.assertEquals("string", apiParamDoc.getJsondocType().getOneLineText());
                    }
                }
            }
            if (apiMethodDoc.getPath().contains("/age")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("204", apiMethodDoc.getResponsestatuscode());
                org.junit.Assert.assertEquals("integer", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("integer", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
                for (org.jsondoc.core.pojo.ApiParamDoc apiParamDoc : apiMethodDoc.getPathparameters()) {
                    if (apiParamDoc.getName().equals("age")) {
                        org.junit.Assert.assertEquals("integer", apiParamDoc.getJsondocType().getOneLineText());
                    }
                }
            }
            if (apiMethodDoc.getPath().contains("/avg")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("long", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("long", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
                for (org.jsondoc.core.pojo.ApiParamDoc apiParamDoc : apiMethodDoc.getPathparameters()) {
                    if (apiParamDoc.getName().equals("avg")) {
                        org.junit.Assert.assertEquals("long", apiParamDoc.getJsondocType().getOneLineText());
                    }
                }
            }
            if (apiMethodDoc.getPath().contains("/map")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("map[string, integer]", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("map[string, integer]", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
                for (org.jsondoc.core.pojo.ApiParamDoc apiParamDoc : apiMethodDoc.getPathparameters()) {
                    if (apiParamDoc.getName().equals("map")) {
                        org.junit.Assert.assertEquals("map[string, integer]", apiParamDoc.getJsondocType().getOneLineText());
                    }
                }
            }
            if (apiMethodDoc.getPath().contains("/parametrizedList")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("list of string", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("list of string", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
                for (org.jsondoc.core.pojo.ApiParamDoc apiParamDoc : apiMethodDoc.getPathparameters()) {
                    if (apiParamDoc.getName().equals("parametrizedList")) {
                        org.junit.Assert.assertEquals("list of string", apiParamDoc.getJsondocType().getOneLineText());
                    }
                }
            }
            if (apiMethodDoc.getPath().contains("/wildcardParametrizedList")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("list of wildcard", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("list of wildcard", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
                for (org.jsondoc.core.pojo.ApiParamDoc apiParamDoc : apiMethodDoc.getPathparameters()) {
                    if (apiParamDoc.getName().equals("wildcardParametrizedList")) {
                        org.junit.Assert.assertEquals("list of wildcard", apiParamDoc.getJsondocType().getOneLineText());
                    }
                }
            }
            if (apiMethodDoc.getPath().contains("/LongArray")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("array of long", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("array of long", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
                for (org.jsondoc.core.pojo.ApiParamDoc apiParamDoc : apiMethodDoc.getPathparameters()) {
                    if (apiParamDoc.getName().equals("LongArray")) {
                        org.junit.Assert.assertEquals("array of long", apiParamDoc.getJsondocType().getOneLineText());
                    }
                }
            }
            if (apiMethodDoc.getPath().contains("/longArray")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("array of long", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("array of long", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
                for (org.jsondoc.core.pojo.ApiParamDoc apiParamDoc : apiMethodDoc.getPathparameters()) {
                    if (apiParamDoc.getName().equals("longArray")) {
                        org.junit.Assert.assertEquals("array of long", apiParamDoc.getJsondocType().getOneLineText());
                    }
                }
            }
            if (apiMethodDoc.getPath().contains("/version")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("1.0", apiMethodDoc.getSupportedversions().getSince());
                org.junit.Assert.assertEquals("2.12", apiMethodDoc.getSupportedversions().getUntil());
            }
            if (apiMethodDoc.getPath().contains("/child")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("child", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
            }
            if (apiMethodDoc.getPath().contains("/pizza")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("customPizzaObject", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
            }
            if (apiMethodDoc.getPath().contains("/multiple-request-methods")) {
                org.junit.Assert.assertEquals(2, apiMethodDoc.getVerb().size());
                java.util.Iterator<org.jsondoc.core.pojo.ApiVerb> iterator = apiMethodDoc.getVerb().iterator();
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, iterator.next());
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.POST, iterator.next());
            }
        }
        classes.clear();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.TestControllerWithBasicAuth.class);
        apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        org.junit.Assert.assertEquals("test-controller-with-basic-auth", apiDoc.getName());
        org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiAuthType.BASIC_AUTH.name(), apiDoc.getAuth().getType());
        org.junit.Assert.assertEquals("ROLE_USER", apiDoc.getAuth().getRoles().get(0));
        org.junit.Assert.assertEquals("ROLE_ADMIN", apiDoc.getAuth().getRoles().get(1));
        org.junit.Assert.assertTrue(((apiDoc.getAuth().getTestusers().size()) > 0));
        for (org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc : apiDoc.getMethods()) {
            if (apiMethodDoc.getPath().contains("/basicAuth")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiAuthType.BASIC_AUTH.name(), apiMethodDoc.getAuth().getType());
                org.junit.Assert.assertEquals("ROLE_USER", apiMethodDoc.getAuth().getRoles().get(0));
                org.junit.Assert.assertTrue(((apiMethodDoc.getAuth().getTestusers().size()) > 0));
            }
            if (apiMethodDoc.getPath().contains("/noAuth")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiAuthType.NONE.name(), apiMethodDoc.getAuth().getType());
                org.junit.Assert.assertEquals(org.jsondoc.core.scanner.DefaultJSONDocScanner.ANONYMOUS, apiMethodDoc.getAuth().getRoles().get(0));
            }
            if (apiMethodDoc.getPath().contains("/undefinedAuthWithAuthOnClass")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiAuthType.BASIC_AUTH.name(), apiMethodDoc.getAuth().getType());
                org.junit.Assert.assertEquals("ROLE_USER", apiMethodDoc.getAuth().getRoles().get(0));
                org.junit.Assert.assertEquals("ROLE_ADMIN", apiMethodDoc.getAuth().getRoles().get(1));
            }
        }
        classes.clear();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.TestControllerWithNoAuthAnnotation.class);
        apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        org.junit.Assert.assertEquals("test-controller-with-no-auth-annotation", apiDoc.getName());
        org.junit.Assert.assertNull(apiDoc.getAuth());
        for (org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc : apiDoc.getMethods()) {
            if (apiMethodDoc.getPath().contains("/basicAuth")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiAuthType.BASIC_AUTH.name(), apiMethodDoc.getAuth().getType());
                org.junit.Assert.assertEquals("ROLE_USER", apiMethodDoc.getAuth().getRoles().get(0));
                org.junit.Assert.assertTrue(((apiMethodDoc.getAuth().getTestusers().size()) > 0));
            }
            if (apiMethodDoc.getPath().contains("/noAuth")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiAuthType.NONE.name(), apiMethodDoc.getAuth().getType());
                org.junit.Assert.assertEquals(org.jsondoc.core.scanner.DefaultJSONDocScanner.ANONYMOUS, apiMethodDoc.getAuth().getRoles().get(0));
            }
            if (apiMethodDoc.getPath().contains("/undefinedAuthWithoutAuthOnClass")) {
                org.junit.Assert.assertNull(apiMethodDoc.getAuth());
            }
        }
        classes.clear();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.TestOldStyleServlets.class);
        apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        org.junit.Assert.assertEquals("test-old-style-servlets", apiDoc.getName());
        for (org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc : apiDoc.getMethods()) {
            if (apiMethodDoc.getPath().contains("/oldStyle")) {
                org.junit.Assert.assertEquals(1, apiMethodDoc.getPathparameters().size());
            }
            if (apiMethodDoc.getPath().contains("/oldStyleWithList")) {
                org.junit.Assert.assertEquals(1, apiMethodDoc.getPathparameters().size());
            }
            if (apiMethodDoc.getPath().contains("/oldStyleWithMap")) {
                org.junit.Assert.assertEquals(1, apiMethodDoc.getPathparameters().size());
            }
            if (apiMethodDoc.getPath().contains("/oldStyleMixed")) {
                org.junit.Assert.assertEquals(3, apiMethodDoc.getPathparameters().size());
                org.junit.Assert.assertEquals(1, apiMethodDoc.getQueryparameters().size());
                org.junit.Assert.assertEquals("qTest", apiMethodDoc.getQueryparameters().iterator().next().getDefaultvalue());
            }
            if (apiMethodDoc.getPath().contains("/oldStyleResponseObject")) {
                org.junit.Assert.assertEquals("list", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
            }
            if (apiMethodDoc.getPath().contains("/oldStyleBodyObject")) {
                org.junit.Assert.assertEquals("list", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
            }
        }
        classes.clear();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.TestErrorsAndWarningsAndHints.class);
        apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        org.junit.Assert.assertEquals("test-errors-warnings-hints", apiDoc.getName());
        org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc = apiDoc.getMethods().iterator().next();
        org.junit.Assert.assertEquals(1, apiMethodDoc.getJsondocerrors().size());
        org.junit.Assert.assertEquals(1, apiMethodDoc.getJsondocwarnings().size());
        org.junit.Assert.assertEquals(2, apiMethodDoc.getJsondochints().size());
        classes.clear();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.TestErrorsAndWarningsAndHintsMethodSummary.class);
        apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.SUMMARY).iterator().next();
        apiMethodDoc = apiDoc.getMethods().iterator().next();
        org.junit.Assert.assertEquals(1, apiMethodDoc.getJsondocerrors().size());
        org.junit.Assert.assertEquals(1, apiMethodDoc.getJsondocwarnings().size());
        org.junit.Assert.assertEquals(3, apiMethodDoc.getJsondochints().size());
        classes.clear();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.InterfaceController.class);
        apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        org.junit.Assert.assertEquals("interface-controller", apiDoc.getName());
        apiMethodDoc = apiDoc.getMethods().iterator().next();
        org.junit.Assert.assertNotNull(apiMethodDoc);
        org.junit.Assert.assertEquals("/interface", apiMethodDoc.getPath().iterator().next());
        classes.clear();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.TestDeclaredMethods.class);
        apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        org.junit.Assert.assertEquals("test-declared-methods", apiDoc.getName());
        org.junit.Assert.assertEquals(2, apiDoc.getMethods().size());
        classes.clear();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.TestMultipleParamsWithSameMethod.class);
        apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        org.junit.Assert.assertEquals(3, apiDoc.getMethods().size());
    }

    /* amplification of org.jsondoc.core.doc.ApiDocTest#testApiDoc */
    @org.junit.Test(timeout = 10000)
    public void testApiDoc_cf396() {
        java.util.Set<java.lang.Class<?>> classes = new java.util.HashSet<java.lang.Class<?>>();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.TestController.class);
        org.jsondoc.core.pojo.ApiDoc apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        org.junit.Assert.assertEquals("test-controller", apiDoc.getName());
        org.junit.Assert.assertEquals("a-test-controller", apiDoc.getDescription());
        org.junit.Assert.assertEquals("1.0", apiDoc.getSupportedversions().getSince());
        org.junit.Assert.assertEquals("2.12", apiDoc.getSupportedversions().getUntil());
        org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiAuthType.NONE.name(), apiDoc.getAuth().getType());
        org.junit.Assert.assertEquals(org.jsondoc.core.scanner.DefaultJSONDocScanner.ANONYMOUS, apiDoc.getAuth().getRoles().get(0));
        for (org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc : apiDoc.getMethods()) {
            if (apiMethodDoc.getPath().contains("/name")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("string", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("string", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("200 - OK", apiMethodDoc.getResponsestatuscode());
                for (org.jsondoc.core.pojo.ApiParamDoc apiParamDoc : apiMethodDoc.getPathparameters()) {
                    if (apiParamDoc.getName().equals("name")) {
                        org.junit.Assert.assertEquals("string", apiParamDoc.getJsondocType().getOneLineText());
                    }
                }
            }
            if (apiMethodDoc.getPath().contains("/age")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("204", apiMethodDoc.getResponsestatuscode());
                org.junit.Assert.assertEquals("integer", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("integer", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
                for (org.jsondoc.core.pojo.ApiParamDoc apiParamDoc : apiMethodDoc.getPathparameters()) {
                    if (apiParamDoc.getName().equals("age")) {
                        org.junit.Assert.assertEquals("integer", apiParamDoc.getJsondocType().getOneLineText());
                    }
                }
            }
            if (apiMethodDoc.getPath().contains("/avg")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("long", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("long", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
                for (org.jsondoc.core.pojo.ApiParamDoc apiParamDoc : apiMethodDoc.getPathparameters()) {
                    if (apiParamDoc.getName().equals("avg")) {
                        org.junit.Assert.assertEquals("long", apiParamDoc.getJsondocType().getOneLineText());
                    }
                }
            }
            if (apiMethodDoc.getPath().contains("/map")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("map[string, integer]", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("map[string, integer]", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
                for (org.jsondoc.core.pojo.ApiParamDoc apiParamDoc : apiMethodDoc.getPathparameters()) {
                    if (apiParamDoc.getName().equals("map")) {
                        org.junit.Assert.assertEquals("map[string, integer]", apiParamDoc.getJsondocType().getOneLineText());
                    }
                }
            }
            if (apiMethodDoc.getPath().contains("/parametrizedList")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("list of string", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("list of string", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
                for (org.jsondoc.core.pojo.ApiParamDoc apiParamDoc : apiMethodDoc.getPathparameters()) {
                    if (apiParamDoc.getName().equals("parametrizedList")) {
                        org.junit.Assert.assertEquals("list of string", apiParamDoc.getJsondocType().getOneLineText());
                    }
                }
            }
            if (apiMethodDoc.getPath().contains("/wildcardParametrizedList")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("list of wildcard", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("list of wildcard", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
                for (org.jsondoc.core.pojo.ApiParamDoc apiParamDoc : apiMethodDoc.getPathparameters()) {
                    if (apiParamDoc.getName().equals("wildcardParametrizedList")) {
                        org.junit.Assert.assertEquals("list of wildcard", apiParamDoc.getJsondocType().getOneLineText());
                    }
                }
            }
            if (apiMethodDoc.getPath().contains("/LongArray")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("array of long", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("array of long", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
                for (org.jsondoc.core.pojo.ApiParamDoc apiParamDoc : apiMethodDoc.getPathparameters()) {
                    if (apiParamDoc.getName().equals("LongArray")) {
                        org.junit.Assert.assertEquals("array of long", apiParamDoc.getJsondocType().getOneLineText());
                    }
                }
            }
            if (apiMethodDoc.getPath().contains("/longArray")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("array of long", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("array of long", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
                for (org.jsondoc.core.pojo.ApiParamDoc apiParamDoc : apiMethodDoc.getPathparameters()) {
                    if (apiParamDoc.getName().equals("longArray")) {
                        org.junit.Assert.assertEquals("array of long", apiParamDoc.getJsondocType().getOneLineText());
                    }
                }
            }
            if (apiMethodDoc.getPath().contains("/version")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("1.0", apiMethodDoc.getSupportedversions().getSince());
                org.junit.Assert.assertEquals("2.12", apiMethodDoc.getSupportedversions().getUntil());
            }
            if (apiMethodDoc.getPath().contains("/child")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("child", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
            }
            if (apiMethodDoc.getPath().contains("/pizza")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("customPizzaObject", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
            }
            if (apiMethodDoc.getPath().contains("/multiple-request-methods")) {
                org.junit.Assert.assertEquals(2, apiMethodDoc.getVerb().size());
                java.util.Iterator<org.jsondoc.core.pojo.ApiVerb> iterator = apiMethodDoc.getVerb().iterator();
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, iterator.next());
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.POST, iterator.next());
            }
        }
        classes.clear();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.TestControllerWithBasicAuth.class);
        apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        org.junit.Assert.assertEquals("test-controller-with-basic-auth", apiDoc.getName());
        org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiAuthType.BASIC_AUTH.name(), apiDoc.getAuth().getType());
        org.junit.Assert.assertEquals("ROLE_USER", apiDoc.getAuth().getRoles().get(0));
        org.junit.Assert.assertEquals("ROLE_ADMIN", apiDoc.getAuth().getRoles().get(1));
        org.junit.Assert.assertTrue(((apiDoc.getAuth().getTestusers().size()) > 0));
        for (org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc : apiDoc.getMethods()) {
            if (apiMethodDoc.getPath().contains("/basicAuth")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiAuthType.BASIC_AUTH.name(), apiMethodDoc.getAuth().getType());
                org.junit.Assert.assertEquals("ROLE_USER", apiMethodDoc.getAuth().getRoles().get(0));
                org.junit.Assert.assertTrue(((apiMethodDoc.getAuth().getTestusers().size()) > 0));
            }
            if (apiMethodDoc.getPath().contains("/noAuth")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiAuthType.NONE.name(), apiMethodDoc.getAuth().getType());
                org.junit.Assert.assertEquals(org.jsondoc.core.scanner.DefaultJSONDocScanner.ANONYMOUS, apiMethodDoc.getAuth().getRoles().get(0));
            }
            if (apiMethodDoc.getPath().contains("/undefinedAuthWithAuthOnClass")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiAuthType.BASIC_AUTH.name(), apiMethodDoc.getAuth().getType());
                org.junit.Assert.assertEquals("ROLE_USER", apiMethodDoc.getAuth().getRoles().get(0));
                org.junit.Assert.assertEquals("ROLE_ADMIN", apiMethodDoc.getAuth().getRoles().get(1));
            }
        }
        classes.clear();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.TestControllerWithNoAuthAnnotation.class);
        apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        org.junit.Assert.assertEquals("test-controller-with-no-auth-annotation", apiDoc.getName());
        org.junit.Assert.assertNull(apiDoc.getAuth());
        for (org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc : apiDoc.getMethods()) {
            if (apiMethodDoc.getPath().contains("/basicAuth")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiAuthType.BASIC_AUTH.name(), apiMethodDoc.getAuth().getType());
                org.junit.Assert.assertEquals("ROLE_USER", apiMethodDoc.getAuth().getRoles().get(0));
                org.junit.Assert.assertTrue(((apiMethodDoc.getAuth().getTestusers().size()) > 0));
            }
            if (apiMethodDoc.getPath().contains("/noAuth")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiAuthType.NONE.name(), apiMethodDoc.getAuth().getType());
                org.junit.Assert.assertEquals(org.jsondoc.core.scanner.DefaultJSONDocScanner.ANONYMOUS, apiMethodDoc.getAuth().getRoles().get(0));
            }
            if (apiMethodDoc.getPath().contains("/undefinedAuthWithoutAuthOnClass")) {
                org.junit.Assert.assertNull(apiMethodDoc.getAuth());
            }
        }
        classes.clear();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.TestOldStyleServlets.class);
        apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        org.junit.Assert.assertEquals("test-old-style-servlets", apiDoc.getName());
        for (org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc : apiDoc.getMethods()) {
            if (apiMethodDoc.getPath().contains("/oldStyle")) {
                org.junit.Assert.assertEquals(1, apiMethodDoc.getPathparameters().size());
            }
            if (apiMethodDoc.getPath().contains("/oldStyleWithList")) {
                org.junit.Assert.assertEquals(1, apiMethodDoc.getPathparameters().size());
            }
            if (apiMethodDoc.getPath().contains("/oldStyleWithMap")) {
                org.junit.Assert.assertEquals(1, apiMethodDoc.getPathparameters().size());
            }
            if (apiMethodDoc.getPath().contains("/oldStyleMixed")) {
                org.junit.Assert.assertEquals(3, apiMethodDoc.getPathparameters().size());
                org.junit.Assert.assertEquals(1, apiMethodDoc.getQueryparameters().size());
                org.junit.Assert.assertEquals("qTest", apiMethodDoc.getQueryparameters().iterator().next().getDefaultvalue());
            }
            if (apiMethodDoc.getPath().contains("/oldStyleResponseObject")) {
                org.junit.Assert.assertEquals("list", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
            }
            if (apiMethodDoc.getPath().contains("/oldStyleBodyObject")) {
                org.junit.Assert.assertEquals("list", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
            }
        }
        classes.clear();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.TestErrorsAndWarningsAndHints.class);
        apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        org.junit.Assert.assertEquals("test-errors-warnings-hints", apiDoc.getName());
        org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc = apiDoc.getMethods().iterator().next();
        org.junit.Assert.assertEquals(1, apiMethodDoc.getJsondocerrors().size());
        org.junit.Assert.assertEquals(1, apiMethodDoc.getJsondocwarnings().size());
        org.junit.Assert.assertEquals(2, apiMethodDoc.getJsondochints().size());
        classes.clear();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.TestErrorsAndWarningsAndHintsMethodSummary.class);
        apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.SUMMARY).iterator().next();
        apiMethodDoc = apiDoc.getMethods().iterator().next();
        org.junit.Assert.assertEquals(1, apiMethodDoc.getJsondocerrors().size());
        org.junit.Assert.assertEquals(1, apiMethodDoc.getJsondocwarnings().size());
        org.junit.Assert.assertEquals(3, apiMethodDoc.getJsondochints().size());
        classes.clear();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.InterfaceController.class);
        apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        org.junit.Assert.assertEquals("interface-controller", apiDoc.getName());
        apiMethodDoc = apiDoc.getMethods().iterator().next();
        org.junit.Assert.assertNotNull(apiMethodDoc);
        org.junit.Assert.assertEquals("/interface", apiMethodDoc.getPath().iterator().next());
        classes.clear();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.TestDeclaredMethods.class);
        apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        org.junit.Assert.assertEquals("test-declared-methods", apiDoc.getName());
        org.junit.Assert.assertEquals(2, apiDoc.getMethods().size());
        classes.clear();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.TestMultipleParamsWithSameMethod.class);
        apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        // StatementAdderOnAssert create null value
        java.util.Set<org.jsondoc.core.pojo.ApiMethodDoc> vc_38 = (java.util.Set)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_38);
        // StatementAdderOnAssert create random local variable
        org.jsondoc.core.pojo.ApiDoc vc_37 = new org.jsondoc.core.pojo.ApiDoc();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_37).getGroup(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_37).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_37).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        java.util.TreeSet collection_1683165741 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_1683165741, ((org.jsondoc.core.pojo.ApiDoc)vc_37).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_37).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_37).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // StatementAdderMethod cloned existing statement
        vc_37.setMethods(vc_38);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_37).getGroup(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_37).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_37).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_37).getMethods());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_37).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_37).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_37).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_37).getVisibility()).getDeclaringClass()).isAnonymousClass());
        org.junit.Assert.assertEquals(3, apiDoc.getMethods().size());
    }

    /* amplification of org.jsondoc.core.doc.ApiDocTest#testApiDoc */
    @org.junit.Test(timeout = 10000)
    public void testApiDoc_cf351_cf3383() {
        java.util.Set<java.lang.Class<?>> classes = new java.util.HashSet<java.lang.Class<?>>();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.TestController.class);
        org.jsondoc.core.pojo.ApiDoc apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        org.junit.Assert.assertEquals("test-controller", apiDoc.getName());
        org.junit.Assert.assertEquals("a-test-controller", apiDoc.getDescription());
        org.junit.Assert.assertEquals("1.0", apiDoc.getSupportedversions().getSince());
        org.junit.Assert.assertEquals("2.12", apiDoc.getSupportedversions().getUntil());
        org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiAuthType.NONE.name(), apiDoc.getAuth().getType());
        org.junit.Assert.assertEquals(org.jsondoc.core.scanner.DefaultJSONDocScanner.ANONYMOUS, apiDoc.getAuth().getRoles().get(0));
        for (org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc : apiDoc.getMethods()) {
            if (apiMethodDoc.getPath().contains("/name")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("string", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("string", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("200 - OK", apiMethodDoc.getResponsestatuscode());
                for (org.jsondoc.core.pojo.ApiParamDoc apiParamDoc : apiMethodDoc.getPathparameters()) {
                    if (apiParamDoc.getName().equals("name")) {
                        org.junit.Assert.assertEquals("string", apiParamDoc.getJsondocType().getOneLineText());
                    }
                }
            }
            if (apiMethodDoc.getPath().contains("/age")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("204", apiMethodDoc.getResponsestatuscode());
                org.junit.Assert.assertEquals("integer", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("integer", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
                for (org.jsondoc.core.pojo.ApiParamDoc apiParamDoc : apiMethodDoc.getPathparameters()) {
                    if (apiParamDoc.getName().equals("age")) {
                        org.junit.Assert.assertEquals("integer", apiParamDoc.getJsondocType().getOneLineText());
                    }
                }
            }
            if (apiMethodDoc.getPath().contains("/avg")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("long", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("long", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
                for (org.jsondoc.core.pojo.ApiParamDoc apiParamDoc : apiMethodDoc.getPathparameters()) {
                    if (apiParamDoc.getName().equals("avg")) {
                        org.junit.Assert.assertEquals("long", apiParamDoc.getJsondocType().getOneLineText());
                    }
                }
            }
            if (apiMethodDoc.getPath().contains("/map")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("map[string, integer]", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("map[string, integer]", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
                for (org.jsondoc.core.pojo.ApiParamDoc apiParamDoc : apiMethodDoc.getPathparameters()) {
                    if (apiParamDoc.getName().equals("map")) {
                        org.junit.Assert.assertEquals("map[string, integer]", apiParamDoc.getJsondocType().getOneLineText());
                    }
                }
            }
            if (apiMethodDoc.getPath().contains("/parametrizedList")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("list of string", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("list of string", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
                for (org.jsondoc.core.pojo.ApiParamDoc apiParamDoc : apiMethodDoc.getPathparameters()) {
                    if (apiParamDoc.getName().equals("parametrizedList")) {
                        org.junit.Assert.assertEquals("list of string", apiParamDoc.getJsondocType().getOneLineText());
                    }
                }
            }
            if (apiMethodDoc.getPath().contains("/wildcardParametrizedList")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("list of wildcard", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("list of wildcard", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
                for (org.jsondoc.core.pojo.ApiParamDoc apiParamDoc : apiMethodDoc.getPathparameters()) {
                    if (apiParamDoc.getName().equals("wildcardParametrizedList")) {
                        org.junit.Assert.assertEquals("list of wildcard", apiParamDoc.getJsondocType().getOneLineText());
                    }
                }
            }
            if (apiMethodDoc.getPath().contains("/LongArray")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("array of long", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("array of long", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
                for (org.jsondoc.core.pojo.ApiParamDoc apiParamDoc : apiMethodDoc.getPathparameters()) {
                    if (apiParamDoc.getName().equals("LongArray")) {
                        org.junit.Assert.assertEquals("array of long", apiParamDoc.getJsondocType().getOneLineText());
                    }
                }
            }
            if (apiMethodDoc.getPath().contains("/longArray")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("array of long", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("array of long", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
                for (org.jsondoc.core.pojo.ApiParamDoc apiParamDoc : apiMethodDoc.getPathparameters()) {
                    if (apiParamDoc.getName().equals("longArray")) {
                        org.junit.Assert.assertEquals("array of long", apiParamDoc.getJsondocType().getOneLineText());
                    }
                }
            }
            if (apiMethodDoc.getPath().contains("/version")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("1.0", apiMethodDoc.getSupportedversions().getSince());
                org.junit.Assert.assertEquals("2.12", apiMethodDoc.getSupportedversions().getUntil());
            }
            if (apiMethodDoc.getPath().contains("/child")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("child", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
            }
            if (apiMethodDoc.getPath().contains("/pizza")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("customPizzaObject", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
            }
            if (apiMethodDoc.getPath().contains("/multiple-request-methods")) {
                org.junit.Assert.assertEquals(2, apiMethodDoc.getVerb().size());
                java.util.Iterator<org.jsondoc.core.pojo.ApiVerb> iterator = apiMethodDoc.getVerb().iterator();
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, iterator.next());
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.POST, iterator.next());
            }
        }
        classes.clear();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.TestControllerWithBasicAuth.class);
        apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        org.junit.Assert.assertEquals("test-controller-with-basic-auth", apiDoc.getName());
        org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiAuthType.BASIC_AUTH.name(), apiDoc.getAuth().getType());
        org.junit.Assert.assertEquals("ROLE_USER", apiDoc.getAuth().getRoles().get(0));
        org.junit.Assert.assertEquals("ROLE_ADMIN", apiDoc.getAuth().getRoles().get(1));
        org.junit.Assert.assertTrue(((apiDoc.getAuth().getTestusers().size()) > 0));
        for (org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc : apiDoc.getMethods()) {
            if (apiMethodDoc.getPath().contains("/basicAuth")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiAuthType.BASIC_AUTH.name(), apiMethodDoc.getAuth().getType());
                org.junit.Assert.assertEquals("ROLE_USER", apiMethodDoc.getAuth().getRoles().get(0));
                org.junit.Assert.assertTrue(((apiMethodDoc.getAuth().getTestusers().size()) > 0));
            }
            if (apiMethodDoc.getPath().contains("/noAuth")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiAuthType.NONE.name(), apiMethodDoc.getAuth().getType());
                org.junit.Assert.assertEquals(org.jsondoc.core.scanner.DefaultJSONDocScanner.ANONYMOUS, apiMethodDoc.getAuth().getRoles().get(0));
            }
            if (apiMethodDoc.getPath().contains("/undefinedAuthWithAuthOnClass")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiAuthType.BASIC_AUTH.name(), apiMethodDoc.getAuth().getType());
                org.junit.Assert.assertEquals("ROLE_USER", apiMethodDoc.getAuth().getRoles().get(0));
                org.junit.Assert.assertEquals("ROLE_ADMIN", apiMethodDoc.getAuth().getRoles().get(1));
            }
        }
        classes.clear();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.TestControllerWithNoAuthAnnotation.class);
        apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        org.junit.Assert.assertEquals("test-controller-with-no-auth-annotation", apiDoc.getName());
        org.junit.Assert.assertNull(apiDoc.getAuth());
        for (org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc : apiDoc.getMethods()) {
            if (apiMethodDoc.getPath().contains("/basicAuth")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiAuthType.BASIC_AUTH.name(), apiMethodDoc.getAuth().getType());
                org.junit.Assert.assertEquals("ROLE_USER", apiMethodDoc.getAuth().getRoles().get(0));
                org.junit.Assert.assertTrue(((apiMethodDoc.getAuth().getTestusers().size()) > 0));
            }
            if (apiMethodDoc.getPath().contains("/noAuth")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiAuthType.NONE.name(), apiMethodDoc.getAuth().getType());
                org.junit.Assert.assertEquals(org.jsondoc.core.scanner.DefaultJSONDocScanner.ANONYMOUS, apiMethodDoc.getAuth().getRoles().get(0));
            }
            if (apiMethodDoc.getPath().contains("/undefinedAuthWithoutAuthOnClass")) {
                org.junit.Assert.assertNull(apiMethodDoc.getAuth());
            }
        }
        classes.clear();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.TestOldStyleServlets.class);
        apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        org.junit.Assert.assertEquals("test-old-style-servlets", apiDoc.getName());
        for (org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc : apiDoc.getMethods()) {
            if (apiMethodDoc.getPath().contains("/oldStyle")) {
                org.junit.Assert.assertEquals(1, apiMethodDoc.getPathparameters().size());
            }
            if (apiMethodDoc.getPath().contains("/oldStyleWithList")) {
                org.junit.Assert.assertEquals(1, apiMethodDoc.getPathparameters().size());
            }
            if (apiMethodDoc.getPath().contains("/oldStyleWithMap")) {
                org.junit.Assert.assertEquals(1, apiMethodDoc.getPathparameters().size());
            }
            if (apiMethodDoc.getPath().contains("/oldStyleMixed")) {
                org.junit.Assert.assertEquals(3, apiMethodDoc.getPathparameters().size());
                org.junit.Assert.assertEquals(1, apiMethodDoc.getQueryparameters().size());
                org.junit.Assert.assertEquals("qTest", apiMethodDoc.getQueryparameters().iterator().next().getDefaultvalue());
            }
            if (apiMethodDoc.getPath().contains("/oldStyleResponseObject")) {
                org.junit.Assert.assertEquals("list", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
            }
            if (apiMethodDoc.getPath().contains("/oldStyleBodyObject")) {
                org.junit.Assert.assertEquals("list", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
            }
        }
        classes.clear();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.TestErrorsAndWarningsAndHints.class);
        apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        org.junit.Assert.assertEquals("test-errors-warnings-hints", apiDoc.getName());
        org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc = apiDoc.getMethods().iterator().next();
        org.junit.Assert.assertEquals(1, apiMethodDoc.getJsondocerrors().size());
        org.junit.Assert.assertEquals(1, apiMethodDoc.getJsondocwarnings().size());
        org.junit.Assert.assertEquals(2, apiMethodDoc.getJsondochints().size());
        classes.clear();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.TestErrorsAndWarningsAndHintsMethodSummary.class);
        apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.SUMMARY).iterator().next();
        apiMethodDoc = apiDoc.getMethods().iterator().next();
        org.junit.Assert.assertEquals(1, apiMethodDoc.getJsondocerrors().size());
        org.junit.Assert.assertEquals(1, apiMethodDoc.getJsondocwarnings().size());
        org.junit.Assert.assertEquals(3, apiMethodDoc.getJsondochints().size());
        classes.clear();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.InterfaceController.class);
        apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        org.junit.Assert.assertEquals("interface-controller", apiDoc.getName());
        apiMethodDoc = apiDoc.getMethods().iterator().next();
        org.junit.Assert.assertNotNull(apiMethodDoc);
        org.junit.Assert.assertEquals("/interface", apiMethodDoc.getPath().iterator().next());
        classes.clear();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.TestDeclaredMethods.class);
        apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        org.junit.Assert.assertEquals("test-declared-methods", apiDoc.getName());
        org.junit.Assert.assertEquals(2, apiDoc.getMethods().size());
        classes.clear();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.TestMultipleParamsWithSameMethod.class);
        apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        // StatementAdderOnAssert create random local variable
        org.jsondoc.core.pojo.ApiDoc vc_17 = new org.jsondoc.core.pojo.ApiDoc();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_17).getGroup(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_17).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_17).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_17).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        java.util.TreeSet collection_919391392 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_919391392, ((org.jsondoc.core.pojo.ApiDoc)vc_17).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_17).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_17).getGroup(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_17).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_17).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_17).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        java.util.TreeSet collection_1685836813 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_1685836813, ((org.jsondoc.core.pojo.ApiDoc)vc_17).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_17).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).name(), "UNDEFINED");
        // AssertGenerator replace invocation
        org.jsondoc.core.pojo.ApiVersionDoc o_testApiDoc_cf351__562 = // StatementAdderMethod cloned existing statement
vc_17.getSupportedversions();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testApiDoc_cf351__562);
        // StatementAdderOnAssert create null value
        java.lang.String vc_426 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_426);
        // StatementAdderOnAssert create random local variable
        org.jsondoc.core.pojo.ApiDoc vc_425 = new org.jsondoc.core.pojo.ApiDoc();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_425).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_425).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_425).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        java.util.TreeSet collection_1742071756 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_1742071756, ((org.jsondoc.core.pojo.ApiDoc)vc_425).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_425).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_425).getGroup(), "");
        // StatementAdderMethod cloned existing statement
        vc_425.setGroup(vc_426);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_425).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_425).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_425).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        java.util.TreeSet collection_1881895208 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_1881895208, ((org.jsondoc.core.pojo.ApiDoc)vc_425).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_425).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_425).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_425).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_425).getGroup());
        org.junit.Assert.assertEquals(3, apiDoc.getMethods().size());
    }

    /* amplification of org.jsondoc.core.doc.ApiDocTest#testApiDoc */
    @org.junit.Test(timeout = 10000)
    public void testApiDoc_cf351_literalMutation3205_cf16688() {
        java.util.Set<java.lang.Class<?>> classes = new java.util.HashSet<java.lang.Class<?>>();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.TestController.class);
        org.jsondoc.core.pojo.ApiDoc apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        org.junit.Assert.assertEquals("test-controller", apiDoc.getName());
        org.junit.Assert.assertEquals("a-test-controller", apiDoc.getDescription());
        org.junit.Assert.assertEquals("1.0", apiDoc.getSupportedversions().getSince());
        org.junit.Assert.assertEquals("2.12", apiDoc.getSupportedversions().getUntil());
        org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiAuthType.NONE.name(), apiDoc.getAuth().getType());
        org.junit.Assert.assertEquals(org.jsondoc.core.scanner.DefaultJSONDocScanner.ANONYMOUS, apiDoc.getAuth().getRoles().get(0));
        for (org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc : apiDoc.getMethods()) {
            if (apiMethodDoc.getPath().contains("/name")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("string", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("string", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("200 - OK", apiMethodDoc.getResponsestatuscode());
                for (org.jsondoc.core.pojo.ApiParamDoc apiParamDoc : apiMethodDoc.getPathparameters()) {
                    if (apiParamDoc.getName().equals("name")) {
                        org.junit.Assert.assertEquals("string", apiParamDoc.getJsondocType().getOneLineText());
                    }
                }
            }
            if (apiMethodDoc.getPath().contains("/age")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("204", apiMethodDoc.getResponsestatuscode());
                org.junit.Assert.assertEquals("integer", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("integer", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
                for (org.jsondoc.core.pojo.ApiParamDoc apiParamDoc : apiMethodDoc.getPathparameters()) {
                    if (apiParamDoc.getName().equals("age")) {
                        org.junit.Assert.assertEquals("integer", apiParamDoc.getJsondocType().getOneLineText());
                    }
                }
            }
            if (apiMethodDoc.getPath().contains("/avg")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("long", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("long", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
                for (org.jsondoc.core.pojo.ApiParamDoc apiParamDoc : apiMethodDoc.getPathparameters()) {
                    if (apiParamDoc.getName().equals("avg")) {
                        org.junit.Assert.assertEquals("long", apiParamDoc.getJsondocType().getOneLineText());
                    }
                }
            }
            if (apiMethodDoc.getPath().contains("/map")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("map[string, integer]", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("map[string, integer]", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
                for (org.jsondoc.core.pojo.ApiParamDoc apiParamDoc : apiMethodDoc.getPathparameters()) {
                    if (apiParamDoc.getName().equals("map")) {
                        org.junit.Assert.assertEquals("map[string, integer]", apiParamDoc.getJsondocType().getOneLineText());
                    }
                }
            }
            if (apiMethodDoc.getPath().contains("/parametrizedList")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("list of string", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("list of string", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
                for (org.jsondoc.core.pojo.ApiParamDoc apiParamDoc : apiMethodDoc.getPathparameters()) {
                    if (apiParamDoc.getName().equals("parametrizedList")) {
                        org.junit.Assert.assertEquals("list of string", apiParamDoc.getJsondocType().getOneLineText());
                    }
                }
            }
            if (apiMethodDoc.getPath().contains("/wildcardParametrizedList")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("list of wildcard", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("list of wildcard", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
                for (org.jsondoc.core.pojo.ApiParamDoc apiParamDoc : apiMethodDoc.getPathparameters()) {
                    if (apiParamDoc.getName().equals("wildcardParametrizedList")) {
                        org.junit.Assert.assertEquals("list of wildcard", apiParamDoc.getJsondocType().getOneLineText());
                    }
                }
            }
            if (apiMethodDoc.getPath().contains("/LongArray")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("array of long", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("array of long", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
                for (org.jsondoc.core.pojo.ApiParamDoc apiParamDoc : apiMethodDoc.getPathparameters()) {
                    if (apiParamDoc.getName().equals("LongArray")) {
                        org.junit.Assert.assertEquals("array of long", apiParamDoc.getJsondocType().getOneLineText());
                    }
                }
            }
            if (apiMethodDoc.getPath().contains("/longArray")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("array of long", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
                org.junit.Assert.assertEquals("array of long", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
                for (org.jsondoc.core.pojo.ApiParamDoc apiParamDoc : apiMethodDoc.getPathparameters()) {
                    if (apiParamDoc.getName().equals("longArray")) {
                        org.junit.Assert.assertEquals("array of long", apiParamDoc.getJsondocType().getOneLineText());
                    }
                }
            }
            if (apiMethodDoc.getPath().contains("/version")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("1.0", apiMethodDoc.getSupportedversions().getSince());
                org.junit.Assert.assertEquals("2.12", apiMethodDoc.getSupportedversions().getUntil());
            }
            if (apiMethodDoc.getPath().contains("/child")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("child", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
            }
            if (apiMethodDoc.getPath().contains("/pizza")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, apiMethodDoc.getVerb().iterator().next());
                org.junit.Assert.assertEquals("customPizzaObject", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
            }
            if (apiMethodDoc.getPath().contains("/multiple-request-methods")) {
                org.junit.Assert.assertEquals(2, apiMethodDoc.getVerb().size());
                java.util.Iterator<org.jsondoc.core.pojo.ApiVerb> iterator = apiMethodDoc.getVerb().iterator();
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.GET, iterator.next());
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiVerb.POST, iterator.next());
            }
        }
        classes.clear();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.TestControllerWithBasicAuth.class);
        apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        org.junit.Assert.assertEquals("test-controller-with-basic-auth", apiDoc.getName());
        org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiAuthType.BASIC_AUTH.name(), apiDoc.getAuth().getType());
        org.junit.Assert.assertEquals("ROLE_USER", apiDoc.getAuth().getRoles().get(0));
        org.junit.Assert.assertEquals("ROLE_ADMIN", apiDoc.getAuth().getRoles().get(1));
        org.junit.Assert.assertTrue(((apiDoc.getAuth().getTestusers().size()) > -1));
        for (org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc : apiDoc.getMethods()) {
            if (apiMethodDoc.getPath().contains("/basicAuth")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiAuthType.BASIC_AUTH.name(), apiMethodDoc.getAuth().getType());
                org.junit.Assert.assertEquals("ROLE_USER", apiMethodDoc.getAuth().getRoles().get(0));
                org.junit.Assert.assertTrue(((apiMethodDoc.getAuth().getTestusers().size()) > 0));
            }
            if (apiMethodDoc.getPath().contains("/noAuth")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiAuthType.NONE.name(), apiMethodDoc.getAuth().getType());
                org.junit.Assert.assertEquals(org.jsondoc.core.scanner.DefaultJSONDocScanner.ANONYMOUS, apiMethodDoc.getAuth().getRoles().get(0));
            }
            if (apiMethodDoc.getPath().contains("/undefinedAuthWithAuthOnClass")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiAuthType.BASIC_AUTH.name(), apiMethodDoc.getAuth().getType());
                org.junit.Assert.assertEquals("ROLE_USER", apiMethodDoc.getAuth().getRoles().get(0));
                org.junit.Assert.assertEquals("ROLE_ADMIN", apiMethodDoc.getAuth().getRoles().get(1));
            }
        }
        classes.clear();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.TestControllerWithNoAuthAnnotation.class);
        apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        org.junit.Assert.assertEquals("test-controller-with-no-auth-annotation", apiDoc.getName());
        org.junit.Assert.assertNull(apiDoc.getAuth());
        for (org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc : apiDoc.getMethods()) {
            if (apiMethodDoc.getPath().contains("/basicAuth")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiAuthType.BASIC_AUTH.name(), apiMethodDoc.getAuth().getType());
                org.junit.Assert.assertEquals("ROLE_USER", apiMethodDoc.getAuth().getRoles().get(0));
                org.junit.Assert.assertTrue(((apiMethodDoc.getAuth().getTestusers().size()) > 0));
            }
            if (apiMethodDoc.getPath().contains("/noAuth")) {
                org.junit.Assert.assertEquals(org.jsondoc.core.pojo.ApiAuthType.NONE.name(), apiMethodDoc.getAuth().getType());
                org.junit.Assert.assertEquals(org.jsondoc.core.scanner.DefaultJSONDocScanner.ANONYMOUS, apiMethodDoc.getAuth().getRoles().get(0));
            }
            if (apiMethodDoc.getPath().contains("/undefinedAuthWithoutAuthOnClass")) {
                org.junit.Assert.assertNull(apiMethodDoc.getAuth());
            }
        }
        classes.clear();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.TestOldStyleServlets.class);
        apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        org.junit.Assert.assertEquals("test-old-style-servlets", apiDoc.getName());
        for (org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc : apiDoc.getMethods()) {
            if (apiMethodDoc.getPath().contains("/oldStyle")) {
                org.junit.Assert.assertEquals(1, apiMethodDoc.getPathparameters().size());
            }
            if (apiMethodDoc.getPath().contains("/oldStyleWithList")) {
                org.junit.Assert.assertEquals(1, apiMethodDoc.getPathparameters().size());
            }
            if (apiMethodDoc.getPath().contains("/oldStyleWithMap")) {
                org.junit.Assert.assertEquals(1, apiMethodDoc.getPathparameters().size());
            }
            if (apiMethodDoc.getPath().contains("/oldStyleMixed")) {
                org.junit.Assert.assertEquals(3, apiMethodDoc.getPathparameters().size());
                org.junit.Assert.assertEquals(1, apiMethodDoc.getQueryparameters().size());
                org.junit.Assert.assertEquals("qTest", apiMethodDoc.getQueryparameters().iterator().next().getDefaultvalue());
            }
            if (apiMethodDoc.getPath().contains("/oldStyleResponseObject")) {
                org.junit.Assert.assertEquals("list", apiMethodDoc.getResponse().getJsondocType().getOneLineText());
            }
            if (apiMethodDoc.getPath().contains("/oldStyleBodyObject")) {
                org.junit.Assert.assertEquals("list", apiMethodDoc.getBodyobject().getJsondocType().getOneLineText());
            }
        }
        classes.clear();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.TestErrorsAndWarningsAndHints.class);
        apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        org.junit.Assert.assertEquals("test-errors-warnings-hints", apiDoc.getName());
        org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc = apiDoc.getMethods().iterator().next();
        org.junit.Assert.assertEquals(1, apiMethodDoc.getJsondocerrors().size());
        org.junit.Assert.assertEquals(1, apiMethodDoc.getJsondocwarnings().size());
        org.junit.Assert.assertEquals(2, apiMethodDoc.getJsondochints().size());
        classes.clear();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.TestErrorsAndWarningsAndHintsMethodSummary.class);
        apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.SUMMARY).iterator().next();
        apiMethodDoc = apiDoc.getMethods().iterator().next();
        org.junit.Assert.assertEquals(1, apiMethodDoc.getJsondocerrors().size());
        org.junit.Assert.assertEquals(1, apiMethodDoc.getJsondocwarnings().size());
        org.junit.Assert.assertEquals(3, apiMethodDoc.getJsondochints().size());
        classes.clear();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.InterfaceController.class);
        apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        org.junit.Assert.assertEquals("interface-controller", apiDoc.getName());
        apiMethodDoc = apiDoc.getMethods().iterator().next();
        org.junit.Assert.assertNotNull(apiMethodDoc);
        org.junit.Assert.assertEquals("/interface", apiMethodDoc.getPath().iterator().next());
        classes.clear();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.TestDeclaredMethods.class);
        apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        org.junit.Assert.assertEquals("test-declared-methods", apiDoc.getName());
        org.junit.Assert.assertEquals(2, apiDoc.getMethods().size());
        classes.clear();
        classes.add(org.jsondoc.core.doc.AmplApiDocTest.TestMultipleParamsWithSameMethod.class);
        apiDoc = jsondocScanner.getApiDocs(classes, org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        // StatementAdderOnAssert create random local variable
        org.jsondoc.core.pojo.ApiDoc vc_17 = new org.jsondoc.core.pojo.ApiDoc();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_17).getGroup(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_17).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_17).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_17).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        java.util.TreeSet collection_1416764714 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_1416764714, ((org.jsondoc.core.pojo.ApiDoc)vc_17).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_17).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_17).getGroup(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_17).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_17).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_17).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        java.util.TreeSet collection_1046611275 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_1046611275, ((org.jsondoc.core.pojo.ApiDoc)vc_17).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_17).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_17).getGroup(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_17).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_17).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_17).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        java.util.TreeSet collection_1685836813 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_1685836813, ((org.jsondoc.core.pojo.ApiDoc)vc_17).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_17).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_17).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_17).getStage()).name(), "UNDEFINED");
        // AssertGenerator replace invocation
        org.jsondoc.core.pojo.ApiVersionDoc o_testApiDoc_cf351__562 = // StatementAdderMethod cloned existing statement
vc_17.getSupportedversions();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testApiDoc_cf351__562);
        // StatementAdderOnAssert create random local variable
        org.jsondoc.core.pojo.ApiDoc vc_2137 = new org.jsondoc.core.pojo.ApiDoc();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        java.util.TreeSet collection_215469535 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_215469535, ((org.jsondoc.core.pojo.ApiDoc)vc_2137).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2137).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2137).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2137).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2137).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2137).getGroup(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2137).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator replace invocation
        java.lang.String o_testApiDoc_cf351_literalMutation3205_cf16688__793 = // StatementAdderMethod cloned existing statement
vc_2137.getName();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testApiDoc_cf351_literalMutation3205_cf16688__793, "");
        org.junit.Assert.assertEquals(3, apiDoc.getMethods().size());
    }

    /* amplification of org.jsondoc.core.doc.ApiDocTest#testApiErrorsDoc */
    @org.junit.Test(timeout = 10000)
    public void testApiErrorsDoc_cf19430_failAssert43() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            final org.jsondoc.core.pojo.ApiDoc apiDoc = jsondocScanner.getApiDocs(com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(org.jsondoc.core.util.controller.Test3Controller.class), org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
            final java.util.Set<org.jsondoc.core.pojo.ApiMethodDoc> methods = apiDoc.getMethods();
            final org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc = methods.iterator().next();
            final java.util.List<org.jsondoc.core.pojo.ApiErrorDoc> apiErrors = apiMethodDoc.getApierrors();
            // MethodAssertGenerator build local variable
            Object o_13_0 = methods.size();
            // MethodAssertGenerator build local variable
            Object o_15_0 = apiErrors.size();
            // MethodAssertGenerator build local variable
            Object o_17_0 = apiErrors.get(0).getCode();
            // MethodAssertGenerator build local variable
            Object o_20_0 = apiErrors.get(0).getDescription();
            // MethodAssertGenerator build local variable
            Object o_23_0 = apiErrors.get(1).getCode();
            // StatementAdderOnAssert create null value
            org.jsondoc.core.pojo.ApiMethodDoc vc_2542 = (org.jsondoc.core.pojo.ApiMethodDoc)null;
            // StatementAdderOnAssert create random local variable
            org.jsondoc.core.pojo.ApiDoc vc_2541 = new org.jsondoc.core.pojo.ApiDoc();
            // StatementAdderMethod cloned existing statement
            vc_2541.addMethod(vc_2542);
            // MethodAssertGenerator build local variable
            Object o_32_0 = apiErrors.get(2).getCode();
            org.junit.Assert.fail("testApiErrorsDoc_cf19430 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsondoc.core.doc.ApiDocTest#testApiErrorsDoc */
    @org.junit.Test(timeout = 10000)
    public void testApiErrorsDoc_cf19454() throws java.lang.Exception {
        final org.jsondoc.core.pojo.ApiDoc apiDoc = jsondocScanner.getApiDocs(com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(org.jsondoc.core.util.controller.Test3Controller.class), org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        final java.util.Set<org.jsondoc.core.pojo.ApiMethodDoc> methods = apiDoc.getMethods();
        final org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc = methods.iterator().next();
        final java.util.List<org.jsondoc.core.pojo.ApiErrorDoc> apiErrors = apiMethodDoc.getApierrors();
        org.junit.Assert.assertEquals(1, methods.size());
        org.junit.Assert.assertEquals(3, apiErrors.size());
        org.junit.Assert.assertEquals("1000", apiErrors.get(0).getCode());
        org.junit.Assert.assertEquals("method-level annotation should be applied", "A test error #1", apiErrors.get(0).getDescription());
        org.junit.Assert.assertEquals("2000", apiErrors.get(1).getCode());
        // StatementAdderOnAssert create null value
        java.lang.String vc_2554 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_2554);
        // StatementAdderOnAssert create random local variable
        org.jsondoc.core.pojo.ApiDoc vc_2553 = new org.jsondoc.core.pojo.ApiDoc();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        java.util.TreeSet collection_609393304 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_609393304, ((org.jsondoc.core.pojo.ApiDoc)vc_2553).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getGroup(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isPrimitive());
        // StatementAdderMethod cloned existing statement
        vc_2553.setGroup(vc_2554);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        java.util.TreeSet collection_691677649 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_691677649, ((org.jsondoc.core.pojo.ApiDoc)vc_2553).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getGroup());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isPrimitive());
        org.junit.Assert.assertEquals("400", apiErrors.get(2).getCode());
    }

    /* amplification of org.jsondoc.core.doc.ApiDocTest#testApiErrorsDoc */
    @org.junit.Test(timeout = 10000)
    public void testApiErrorsDoc_cf19473() throws java.lang.Exception {
        final org.jsondoc.core.pojo.ApiDoc apiDoc = jsondocScanner.getApiDocs(com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(org.jsondoc.core.util.controller.Test3Controller.class), org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        final java.util.Set<org.jsondoc.core.pojo.ApiMethodDoc> methods = apiDoc.getMethods();
        final org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc = methods.iterator().next();
        final java.util.List<org.jsondoc.core.pojo.ApiErrorDoc> apiErrors = apiMethodDoc.getApierrors();
        org.junit.Assert.assertEquals(1, methods.size());
        org.junit.Assert.assertEquals(3, apiErrors.size());
        org.junit.Assert.assertEquals("1000", apiErrors.get(0).getCode());
        org.junit.Assert.assertEquals("method-level annotation should be applied", "A test error #1", apiErrors.get(0).getDescription());
        org.junit.Assert.assertEquals("2000", apiErrors.get(1).getCode());
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_137 = "2000";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_137, "2000");
        // StatementAdderOnAssert create random local variable
        org.jsondoc.core.pojo.ApiDoc vc_2561 = new org.jsondoc.core.pojo.ApiDoc();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2561).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        java.util.TreeSet collection_1224158157 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_1224158157, ((org.jsondoc.core.pojo.ApiDoc)vc_2561).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2561).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2561).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2561).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2561).getGroup(), "");
        // StatementAdderMethod cloned existing statement
        vc_2561.setName(String_vc_137);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2561).getName(), "2000");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        java.util.TreeSet collection_1751096951 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_1751096951, ((org.jsondoc.core.pojo.ApiDoc)vc_2561).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2561).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2561).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2561).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2561).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2561).getGroup(), "");
        org.junit.Assert.assertEquals("400", apiErrors.get(2).getCode());
    }

    /* amplification of org.jsondoc.core.doc.ApiDocTest#testApiErrorsDoc */
    @org.junit.Test(timeout = 10000)
    public void testApiErrorsDoc_cf19420_cf21583_failAssert13() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            final org.jsondoc.core.pojo.ApiDoc apiDoc = jsondocScanner.getApiDocs(com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(org.jsondoc.core.util.controller.Test3Controller.class), org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
            final java.util.Set<org.jsondoc.core.pojo.ApiMethodDoc> methods = apiDoc.getMethods();
            final org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc = methods.iterator().next();
            final java.util.List<org.jsondoc.core.pojo.ApiErrorDoc> apiErrors = apiMethodDoc.getApierrors();
            // MethodAssertGenerator build local variable
            Object o_13_0 = methods.size();
            // MethodAssertGenerator build local variable
            Object o_15_0 = apiErrors.size();
            // MethodAssertGenerator build local variable
            Object o_17_0 = apiErrors.get(0).getCode();
            // MethodAssertGenerator build local variable
            Object o_20_0 = apiErrors.get(0).getDescription();
            // MethodAssertGenerator build local variable
            Object o_23_0 = apiErrors.get(1).getCode();
            // StatementAdderOnAssert create random local variable
            org.jsondoc.core.pojo.ApiDoc vc_2537 = new org.jsondoc.core.pojo.ApiDoc();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getStage()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getStage()).name(), "UNDEFINED");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getVisibility()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getVisibility()).getLabel(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getStage()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getVisibility()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getVisibility()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getVisibility()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2537).getDescription(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getVisibility()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getStage()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2537).getName(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getStage()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            java.util.TreeSet collection_1687268177 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_1687268177, ((org.jsondoc.core.pojo.ApiDoc)vc_2537).getMethods());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getVisibility()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getStage()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2537).getGroup(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getStage()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getStage()).getLabel(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getStage()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getStage()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getVisibility()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getStage()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getVisibility()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getVisibility()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getStage()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getVisibility()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2537).getSupportedversions());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2537).getAuth());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getStage()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getVisibility()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getStage()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getVisibility()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getVisibility()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getVisibility()).name(), "UNDEFINED");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getStage()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getStage()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getStage()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getStage()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getStage()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getStage()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getVisibility()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2537).getVisibility()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator replace invocation
            org.jsondoc.core.pojo.ApiVersionDoc o_testApiErrorsDoc_cf19420__28 = // StatementAdderMethod cloned existing statement
vc_2537.getSupportedversions();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_testApiErrorsDoc_cf19420__28);
            // StatementAdderOnAssert create null value
            org.jsondoc.core.pojo.ApiMethodDoc vc_3438 = (org.jsondoc.core.pojo.ApiMethodDoc)null;
            // StatementAdderMethod cloned existing statement
            apiDoc.addMethod(vc_3438);
            // MethodAssertGenerator build local variable
            Object o_148_0 = apiErrors.get(2).getCode();
            org.junit.Assert.fail("testApiErrorsDoc_cf19420_cf21583 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsondoc.core.doc.ApiDocTest#testApiErrorsDoc */
    @org.junit.Test(timeout = 10000)
    public void testApiErrorsDoc_cf19411_cf20918() throws java.lang.Exception {
        final org.jsondoc.core.pojo.ApiDoc apiDoc = jsondocScanner.getApiDocs(com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(org.jsondoc.core.util.controller.Test3Controller.class), org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        final java.util.Set<org.jsondoc.core.pojo.ApiMethodDoc> methods = apiDoc.getMethods();
        final org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc = methods.iterator().next();
        final java.util.List<org.jsondoc.core.pojo.ApiErrorDoc> apiErrors = apiMethodDoc.getApierrors();
        org.junit.Assert.assertEquals(1, methods.size());
        org.junit.Assert.assertEquals(3, apiErrors.size());
        org.junit.Assert.assertEquals("1000", apiErrors.get(0).getCode());
        org.junit.Assert.assertEquals("method-level annotation should be applied", "A test error #1", apiErrors.get(0).getDescription());
        org.junit.Assert.assertEquals("2000", apiErrors.get(1).getCode());
        // StatementAdderOnAssert create random local variable
        org.jsondoc.core.pojo.ApiDoc vc_2531 = new org.jsondoc.core.pojo.ApiDoc();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2531).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        java.util.TreeSet collection_2126664916 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_2126664916, ((org.jsondoc.core.pojo.ApiDoc)vc_2531).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2531).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2531).getGroup(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2531).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2531).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2531).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        java.util.TreeSet collection_678293170 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_678293170, ((org.jsondoc.core.pojo.ApiDoc)vc_2531).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2531).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2531).getGroup(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2531).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2531).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator replace invocation
        java.util.Set<org.jsondoc.core.pojo.ApiMethodDoc> o_testApiErrorsDoc_cf19411__28 = // StatementAdderMethod cloned existing statement
vc_2531.getMethods();
        // AssertGenerator add assertion
        java.util.TreeSet collection_1517994677 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_1517994677, o_testApiErrorsDoc_cf19411__28);;
        // StatementAdderOnAssert create null value
        org.jsondoc.core.pojo.ApiAuthDoc vc_3162 = (org.jsondoc.core.pojo.ApiAuthDoc)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_3162);
        // StatementAdderMethod cloned existing statement
        vc_2531.setAuth(vc_3162);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2531).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        java.util.TreeSet collection_935320669 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_935320669, ((org.jsondoc.core.pojo.ApiDoc)vc_2531).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2531).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2531).getGroup(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2531).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2531).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2531).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        org.junit.Assert.assertEquals("400", apiErrors.get(2).getCode());
    }

    /* amplification of org.jsondoc.core.doc.ApiDocTest#testApiErrorsDoc */
    @org.junit.Test(timeout = 10000)
    public void testApiErrorsDoc_cf19405_cf20473_failAssert14() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            final org.jsondoc.core.pojo.ApiDoc apiDoc = jsondocScanner.getApiDocs(com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(org.jsondoc.core.util.controller.Test3Controller.class), org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
            final java.util.Set<org.jsondoc.core.pojo.ApiMethodDoc> methods = apiDoc.getMethods();
            final org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc = methods.iterator().next();
            final java.util.List<org.jsondoc.core.pojo.ApiErrorDoc> apiErrors = apiMethodDoc.getApierrors();
            // MethodAssertGenerator build local variable
            Object o_13_0 = methods.size();
            // MethodAssertGenerator build local variable
            Object o_15_0 = apiErrors.size();
            // MethodAssertGenerator build local variable
            Object o_17_0 = apiErrors.get(0).getCode();
            // MethodAssertGenerator build local variable
            Object o_20_0 = apiErrors.get(0).getDescription();
            // MethodAssertGenerator build local variable
            Object o_23_0 = apiErrors.get(1).getCode();
            // StatementAdderOnAssert create random local variable
            org.jsondoc.core.pojo.ApiDoc vc_2527 = new org.jsondoc.core.pojo.ApiDoc();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2527).getDescription(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2527).getName(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            java.util.TreeSet collection_468948226 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_468948226, ((org.jsondoc.core.pojo.ApiDoc)vc_2527).getMethods());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2527).getAuth());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).name(), "UNDEFINED");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2527).getGroup(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getLabel(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2527).getSupportedversions());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).name(), "UNDEFINED");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getLabel(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator replace invocation
            java.lang.String o_testApiErrorsDoc_cf19405__28 = // StatementAdderMethod cloned existing statement
vc_2527.getGroup();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testApiErrorsDoc_cf19405__28, "");
            // StatementAdderOnAssert create null value
            org.jsondoc.core.pojo.ApiDoc vc_2970 = (org.jsondoc.core.pojo.ApiDoc)null;
            // StatementAdderMethod cloned existing statement
            vc_2527.compareTo(vc_2970);
            // MethodAssertGenerator build local variable
            Object o_148_0 = apiErrors.get(2).getCode();
            org.junit.Assert.fail("testApiErrorsDoc_cf19405_cf20473 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.jsondoc.core.doc.ApiDocTest#testApiErrorsDoc */
    @org.junit.Test(timeout = 10000)
    public void testApiErrorsDoc_cf19486_cf24065_cf29204() throws java.lang.Exception {
        final org.jsondoc.core.pojo.ApiDoc apiDoc = jsondocScanner.getApiDocs(com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(org.jsondoc.core.util.controller.Test3Controller.class), org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        final java.util.Set<org.jsondoc.core.pojo.ApiMethodDoc> methods = apiDoc.getMethods();
        final org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc = methods.iterator().next();
        final java.util.List<org.jsondoc.core.pojo.ApiErrorDoc> apiErrors = apiMethodDoc.getApierrors();
        org.junit.Assert.assertEquals(1, methods.size());
        org.junit.Assert.assertEquals(3, apiErrors.size());
        org.junit.Assert.assertEquals("1000", apiErrors.get(0).getCode());
        org.junit.Assert.assertEquals("method-level annotation should be applied", "A test error #1", apiErrors.get(0).getDescription());
        org.junit.Assert.assertEquals("2000", apiErrors.get(1).getCode());
        // StatementAdderOnAssert create random local variable
        org.jsondoc.core.pojo.ApiVersionDoc vc_2571 = new org.jsondoc.core.pojo.ApiVersionDoc();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiVersionDoc)vc_2571).getSince());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiVersionDoc)vc_2571).getUntil());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiVersionDoc)vc_2571).getSince());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiVersionDoc)vc_2571).getUntil());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiVersionDoc)vc_2571).getSince());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiVersionDoc)vc_2571).getUntil());
        // StatementAdderOnAssert create random local variable
        org.jsondoc.core.pojo.ApiDoc vc_2569 = new org.jsondoc.core.pojo.ApiDoc();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        java.util.TreeSet collection_1820210394 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_1820210394, ((org.jsondoc.core.pojo.ApiDoc)vc_2569).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getGroup(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        java.util.TreeSet collection_752736182 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_752736182, ((org.jsondoc.core.pojo.ApiDoc)vc_2569).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getGroup(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        java.util.TreeSet collection_2090583390 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_2090583390, ((org.jsondoc.core.pojo.ApiDoc)vc_2569).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getGroup(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getEnclosingMethod());
        // StatementAdderMethod cloned existing statement
        vc_2569.setSupportedversions(vc_2571);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        java.util.TreeSet collection_1194152490 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_1194152490, ((org.jsondoc.core.pojo.ApiDoc)vc_2569).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getGroup(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiVersionDoc)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getSupportedversions()).getUntil());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiVersionDoc)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getSupportedversions()).getSince());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getSupportedversions().equals(vc_2571));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        java.util.TreeSet collection_213005688 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_213005688, ((org.jsondoc.core.pojo.ApiDoc)vc_2569).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getGroup(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiVersionDoc)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getSupportedversions()).getUntil());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiVersionDoc)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getSupportedversions()).getSince());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getSupportedversions().equals(vc_2571));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        java.util.TreeSet collection_24125773 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_24125773, ((org.jsondoc.core.pojo.ApiDoc)vc_2569).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getGroup(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiVersionDoc)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getSupportedversions()).getUntil());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiVersionDoc)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getSupportedversions()).getSince());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getSupportedversions().equals(vc_2571));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getEnclosingMethod());
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_236 = "2000";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_236, "2000");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_236, "2000");
        // StatementAdderMethod cloned existing statement
        vc_2569.setName(String_vc_236);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        java.util.TreeSet collection_1042010043 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_1042010043, ((org.jsondoc.core.pojo.ApiDoc)vc_2569).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getGroup(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiVersionDoc)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getSupportedversions()).getUntil());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiVersionDoc)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getSupportedversions()).getSince());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getName(), "2000");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getSupportedversions().equals(vc_2571));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        java.util.TreeSet collection_48600356 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_48600356, ((org.jsondoc.core.pojo.ApiDoc)vc_2569).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getGroup(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiVersionDoc)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getSupportedversions()).getUntil());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiVersionDoc)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getSupportedversions()).getSince());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getName(), "2000");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((org.jsondoc.core.pojo.ApiDoc)vc_2569).getSupportedversions().equals(vc_2571));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2569).getStage()).getDeclaringClass()).getEnclosingMethod());
        // StatementAdderOnAssert create random local variable
        org.jsondoc.core.pojo.ApiDoc vc_6385 = new org.jsondoc.core.pojo.ApiDoc();
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_6385).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_6385).getGroup(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_6385).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_6385).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        java.util.TreeSet collection_1102354983 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_1102354983, ((org.jsondoc.core.pojo.ApiDoc)vc_6385).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_6385).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_6385).getDescription(), "");
        // AssertGenerator replace invocation
        int o_testApiErrorsDoc_cf19486_cf24065_cf29204__620 = // StatementAdderMethod cloned existing statement
vc_6385.compareTo(apiDoc);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testApiErrorsDoc_cf19486_cf24065_cf29204__620, -15);
        org.junit.Assert.assertEquals("400", apiErrors.get(2).getCode());
    }

    /* amplification of org.jsondoc.core.doc.ApiDocTest#testApiErrorsDoc */
    @org.junit.Test(timeout = 10000)
    public void testApiErrorsDoc_cf19455_cf22707_add26981() throws java.lang.Exception {
        final org.jsondoc.core.pojo.ApiDoc apiDoc = jsondocScanner.getApiDocs(com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(org.jsondoc.core.util.controller.Test3Controller.class), org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
        final java.util.Set<org.jsondoc.core.pojo.ApiMethodDoc> methods = apiDoc.getMethods();
        final org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc = methods.iterator().next();
        final java.util.List<org.jsondoc.core.pojo.ApiErrorDoc> apiErrors = apiMethodDoc.getApierrors();
        org.junit.Assert.assertEquals(1, methods.size());
        org.junit.Assert.assertEquals(3, apiErrors.size());
        org.junit.Assert.assertEquals("1000", apiErrors.get(0).getCode());
        org.junit.Assert.assertEquals("method-level annotation should be applied", "A test error #1", apiErrors.get(0).getDescription());
        org.junit.Assert.assertEquals("2000", apiErrors.get(1).getCode());
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_136 = "1000";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_136, "1000");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_136, "1000");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(String_vc_136, "1000");
        // StatementAdderOnAssert create random local variable
        org.jsondoc.core.pojo.ApiDoc vc_2553 = new org.jsondoc.core.pojo.ApiDoc();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        java.util.TreeSet collection_38536294 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_38536294, ((org.jsondoc.core.pojo.ApiDoc)vc_2553).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getGroup(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        java.util.TreeSet collection_746609547 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_746609547, ((org.jsondoc.core.pojo.ApiDoc)vc_2553).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getGroup(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        java.util.TreeSet collection_1114649771 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_1114649771, ((org.jsondoc.core.pojo.ApiDoc)vc_2553).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getGroup(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isPrimitive());
        // StatementAdderMethod cloned existing statement
        // MethodCallAdder
        vc_2553.setGroup(String_vc_136);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        java.util.TreeSet collection_1882492735 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_1882492735, ((org.jsondoc.core.pojo.ApiDoc)vc_2553).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getGroup(), "1000");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isPrimitive());
        // StatementAdderMethod cloned existing statement
        vc_2553.setGroup(String_vc_136);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        java.util.TreeSet collection_2035137529 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_2035137529, ((org.jsondoc.core.pojo.ApiDoc)vc_2553).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getGroup(), "1000");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        java.util.TreeSet collection_1448498321 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_1448498321, ((org.jsondoc.core.pojo.ApiDoc)vc_2553).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getGroup(), "1000");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        java.util.TreeSet collection_1362617711 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_1362617711, ((org.jsondoc.core.pojo.ApiDoc)vc_2553).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getGroup(), "1000");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2553).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2553).getStage()).getDeclaringClass()).isPrimitive());
        // StatementAdderOnAssert create random local variable
        org.jsondoc.core.pojo.ApiDoc vc_3867 = new org.jsondoc.core.pojo.ApiDoc();
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_3867).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_3867).getGroup(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_3867).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_3867).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.TreeSet collection_986724734 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_986724734, ((org.jsondoc.core.pojo.ApiDoc)vc_3867).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_3867).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_3867).getName(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).isSynthetic());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_3867).getGroup(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_3867).getDescription(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).isArray());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).isInterface());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_3867).getAuth());
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).isEnum());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).isMemberClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).getEnclosingClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).isAnnotation());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        java.util.TreeSet collection_1176248954 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_1176248954, ((org.jsondoc.core.pojo.ApiDoc)vc_3867).getMethods());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).getEnclosingMethod());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).getSigners());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).ordinal(), 0);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).getComponentType());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).name(), "UNDEFINED");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getLabel(), "");
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).isAnonymousClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).isLocalClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).isPrimitive());
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getStage()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_3867).getSupportedversions());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_3867).getVisibility()).getDeclaringClass()).getDeclaringClass());
        // AssertGenerator replace invocation
        int o_testApiErrorsDoc_cf19455_cf22707__260 = // StatementAdderMethod cloned existing statement
apiDoc.compareTo(vc_3867);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testApiErrorsDoc_cf19455_cf22707__260, 15);
        org.junit.Assert.assertEquals("400", apiErrors.get(2).getCode());
    }

    /* amplification of org.jsondoc.core.doc.ApiDocTest#testApiErrorsDoc */
    @org.junit.Test(timeout = 10000)
    public void testApiErrorsDoc_cf19405_cf20516_cf30796_failAssert40() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            final org.jsondoc.core.pojo.ApiDoc apiDoc = jsondocScanner.getApiDocs(com.google.common.collect.Sets.<java.lang.Class<?>>newHashSet(org.jsondoc.core.util.controller.Test3Controller.class), org.jsondoc.core.pojo.JSONDoc.MethodDisplay.URI).iterator().next();
            final java.util.Set<org.jsondoc.core.pojo.ApiMethodDoc> methods = apiDoc.getMethods();
            final org.jsondoc.core.pojo.ApiMethodDoc apiMethodDoc = methods.iterator().next();
            final java.util.List<org.jsondoc.core.pojo.ApiErrorDoc> apiErrors = apiMethodDoc.getApierrors();
            // MethodAssertGenerator build local variable
            Object o_13_0 = methods.size();
            // MethodAssertGenerator build local variable
            Object o_15_0 = apiErrors.size();
            // MethodAssertGenerator build local variable
            Object o_17_0 = apiErrors.get(0).getCode();
            // MethodAssertGenerator build local variable
            Object o_20_0 = apiErrors.get(0).getDescription();
            // MethodAssertGenerator build local variable
            Object o_23_0 = apiErrors.get(1).getCode();
            // StatementAdderOnAssert create random local variable
            org.jsondoc.core.pojo.ApiDoc vc_2527 = new org.jsondoc.core.pojo.ApiDoc();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2527).getDescription(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2527).getName(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            java.util.TreeSet collection_1023184676 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_1023184676, ((org.jsondoc.core.pojo.ApiDoc)vc_2527).getMethods());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2527).getAuth());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).name(), "UNDEFINED");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2527).getGroup(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getLabel(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2527).getSupportedversions());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).name(), "UNDEFINED");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getLabel(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2527).getDescription(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2527).getName(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            java.util.TreeSet collection_468948226 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_468948226, ((org.jsondoc.core.pojo.ApiDoc)vc_2527).getMethods());;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2527).getAuth());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).name(), "UNDEFINED");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2527).getGroup(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getLabel(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2527).getSupportedversions());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).name(), "UNDEFINED");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getLabel(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getVisibility()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2527).getStage()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator replace invocation
            java.lang.String o_testApiErrorsDoc_cf19405__28 = // StatementAdderMethod cloned existing statement
vc_2527.getGroup();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_testApiErrorsDoc_cf19405__28, "");
            // StatementAdderOnAssert create null value
            org.jsondoc.core.pojo.ApiAuthDoc vc_2994 = (org.jsondoc.core.pojo.ApiAuthDoc)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_2994);
            // StatementAdderOnAssert create random local variable
            org.jsondoc.core.pojo.ApiDoc vc_2993 = new org.jsondoc.core.pojo.ApiDoc();
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).name(), "UNDEFINED");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2993).getAuth());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getLabel(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getLabel(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2993).getGroup(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            java.util.TreeSet collection_206463945 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_206463945, ((org.jsondoc.core.pojo.ApiDoc)vc_2993).getMethods());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).name(), "UNDEFINED");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2993).getDescription(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2993).getSupportedversions());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2993).getName(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).getEnclosingConstructor());
            // StatementAdderMethod cloned existing statement
            vc_2993.setAuth(vc_2994);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).getTypeName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).name(), "UNDEFINED");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2993).getAuth());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).isAnnotation());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getLabel(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).getEnclosingConstructor());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getLabel(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2993).getGroup(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).isMemberClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).ordinal(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            java.util.TreeSet collection_797370202 = new java.util.TreeSet<Object>();
	org.junit.Assert.assertEquals(collection_797370202, ((org.jsondoc.core.pojo.ApiDoc)vc_2993).getMethods());;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).isSynthetic());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).desiredAssertionStatus());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).isInterface());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).getModifiers(), 16401);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).getSimpleName(), "ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).getEnclosingClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).getComponentType());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).isArray());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).isPrimitive());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).isAnonymousClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).name(), "UNDEFINED");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).toGenericString(), "public final enum org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).getEnclosingMethod());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertFalse(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).isLocalClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).getSimpleName(), "ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiVisibility");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2993).getDescription(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).getCanonicalName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).getSigners());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((org.jsondoc.core.pojo.ApiDoc)vc_2993).getSupportedversions());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((org.jsondoc.core.pojo.ApiDoc)vc_2993).getName(), "");
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((java.lang.Class)((org.jsondoc.core.pojo.ApiVisibility)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getVisibility()).getDeclaringClass()).isEnum());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).getName(), "org.jsondoc.core.pojo.ApiStage");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).getDeclaringClass());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((java.lang.Class)((org.jsondoc.core.pojo.ApiStage)((org.jsondoc.core.pojo.ApiDoc)vc_2993).getStage()).getDeclaringClass()).getEnclosingConstructor());
            // StatementAdderOnAssert create null value
            org.jsondoc.core.pojo.ApiMethodDoc vc_7022 = (org.jsondoc.core.pojo.ApiMethodDoc)null;
            // StatementAdderOnAssert create random local variable
            org.jsondoc.core.pojo.ApiDoc vc_7021 = new org.jsondoc.core.pojo.ApiDoc();
            // StatementAdderMethod cloned existing statement
            vc_7021.addMethod(vc_7022);
            // MethodAssertGenerator build local variable
            Object o_494_0 = apiErrors.get(2).getCode();
            org.junit.Assert.fail("testApiErrorsDoc_cf19405_cf20516_cf30796 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

