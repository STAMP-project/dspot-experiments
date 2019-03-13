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
package org.openhab.binding.homematic.internal.communicator.virtual;


import CommonTriggerEvents.DOUBLE_PRESSED;
import CommonTriggerEvents.LONG_PRESSED;
import CommonTriggerEvents.SHORT_PRESSED;
import java.io.IOException;
import org.eclipse.smarthome.test.java.JavaTest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.openhab.binding.homematic.internal.misc.HomematicClientException;
import org.openhab.binding.homematic.internal.misc.MiscUtils;
import org.openhab.binding.homematic.internal.model.HmDatapoint;


/**
 * Tests for {@link ButtonVirtualDatapointHandler}.
 *
 * @author Michael Reitler - Initial Contribution
 */
public class ButtonDatapointTest extends JavaTest {
    private static final int DISABLE_DATAPOINT_DELAY = 50;

    private ButtonDatapointTest.MockEventReceiver mockEventReceiver;

    private final ButtonVirtualDatapointHandler bvdpHandler = new ButtonVirtualDatapointHandler();

    @Test
    public void testShortPress() throws IOException, HomematicClientException {
        HmDatapoint shortPressDp = createPressDatapoint("PRESS_SHORT", Boolean.TRUE);
        HmDatapoint buttonVirtualDatapoint = getButtonVirtualDatapoint(shortPressDp);
        mockEventReceiver.eventReceived(shortPressDp);
        MatcherAssert.assertThat(buttonVirtualDatapoint.getValue(), CoreMatchers.is(SHORT_PRESSED));
    }

    @Test
    public void testLongPress() throws IOException, HomematicClientException {
        HmDatapoint longPressDp = createPressDatapoint("PRESS_LONG", Boolean.TRUE);
        HmDatapoint buttonVirtualDatapoint = getButtonVirtualDatapoint(longPressDp);
        mockEventReceiver.eventReceived(longPressDp);
        MatcherAssert.assertThat(buttonVirtualDatapoint.getValue(), CoreMatchers.is(LONG_PRESSED));
    }

    @Test
    public void testUnsupportedEvents() throws IOException, HomematicClientException {
        HmDatapoint contPressDp = createPressDatapoint("PRESS_CONT", Boolean.TRUE);
        HmDatapoint contButtonVirtualDatapoint = getButtonVirtualDatapoint(contPressDp);
        mockEventReceiver.eventReceived(contPressDp);
        HmDatapoint releaseDp = createPressDatapoint("PRESS_LONG_RELEASE", Boolean.TRUE);
        HmDatapoint releaseButtonVirtualDatapoint = getButtonVirtualDatapoint(releaseDp);
        mockEventReceiver.eventReceived(releaseDp);
        HmDatapoint crapDp = createPressDatapoint("CRAP", Boolean.TRUE);
        HmDatapoint crapButtonVirtualDatapoint = getButtonVirtualDatapoint(releaseDp);
        mockEventReceiver.eventReceived(crapDp);
        MatcherAssert.assertThat(contButtonVirtualDatapoint.getValue(), CoreMatchers.nullValue());
        MatcherAssert.assertThat(releaseButtonVirtualDatapoint.getValue(), CoreMatchers.nullValue());
        MatcherAssert.assertThat(crapButtonVirtualDatapoint.getValue(), CoreMatchers.nullValue());
    }

    @Test
    public void testDoublePress() throws IOException, InterruptedException, HomematicClientException {
        HmDatapoint shortPressDp = createPressDatapoint("PRESS_SHORT", Boolean.TRUE);
        HmDatapoint buttonVirtualDatapoint = getButtonVirtualDatapoint(shortPressDp);
        mockEventReceiver.eventReceived(shortPressDp);
        MatcherAssert.assertThat(buttonVirtualDatapoint.getValue(), CoreMatchers.is(SHORT_PRESSED));
        Thread.sleep(((ButtonDatapointTest.DISABLE_DATAPOINT_DELAY) / 2));
        shortPressDp.setValue(Boolean.TRUE);
        mockEventReceiver.eventReceived(shortPressDp);
        MatcherAssert.assertThat(buttonVirtualDatapoint.getValue(), CoreMatchers.is(DOUBLE_PRESSED));
        Thread.sleep(((ButtonDatapointTest.DISABLE_DATAPOINT_DELAY) * 2));
        shortPressDp.setValue(Boolean.TRUE);
        mockEventReceiver.eventReceived(shortPressDp);
        MatcherAssert.assertThat(buttonVirtualDatapoint.getValue(), CoreMatchers.is(SHORT_PRESSED));
    }

    /**
     * Mock parts of {@linkplain org.openhab.binding.homematic.internal.communicator.AbstractHomematicGateway}
     */
    private class MockEventReceiver {
        public void eventReceived(HmDatapoint dp) throws IOException, HomematicClientException {
            if (bvdpHandler.canHandleEvent(dp)) {
                bvdpHandler.handleEvent(null, dp);
            }
            if ((dp.isPressDatapoint()) && (MiscUtils.isTrueValue(dp.getValue()))) {
                disableDatapoint(dp);
            }
        }

        private void disableDatapoint(HmDatapoint dp) {
            new Thread(() -> {
                try {
                    Thread.sleep(ButtonDatapointTest.DISABLE_DATAPOINT_DELAY);
                    dp.setValue(Boolean.FALSE);
                } catch (InterruptedException e) {
                }
            }).start();
        }
    }
}

