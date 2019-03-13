/**
 * -\-\-
 * Spotify Apollo Slack Module
 * --
 * Copyright (C) 2013 - 2015 Spotify AB
 * --
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
 * -/-/-
 */
package com.spotify.apollo.slack;


import SlackProvider.CONFIG_PATH_EMOJI;
import SlackProvider.CONFIG_PATH_MSG_SHUTDOWN;
import SlackProvider.CONFIG_PATH_MSG_STARTUP;
import SlackProvider.CONFIG_PATH_USERNAME;
import SlackProvider.CONFIG_PATH_WEBHOOK;
import SlackProvider.SlackConfig;
import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class SlackProviderTest {
    @Test
    public void config() {
        Map<String, Object> config = ImmutableMap.<String, Object>of(CONFIG_PATH_WEBHOOK, "http://example.com", CONFIG_PATH_USERNAME, "username", CONFIG_PATH_EMOJI, "emoji", CONFIG_PATH_MSG_STARTUP, "startup", CONFIG_PATH_MSG_SHUTDOWN, "shutdown");
        SlackProvider.SlackConfig slackConfig = SlackConfig.fromConfig(ConfigFactory.parseMap(config), "servicename");
        Assert.assertEquals(config.get(CONFIG_PATH_USERNAME), slackConfig.username());
        Assert.assertEquals(config.get(CONFIG_PATH_EMOJI), slackConfig.emoji());
        Assert.assertEquals(config.get(CONFIG_PATH_MSG_STARTUP), slackConfig.startupMsg());
        Assert.assertEquals(config.get(CONFIG_PATH_MSG_SHUTDOWN), slackConfig.shutdownMsg());
    }

    @Test
    public void configDefaultsToServicename() {
        final String servicename = "servicename";
        Map<String, Object> configMap = ImmutableMap.<String, Object>of(CONFIG_PATH_WEBHOOK, "http://example.com");
        final Config config = ConfigFactory.parseMap(configMap);
        SlackProvider.SlackConfig slackConfig = SlackConfig.fromConfig(config, servicename);
        Assert.assertEquals(servicename, slackConfig.username());
    }

    @Test
    public void configDefaults() {
        Map<String, Object> configMap = ImmutableMap.<String, Object>of(CONFIG_PATH_WEBHOOK, "http://example.com");
        final Config config = ConfigFactory.parseMap(configMap);
        SlackProvider.SlackConfig slackConfig = SlackConfig.fromConfig(config, "nop");
        Assert.assertEquals(true, SlackConfig.enabled(config));
        Assert.assertEquals("nop", slackConfig.username());
        Assert.assertEquals(":spoticon:", slackConfig.emoji());
        Assert.assertEquals("", slackConfig.startupMsg());
        Assert.assertEquals("", slackConfig.shutdownMsg());
    }
}

