/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.examples.linking;


import MediaType.APPLICATION_XML_TYPE;
import Response.StatusType;
import java.util.List;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Naresh (Srinivas.Bhimisetty@Sun.Com)
 * @author Gerard Davison (gerard.davison at oracle.com)
 */
public class LinkWebAppTest extends JerseyTest {
    /**
     * Test that the expected response is sent back.
     */
    @Test
    public void testLinks() throws Exception {
        final Response response = target().path("items").queryParam("offset", 10).queryParam("limit", "10").request(APPLICATION_XML_TYPE).get(Response.class);
        final Response.StatusType statusInfo = response.getStatusInfo();
        Assert.assertEquals("Should have succeeded", 200, statusInfo.getStatusCode());
        final String content = response.readEntity(String.class);
        final List<Object> linkHeaders = response.getHeaders().get("Link");
        Assert.assertEquals("Should have two link headers", 2, linkHeaders.size());
        MatcherAssert.assertThat("Content should contain next link", content, CoreMatchers.containsString((("http://localhost:" + (getPort())) + "/items?offset=20&amp;limit=10")));
    }
}

