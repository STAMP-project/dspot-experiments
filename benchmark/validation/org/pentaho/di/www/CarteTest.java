/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2016 - 2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.www;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import java.util.Base64;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Created by ccaspanello on 5/31/2016.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Client.class)
public class CarteTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void callStopCarteRestService() throws Exception {
        WebResource status = Mockito.mock(WebResource.class);
        Mockito.doReturn("<serverstatus>").when(status).get(String.class);
        WebResource stop = Mockito.mock(WebResource.class);
        Mockito.doReturn("Shutting Down").when(stop).get(String.class);
        Client client = Mockito.mock(Client.class);
        Mockito.doCallRealMethod().when(client).addFilter(ArgumentMatchers.any(HTTPBasicAuthFilter.class));
        Mockito.doCallRealMethod().when(client).getHeadHandler();
        Mockito.doReturn(status).when(client).resource("http://localhost:8080/kettle/status/?xml=Y");
        Mockito.doReturn(stop).when(client).resource("http://localhost:8080/kettle/stopCarte");
        mockStatic(Client.class);
        Mockito.when(Client.create(ArgumentMatchers.any(ClientConfig.class))).thenReturn(client);
        Carte.callStopCarteRestService("localhost", "8080", "admin", "Encrypted 2be98afc86aa7f2e4bb18bd63c99dbdde");
        // the expected value is: "Basic <base64 encoded username:password>"
        Assert.assertEquals(("Basic " + (new String(Base64.getEncoder().encode("admin:password".getBytes("utf-8"))))), getInternalState(client.getHeadHandler(), "authentication"));
    }
}

