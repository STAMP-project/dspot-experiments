/**
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.restassured.itest.java;


import io.restassured.RestAssured;
import io.restassured.config.SessionConfig;
import io.restassured.itest.java.support.WithJetty;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class SessionIdITest extends WithJetty {
    @Test
    public void settingSessionIdThroughTheDSLConfig() throws Exception {
        get("/sessionId");
    }

    @Test
    public void settingSessionIdThroughTheDSL() throws Exception {
        get("/sessionId");
    }

    @Test
    public void settingSessionIdThroughTheDSLHasPrecedenceOverTheConfig() throws Exception {
        get("/sessionId");
    }

    @Test
    public void settingSessionIdThroughTheDSLHasPrecedenceOverTheStaticConfig() throws Exception {
        RestAssured.config = newConfig().sessionConfig(new SessionConfig("1235"));
        try {
            get("/sessionId");
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void settingSessionIdThroughStaticConfig() throws Exception {
        RestAssured.config = newConfig().sessionConfig(new SessionConfig("1234"));
        try {
            get("/sessionId");
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void settingSessionIdNameThroughTheDSLOverridesTheSessionIdInTheDefaultSessionConfig() throws Exception {
        RestAssured.config = newConfig().sessionConfig(new SessionConfig("phpsessionid", "12345"));
        try {
            get("/sessionId");
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void settingSessionIdNameThroughTheDSLWorks() throws Exception {
        get("/sessionId");
    }

    @Test
    public void settingSessionIdStaticallyWorks() throws Exception {
        RestAssured.sessionId = "1234";
        try {
            get("/sessionId");
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void settingTheSessionIdTwiceOverwritesTheFirstOne() throws Exception {
        get("/sessionId");
    }

    @Test
    public void restAssuredResponseSupportsGettingTheSessionId() throws Exception {
        final String sessionId = get("/sessionId").sessionId();
        Assert.assertThat(sessionId, Matchers.equalTo("1234"));
    }

    @Test
    public void sessionIdReturnsNullWhenNoCookiesAreDefined() throws Exception {
        final String sessionId = get("/shopping").sessionId();
        Assert.assertThat(sessionId, Matchers.nullValue());
    }
}

