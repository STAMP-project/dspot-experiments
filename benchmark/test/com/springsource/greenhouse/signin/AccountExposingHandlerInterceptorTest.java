/**
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.springsource.greenhouse.signin;


import com.springsource.greenhouse.account.Account;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;


public class AccountExposingHandlerInterceptorTest {
    private AccountExposingHandlerInterceptor interceptor;

    private Account account;

    @Test
    public void preHandle() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        interceptor.preHandle(request, null, null);
        Assert.assertSame(account, request.getAttribute("account"));
    }
}

