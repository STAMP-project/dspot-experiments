/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.dmx.internal.config;


import DmxChannel.DMX_MAX_VALUE;
import DmxDimmerItem.DIMMER_STEP_SIZE;
import IncreaseDecreaseType.DECREASE;
import IncreaseDecreaseType.INCREASE;
import PercentType.ZERO;
import org.junit.Test;
import org.mockito.Mockito;
import org.openhab.binding.dmx.DmxService;
import org.openhab.core.library.types.PercentType;
import org.openhab.model.item.binding.BindingConfigParseException;


/**
 * DmxDimmerItem configuration tests.
 *
 * @author Davy Vanherbergen
 * @since 1.2.0
 */
public class DmxDimmerItemTest extends DmxSwitchItemTest {
    @Test
    public void decreasesWhenDecreaseCommandReceived() throws BindingConfigParseException {
        DmxItem item = getValidInstance();
        DmxService service = Mockito.mock(DmxService.class);
        Mockito.when(service.getChannelValue(3)).thenReturn(95);
        Mockito.when(service.getChannelValue(4)).thenReturn(3);
        item.processCommand(service, DECREASE);
        Mockito.verify(service).decreaseChannel(3, DIMMER_STEP_SIZE);
        Mockito.verify(service).decreaseChannel(4, DIMMER_STEP_SIZE);
    }

    @Test
    public void increasesWhenIncreaseCommandReceived() throws BindingConfigParseException {
        DmxItem item = getValidInstance();
        DmxService service = Mockito.mock(DmxService.class);
        Mockito.when(service.getChannelValue(3)).thenReturn(95);
        Mockito.when(service.getChannelValue(4)).thenReturn(3);
        item.processCommand(service, INCREASE);
        Mockito.verify(service).enableChannel(3);
        Mockito.verify(service).increaseChannel(3, DIMMER_STEP_SIZE);
        Mockito.verify(service).enableChannel(4);
        Mockito.verify(service).increaseChannel(4, DIMMER_STEP_SIZE);
    }

    @Test
    public void canBeSetWithPercentType() throws BindingConfigParseException {
        DmxItem item = getValidInstance();
        DmxService service = Mockito.mock(DmxService.class);
        Mockito.when(service.getChannelValue(3)).thenReturn(0);
        Mockito.when(service.getChannelValue(4)).thenReturn(155);
        item.processCommand(service, new PercentType(0));
        Mockito.verify(service).setChannelValue(3, DMX_MAX_VALUE);
        Mockito.verify(service).setChannelValue(3, ZERO);
        Mockito.verify(service).setChannelValue(4, DMX_MAX_VALUE);
        Mockito.verify(service).setChannelValue(4, ZERO);
    }
}

