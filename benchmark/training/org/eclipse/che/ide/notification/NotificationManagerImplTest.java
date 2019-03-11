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
package org.eclipse.che.ide.notification;


import ReadState.READ;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.api.notification.Notification;
import org.eclipse.che.ide.api.notification.NotificationListener;
import org.eclipse.che.ide.api.notification.StatusNotification;
import org.eclipse.che.ide.part.PartStackPresenter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Testing {@link NotificationManagerImpl} functionality
 *
 * @author Andrey Plotnikov
 * @author Vlad Zhukovskyi
 */
@RunWith(GwtMockitoTestRunner.class)
public class NotificationManagerImplTest {
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Resources resources;

    @Mock
    private NotificationManagerView view;

    @Mock
    private NotificationContainer notificationContainer;

    @Mock
    private NotificationPopupStack notificationMessageStack;

    private NotificationManagerImpl manager;

    @Mock
    PartStackPresenter partStack;

    @Test
    public void testOnValueChanged() throws Exception {
        Notification notification = new Notification("Title");
        manager.notify(notification);
        Mockito.reset(view);
        manager.onValueChanged();
        Mockito.reset(view);
        notification.setState(READ);
    }

    @Test
    public void testShowSimpleNotification() throws Exception {
        Notification notification = new Notification("Title");
        manager.notify(notification);
        Mockito.verify(notificationContainer).addNotification(ArgumentMatchers.eq(notification));
        Mockito.verify(notificationMessageStack, Mockito.never()).push(ArgumentMatchers.any(StatusNotification.class));
    }

    @Test
    public void testShowStatusNotification() throws Exception {
        StatusNotification notification = new StatusNotification("Title", "Message", SUCCESS, FLOAT_MODE, null, null);
        manager.notify(notification);
        Mockito.verify(notificationContainer).addNotification(ArgumentMatchers.eq(notification));
        Mockito.verify(notificationMessageStack).push(ArgumentMatchers.eq(notification));
    }

    @Test
    public void testShowStatusNotificationOnlyInEventsPanel() throws Exception {
        StatusNotification notification = new StatusNotification("Title", "Message", SUCCESS, NOT_EMERGE_MODE, null, null);
        manager.notify(notification);
        Mockito.verify(notificationContainer).addNotification(ArgumentMatchers.eq(notification));
        Mockito.verify(notificationMessageStack, Mockito.never()).push(ArgumentMatchers.any(StatusNotification.class));
    }

    @Test
    public void testRemoveNotification() throws Exception {
        StatusNotification notification = new StatusNotification("Title", "Message", SUCCESS, NOT_EMERGE_MODE, null, null);
        manager.removeNotification(notification);
        Mockito.verify(notificationContainer).removeNotification(ArgumentMatchers.eq(notification));
    }

    @Test
    public void testOnMessageClicked() throws Exception {
        NotificationListener listener = Mockito.mock(NotificationListener.class);
        StatusNotification notification = new StatusNotification("Title", "Message", SUCCESS, NOT_EMERGE_MODE, null, listener);
        manager.onClick(notification);
        Mockito.verify(listener).onClick(ArgumentMatchers.eq(notification));
        Mockito.verify(listener, Mockito.never()).onClose(ArgumentMatchers.eq(notification));
        Mockito.verify(listener, Mockito.never()).onDoubleClick(ArgumentMatchers.eq(notification));
    }

    @Test
    public void testOnMessageDoubleClicked() throws Exception {
        NotificationListener listener = Mockito.mock(NotificationListener.class);
        StatusNotification notification = new StatusNotification("Title", "Message", SUCCESS, NOT_EMERGE_MODE, null, listener);
        manager.onDoubleClick(notification);
        Mockito.verify(listener, Mockito.never()).onClick(ArgumentMatchers.eq(notification));
        Mockito.verify(listener, Mockito.never()).onClose(ArgumentMatchers.eq(notification));
        Mockito.verify(listener).onDoubleClick(ArgumentMatchers.eq(notification));
    }

    @Test
    public void testOnCloseMessageClicked() throws Exception {
        NotificationListener listener = Mockito.mock(NotificationListener.class);
        StatusNotification notification = new StatusNotification("Title", "Message", SUCCESS, NOT_EMERGE_MODE, null, listener);
        manager.onClose(notification);
        Mockito.verify(listener, Mockito.never()).onClick(ArgumentMatchers.eq(notification));
        Mockito.verify(listener).onClose(ArgumentMatchers.eq(notification));
        Mockito.verify(listener, Mockito.never()).onDoubleClick(ArgumentMatchers.eq(notification));
    }

    @Test
    public void testGo() throws Exception {
        AcceptsOneWidget container = Mockito.mock(AcceptsOneWidget.class);
        manager.go(container);
        Mockito.verify(container).setWidget(ArgumentMatchers.eq(view));
    }
}

