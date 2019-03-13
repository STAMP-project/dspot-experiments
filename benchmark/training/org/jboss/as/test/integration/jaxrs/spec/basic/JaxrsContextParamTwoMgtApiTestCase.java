/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.as.test.integration.jaxrs.spec.basic;


import ModelDescriptionConstants.INCLUDE_RUNTIME;
import java.io.IOException;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.test.integration.jaxrs.spec.basic.resource.JaxrsAppResource;
import org.jboss.as.test.integration.management.base.ContainerResourceMgmtTestBase;
import org.jboss.as.test.integration.management.util.MgmtOperationException;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 */
@RunWith(Arquillian.class)
@RunAsClient
public class JaxrsContextParamTwoMgtApiTestCase extends ContainerResourceMgmtTestBase {
    /**
     * When web.xml is present in the archive and the Application subclass is
     * declared only in the context-param element.  When the subclass does not
     * provide a list of classes to use in the getClasses or getSingletons method,
     * then the RESTEasy Configuration Switch must be declared in a context-param.
     * confirm resource class is registered in the (CLI) management model
     * Corresponding CLI cmd:
     * ./jboss-cli.sh -c --command="/deployment=JaxrsContextParamTwoMgtApiTestCase.war/subsystem=jaxrs:read-resource(include-runtime=true,recursive=true)"
     */
    @Test
    public void testContextParam() throws IOException, MgmtOperationException {
        ModelNode op = Util.createOperation(READ_RESOURCE_OPERATION, PathAddress.pathAddress(DEPLOYMENT, ((JaxrsContextParamTwoMgtApiTestCase.class.getSimpleName()) + ".war")).append(SUBSYSTEM, "jaxrs").append("rest-resource", JaxrsAppResource.class.getCanonicalName()));
        op.get(INCLUDE_RUNTIME).set(true);
        ModelNode result = executeOperation(op);
        Assert.assertFalse("Subsystem is empty.", ((result.keys().size()) == 0));
        ModelNode resClass = result.get("resource-class");
        Assert.assertNotNull("No resource-class present.", resClass);
        Assert.assertTrue("Expected resource-class not found.", resClass.toString().contains(JaxrsAppResource.class.getSimpleName()));
    }
}

