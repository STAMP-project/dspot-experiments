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
package org.pentaho.di.ui.trans.steps.groupby;


import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TableItem;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.pentaho.di.ui.core.widget.TableView;


public class GroupByDialogTest {
    @Mock
    private TableView tableView = Mockito.mock(TableView.class);

    @Mock
    private Button allRowsCheckBox = Mockito.mock(Button.class);

    @Mock
    private GroupByDialog dialog = Mockito.mock(GroupByDialog.class);

    @Mock
    private TableItem tableItem = Mockito.mock(TableItem.class);

    @Test
    public void updateAllRowsCheckbox_trueTest() {
        Mockito.doReturn("CUM_SUM").when(tableItem).getText(ArgumentMatchers.anyInt());
        dialog.updateAllRowsCheckbox(tableView, allRowsCheckBox, true);
        Mockito.verify(allRowsCheckBox, Mockito.times(1)).setSelection(true);
        Mockito.verify(allRowsCheckBox, Mockito.times(1)).setEnabled(false);
    }

    @Test
    public void updateAllRowsCheckbox_falseTest() {
        Mockito.doReturn("ANOTHER_VALUE").when(tableItem).getText(ArgumentMatchers.anyInt());
        dialog.updateAllRowsCheckbox(tableView, allRowsCheckBox, true);
        Mockito.verify(allRowsCheckBox, Mockito.times(1)).setEnabled(true);
    }
}

