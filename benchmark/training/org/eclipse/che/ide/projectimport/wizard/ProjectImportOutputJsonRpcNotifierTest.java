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
package org.eclipse.che.ide.projectimport.wizard;


import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.event.shared.EventBus;
import java.util.function.Consumer;
import org.eclipse.che.api.core.jsonrpc.commons.RequestHandlerConfigurator;
import org.eclipse.che.api.core.jsonrpc.commons.reception.ConsumerConfiguratorOneToNone;
import org.eclipse.che.api.core.jsonrpc.commons.reception.MethodNameConfigurator;
import org.eclipse.che.api.core.jsonrpc.commons.reception.ParamsConfigurator;
import org.eclipse.che.api.core.jsonrpc.commons.reception.ResultConfiguratorFromOne;
import org.eclipse.che.api.project.shared.dto.ImportProgressRecordDto;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.notification.StatusNotification;
import org.eclipse.che.ide.api.notification.StatusNotification.DisplayMode;
import org.eclipse.che.ide.api.notification.StatusNotification.Status;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit tests for {@link ProjectImportOutputJsonRpcNotifier}.
 *
 * @author Vlad Zhukovskyi
 */
@RunWith(GwtMockitoTestRunner.class)
public class ProjectImportOutputJsonRpcNotifierTest {
    @Mock
    NotificationManager notificationManager;

    @Mock
    CoreLocalizationConstant locale;

    @Mock
    EventBus eventBus;

    @Mock
    RequestHandlerConfigurator configurator;

    @Mock
    ImportProgressJsonRpcHandler importProgressJsonRpcHandler;

    private ProjectImportOutputJsonRpcNotifier notifier;

    @Test
    public void testShouldSubscribeForDisplayingNotification() throws Exception {
        // given
        final ImportProgressRecordDto dto = Mockito.mock(ImportProgressRecordDto.class);
        Mockito.when(dto.getNum()).thenReturn(1);
        Mockito.when(dto.getLine()).thenReturn("message");
        Mockito.when(dto.getProjectName()).thenReturn("project");
        final ArgumentCaptor<Consumer> argumentCaptor = ArgumentCaptor.forClass(Consumer.class);
        final StatusNotification statusNotification = Mockito.mock(StatusNotification.class);
        Mockito.when(notificationManager.notify(ArgumentMatchers.anyString(), ArgumentMatchers.any(Status.class), ArgumentMatchers.any(DisplayMode.class))).thenReturn(statusNotification);
        Mockito.when(locale.importingProject(ArgumentMatchers.anyString())).thenReturn("message");
        final MethodNameConfigurator methodNameConfigurator = Mockito.mock(MethodNameConfigurator.class);
        Mockito.when(configurator.newConfiguration()).thenReturn(methodNameConfigurator);
        final ParamsConfigurator paramsConfigurator = Mockito.mock(ParamsConfigurator.class);
        Mockito.when(methodNameConfigurator.methodName(ArgumentMatchers.anyString())).thenReturn(paramsConfigurator);
        final ResultConfiguratorFromOne resultConfiguratorFromOne = Mockito.mock(ResultConfiguratorFromOne.class);
        Mockito.when(paramsConfigurator.paramsAsDto(ArgumentMatchers.any())).thenReturn(resultConfiguratorFromOne);
        final ConsumerConfiguratorOneToNone consumerConfiguratorOneToNone = Mockito.mock(ConsumerConfiguratorOneToNone.class);
        Mockito.when(resultConfiguratorFromOne.noResult()).thenReturn(consumerConfiguratorOneToNone);
        // when
        notifier.subscribe("project");
        // then
        Mockito.verify(locale).importingProject(ArgumentMatchers.eq("project"));
        Mockito.verify(consumerConfiguratorOneToNone).withConsumer(argumentCaptor.capture());
        argumentCaptor.getValue().accept(dto);
        Mockito.verify(statusNotification).setTitle(ArgumentMatchers.eq("message"));
        Mockito.verify(statusNotification).setContent(ArgumentMatchers.eq(dto.getLine()));
    }

    @Test
    public void testShouldUnSubscribeFromDisplayingNotification() throws Exception {
        // given
        Mockito.when(locale.importProjectMessageSuccess(ArgumentMatchers.nullable(String.class))).thenReturn("message");
        final StatusNotification statusNotification = Mockito.mock(StatusNotification.class);
        Mockito.when(notificationManager.notify(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(Status.class), ArgumentMatchers.nullable(DisplayMode.class))).thenReturn(statusNotification);
        final MethodNameConfigurator methodNameConfigurator = Mockito.mock(MethodNameConfigurator.class);
        Mockito.when(configurator.newConfiguration()).thenReturn(methodNameConfigurator);
        final ParamsConfigurator paramsConfigurator = Mockito.mock(ParamsConfigurator.class);
        Mockito.when(methodNameConfigurator.methodName(ArgumentMatchers.nullable(String.class))).thenReturn(paramsConfigurator);
        final ResultConfiguratorFromOne resultConfiguratorFromOne = Mockito.mock(ResultConfiguratorFromOne.class);
        Mockito.when(paramsConfigurator.paramsAsDto(ArgumentMatchers.any())).thenReturn(resultConfiguratorFromOne);
        final ConsumerConfiguratorOneToNone consumerConfiguratorOneToNone = Mockito.mock(ConsumerConfiguratorOneToNone.class);
        Mockito.when(resultConfiguratorFromOne.noResult()).thenReturn(consumerConfiguratorOneToNone);
        // when
        notifier.subscribe("project");
        notifier.onSuccess();
        // then
        Mockito.verify(statusNotification).setStatus(ArgumentMatchers.eq(SUCCESS));
        Mockito.verify(statusNotification).setTitle(ArgumentMatchers.eq("message"));
        Mockito.verify(statusNotification).setContent(ArgumentMatchers.eq(""));
    }

    @Test
    public void testShouldUnSubscribeFromDisplayingNotificationIfExceptionOccurred() throws Exception {
        // given
        final StatusNotification statusNotification = Mockito.mock(StatusNotification.class);
        Mockito.when(notificationManager.notify(ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(Status.class), ArgumentMatchers.nullable(DisplayMode.class))).thenReturn(statusNotification);
        final MethodNameConfigurator methodNameConfigurator = Mockito.mock(MethodNameConfigurator.class);
        Mockito.when(configurator.newConfiguration()).thenReturn(methodNameConfigurator);
        final ParamsConfigurator paramsConfigurator = Mockito.mock(ParamsConfigurator.class);
        Mockito.when(methodNameConfigurator.methodName(ArgumentMatchers.nullable(String.class))).thenReturn(paramsConfigurator);
        final ResultConfiguratorFromOne resultConfiguratorFromOne = Mockito.mock(ResultConfiguratorFromOne.class);
        Mockito.when(paramsConfigurator.paramsAsDto(ArgumentMatchers.any())).thenReturn(resultConfiguratorFromOne);
        final ConsumerConfiguratorOneToNone consumerConfiguratorOneToNone = Mockito.mock(ConsumerConfiguratorOneToNone.class);
        Mockito.when(resultConfiguratorFromOne.noResult()).thenReturn(consumerConfiguratorOneToNone);
        // when
        notifier.subscribe("project");
        notifier.onFailure("message");
        // then
        Mockito.verify(statusNotification).setStatus(ArgumentMatchers.eq(FAIL));
        Mockito.verify(statusNotification).setContent(ArgumentMatchers.eq("message"));
    }
}

