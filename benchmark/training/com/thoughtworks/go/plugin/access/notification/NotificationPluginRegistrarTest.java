/**
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.plugin.access.notification;


import NotificationExtension.VALID_NOTIFICATION_TYPES;
import com.thoughtworks.go.plugin.infra.PluginManager;
import com.thoughtworks.go.plugin.infra.plugininfo.GoPluginDescriptor;
import com.thoughtworks.go.util.ReflectionUtil;
import java.util.Arrays;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;


public class NotificationPluginRegistrarTest {
    public static final String PLUGIN_ID_1 = "plugin-id-1";

    public static final String PLUGIN_ID_2 = "plugin-id-2";

    public static final String PLUGIN_ID_3 = "plugin-id-3";

    public static final String PLUGIN_ID_4 = "plugin-id-4";

    public static final String PIPELINE_STATUS = "pipeline-status";

    public static final String STAGE_STATUS = "stage-status";

    public static final String JOB_STATUS = "job-status";

    @Mock
    private PluginManager pluginManager;

    @Mock
    private NotificationExtension notificationExtension;

    @Mock
    private NotificationPluginRegistry notificationPluginRegistry;

    @Test
    public void shouldRegisterPluginInterestsOnPluginLoad() {
        NotificationPluginRegistrar notificationPluginRegistrar = new NotificationPluginRegistrar(pluginManager, notificationExtension, notificationPluginRegistry);
        notificationPluginRegistrar.pluginLoaded(new GoPluginDescriptor(NotificationPluginRegistrarTest.PLUGIN_ID_1, null, null, null, null, true));
        Mockito.verify(notificationPluginRegistry).registerPluginInterests(NotificationPluginRegistrarTest.PLUGIN_ID_1, Arrays.asList(NotificationPluginRegistrarTest.PIPELINE_STATUS, NotificationPluginRegistrarTest.STAGE_STATUS, NotificationPluginRegistrarTest.JOB_STATUS));
        notificationPluginRegistrar.pluginLoaded(new GoPluginDescriptor(NotificationPluginRegistrarTest.PLUGIN_ID_2, null, null, null, null, true));
        Mockito.verify(notificationPluginRegistry).registerPluginInterests(NotificationPluginRegistrarTest.PLUGIN_ID_2, Arrays.asList(NotificationPluginRegistrarTest.PIPELINE_STATUS));
        notificationPluginRegistrar.pluginLoaded(new GoPluginDescriptor(NotificationPluginRegistrarTest.PLUGIN_ID_3, null, null, null, null, true));
        Mockito.verify(notificationPluginRegistry).registerPluginInterests(NotificationPluginRegistrarTest.PLUGIN_ID_3, Arrays.asList(NotificationPluginRegistrarTest.STAGE_STATUS));
    }

    @Test
    public void shouldNotRegisterPluginInterestsOnPluginLoadIfPluginIfPluginIsNotOfNotificationType() {
        NotificationPluginRegistrar notificationPluginRegistrar = new NotificationPluginRegistrar(pluginManager, notificationExtension, notificationPluginRegistry);
        notificationPluginRegistrar.pluginLoaded(new GoPluginDescriptor(NotificationPluginRegistrarTest.PLUGIN_ID_4, null, null, null, null, true));
        Mockito.verify(notificationPluginRegistry, Mockito.never()).registerPluginInterests(ArgumentMatchers.anyString(), ArgumentMatchers.anyList());
    }

    @Test
    public void shouldUnregisterPluginInterestsOnPluginUnLoad() {
        NotificationPluginRegistrar notificationPluginRegistrar = new NotificationPluginRegistrar(pluginManager, notificationExtension, notificationPluginRegistry);
        notificationPluginRegistrar.pluginUnLoaded(new GoPluginDescriptor(NotificationPluginRegistrarTest.PLUGIN_ID_1, null, null, null, null, true));
        Mockito.verify(notificationPluginRegistry).removePluginInterests(NotificationPluginRegistrarTest.PLUGIN_ID_1);
    }

    @Test
    public void shouldNotUnregisterPluginInterestsOnPluginUnLoadIfPluginIsNotOfNotificationType() {
        NotificationPluginRegistrar notificationPluginRegistrar = new NotificationPluginRegistrar(pluginManager, notificationExtension, notificationPluginRegistry);
        notificationPluginRegistrar.pluginUnLoaded(new GoPluginDescriptor(NotificationPluginRegistrarTest.PLUGIN_ID_4, null, null, null, null, true));
        Mockito.verify(notificationPluginRegistry, Mockito.never()).removePluginInterests(NotificationPluginRegistrarTest.PLUGIN_ID_4);
    }

    @Test
    public void shouldLogWarningIfPluginTriesToRegisterForInvalidNotificationType() {
        NotificationPluginRegistrar notificationPluginRegistrar = new NotificationPluginRegistrar(pluginManager, notificationExtension, notificationPluginRegistry);
        Logger logger = Mockito.mock(Logger.class);
        ReflectionUtil.setStaticField(NotificationPluginRegistrar.class, "LOGGER", logger);
        notificationPluginRegistrar.pluginLoaded(new GoPluginDescriptor(NotificationPluginRegistrarTest.PLUGIN_ID_1, null, null, null, null, true));
        Mockito.verify(logger).warn("Plugin '{}' is trying to register for '{}' which is not a valid notification type. Valid notification types are: {}", "plugin-id-1", "pipeline-status", VALID_NOTIFICATION_TYPES);
        Mockito.verify(logger).warn("Plugin '{}' is trying to register for '{}' which is not a valid notification type. Valid notification types are: {}", "plugin-id-1", "job-status", VALID_NOTIFICATION_TYPES);
    }

    @Test
    public void shouldRegisterPluginOnPluginLoad() {
        NotificationPluginRegistrar notificationPluginRegistrar = new NotificationPluginRegistrar(pluginManager, notificationExtension, notificationPluginRegistry);
        notificationPluginRegistrar.pluginLoaded(new GoPluginDescriptor(NotificationPluginRegistrarTest.PLUGIN_ID_1, null, null, null, null, true));
        Mockito.verify(notificationPluginRegistry).registerPlugin(NotificationPluginRegistrarTest.PLUGIN_ID_1);
    }

    @Test
    public void shouldUnregisterPluginOnPluginUnLoad() {
        NotificationPluginRegistrar notificationPluginRegistrar = new NotificationPluginRegistrar(pluginManager, notificationExtension, notificationPluginRegistry);
        notificationPluginRegistrar.pluginUnLoaded(new GoPluginDescriptor(NotificationPluginRegistrarTest.PLUGIN_ID_1, null, null, null, null, true));
        Mockito.verify(notificationPluginRegistry).deregisterPlugin(NotificationPluginRegistrarTest.PLUGIN_ID_1);
    }
}

