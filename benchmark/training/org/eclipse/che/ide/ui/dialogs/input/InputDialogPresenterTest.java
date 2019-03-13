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
package org.eclipse.che.ide.ui.dialogs.input;


import InputValidator.Violation;
import org.eclipse.che.ide.ui.UILocalizationConstant;
import org.eclipse.che.ide.ui.dialogs.BaseTest;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Testing {@link InputDialogPresenter} functionality.
 *
 * @author Artem Zatsarynnyi
 * @author Roman Nikitenko
 */
public class InputDialogPresenterTest extends BaseTest {
    private static final String CORRECT_INPUT_VALUE = "testInputValue";

    private static final String INCORRECT_INPUT_VALUE = "test input value";

    private static final String ERROR_MESSAGE = "ERROR";

    @Mock
    UILocalizationConstant localizationConstant;

    @Mock
    private InputDialogView view;

    private InputDialogPresenter presenter;

    @Test
    public void shouldCallCallbackOnCanceled() throws Exception {
        presenter.cancelled();
        Mockito.verify(view).closeDialog();
        Mockito.verify(cancelCallback).cancelled();
    }

    @Test
    public void shouldNotCallCallbackOnCanceled() throws Exception {
        presenter = new InputDialogPresenter(view, BaseTest.TITLE, BaseTest.MESSAGE, inputCallback, null, localizationConstant);
        presenter.cancelled();
        Mockito.verify(view).closeDialog();
        Mockito.verify(cancelCallback, Mockito.never()).cancelled();
    }

    @Test
    public void shouldCallCallbackOnAccepted() throws Exception {
        presenter.accepted();
        Mockito.verify(view).closeDialog();
        Mockito.verify(view).getValue();
        Mockito.verify(inputCallback).accepted(ArgumentMatchers.nullable(String.class));
    }

    @Test
    public void shouldNotCallCallbackOnAccepted() throws Exception {
        presenter = new InputDialogPresenter(view, BaseTest.TITLE, BaseTest.MESSAGE, null, cancelCallback, localizationConstant);
        presenter.accepted();
        Mockito.verify(view).closeDialog();
        Mockito.verify(inputCallback, Mockito.never()).accepted(ArgumentMatchers.anyString());
    }

    @Test
    public void shouldShowView() throws Exception {
        Mockito.when(view.getValue()).thenReturn(InputDialogPresenterTest.CORRECT_INPUT_VALUE);
        presenter.show();
        Mockito.verify(view).showDialog();
    }

    @Test
    public void shouldShowErrorHintWhenEmptyValue() throws Exception {
        Mockito.reset(view);
        Mockito.when(view.getValue()).thenReturn("");
        presenter.inputValueChanged();
        Mockito.verify(view).showErrorHint(ArgumentMatchers.eq(""));
        Mockito.verify(view, Mockito.never()).hideErrorHint();
        Mockito.verify(view, Mockito.never()).setValue(ArgumentMatchers.anyString());
    }

    @Test
    public void shouldShowErrorHintWhenValueIsIncorrect() throws Exception {
        Mockito.reset(view);
        Mockito.when(view.getValue()).thenReturn(InputDialogPresenterTest.INCORRECT_INPUT_VALUE);
        InputValidator inputValidator = Mockito.mock(InputValidator.class);
        InputValidator.Violation violation = Mockito.mock(Violation.class);
        Mockito.when(inputValidator.validate(InputDialogPresenterTest.INCORRECT_INPUT_VALUE)).thenReturn(violation);
        Mockito.when(violation.getMessage()).thenReturn(InputDialogPresenterTest.ERROR_MESSAGE);
        presenter.withValidator(inputValidator);
        presenter.inputValueChanged();
        Mockito.verify(view).showErrorHint(ArgumentMatchers.anyString());
        Mockito.verify(view, Mockito.never()).hideErrorHint();
        Mockito.verify(view, Mockito.never()).setValue(ArgumentMatchers.anyString());
    }

    @Test
    public void shouldNotShowErrorHintWhenViolationHasCorrectValue() throws Exception {
        Mockito.reset(view);
        Mockito.when(view.getValue()).thenReturn(InputDialogPresenterTest.INCORRECT_INPUT_VALUE);
        InputValidator inputValidator = Mockito.mock(InputValidator.class);
        InputValidator.Violation violation = Mockito.mock(Violation.class);
        Mockito.when(inputValidator.validate(InputDialogPresenterTest.INCORRECT_INPUT_VALUE)).thenReturn(violation);
        Mockito.when(violation.getMessage()).thenReturn(null);
        Mockito.when(violation.getCorrectedValue()).thenReturn(InputDialogPresenterTest.CORRECT_INPUT_VALUE);
        presenter.withValidator(inputValidator);
        presenter.inputValueChanged();
        Mockito.verify(view, Mockito.never()).showErrorHint(ArgumentMatchers.anyString());
        Mockito.verify(view).hideErrorHint();
        Mockito.verify(view).setValue(ArgumentMatchers.eq(InputDialogPresenterTest.CORRECT_INPUT_VALUE));
    }

    @Test
    public void onEnterClickedWhenOkButtonInFocus() throws Exception {
        Mockito.reset(view);
        Mockito.when(view.isOkButtonInFocus()).thenReturn(true);
        presenter.onEnterClicked();
        Mockito.verify(view, Mockito.never()).showErrorHint(ArgumentMatchers.anyString());
        Mockito.verify(view).closeDialog();
        Mockito.verify(inputCallback).accepted(ArgumentMatchers.nullable(String.class));
    }

    @Test
    public void onEnterClickedWhenCancelButtonInFocus() throws Exception {
        Mockito.reset(view);
        Mockito.when(view.isCancelButtonInFocus()).thenReturn(true);
        presenter.onEnterClicked();
        Mockito.verify(view, Mockito.never()).showErrorHint(ArgumentMatchers.anyString());
        Mockito.verify(inputCallback, Mockito.never()).accepted(ArgumentMatchers.anyString());
        Mockito.verify(view).closeDialog();
        Mockito.verify(cancelCallback).cancelled();
    }

    @Test
    public void onEnterClickedWhenInputValueIsCorrect() throws Exception {
        Mockito.reset(view);
        Mockito.when(view.getValue()).thenReturn(InputDialogPresenterTest.CORRECT_INPUT_VALUE);
        InputValidator inputValidator = Mockito.mock(InputValidator.class);
        Mockito.when(inputValidator.validate(InputDialogPresenterTest.CORRECT_INPUT_VALUE)).thenReturn(null);
        presenter.withValidator(inputValidator);
        presenter.onEnterClicked();
        Mockito.verify(view, Mockito.never()).showErrorHint(ArgumentMatchers.anyString());
        Mockito.verify(view).hideErrorHint();
        Mockito.verify(view).closeDialog();
        Mockito.verify(inputCallback).accepted(ArgumentMatchers.eq(InputDialogPresenterTest.CORRECT_INPUT_VALUE));
    }

    @Test
    public void onEnterClickedWhenInputValueIsIncorrect() throws Exception {
        Mockito.reset(view);
        Mockito.when(view.getValue()).thenReturn(InputDialogPresenterTest.INCORRECT_INPUT_VALUE);
        InputValidator inputValidator = Mockito.mock(InputValidator.class);
        InputValidator.Violation violation = Mockito.mock(Violation.class);
        Mockito.when(inputValidator.validate(InputDialogPresenterTest.INCORRECT_INPUT_VALUE)).thenReturn(violation);
        Mockito.when(violation.getMessage()).thenReturn(InputDialogPresenterTest.ERROR_MESSAGE);
        presenter.withValidator(inputValidator);
        presenter.onEnterClicked();
        Mockito.verify(view).showErrorHint(ArgumentMatchers.anyString());
        Mockito.verify(view, Mockito.never()).hideErrorHint();
        Mockito.verify(view, Mockito.never()).setValue(ArgumentMatchers.anyString());
        Mockito.verify(inputCallback, Mockito.never()).accepted(ArgumentMatchers.anyString());
    }
}

