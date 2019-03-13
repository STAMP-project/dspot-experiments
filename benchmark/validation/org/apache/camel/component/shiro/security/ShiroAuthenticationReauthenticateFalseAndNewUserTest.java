/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.shiro.security;


import ShiroSecurityConstants.SHIRO_SECURITY_TOKEN;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class ShiroAuthenticationReauthenticateFalseAndNewUserTest extends CamelTestSupport {
    @EndpointInject(uri = "mock:success")
    protected MockEndpoint successEndpoint;

    @EndpointInject(uri = "mock:authenticationException")
    protected MockEndpoint failureEndpoint;

    private byte[] passPhrase = new byte[]{ ((byte) (8)), ((byte) (9)), ((byte) (10)), ((byte) (11)), ((byte) (12)), ((byte) (13)), ((byte) (14)), ((byte) (15)), ((byte) (16)), ((byte) (17)), ((byte) (18)), ((byte) (19)), ((byte) (20)), ((byte) (21)), ((byte) (22)), ((byte) (23)) };

    @Test
    public void testSuccessfulShiroAuthenticationWithNoAuthorization() throws Exception {
        ShiroSecurityToken shiroSecurityToken = new ShiroSecurityToken("ringo", "starr");
        ShiroAuthenticationReauthenticateFalseAndNewUserTest.TestShiroSecurityTokenInjector shiroSecurityTokenInjector = new ShiroAuthenticationReauthenticateFalseAndNewUserTest.TestShiroSecurityTokenInjector(shiroSecurityToken, passPhrase);
        ShiroSecurityToken shiroSecurityToken2 = new ShiroSecurityToken("george", "harrison");
        ShiroAuthenticationReauthenticateFalseAndNewUserTest.TestShiroSecurityTokenInjector shiroSecurityTokenInjector2 = new ShiroAuthenticationReauthenticateFalseAndNewUserTest.TestShiroSecurityTokenInjector(shiroSecurityToken2, passPhrase);
        successEndpoint.expectedMessageCount(2);
        failureEndpoint.expectedMessageCount(0);
        template.send("direct:secureEndpoint", shiroSecurityTokenInjector);
        template.send("direct:secureEndpoint", shiroSecurityTokenInjector2);
        successEndpoint.assertIsSatisfied();
        failureEndpoint.assertIsSatisfied();
    }

    private static class TestShiroSecurityTokenInjector extends ShiroSecurityTokenInjector {
        TestShiroSecurityTokenInjector(ShiroSecurityToken shiroSecurityToken, byte[] bytes) {
            super(shiroSecurityToken, bytes);
        }

        public void process(Exchange exchange) throws Exception {
            exchange.getIn().setHeader(SHIRO_SECURITY_TOKEN, encrypt());
            exchange.getIn().setBody("Beatle Mania");
        }
    }
}

