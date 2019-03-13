/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.trans.steps.rest;


import WebResource.Builder;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.apache4.ApacheHttpClient4;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import javax.ws.rs.core.MultivaluedMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static RestMeta.HTTP_METHOD_DELETE;
import static org.mockito.ArgumentMatchers.anyString;


@RunWith(PowerMockRunner.class)
@PrepareForTest(ApacheHttpClient4.class)
public class RestTest {
    @Test
    public void testCreateMultivalueMap() {
        StepMeta stepMeta = new StepMeta();
        stepMeta.setName("TestRest");
        TransMeta transMeta = new TransMeta();
        transMeta.setName("TestRest");
        transMeta.addStep(stepMeta);
        Rest rest = new Rest(stepMeta, Mockito.mock(StepDataInterface.class), 1, transMeta, Mockito.mock(Trans.class));
        MultivaluedMapImpl map = rest.createMultivalueMap("param1", "{a:{[val1]}}");
        String val1 = map.getFirst("param1");
        Assert.assertTrue(val1.contains("%7D"));
    }

    @Test
    public void testCallEndpointWithDeleteVerb() throws KettleException {
        MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
        headers.add("Content-Type", "application/json");
        ClientResponse response = Mockito.mock(ClientResponse.class);
        Mockito.doReturn(200).when(response).getStatus();
        Mockito.doReturn(headers).when(response).getHeaders();
        Mockito.doReturn("true").when(response).getEntity(String.class);
        WebResource.Builder builder = Mockito.mock(Builder.class);
        Mockito.doReturn(response).when(builder).delete(ClientResponse.class);
        WebResource resource = Mockito.mock(WebResource.class);
        Mockito.doReturn(builder).when(resource).getRequestBuilder();
        ApacheHttpClient4 client = Mockito.mock(ApacheHttpClient4.class);
        Mockito.doReturn(resource).when(client).resource(anyString());
        mockStatic(ApacheHttpClient4.class);
        Mockito.when(ApacheHttpClient4.create(ArgumentMatchers.any())).thenReturn(client);
        RestMeta meta = Mockito.mock(RestMeta.class);
        Mockito.doReturn(false).when(meta).isDetailed();
        Mockito.doReturn(false).when(meta).isUrlInField();
        Mockito.doReturn(false).when(meta).isDynamicMethod();
        RowMetaInterface rmi = Mockito.mock(RowMetaInterface.class);
        Mockito.doReturn(1).when(rmi).size();
        RestData data = Mockito.mock(RestData.class);
        data.method = HTTP_METHOD_DELETE;
        data.inputRowMeta = rmi;
        data.resultFieldName = "result";
        data.resultCodeFieldName = "status";
        data.resultHeaderFieldName = "headers";
        Rest rest = Mockito.mock(Rest.class);
        Mockito.doCallRealMethod().when(rest).callRest(ArgumentMatchers.any());
        Mockito.doCallRealMethod().when(rest).searchForHeaders(ArgumentMatchers.any());
        Whitebox.setInternalState(rest, "meta", meta);
        Whitebox.setInternalState(rest, "data", data);
        Object[] output = rest.callRest(new Object[]{ 0 });
        Mockito.verify(builder, Mockito.times(1)).delete(ClientResponse.class);
        Assert.assertEquals("true", output[1]);
        Assert.assertEquals(200L, output[2]);
        Assert.assertEquals("{\"Content-Type\":\"application\\/json\"}", output[3]);
    }
}

