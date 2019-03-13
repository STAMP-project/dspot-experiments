/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shiro.spring.security.interceptor;


import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.junit.Assert;
import org.junit.Test;


public class AuthorizationAttributeSourceAdvisorTest {
    static class Secured {
        @RequiresAuthentication
        public void secureMethod() {
        }

        public void unsecuredMethod() {
        }
    }

    interface ServiceInterface {
        @RequiresAuthentication
        String secureMethod();

        String unsecuredMethod();
    }

    static class ServiceImpl implements AuthorizationAttributeSourceAdvisorTest.ServiceInterface {
        @Override
        public String secureMethod() {
            return "";
        }

        @Override
        public String unsecuredMethod() {
            return "";
        }
    }

    @RequiresAuthentication
    interface SafeServiceInterface {
        String someMethod();
    }

    static class SafeServiceImpl implements AuthorizationAttributeSourceAdvisorTest.SafeServiceInterface {
        @Override
        public String someMethod() {
            return "";
        }
    }

    @Test
    public void matches() throws NoSuchMethodException {
        Assert.assertTrue("the method is annotated, should match", new AuthorizationAttributeSourceAdvisor().matches(AuthorizationAttributeSourceAdvisorTest.Secured.class.getDeclaredMethod("secureMethod"), AuthorizationAttributeSourceAdvisorTest.Secured.class));
        Assert.assertFalse("the method is not annotated, should not match", new AuthorizationAttributeSourceAdvisor().matches(AuthorizationAttributeSourceAdvisorTest.Secured.class.getDeclaredMethod("unsecuredMethod"), AuthorizationAttributeSourceAdvisorTest.Secured.class));
        Assert.assertTrue("the method declaration is annotated in the interface, should match", new AuthorizationAttributeSourceAdvisor().matches(AuthorizationAttributeSourceAdvisorTest.ServiceInterface.class.getDeclaredMethod("secureMethod"), AuthorizationAttributeSourceAdvisorTest.ServiceImpl.class));
        Assert.assertFalse("not annotated method, should not match", new AuthorizationAttributeSourceAdvisor().matches(AuthorizationAttributeSourceAdvisorTest.ServiceInterface.class.getDeclaredMethod("unsecuredMethod"), AuthorizationAttributeSourceAdvisorTest.ServiceImpl.class));
        Assert.assertTrue("the method declaration is in the interface with type-annotation, should match", new AuthorizationAttributeSourceAdvisor().matches(AuthorizationAttributeSourceAdvisorTest.SafeServiceInterface.class.getDeclaredMethod("someMethod"), AuthorizationAttributeSourceAdvisorTest.SafeServiceInterface.class));
        Assert.assertTrue("the method declaration is in the interface with type-annotation, should match", new AuthorizationAttributeSourceAdvisor().matches(AuthorizationAttributeSourceAdvisorTest.SafeServiceImpl.class.getDeclaredMethod("someMethod"), AuthorizationAttributeSourceAdvisorTest.SafeServiceImpl.class));
    }
}

