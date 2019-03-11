/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2018, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.microprofile.opentracing;


import java.net.URL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.shared.ServerReload;
import org.junit.Test;


/**
 * A parent for EAR-based OpenTracing tests
 *
 * @author <a href="mailto:manovotn@redhat.com">Matej Novotny</a>
 */
public abstract class AbstractEarOpenTracingTestCase {
    @ContainerResource
    ManagementClient managementClient;

    @ArquillianResource
    private URL url;

    @Test
    public void testEarServicesUseDifferentTracers() throws Exception {
        testHttpInvokation();
    }

    @Test
    public void testEarServicesUseDifferentTracersAfterReload() throws Exception {
        // TODO the tracer instance is same after reload as before it - check whether this is correct or no
        testHttpInvokation();
        ServerReload.executeReloadAndWaitForCompletion(managementClient.getControllerClient());
        testHttpInvokation();
    }
}

