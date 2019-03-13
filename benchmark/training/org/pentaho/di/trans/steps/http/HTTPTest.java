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
package org.pentaho.di.trans.steps.http;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.util.HttpClientManager;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author Luis Martins
 * @since 14-Aug-2018
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(HttpClientManager.class)
public class HTTPTest {
    private LogChannelInterface log = Mockito.mock(LogChannelInterface.class);

    private RowMetaInterface rmi = Mockito.mock(RowMetaInterface.class);

    private HTTPData data = Mockito.mock(HTTPData.class);

    private HTTPMeta meta = Mockito.mock(HTTPMeta.class);

    private HTTP http = Mockito.mock(HTTP.class);

    private final String DATA = "This is the description, there's some HTML here, like &lt;strong&gt;this&lt;/strong&gt;. " + (("Sometimes this text is another language that might contain these characters:\n" + "&lt;p&gt;?, ?, ?, ?, ?, ?, ?.&lt;/p&gt; They can, of course, come in uppercase as well: &lt;p&gt;?, ? ?, ?, ?,") + " ?, ?&lt;/p&gt;. UTF-8 handles this well.");

    @Test
    public void callHttpServiceWithUTF8Encoding() throws Exception {
        Mockito.doReturn("UTF-8").when(meta).getEncoding();
        Assert.assertEquals(DATA, http.callHttpService(rmi, new Object[]{ 0 })[0]);
    }

    @Test
    public void callHttpServiceWithoutEncoding() throws Exception {
        Mockito.doReturn(null).when(meta).getEncoding();
        Assert.assertNotEquals(DATA, http.callHttpService(rmi, new Object[]{ 0 })[0]);
    }
}

