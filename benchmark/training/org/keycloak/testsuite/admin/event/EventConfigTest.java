/**
 * Copyright 2016 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.keycloak.testsuite.admin.event;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test updates to the events configuration.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2016 Red Hat Inc.
 */
public class EventConfigTest extends AbstractEventTest {
    @Test
    public void defaultEventConfigTest() {
        Assert.assertFalse(configRep.isAdminEventsDetailsEnabled());
        Assert.assertFalse(configRep.isAdminEventsEnabled());
        Assert.assertFalse(configRep.isEventsEnabled());
        List<String> eventListeners = configRep.getEventsListeners();
        Assert.assertEquals(1, eventListeners.size());
        Assert.assertEquals("jboss-logging", eventListeners.get(0));
    }

    @Test
    public void enableEventsTest() {
        enableEvents();
        Assert.assertTrue(configRep.isEventsEnabled());
        Assert.assertTrue(configRep.isAdminEventsEnabled());
    }

    @Test
    public void addRemoveListenerTest() {
        configRep.setEventsListeners(Collections.EMPTY_LIST);
        saveConfig();
        Assert.assertEquals(0, configRep.getEventsListeners().size());
        configRep.setEventsListeners(Arrays.asList("email"));
        saveConfig();
        List<String> eventListeners = configRep.getEventsListeners();
        Assert.assertEquals(1, eventListeners.size());
        Assert.assertEquals("email", eventListeners.get(0));
    }

    @Test
    public void loginEventSettingsTest() {
        enableEvents();
        Assert.assertTrue(hasEventType("LOGIN"));
        Assert.assertTrue(hasEventType("LOGOUT"));
        Assert.assertTrue(hasEventType("CLIENT_DELETE_ERROR"));
        int defaultEventCount = configRep.getEnabledEventTypes().size();
        configRep.setEnabledEventTypes(Arrays.asList("CLIENT_DELETE", "CLEINT_DELETE_ERROR"));
        saveConfig();
        List<String> enabledEventTypes = configRep.getEnabledEventTypes();
        Assert.assertEquals(2, enabledEventTypes.size());
        // remove all event types
        configRep.setEnabledEventTypes(Collections.EMPTY_LIST);
        saveConfig();
        // removing all event types restores default events
        Assert.assertEquals(defaultEventCount, configRep.getEnabledEventTypes().size());
    }

    @Test
    public void includeRepresentationTest() {
        enableEvents();
        Assert.assertTrue(configRep.isAdminEventsEnabled());
        Assert.assertFalse(configRep.isAdminEventsDetailsEnabled());
        configRep.setAdminEventsDetailsEnabled(Boolean.TRUE);
        saveConfig();
        Assert.assertTrue(configRep.isAdminEventsDetailsEnabled());
    }

    @Test
    public void setLoginEventExpirationTest() {
        enableEvents();
        Assert.assertNull(configRep.getEventsExpiration());
        Long oneHour = 3600L;
        configRep.setEventsExpiration(oneHour);
        saveConfig();
        Assert.assertEquals(oneHour, configRep.getEventsExpiration());
    }
}

