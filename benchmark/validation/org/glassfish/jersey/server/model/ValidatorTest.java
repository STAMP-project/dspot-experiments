/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.server.model;


import ServerProperties.RESOURCE_VALIDATION_IGNORE_ERRORS;
import Severity.FATAL;
import Severity.HINT;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import javax.inject.Singleton;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.sse.SseEventSink;
import org.glassfish.jersey.Severity;
import org.glassfish.jersey.internal.Errors;
import org.glassfish.jersey.internal.inject.PerLookup;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.RequestContextBuilder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerBootstrapBag;
import org.junit.Assert;
import org.junit.Test;


/**
 * Taken from Jersey 1: jersey-server:com.sun.jersey.server.impl.modelapi.validation.ResourceModelValidatorTest.java
 *
 * @author Jakub Podlesak (jakub.podlesak at oracle.com)
 */
public class ValidatorTest {
    private static final Logger LOGGER = Logger.getLogger(ValidatorTest.class.getName());

    @Path("rootNonAmbigCtors")
    public static class TestRootResourceNonAmbigCtors {
        // TODO: hmmm, even if this is not ambiguous, it is strange; shall we warn, the 1st and the 2nd ctor won't be used?
        public TestRootResourceNonAmbigCtors(@QueryParam("s")
        String s) {
        }

        public TestRootResourceNonAmbigCtors(@QueryParam("n")
        int n) {
        }

        public TestRootResourceNonAmbigCtors(@QueryParam("s")
        String s, @QueryParam("n")
        int n) {
        }

        @GET
        public String getIt() {
            return "it";
        }
    }

    @Test
    public void testRootResourceNonAmbigConstructors() throws Exception {
        ValidatorTest.LOGGER.info(("No issue should be reported if more public ctors exists with the same number of params, " + "but another just one is presented with more params at a root resource:"));
        Resource resource = Resource.builder(ValidatorTest.TestRootResourceNonAmbigCtors.class).build();
        ServerBootstrapBag serverBag = initializeApplication();
        ComponentModelValidator validator = new ComponentModelValidator(serverBag.getValueParamProviders(), serverBag.getMessageBodyWorkers());
        validator.validate(resource);
        Assert.assertTrue(validator.getIssueList().isEmpty());
    }

    public static class MyBeanParam {
        @HeaderParam("h")
        String hParam;
    }

    @Singleton
    @Path("rootSingleton/{p}")
    public static class TestCantInjectFieldsForSingleton {
        @MatrixParam("m")
        String matrixParam;

        @QueryParam("q")
        String queryParam;

        @PathParam("p")
        String pParam;

        @CookieParam("c")
        String cParam;

        @HeaderParam("h")
        String hParam;

        @BeanParam
        ValidatorTest.MyBeanParam beanParam;

        @GET
        public String getIt() {
            return "it";
        }
    }

    public static interface ChildOfContainerRequestFilter extends ContainerRequestFilter {}

    @Path("rootSingleton/{p}")
    public static class TestCantInjectFieldsForProvider implements ValidatorTest.ChildOfContainerRequestFilter {
        @MatrixParam("m")
        String matrixParam;

        @QueryParam("q")
        String queryParam;

        @PathParam("p")
        String pParam;

        @CookieParam("c")
        String cParam;

        @HeaderParam("h")
        String hParam;

        @BeanParam
        ValidatorTest.MyBeanParam beanParam;

        @GET
        public String getIt() {
            return "it";
        }

        @Override
        public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        }
    }

    @Singleton
    @Path("rootSingletonConstructorB/{p}")
    public static class TestCantInjectConstructorParamsForSingleton {
        public TestCantInjectConstructorParamsForSingleton() {
        }

        public TestCantInjectConstructorParamsForSingleton(@QueryParam("q")
        String queryParam) {
        }

        @GET
        public String getIt() {
            return "it";
        }
    }

    @Singleton
    @Path("rootSingletonConstructorB/{p}")
    public static class TestCantInjectMethodParamsForSingleton {
        @GET
        public String getIt(@QueryParam("q")
        String queryParam) {
            return "it";
        }
    }

    @Singleton
    @Path("sseEventSinkWithReturnType")
    public static class TestSseEventSinkValidations {
        @Path("notVoid")
        @GET
        public SseEventSink nonVoidReturnType(@Context
        SseEventSink eventSink) {
            return eventSink;
        }

        @Path("multiple")
        @GET
        public void multipleInjection(@Context
        SseEventSink firstSink, @Context
        SseEventSink secondSink) {
            // do nothing
        }
    }

    @Path("rootRelaxedParser")
    @Produces(" a/b, c/d ")
    @Consumes({ "e/f,g/h", " i/j" })
    public static class TestRelaxedProducesConsumesParserRules {
        @GET
        @Produces({ "e/f,g/h", " i/j" })
        @Consumes(" a/b, c/d ")
        public String getIt(@QueryParam("q")
        String queryParam) {
            return "it";
        }
    }

    @Test
    public void testRelaxedProducesConsumesParserRules() throws Exception {
        ValidatorTest.LOGGER.info("An issue should not be reported with the relaxed Produces/Consumes values parser.");
        List<ResourceModelIssue> issues = testResourceValidation(ValidatorTest.TestCantInjectMethodParamsForSingleton.class);
        Assert.assertTrue(issues.isEmpty());
    }

    @Test
    public void testSingletonFieldsInjection() throws Exception {
        ValidatorTest.LOGGER.info("An issue should be reported if injection is required for a singleton life-cycle:");
        List<ResourceModelIssue> issues = testResourceValidation(ValidatorTest.TestCantInjectFieldsForSingleton.class);
        Assert.assertTrue((!(issues.isEmpty())));
        Assert.assertEquals(6, issues.size());
    }

    @Test
    public void testCantReturnFromEventSinkInjectedMethod() {
        ValidatorTest.LOGGER.info("An issue should be reported if method with injected SseEventSink parameter does not return void.");
        final List<ResourceModelIssue> issues = testResourceValidation(ValidatorTest.TestSseEventSinkValidations.class);
        Assert.assertTrue((!(issues.isEmpty())));
        Assert.assertEquals(2, issues.size());
    }

    @Test
    public void testProviderFieldsInjection() throws Exception {
        ValidatorTest.LOGGER.info(("An issue should be reported if injection is required for a class which is provider and " + "therefore singleton:"));
        List<ResourceModelIssue> issues = testResourceValidation(ValidatorTest.TestCantInjectFieldsForProvider.class);
        Assert.assertTrue((!(issues.isEmpty())));
        Assert.assertEquals(7, issues.size());
    }

    @Test
    public void testSingletonConstructorParamsInjection() throws Exception {
        ValidatorTest.LOGGER.info("An issue should be reported if injection is required for a singleton life-cycle:");
        List<ResourceModelIssue> issues = testResourceValidation(ValidatorTest.TestCantInjectConstructorParamsForSingleton.class);
        Assert.assertTrue((!(issues.isEmpty())));
        Assert.assertEquals(1, issues.size());
    }

    @Test
    public void testSingletonMethodParamsInjection() throws Exception {
        ValidatorTest.LOGGER.info("An issue should not be reported as injections into the methods are allowed.");
        List<ResourceModelIssue> issues = testResourceValidation(ValidatorTest.TestCantInjectMethodParamsForSingleton.class);
        Assert.assertTrue(issues.isEmpty());
    }

    @Path("resourceAsProvider")
    public static class ResourceAsProvider implements ContainerRequestFilter {
        @Override
        public void filter(ContainerRequestContext requestContext) throws IOException {
        }

        @GET
        public String get() {
            return "get";
        }
    }

    @Singleton
    @PerLookup
    @Path("resourceMultipleScopes")
    public static class ResourceWithMultipleScopes implements ContainerRequestFilter {
        @Override
        public void filter(ContainerRequestContext requestContext) throws IOException {
        }

        @GET
        public String get() {
            return "get";
        }
    }

    @Test
    public void testResourceAsProvider() throws Exception {
        ValidatorTest.LOGGER.info("An issue should be reported as resource implements provider but does not define scope.");
        List<ResourceModelIssue> issues = testResourceValidation(ValidatorTest.ResourceAsProvider.class);
        Assert.assertEquals(1, issues.size());
    }

    @Test
    public void testResourceWithMultipleScopes() throws Exception {
        ValidatorTest.LOGGER.info("An issue should not be reported as resource defines multiple scopes.");
        List<ResourceModelIssue> issues = testResourceValidation(ValidatorTest.ResourceWithMultipleScopes.class);
        Assert.assertEquals(1, issues.size());
    }

    public static class TestNonPublicRM {
        @POST
        public String publicPost() {
            return "public";
        }

        @GET
        private String privateGet() {
            return "private";
        }
    }

    @Test
    public void testNonPublicRM() throws Exception {
        ValidatorTest.LOGGER.info("An issue should be reported if a resource method is not public:");
        List<ResourceModelIssue> issues = testResourceValidation(ValidatorTest.TestNonPublicRM.class);
        Assert.assertTrue((!(issues.isEmpty())));
    }

    public static class TestMoreThanOneEntity {
        @PUT
        public void put(String one, String two) {
        }
    }

    @Path("test")
    public static class TestGetRMReturningVoid {
        @GET
        public void getMethod() {
        }
    }

    @Test
    public void testGetRMReturningVoid() throws Exception {
        ValidatorTest.LOGGER.info("An issue should be reported if a non-async get method returns void:");
        List<ResourceModelIssue> issues = testResourceValidation(ValidatorTest.TestGetRMReturningVoid.class);
        Assert.assertFalse(issues.isEmpty());
        Assert.assertNotEquals(FATAL, issues.get(0).getSeverity());
    }

    public static class TestAsyncGetRMReturningVoid {
        @GET
        public void getMethod(@Suspended
        AsyncResponse ar) {
        }
    }

    @Test
    public void testAsyncGetRMReturningVoid() throws Exception {
        ValidatorTest.LOGGER.info("An issue should NOT be reported if a async get method returns void:");
        List<ResourceModelIssue> issues = testResourceValidation(ValidatorTest.TestAsyncGetRMReturningVoid.class);
        Assert.assertTrue(issues.isEmpty());
    }

    @Path("test")
    public static class TestGetRMConsumingEntity {
        @GET
        public String getMethod(Object o) {
            return "it";
        }
    }

    @Test
    public void testGetRMConsumingEntity() throws Exception {
        ValidatorTest.LOGGER.info("An issue should be reported if a get method consumes an entity:");
        List<ResourceModelIssue> issues = testResourceValidation(ValidatorTest.TestGetRMConsumingEntity.class);
        Assert.assertFalse(issues.isEmpty());
        Assert.assertNotEquals(FATAL, issues.get(0).getSeverity());
    }

    @Path("test")
    public static class TestGetRMConsumingFormParam {
        @GET
        public String getMethod(@FormParam("f")
        String s1, @FormParam("g")
        String s2) {
            return "it";
        }
    }

    @Test
    public void testGetRMConsumingFormParam() throws Exception {
        ValidatorTest.LOGGER.info("An issue should be reported if a get method consumes a form param:");
        List<ResourceModelIssue> issues = testResourceValidation(ValidatorTest.TestGetRMConsumingFormParam.class);
        Assert.assertTrue(((issues.size()) == 1));
        Assert.assertEquals(FATAL, issues.get(0).getSeverity());
    }

    @Path("test")
    public static class TestSRLReturningVoid {
        @Path("srl")
        public void srLocator() {
        }
    }

    @Test
    public void testSRLReturningVoid() throws Exception {
        ValidatorTest.LOGGER.info("An issue should be reported if a sub-resource locator returns void:");
        Resource resource = Resource.builder(ValidatorTest.TestSRLReturningVoid.class).build();
        ServerBootstrapBag serverBag = initializeApplication();
        ComponentModelValidator validator = new ComponentModelValidator(serverBag.getValueParamProviders(), serverBag.getMessageBodyWorkers());
        validator.validate(resource);
        Assert.assertTrue(validator.fatalIssuesFound());
    }

    @Path("test")
    public static class TestGetSRMReturningVoid {
        @GET
        @Path("srm")
        public void getSRMethod() {
        }
    }

    @Test
    public void testGetSRMReturningVoid() throws Exception {
        ValidatorTest.LOGGER.info("An issue should be reported if a get sub-resource method returns void:");
        List<ResourceModelIssue> issues = testResourceValidation(ValidatorTest.TestGetSRMReturningVoid.class);
        Assert.assertFalse(issues.isEmpty());
        Assert.assertNotEquals(FATAL, issues.get(0).getSeverity());
    }

    @Path("test")
    public static class TestGetSRMConsumingEntity {
        @Path("p")
        @GET
        public String getMethod(Object o) {
            return "it";
        }
    }

    @Test
    public void testGetSRMConsumingEntity() throws Exception {
        ValidatorTest.LOGGER.info("An issue should be reported if a get method consumes an entity:");
        List<ResourceModelIssue> issues = testResourceValidation(ValidatorTest.TestGetSRMConsumingEntity.class);
        Assert.assertFalse(issues.isEmpty());
        Assert.assertNotEquals(FATAL, issues.get(0).getSeverity());
    }

    @Path("root")
    public static class TestGetSRMConsumingFormParam {
        @GET
        @Path("p")
        public String getMethod(@FormParam("f")
        String formParam) {
            return "it";
        }
    }

    @Test
    public void testGetSRMConsumingFormParam() throws Exception {
        ValidatorTest.LOGGER.info("An issue should be reported if a get method consumes a form param:");
        List<ResourceModelIssue> issues = testResourceValidation(ValidatorTest.TestGetSRMConsumingFormParam.class);
        Assert.assertFalse(issues.isEmpty());
        Assert.assertEquals(FATAL, issues.get(0).getSeverity());
    }

    @Path("rootMultipleHttpMethodDesignatorsRM")
    public static class TestMultipleHttpMethodDesignatorsRM {
        @GET
        @PUT
        public String getPutIt() {
            return "it";
        }
    }

    @Test
    public void testMultipleHttpMethodDesignatorsRM() throws Exception {
        ValidatorTest.LOGGER.info(("An issue should be reported if more than one HTTP method designator exist on a resource " + "method:"));
        Resource resource = Resource.builder(ValidatorTest.TestMultipleHttpMethodDesignatorsRM.class).build();
        ServerBootstrapBag serverBag = initializeApplication();
        ComponentModelValidator validator = new ComponentModelValidator(serverBag.getValueParamProviders(), serverBag.getMessageBodyWorkers());
        validator.validate(resource);
        Assert.assertTrue(validator.fatalIssuesFound());
    }

    @Path("rootMultipleHttpMethodDesignatorsSRM")
    public static class TestMultipleHttpMethodDesignatorsSRM {
        @Path("srm")
        @POST
        @PUT
        public String postPutIt() {
            return "it";
        }
    }

    @Test
    public void testMultipleHttpMethodDesignatorsSRM() throws Exception {
        ValidatorTest.LOGGER.info(("An issue should be reported if more than one HTTP method designator exist on a sub-resource " + "method:"));
        Resource resource = Resource.builder(ValidatorTest.TestMultipleHttpMethodDesignatorsSRM.class).build();
        ServerBootstrapBag serverBag = initializeApplication();
        ComponentModelValidator validator = new ComponentModelValidator(serverBag.getValueParamProviders(), serverBag.getMessageBodyWorkers());
        validator.validate(resource);
        Assert.assertTrue(validator.fatalIssuesFound());
    }

    @Path("rootEntityParamOnSRL")
    public static class TestEntityParamOnSRL {
        @Path("srl")
        public String locator(String s) {
            return "it";
        }
    }

    @Test
    public void testEntityParamOnSRL() throws Exception {
        ValidatorTest.LOGGER.info("An issue should be reported if an entity parameter exists on a sub-resource locator:");
        Resource resource = Resource.builder(ValidatorTest.TestEntityParamOnSRL.class).build();
        ServerBootstrapBag serverBag = initializeApplication();
        ComponentModelValidator validator = new ComponentModelValidator(serverBag.getValueParamProviders(), serverBag.getMessageBodyWorkers());
        validator.validate(resource);
        Assert.assertTrue(validator.fatalIssuesFound());
    }

    @Path("/DeleteTest")
    public static class TestNonConflictingHttpMethodDelete {
        static String html_content = "<html>" + ("<head><title>Delete text/html</title></head>" + "<body>Delete text/html</body></html>");

        @DELETE
        @Produces("text/plain")
        public String getPlain() {
            return "Delete text/plain";
        }

        @DELETE
        @Produces("text/html")
        public String getHtml() {
            return ValidatorTest.TestNonConflictingHttpMethodDelete.html_content;
        }

        @DELETE
        @Path("/sub")
        @Produces("text/html")
        public String getSub() {
            return ValidatorTest.TestNonConflictingHttpMethodDelete.html_content;
        }
    }

    @Test
    public void testNonConflictingHttpMethodDelete() throws Exception {
        ValidatorTest.LOGGER.info("No issue should be reported if produced mime types differ");
        List<ResourceModelIssue> issues = testResourceValidation(ValidatorTest.TestNonConflictingHttpMethodDelete.class);
        Assert.assertTrue(issues.isEmpty());
    }

    @Path("/AmbigParamTest")
    public static class TestAmbiguousParams {
        @QueryParam("q")
        @HeaderParam("q")
        private int a;

        @QueryParam("b")
        @HeaderParam("b")
        @MatrixParam("q")
        public void setB(String b) {
        }

        @GET
        @Path("a")
        public String get(@PathParam("a")
        @QueryParam("a")
        String a) {
            return "hi";
        }

        @GET
        @Path("b")
        public String getSub(@PathParam("a")
        @QueryParam("b")
        @MatrixParam("c")
        String a, @MatrixParam("m")
        @QueryParam("m")
        int i) {
            return "hi";
        }

        @Path("c")
        public Object getSubLoc(@MatrixParam("m")
        @CookieParam("c")
        String a) {
            return null;
        }
    }

    @Test
    public void testAmbiguousParams() throws Exception {
        ValidatorTest.LOGGER.info("A warning should be reported if ambiguous source of a parameter is seen");
        Errors.process(new Runnable() {
            @Override
            public void run() {
                Resource resource = Resource.builder(ValidatorTest.TestAmbiguousParams.class).build();
                ServerBootstrapBag serverBag = initializeApplication();
                ComponentModelValidator validator = new ComponentModelValidator(serverBag.getValueParamProviders(), serverBag.getMessageBodyWorkers());
                validator.validate(resource);
                Assert.assertTrue((!(validator.fatalIssuesFound())));
                Assert.assertEquals(4, validator.getIssueList().size());
                Assert.assertEquals(6, Errors.getErrorMessages().size());
            }
        });
    }

    @Path("/EmptyPathSegmentTest")
    public static class TestEmptyPathSegment {
        @GET
        @Path("/")
        public String get() {
            return "hi";
        }
    }

    @Test
    public void testEmptyPathSegment() throws Exception {
        ValidatorTest.LOGGER.info("A warning should be reported if @Path with \"/\" or empty string value is seen");
        Resource resource = Resource.builder(ValidatorTest.TestEmptyPathSegment.class).build();
        ServerBootstrapBag serverBag = initializeApplication();
        ComponentModelValidator validator = new ComponentModelValidator(serverBag.getValueParamProviders(), serverBag.getMessageBodyWorkers());
        validator.validate(resource);
        Assert.assertTrue((!(validator.fatalIssuesFound())));
        Assert.assertEquals(1, validator.getIssueList().size());
    }

    public static class TypeVariableResource<T, V> {
        @QueryParam("v")
        V fieldV;

        V methodV;

        @QueryParam("v")
        public void set(V methodV) {
            this.methodV = methodV;
        }

        @GET
        public String get(@BeanParam
        V getV) {
            return ((getV.toString()) + (fieldV.toString())) + (methodV.toString());
        }

        @POST
        public T post(T t) {
            return t;
        }

        @Path("sub")
        @POST
        public T postSub(T t) {
            return t;
        }
    }

    @Test
    public void testTypeVariableResource() throws Exception {
        ValidatorTest.LOGGER.info("");
        Errors.process(new Runnable() {
            @Override
            public void run() {
                Resource resource = Resource.builder(ValidatorTest.TypeVariableResource.class).build();
                ServerBootstrapBag serverBag = initializeApplication();
                ComponentModelValidator validator = new ComponentModelValidator(serverBag.getValueParamProviders(), serverBag.getMessageBodyWorkers());
                validator.validate(resource);
                Assert.assertTrue((!(validator.fatalIssuesFound())));
                Assert.assertEquals(5, validator.getIssueList().size());
                Assert.assertEquals(7, Errors.getErrorMessages().size());
            }
        });
    }

    public static class ParameterizedTypeResource<T, V> {
        @QueryParam("v")
        Collection<V> fieldV;

        List<List<V>> methodV;

        @QueryParam("v")
        public void set(List<List<V>> methodV) {
            this.methodV = methodV;
        }

        @GET
        public String get(@QueryParam("v")
        List<V> getV) {
            return "";
        }

        @POST
        public Collection<T> post(Collection<T> t) {
            return t;
        }

        @Path("sub")
        @POST
        public Collection<T> postSub(Collection<T> t) {
            return t;
        }
    }

    public static class ConcreteParameterizedTypeResource extends ValidatorTest.ParameterizedTypeResource<String, String> {}

    @Test
    public void testParameterizedTypeResource() throws Exception {
        ValidatorTest.LOGGER.info("");
        Resource resource = Resource.builder(ValidatorTest.ConcreteParameterizedTypeResource.class).build();
        ServerBootstrapBag serverBag = initializeApplication();
        ComponentModelValidator validator = new ComponentModelValidator(serverBag.getValueParamProviders(), serverBag.getMessageBodyWorkers());
        validator.validate(resource);
        Assert.assertTrue((!(validator.fatalIssuesFound())));
        Assert.assertEquals(0, validator.getIssueList().size());
    }

    public static class GenericArrayResource<T, V> {
        @QueryParam("v")
        V[] fieldV;

        V[] methodV;

        @QueryParam("v")
        public void set(V[] methodV) {
            this.methodV = methodV;
        }

        @POST
        public T[] post(T[] t) {
            return t;
        }

        @Path("sub")
        @POST
        public T[] postSub(T[] t) {
            return t;
        }
    }

    public static class ConcreteGenericArrayResource extends ValidatorTest.GenericArrayResource<String, String> {}

    @Test
    public void testGenericArrayResource() throws Exception {
        ValidatorTest.LOGGER.info("");
        Resource resource = Resource.builder(ValidatorTest.ConcreteGenericArrayResource.class).build();
        ServerBootstrapBag serverBag = initializeApplication();
        ComponentModelValidator validator = new ComponentModelValidator(serverBag.getValueParamProviders(), serverBag.getMessageBodyWorkers());
        validator.validate(resource);
        Assert.assertTrue((!(validator.fatalIssuesFound())));
        Assert.assertEquals(0, validator.getIssueList().size());
    }

    // TODO: test multiple root resources with the same uriTemplate (in WebApplicationImpl.processRootResources ?)
    @Path("test1")
    public static class PercentEncodedTest {
        @GET
        @Path("%5B%5D")
        public String percent() {
            return "percent";
        }

        @GET
        @Path("[]")
        public String notEncoded() {
            return "not-encoded";
        }
    }

    @Test
    public void testPercentEncoded() throws Exception {
        List<ResourceModelIssue> issues = testResourceValidation(ValidatorTest.PercentEncodedTest.class);
        Assert.assertEquals(1, issues.size());
        Assert.assertEquals(FATAL, issues.get(0).getSeverity());
    }

    @Path("test2")
    public static class PercentEncodedCaseSensitiveTest {
        @GET
        @Path("%5B%5D")
        public String percent() {
            return "percent";
        }

        @GET
        @Path("%5b%5d")
        public String notEncoded() {
            return "not-encoded";
        }
    }

    @Test
    public void testPercentEncodedCaseSensitive() throws Exception {
        List<ResourceModelIssue> issues = testResourceValidation(ValidatorTest.PercentEncodedCaseSensitiveTest.class);
        Assert.assertEquals(1, issues.size());
        Assert.assertEquals(FATAL, issues.get(0).getSeverity());
    }

    @Path("ambiguous-parameter")
    public static class AmbiguousParameterResource {
        @POST
        public String moreNonAnnotatedParameters(@HeaderParam("something")
        String header, String entity1, String entity2) {
            return "x";
        }
    }

    @Test
    public void testNotAnnotatedParameters() throws Exception {
        Resource resource = Resource.builder(ValidatorTest.AmbiguousParameterResource.class).build();
        ServerBootstrapBag serverBag = initializeApplication();
        ComponentModelValidator validator = new ComponentModelValidator(serverBag.getValueParamProviders(), serverBag.getMessageBodyWorkers());
        validator.validate(resource);
        final List<ResourceModelIssue> errorMessages = validator.getIssueList();
        Assert.assertEquals(1, errorMessages.size());
        Assert.assertEquals(FATAL, errorMessages.get(0).getSeverity());
    }

    public static class SubResource {
        public static final String MESSAGE = "Got it!";

        @GET
        public String getIt() {
            return ValidatorTest.SubResource.MESSAGE;
        }
    }

    /**
     * Should report warning during validation as Resource cannot have resource method and sub resource locators on the same path.
     */
    @Path("failRoot")
    public static class MethodAndLocatorResource {
        @Path("/")
        public ValidatorTest.SubResource getSubResourceLocator() {
            return new ValidatorTest.SubResource();
        }

        @GET
        public String get() {
            return "should never be called - fails during validation";
        }
    }

    @Test
    public void testLocatorAndMethodValidation() throws Exception {
        ValidatorTest.LOGGER.info(("Should report warning during validation as Resource cannot have resource method and sub " + "resource locators on the same path."));
        List<ResourceModelIssue> issues = testResourceValidation(ValidatorTest.MethodAndLocatorResource.class);
        Assert.assertEquals(1, issues.size());
        Assert.assertNotEquals(FATAL, issues.get(0).getSeverity());
    }

    /**
     * Should report warning during validation as Resource cannot have resource method and sub resource locators on the same path.
     */
    @Path("failRoot")
    public static class MethodAndLocatorResource2 {
        @Path("a")
        public ValidatorTest.SubResource getSubResourceLocator() {
            return new ValidatorTest.SubResource();
        }

        @GET
        @Path("a")
        public String get() {
            return "should never be called - fails during validation";
        }
    }

    @Test
    public void testLocatorAndMethod2Validation() throws Exception {
        ValidatorTest.LOGGER.info(("Should report warning during validation as Resource cannot have resource method and sub " + "resource locators on the same path."));
        List<ResourceModelIssue> issues = testResourceValidation(ValidatorTest.MethodAndLocatorResource2.class);
        Assert.assertEquals(1, issues.size());
        Assert.assertNotEquals(FATAL, issues.get(0).getSeverity());
    }

    /**
     * Warning should be reported informing wich locator will be used in runtime
     */
    @Path("locator")
    public static class TwoLocatorsResource {
        @Path("a")
        public ValidatorTest.SubResource locator() {
            return new ValidatorTest.SubResource();
        }

        @Path("a")
        public ValidatorTest.SubResource locator2() {
            return new ValidatorTest.SubResource();
        }
    }

    @Test
    public void testLocatorPathValidationFail() throws Exception {
        ValidatorTest.LOGGER.info("Should report error during validation as Resource cannot have ambiguous sub resource locators.");
        List<ResourceModelIssue> issues = testResourceValidation(ValidatorTest.TwoLocatorsResource.class);
        Assert.assertEquals(1, issues.size());
        Assert.assertEquals(FATAL, issues.get(0).getSeverity());
    }

    @Path("root")
    public static class ResourceRoot {
        // in path collision with ResourceSubRoot.get()
        @GET
        @Path("sub-root")
        public String get() {
            return "should never be called - fails during validation";
        }
    }

    @Path("root/sub-root")
    public static class ResourceSubPathRoot {
        @GET
        public String get() {
            return "should never be called - fails during validation";
        }
    }

    @Path("root")
    public static class ResourceRootNotUnique {
        // in path collision with ResourceSubRoot.get()
        @GET
        @Path("sub-root")
        public String get() {
            return "should never be called - fails during validation";
        }
    }

    @Path("root")
    public static class EmptyResource {
        public String get() {
            return "not a get method.";
        }
    }

    @Test
    public void testEmptyResourcel() throws Exception {
        ValidatorTest.LOGGER.info(("Should report warning during validation as Resource cannot have resource method and sub " + "resource locators on the same path."));
        List<ResourceModelIssue> issues = testResourceValidation(ValidatorTest.EmptyResource.class);
        Assert.assertEquals(1, issues.size());
        Assert.assertFalse(((issues.get(0).getSeverity()) == (Severity.FATAL)));
    }

    @Path("{abc}")
    public static class AmbiguousResource1 {
        @Path("x")
        @GET
        public String get() {
            return "get";
        }
    }

    @Path("{def}")
    public static class AmbiguousResource2 {
        @Path("x")
        @GET
        public String get() {
            return "get";
        }
    }

    @Path("unique")
    public static class UniqueResource {
        @Path("x")
        public String get() {
            return "get";
        }
    }

    @Test
    public void testAmbiguousResources() throws Exception {
        ValidatorTest.LOGGER.info(("Should report warning during validation error as resource path patterns are ambiguous ({abc} and {def} " + "results into same path pattern)."));
        List<ResourceModelIssue> issues = testResourceValidation(ValidatorTest.AmbiguousResource1.class, ValidatorTest.AmbiguousResource2.class, ValidatorTest.UniqueResource.class);
        Assert.assertEquals(1, issues.size());
        Assert.assertEquals(FATAL, issues.get(0).getSeverity());
    }

    @Path("{abc}")
    public static class AmbiguousLocatorResource1 {
        @Path("x")
        public ValidatorTest.SubResource locator() {
            return new ValidatorTest.SubResource();
        }
    }

    @Path("{def}")
    public static class AmbiguousLocatorResource2 {
        @Path("x")
        public ValidatorTest.SubResource locator2() {
            return new ValidatorTest.SubResource();
        }
    }

    @Test
    public void testAmbiguousResourceLocators() throws Exception {
        ValidatorTest.LOGGER.info(("Should report warning during validation error as resource path patterns are ambiguous ({abc} and {def} " + "results into same path pattern)."));
        List<ResourceModelIssue> issues = testResourceValidation(ValidatorTest.AmbiguousLocatorResource1.class, ValidatorTest.AmbiguousLocatorResource2.class);
        Assert.assertEquals(1, issues.size());
        Assert.assertEquals(FATAL, issues.get(0).getSeverity());
    }

    @Path("resource")
    public static class ResourceMethodWithVoidReturnType {
        @GET
        @Path("error")
        public void error() {
        }
    }

    @Test
    public void testVoidReturnType() throws Exception {
        ValidatorTest.LOGGER.info("Should report hint during validation as @GET resource method returns void.");
        List<ResourceModelIssue> issues = testResourceValidation(ValidatorTest.ResourceMethodWithVoidReturnType.class);
        Assert.assertEquals(1, issues.size());
        Assert.assertEquals(HINT, issues.get(0).getSeverity());
    }

    @Path("paramonsetter")
    public static class ResourceWithParamOnSetter {
        @QueryParam("id")
        public void setId(String id) {
        }

        @GET
        public String get() {
            return "get";
        }
    }

    @Test
    public void testParamOnSetterIsOk() {
        ValidatorTest.LOGGER.info("Validation should report no issues.");
        List<ResourceModelIssue> issues = testResourceValidation(ValidatorTest.ResourceWithParamOnSetter.class);
        Assert.assertEquals(0, issues.size());
    }

    @Path("paramonresourcepath")
    public static class ResourceWithParamOnResourcePathAnnotatedMethod {
        @QueryParam("id")
        @Path("fail")
        public String query() {
            return "post";
        }
    }

    @Test
    public void testParamOnResourcePathAnnotatedMethodFails() {
        ValidatorTest.LOGGER.info("Should report fatal during validation as @Path method should not be annotated with parameter annotation");
        List<ResourceModelIssue> issues = testResourceValidation(ValidatorTest.ResourceWithParamOnResourcePathAnnotatedMethod.class);
        Assert.assertEquals(1, issues.size());
        Assert.assertEquals(FATAL, issues.get(0).getSeverity());
    }

    @Path("paramonresourceget")
    public static class ResourceGETMethodFails {
        @QueryParam("id")
        @GET
        public String get(@PathParam("abc")
        String id) {
            return "get";
        }
    }

    @Test
    public void testParamOnResourceGETMethodFails() {
        ValidatorTest.LOGGER.info("Should report fatal during validation as @GET method should not be annotated with parameter annotation");
        List<ResourceModelIssue> issues = testResourceValidation(ValidatorTest.ResourceGETMethodFails.class);
        Assert.assertEquals(1, issues.size());
        Assert.assertEquals(FATAL, issues.get(0).getSeverity());
    }

    /**
     * Test of disabled validation failing on errors.
     */
    @Path("test-disable-validation-fail-on-error")
    public static class TestDisableValidationFailOnErrorResource {
        @GET
        public String get() {
            return "PASSED";
        }
    }

    @Test
    public void testDisableFailOnErrors() throws InterruptedException, ExecutionException {
        final ResourceConfig rc = // we should still be able to invoke a GET on this one.
        new ResourceConfig(ValidatorTest.AmbiguousLocatorResource1.class, ValidatorTest.AmbiguousLocatorResource2.class, ValidatorTest.AmbiguousParameterResource.class, ValidatorTest.AmbiguousResource1.class, ValidatorTest.AmbiguousResource2.class, ValidatorTest.ConcreteGenericArrayResource.class, ValidatorTest.ConcreteParameterizedTypeResource.class, ValidatorTest.EmptyResource.class, ValidatorTest.GenericArrayResource.class, ValidatorTest.MethodAndLocatorResource.class, ValidatorTest.MethodAndLocatorResource2.class, ValidatorTest.MyBeanParam.class, ValidatorTest.ParameterizedTypeResource.class, ValidatorTest.PercentEncodedCaseSensitiveTest.class, ValidatorTest.PercentEncodedTest.class, ValidatorTest.ResourceAsProvider.class, ValidatorTest.ResourceGETMethodFails.class, ValidatorTest.ResourceMethodWithVoidReturnType.class, ValidatorTest.ResourceRoot.class, ValidatorTest.ResourceRootNotUnique.class, ValidatorTest.ResourceSubPathRoot.class, ValidatorTest.ResourceWithMultipleScopes.class, ValidatorTest.ResourceWithParamOnResourcePathAnnotatedMethod.class, ValidatorTest.TestAmbiguousParams.class, ValidatorTest.TestAsyncGetRMReturningVoid.class, ValidatorTest.TestEmptyPathSegment.class, ValidatorTest.TestEntityParamOnSRL.class, ValidatorTest.TestGetRMConsumingEntity.class, ValidatorTest.TestGetRMConsumingFormParam.class, ValidatorTest.TestGetRMReturningVoid.class, ValidatorTest.TestGetSRMConsumingEntity.class, ValidatorTest.TestGetSRMConsumingFormParam.class, ValidatorTest.TestGetSRMReturningVoid.class, ValidatorTest.TestMoreThanOneEntity.class, ValidatorTest.TestMultipleHttpMethodDesignatorsRM.class, ValidatorTest.TestMultipleHttpMethodDesignatorsSRM.class, ValidatorTest.TestNonConflictingHttpMethodDelete.class, ValidatorTest.TestNonPublicRM.class, ValidatorTest.TestRelaxedProducesConsumesParserRules.class, ValidatorTest.TestRootResourceNonAmbigCtors.class, ValidatorTest.TestSRLReturningVoid.class, ValidatorTest.TwoLocatorsResource.class, ValidatorTest.TypeVariableResource.class, ValidatorTest.UniqueResource.class, ValidatorTest.TestDisableValidationFailOnErrorResource.class);
        rc.property(RESOURCE_VALIDATION_IGNORE_ERRORS, true);
        ApplicationHandler ah = new ApplicationHandler(rc);
        final ContainerRequest request = RequestContextBuilder.from("/test-disable-validation-fail-on-error", "GET").build();
        ContainerResponse response = ah.apply(request).get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("PASSED", response.getEntity());
    }
}

