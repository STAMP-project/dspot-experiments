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
package org.pentaho.di.ui.spoon;


import org.apache.http.client.ClientProtocolException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;


public class SpoonSlaveTest {
    @Test
    public void setErrorTextWithNoCauseException() {
        Exception e = new KettleException("kettleMessage");
        SpoonSlave spoonSlave = Mockito.mock(SpoonSlave.class);
        Mockito.doCallRealMethod().when(spoonSlave).setExceptionMessage(ArgumentMatchers.any(Exception.class));
        String message = spoonSlave.setExceptionMessage(e);
        Assert.assertEquals(message, e.getMessage().toString());
    }

    @Test
    public void setErrorTextWithCauseMessageException() {
        ClientProtocolException cpe = new ClientProtocolException("causeMessage");
        Exception e = new KettleException("kettleMessage", cpe);
        SpoonSlave spoonSlave = Mockito.mock(SpoonSlave.class);
        Mockito.doCallRealMethod().when(spoonSlave).setExceptionMessage(ArgumentMatchers.any(Exception.class));
        String message = spoonSlave.setExceptionMessage(e);
        Throwable cause = e.getCause();
        Assert.assertEquals(message, cause.getMessage().toString());
    }

    @Test
    public void setErrorTextWithCauseExceptionWithoutCauseMessage() {
        // cause without message
        ClientProtocolException cpe = new ClientProtocolException();
        Exception e = new KettleException("kettleMessage", cpe);
        SpoonSlave spoonSlave = Mockito.mock(SpoonSlave.class);
        Mockito.doCallRealMethod().when(spoonSlave).setExceptionMessage(ArgumentMatchers.any(Exception.class));
        String message = spoonSlave.setExceptionMessage(e);
        Assert.assertEquals(message, e.getMessage().toString());
    }
}

