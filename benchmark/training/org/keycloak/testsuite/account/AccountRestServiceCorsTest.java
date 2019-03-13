/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.testsuite.account;


import java.io.IOException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.util.TokenUtil;
import org.openqa.selenium.JavascriptExecutor;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class AccountRestServiceCorsTest extends AbstractTestRealmKeycloakTest {
    private static final String VALID_CORS_URL = "http://localtest.me:8180/auth";

    private static final String INVALID_CORS_URL = "http://invalid.localtest.me:8180/auth";

    @Rule
    public TokenUtil tokenUtil = new TokenUtil();

    private CloseableHttpClient client;

    private JavascriptExecutor executor;

    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Test
    public void testGetProfile() throws IOException, InterruptedException {
        driver.navigate().to(AccountRestServiceCorsTest.VALID_CORS_URL);
        doJsGet(executor, getAccountUrl(), tokenUtil.getToken(), true);
    }

    @Test
    public void testGetProfileInvalidOrigin() throws IOException, InterruptedException {
        driver.navigate().to(AccountRestServiceCorsTest.INVALID_CORS_URL);
        doJsGet(executor, getAccountUrl(), tokenUtil.getToken(), false);
    }

    @Test
    public void testUpdateProfile() throws IOException {
        driver.navigate().to(AccountRestServiceCorsTest.VALID_CORS_URL);
        doJsPost(executor, getAccountUrl(), tokenUtil.getToken(), "{ \"firstName\" : \"Bob\" }", true);
    }

    @Test
    public void testUpdateProfileInvalidOrigin() throws IOException {
        driver.navigate().to(AccountRestServiceCorsTest.INVALID_CORS_URL);
        doJsPost(executor, getAccountUrl(), tokenUtil.getToken(), "{ \"firstName\" : \"Bob\" }", false);
    }

    private static class Result {
        int status;

        String result;

        public Result(int status, String result) {
            this.status = status;
            this.result = result;
        }

        public int getStatus() {
            return status;
        }

        public String getResult() {
            return result;
        }
    }
}

