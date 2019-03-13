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
import org.mockito.Mockito;
import org.openhab.binding.yamahareceiver.internal.TestModels;
import org.openhab.binding.yamahareceiver.internal.state.ZoneControlState;


/**
 * Unit test for {@link ZoneControlXML}.
 *
 * @author Tomasz Maruszak - Initial contribution
 */
public class ZoneControlXMLTest extends AbstractZoneControlXMLTest {
    private ZoneControlXML subject;

    @Test
    public void given_RX_S601D_when_power_then_sendsProperCommand() throws Exception {
        when_power_then_sendsProperCommand(TestModels.RX_S601D);
    }

    @Test
    public void given_RX_V3900_when_power_then_sendsProperCommand() throws Exception {
        when_power_then_sendsProperCommand(TestModels.RX_V3900);
    }

    @Test
    public void given_RX_S601D_when_mute_then_sendsProperCommand() throws Exception {
        given(TestModels.RX_S601D);
        // when
        subject.setMute(true);
        subject.setMute(false);
        // then
        Mockito.verify(con).send(ArgumentMatchers.eq("<Main_Zone><Volume><Mute>On</Mute></Volume></Main_Zone>"));
        Mockito.verify(con).send(ArgumentMatchers.eq("<Main_Zone><Volume><Mute>Off</Mute></Volume></Main_Zone>"));
    }

    @Test
    public void given_RX_V3900_when_mute_then_sendsProperCommand() throws Exception {
        given(TestModels.RX_V3900);
        // when
        subject.setMute(true);
        subject.setMute(false);
        // then
        Mockito.verify(con).send(ArgumentMatchers.eq("<Main_Zone><Vol><Mute>On</Mute></Vol></Main_Zone>"));
        Mockito.verify(con).send(ArgumentMatchers.eq("<Main_Zone><Vol><Mute>Off</Mute></Vol></Main_Zone>"));
    }

    @Test
    public void given_RX_S601D_when_volume_then_sendsProperCommand() throws Exception {
        given(TestModels.RX_S601D);
        // when
        subject.setVolumeDB((-2));
        // then
        Mockito.verify(con).send(ArgumentMatchers.eq("<Main_Zone><Volume><Lvl><Val>-20</Val><Exp>1</Exp><Unit>dB</Unit></Lvl></Volume></Main_Zone>"));
    }

    @Test
    public void given_RX_V3900_when_volume_then_sendsProperCommand() throws Exception {
        given(TestModels.RX_V3900);
        // when
        subject.setVolumeDB((-2));
        // then
        Mockito.verify(con).send(ArgumentMatchers.eq("<Main_Zone><Vol><Lvl><Val>-20</Val><Exp>1</Exp><Unit>dB</Unit></Lvl></Vol></Main_Zone>"));
    }

    @Test
    public void given_RX_S601D_when_input_then_sendsProperCommand() throws Exception {
        when_input_then_sendsProperCommand(TestModels.RX_S601D);
    }

    @Test
    public void given_RX_V3900_when_input_then_sendsProperCommand() throws Exception {
        when_input_then_sendsProperCommand(TestModels.RX_V3900);
    }

    @Test
    public void given_RX_S601D_when_surroundProgram_then_sendsProperCommand() throws Exception {
        given(TestModels.RX_S601D);
        // when
        subject.setSurroundProgram("Adventure");
        // then
        Mockito.verify(con).send(ArgumentMatchers.eq("<Main_Zone><Surround><Program_Sel><Current><Sound_Program>Adventure</Sound_Program></Current></Program_Sel></Surround></Main_Zone>"));
    }

    @Test
    public void given_RX_V3900_when_surroundProgram_then_sendsProperCommand() throws Exception {
        given(TestModels.RX_V3900);
        // when
        subject.setSurroundProgram("Adventure");
        // then
        Mockito.verify(con).send(ArgumentMatchers.eq("<Main_Zone><Surr><Pgm_Sel><Straight>Off</Straight><Pgm>Adventure</Pgm></Pgm_Sel></Surr></Main_Zone>"));
    }

    @Test
    public void given_RX_S601D_when_surroundProgramStraight_then_sendsProperCommand() throws Exception {
        given(TestModels.RX_S601D);
        // when
        subject.setSurroundProgram("Straight");
        // then
        Mockito.verify(con).send(ArgumentMatchers.eq("<Main_Zone><Surround><Program_Sel><Current><Straight>On</Straight></Current></Program_Sel></Surround></Main_Zone>"));
    }

    @Test
    public void given_RX_V3900_when_surroundProgramStraight_then_sendsProperCommand() throws Exception {
        given(TestModels.RX_V3900);
        // when
        subject.setSurroundProgram("Straight");
        // then
        Mockito.verify(con).send(ArgumentMatchers.eq("<Main_Zone><Surr><Pgm_Sel><Straight>On</Straight></Pgm_Sel></Surr></Main_Zone>"));
    }

    @Test
    public void given_HTR_4069_when_dialogueLevel_then_sendsProperCommand() throws Exception {
        given(TestModels.HTR_4069);
        // when
        subject.setDialogueLevel(10);
        // then
        Mockito.verify(con).send(ArgumentMatchers.eq("<Main_Zone><Sound_Video><Dialogue_Adjust><Dialogue_Lvl>10</Dialogue_Lvl></Dialogue_Adjust></Sound_Video></Main_Zone>"));
    }

    @Test
    public void given_RX_S601D_when_update_then_readsStateProperly() throws Exception {
        given(TestModels.RX_S601D);
        // when
        subject.update();
        // then
        ArgumentCaptor<ZoneControlState> stateArg = ArgumentCaptor.forClass(ZoneControlState.class);
        Mockito.verify(zoneControlStateListener).zoneStateChanged(stateArg.capture());
        ZoneControlState state = stateArg.getValue();
        Assert.assertNotNull(state);
        Assert.assertEquals(false, state.power);
        Assert.assertEquals(false, state.mute);
        Assert.assertEquals((-43), state.volumeDB, 0);
        Assert.assertEquals("Spotify", state.inputID);
        Assert.assertEquals("5ch Stereo", state.surroundProgram);
        // this model does not support dialogue level
        Assert.assertEquals(0, state.dialogueLevel);
    }

    @Test
    public void given_RX_V3900_when_update_then_readsStateProperly() throws Exception {
        given(TestModels.RX_V3900);
        // when
        subject.update();
        // then
        ArgumentCaptor<ZoneControlState> stateArg = ArgumentCaptor.forClass(ZoneControlState.class);
        Mockito.verify(zoneControlStateListener).zoneStateChanged(stateArg.capture());
        ZoneControlState state = stateArg.getValue();
        Assert.assertNotNull(state);
        Assert.assertEquals(true, state.power);
        Assert.assertEquals(false, state.mute);
        Assert.assertEquals((-46), state.volumeDB, 0);
        Assert.assertEquals("TV", state.inputID);
        Assert.assertEquals("2ch Stereo", state.surroundProgram);
        // this model does not support dialogue level
        Assert.assertEquals(0, state.dialogueLevel);
    }

    @Test
    public void given_HTR_4069_when_update_then_readsStateProperly() throws Exception {
        given(TestModels.HTR_4069);
        // when
        subject.update();
        // then
        ArgumentCaptor<ZoneControlState> stateArg = ArgumentCaptor.forClass(ZoneControlState.class);
        Mockito.verify(zoneControlStateListener).zoneStateChanged(stateArg.capture());
        ZoneControlState state = stateArg.getValue();
        Assert.assertNotNull(state);
        Assert.assertEquals(false, state.power);
        Assert.assertEquals(false, state.mute);
        Assert.assertEquals((-46), state.volumeDB, 0);
        Assert.assertEquals("TUNER", state.inputID);
        Assert.assertEquals("5ch Stereo", state.surroundProgram);
        Assert.assertEquals(1, state.dialogueLevel);
    }
}

