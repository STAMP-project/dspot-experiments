/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.trans.steps.webservices;


import java.net.URISyntaxException;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicHeader;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


public class WebServiceTest {
    private static final String LOCATION_HEADER = "Location";

    private static final String TEST_URL = "TEST_URL";

    private static final String NOT_VALID_URL = "NOT VALID URL";

    private StepMockHelper<WebServiceMeta, WebServiceData> mockHelper;

    private WebService webServiceStep;

    @Test(expected = URISyntaxException.class)
    public void newHttpMethodWithInvalidUrl() throws URISyntaxException {
        webServiceStep.getHttpMethod(WebServiceTest.NOT_VALID_URL);
    }

    @Test
    public void getLocationFrom() {
        HttpPost postMethod = Mockito.mock(HttpPost.class);
        Header locationHeader = new BasicHeader(WebServiceTest.LOCATION_HEADER, WebServiceTest.TEST_URL);
        Mockito.doReturn(locationHeader).when(postMethod).getFirstHeader(WebServiceTest.LOCATION_HEADER);
        Assert.assertEquals(WebServiceTest.TEST_URL, WebService.getLocationFrom(postMethod));
    }
}

