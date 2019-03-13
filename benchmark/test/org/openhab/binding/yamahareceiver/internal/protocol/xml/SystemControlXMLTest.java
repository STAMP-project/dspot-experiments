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
package org.openhab.binding.yamahareceiver.internal.protocol.xml;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openhab.binding.yamahareceiver.internal.TestModels;
import org.openhab.binding.yamahareceiver.internal.state.DeviceInformationState;
import org.openhab.binding.yamahareceiver.internal.state.SystemControlState;
import org.openhab.binding.yamahareceiver.internal.state.SystemControlStateListener;


/**
 * Unit test for {@link SystemControlXML}.
 *
 * @author Tomasz Maruszak - Initial contribution
 */
public class SystemControlXMLTest extends AbstractXMLProtocolTest {
    private SystemControlXML subject;

    private DeviceInformationState deviceInformationState;

    @Mock
    private SystemControlStateListener systemControlStateListener;

    @Test
    public void given_RX_S601D_when_update_then_parsesState() throws Exception {
        // given
        setupFor(TestModels.RX_S601D);
        // when
        subject.update();
        // then
        ArgumentCaptor<SystemControlState> stateArg = ArgumentCaptor.forClass(SystemControlState.class);
        Mockito.verify(systemControlStateListener, Mockito.only()).systemControlStateChanged(stateArg.capture());
        SystemControlState state = stateArg.getValue();
        Assert.assertTrue(state.power);
        Assert.assertTrue(state.partyMode);
    }

    @Test
    public void given_RX_S601D_when_power_then_sendsProperCommand() throws Exception {
        // given
        setupFor(TestModels.RX_S601D);
        // when
        subject.setPower(true);
        subject.setPower(false);
        subject.setPartyMode(true);
        subject.setPartyModeMute(true);
        // then
        Mockito.verify(con).send(ArgumentMatchers.eq("<System><Power_Control><Power>On</Power></Power_Control></System>"));
        Mockito.verify(con).send(ArgumentMatchers.eq("<System><Power_Control><Power>Standby</Power></Power_Control></System>"));
        Mockito.verify(con).send(ArgumentMatchers.eq("<System><Party_Mode><Mode>On</Mode></Party_Mode></System>"));
        Mockito.verify(con).send(ArgumentMatchers.eq("<System><Party_Mode><Volume><Mute>On</Mute></Volume></Party_Mode></System>"));
    }

    @Test
    public void given_RX_V3900_when_partyMode_then_noCommandSend() throws Exception {
        // given
        setupFor(TestModels.RX_V3900);
        // when
        subject.setPartyMode(true);
        subject.setPartyModeMute(true);
        subject.setPartyModeVolume(true);
        // then
        Mockito.verify(con, Mockito.never()).send(ArgumentMatchers.anyString());
    }

    @Test
    public void given_RX_V3900_when_update_then_parsesStateAndDoesNotUpdateStateForPartyMode() throws Exception {
        // given
        setupFor(TestModels.RX_V3900);
        // when
        subject.update();
        // then
        ArgumentCaptor<SystemControlState> stateArg = ArgumentCaptor.forClass(SystemControlState.class);
        Mockito.verify(systemControlStateListener, Mockito.only()).systemControlStateChanged(stateArg.capture());
        Mockito.verify(con, Mockito.never()).sendReceive(ArgumentMatchers.eq("<System><Party_Mode><Mode>GetParam</Mode></Party_Mode></System>"));
        SystemControlState state = stateArg.getValue();
        Assert.assertTrue(state.power);
        Assert.assertFalse(state.partyMode);
    }
}

