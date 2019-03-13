/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2014-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.tests.cdi.resources;


import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Part of JERSEY-2526 reproducer. Without the fix, the application would
 * not deploy at all. This is just to make sure the JAX-RS parameter producer
 * keeps working as expected without regressions.
 *
 * @author Jakub Podlesak (jakub.podlesak at oralc.com)
 */
public class ConstructorInjectionTest extends CdiTest {
    @Test
    public void testConstructorInjectedResource() {
        final WebTarget target = target().path("ctor-injected");
        final Response pathParamResponse = target.path("pathParam").request().get();
        Assert.assertThat(pathParamResponse.getStatus(), CoreMatchers.is(200));
        Assert.assertThat(pathParamResponse.readEntity(String.class), CoreMatchers.is("pathParam"));
        final Response queryParamResponse = target.path("queryParam").queryParam("q", "123").request().get();
        Assert.assertThat(queryParamResponse.getStatus(), CoreMatchers.is(200));
        Assert.assertThat(queryParamResponse.readEntity(String.class), CoreMatchers.is("123"));
        final Response matrixParamResponse = target.path("matrixParam").matrixParam("m", "456").request().get();
        Assert.assertThat(matrixParamResponse.getStatus(), CoreMatchers.is(200));
        Assert.assertThat(matrixParamResponse.readEntity(String.class), CoreMatchers.is("456"));
        final Response headerParamResponse = target.path("headerParam").request().header("Custom-Header", "789").get();
        Assert.assertThat(headerParamResponse.getStatus(), CoreMatchers.is(200));
        Assert.assertThat(headerParamResponse.readEntity(String.class), CoreMatchers.is("789"));
        final Response cdiParamResponse = target.path("cdiParam").request().get();
        Assert.assertThat(cdiParamResponse.getStatus(), CoreMatchers.is(200));
        Assert.assertThat(cdiParamResponse.readEntity(String.class), CoreMatchers.is("cdi-produced"));
    }
}

