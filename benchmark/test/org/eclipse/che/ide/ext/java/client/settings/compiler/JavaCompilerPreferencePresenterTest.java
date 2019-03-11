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
package org.eclipse.che.ide.ext.java.client.settings.compiler;


import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;
import java.util.Map;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.preferences.PreferencePagePresenter.DirtyStateListener;
import org.eclipse.che.ide.api.preferences.PreferencesManager;
import org.eclipse.che.ide.ext.java.client.JavaLocalizationConstant;
import org.eclipse.che.ide.ext.java.client.inject.factories.PropertyWidgetFactory;
import org.eclipse.che.ide.ext.java.client.settings.property.PropertyWidget;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
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
 *
 *
 * @author Dmitry Shnurenko
 */
@RunWith(MockitoJUnitRunner.class)
public class JavaCompilerPreferencePresenterTest {
    private static final String ID_1 = "id1";

    private static final String ID_2 = "id2";

    private static final String VALUE_1 = "value1";

    private static final String VALUE_2 = "value2";

    // constructor mocks
    @Mock
    private ErrorWarningsView view;

    @Mock
    private PropertyWidgetFactory propertyFactory;

    @Mock
    private JavaLocalizationConstant locale;

    @Mock
    private PreferencesManager preferencesManager;

    @Mock
    private EventBus eventBus;

    @Mock
    private Provider<NotificationManager> notificationManagerProvider;

    @Mock
    private DirtyStateListener dirtyStateListener;

    @Mock
    private Promise<Map<String, String>> mapPromise;

    @Mock
    private AcceptsOneWidget container;

    @Mock
    private PropertyWidget widget;

    @Captor
    private ArgumentCaptor<Map<String, String>> mapCaptor;

    @Captor
    private ArgumentCaptor<Operation<Map<String, String>>> operationCaptor;

    @Captor
    private ArgumentCaptor<Operation<PromiseError>> errorOperationCaptor;

    @InjectMocks
    private JavaCompilerPreferencePresenter presenter;

    @Test
    public void constructorShouldBeVerified() {
        Mockito.verify(locale).compilerSetup();
    }

    @Test
    public void pageShouldNotBeDirty() {
        boolean isDirty = presenter.isDirty();
        Assert.assertThat(isDirty, CoreMatchers.equalTo(false));
    }

    @Test
    public void changedValuesShouldBeSaved() throws OperationException {
        initWidgets();
        Mockito.when(widget.getSelectedValue()).thenReturn(JavaCompilerPreferencePresenterTest.VALUE_2);
        Mockito.when(preferencesManager.getValue(ArgumentMatchers.anyString())).thenReturn(JavaCompilerPreferencePresenterTest.VALUE_1);
        presenter.go(container);
        Mockito.verify(mapPromise).then(operationCaptor.capture());
        operationCaptor.getValue().apply(getAllProperties());
        presenter.storeChanges();
        Mockito.verify(preferencesManager, Mockito.times(18)).setValue(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.verify(preferencesManager, Mockito.times(36)).getValue(ArgumentMatchers.anyString());
        Mockito.when(preferencesManager.getValue(ArgumentMatchers.anyString())).thenReturn(JavaCompilerPreferencePresenterTest.VALUE_2);
        Assert.assertThat(presenter.isDirty(), CoreMatchers.equalTo(false));
    }

    @Test
    public void changesShouldBeReverted() throws Exception {
        initWidgets();
        Mockito.when(widget.getSelectedValue()).thenReturn(JavaCompilerPreferencePresenterTest.VALUE_2);
        Mockito.when(preferencesManager.getValue(ArgumentMatchers.anyString())).thenReturn(JavaCompilerPreferencePresenterTest.VALUE_1);
        presenter.go(container);
        Mockito.verify(mapPromise).then(operationCaptor.capture());
        operationCaptor.getValue().apply(getAllProperties());
        presenter.onPropertyChanged();
        presenter.revertChanges();
        Mockito.verify(preferencesManager, Mockito.times(36)).getValue(ArgumentMatchers.anyString());
        Mockito.verify(widget, Mockito.times(36)).selectPropertyValue(ArgumentMatchers.anyString());
        Mockito.verify(widget, Mockito.times(18)).getSelectedValue();
    }

    @Test
    public void propertyShouldBeChanged() {
        presenter.onPropertyChanged();
        Mockito.verify(dirtyStateListener).onDirtyChanged();
    }

    @Test
    public void propertiesShouldBeDisplayed() throws Exception {
        presenter.go(container);
        Mockito.verify(mapPromise).then(operationCaptor.capture());
        operationCaptor.getValue().apply(getAllProperties());
        Mockito.verify(propertyFactory, Mockito.times(18)).create(ArgumentMatchers.<ErrorWarningsOptions>anyObject());
        Mockito.verify(widget, Mockito.times(18)).selectPropertyValue(ArgumentMatchers.nullable(String.class));
        Mockito.verify(widget, Mockito.times(18)).setDelegate(presenter);
        Mockito.verify(view, Mockito.times(18)).addProperty(widget);
    }

    @Test
    public void propertiesShouldBeDisplayedFailed() throws OperationException {
        PromiseError promiseError = Mockito.mock(PromiseError.class);
        NotificationManager notificationManager = Mockito.mock(NotificationManager.class);
        Mockito.when(notificationManagerProvider.get()).thenReturn(notificationManager);
        presenter.go(container);
        Mockito.verify(mapPromise).catchError(errorOperationCaptor.capture());
        errorOperationCaptor.getValue().apply(promiseError);
        Mockito.verify(preferencesManager).loadPreferences();
        Mockito.verify(notificationManager).notify(ArgumentMatchers.nullable(String.class), ArgumentMatchers.eq(FAIL), ArgumentMatchers.eq(FLOAT_MODE));
    }
}

