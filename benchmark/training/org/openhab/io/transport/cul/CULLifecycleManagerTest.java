/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.io.transport.cul;


import CULLifecycleManager.KEY_DEVICE_NAME;
import java.util.Dictionary;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.openhab.io.transport.cul.internal.CULConfig;
import org.openhab.io.transport.cul.internal.CULConfigFactory;
import org.openhab.io.transport.cul.internal.CULHandlerInternal;
import org.openhab.io.transport.cul.internal.CULManager;
import org.osgi.service.cm.ConfigurationException;


@RunWith(MockitoJUnitRunner.class)
public class CULLifecycleManagerTest {
    private static final CULMode MODE = CULMode.values()[0];

    private CULLifecycleManager sut;

    @Mock
    private CULLifecycleListener listener;

    @Mock
    private CULManager manager;

    @Mock
    private CULHandlerInternal<CULConfig> cul;

    @Mock
    private CULHandlerInternal<CULConfig> newCul;

    @Mock
    private CULConfig config;

    @Mock
    private CULConfig differentConfig;

    @Mock
    private Dictionary<String, String> allConfig;

    @Mock
    private CULConfigFactory configFactory;

    @Test
    public void config_noConfig() throws Exception {
        sut.config(null);
        Mockito.verifyNoMoreInteractions(manager);
    }

    @Test(expected = ConfigurationException.class)
    public void config_noDeviceName() throws Exception {
        sut.config(allConfig);
        Mockito.verifyNoMoreInteractions(manager);
    }

    @Test(expected = ConfigurationException.class)
    public void config_invalidDeviceName() throws Exception {
        BDDMockito.given(allConfig.get(KEY_DEVICE_NAME)).willReturn("foo");
        sut.config(allConfig);
        Mockito.verifyNoMoreInteractions(manager);
    }

    @Test(expected = ConfigurationException.class)
    public void config_invalidDeviceType() throws Exception {
        BDDMockito.given(allConfig.get(KEY_DEVICE_NAME)).willReturn("foo:bar");
        sut.config(allConfig);
        Mockito.verifyNoMoreInteractions(manager);
    }

    @Test
    public void config_ok() throws Exception {
        sut = new CULLifecycleManager(CULLifecycleManagerTest.MODE, listener, manager, null, null);
        BDDMockito.given(allConfig.get(KEY_DEVICE_NAME)).willReturn("foo:bar");
        BDDMockito.given(manager.getConfigFactory("foo")).willReturn(configFactory);
        BDDMockito.given(configFactory.create("foo", "bar", CULLifecycleManagerTest.MODE, allConfig)).willReturn(config);
        BDDMockito.given(manager.getOpenCULHandler(config)).willReturn(newCul);
        sut.config(allConfig);
        BDDMockito.then(listener).should().open(newCul);
    }

    @Test
    public void close_notOpened() throws Exception {
        sut = new CULLifecycleManager(CULLifecycleManagerTest.MODE, listener, manager, null, null);
        sut.close();
        Mockito.verifyNoMoreInteractions(listener, manager);
        assertNotReady();
    }

    @Test
    public void close_opened() throws Exception {
        sut.close();
        BDDMockito.then(listener).should().close(cul);
        BDDMockito.then(manager).should().close(cul);
        assertNotReady();
    }

    @Test
    public void open_noConfig() throws Exception {
        sut = new CULLifecycleManager(CULLifecycleManagerTest.MODE, listener, manager, null, null);
        sut.open();
        Mockito.verifyNoMoreInteractions(listener, manager);
        assertNotReady();
    }

    @Test
    public void open_sameConfig() throws Exception {
        BDDMockito.given(cul.getConfig()).willReturn(config);
        sut.open();
        Mockito.verifyNoMoreInteractions(listener, manager);
        assertReady();
    }

    @Test
    public void open_firstConfig() throws Exception {
        BDDMockito.given(manager.getOpenCULHandler(config)).willReturn(newCul);
        sut = new CULLifecycleManager(CULLifecycleManagerTest.MODE, listener, manager, null, config);
        sut.open();
        BDDMockito.then(listener).should().open(newCul);
        assertReady();
    }

    @Test
    public void open_changedConfig() throws Exception {
        BDDMockito.given(cul.getConfig()).willReturn(differentConfig);
        BDDMockito.given(manager.getOpenCULHandler(config)).willReturn(newCul);
        sut.open();
        BDDMockito.then(listener).should().close(cul);
        BDDMockito.then(manager).should().close(cul);
        BDDMockito.then(listener).should().open(newCul);
        assertReady();
    }

    @Test
    public void open_failsDevice() throws Exception {
        BDDMockito.given(manager.getOpenCULHandler(config)).willThrow(new CULDeviceException());
        sut = new CULLifecycleManager(CULLifecycleManagerTest.MODE, listener, manager, null, config);
        sut.open();
        assertNotReady();
    }

    @Test
    public void open_failsCommunication() throws Exception {
        BDDMockito.given(manager.getOpenCULHandler(config)).willReturn(newCul);
        Mockito.doThrow(new CULCommunicationException()).when(listener).open(newCul);
        sut = new CULLifecycleManager(CULLifecycleManagerTest.MODE, listener, manager, null, config);
        sut.open();
        assertNotReady();
    }
}

