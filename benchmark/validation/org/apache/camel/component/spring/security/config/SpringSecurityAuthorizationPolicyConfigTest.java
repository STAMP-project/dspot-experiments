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
package org.apache.camel.component.spring.security.config;


import org.apache.camel.component.spring.security.SpringSecurityAuthorizationPolicy;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;


public class SpringSecurityAuthorizationPolicyConfigTest extends Assert {
    private AbstractXmlApplicationContext context;

    @Test
    public void testAuthorizationPolicy() {
        SpringSecurityAuthorizationPolicy adminPolicy = context.getBean("admin", SpringSecurityAuthorizationPolicy.class);
        Assert.assertNotNull("We should get admin policy", adminPolicy);
        Assert.assertNotNull("The accessDecisionManager should not be null", adminPolicy.getAccessDecisionManager());
        Assert.assertNotNull("The authenticationManager should not be null", adminPolicy.getAuthenticationManager());
        Assert.assertNotNull("The springSecurityAccessPolicy should not be null", adminPolicy.getSpringSecurityAccessPolicy());
        SpringSecurityAuthorizationPolicy userPolicy = context.getBean("user", SpringSecurityAuthorizationPolicy.class);
        Assert.assertNotNull("We should get user policy", userPolicy);
        Assert.assertNotNull("The accessDecisionManager should not be null", userPolicy.getAccessDecisionManager());
        Assert.assertNotNull("The authenticationManager should not be null", userPolicy.getAuthenticationManager());
        Assert.assertNotNull("The springSecurityAccessPolicy should not be null", userPolicy.getSpringSecurityAccessPolicy());
        Assert.assertEquals("user policy and admin policy should have same accessDecisionManager", adminPolicy.getAccessDecisionManager(), userPolicy.getAccessDecisionManager());
        Assert.assertEquals("user policy and admin policy should have same authenticationManager", adminPolicy.getAuthenticationManager(), userPolicy.getAuthenticationManager());
    }
}

