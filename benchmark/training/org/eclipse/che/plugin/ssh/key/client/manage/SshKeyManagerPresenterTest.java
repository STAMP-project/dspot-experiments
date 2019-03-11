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
package org.eclipse.che.plugin.ssh.key.client.manage;


import SshKeyManagerPresenter.VCS_SSH_SERVICE;
import StatusNotification.Status;
import StatusNotification.Status.FAIL;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.api.promises.client.js.JsPromiseError;
import org.eclipse.che.api.ssh.shared.dto.SshPairDto;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.notification.StatusNotification.DisplayMode;
import org.eclipse.che.ide.api.ssh.SshServiceClient;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.ui.dialogs.CancelCallback;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.ide.ui.dialogs.confirm.ConfirmCallback;
import org.eclipse.che.ide.ui.dialogs.confirm.ConfirmDialog;
import org.eclipse.che.ide.ui.dialogs.input.InputCallback;
import org.eclipse.che.ide.ui.dialogs.input.InputDialog;
import org.eclipse.che.ide.ui.dialogs.input.InputValidator.Violation;
import org.eclipse.che.ide.ui.dialogs.message.MessageDialog;
import org.eclipse.che.plugin.ssh.key.client.SshKeyLocalizationConstant;
import org.eclipse.che.plugin.ssh.key.client.SshResources;
import org.eclipse.che.plugin.ssh.key.client.upload.UploadSshKeyPresenter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Testing {@link SshKeyManagerPresenter} functionality.
 *
 * @author Roman Nikitenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class SshKeyManagerPresenterTest {
    public static final String GITHUB_HOST = "github.com";

    @Captor
    private ArgumentCaptor<AsyncCallback<Void>> asyncCallbackCaptor;

    @Captor
    private ArgumentCaptor<ConfirmCallback> confirmCallbackCaptor;

    @Captor
    private ArgumentCaptor<CancelCallback> cancelCallbackCaptor;

    @Captor
    private ArgumentCaptor<InputCallback> inputCallbackCaptor;

    @Captor
    private ArgumentCaptor<Operation<Void>> operationVoidCapture;

    @Captor
    private ArgumentCaptor<Operation<List<SshPairDto>>> operationSshPairDTOsCapture;

    @Captor
    private ArgumentCaptor<Operation<SshPairDto>> operationSshPairDTOCapture;

    @Captor
    private ArgumentCaptor<Operation<PromiseError>> operationErrorCapture;

    private Promise<Void> voidPromise;

    private Promise<SshPairDto> sshPairDTOPromise;

    private Promise<List<SshPairDto>> sshPairDTOsPromise;

    @Mock
    private AppContext appContext;

    @Mock
    private DtoUnmarshallerFactory dtoUnmarshallerFactory;

    @Mock
    private DialogFactory dialogFactory;

    @Mock
    private SshKeyManagerView view;

    @Mock
    private SshServiceClient service;

    @Mock
    private ShowSshKeyView showSshKeyView;

    @Mock
    private SshKeyLocalizationConstant constant;

    @Mock
    private SshResources resources;

    @Mock
    private UploadSshKeyPresenter uploadSshKeyPresenter;

    @Mock
    private NotificationManager notificationManager;

    @Mock
    private InputDialog inputDialog;

    @InjectMocks
    private SshKeyManagerPresenter presenter;

    @Mock
    SshPairDto sshPairDto;

    @Test
    public void testGo() {
        AcceptsOneWidget container = Mockito.mock(AcceptsOneWidget.class);
        presenter.go(container);
        Mockito.verify(service).getPairs(ArgumentMatchers.eq(VCS_SSH_SERVICE));
        Mockito.verify(container).setWidget(ArgumentMatchers.eq(view));
    }

    @Test
    public void testOnViewClickedWhenGetPublicKeyIsSuccess() {
        Mockito.when(sshPairDto.getPublicKey()).thenReturn("publicKey");
        Mockito.when(sshPairDto.getName()).thenReturn("name");
        MessageDialog messageDialog = Mockito.mock(MessageDialog.class);
        Mockito.when(dialogFactory.createMessageDialog(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ((ConfirmCallback) (ArgumentMatchers.anyObject())))).thenReturn(messageDialog);
        presenter.onViewClicked(sshPairDto);
        Mockito.verify(showSshKeyView).show("name", "publicKey");
    }

    @Test
    public void testOnDeleteClickedWhenDeleteKeyConfirmed() {
        Mockito.when(sshPairDto.getService()).thenReturn(VCS_SSH_SERVICE);
        Mockito.when(sshPairDto.getName()).thenReturn(SshKeyManagerPresenterTest.GITHUB_HOST);
        SafeHtml safeHtml = Mockito.mock(SafeHtml.class);
        ConfirmDialog confirmDialog = Mockito.mock(ConfirmDialog.class);
        Mockito.when(constant.deleteSshKeyQuestion(ArgumentMatchers.anyString())).thenReturn(safeHtml);
        Mockito.when(safeHtml.asString()).thenReturn("");
        Mockito.when(dialogFactory.createConfirmDialog(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(ConfirmCallback.class), ArgumentMatchers.nullable(CancelCallback.class))).thenReturn(confirmDialog);
        presenter.onDeleteClicked(sshPairDto);
        Mockito.verify(dialogFactory).createConfirmDialog(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), confirmCallbackCaptor.capture(), ((CancelCallback) (ArgumentMatchers.anyObject())));
        ConfirmCallback confirmCallback = confirmCallbackCaptor.getValue();
        confirmCallback.accepted();
        Mockito.verify(confirmDialog).show();
        Mockito.verify(service).deletePair(ArgumentMatchers.eq(VCS_SSH_SERVICE), ArgumentMatchers.eq(SshKeyManagerPresenterTest.GITHUB_HOST));
    }

    @Test
    public void testOnDeleteClickedWhenDeleteKeyCanceled() {
        SafeHtml safeHtml = Mockito.mock(SafeHtml.class);
        ConfirmDialog confirmDialog = Mockito.mock(ConfirmDialog.class);
        Mockito.when(constant.deleteSshKeyQuestion(ArgumentMatchers.nullable(String.class))).thenReturn(safeHtml);
        Mockito.when(safeHtml.asString()).thenReturn("");
        Mockito.when(dialogFactory.createConfirmDialog(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(ConfirmCallback.class), ArgumentMatchers.nullable(CancelCallback.class))).thenReturn(confirmDialog);
        presenter.onDeleteClicked(sshPairDto);
        Mockito.verify(dialogFactory).createConfirmDialog(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(ConfirmCallback.class), cancelCallbackCaptor.capture());
        CancelCallback cancelCallback = cancelCallbackCaptor.getValue();
        cancelCallback.cancelled();
        Mockito.verify(confirmDialog).show();
        Mockito.verify(service, Mockito.never()).deletePair(ArgumentMatchers.eq(VCS_SSH_SERVICE), ArgumentMatchers.anyString());
    }

    @Test
    public void testOnDeleteClickedWhenDeleteKeyIsSuccess() throws OperationException {
        Mockito.when(sshPairDto.getService()).thenReturn(VCS_SSH_SERVICE);
        Mockito.when(sshPairDto.getName()).thenReturn(SshKeyManagerPresenterTest.GITHUB_HOST);
        SafeHtml safeHtml = Mockito.mock(SafeHtml.class);
        ConfirmDialog confirmDialog = Mockito.mock(ConfirmDialog.class);
        Mockito.when(constant.deleteSshKeyQuestion(ArgumentMatchers.anyString())).thenReturn(safeHtml);
        Mockito.when(safeHtml.asString()).thenReturn("");
        Mockito.when(dialogFactory.createConfirmDialog(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(ConfirmCallback.class), ArgumentMatchers.nullable(CancelCallback.class))).thenReturn(confirmDialog);
        presenter.onDeleteClicked(sshPairDto);
        Mockito.verify(dialogFactory).createConfirmDialog(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), confirmCallbackCaptor.capture(), ((CancelCallback) (ArgumentMatchers.anyObject())));
        ConfirmCallback confirmCallback = confirmCallbackCaptor.getValue();
        confirmCallback.accepted();
        Mockito.verify(voidPromise).then(operationVoidCapture.capture());
        operationVoidCapture.getValue().apply(null);
        Mockito.verify(confirmDialog).show();
        Mockito.verify(service).deletePair(ArgumentMatchers.eq(VCS_SSH_SERVICE), ArgumentMatchers.eq(SshKeyManagerPresenterTest.GITHUB_HOST));
        Mockito.verify(service).getPairs(ArgumentMatchers.eq(VCS_SSH_SERVICE));
    }

    @Test
    public void testOnDeleteClickedWhenDeleteKeyIsFailure() throws OperationException {
        Mockito.when(sshPairDto.getService()).thenReturn(VCS_SSH_SERVICE);
        Mockito.when(sshPairDto.getName()).thenReturn(SshKeyManagerPresenterTest.GITHUB_HOST);
        SafeHtml safeHtml = Mockito.mock(SafeHtml.class);
        ConfirmDialog confirmDialog = Mockito.mock(ConfirmDialog.class);
        Mockito.when(constant.deleteSshKeyQuestion(ArgumentMatchers.anyString())).thenReturn(safeHtml);
        Mockito.when(safeHtml.asString()).thenReturn("");
        Mockito.when(dialogFactory.createConfirmDialog(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(ConfirmCallback.class), ArgumentMatchers.nullable(CancelCallback.class))).thenReturn(confirmDialog);
        presenter.onDeleteClicked(sshPairDto);
        Mockito.verify(dialogFactory).createConfirmDialog(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), confirmCallbackCaptor.capture(), ((CancelCallback) (ArgumentMatchers.anyObject())));
        ConfirmCallback confirmCallback = confirmCallbackCaptor.getValue();
        confirmCallback.accepted();
        Mockito.verify(voidPromise).catchError(operationErrorCapture.capture());
        operationErrorCapture.getValue().apply(JsPromiseError.create(""));
        Mockito.verify(confirmDialog).show();
        Mockito.verify(service).deletePair(ArgumentMatchers.eq(VCS_SSH_SERVICE), ArgumentMatchers.anyString());
        Mockito.verify(notificationManager).notify(ArgumentMatchers.anyString(), ArgumentMatchers.eq(FAIL), ArgumentMatchers.eq(FLOAT_MODE));
        Mockito.verify(service, Mockito.never()).getPairs(ArgumentMatchers.eq(VCS_SSH_SERVICE));
    }

    @Test
    public void testShouldRefreshKeysAfterSuccessfulDeleteKey() throws OperationException {
        Mockito.when(sshPairDto.getService()).thenReturn(VCS_SSH_SERVICE);
        Mockito.when(sshPairDto.getName()).thenReturn(SshKeyManagerPresenterTest.GITHUB_HOST);
        SafeHtml safeHtml = Mockito.mock(SafeHtml.class);
        ConfirmDialog confirmDialog = Mockito.mock(ConfirmDialog.class);
        List<SshPairDto> sshPairDtoArray = new ArrayList<>();
        Mockito.when(constant.deleteSshKeyQuestion(ArgumentMatchers.anyString())).thenReturn(safeHtml);
        Mockito.when(safeHtml.asString()).thenReturn("");
        Mockito.when(dialogFactory.createConfirmDialog(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(ConfirmCallback.class), ArgumentMatchers.nullable(CancelCallback.class))).thenReturn(confirmDialog);
        presenter.onDeleteClicked(sshPairDto);
        Mockito.verify(dialogFactory).createConfirmDialog(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), confirmCallbackCaptor.capture(), ArgumentMatchers.nullable(CancelCallback.class));
        ConfirmCallback confirmCallback = confirmCallbackCaptor.getValue();
        confirmCallback.accepted();
        Mockito.verify(voidPromise).then(operationVoidCapture.capture());
        operationVoidCapture.getValue().apply(null);
        Mockito.verify(sshPairDTOsPromise).then(operationSshPairDTOsCapture.capture());
        operationSshPairDTOsCapture.getValue().apply(sshPairDtoArray);
        Mockito.verify(confirmDialog).show();
        Mockito.verify(service).deletePair(ArgumentMatchers.eq(VCS_SSH_SERVICE), ArgumentMatchers.eq(SshKeyManagerPresenterTest.GITHUB_HOST));
        Mockito.verify(service).getPairs(ArgumentMatchers.eq(VCS_SSH_SERVICE));
        Mockito.verify(view).setPairs(ArgumentMatchers.eq(sshPairDtoArray));
    }

    @Test
    public void testFailedRefreshKeysAfterSuccessfulDeleteKey() throws OperationException {
        Mockito.when(sshPairDto.getService()).thenReturn(VCS_SSH_SERVICE);
        Mockito.when(sshPairDto.getName()).thenReturn(SshKeyManagerPresenterTest.GITHUB_HOST);
        SafeHtml safeHtml = Mockito.mock(SafeHtml.class);
        ConfirmDialog confirmDialog = Mockito.mock(ConfirmDialog.class);
        List<SshPairDto> sshPairDtoArray = new ArrayList<>();
        Mockito.when(constant.deleteSshKeyQuestion(ArgumentMatchers.anyString())).thenReturn(safeHtml);
        Mockito.when(safeHtml.asString()).thenReturn("");
        Mockito.when(dialogFactory.createConfirmDialog(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(ConfirmCallback.class), ArgumentMatchers.nullable(CancelCallback.class))).thenReturn(confirmDialog);
        presenter.onDeleteClicked(sshPairDto);
        Mockito.verify(dialogFactory).createConfirmDialog(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), confirmCallbackCaptor.capture(), ((CancelCallback) (ArgumentMatchers.anyObject())));
        ConfirmCallback confirmCallback = confirmCallbackCaptor.getValue();
        confirmCallback.accepted();
        Mockito.verify(voidPromise).then(operationVoidCapture.capture());
        operationVoidCapture.getValue().apply(null);
        Mockito.verify(sshPairDTOsPromise).catchError(operationErrorCapture.capture());
        operationErrorCapture.getValue().apply(JsPromiseError.create(""));
        Mockito.verify(confirmDialog).show();
        Mockito.verify(service).deletePair(ArgumentMatchers.eq(VCS_SSH_SERVICE), ArgumentMatchers.eq(SshKeyManagerPresenterTest.GITHUB_HOST));
        Mockito.verify(service).getPairs(ArgumentMatchers.eq(VCS_SSH_SERVICE));
        Mockito.verify(view, Mockito.never()).setPairs(ArgumentMatchers.eq(sshPairDtoArray));
        Mockito.verify(notificationManager).notify(ArgumentMatchers.nullable(String.class), ArgumentMatchers.any(Status.class), ((DisplayMode) (ArgumentMatchers.anyObject())));
    }

    @Test
    public void testShouldRefreshKeysAfterSuccessfulUploadKey() throws OperationException {
        List<SshPairDto> sshPairDtoArray = new ArrayList<>();
        presenter.onUploadClicked();
        Mockito.verify(uploadSshKeyPresenter).showDialog(asyncCallbackCaptor.capture());
        AsyncCallback<Void> asyncCallback = asyncCallbackCaptor.getValue();
        asyncCallback.onSuccess(null);
        Mockito.verify(sshPairDTOsPromise).then(operationSshPairDTOsCapture.capture());
        operationSshPairDTOsCapture.getValue().apply(sshPairDtoArray);
        Mockito.verify(service).getPairs(ArgumentMatchers.eq(VCS_SSH_SERVICE));
        Mockito.verify(view).setPairs(ArgumentMatchers.eq(sshPairDtoArray));
    }

    @Test
    public void testFailedRefreshKeysAfterSuccessfulUploadKey() throws OperationException {
        List<SshPairDto> sshPairDtoArray = new ArrayList<>();
        presenter.onUploadClicked();
        Mockito.verify(uploadSshKeyPresenter).showDialog(asyncCallbackCaptor.capture());
        AsyncCallback<Void> asyncCallback = asyncCallbackCaptor.getValue();
        asyncCallback.onSuccess(null);
        Mockito.verify(sshPairDTOsPromise).catchError(operationErrorCapture.capture());
        operationErrorCapture.getValue().apply(JsPromiseError.create(""));
        Mockito.verify(service).getPairs(ArgumentMatchers.eq(VCS_SSH_SERVICE));
        Mockito.verify(view, Mockito.never()).setPairs(ArgumentMatchers.eq(sshPairDtoArray));
    }

    @Test
    public void testOnGenerateClickedWhenUserConfirmGenerateKey() {
        Mockito.when(dialogFactory.createInputDialog(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(InputCallback.class), ArgumentMatchers.nullable(CancelCallback.class))).thenReturn(inputDialog);
        presenter.onGenerateClicked();
        Mockito.verify(dialogFactory).createInputDialog(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), inputCallbackCaptor.capture(), ((CancelCallback) (ArgumentMatchers.anyObject())));
        InputCallback inputCallback = inputCallbackCaptor.getValue();
        inputCallback.accepted(SshKeyManagerPresenterTest.GITHUB_HOST);
        Mockito.verify(service).generatePair(ArgumentMatchers.eq(VCS_SSH_SERVICE), ArgumentMatchers.eq(SshKeyManagerPresenterTest.GITHUB_HOST));
    }

    @Test
    public void testOnGenerateClickedWhenUserCancelGenerateKey() {
        Mockito.when(dialogFactory.createInputDialog(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(InputCallback.class), ArgumentMatchers.nullable(CancelCallback.class))).thenReturn(inputDialog);
        presenter.onGenerateClicked();
        Mockito.verify(dialogFactory).createInputDialog(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), ArgumentMatchers.<InputCallback>any(), cancelCallbackCaptor.capture());
        CancelCallback cancelCallback = cancelCallbackCaptor.getValue();
        cancelCallback.cancelled();
        Mockito.verify(service, Mockito.never()).generatePair(ArgumentMatchers.eq(VCS_SSH_SERVICE), ArgumentMatchers.eq(SshKeyManagerPresenterTest.GITHUB_HOST));
    }

    @Test
    public void testOnGenerateClickedWhenGenerateKeyIsFailed() throws OperationException {
        Mockito.when(dialogFactory.createInputDialog(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(InputCallback.class), ArgumentMatchers.nullable(CancelCallback.class))).thenReturn(inputDialog);
        presenter.onGenerateClicked();
        Mockito.verify(dialogFactory).createInputDialog(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), inputCallbackCaptor.capture(), cancelCallbackCaptor.capture());
        InputCallback inputCallback = inputCallbackCaptor.getValue();
        inputCallback.accepted(SshKeyManagerPresenterTest.GITHUB_HOST);
        Mockito.verify(sshPairDTOPromise).catchError(operationErrorCapture.capture());
        operationErrorCapture.getValue().apply(JsPromiseError.create(""));
        Mockito.verify(service).generatePair(ArgumentMatchers.eq(VCS_SSH_SERVICE), ArgumentMatchers.eq(SshKeyManagerPresenterTest.GITHUB_HOST));
        Mockito.verify(service, Mockito.never()).getPairs(ArgumentMatchers.eq(VCS_SSH_SERVICE));
        Mockito.verify(view, Mockito.never()).setPairs(((List<SshPairDto>) (ArgumentMatchers.anyObject())));
        Mockito.verify(notificationManager).notify(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), ArgumentMatchers.any(Status.class), ((DisplayMode) (ArgumentMatchers.anyObject())));
    }

    @Test
    public void testShouldRefreshKeysAfterSuccessfulGenerateKey() throws OperationException {
        List<SshPairDto> sshPairDtoArray = new ArrayList<>();
        Mockito.when(dialogFactory.createInputDialog(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(InputCallback.class), ArgumentMatchers.nullable(CancelCallback.class))).thenReturn(inputDialog);
        presenter.onGenerateClicked();
        Mockito.verify(dialogFactory).createInputDialog(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), inputCallbackCaptor.capture(), cancelCallbackCaptor.capture());
        InputCallback inputCallback = inputCallbackCaptor.getValue();
        inputCallback.accepted(SshKeyManagerPresenterTest.GITHUB_HOST);
        Mockito.verify(sshPairDTOPromise).then(operationSshPairDTOCapture.capture());
        operationSshPairDTOCapture.getValue().apply(null);
        Mockito.verify(sshPairDTOsPromise).then(operationSshPairDTOsCapture.capture());
        operationSshPairDTOsCapture.getValue().apply(sshPairDtoArray);
        Mockito.verify(service).generatePair(ArgumentMatchers.eq(VCS_SSH_SERVICE), ArgumentMatchers.eq(SshKeyManagerPresenterTest.GITHUB_HOST));
        Mockito.verify(service).getPairs(ArgumentMatchers.eq(VCS_SSH_SERVICE));
        Mockito.verify(view).setPairs(ArgumentMatchers.eq(sshPairDtoArray));
    }

    @Test
    public void shouldReturnErrorOnHostNameWithHttpProtocolValidation() throws OperationException {
        String invalidHostname = "http://host.xz";
        Mockito.when(constant.invalidHostName()).thenReturn("ErrorMessage");
        String errorMessage = validate(invalidHostname).getMessage();
        Assert.assertEquals("ErrorMessage", errorMessage);
    }

    @Test
    public void shouldReturnErrorOnHostNameWithHttpsProtocolValidation() throws OperationException {
        String invalidHostname = "https://host.xz";
        Mockito.when(constant.invalidHostName()).thenReturn("ErrorMessage");
        String errorMessage = validate(invalidHostname).getMessage();
        Assert.assertEquals("ErrorMessage", errorMessage);
    }

    @Test
    public void shouldReturnErrorOnHostNameWithPortValidation() throws OperationException {
        String invalidHostname = "host:5005";
        Mockito.when(constant.invalidHostName()).thenReturn("ErrorMessage");
        String errorMessage = validate(invalidHostname).getMessage();
        Assert.assertEquals("ErrorMessage", errorMessage);
    }

    @Test
    public void shouldReturnErrorOnHostNameThatStartsWithDotValidation() throws OperationException {
        String invalidHostname = ".host.com";
        Mockito.when(constant.invalidHostName()).thenReturn("ErrorMessage");
        String errorMessage = validate(invalidHostname).getMessage();
        Assert.assertEquals("ErrorMessage", errorMessage);
    }

    @Test
    public void shouldReturnErrorOnHostNameThatStartsWithDashValidation() throws OperationException {
        String invalidHostname = "-host.com";
        Mockito.when(constant.invalidHostName()).thenReturn("ErrorMessage");
        String errorMessage = validate(invalidHostname).getMessage();
        Assert.assertEquals("ErrorMessage", errorMessage);
    }

    @Test
    public void shouldReturnErrorOnHostNameThatEndsOnDotValidation() throws OperationException {
        String invalidHostname = "host.com.";
        Mockito.when(constant.invalidHostName()).thenReturn("ErrorMessage");
        String errorMessage = validate(invalidHostname).getMessage();
        Assert.assertEquals("ErrorMessage", errorMessage);
    }

    @Test
    public void shouldReturnErrorOnHostNameThatEndsOnDashValidation() throws OperationException {
        String invalidHostname = "host.com-";
        Mockito.when(constant.invalidHostName()).thenReturn("ErrorMessage");
        String errorMessage = validate(invalidHostname).getMessage();
        Assert.assertEquals("ErrorMessage", errorMessage);
    }

    @Test
    public void shouldReturnErrorOnHostNameWithDotAndDashTogetherValidation() throws OperationException {
        String invalidHostname = "host.-com";
        Mockito.when(constant.invalidHostName()).thenReturn("ErrorMessage");
        String errorMessage = validate(invalidHostname).getMessage();
        Assert.assertEquals("ErrorMessage", errorMessage);
    }

    @Test
    public void shouldReturnNullOnValidHostNameValidation() throws OperationException {
        String validHostname = "hostname.com";
        Violation violation = ((org.eclipse.che.ide.ui.dialogs.input.InputValidator) (presenter.hostNameValidator)).validate(validHostname);
        Assert.assertNull(violation);
    }

    @Test
    public void shouldReturnNullOnHostNameWithDotAndDashValidation() throws OperationException {
        String validHostname = "host-name.com";
        Violation violation = ((org.eclipse.che.ide.ui.dialogs.input.InputValidator) (presenter.hostNameValidator)).validate(validHostname);
        Assert.assertNull(violation);
    }

    @Test
    public void shouldReturnNullOnHostNameWithSeveralDotsAndDashesValidation() throws OperationException {
        String validHostname = "ho-st.na-me.com";
        Violation violation = ((org.eclipse.che.ide.ui.dialogs.input.InputValidator) (presenter.hostNameValidator)).validate(validHostname);
        Assert.assertNull(violation);
    }
}

