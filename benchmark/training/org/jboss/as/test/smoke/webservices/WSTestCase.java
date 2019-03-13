/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.smoke.webservices;


import ModelDescriptionConstants.DEPLOYMENT;
import ModelDescriptionConstants.INCLUDE_RUNTIME;
import ModelDescriptionConstants.OP;
import ModelDescriptionConstants.OP_ADDR;
import ModelDescriptionConstants.READ_RESOURCE_OPERATION;
import ModelDescriptionConstants.RECURSIVE;
import ModelDescriptionConstants.SUBSYSTEM;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.common.HttpRequest;
import org.jboss.as.test.integration.domain.management.util.DomainTestSupport;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author <a href="alessio.soldano@jboss.com">Alessio Soldano</a>
 * @version $Revision: 1.1 $
 */
@RunWith(Arquillian.class)
@RunAsClient
public class WSTestCase {
    private static final ModelNode webserviceAddress;

    static {
        webserviceAddress = new ModelNode();
        WSTestCase.webserviceAddress.add("subsystem", "webservices");
    }

    @ContainerResource
    private ManagementClient managementClient;

    @ArquillianResource
    private URL url;

    @Test
    @InSequence(1)
    public void testWSDL() throws Exception {
        String wsdl = performCall("?wsdl");
        Assert.assertNotNull(wsdl);
        Assert.assertThat(wsdl, CoreMatchers.containsString("wsdl:definitions"));
    }

    @Test
    @InSequence(2)
    public void testManagementDescription() throws Exception {
        final ModelNode address = new ModelNode();
        address.add(DEPLOYMENT, "ws-example.war");
        address.add(SUBSYSTEM, "webservices");// EndpointService

        address.add("endpoint", "*");// get all endpoints

        final ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(OP_ADDR).set(address);
        operation.get(INCLUDE_RUNTIME).set(true);
        operation.get(RECURSIVE).set(true);
        final ModelNode result = managementClient.getControllerClient().execute(operation);
        List<ModelNode> endpoints = DomainTestSupport.validateResponse(result).asList();
        Assert.assertThat(((endpoints.size()) > 0), CoreMatchers.is(true));
        for (final ModelNode endpointResult : result.get("result").asList()) {
            final ModelNode endpoint = endpointResult.get("result");
            Assert.assertThat(endpoint.hasDefined("class"), CoreMatchers.is(true));
            Assert.assertThat(endpoint.hasDefined("name"), CoreMatchers.is(true));
            Assert.assertThat(endpoint.hasDefined("wsdl-url"), CoreMatchers.is(true));
            Assert.assertThat(endpoint.get("wsdl-url").asString().endsWith("?wsdl"), CoreMatchers.is(true));
            Assert.assertThat(endpoint.hasDefined("request-count"), CoreMatchers.is(true));
            Assert.assertThat(endpoint.get("request-count").asString(), CoreMatchers.is("0"));
        }
    }

    @Test
    @InSequence(3)
    public void testManagementDescriptionMetrics() throws Exception {
        setStatisticsEnabled(true);
        final ModelNode address = new ModelNode();
        address.add(DEPLOYMENT, "ws-example.war");
        address.add(SUBSYSTEM, "webservices");// EndpointService

        address.add("endpoint", "*");// get all endpoints

        final ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(OP_ADDR).set(address);
        operation.get(INCLUDE_RUNTIME).set(true);
        operation.get(RECURSIVE).set(true);
        ModelNode result = managementClient.getControllerClient().execute(operation);
        List<ModelNode> endpoints = DomainTestSupport.validateResponse(result).asList();
        Assert.assertThat(((endpoints.size()) > 0), CoreMatchers.is(true));
        for (final ModelNode endpointResult : result.get("result").asList()) {
            final ModelNode endpoint = endpointResult.get("result");
            // Get the wsdl again to be sure the endpoint has been hit at least once
            final URL wsdlUrl = new URL(endpoint.get("wsdl-url").asString());
            String wsdl = HttpRequest.get(wsdlUrl.toExternalForm(), 30, TimeUnit.SECONDS);
            Assert.assertThat(wsdl, CoreMatchers.is(CoreMatchers.notNullValue()));
            // Read metrics
            checkCountMetric(endpointResult, managementClient.getControllerClient(), "request-count");
            checkCountMetric(endpointResult, managementClient.getControllerClient(), "response-count");
        }
        setStatisticsEnabled(false);
        result = managementClient.getControllerClient().execute(operation);
        endpoints = DomainTestSupport.validateResponse(result).asList();
        for (final ModelNode endpointResult : endpoints) {
            final ModelNode endpoint = endpointResult.get("result");
            Assert.assertThat(endpoint.hasDefined("request-count"), CoreMatchers.is(true));
            Assert.assertThat(endpoint.get("request-count").asString(), CoreMatchers.is("1"));
        }
    }

    @Test
    @InSequence(4)
    public void testAccess() throws Exception {
        URL wsdlURL = new URL(((this.url.toExternalForm()) + "ws-example?wsdl"));
        QName serviceName = new QName("http://webservices.smoke.test.as.jboss.org/", "EndpointService");
        Service service = Service.create(wsdlURL, serviceName);
        Endpoint port = ((Endpoint) (service.getPort(Endpoint.class)));
        String echo = port.echo("Foo");
        Assert.assertThat(("Echoing Foo should return Foo not " + echo), echo, CoreMatchers.is("Foo"));
    }
}

