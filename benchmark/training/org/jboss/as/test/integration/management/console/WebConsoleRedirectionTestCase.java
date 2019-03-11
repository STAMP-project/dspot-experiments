/**
 * JBoss, Home of Professional Open Source
 * Copyright 2017, Red Hat, Inc., and individual contributors as indicated
 * by the @authors tag.
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
package org.jboss.as.test.integration.management.console;


import Headers.LOCATION_STRING;
import java.net.HttpURLConnection;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.management.util.ServerReload;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Tomas Hofman (thofman@redhat.com)
 */
@RunWith(Arquillian.class)
@RunAsClient
public class WebConsoleRedirectionTestCase {
    @SuppressWarnings("unused")
    @ContainerResource
    private ManagementClient managementClient;

    @Test
    public void testRedirectionInAdminMode() throws Exception {
        ServerReload.executeReloadAndWaitForCompletion(managementClient.getControllerClient(), true);
        try {
            final HttpURLConnection connection = getConnection();
            Assert.assertEquals(HttpURLConnection.HTTP_MOVED_TEMP, connection.getResponseCode());
            String location = connection.getHeaderFields().get(LOCATION_STRING).get(0);
            Assert.assertEquals("/consoleerror/noConsoleForAdminModeError.html", location);
        } finally {
            ServerReload.executeReloadAndWaitForCompletion(managementClient.getControllerClient(), false);
        }
    }

    @Test
    public void testRedirectionInNormalMode() throws Exception {
        final HttpURLConnection connection = getConnection();
        Assert.assertEquals(HttpURLConnection.HTTP_MOVED_TEMP, connection.getResponseCode());
        String location = connection.getHeaderFields().get(LOCATION_STRING).get(0);
        Assert.assertEquals("/console/index.html", location);
    }
}

