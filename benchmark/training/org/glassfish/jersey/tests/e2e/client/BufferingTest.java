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
package org.glassfish.jersey.tests.e2e.client;


import RequestEntityProcessing.BUFFERED;
import RequestEntityProcessing.CHUNKED;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;


/**
 * Tests chunk encoding and possibility of buffering the entity.
 *
 * @author Miroslav Fuksa
 */
public class BufferingTest extends JerseyTest {
    @Path("resource")
    public static class MyResource {
        @POST
        public String getBuffered(@HeaderParam("content-length")
        String contentLenght, @HeaderParam("transfer-encoding")
        String transferEncoding) {
            if ((transferEncoding != null) && (transferEncoding.equals("chunked"))) {
                return "chunked";
            }
            return contentLenght;
        }
    }

    @Test
    public void testApacheConnector() {
        testWithBuffering(getApacheConnectorConfig());
        testWithChunkEncodingWithoutPropertyDefinition(getApacheConnectorConfig());
        testWithChunkEncodingWithPropertyDefinition(getApacheConnectorConfig());
        testWithChunkEncodingPerRequest(getApacheConnectorConfig());
        testDefaultOption(getApacheConnectorConfig(), CHUNKED);
    }

    @Test
    public void testGrizzlyConnector() {
        testWithBuffering(getGrizzlyConnectorConfig());
        testWithChunkEncodingWithoutPropertyDefinition(getGrizzlyConnectorConfig());
        testWithChunkEncodingWithPropertyDefinition(getGrizzlyConnectorConfig());
        testWithChunkEncodingPerRequest(getGrizzlyConnectorConfig());
        testDefaultOption(getGrizzlyConnectorConfig(), CHUNKED);
    }

    @Test
    public void testHttpUrlConnector() {
        testWithBuffering(getHttpUrlConnectorConfig());
        testWithChunkEncodingWithoutPropertyDefinition(getHttpUrlConnectorConfig());
        testWithChunkEncodingWithPropertyDefinition(getHttpUrlConnectorConfig());
        testWithChunkEncodingPerRequest(getHttpUrlConnectorConfig());
        testDefaultOption(getHttpUrlConnectorConfig(), BUFFERED);
    }
}

