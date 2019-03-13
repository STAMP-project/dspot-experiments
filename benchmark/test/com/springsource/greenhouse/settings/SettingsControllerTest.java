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
package com.springsource.greenhouse.settings;


import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.ui.ExtendedModelMap;


public class SettingsControllerTest {
    private EmbeddedDatabase db;

    private JdbcTemplate jdbcTemplate;

    private SettingsController controller;

    private TokenStore tokenStore;

    @Test
    public void settingsPage() {
        ExtendedModelMap model = new ExtendedModelMap();
        controller.settingsPage(testAccount(), model);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> apps = ((List<Map<String, Object>>) (model.get("apps")));
        Assert.assertNotNull(apps);
        Assert.assertEquals(1, apps.size());
        Assert.assertEquals("Greenhouse for the iPhone", apps.get(0).get("name"));
        Assert.assertEquals("authme", apps.get(0).get("accessToken"));
    }

    @Test
    public void disconnectApp() {
        Assert.assertEquals("redirect:/settings", controller.disconnectApp("authme", testAccount()));
        Assert.assertEquals(0, tokenStore.findTokensByUserName("kdonald").size());
    }
}

