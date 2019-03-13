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
package org.eclipse.che.plugin.debugger.ide.debug.dialogs.changevalue;


import java.util.ArrayList;
import org.eclipse.che.api.debug.shared.dto.SimpleValueDto;
import org.eclipse.che.api.debug.shared.dto.VariableDto;
import org.eclipse.che.api.debug.shared.dto.VariablePathDto;
import org.eclipse.che.api.debug.shared.model.MutableVariable;
import org.eclipse.che.api.debug.shared.model.Variable;
import org.eclipse.che.ide.debug.Debugger;
import org.eclipse.che.ide.debug.DebuggerManager;
import org.eclipse.che.plugin.debugger.ide.BaseTest;
import org.eclipse.che.plugin.debugger.ide.debug.DebuggerPresenter;
import org.eclipse.che.plugin.debugger.ide.debug.dialogs.DebuggerDialogFactory;
import org.eclipse.che.plugin.debugger.ide.debug.dialogs.common.TextAreaDialogView;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Testing {@link ChangeValuePresenter} functionality.
 *
 * @author Artem Zatsarynnyi
 */
public class ChangeValuePresenterTest extends BaseTest {
    private static final String VAR_VALUE = "var_value";

    private static final String VAR_NAME = "var_name";

    private static final String EMPTY_VALUE = "";

    @Mock
    private TextAreaDialogView view;

    @Mock
    private DebuggerManager debuggerManager;

    @Mock
    private DebuggerPresenter debuggerPresenter;

    @Mock
    private DebuggerDialogFactory dialogFactory;

    private ChangeValuePresenter presenter;

    @Mock
    private VariableDto var;

    @Mock
    private VariablePathDto varPath;

    @Mock
    private Debugger debugger;

    @Mock
    private MutableVariable variable;

    @Mock
    private VariablePathDto variablePathDto;

    @Mock
    private SimpleValueDto simpleValueDto;

    @Test
    public void shouldShowDialog() throws Exception {
        Mockito.when(debuggerPresenter.getSelectedVariable()).thenReturn(variable);
        Mockito.when(variable.getValue()).thenReturn(simpleValueDto);
        presenter.showDialog();
        Mockito.verify(debuggerPresenter).getSelectedVariable();
        Mockito.verify(view).setValueTitle(constants.changeValueViewExpressionFieldTitle(ChangeValuePresenterTest.VAR_NAME));
        Mockito.verify(view).setValue(ChangeValuePresenterTest.VAR_VALUE);
        Mockito.verify(view).focusInValueField();
        Mockito.verify(view).selectAllText();
        Mockito.verify(view).setEnableChangeButton(ArgumentMatchers.eq(BaseTest.DISABLE_BUTTON));
        Mockito.verify(view).showDialog();
    }

    @Test
    public void shouldCloseDialogOnCancelClicked() throws Exception {
        presenter.onCancelClicked();
        Mockito.verify(view).close();
    }

    @Test
    public void shouldDisableChangeButtonIfNoValue() throws Exception {
        Mockito.when(view.getValue()).thenReturn(ChangeValuePresenterTest.EMPTY_VALUE);
        presenter.onValueChanged();
        Mockito.verify(view).setEnableChangeButton(ArgumentMatchers.eq(BaseTest.DISABLE_BUTTON));
    }

    @Test
    public void shouldEnableChangeButtonIfValueNotEmpty() throws Exception {
        Mockito.when(view.getValue()).thenReturn(ChangeValuePresenterTest.VAR_VALUE);
        presenter.onValueChanged();
        Mockito.verify(view).setEnableChangeButton(ArgumentMatchers.eq((!(BaseTest.DISABLE_BUTTON))));
    }

    @Test
    public void testChangeValueRequest() throws Exception {
        Mockito.when(debuggerPresenter.getSelectedVariable()).thenReturn(variable);
        Mockito.when(debuggerManager.getActiveDebugger()).thenReturn(debugger);
        Mockito.when(view.getValue()).thenReturn(ChangeValuePresenterTest.VAR_VALUE);
        Mockito.when(variable.getVariablePath()).thenReturn(variablePathDto);
        Mockito.when(variable.getValue()).thenReturn(Mockito.mock(SimpleValueDto.class));
        Mockito.when(variablePathDto.getPath()).thenReturn(new ArrayList());
        presenter.showDialog();
        presenter.onAgreeClicked();
        Mockito.verify(debugger).setValue(ArgumentMatchers.any(Variable.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt());
        Mockito.verify(view).close();
    }
}

