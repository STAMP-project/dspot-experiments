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
package org.eclipse.che.plugin.debugger.ide;


import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.ide.debug.Debugger;
import org.eclipse.che.ide.debug.DebuggerManager;
import org.eclipse.che.plugin.debugger.ide.debug.DebuggerPresenter;
import org.eclipse.che.plugin.debugger.ide.debug.expression.EvaluateExpressionPresenter;
import org.eclipse.che.plugin.debugger.ide.debug.expression.EvaluateExpressionView;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Testing {@link EvaluateExpressionPresenter} functionality.
 *
 * @author Artem Zatsarynnyi
 */
public class EvaluateExpressionTest extends BaseTest {
    private static final String EXPRESSION = "expression";

    private static final String EMPTY_EXPRESSION = "";

    private static final String FAIL_REASON = "reason";

    private static final long THREAD_ID = 1;

    private static final int FRAME_INDEX = 0;

    @Mock
    private EvaluateExpressionView view;

    @Mock
    private DebuggerManager debuggerManager;

    @Mock
    private Debugger debugger;

    @Mock
    private Promise<String> promise;

    @Mock
    private PromiseError promiseError;

    @Mock
    private DebuggerPresenter debuggerPresenter;

    @Captor
    private ArgumentCaptor<Operation<PromiseError>> errorCaptor;

    @InjectMocks
    private EvaluateExpressionPresenter presenter;

    @Test
    public void shouldShowDialog() throws Exception {
        presenter.showDialog();
        Mockito.verify(view).setResult(ArgumentMatchers.eq(EvaluateExpressionTest.EMPTY_EXPRESSION));
        Mockito.verify(view).setEnableEvaluateButton(ArgumentMatchers.eq(BaseTest.DISABLE_BUTTON));
        Mockito.verify(view).showDialog();
        Mockito.verify(view).focusInExpressionField();
    }

    @Test
    public void shouldCloseDialog() throws Exception {
        presenter.closeDialog();
        Mockito.verify(view).close();
    }

    @Test
    public void shouldCloseDialogOnCloseClicked() throws Exception {
        presenter.onCloseClicked();
        Mockito.verify(view).close();
    }

    @Test
    public void shouldDisableEvaluateButtonIfNoExpression() throws Exception {
        Mockito.when(view.getExpression()).thenReturn(EvaluateExpressionTest.EMPTY_EXPRESSION);
        presenter.onExpressionValueChanged();
        Mockito.verify(view).setEnableEvaluateButton(ArgumentMatchers.eq(BaseTest.DISABLE_BUTTON));
    }

    @Test
    public void shouldEnableEvaluateButtonIfExpressionNotEmpty() throws Exception {
        Mockito.when(view.getExpression()).thenReturn(EvaluateExpressionTest.EXPRESSION);
        presenter.onExpressionValueChanged();
        Mockito.verify(view).setEnableEvaluateButton(ArgumentMatchers.eq((!(BaseTest.DISABLE_BUTTON))));
    }

    @Test
    public void testEvaluateExpressionRequestIsSuccessful() throws Exception {
        Mockito.when(debugger.evaluate(ArgumentMatchers.anyString(), ArgumentMatchers.eq(EvaluateExpressionTest.THREAD_ID), ArgumentMatchers.eq(EvaluateExpressionTest.FRAME_INDEX))).thenReturn(promise);
        Mockito.when(view.getExpression()).thenReturn(EvaluateExpressionTest.EXPRESSION);
        Mockito.when(promise.then(ArgumentMatchers.any(Operation.class))).thenReturn(promise);
        Mockito.when(debuggerManager.getActiveDebugger()).thenReturn(debugger);
        presenter.showDialog();
        presenter.onEvaluateClicked();
        Mockito.verify(view, Mockito.atLeastOnce()).setEnableEvaluateButton(ArgumentMatchers.eq(BaseTest.DISABLE_BUTTON));
        Mockito.verify(debugger).evaluate(EvaluateExpressionTest.EXPRESSION, EvaluateExpressionTest.THREAD_ID, EvaluateExpressionTest.FRAME_INDEX);
    }

    @Test
    public void testEvaluateExpressionRequestIsFailed() throws Exception {
        Mockito.when(view.getExpression()).thenReturn(EvaluateExpressionTest.EXPRESSION);
        Mockito.when(debugger.evaluate(view.getExpression(), EvaluateExpressionTest.THREAD_ID, EvaluateExpressionTest.FRAME_INDEX)).thenReturn(promise);
        Mockito.when(promise.then(((Operation) (ArgumentMatchers.anyObject())))).thenReturn(promise);
        Mockito.when(promise.catchError(ArgumentMatchers.<Operation<PromiseError>>anyObject())).thenReturn(promise);
        Mockito.when(debuggerManager.getActiveDebugger()).thenReturn(debugger);
        Mockito.when(promiseError.getMessage()).thenReturn(EvaluateExpressionTest.FAIL_REASON);
        presenter.showDialog();
        presenter.onEvaluateClicked();
        Mockito.verify(view, Mockito.atLeastOnce()).setEnableEvaluateButton(ArgumentMatchers.eq(BaseTest.DISABLE_BUTTON));
        Mockito.verify(debugger).evaluate(EvaluateExpressionTest.EXPRESSION, EvaluateExpressionTest.THREAD_ID, EvaluateExpressionTest.FRAME_INDEX);
        Mockito.verify(promise).catchError(errorCaptor.capture());
        errorCaptor.getValue().apply(promiseError);
        Mockito.verify(view).setEnableEvaluateButton(ArgumentMatchers.eq((!(BaseTest.DISABLE_BUTTON))));
        Mockito.verify(constants).evaluateExpressionFailed(EvaluateExpressionTest.FAIL_REASON);
    }
}

