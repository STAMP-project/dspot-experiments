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


import org.eclipse.swt.widgets.Shell;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.ui.core.database.dialog.DatabaseDialog;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
public class BaseStepDialog_ConnectionLine_Test {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private static String INITIAL_NAME = "qwerty";

    private static String INPUT_NAME = "asdfg";

    private static String INITIAL_HOST = "1.2.3.4";

    private static String INPUT_HOST = "5.6.7.8";

    @Test
    public void adds_WhenConnectionNameIsUnique() throws Exception {
        TransMeta transMeta = new TransMeta();
        invokeAddConnectionListener(transMeta, BaseStepDialog_ConnectionLine_Test.INPUT_NAME);
        assertOnlyDbExists(transMeta, BaseStepDialog_ConnectionLine_Test.INPUT_NAME, BaseStepDialog_ConnectionLine_Test.INPUT_HOST);
    }

    @Test
    public void ignores_WhenConnectionNameIsUsed() throws Exception {
        TransMeta transMeta = new TransMeta();
        transMeta.addDatabase(createDefaultDatabase());
        invokeAddConnectionListener(transMeta, null);
        assertOnlyDbExists(transMeta, BaseStepDialog_ConnectionLine_Test.INITIAL_NAME, BaseStepDialog_ConnectionLine_Test.INITIAL_HOST);
    }

    @Test
    public void edits_WhenNotRenamed() throws Exception {
        TransMeta transMeta = new TransMeta();
        transMeta.addDatabase(createDefaultDatabase());
        invokeEditConnectionListener(transMeta, BaseStepDialog_ConnectionLine_Test.INITIAL_NAME);
        assertOnlyDbExists(transMeta, BaseStepDialog_ConnectionLine_Test.INITIAL_NAME, BaseStepDialog_ConnectionLine_Test.INPUT_HOST);
    }

    @Test
    public void edits_WhenNewNameIsUnique() throws Exception {
        TransMeta transMeta = new TransMeta();
        transMeta.addDatabase(createDefaultDatabase());
        invokeEditConnectionListener(transMeta, BaseStepDialog_ConnectionLine_Test.INPUT_NAME);
        assertOnlyDbExists(transMeta, BaseStepDialog_ConnectionLine_Test.INPUT_NAME, BaseStepDialog_ConnectionLine_Test.INPUT_HOST);
    }

    @Test
    public void ignores_WhenNewNameIsUsed() throws Exception {
        TransMeta transMeta = new TransMeta();
        transMeta.addDatabase(createDefaultDatabase());
        invokeEditConnectionListener(transMeta, null);
        assertOnlyDbExists(transMeta, BaseStepDialog_ConnectionLine_Test.INITIAL_NAME, BaseStepDialog_ConnectionLine_Test.INITIAL_HOST);
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

    @Test
    public void showDbDialog_ReturnsNull_OnCancel() throws Exception {
        // null as input emulates cancelling
        test_showDbDialogUnlessCancelledOrValid_ShownOnce(null, null);
    }

    @Test
    public void showDbDialog_ReturnsInputName_WhenItIsUnique() throws Exception {
        test_showDbDialogUnlessCancelledOrValid_ShownOnce(BaseStepDialog_ConnectionLine_Test.INPUT_NAME, BaseStepDialog_ConnectionLine_Test.INPUT_NAME);
    }

    @Test
    public void showDbDialog_ReturnsInputName_WhenItIsUnique_WithSpaces() throws Exception {
        String input = (" " + (BaseStepDialog_ConnectionLine_Test.INPUT_NAME)) + " ";
        test_showDbDialogUnlessCancelledOrValid_ShownOnce(input, BaseStepDialog_ConnectionLine_Test.INPUT_NAME);
    }

    @Test
    public void showDbDialog_ReturnsExistingName_WhenNameWasNotChanged() throws Exception {
        // this is the case of editing when name was not changed (e.g., host was updated)
        test_showDbDialogUnlessCancelledOrValid_ShownOnce(BaseStepDialog_ConnectionLine_Test.INITIAL_NAME, BaseStepDialog_ConnectionLine_Test.INITIAL_NAME);
    }

    @Test
    public void showDbDialog_LoopsUntilUniqueValueIsInput() throws Exception {
        DatabaseMeta db1 = createDefaultDatabase();
        DatabaseMeta db2 = createDefaultDatabase();
        db2.setName(BaseStepDialog_ConnectionLine_Test.INPUT_NAME);
        TransMeta transMeta = new TransMeta();
        transMeta.addDatabase(db1);
        transMeta.addDatabase(db2);
        final String expectedResult = (BaseStepDialog_ConnectionLine_Test.INPUT_NAME) + "2";
        DatabaseDialog databaseDialog = Mockito.mock(DatabaseDialog.class);
        // unique value
        // duplicate in other case
        // duplicate with spaces
        // duplicate
        Mockito.when(databaseDialog.open()).thenReturn(BaseStepDialog_ConnectionLine_Test.INPUT_NAME).thenReturn(((BaseStepDialog_ConnectionLine_Test.INPUT_NAME) + " ")).thenReturn(BaseStepDialog_ConnectionLine_Test.INPUT_NAME.toUpperCase()).thenReturn(expectedResult);
        BaseStepDialog dialog = Mockito.mock(BaseStepDialog.class);
        dialog.databaseDialog = databaseDialog;
        dialog.transMeta = transMeta;
        Mockito.when(dialog.showDbDialogUnlessCancelledOrValid(BaseStepDialog_ConnectionLine_Test.anyDbMeta(), BaseStepDialog_ConnectionLine_Test.anyDbMeta())).thenCallRealMethod();
        Mockito.when(dialog.getDatabaseDialog(ArgumentMatchers.any(Shell.class))).thenCallRealMethod();
        // try to rename db1 ("qwerty")
        String result = dialog.showDbDialogUnlessCancelledOrValid(((DatabaseMeta) (db1.clone())), db1);
        Assert.assertEquals(expectedResult, result);
        // database dialog should be shown four times
        Mockito.verify(databaseDialog, Mockito.times(4)).open();
        // and the error message should be shown three times
        Mockito.verify(dialog, Mockito.times(3)).showDbExistsDialog(BaseStepDialog_ConnectionLine_Test.anyDbMeta());
    }
}

