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
package org.eclipse.che.plugin.github.ide.authenticator;


import SshKeyManagerPresenter.VCS_SSH_SERVICE;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwtmockito.GwtMockitoTestRunner;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.ssh.shared.dto.SshPairDto;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentUser;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.ssh.SshServiceClient;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.ide.ui.dialogs.confirm.ConfirmCallback;
import org.eclipse.che.ide.ui.dialogs.message.MessageDialog;
import org.eclipse.che.plugin.github.ide.GitHubLocalizationConstant;
import org.eclipse.che.plugin.ssh.key.client.SshKeyUploader;
import org.eclipse.che.plugin.ssh.key.client.SshKeyUploaderRegistry;
import org.eclipse.che.security.oauth.OAuthStatus;
import org.hamcrest.core.Is;
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
 * Testing {@link GitHubAuthenticatorImpl} functionality.
 *
 * @author Roman Nikitenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class GitHubAuthenticatorImplTest {
    public static final String GITHUB_HOST = "github.com";

    @Captor
    private ArgumentCaptor<AsyncCallback<Void>> generateKeyCallbackCaptor;

    @Captor
    private ArgumentCaptor<Operation<List<SshPairDto>>> operationSshPairDTOsCapture;

    @Captor
    private ArgumentCaptor<Operation<Void>> operationVoid;

    private Promise<List<SshPairDto>> sshPairDTOsPromise;

    private Promise<Void> voidPromise;

    @Mock
    private GitHubAuthenticatorView view;

    @Mock
    private SshServiceClient sshServiceClient;

    @Mock
    private DialogFactory dialogFactory;

    @Mock
    private DtoUnmarshallerFactory dtoUnmarshallerFactory;

    @Mock
    private NotificationManager notificationManager;

    @Mock
    private GitHubLocalizationConstant locale;

    @Mock
    private AppContext appContext;

    @Mock
    private SshKeyUploaderRegistry registry;

    @InjectMocks
    private GitHubAuthenticatorImpl gitHubAuthenticator;

    @Test
    public void delegateShouldBeSet() throws Exception {
        Mockito.verify(view).setDelegate(gitHubAuthenticator);
    }

    @Test
    public void dialogShouldBeShow() throws Exception {
        AsyncCallback<OAuthStatus> callback = getCallBack();
        gitHubAuthenticator.authenticate(null, callback);
        Mockito.verify(view).showDialog();
        Assert.assertThat(gitHubAuthenticator.callback, Is.is(callback));
    }

    @Test
    public void onAuthenticatedWhenGenerateKeysIsSelected() throws Exception {
        String userId = "userId";
        OAuthStatus authStatus = Mockito.mock(OAuthStatus.class);
        SshKeyUploader sshKeyUploader = Mockito.mock(SshKeyUploader.class);
        CurrentUser user = Mockito.mock(CurrentUser.class);
        Mockito.when(view.isGenerateKeysSelected()).thenReturn(true);
        Mockito.when(registry.getUploader(GitHubAuthenticatorImplTest.GITHUB_HOST)).thenReturn(sshKeyUploader);
        Mockito.when(appContext.getCurrentUser()).thenReturn(user);
        Mockito.when(user.getId()).thenReturn(userId);
        gitHubAuthenticator.onAuthenticated(authStatus);
        Mockito.verify(view).isGenerateKeysSelected();
        Mockito.verify(registry).getUploader(ArgumentMatchers.eq(GitHubAuthenticatorImplTest.GITHUB_HOST));
        Mockito.verify(appContext).getCurrentUser();
        Mockito.verify(sshKeyUploader).uploadKey(ArgumentMatchers.eq(userId), ArgumentMatchers.<AsyncCallback<Void>>anyObject());
    }

    @Test
    public void onAuthenticatedWhenGenerateKeysIsNotSelected() throws Exception {
        String userId = "userId";
        OAuthStatus authStatus = Mockito.mock(OAuthStatus.class);
        CurrentUser user = Mockito.mock(CurrentUser.class);
        Mockito.when(view.isGenerateKeysSelected()).thenReturn(false);
        Mockito.when(appContext.getCurrentUser()).thenReturn(user);
        Mockito.when(user.getId()).thenReturn(userId);
        gitHubAuthenticator.authenticate(null, getCallBack());
        gitHubAuthenticator.onAuthenticated(authStatus);
        Mockito.verify(view).isGenerateKeysSelected();
        Mockito.verifyNoMoreInteractions(registry);
    }

    @Test
    public void onAuthenticatedWhenGenerateKeysIsSuccess() throws Exception {
        String userId = "userId";
        OAuthStatus authStatus = Mockito.mock(OAuthStatus.class);
        SshKeyUploader keyProvider = Mockito.mock(SshKeyUploader.class);
        CurrentUser user = Mockito.mock(CurrentUser.class);
        Mockito.when(view.isGenerateKeysSelected()).thenReturn(true);
        Mockito.when(registry.getUploader(GitHubAuthenticatorImplTest.GITHUB_HOST)).thenReturn(keyProvider);
        Mockito.when(appContext.getCurrentUser()).thenReturn(user);
        Mockito.when(user.getId()).thenReturn(userId);
        gitHubAuthenticator.authenticate(null, getCallBack());
        gitHubAuthenticator.onAuthenticated(authStatus);
        Mockito.verify(keyProvider).uploadKey(ArgumentMatchers.eq(userId), generateKeyCallbackCaptor.capture());
        AsyncCallback<Void> generateKeyCallback = generateKeyCallbackCaptor.getValue();
        generateKeyCallback.onSuccess(null);
        Mockito.verify(view).isGenerateKeysSelected();
        Mockito.verify(registry).getUploader(ArgumentMatchers.eq(GitHubAuthenticatorImplTest.GITHUB_HOST));
        Mockito.verify(appContext).getCurrentUser();
        Mockito.verify(notificationManager).notify(ArgumentMatchers.nullable(String.class), ArgumentMatchers.eq(SUCCESS), ArgumentMatchers.eq(FLOAT_MODE));
    }

    @Test
    public void onAuthenticatedWhenGenerateKeysIsFailure() throws Exception {
        String userId = "userId";
        OAuthStatus authStatus = Mockito.mock(OAuthStatus.class);
        SshKeyUploader keyProvider = Mockito.mock(SshKeyUploader.class);
        CurrentUser user = Mockito.mock(CurrentUser.class);
        MessageDialog messageDialog = Mockito.mock(MessageDialog.class);
        Mockito.when(view.isGenerateKeysSelected()).thenReturn(true);
        Mockito.when(registry.getUploader(GitHubAuthenticatorImplTest.GITHUB_HOST)).thenReturn(keyProvider);
        Mockito.when(appContext.getCurrentUser()).thenReturn(user);
        Mockito.when(user.getId()).thenReturn(userId);
        Mockito.when(dialogFactory.createMessageDialog(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), ArgumentMatchers.<ConfirmCallback>anyObject())).thenReturn(messageDialog);
        gitHubAuthenticator.authenticate(null, getCallBack());
        gitHubAuthenticator.onAuthenticated(authStatus);
        Mockito.verify(keyProvider).uploadKey(ArgumentMatchers.eq(userId), generateKeyCallbackCaptor.capture());
        AsyncCallback<Void> generateKeyCallback = generateKeyCallbackCaptor.getValue();
        generateKeyCallback.onFailure(new Exception(""));
        Mockito.verify(view).isGenerateKeysSelected();
        Mockito.verify(registry).getUploader(ArgumentMatchers.eq(GitHubAuthenticatorImplTest.GITHUB_HOST));
        Mockito.verify(appContext).getCurrentUser();
        Mockito.verify(dialogFactory).createMessageDialog(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), ArgumentMatchers.<ConfirmCallback>anyObject());
        Mockito.verify(messageDialog).show();
        Mockito.verify(sshServiceClient).getPairs(ArgumentMatchers.eq(VCS_SSH_SERVICE));
    }

    @Test
    public void onAuthenticatedWhenGetFailedKeyIsSuccess() throws Exception {
        String userId = "userId";
        SshPairDto pair = Mockito.mock(SshPairDto.class);
        List<SshPairDto> pairs = new ArrayList<>();
        pairs.add(pair);
        OAuthStatus authStatus = Mockito.mock(OAuthStatus.class);
        SshKeyUploader keyUploader = Mockito.mock(SshKeyUploader.class);
        CurrentUser user = Mockito.mock(CurrentUser.class);
        MessageDialog messageDialog = Mockito.mock(MessageDialog.class);
        Mockito.when(view.isGenerateKeysSelected()).thenReturn(true);
        Mockito.when(registry.getUploader(GitHubAuthenticatorImplTest.GITHUB_HOST)).thenReturn(keyUploader);
        Mockito.when(appContext.getCurrentUser()).thenReturn(user);
        Mockito.when(user.getId()).thenReturn(userId);
        Mockito.when(dialogFactory.createMessageDialog(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), ArgumentMatchers.<ConfirmCallback>anyObject())).thenReturn(messageDialog);
        Mockito.when(pair.getName()).thenReturn(GitHubAuthenticatorImplTest.GITHUB_HOST);
        Mockito.when(pair.getService()).thenReturn(VCS_SSH_SERVICE);
        gitHubAuthenticator.authenticate(null, getCallBack());
        gitHubAuthenticator.onAuthenticated(authStatus);
        Mockito.verify(keyUploader).uploadKey(ArgumentMatchers.eq(userId), generateKeyCallbackCaptor.capture());
        AsyncCallback<Void> generateKeyCallback = generateKeyCallbackCaptor.getValue();
        generateKeyCallback.onFailure(new Exception(""));
        Mockito.verify(sshPairDTOsPromise).then(operationSshPairDTOsCapture.capture());
        operationSshPairDTOsCapture.getValue().apply(pairs);
        Mockito.verify(view).isGenerateKeysSelected();
        Mockito.verify(registry).getUploader(ArgumentMatchers.eq(GitHubAuthenticatorImplTest.GITHUB_HOST));
        Mockito.verify(appContext).getCurrentUser();
        Mockito.verify(dialogFactory).createMessageDialog(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), ArgumentMatchers.<ConfirmCallback>anyObject());
        Mockito.verify(messageDialog).show();
        Mockito.verify(sshServiceClient).getPairs(ArgumentMatchers.eq(VCS_SSH_SERVICE));
        Mockito.verify(sshServiceClient).deletePair(ArgumentMatchers.eq(VCS_SSH_SERVICE), ArgumentMatchers.eq(GitHubAuthenticatorImplTest.GITHUB_HOST));
    }
}

