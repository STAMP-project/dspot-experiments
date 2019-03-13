/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.robonect.internal.handler;


import MowerMode.AUTO;
import MowerStatus.ERROR_STATUS;
import MowerStatus.MOWING;
import OnOffType.ON;
import RefreshType.REFRESH;
import Timer.TimerMode.ACTIVE;
import UnDefType.UNDEF;
import java.util.Calendar;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.smarthome.core.library.types.DateTimeType;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerCallback;
import org.eclipse.smarthome.core.types.State;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openhab.binding.robonect.internal.RobonectBindingConstants;
import org.openhab.binding.robonect.internal.RobonectClient;
import org.openhab.binding.robonect.internal.model.ErrorEntry;
import org.openhab.binding.robonect.internal.model.ErrorList;
import org.openhab.binding.robonect.internal.model.MowerInfo;
import org.openhab.binding.robonect.internal.model.NextTimer;
import org.openhab.binding.robonect.internal.model.Timer;


/**
 * The goal of this class is to test RobonectHandler in isolation.
 *
 * @author Marco Meyer - Initial contribution
 */
public class RobonectHandlerTest {
    private RobonectHandler subject;

    @Mock
    private Thing robonectThingMock;

    @Mock
    private RobonectClient robonectClientMock;

    @Mock
    private ThingHandlerCallback callbackMock;

    @Mock
    private HttpClient httpClientMock;

    @Test
    public void shouldUpdateNextTimerChannelWithDateTimeState() throws InterruptedException {
        ArgumentCaptor<State> stateCaptor = ArgumentCaptor.forClass(State.class);
        // given
        MowerInfo mowerInfo = createSuccessfulMowerInfoResponse();
        Timer timer = new Timer();
        timer.setStatus(ACTIVE);
        NextTimer nextTimer = new NextTimer();
        nextTimer.setDate("01.05.2017");
        nextTimer.setTime("19:00:00");
        nextTimer.setUnix("1493665200");
        timer.setNext(nextTimer);
        // when
        Mockito.when(robonectClientMock.getMowerInfo()).thenReturn(mowerInfo);
        Mockito.when(robonectThingMock.getUID()).thenReturn(new ThingUID("1:2:3"));
        subject.handleCommand(new org.eclipse.smarthome.core.thing.ChannelUID(new ThingUID("1:2:3"), RobonectBindingConstants.CHANNEL_TIMER_NEXT_TIMER), REFRESH);
        // then
        Mockito.verify(callbackMock, Mockito.times(1)).stateUpdated(ArgumentMatchers.eq(new org.eclipse.smarthome.core.thing.ChannelUID(new ThingUID("1:2:3"), RobonectBindingConstants.CHANNEL_TIMER_NEXT_TIMER)), stateCaptor.capture());
        State value = stateCaptor.getValue();
        Assert.assertTrue((value instanceof DateTimeType));
        DateTimeType dateTimeType = ((DateTimeType) (value));
        Assert.assertEquals(1, dateTimeType.getCalendar().get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(2017, dateTimeType.getCalendar().get(Calendar.YEAR));
        // calendar january is 0
        Assert.assertEquals(4, dateTimeType.getCalendar().get(Calendar.MONTH));
        Assert.assertEquals(19, dateTimeType.getCalendar().get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(0, dateTimeType.getCalendar().get(Calendar.MINUTE));
        Assert.assertEquals(0, dateTimeType.getCalendar().get(Calendar.SECOND));
    }

    @Test
    public void shouldUpdateErrorChannelsIfErrorStatusReturned() throws InterruptedException {
        ArgumentCaptor<State> errorCodeCaptor = ArgumentCaptor.forClass(State.class);
        ArgumentCaptor<State> errorMessageCaptor = ArgumentCaptor.forClass(State.class);
        ArgumentCaptor<State> errorDateCaptor = ArgumentCaptor.forClass(State.class);
        // given
        MowerInfo mowerInfo = createSuccessfulMowerInfoResponse();
        ErrorEntry error = new ErrorEntry();
        error.setDate("01.05.2017");
        error.setTime("19:00:00");
        error.setUnix("1493665200");
        error.setErrorCode(new Integer(22));
        error.setErrorMessage("Dummy Message");
        mowerInfo.getStatus().setStatus(ERROR_STATUS);
        mowerInfo.setError(error);
        ErrorList errorList = new ErrorList();
        errorList.setSuccessful(true);
        // when
        Mockito.when(robonectClientMock.getMowerInfo()).thenReturn(mowerInfo);
        Mockito.when(robonectClientMock.errorList()).thenReturn(errorList);
        Mockito.when(robonectThingMock.getUID()).thenReturn(new ThingUID("1:2:3"));
        subject.handleCommand(new org.eclipse.smarthome.core.thing.ChannelUID(new ThingUID("1:2:3"), RobonectBindingConstants.CHANNEL_STATUS), REFRESH);
        // then
        Mockito.verify(callbackMock, Mockito.times(1)).stateUpdated(ArgumentMatchers.eq(new org.eclipse.smarthome.core.thing.ChannelUID(new ThingUID("1:2:3"), RobonectBindingConstants.CHANNEL_ERROR_CODE)), errorCodeCaptor.capture());
        Mockito.verify(callbackMock, Mockito.times(1)).stateUpdated(ArgumentMatchers.eq(new org.eclipse.smarthome.core.thing.ChannelUID(new ThingUID("1:2:3"), RobonectBindingConstants.CHANNEL_ERROR_MESSAGE)), errorMessageCaptor.capture());
        Mockito.verify(callbackMock, Mockito.times(1)).stateUpdated(ArgumentMatchers.eq(new org.eclipse.smarthome.core.thing.ChannelUID(new ThingUID("1:2:3"), RobonectBindingConstants.CHANNEL_ERROR_DATE)), errorDateCaptor.capture());
        State errorDate = errorDateCaptor.getValue();
        Assert.assertTrue((errorDate instanceof DateTimeType));
        DateTimeType dateTimeType = ((DateTimeType) (errorDate));
        Assert.assertEquals(1, dateTimeType.getCalendar().get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(2017, dateTimeType.getCalendar().get(Calendar.YEAR));
        // calendar january is 0
        Assert.assertEquals(4, dateTimeType.getCalendar().get(Calendar.MONTH));
        Assert.assertEquals(19, dateTimeType.getCalendar().get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(0, dateTimeType.getCalendar().get(Calendar.MINUTE));
        Assert.assertEquals(0, dateTimeType.getCalendar().get(Calendar.SECOND));
        State errorMessage = errorMessageCaptor.getValue();
        Assert.assertTrue((errorMessage instanceof StringType));
        StringType msgStringType = ((StringType) (errorMessage));
        Assert.assertEquals("Dummy Message", msgStringType.toFullString());
        State errorCode = errorCodeCaptor.getValue();
        Assert.assertTrue((errorCode instanceof DecimalType));
        DecimalType codeDecimaltype = ((DecimalType) (errorCode));
        Assert.assertEquals(22, codeDecimaltype.intValue());
    }

    @Test
    public void shouldResetErrorStateIfNoErrorInStatusUpdate() throws InterruptedException {
        ArgumentCaptor<State> errorCodeCaptor = ArgumentCaptor.forClass(State.class);
        ArgumentCaptor<State> errorMessageCaptor = ArgumentCaptor.forClass(State.class);
        ArgumentCaptor<State> errorDateCaptor = ArgumentCaptor.forClass(State.class);
        // given
        MowerInfo mowerInfo = createSuccessfulMowerInfoResponse();
        mowerInfo.getStatus().setStatus(MOWING);
        mowerInfo.setError(null);
        // when
        Mockito.when(robonectClientMock.getMowerInfo()).thenReturn(mowerInfo);
        Mockito.when(robonectThingMock.getUID()).thenReturn(new ThingUID("1:2:3"));
        subject.handleCommand(new org.eclipse.smarthome.core.thing.ChannelUID(new ThingUID("1:2:3"), RobonectBindingConstants.CHANNEL_STATUS), REFRESH);
        // then
        Mockito.verify(callbackMock, Mockito.times(1)).stateUpdated(ArgumentMatchers.eq(new org.eclipse.smarthome.core.thing.ChannelUID(new ThingUID("1:2:3"), RobonectBindingConstants.CHANNEL_ERROR_CODE)), errorCodeCaptor.capture());
        Mockito.verify(callbackMock, Mockito.times(1)).stateUpdated(ArgumentMatchers.eq(new org.eclipse.smarthome.core.thing.ChannelUID(new ThingUID("1:2:3"), RobonectBindingConstants.CHANNEL_ERROR_MESSAGE)), errorMessageCaptor.capture());
        Mockito.verify(callbackMock, Mockito.times(1)).stateUpdated(ArgumentMatchers.eq(new org.eclipse.smarthome.core.thing.ChannelUID(new ThingUID("1:2:3"), RobonectBindingConstants.CHANNEL_ERROR_DATE)), errorDateCaptor.capture());
        Assert.assertEquals(errorCodeCaptor.getValue(), UNDEF);
        Assert.assertEquals(errorMessageCaptor.getValue(), UNDEF);
        Assert.assertEquals(errorDateCaptor.getValue(), UNDEF);
    }

    @Test
    public void shouldUpdateNumericStateOnMowerStatusRefresh() throws InterruptedException {
        ArgumentCaptor<State> stateCaptor = ArgumentCaptor.forClass(State.class);
        // given
        MowerInfo mowerInfo = createSuccessfulMowerInfoResponse();
        mowerInfo.getStatus().setStatus(MOWING);
        // when
        Mockito.when(robonectClientMock.getMowerInfo()).thenReturn(mowerInfo);
        Mockito.when(robonectThingMock.getUID()).thenReturn(new ThingUID("1:2:3"));
        subject.handleCommand(new org.eclipse.smarthome.core.thing.ChannelUID(new ThingUID("1:2:3"), RobonectBindingConstants.CHANNEL_STATUS), REFRESH);
        // then
        Mockito.verify(callbackMock, Mockito.times(1)).stateUpdated(ArgumentMatchers.eq(new org.eclipse.smarthome.core.thing.ChannelUID(new ThingUID("1:2:3"), RobonectBindingConstants.CHANNEL_STATUS)), stateCaptor.capture());
        State value = stateCaptor.getValue();
        Assert.assertTrue((value instanceof DecimalType));
        DecimalType status = ((DecimalType) (value));
        Assert.assertEquals(MOWING.getStatusCode(), status.intValue());
    }

    @Test
    public void shouldUpdateAllChannels() {
        ArgumentCaptor<State> stateCaptorName = ArgumentCaptor.forClass(State.class);
        ArgumentCaptor<State> stateCaptorBattery = ArgumentCaptor.forClass(State.class);
        ArgumentCaptor<State> stateCaptorStatus = ArgumentCaptor.forClass(State.class);
        ArgumentCaptor<State> stateCaptorDuration = ArgumentCaptor.forClass(State.class);
        ArgumentCaptor<State> stateCaptorHours = ArgumentCaptor.forClass(State.class);
        ArgumentCaptor<State> stateCaptorMode = ArgumentCaptor.forClass(State.class);
        ArgumentCaptor<State> stateCaptorStarted = ArgumentCaptor.forClass(State.class);
        ArgumentCaptor<State> stateCaptorWlan = ArgumentCaptor.forClass(State.class);
        // given
        MowerInfo mowerInfo = createSuccessfulMowerInfoResponse();
        ErrorList errorList = new ErrorList();
        errorList.setSuccessful(true);
        // when
        Mockito.when(robonectClientMock.getMowerInfo()).thenReturn(mowerInfo);
        Mockito.when(robonectClientMock.errorList()).thenReturn(errorList);
        Mockito.when(robonectThingMock.getUID()).thenReturn(new ThingUID("1:2:3"));
        subject.handleCommand(new org.eclipse.smarthome.core.thing.ChannelUID(new ThingUID("1:2:3"), RobonectBindingConstants.CHANNEL_STATUS), REFRESH);
        // then
        Mockito.verify(callbackMock, Mockito.times(1)).stateUpdated(ArgumentMatchers.eq(new org.eclipse.smarthome.core.thing.ChannelUID(new ThingUID("1:2:3"), RobonectBindingConstants.CHANNEL_MOWER_NAME)), stateCaptorName.capture());
        Mockito.verify(callbackMock, Mockito.times(1)).stateUpdated(ArgumentMatchers.eq(new org.eclipse.smarthome.core.thing.ChannelUID(new ThingUID("1:2:3"), RobonectBindingConstants.CHANNEL_STATUS_BATTERY)), stateCaptorBattery.capture());
        Mockito.verify(callbackMock, Mockito.times(1)).stateUpdated(ArgumentMatchers.eq(new org.eclipse.smarthome.core.thing.ChannelUID(new ThingUID("1:2:3"), RobonectBindingConstants.CHANNEL_STATUS)), stateCaptorStatus.capture());
        Mockito.verify(callbackMock, Mockito.times(1)).stateUpdated(ArgumentMatchers.eq(new org.eclipse.smarthome.core.thing.ChannelUID(new ThingUID("1:2:3"), RobonectBindingConstants.CHANNEL_STATUS_DURATION)), stateCaptorDuration.capture());
        Mockito.verify(callbackMock, Mockito.times(1)).stateUpdated(ArgumentMatchers.eq(new org.eclipse.smarthome.core.thing.ChannelUID(new ThingUID("1:2:3"), RobonectBindingConstants.CHANNEL_STATUS_HOURS)), stateCaptorHours.capture());
        Mockito.verify(callbackMock, Mockito.times(1)).stateUpdated(ArgumentMatchers.eq(new org.eclipse.smarthome.core.thing.ChannelUID(new ThingUID("1:2:3"), RobonectBindingConstants.CHANNEL_STATUS_MODE)), stateCaptorMode.capture());
        Mockito.verify(callbackMock, Mockito.times(1)).stateUpdated(ArgumentMatchers.eq(new org.eclipse.smarthome.core.thing.ChannelUID(new ThingUID("1:2:3"), RobonectBindingConstants.CHANNEL_MOWER_START)), stateCaptorStarted.capture());
        Mockito.verify(callbackMock, Mockito.times(1)).stateUpdated(ArgumentMatchers.eq(new org.eclipse.smarthome.core.thing.ChannelUID(new ThingUID("1:2:3"), RobonectBindingConstants.CHANNEL_WLAN_SIGNAL)), stateCaptorWlan.capture());
        Assert.assertEquals("Mowy", stateCaptorName.getValue().toFullString());
        Assert.assertEquals(99, intValue());
        Assert.assertEquals(4, intValue());
        Assert.assertEquals(55, intValue());
        Assert.assertEquals(22, intValue());
        Assert.assertEquals(AUTO.name(), stateCaptorMode.getValue().toFullString());
        Assert.assertEquals(ON, stateCaptorStarted.getValue());
        Assert.assertEquals((-88), intValue());
    }
}

