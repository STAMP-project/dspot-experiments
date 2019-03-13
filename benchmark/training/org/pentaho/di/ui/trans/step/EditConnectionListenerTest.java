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
package org.pentaho.di.ui.trans.step;


import java.io.IOException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.shared.SharedObjectInterface;
import org.pentaho.di.shared.SharedObjects;
import org.pentaho.di.ui.trans.step.BaseStepDialog.EditConnectionListener;

import static org.mockito.ArgumentMatchers.any;


public class EditConnectionListenerTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private static String TEST_NAME = "TEST_NAME";

    private static String TEST_HOST = "TEST_HOST";

    private BaseStepDialog dialog;

    private EditConnectionListener editConnectionListener;

    @Test
    public void replaceSharedConnection() throws IOException, KettleException {
        dialog.transMeta.addDatabase(EditConnectionListenerTest.createDefaultDatabase(true));
        SharedObjects sharedObjects = Mockito.mock(SharedObjects.class);
        Mockito.doReturn(sharedObjects).when(dialog.transMeta).getSharedObjects();
        editConnectionListener.widgetSelected(null);
        Mockito.verify(editConnectionListener).replaceSharedConnection(any(DatabaseMeta.class), any(DatabaseMeta.class));
        Mockito.verify(sharedObjects).removeObject(org.mockito.ArgumentMatchers.any(SharedObjectInterface.class));
        Mockito.verify(sharedObjects).storeObject(org.mockito.ArgumentMatchers.any(SharedObjectInterface.class));
        Mockito.verify(sharedObjects).saveToFile();
    }

    @Test
    public void replaceSharedConnectionDoesNotExecuted_for_nonshared_connection() {
        dialog.transMeta.addDatabase(EditConnectionListenerTest.createDefaultDatabase(false));
        editConnectionListener.widgetSelected(null);
        Mockito.verify(editConnectionListener, Mockito.never()).replaceSharedConnection(any(DatabaseMeta.class), any(DatabaseMeta.class));
    }

    @Test
    public void replaceSharedConnectionReturnsFalse_on_error() throws IOException, KettleException {
        dialog.transMeta.addDatabase(EditConnectionListenerTest.createDefaultDatabase(false));
        SharedObjects sharedObjects = Mockito.mock(SharedObjects.class);
        Mockito.doThrow(Exception.class).when(sharedObjects).saveToFile();
        boolean actualResult = editConnectionListener.replaceSharedConnection(EditConnectionListenerTest.anyDbMeta(), EditConnectionListenerTest.anyDbMeta());
        Assert.assertFalse(actualResult);
        Mockito.verify(editConnectionListener).showErrorDialog(org.mockito.ArgumentMatchers.any(Exception.class));
    }

    private static class PropsSettingAnswer implements Answer<String> {
        private final String name;

        private final String host;

        public PropsSettingAnswer(String name, String host) {
            this.name = name;
            this.host = host;
        }

        @Override
        public String answer(InvocationOnMock invocation) throws Throwable {
            DatabaseMeta meta = ((DatabaseMeta) (invocation.getArguments()[0]));
            meta.setName(name);
            meta.setHostname(host);
            return name;
        }
    }
}

