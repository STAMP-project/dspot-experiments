/**
 * ! ******************************************************************************
 *
 *  Pentaho Data Integration
 *
 *  Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * *****************************************************************************
 */
package org.pentaho.di.trans.ael.websocket;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.engine.api.model.Operation;


@RunWith(MockitoJUnitRunner.class)
public class StepDataInterfaceWebSocketEngineAdapterTest {
    @Mock
    private Operation op;

    private MessageEventService messageEventService;

    private StepDataInterfaceWebSocketEngineAdapter stepDataInterfaceWebSocketEngineAdapter;

    @Test
    public void testHandlerCreation() throws KettleException {
        Assert.assertTrue(messageEventService.hasHandlers(Util.getOperationStatusEvent(op.getId())));
        Assert.assertTrue(((messageEventService.getHandlersFor(Util.getOperationStatusEvent(op.getId())).size()) == 1));
    }

    @Test
    public void testInitValues() throws KettleException {
        // init values only
        Assert.assertTrue(((stepDataInterfaceWebSocketEngineAdapter.getStatus()) == (STATUS_INIT)));
        Assert.assertTrue(stepDataInterfaceWebSocketEngineAdapter.isInitialising());
        Assert.assertFalse(stepDataInterfaceWebSocketEngineAdapter.isEmpty());
        Assert.assertFalse(stepDataInterfaceWebSocketEngineAdapter.isRunning());
        Assert.assertFalse(stepDataInterfaceWebSocketEngineAdapter.isIdle());
        Assert.assertFalse(stepDataInterfaceWebSocketEngineAdapter.isDisposed());
        Assert.assertFalse(stepDataInterfaceWebSocketEngineAdapter.isFinished());
    }
}

