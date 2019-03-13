/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.plugin.access.notification;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class NotificationPluginRegistryTest {
    public static final String PLUGIN_ID_1 = "plugin-id-1";

    public static final String PLUGIN_ID_2 = "plugin-id-2";

    public static final String PLUGIN_ID_3 = "plugin-id-3";

    public static final String PLUGIN_ID_4 = "plugin-id-4";

    public static final String PIPELINE_STATUS = "pipeline-status";

    public static final String STAGE_STATUS = "stage-status";

    public static final String JOB_STATUS = "job-status";

    public static final String UNKNOWN_NOTIFICATION = "unknown-notification";

    private NotificationPluginRegistry notificationPluginRegistry;

    @Test
    public void should_getPluginsInterestedIn_Correctly() {
        Assert.assertThat(notificationPluginRegistry.getPluginsInterestedIn(NotificationPluginRegistryTest.PIPELINE_STATUS), Matchers.containsInAnyOrder(NotificationPluginRegistryTest.PLUGIN_ID_1, NotificationPluginRegistryTest.PLUGIN_ID_2));
        Assert.assertThat(notificationPluginRegistry.getPluginsInterestedIn(NotificationPluginRegistryTest.STAGE_STATUS), Matchers.containsInAnyOrder(NotificationPluginRegistryTest.PLUGIN_ID_1, NotificationPluginRegistryTest.PLUGIN_ID_3));
        Assert.assertThat(notificationPluginRegistry.getPluginsInterestedIn(NotificationPluginRegistryTest.JOB_STATUS), Matchers.containsInAnyOrder(NotificationPluginRegistryTest.PLUGIN_ID_1));
        Assert.assertThat(notificationPluginRegistry.getPluginsInterestedIn(NotificationPluginRegistryTest.UNKNOWN_NOTIFICATION), Matchers.containsInAnyOrder());
    }

    @Test
    public void should_getPluginInterests_Correctly() {
        Assert.assertThat(notificationPluginRegistry.getPluginInterests(NotificationPluginRegistryTest.PLUGIN_ID_1), Matchers.containsInAnyOrder(NotificationPluginRegistryTest.PIPELINE_STATUS, NotificationPluginRegistryTest.STAGE_STATUS, NotificationPluginRegistryTest.JOB_STATUS));
        Assert.assertThat(notificationPluginRegistry.getPluginInterests(NotificationPluginRegistryTest.PLUGIN_ID_2), Matchers.containsInAnyOrder(NotificationPluginRegistryTest.PIPELINE_STATUS));
        Assert.assertThat(notificationPluginRegistry.getPluginInterests(NotificationPluginRegistryTest.PLUGIN_ID_3), Matchers.containsInAnyOrder(NotificationPluginRegistryTest.STAGE_STATUS));
        Assert.assertThat(notificationPluginRegistry.getPluginInterests(NotificationPluginRegistryTest.PLUGIN_ID_4), Matchers.containsInAnyOrder());
    }

    @Test
    public void should_removePluginInterests_Correctly() {
        notificationPluginRegistry.removePluginInterests(NotificationPluginRegistryTest.PLUGIN_ID_1);
        Assert.assertThat(notificationPluginRegistry.getPluginsInterestedIn(NotificationPluginRegistryTest.PIPELINE_STATUS), Matchers.containsInAnyOrder(NotificationPluginRegistryTest.PLUGIN_ID_2));
        Assert.assertThat(notificationPluginRegistry.getPluginsInterestedIn(NotificationPluginRegistryTest.STAGE_STATUS), Matchers.containsInAnyOrder(NotificationPluginRegistryTest.PLUGIN_ID_3));
        Assert.assertThat(notificationPluginRegistry.getPluginsInterestedIn(NotificationPluginRegistryTest.JOB_STATUS), Matchers.containsInAnyOrder());
        Assert.assertThat(notificationPluginRegistry.getPluginsInterestedIn(NotificationPluginRegistryTest.UNKNOWN_NOTIFICATION), Matchers.containsInAnyOrder());
    }

    @Test
    public void should_isAnyPluginInterestedIn_Correctly() {
        Assert.assertThat(notificationPluginRegistry.isAnyPluginInterestedIn(NotificationPluginRegistryTest.PIPELINE_STATUS), Matchers.is(true));
        Assert.assertThat(notificationPluginRegistry.isAnyPluginInterestedIn(NotificationPluginRegistryTest.UNKNOWN_NOTIFICATION), Matchers.is(false));
    }

    @Test
    public void shouldListRegisteredPlugins() {
        notificationPluginRegistry.registerPlugin("plugin_id_1");
        notificationPluginRegistry.registerPlugin("plugin_id_2");
        Assert.assertThat(notificationPluginRegistry.getNotificationPlugins().size(), Matchers.is(2));
        Assert.assertTrue(notificationPluginRegistry.getNotificationPlugins().contains("plugin_id_1"));
        Assert.assertTrue(notificationPluginRegistry.getNotificationPlugins().contains("plugin_id_2"));
    }

    @Test
    public void shouldNotRegisterDuplicatePlugins() {
        notificationPluginRegistry.registerPlugin("plugin_id_1");
        notificationPluginRegistry.registerPlugin("plugin_id_1");
        Assert.assertThat(notificationPluginRegistry.getNotificationPlugins().size(), Matchers.is(1));
        Assert.assertTrue(notificationPluginRegistry.getNotificationPlugins().contains("plugin_id_1"));
    }

    @Test
    public void shouldNotListDeRegisteredPlugins() {
        notificationPluginRegistry.registerPlugin("plugin_id_1");
        notificationPluginRegistry.deregisterPlugin("plugin_id_1");
        Assert.assertTrue(notificationPluginRegistry.getNotificationPlugins().isEmpty());
    }
}

