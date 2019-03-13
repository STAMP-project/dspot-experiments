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
package org.pentaho.di.core.plugins;


import java.io.InputStream;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.encryption.TwoWayPasswordEncoderPluginType;


public class BasePluginTypeTest {
    @Test
    public void testRegisterNativesCloseResAsStream() throws Exception {
        BasePluginType bpt = Mockito.spy(DatabasePluginType.getInstance());
        InputStream is = Mockito.mock(InputStream.class);
        Mockito.doReturn(is).when(bpt).getResAsStreamExternal(ArgumentMatchers.anyString());
        Mockito.doNothing().when(bpt).registerPlugins(is);
        bpt.registerNatives();
        Mockito.verify(is).close();
    }

    @Test
    public void testRegisterNativesCloseFileInStream() throws Exception {
        BasePluginType bpt = Mockito.spy(TwoWayPasswordEncoderPluginType.getInstance());
        InputStream is = Mockito.mock(InputStream.class);
        Mockito.doReturn("foo").when(bpt).getPropertyExternal(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn(null).when(bpt).getResAsStreamExternal(ArgumentMatchers.anyString());
        Mockito.doReturn(is).when(bpt).getFileInputStreamExternal(ArgumentMatchers.anyString());
        Mockito.doNothing().when(bpt).registerPlugins(is);
        bpt.registerNatives();
        Mockito.verify(is).close();
    }
}

