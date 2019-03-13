/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc. and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.integration.management.deploy.runtime;


import ModelDescriptionConstants.INCLUDE_RUNTIME;
import ModelDescriptionConstants.RECURSIVE;
import java.net.URL;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.test.integration.management.deploy.runtime.jaxrs.HelloResource;
import org.jboss.as.test.integration.management.deploy.runtime.jaxrs.PureProxyApiService;
import org.jboss.as.test.integration.management.deploy.runtime.jaxrs.PureProxyEndPoint;
import org.jboss.as.test.integration.management.deploy.runtime.jaxrs.SubHelloResource;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
@RunAsClient
public class JaxrsRuntimeNameTestCase extends AbstractRuntimeTestCase {
    private static final String CONSUMES = "consumes";

    private static final String DEPLOYMENT_NAME = "hello-rs.war";

    private static final String JAVA_METHOD = "java-method";

    private static final String PRODUCES = "produces";

    private static final String RESOURCE_CLASS = "resource-class";

    private static final String RESOURCE_METHODS = "resource-methods";

    private static final String RESOURCE_PATH = "resource-path";

    private static final String RESOURCE_PATHS = "rest-resource-paths";

    private static final String REST_RESOURCE_NAME = "rest-resource";

    private static final String SUB_RESOURCE_LOCATORS = "sub-resource-locators";

    private static final String SUBSYSTEM_NAME = "jaxrs";

    private static final ModelControllerClient controllerClient = TestSuiteEnvironment.getModelControllerClient();

    @ArquillianResource
    private URL url;

    @Test
    public void testSubResource() throws Exception {
        Assert.assertThat(performCall("hello"), CoreMatchers.is("Hello World!"));
    }

    @Test
    public void testStepByStep() throws Exception {
        PathAddress deploymentAddress = PathAddress.pathAddress(DEPLOYMENT, JaxrsRuntimeNameTestCase.DEPLOYMENT_NAME);
        ModelNode readResource = Util.createOperation(READ_RESOURCE_OPERATION, deploymentAddress);
        ModelNode result = JaxrsRuntimeNameTestCase.controllerClient.execute(readResource);
        Assert.assertThat(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result), CoreMatchers.is(true));
        PathAddress subsystemAddress = deploymentAddress.append(SUBSYSTEM, JaxrsRuntimeNameTestCase.SUBSYSTEM_NAME);
        readResource = Util.createOperation(READ_RESOURCE_OPERATION, subsystemAddress);
        result = JaxrsRuntimeNameTestCase.controllerClient.execute(readResource);
        Assert.assertThat(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result), CoreMatchers.is(true));
        readResource = Util.createOperation("show-resources", subsystemAddress);
        result = JaxrsRuntimeNameTestCase.controllerClient.execute(readResource);
        Assert.assertThat(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result), CoreMatchers.is(true));
        List<ModelNode> jaxrsResources = Operations.readResult(result).asList();
        Assert.assertThat(jaxrsResources, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(jaxrsResources.size(), CoreMatchers.is(5));
        int count = 0;
        for (ModelNode jaxrsResource : jaxrsResources) {
            if (jaxrsResource.get(JaxrsRuntimeNameTestCase.RESOURCE_CLASS).asString().equals(HelloResource.class.getName())) {
                count++;
                String path = jaxrsResource.get(JaxrsRuntimeNameTestCase.RESOURCE_PATH).asString();
                switch (path) {
                    case "/update" :
                        Assert.assertThat(jaxrsResource.toString(), jaxrsResource.get(JaxrsRuntimeNameTestCase.RESOURCE_METHODS).asList().get(0).asString(), CoreMatchers.is("PUT /hello-rs/hello/update - org.jboss.as.test.integration.management.deploy.runtime.jaxrs.HelloResource.updateMessage(...)"));
                        break;
                    case "/json" :
                        Assert.assertThat(jaxrsResource.toString(), jaxrsResource.get(JaxrsRuntimeNameTestCase.RESOURCE_METHODS).asList().get(0).asString(), CoreMatchers.is("GET /hello-rs/hello/json - org.jboss.as.test.integration.management.deploy.runtime.jaxrs.HelloResource.getHelloWorldJSON()"));
                        break;
                    case "/xml" :
                        Assert.assertThat(jaxrsResource.toString(), jaxrsResource.get(JaxrsRuntimeNameTestCase.RESOURCE_METHODS).asList().get(0).asString(), CoreMatchers.is("GET /hello-rs/hello/xml - org.jboss.as.test.integration.management.deploy.runtime.jaxrs.HelloResource.getHelloWorldXML()"));
                        break;
                    case "/" :
                        Assert.assertThat(jaxrsResource.toString(), jaxrsResource.get(JaxrsRuntimeNameTestCase.RESOURCE_METHODS).asList().get(0).asString(), CoreMatchers.is("GET /hello-rs/hello/ - org.jboss.as.test.integration.management.deploy.runtime.jaxrs.HelloResource.getHelloWorld()"));
                        break;
                    default :
                        Assert.assertThat(jaxrsResource.toString(), false, CoreMatchers.is(true));
                }
            } else
                if (jaxrsResource.get(JaxrsRuntimeNameTestCase.RESOURCE_CLASS).asString().equals(PureProxyApiService.class.getName())) {
                    count++;
                    String path = jaxrsResource.get(JaxrsRuntimeNameTestCase.RESOURCE_PATH).asString();
                    Assert.assertThat(jaxrsResource.toString(), path, CoreMatchers.is("pure/proxy/test/{a}/{b}"));
                    Assert.assertThat(jaxrsResource.toString(), jaxrsResource.get(JaxrsRuntimeNameTestCase.RESOURCE_METHODS).asList().get(0).asString(), CoreMatchers.is("GET /hello-rs/hello/pure/proxy/test/{a}/{b} - org.jboss.as.test.integration.management.deploy.runtime.jaxrs.PureProxyApiService.test(...)"));
                }

        }
        Assert.assertThat(count, CoreMatchers.is(5));
    }

    @Test
    public void testReadRestResource() throws Exception {
        ModelNode removeResource = Util.createRemoveOperation(PathAddress.pathAddress(DEPLOYMENT, JaxrsRuntimeNameTestCase.DEPLOYMENT_NAME).append(SUBSYSTEM, JaxrsRuntimeNameTestCase.SUBSYSTEM_NAME));
        Assert.assertThat(Operations.getFailureDescription(JaxrsRuntimeNameTestCase.controllerClient.execute(removeResource)).asString(), CoreMatchers.containsString("WFLYCTL0031"));
        ModelNode readResource = Util.createOperation(READ_RESOURCE_OPERATION, PathAddress.pathAddress(DEPLOYMENT, JaxrsRuntimeNameTestCase.DEPLOYMENT_NAME).append(SUBSYSTEM, JaxrsRuntimeNameTestCase.SUBSYSTEM_NAME).append(JaxrsRuntimeNameTestCase.REST_RESOURCE_NAME, HelloResource.class.getCanonicalName()));
        readResource.get(INCLUDE_RUNTIME).set(true);
        ModelNode result = JaxrsRuntimeNameTestCase.controllerClient.execute(readResource);
        Assert.assertThat(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result), CoreMatchers.is(true));
        ModelNode res = Operations.readResult(result);
        Assert.assertThat(res.isDefined(), CoreMatchers.is(true));
        Assert.assertThat(res.get(JaxrsRuntimeNameTestCase.RESOURCE_CLASS).asString(), CoreMatchers.is(HelloResource.class.getCanonicalName()));
        List<ModelNode> subResList = res.get(JaxrsRuntimeNameTestCase.RESOURCE_PATHS).asList();
        Assert.assertThat(subResList.size(), CoreMatchers.is(4));
        ModelNode rootRes = subResList.get(0);// '/'

        Assert.assertThat(rootRes.get(JaxrsRuntimeNameTestCase.RESOURCE_PATH).asString(), CoreMatchers.is("/"));
        Assert.assertThat(rootRes.get(JaxrsRuntimeNameTestCase.JAVA_METHOD).asString(), CoreMatchers.is((("java.lang.String " + (HelloResource.class.getCanonicalName())) + ".getHelloWorld()")));
        Assert.assertThat(rootRes.get(JaxrsRuntimeNameTestCase.CONSUMES).isDefined(), CoreMatchers.is(false));
        Assert.assertThat(rootRes.get(JaxrsRuntimeNameTestCase.PRODUCES).asList().size(), CoreMatchers.is(1));
        Assert.assertThat(rootRes.get(JaxrsRuntimeNameTestCase.PRODUCES).asList().get(0).asString(), CoreMatchers.is("text/plain"));
        Assert.assertThat(rootRes.get(JaxrsRuntimeNameTestCase.RESOURCE_METHODS).asList().size(), CoreMatchers.is(1));
        Assert.assertThat(rootRes.get(JaxrsRuntimeNameTestCase.RESOURCE_METHODS).asList().get(0).asString(), CoreMatchers.is("GET /hello-rs/hello/"));
        ModelNode jsonRes = subResList.get(1);// '/json'

        Assert.assertThat(jsonRes.get(JaxrsRuntimeNameTestCase.RESOURCE_PATH).asString(), CoreMatchers.is("/json"));
        Assert.assertThat(jsonRes.get(JaxrsRuntimeNameTestCase.JAVA_METHOD).asString(), CoreMatchers.is((("javax.json.JsonObject " + (HelloResource.class.getCanonicalName())) + ".getHelloWorldJSON()")));
        Assert.assertThat(jsonRes.get(JaxrsRuntimeNameTestCase.CONSUMES).isDefined(), CoreMatchers.is(false));
        Assert.assertThat(jsonRes.get(JaxrsRuntimeNameTestCase.PRODUCES).asList().size(), CoreMatchers.is(1));
        Assert.assertThat(jsonRes.get(JaxrsRuntimeNameTestCase.PRODUCES).asList().get(0).asString(), CoreMatchers.is("application/json"));
        Assert.assertThat(jsonRes.get(JaxrsRuntimeNameTestCase.RESOURCE_METHODS).asList().size(), CoreMatchers.is(1));
        Assert.assertThat(jsonRes.get(JaxrsRuntimeNameTestCase.RESOURCE_METHODS).asList().get(0).asString(), CoreMatchers.is("GET /hello-rs/hello/json"));
        ModelNode updateRes = subResList.get(2);// '/update'

        Assert.assertThat(updateRes.get(JaxrsRuntimeNameTestCase.RESOURCE_PATH).asString(), CoreMatchers.is("/update"));
        Assert.assertThat(updateRes.get(JaxrsRuntimeNameTestCase.JAVA_METHOD).asString(), CoreMatchers.is((("void " + (HelloResource.class.getCanonicalName())) + ".updateMessage(@QueryParam java.lang.String content = 'Hello')")));
        Assert.assertThat(updateRes.get(JaxrsRuntimeNameTestCase.PRODUCES).isDefined(), CoreMatchers.is(false));
        Assert.assertThat(updateRes.get(JaxrsRuntimeNameTestCase.CONSUMES).asList().size(), CoreMatchers.is(1));
        Assert.assertThat(updateRes.get(JaxrsRuntimeNameTestCase.CONSUMES).asList().get(0).asString(), CoreMatchers.is("text/plain"));
        Assert.assertThat(updateRes.get(JaxrsRuntimeNameTestCase.RESOURCE_METHODS).asList().size(), CoreMatchers.is(1));
        Assert.assertThat(updateRes.get(JaxrsRuntimeNameTestCase.RESOURCE_METHODS).asList().get(0).asString(), CoreMatchers.is("PUT /hello-rs/hello/update"));
        ModelNode xmlRes = subResList.get(3);// '/xml'

        Assert.assertThat(xmlRes.get(JaxrsRuntimeNameTestCase.RESOURCE_PATH).asString(), CoreMatchers.is("/xml"));
        Assert.assertThat(xmlRes.get(JaxrsRuntimeNameTestCase.JAVA_METHOD).asString(), CoreMatchers.is((("java.lang.String " + (HelloResource.class.getCanonicalName())) + ".getHelloWorldXML()")));
        Assert.assertThat(xmlRes.get(JaxrsRuntimeNameTestCase.CONSUMES).isDefined(), CoreMatchers.is(false));
        Assert.assertThat(xmlRes.get(JaxrsRuntimeNameTestCase.PRODUCES).asList().size(), CoreMatchers.is(1));
        Assert.assertThat(xmlRes.get(JaxrsRuntimeNameTestCase.PRODUCES).asList().get(0).asString(), CoreMatchers.is("application/xml"));
        Assert.assertThat(xmlRes.get(JaxrsRuntimeNameTestCase.RESOURCE_METHODS).asList().size(), CoreMatchers.is(1));
        Assert.assertThat(xmlRes.get(JaxrsRuntimeNameTestCase.RESOURCE_METHODS).asList().get(0).asString(), CoreMatchers.is("GET /hello-rs/hello/xml"));
        List<ModelNode> subLocatorList = res.get(JaxrsRuntimeNameTestCase.SUB_RESOURCE_LOCATORS).asList();
        Assert.assertThat(subLocatorList.size(), CoreMatchers.is(1));
        ModelNode subLocatorRes = subLocatorList.get(0);
        Assert.assertThat(subLocatorRes.get(JaxrsRuntimeNameTestCase.RESOURCE_CLASS).asString(), CoreMatchers.is(SubHelloResource.class.getCanonicalName()));
        List<ModelNode> subResInsideSubLocator = subLocatorRes.get(JaxrsRuntimeNameTestCase.RESOURCE_PATHS).asList();
        Assert.assertThat(subResInsideSubLocator.size(), CoreMatchers.is(2));
        ModelNode subRootHi = subResInsideSubLocator.get(0);
        Assert.assertThat(subRootHi.get(JaxrsRuntimeNameTestCase.RESOURCE_PATH).asString(), CoreMatchers.is("/sub/"));
        Assert.assertThat(subRootHi.get(JaxrsRuntimeNameTestCase.JAVA_METHOD).asString(), CoreMatchers.is((("java.lang.String " + (SubHelloResource.class.getCanonicalName())) + ".hi()")));
        Assert.assertThat(subRootHi.get(JaxrsRuntimeNameTestCase.CONSUMES).isDefined(), CoreMatchers.is(false));
        Assert.assertThat(subRootHi.get(JaxrsRuntimeNameTestCase.PRODUCES).asList().size(), CoreMatchers.is(1));
        Assert.assertThat(subRootHi.get(JaxrsRuntimeNameTestCase.PRODUCES).asList().get(0).asString(), CoreMatchers.is("text/plain"));
        Assert.assertThat(subRootHi.get(JaxrsRuntimeNameTestCase.RESOURCE_METHODS).asList().size(), CoreMatchers.is(1));
        Assert.assertThat(subRootHi.get(JaxrsRuntimeNameTestCase.RESOURCE_METHODS).asList().get(0).asString(), CoreMatchers.is("GET /hello-rs/hello/sub/"));
        ModelNode pingNode = subResInsideSubLocator.get(1);
        Assert.assertThat(pingNode.get(JaxrsRuntimeNameTestCase.RESOURCE_PATH).asString(), CoreMatchers.is("/sub/ping/{name}"));
        Assert.assertThat(pingNode.get(JaxrsRuntimeNameTestCase.JAVA_METHOD).asString(), CoreMatchers.is((("java.lang.String " + (SubHelloResource.class.getCanonicalName())) + ".ping(@PathParam java.lang.String name = 'JBoss')")));
        Assert.assertThat(pingNode.get(JaxrsRuntimeNameTestCase.CONSUMES).isDefined(), CoreMatchers.is(false));
        Assert.assertThat(pingNode.get(JaxrsRuntimeNameTestCase.PRODUCES).asList().size(), CoreMatchers.is(1));
        Assert.assertThat(pingNode.get(JaxrsRuntimeNameTestCase.PRODUCES).asList().get(0).asString(), CoreMatchers.is("text/plain"));
        Assert.assertThat(pingNode.get(JaxrsRuntimeNameTestCase.RESOURCE_METHODS).asList().size(), CoreMatchers.is(1));
        Assert.assertThat(pingNode.get(JaxrsRuntimeNameTestCase.RESOURCE_METHODS).asList().get(0).asString(), CoreMatchers.is("GET /hello-rs/hello/sub/ping/{name}"));
    }

    @Test
    public void testReadRestEndPointIntf() throws Exception {
        Assert.assertThat(performCall("hello/pure/proxy/test/Hello/World"), CoreMatchers.is("Hello World"));
        ModelNode readResource = Util.createOperation(READ_RESOURCE_OPERATION, PathAddress.pathAddress(DEPLOYMENT, JaxrsRuntimeNameTestCase.DEPLOYMENT_NAME).append(SUBSYSTEM, JaxrsRuntimeNameTestCase.SUBSYSTEM_NAME).append(JaxrsRuntimeNameTestCase.REST_RESOURCE_NAME, PureProxyEndPoint.class.getCanonicalName()));
        readResource.get(INCLUDE_RUNTIME).set(true);
        ModelNode result = JaxrsRuntimeNameTestCase.controllerClient.execute(readResource);
        Assert.assertThat(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result), CoreMatchers.is(true));
        ModelNode res = Operations.readResult(result);
        Assert.assertThat(res.isDefined(), CoreMatchers.is(true));
        Assert.assertThat(res.get(JaxrsRuntimeNameTestCase.RESOURCE_CLASS).asString(), CoreMatchers.is(PureProxyEndPoint.class.getCanonicalName()));
        List<ModelNode> subResList = res.get(JaxrsRuntimeNameTestCase.RESOURCE_PATHS).asList();
        Assert.assertThat(subResList.size(), CoreMatchers.is(1));
        ModelNode rootRes = subResList.get(0);
        Assert.assertThat(rootRes.get(JaxrsRuntimeNameTestCase.RESOURCE_PATH).asString(), CoreMatchers.is("pure/proxy/test/{a}/{b}"));
        Assert.assertThat(rootRes.get(JaxrsRuntimeNameTestCase.JAVA_METHOD).asString(), CoreMatchers.is((("java.lang.String " + (PureProxyEndPoint.class.getCanonicalName())) + ".test(@PathParam java.lang.String a, @PathParam java.lang.String b)")));
        Assert.assertThat(rootRes.get(JaxrsRuntimeNameTestCase.CONSUMES).isDefined(), CoreMatchers.is(false));
        Assert.assertThat(rootRes.get(JaxrsRuntimeNameTestCase.PRODUCES).isDefined(), CoreMatchers.is(false));
        Assert.assertThat(rootRes.get(JaxrsRuntimeNameTestCase.RESOURCE_METHODS).asList().size(), CoreMatchers.is(1));
        Assert.assertThat(rootRes.get(JaxrsRuntimeNameTestCase.RESOURCE_METHODS).asList().get(0).asString(), CoreMatchers.is("GET /hello-rs/hello/pure/proxy/test/{a}/{b}"));
    }

    @Test
    public void testRecursive() throws Exception {
        ModelNode readResource = Util.createOperation(READ_RESOURCE_OPERATION, PathAddress.pathAddress(DEPLOYMENT, JaxrsRuntimeNameTestCase.DEPLOYMENT_NAME));
        readResource.get(INCLUDE_RUNTIME).set(true);
        readResource.get(RECURSIVE).set(true);
        ModelNode result = JaxrsRuntimeNameTestCase.controllerClient.execute(readResource);
        Assert.assertThat(("Failed to list resources: " + result), Operations.isSuccessfulOutcome(result), CoreMatchers.is(true));
    }
}

