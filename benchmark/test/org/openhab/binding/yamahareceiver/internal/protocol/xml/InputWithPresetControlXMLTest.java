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
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openhab.binding.yamahareceiver.internal.TestModels;
import org.openhab.binding.yamahareceiver.internal.state.PresetInfoState;
import org.openhab.binding.yamahareceiver.internal.state.PresetInfoStateListener;


/**
 * Unit test for {@link InputWithPresetControlXML}.
 *
 * @author Tomasz Maruszak - Initial contribution
 */
public class InputWithPresetControlXMLTest extends AbstractZoneControlXMLTest {
    private InputWithPresetControlXML subject;

    @Mock
    private PresetInfoStateListener presetInfoStateListener;

    @Captor
    private ArgumentCaptor<PresetInfoState> presetInfoStateArg;

    @Test
    public void given_RX_S601D_and_NET_RADIO_when_preset1_then_sendsProperCommand() throws Exception {
        given(TestModels.RX_S601D, INPUT_NET_RADIO, ( ctx) -> {
            ctx.respondWith("<NET_RADIO><Play_Control><Preset><Preset_Sel_Item>GetParam</Preset_Sel_Item></Preset></Play_Control></NET_RADIO>", "NET_RADIO_Play_Control_Preset_Preset_Sel_Item.xml");
            ctx.respondWith("<NET_RADIO><Play_Control><Preset><Preset_Sel>GetParam</Preset_Sel></Preset></Play_Control></NET_RADIO>", "NET_RADIO_Play_Control_Preset_Preset_Sel.xml");
        });
        // when
        subject.selectItemByPresetNumber(1);
        // then
        Mockito.verify(presetInfoStateListener).presetInfoUpdated(presetInfoStateArg.capture());
        PresetInfoState state = presetInfoStateArg.getValue();
        Mockito.verify(con).send(ArgumentMatchers.eq("<NET_RADIO><Play_Control><Preset><Preset_Sel>1</Preset_Sel></Preset></Play_Control></NET_RADIO>"));
        Assert.assertEquals("1 : NET RADIO  Chilli ZET PL", state.presetChannelNames.get(0).getName());
        Assert.assertEquals(1, state.presetChannelNames.get(0).getValue());
        Assert.assertEquals("2 : NET RADIO  Polskie Radio 24", state.presetChannelNames.get(1).getName());
        Assert.assertEquals(2, state.presetChannelNames.get(1).getValue());
    }

    @Test
    public void given_RX_V3900_and_NET_RADIO_when_preset1_then_sendsProperCommand() throws Exception {
        given(TestModels.RX_V3900, INPUT_NET_RADIO, ( ctx) -> {
            ctx.respondWith("<NET_USB><Play_Control><Preset><Preset_Sel_Item>GetParam</Preset_Sel_Item></Preset></Play_Control></NET_USB>", "NET_USB_Play_Control_Preset_Preset_Sel_Item.xml");
            ctx.respondWith("<NET_USB><Play_Control><Preset><Preset_Sel>GetParam</Preset_Sel></Preset></Play_Control></NET_USB>", "NET_USB_Play_Control_Preset_Preset_Sel.xml");
        });
        // when
        subject.selectItemByPresetNumber(1);
        // then
        Mockito.verify(con).send(ArgumentMatchers.eq("<NET_USB><Play_Control><Preset><Preset_Sel>1</Preset_Sel></Preset></Play_Control></NET_USB>"));
    }

    @Test
    public void given_RX_V3900_and_TUNER_when_preset1_then_sendsProperCommand() throws Exception {
        given(TestModels.RX_V3900, INPUT_TUNER, ( ctx) -> {
            ctx.respondWith("<Tuner><Play_Control><Preset><Preset_Sel_Item>GetParam</Preset_Sel_Item></Preset></Play_Control></Tuner>", "Tuner_Play_Control_Preset_Preset_Sel_Item.xml");
            ctx.respondWith("<Tuner><Play_Control><Preset><Preset_Sel>GetParam</Preset_Sel></Preset></Play_Control></Tuner>", "Tuner_Play_Control_Preset_Preset_Sel.xml");
        });
        // when
        subject.selectItemByPresetNumber(101);
        subject.selectItemByPresetNumber(212);
        // then
        Mockito.verify(con).send(ArgumentMatchers.eq("<Tuner><Play_Control><Preset><Preset_Sel>A1</Preset_Sel></Preset></Play_Control></Tuner>"));
        Mockito.verify(con).send(ArgumentMatchers.eq("<Tuner><Play_Control><Preset><Preset_Sel>B12</Preset_Sel></Preset></Play_Control></Tuner>"));
    }

    @Test
    public void given_RX_V3900_and_TUNER_when_update_then_populatesStateCorrectly() throws Exception {
        given(TestModels.RX_V3900, INPUT_TUNER, ( ctx) -> {
            ctx.respondWith("<Tuner><Play_Control><Preset><Preset_Sel_Item>GetParam</Preset_Sel_Item></Preset></Play_Control></Tuner>", "Tuner_Play_Control_Preset_Preset_Sel_Item.xml");
            ctx.respondWith("<Tuner><Play_Control><Preset><Preset_Sel>GetParam</Preset_Sel></Preset></Play_Control></Tuner>", "Tuner_Play_Control_Preset_Preset_Sel.xml");
        });
        // when
        subject.update();
        // then
        Mockito.verify(presetInfoStateListener).presetInfoUpdated(presetInfoStateArg.capture());
        PresetInfoState state = presetInfoStateArg.getValue();
        // A1
        Assert.assertEquals(101, state.presetChannel);
        Assert.assertEquals(16, state.presetChannelNames.size());
        Assert.assertEquals("A1", state.presetChannelNames.get(0).getName());
        Assert.assertEquals(101, state.presetChannelNames.get(0).getValue());
        Assert.assertEquals("A2", state.presetChannelNames.get(1).getName());
        Assert.assertEquals(102, state.presetChannelNames.get(1).getValue());
    }
}

