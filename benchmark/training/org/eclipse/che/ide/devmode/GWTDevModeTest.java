/**
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.ide.devmode;


import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.workspace.WsAgentServerUtil;
import org.eclipse.che.ide.api.workspace.model.MachineImpl;
import org.eclipse.che.ide.api.workspace.model.ServerImpl;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Tests for the {@link GWTDevMode}.
 */
@RunWith(MockitoJUnitRunner.class)
public class GWTDevModeTest {
    static final String INT_CODE_SERVER_URL = "http://172.19.20.28:12345/";

    @Mock
    WsAgentServerUtil wsAgentServerUtil;

    @Mock
    DevModeScriptInjector devModeScriptInjector;

    @Mock
    BookmarkletParams bookmarkletParams;

    @Mock
    DialogFactory dialogFactory;

    @Mock
    CoreLocalizationConstant messages;

    @Mock
    Promise<Void> voidPromise;

    @Mock
    PromiseError promiseError;

    @Captor
    ArgumentCaptor<Operation<PromiseError>> promiseErrorOperationCapture;

    @Mock
    MachineImpl wsAgentMachine;

    @Mock
    ServerImpl codeServer;

    @InjectMocks
    GWTDevMode devMode;

    @Test
    public void shouldSetUpDevModeForInternalCodeServer() throws Exception {
        mockInternalCodeServer();
        Mockito.when(devModeScriptInjector.inject(ArgumentMatchers.anyString())).thenReturn(voidPromise);
        Mockito.when(voidPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(voidPromise);
        devMode.setUp();
        Mockito.verify(bookmarkletParams).setParams(GWTDevModeTest.INT_CODE_SERVER_URL, GWTDevMode.IDE_GWT_APP_SHORT_NAME);
        Mockito.verify(devModeScriptInjector).inject(GWTDevModeTest.INT_CODE_SERVER_URL);
    }

    @Test
    public void shouldSetUpDevModeForLocalCodeServerIfNoInternalOne() throws Exception {
        Mockito.when(devModeScriptInjector.inject(ArgumentMatchers.anyString())).thenReturn(voidPromise);
        Mockito.when(voidPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(voidPromise);
        devMode.setUp();
        Mockito.verify(bookmarkletParams).setParams(GWTDevMode.LOCAL_CODE_SERVER_ADDRESS, GWTDevMode.IDE_GWT_APP_SHORT_NAME);
        Mockito.verify(devModeScriptInjector).inject(GWTDevMode.LOCAL_CODE_SERVER_ADDRESS);
    }

    @Test
    public void shouldSetUpDevModeForLocalCodeServerIfFailedForInternalOne() throws Exception {
        mockInternalCodeServer();
        Mockito.when(devModeScriptInjector.inject(ArgumentMatchers.anyString())).thenReturn(voidPromise);
        Mockito.when(voidPromise.then(ArgumentMatchers.any(Operation.class))).thenReturn(voidPromise);
        devMode.setUp();
        Mockito.verify(bookmarkletParams, Mockito.never()).setParams(GWTDevMode.LOCAL_CODE_SERVER_ADDRESS, GWTDevMode.IDE_GWT_APP_SHORT_NAME);
        Mockito.verify(devModeScriptInjector, Mockito.never()).inject(GWTDevMode.LOCAL_CODE_SERVER_ADDRESS);
        Mockito.verify(voidPromise).catchError(promiseErrorOperationCapture.capture());
        promiseErrorOperationCapture.getValue().apply(promiseError);
        Mockito.verify(bookmarkletParams).setParams(GWTDevModeTest.INT_CODE_SERVER_URL, GWTDevMode.IDE_GWT_APP_SHORT_NAME);
        Mockito.verify(devModeScriptInjector).inject(GWTDevModeTest.INT_CODE_SERVER_URL);
    }
}

