/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.client;


import java.io.IOException;
import java.net.URI;
import javax.ws.rs.core.GenericType;
import org.glassfish.jersey.internal.MapPropertiesDelegate;
import org.glassfish.jersey.internal.util.ExceptionUtils;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * {@code ClientRequest} unit tests.
 *
 * @author Marek Potociar (marek.potociar at oracle.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class ClientRequestTest {
    @Mock
    private MessageBodyWorkers workers;

    @Mock
    private GenericType<?> entityType;

    /**
     * Test of resolving properties in the client request.
     */
    @Test
    public void testResolveProperty() {
        JerseyClient client;
        ClientRequest request;
        // test property in neither config nor request
        client = new JerseyClientBuilder().build();
        request = new ClientRequest(URI.create("http://example.org"), client.getConfiguration(), new MapPropertiesDelegate());
        Assert.assertFalse(request.getConfiguration().getPropertyNames().contains("name"));
        Assert.assertFalse(request.getPropertyNames().contains("name"));
        Assert.assertNull(request.getConfiguration().getProperty("name"));
        Assert.assertNull(request.getProperty("name"));
        Assert.assertNull(request.resolveProperty("name", String.class));
        Assert.assertEquals("value-default", request.resolveProperty("name", "value-default"));
        // test property in config only
        client = new JerseyClientBuilder().property("name", "value-global").build();
        request = new ClientRequest(URI.create("http://example.org"), client.getConfiguration(), new MapPropertiesDelegate());
        Assert.assertTrue(request.getConfiguration().getPropertyNames().contains("name"));
        Assert.assertFalse(request.getPropertyNames().contains("name"));
        Assert.assertEquals("value-global", request.getConfiguration().getProperty("name"));
        Assert.assertNull(request.getProperty("name"));
        Assert.assertEquals("value-global", request.resolveProperty("name", String.class));
        Assert.assertEquals("value-global", request.resolveProperty("name", "value-default"));
        // test property in request only
        client = new JerseyClientBuilder().build();
        request = new ClientRequest(URI.create("http://example.org"), client.getConfiguration(), new MapPropertiesDelegate());
        request.setProperty("name", "value-request");
        Assert.assertFalse(request.getConfiguration().getPropertyNames().contains("name"));
        Assert.assertTrue(request.getPropertyNames().contains("name"));
        Assert.assertNull(request.getConfiguration().getProperty("name"));
        Assert.assertEquals("value-request", request.getProperty("name"));
        Assert.assertEquals("value-request", request.resolveProperty("name", String.class));
        Assert.assertEquals("value-request", request.resolveProperty("name", "value-default"));
        // test property in config and request
        client = new JerseyClientBuilder().property("name", "value-global").build();
        request = new ClientRequest(URI.create("http://example.org"), client.getConfiguration(), new MapPropertiesDelegate());
        request.setProperty("name", "value-request");
        Assert.assertTrue(request.getConfiguration().getPropertyNames().contains("name"));
        Assert.assertTrue(request.getPropertyNames().contains("name"));
        Assert.assertEquals("value-global", request.getConfiguration().getProperty("name"));
        Assert.assertEquals("value-request", request.getProperty("name"));
        Assert.assertEquals("value-request", request.resolveProperty("name", String.class));
        Assert.assertEquals("value-request", request.resolveProperty("name", "value-default"));
    }

    @Test
    public void testSSLExceptionHandling() throws Exception {
        final IOException ioException = new IOException("Test");
        final ClientRequest request = mockThrowing(ioException);
        try {
            request.doWriteEntity(workers, entityType);
            Assert.fail("An IOException exception should be thrown.");
        } catch (IOException e) {
            Assert.assertThat(("Detected a un-expected exception! \n" + (ExceptionUtils.exceptionStackTraceAsString(e))), e, Is.is(ioException));
        }
    }

    @Test
    public void testRuntimeExceptionBeingReThrown() throws Exception {
        final RuntimeException runtimeException = new RuntimeException("Test");
        ClientRequest request = mockThrowing(runtimeException);
        try {
            request.doWriteEntity(workers, entityType);
            Assert.fail("A RuntimeException exception should be thrown.");
        } catch (RuntimeException e) {
            Assert.assertThat(("Detected a un-expected exception! \n" + (ExceptionUtils.exceptionStackTraceAsString(e))), e, Is.is(runtimeException));
        }
    }
}

