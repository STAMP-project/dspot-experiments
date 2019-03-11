/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.tests.e2e.json;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.JSONP;
import org.glassfish.jersey.test.JerseyTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 *
 * @author Michal Gajdos
 */
@RunWith(Parameterized.class)
public class JsonWithPaddingTest extends JerseyTest {
    @SuppressWarnings("UnusedDeclaration")
    @XmlRootElement
    public static class JsonBean {
        private String attribute;

        public JsonBean() {
        }

        public JsonBean(final String attr) {
            this.attribute = attr;
        }

        public static JsonWithPaddingTest.JsonBean createTestInstance() {
            return new JsonWithPaddingTest.JsonBean("attr");
        }

        public String getAttribute() {
            return attribute;
        }

        public void setAttribute(final String attribute) {
            this.attribute = attribute;
        }
    }

    @Path("jsonp")
    @Produces({ "application/x-javascript", "application/json" })
    public static class JsonResource {
        @GET
        @Path("PureJson")
        public JsonWithPaddingTest.JsonBean getPureJson() {
            return JsonWithPaddingTest.JsonBean.createTestInstance();
        }

        @GET
        @JSONP
        @Path("JsonWithPaddingDefault")
        public JsonWithPaddingTest.JsonBean getJsonWithPaddingDefault() {
            return JsonWithPaddingTest.JsonBean.createTestInstance();
        }

        @GET
        @JSONP(queryParam = "eval")
        @Path("JsonWithPaddingQueryCallbackParam")
        public JsonWithPaddingTest.JsonBean getJsonWithPaddingQueryCallbackParam() {
            return JsonWithPaddingTest.JsonBean.createTestInstance();
        }

        @GET
        @JSONP(callback = "parse", queryParam = "eval")
        @Path("JsonWithPaddingCallbackAndQueryCallbackParam")
        public JsonWithPaddingTest.JsonBean getJsonWithPaddingCallbackAndQueryCallbackParam() {
            return JsonWithPaddingTest.JsonBean.createTestInstance();
        }

        @GET
        @JSONP(callback = "eval")
        @Path("JsonWithPaddingCallback")
        public JsonWithPaddingTest.JsonBean getJsonWithPaddingCallback() {
            return JsonWithPaddingTest.JsonBean.createTestInstance();
        }
    }

    private final JsonTestProvider jsonTestProvider;

    private final String errorMessage;

    public JsonWithPaddingTest(final JsonTestProvider jsonTestProvider) throws Exception {
        super(JsonWithPaddingTest.configureJaxrsApplication(jsonTestProvider));
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        this.jsonTestProvider = jsonTestProvider;
        this.errorMessage = String.format("%s: Received JSON entity content does not match expected JSON entity content.", jsonTestProvider.getClass().getSimpleName());
    }

    @Test
    public void testJson() throws Exception {
        final Response response = target("jsonp").path("PureJson").request("application/json").get();
        Assert.assertThat(response.getStatus(), CoreMatchers.equalTo(200));
        Assert.assertThat(response.getMediaType().toString(), CoreMatchers.equalTo("application/json"));
        final String entity = response.readEntity(String.class);
        Assert.assertThat(errorMessage, entity, CoreMatchers.allOf(CoreMatchers.not(CoreMatchers.startsWith("callback(")), CoreMatchers.not(CoreMatchers.endsWith(")"))));
    }

    @Test
    public void testJsonWithJavaScriptMediaType() throws Exception {
        final Response response = target("jsonp").path("PureJson").request("application/x-javascript").get();
        // Method is invoked but we do not have a MBW for application/x-javascript.
        if ((jsonTestProvider.getFeature().getClass()) == (JacksonFeature.class)) {
            Assert.assertThat(response.getStatus(), CoreMatchers.equalTo(200));
        } else {
            Assert.assertThat(response.getStatus(), CoreMatchers.equalTo(500));
        }
    }

    @Test
    public void testJsonWithPaddingDefault() throws Exception {
        test("JsonWithPaddingDefault", "callback");
    }

    @Test
    public void testJsonWithPaddingQueryCallbackParam() throws Exception {
        test("JsonWithPaddingQueryCallbackParam", "eval", "parse");
    }

    @Test
    public void testJsonWithPaddingQueryCallbackParamDefaultQueryParam() throws Exception {
        test("JsonWithPaddingQueryCallbackParam", "callback", "parse", "callback");
    }

    @Test
    public void testJsonWithPaddingQueryCallbackParamDefaultCallback() throws Exception {
        test("JsonWithPaddingQueryCallbackParam", null, "callback");
    }

    @Test
    public void testJsonWithPaddingQueryCallbackParamNegative() throws Exception {
        test("JsonWithPaddingQueryCallbackParam", "call", "parse", true);
    }

    @Test
    public void testJsonWithPaddingCallbackAndQueryCallbackParam() throws Exception {
        test("JsonWithPaddingCallbackAndQueryCallbackParam", "eval", "run");
    }

    @Test
    public void testJsonWithPaddingCallbackAndQueryCallbackParamNegative() throws Exception {
        test("JsonWithPaddingCallbackAndQueryCallbackParam", "eval", "run", "parse", true);
    }

    @Test
    public void testJsonWithPaddingCallbackAndQueryCallbackParamDefault() throws Exception {
        test("JsonWithPaddingCallbackAndQueryCallbackParam", "evalx", "parse");
    }

    @Test
    public void testJsonWithPaddingCallbackAndQueryCallbackParamDefaultNegative() throws Exception {
        test("JsonWithPaddingCallbackAndQueryCallbackParam", "evalx", "xlave", "eval", true);
    }

    @Test
    public void testJsonWithPaddingCallback() throws Exception {
        test("JsonWithPaddingCallback", "eval", "eval");
    }

    @Test
    public void testJsonWithPaddingCallbackNegative() throws Exception {
        test("JsonWithPaddingCallback", "eval", "lave", true);
    }
}

