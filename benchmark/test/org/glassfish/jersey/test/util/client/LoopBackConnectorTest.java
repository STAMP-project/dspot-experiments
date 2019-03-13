/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2015-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.test.util.client;


import LoopBackConnector.TEST_LOOPBACK_CODE;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Basic {@link org.glassfish.jersey.test.util.client.LoopBackConnector} unit tests.
 *
 * @author Michal Gajdos
 */
public class LoopBackConnectorTest {
    private Client client;

    @Test
    public void testHeadersAndStatus() throws Exception {
        final Response response = client.target("baz").request().header("foo", "bar").header("bar", "foo").get();
        Assert.assertThat("Unexpected HTTP response status", response.getStatus(), CoreMatchers.is(TEST_LOOPBACK_CODE));
        Assert.assertThat("Invalid value of header 'foo'", response.getHeaderString("foo"), CoreMatchers.is("bar"));
        Assert.assertThat("Invalid value of header 'bar'", response.getHeaderString("bar"), CoreMatchers.is("foo"));
    }

    @Test
    public void testEntity() throws Exception {
        final Response response = client.target("baz").request().post(Entity.text("foo"));
        Assert.assertThat("Invalid entity received", response.readEntity(String.class), CoreMatchers.is("foo"));
    }

    @Test
    public void testEntityMediaType() throws Exception {
        final Response response = client.target("baz").request().post(Entity.entity("foo", "foo/bar"));
        Assert.assertThat("Invalid entity received", response.readEntity(String.class), CoreMatchers.is("foo"));
        Assert.assertThat("Invalid content-type received", response.getMediaType(), CoreMatchers.is(new MediaType("foo", "bar")));
    }

    @Test(expected = IllegalStateException.class)
    public void testClose() throws Exception {
        client.close();
        client.target("baz").request().get();
    }

    @Test
    public void testAsync() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> throwable = new AtomicReference<>();
        client.target("baz").request().async().get(new javax.ws.rs.client.InvocationCallback<Response>() {
            @Override
            public void completed(final Response response) {
                latch.countDown();
            }

            @Override
            public void failed(final Throwable t) {
                throwable.set(t);
                latch.countDown();
            }
        });
        latch.await();
        Assert.assertThat("Async request failed", throwable.get(), CoreMatchers.nullValue());
    }

    @Test(expected = ProcessingException.class)
    public void testInvalidEntity() throws Exception {
        client.target("baz").request().post(Entity.json(Arrays.asList("foo", "bar")));
    }
}

