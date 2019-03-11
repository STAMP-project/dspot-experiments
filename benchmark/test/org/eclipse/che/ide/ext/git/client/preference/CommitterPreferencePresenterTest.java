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
package org.eclipse.che.ide.ext.git.client.preference;


import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.eclipse.che.ide.api.preferences.PreferencesManager;
import org.eclipse.che.ide.ext.git.client.GitLocalizationConstant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author Valeriy Svydenko
 */
@RunWith(MockitoJUnitRunner.class)
public class CommitterPreferencePresenterTest {
    public static final String SOME_TEXT = "text";

    public static final String COMMITTER_NAME = "git.committer.name";

    public static final String COMMITTER_EMAIL = "git.committer.email";

    public static final String DEFAULT_COMMITTER_NAME = "Anonymous";

    public static final String DEFAULT_COMMITTER_EMAIL = "anonymous@noemail.com";

    @Mock
    private CommitterPreferenceView view;

    @Mock
    private GitLocalizationConstant constant;

    @Mock
    private PreferencesManager preferencesManager;

    @Mock
    private AcceptsOneWidget container;

    private CommitterPreferencePresenter presenter;

    @Test
    public void constructorShouldBePerformed() throws Exception {
        Mockito.verify(view).setDelegate(presenter);
        Mockito.verify(constant).committerTitle();
        Mockito.verify(constant).committerPreferenceCategory();
        Mockito.verify(preferencesManager).getValue(CommitterPreferencePresenterTest.COMMITTER_NAME);
        Mockito.verify(preferencesManager).getValue(CommitterPreferencePresenterTest.COMMITTER_EMAIL);
    }

    @Test
    public void dirtyStateShouldBeReturned() throws Exception {
        Assert.assertFalse(presenter.isDirty());
    }

    @Test
    public void widgetShouldBePrepared() throws Exception {
        presenter.go(container);
        Mockito.verify(container).setWidget(view);
        Mockito.verify(view).setEmail(ArgumentMatchers.anyString());
        Mockito.verify(view).setName(ArgumentMatchers.anyString());
    }

    @Test
    public void changesShouldBeRestored() throws Exception {
        presenter.revertChanges();
        Mockito.verify(preferencesManager, Mockito.times(2)).getValue(CommitterPreferencePresenterTest.COMMITTER_NAME);
        Mockito.verify(preferencesManager, Mockito.times(2)).getValue(CommitterPreferencePresenterTest.COMMITTER_EMAIL);
        Assert.assertFalse(presenter.isDirty());
    }

    @Test
    public void defaultUserNameAndEmailShouldBeRestored() throws Exception {
        // when(preferencesManager.getValue(COMMITTER_EMAIL)).thenReturn(null);
        // when(preferencesManager.getValue(COMMITTER_NAME)).thenReturn(null);
        // 
        // presenter.revertChanges();
        // 
        // verify(preferencesManager, times(2)).getValue(COMMITTER_NAME);
        // verify(preferencesManager, times(2)).getValue(COMMITTER_EMAIL);
        // 
        // verify(view).setEmail(DEFAULT_COMMITTER_EMAIL);
        // verify(view).setName(DEFAULT_COMMITTER_NAME);
        // 
        // assertFalse(presenter.isDirty());
    }
}

