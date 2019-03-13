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
package org.glassfish.jersey.tests.integration.jersey2255;


import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.tests.integration.jersey2255.Issue2255Resource.A;
import org.glassfish.jersey.tests.integration.jersey2255.Issue2255Resource.B;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


/**
 * Reproducer tests for JERSEY-2255.
 *
 * @author Eric Miles (emilesvt at gmail.com)
 */
public class Jersey2255ITCase extends JerseyTest {
    /**
     * Server side response is wrapped, needs to be read to wrapper class.
     */
    @Test
    public void testClassAGet() {
        final Response response = target("A").request().get();
        final A entity = response.readEntity(A.class);
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.equalTo(200));
        Assert.assertNull(entity.getFieldA1());
    }

    @Test
    public void testDetailedClassAGet() {
        final Response response = target("A").queryParam("detailed", true).request().get();
        final A entity = response.readEntity(A.class);
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.equalTo(200));
        MatcherAssert.assertThat(entity.getFieldA1(), CoreMatchers.equalTo("fieldA1Value"));
    }

    /**
     * Server side response is returned as orig class.
     */
    @Test
    public void testDetailedClassBGet() {
        final Response response = target("B").queryParam("detailed", true).request().get();
        final B entity = response.readEntity(B.class);
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.equalTo(200));
        MatcherAssert.assertThat(entity.getFieldA1(), CoreMatchers.equalTo("fieldA1Value"));
        MatcherAssert.assertThat(entity.getFieldB1(), CoreMatchers.equalTo("fieldB1Value"));
    }

    @Test
    public void testClassBGet() {
        final Response response = target("B").request().get();
        final B entity = response.readEntity(B.class);
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.equalTo(200));
        Assert.assertNull(entity.getFieldA1());
        MatcherAssert.assertThat(entity.getFieldB1(), CoreMatchers.equalTo("fieldB1Value"));
    }
}

