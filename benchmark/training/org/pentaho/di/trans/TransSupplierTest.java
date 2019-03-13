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
package org.pentaho.di.trans;


import java.util.Properties;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.ael.websocket.TransWebSocketEngineAdapter;


public class TransSupplierTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private Trans trans = Mockito.mock(Trans.class);

    private TransMeta meta = Mockito.mock(TransMeta.class);

    private Supplier<Trans> fallbackSupplier = Mockito.mock(Supplier.class);

    private LogChannelInterface log = Mockito.mock(LogChannelInterface.class);

    private TransHopMeta transHopMeta = Mockito.mock(TransHopMeta.class);

    private TransSupplier transSupplier;

    private Properties props = null;

    @Test
    public void testFallback() throws KettleException {
        Mockito.when(fallbackSupplier.get()).thenReturn(trans);
        transSupplier = new TransSupplier(meta, log, fallbackSupplier);
        Trans transRet = transSupplier.get();
        Mockito.verify(fallbackSupplier).get();
        Assert.assertEquals(transRet, trans);
    }

    @Test
    public void testWebsocketVersion() throws KettleException {
        props.setProperty("KETTLE_AEL_PDI_DAEMON_VERSION", "2.0");
        Mockito.when(meta.getVariable("engine")).thenReturn("spark");
        Mockito.when(meta.getVariable("engine.host")).thenReturn("hostname");
        Mockito.when(meta.getVariable("engine.port")).thenReturn("8080");
        Mockito.when(meta.nrTransHops()).thenReturn(0);
        Mockito.when(meta.getTransHop(0)).thenReturn(transHopMeta);
        Mockito.when(meta.realClone(false)).thenReturn(meta);
        Mockito.when(transHopMeta.isEnabled()).thenReturn(false);
        transSupplier = new TransSupplier(meta, log, fallbackSupplier);
        Trans transRet = transSupplier.get();
        Assert.assertTrue((transRet instanceof TransWebSocketEngineAdapter));
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidEngine() throws KettleException {
        props.setProperty("KETTLE_AEL_PDI_DAEMON_VERSION", "1.0");
        Mockito.when(meta.getVariable("engine")).thenReturn("invalidEngine");
        transSupplier = new TransSupplier(meta, log, fallbackSupplier);
        transSupplier.get();
    }
}

