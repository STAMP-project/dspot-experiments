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
package org.openhab.binding.fsinternetradio.test;


import FSInternetRadioBindingConstants.CHANNEL_MUTE;
import FSInternetRadioBindingConstants.CHANNEL_PLAY_INFO_NAME;
import FSInternetRadioBindingConstants.CHANNEL_PLAY_INFO_TEXT;
import FSInternetRadioBindingConstants.CHANNEL_POWER;
import FSInternetRadioBindingConstants.CHANNEL_PRESET;
import IncreaseDecreaseType.INCREASE;
import OnOffType.OFF;
import OnOffType.ON;
import UpDownType.UP;
import java.util.ArrayList;
import java.util.HashMap;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerCallback;
import org.eclipse.smarthome.test.TestPortUtil;
import org.eclipse.smarthome.test.TestServer;
import org.eclipse.smarthome.test.java.JavaTest;
import org.junit.Test;
import org.openhab.binding.fsinternetradio.internal.FSInternetRadioBindingConstants;
import org.openhab.binding.fsinternetradio.internal.handler.FSInternetRadioHandler;

import static org.openhab.binding.fsinternetradio.internal.FSInternetRadioBindingConstants.FSInternetRadioBindingConstants.CHANNEL_MODE;
import static org.openhab.binding.fsinternetradio.internal.FSInternetRadioBindingConstants.FSInternetRadioBindingConstants.CHANNEL_MUTE;
import static org.openhab.binding.fsinternetradio.internal.FSInternetRadioBindingConstants.FSInternetRadioBindingConstants.CHANNEL_PLAY_INFO_NAME;
import static org.openhab.binding.fsinternetradio.internal.FSInternetRadioBindingConstants.FSInternetRadioBindingConstants.CHANNEL_PLAY_INFO_TEXT;
import static org.openhab.binding.fsinternetradio.internal.FSInternetRadioBindingConstants.FSInternetRadioBindingConstants.CHANNEL_PRESET;
import static org.openhab.binding.fsinternetradio.internal.FSInternetRadioBindingConstants.FSInternetRadioBindingConstants.CHANNEL_VOLUME_ABSOLUTE;
import static org.openhab.binding.fsinternetradio.internal.FSInternetRadioBindingConstants.FSInternetRadioBindingConstants.CHANNEL_VOLUME_PERCENT;
import static org.openhab.binding.fsinternetradio.internal.FSInternetRadioBindingConstants.FSInternetRadioBindingConstants.THING_TYPE_RADIO;


/**
 * OSGi tests for the {@link FSInternetRadioHandler}.
 *
 * @author Mihaela Memova - Initial contribution
 * @author Markus Rathgeb - Migrated from Groovy to pure Java test, made more robust
 * @author Velin Yordanov - Migrated to mockito
 */
public class FSInternetRadioHandlerJavaTest extends JavaTest {
    private static final String DEFAULT_TEST_THING_NAME = "testRadioThing";

    private static final String DEFAULT_TEST_ITEM_NAME = "testItem";

    private final String VOLUME = "volume";

    // The request send for preset is "SET/netRemote.nav.action.selectPreset";
    private static final String PRESET = "Preset";

    private static final int TIMEOUT = 10 * 1000;

    private static final ThingTypeUID DEFAULT_THING_TYPE_UID = THING_TYPE_RADIO;

    private static final ThingUID DEFAULT_THING_UID = new ThingUID(FSInternetRadioHandlerJavaTest.DEFAULT_THING_TYPE_UID, FSInternetRadioHandlerJavaTest.DEFAULT_TEST_THING_NAME);

    private static final RadioServiceDummy radioServiceDummy = new RadioServiceDummy();

    /**
     * In order to test a specific channel, it is necessary to create a Thing with two channels - CHANNEL_POWER
     * and the tested channel. So before each test, the power channel is created and added
     * to an ArrayList of channels. Then in the tests an additional channel is created and added to the ArrayList
     * when it's needed.
     */
    private Channel powerChannel;

    private ThingHandlerCallback callback;

    private static TestServer server;

    /**
     * A HashMap which saves all the 'channel-acceppted_item_type' pairs.
     * It is set before all the tests.
     */
    private static HashMap<String, String> acceptedItemTypes;

    /**
     * ArrayList of channels which is used to initialize a radioThing in the test cases.
     */
    private final ArrayList<Channel> channels = new ArrayList<Channel>();

    private FSInternetRadioHandler radioHandler;

    private Thing radioThing;

    private static HttpClient httpClient;

    // default configuration properties
    private static final String DEFAULT_CONFIG_PROPERTY_IP = "127.0.0.1";

    private static final String DEFAULT_CONFIG_PROPERTY_PIN = "1234";

    private static final int DEFAULT_CONFIG_PROPERTY_PORT = TestPortUtil.findFreePort();

    /**
     * The default refresh interval is 60 seconds. For the purposes of the tests it is set to 1 second
     */
    private static final String DEFAULT_CONFIG_PROPERTY_REFRESH = "1";

    private static final Configuration DEFAULT_COMPLETE_CONFIGURATION = FSInternetRadioHandlerJavaTest.createDefaultConfiguration();

    /**
     * Verify OFFLINE Thing status when the IP is NULL.
     */
    @Test
    public void offlineIfNullIp() {
        Configuration config = FSInternetRadioHandlerJavaTest.createConfiguration(null, FSInternetRadioHandlerJavaTest.DEFAULT_CONFIG_PROPERTY_PIN, String.valueOf(FSInternetRadioHandlerJavaTest.DEFAULT_CONFIG_PROPERTY_PORT), FSInternetRadioHandlerJavaTest.DEFAULT_CONFIG_PROPERTY_REFRESH);
        Thing radioThingWithNullIP = initializeRadioThing(config);
        testRadioThingConsideringConfiguration(radioThingWithNullIP);
    }

    /**
     * Verify OFFLINE Thing status when the PIN is empty String.
     */
    @Test
    public void offlineIfEmptyPIN() {
        Configuration config = FSInternetRadioHandlerJavaTest.createConfiguration(FSInternetRadioHandlerJavaTest.DEFAULT_CONFIG_PROPERTY_IP, "", String.valueOf(FSInternetRadioHandlerJavaTest.DEFAULT_CONFIG_PROPERTY_PORT), FSInternetRadioHandlerJavaTest.DEFAULT_CONFIG_PROPERTY_REFRESH);
        Thing radioThingWithEmptyPIN = initializeRadioThing(config);
        testRadioThingConsideringConfiguration(radioThingWithEmptyPIN);
    }

    /**
     * Verify OFFLINE Thing status when the PORT is zero.
     */
    @Test
    public void offlineIfZeroPort() {
        Configuration config = FSInternetRadioHandlerJavaTest.createConfiguration(FSInternetRadioHandlerJavaTest.DEFAULT_CONFIG_PROPERTY_IP, FSInternetRadioHandlerJavaTest.DEFAULT_CONFIG_PROPERTY_PIN, "0", FSInternetRadioHandlerJavaTest.DEFAULT_CONFIG_PROPERTY_REFRESH);
        Thing radioThingWithZeroPort = initializeRadioThing(config);
        testRadioThingConsideringConfiguration(radioThingWithZeroPort);
    }

    /**
     * Verify OFFLINE Thing status when the PIN is wrong.
     */
    @Test
    public void offlineIfWrongPIN() {
        final String wrongPin = "5678";
        Configuration config = FSInternetRadioHandlerJavaTest.createConfiguration(FSInternetRadioHandlerJavaTest.DEFAULT_CONFIG_PROPERTY_IP, wrongPin, String.valueOf(FSInternetRadioHandlerJavaTest.DEFAULT_CONFIG_PROPERTY_PORT), FSInternetRadioHandlerJavaTest.DEFAULT_CONFIG_PROPERTY_REFRESH);
        initializeRadioThing(config);
        waitForAssert(() -> {
            String exceptionMessage = "Radio does not allow connection, maybe wrong pin?";
            verifyCommunicationError(exceptionMessage);
        });
    }

    /**
     * Verify OFFLINE Thing status when the HTTP response cannot be parsed correctly.
     */
    @Test
    public void offlineIfParseError() {
        // create a thing with two channels - the power channel and any of the others
        String modeChannelID = CHANNEL_MODE;
        String acceptedItemType = FSInternetRadioHandlerJavaTest.acceptedItemTypes.get(modeChannelID);
        createChannel(FSInternetRadioHandlerJavaTest.DEFAULT_THING_UID, modeChannelID, acceptedItemType);
        Thing radioThing = initializeRadioThing(FSInternetRadioHandlerJavaTest.DEFAULT_COMPLETE_CONFIGURATION);
        testRadioThingConsideringConfiguration(radioThing);
        ChannelUID modeChannelUID = FSInternetRadioHandlerJavaTest.getChannelUID(radioThing, modeChannelID);
        /* Setting the isInvalidResponseExpected variable to true
        in order to get the incorrect XML response from the servlet
         */
        FSInternetRadioHandlerJavaTest.radioServiceDummy.setInvalidResponse(true);
        // try to handle a command
        radioHandler.handleCommand(modeChannelUID, DecimalType.valueOf("1"));
        waitForAssert(() -> {
            String exceptionMessage = "java.io.IOException: org.xml.sax.SAXParseException; lineNumber: 1; columnNumber: 2;";
            verifyCommunicationError(exceptionMessage);
        });
        FSInternetRadioHandlerJavaTest.radioServiceDummy.setInvalidResponse(false);
    }

    /**
     * Verify the HTTP status is handled correctly when it is not OK_200.
     */
    @Test
    public void httpStatusNokHandling() {
        // create a thing with two channels - the power channel and any of the others
        String modeChannelID = CHANNEL_MODE;
        String acceptedItemType = FSInternetRadioHandlerJavaTest.acceptedItemTypes.get(modeChannelID);
        createChannel(FSInternetRadioHandlerJavaTest.DEFAULT_THING_UID, modeChannelID, acceptedItemType);
        Thing radioThing = initializeRadioThing(FSInternetRadioHandlerJavaTest.DEFAULT_COMPLETE_CONFIGURATION);
        testRadioThingConsideringConfiguration(radioThing);
        // turn-on the radio
        turnTheRadioOn(radioThing);
        /* Setting the needed boolean variable to false, so we can be sure
        that the XML response won't have a OK_200 status
         */
        ChannelUID modeChannelUID = FSInternetRadioHandlerJavaTest.getChannelUID(radioThing, modeChannelID);
        Item modeTestItem = initializeItem(modeChannelUID, CHANNEL_MODE, acceptedItemType);
        // try to handle a command
        radioHandler.handleCommand(modeChannelUID, DecimalType.valueOf("1"));
        waitForAssert(() -> {
            assertSame(UnDefType.NULL, modeTestItem.getState());
        });
    }

    /**
     * Verify ONLINE status of a Thing with complete configuration.
     */
    @Test
    public void verifyOnlineStatus() {
        Thing radioThing = initializeRadioThing(FSInternetRadioHandlerJavaTest.DEFAULT_COMPLETE_CONFIGURATION);
        testRadioThingConsideringConfiguration(radioThing);
    }

    /**
     * Verify the power channel is updated.
     */
    @Test
    public void powerChannelUpdated() {
        Thing radioThing = initializeRadioThing(FSInternetRadioHandlerJavaTest.DEFAULT_COMPLETE_CONFIGURATION);
        testRadioThingConsideringConfiguration(radioThing);
        ChannelUID powerChannelUID = powerChannel.getUID();
        initializeItem(powerChannelUID, FSInternetRadioHandlerJavaTest.DEFAULT_TEST_ITEM_NAME, FSInternetRadioHandlerJavaTest.acceptedItemTypes.get(CHANNEL_POWER));
        radioHandler.handleCommand(powerChannelUID, ON);
        waitForAssert(() -> {
            assertTrue("We should be able to turn on the radio", radioServiceDummy.containsRequestParameter(1, CHANNEL_POWER));
            radioServiceDummy.clearRequestParameters();
        });
        radioHandler.handleCommand(powerChannelUID, OFF);
        waitForAssert(() -> {
            assertTrue("We should be able to turn off the radio", radioServiceDummy.containsRequestParameter(0, CHANNEL_POWER));
            radioServiceDummy.clearRequestParameters();
        });
        /* Setting the needed boolean variable to true, so we can be sure
        that an invalid value will be returned in the XML response
         */
        radioHandler.handleCommand(powerChannelUID, ON);
        waitForAssert(() -> {
            assertTrue("We should be able to turn on the radio", radioServiceDummy.containsRequestParameter(1, CHANNEL_POWER));
            radioServiceDummy.clearRequestParameters();
        });
    }

    /**
     * Verify the mute channel is updated.
     */
    @Test
    public void muteChhannelUpdated() {
        String muteChannelID = CHANNEL_MUTE;
        String acceptedItemType = FSInternetRadioHandlerJavaTest.acceptedItemTypes.get(muteChannelID);
        createChannel(FSInternetRadioHandlerJavaTest.DEFAULT_THING_UID, muteChannelID, acceptedItemType);
        Thing radioThing = initializeRadioThing(FSInternetRadioHandlerJavaTest.DEFAULT_COMPLETE_CONFIGURATION);
        testRadioThingConsideringConfiguration(radioThing);
        turnTheRadioOn(radioThing);
        ChannelUID muteChannelUID = FSInternetRadioHandlerJavaTest.getChannelUID(radioThing, CHANNEL_MUTE);
        initializeItem(muteChannelUID, FSInternetRadioHandlerJavaTest.DEFAULT_TEST_ITEM_NAME, acceptedItemType);
        radioHandler.handleCommand(muteChannelUID, ON);
        waitForAssert(() -> {
            assertTrue("We should be able to mute the radio", radioServiceDummy.containsRequestParameter(1, CHANNEL_MUTE));
            radioServiceDummy.clearRequestParameters();
        });
        radioHandler.handleCommand(muteChannelUID, OFF);
        waitForAssert(() -> {
            assertTrue("We should be able to unmute the radio", radioServiceDummy.containsRequestParameter(0, CHANNEL_MUTE));
            radioServiceDummy.clearRequestParameters();
        });
        /* Setting the needed boolean variable to true, so we can be sure
        that an invalid value will be returned in the XML response
         */
    }

    /**
     * Verify the mode channel is updated.
     */
    @Test
    public void modeChannelUdpated() {
        String modeChannelID = CHANNEL_MODE;
        String acceptedItemType = FSInternetRadioHandlerJavaTest.acceptedItemTypes.get(modeChannelID);
        createChannel(FSInternetRadioHandlerJavaTest.DEFAULT_THING_UID, modeChannelID, acceptedItemType);
        Thing radioThing = initializeRadioThing(FSInternetRadioHandlerJavaTest.DEFAULT_COMPLETE_CONFIGURATION);
        testRadioThingConsideringConfiguration(radioThing);
        turnTheRadioOn(radioThing);
        ChannelUID modeChannelUID = FSInternetRadioHandlerJavaTest.getChannelUID(radioThing, modeChannelID);
        initializeItem(modeChannelUID, FSInternetRadioHandlerJavaTest.DEFAULT_TEST_ITEM_NAME, acceptedItemType);
        radioHandler.handleCommand(modeChannelUID, DecimalType.valueOf("1"));
        waitForAssert(() -> {
            assertTrue("We should be able to update the mode channel correctly", radioServiceDummy.containsRequestParameter(1, CHANNEL_MODE));
            radioServiceDummy.clearRequestParameters();
        });
        /* Setting the needed boolean variable to true, so we can be sure
        that an invalid value will be returned in the XML response
         */
        radioHandler.handleCommand(modeChannelUID, DecimalType.valueOf("3"));
        waitForAssert(() -> {
            assertTrue("We should be able to update the mode channel correctly", radioServiceDummy.containsRequestParameter(3, CHANNEL_MODE));
            radioServiceDummy.clearRequestParameters();
        });
    }

    /**
     * Verify the volume is updated through the CHANNEL_VOLUME_ABSOLUTE using INCREASE and DECREASE commands.
     */
    @Test
    public void volumechannelUpdatedAbsIncDec() {
        String absoluteVolumeChannelID = CHANNEL_VOLUME_ABSOLUTE;
        String absoluteAcceptedItemType = FSInternetRadioHandlerJavaTest.acceptedItemTypes.get(absoluteVolumeChannelID);
        createChannel(FSInternetRadioHandlerJavaTest.DEFAULT_THING_UID, absoluteVolumeChannelID, absoluteAcceptedItemType);
        Thing radioThing = initializeRadioThing(FSInternetRadioHandlerJavaTest.DEFAULT_COMPLETE_CONFIGURATION);
        testRadioThingConsideringConfiguration(radioThing);
        turnTheRadioOn(radioThing);
        ChannelUID absoluteVolumeChannelUID = FSInternetRadioHandlerJavaTest.getChannelUID(radioThing, absoluteVolumeChannelID);
        Item volumeTestItem = initializeItem(absoluteVolumeChannelUID, FSInternetRadioHandlerJavaTest.DEFAULT_TEST_ITEM_NAME, absoluteAcceptedItemType);
        testChannelWithINCREASEAndDECREASECommands(absoluteVolumeChannelUID, volumeTestItem);
    }

    /**
     * Verify the volume is updated through the CHANNEL_VOLUME_ABSOLUTE using UP and DOWN commands.
     */
    @Test
    public void volumeChannelUpdatedAbsUpDown() {
        String absoluteVolumeChannelID = CHANNEL_VOLUME_ABSOLUTE;
        String absoluteAcceptedItemType = FSInternetRadioHandlerJavaTest.acceptedItemTypes.get(absoluteVolumeChannelID);
        createChannel(FSInternetRadioHandlerJavaTest.DEFAULT_THING_UID, absoluteVolumeChannelID, absoluteAcceptedItemType);
        Thing radioThing = initializeRadioThing(FSInternetRadioHandlerJavaTest.DEFAULT_COMPLETE_CONFIGURATION);
        testRadioThingConsideringConfiguration(radioThing);
        turnTheRadioOn(radioThing);
        ChannelUID absoluteVolumeChannelUID = FSInternetRadioHandlerJavaTest.getChannelUID(radioThing, absoluteVolumeChannelID);
        Item volumeTestItem = initializeItem(absoluteVolumeChannelUID, FSInternetRadioHandlerJavaTest.DEFAULT_TEST_ITEM_NAME, absoluteAcceptedItemType);
        testChannelWithUPAndDOWNCommands(absoluteVolumeChannelUID, volumeTestItem);
    }

    /**
     * Verify the invalid values when updating CHANNEL_VOLUME_ABSOLUTE are handled correctly.
     */
    @Test
    public void invalidAbsVolumeValues() {
        String absoluteVolumeChannelID = CHANNEL_VOLUME_ABSOLUTE;
        String absoluteAcceptedItemType = FSInternetRadioHandlerJavaTest.acceptedItemTypes.get(absoluteVolumeChannelID);
        createChannel(FSInternetRadioHandlerJavaTest.DEFAULT_THING_UID, absoluteVolumeChannelID, absoluteAcceptedItemType);
        Thing radioThing = initializeRadioThing(FSInternetRadioHandlerJavaTest.DEFAULT_COMPLETE_CONFIGURATION);
        testRadioThingConsideringConfiguration(radioThing);
        turnTheRadioOn(radioThing);
        ChannelUID absoluteVolumeChannelUID = FSInternetRadioHandlerJavaTest.getChannelUID(radioThing, absoluteVolumeChannelID);
        initializeItem(absoluteVolumeChannelUID, FSInternetRadioHandlerJavaTest.DEFAULT_TEST_ITEM_NAME, absoluteAcceptedItemType);
        // Trying to set a value that is greater than the maximum volume
        radioHandler.handleCommand(absoluteVolumeChannelUID, DecimalType.valueOf("36"));
        waitForAssert(() -> {
            assertTrue("The volume should not exceed the maximum value", radioServiceDummy.containsRequestParameter(32, VOLUME));
            radioServiceDummy.clearRequestParameters();
        });
        // Trying to increase the volume more than its maximum value using the INCREASE command
        radioHandler.handleCommand(absoluteVolumeChannelUID, INCREASE);
        waitForAssert(() -> {
            assertTrue("The volume should not be increased above the maximum value", radioServiceDummy.areRequestParametersEmpty());
            radioServiceDummy.clearRequestParameters();
        });
        // Trying to increase the volume more than its maximum value using the UP command
        radioHandler.handleCommand(absoluteVolumeChannelUID, UP);
        waitForAssert(() -> {
            assertTrue("The volume should not be increased above the maximum value", radioServiceDummy.areRequestParametersEmpty());
            radioServiceDummy.clearRequestParameters();
        });
        // Trying to set a value that is lower than the minimum volume value
        radioHandler.handleCommand(absoluteVolumeChannelUID, DecimalType.valueOf("-10"));
        waitForAssert(() -> {
            assertTrue("The volume should not be decreased below 0", radioServiceDummy.containsRequestParameter(0, VOLUME));
            radioServiceDummy.clearRequestParameters();
        });
        /* Setting the needed boolean variable to true, so we can be sure
        that an invalid value will be returned in the XML response
         */
        // trying to set the volume
        radioHandler.handleCommand(absoluteVolumeChannelUID, DecimalType.valueOf("15"));
        waitForAssert(() -> {
            assertTrue("We should be able to set the volume correctly", radioServiceDummy.containsRequestParameter(15, VOLUME));
            radioServiceDummy.clearRequestParameters();
        });
    }

    /**
     * Verify the volume is updated through the CHANNEL_VOLUME_PERCENT using INCREASE and DECREASE commands.
     */
    @Test
    public void volumeChannelUpdatedPercIncDec() {
        /* The volume is set through the CHANNEL_VOLUME_PERCENT in order to check if
        the absolute volume will be updated properly.
         */
        String absoluteVolumeChannelID = CHANNEL_VOLUME_ABSOLUTE;
        String absoluteAcceptedItemType = FSInternetRadioHandlerJavaTest.acceptedItemTypes.get(absoluteVolumeChannelID);
        createChannel(FSInternetRadioHandlerJavaTest.DEFAULT_THING_UID, absoluteVolumeChannelID, absoluteAcceptedItemType);
        String percentVolumeChannelID = CHANNEL_VOLUME_PERCENT;
        String percentAcceptedItemType = FSInternetRadioHandlerJavaTest.acceptedItemTypes.get(percentVolumeChannelID);
        createChannel(FSInternetRadioHandlerJavaTest.DEFAULT_THING_UID, percentVolumeChannelID, percentAcceptedItemType);
        Thing radioThing = initializeRadioThing(FSInternetRadioHandlerJavaTest.DEFAULT_COMPLETE_CONFIGURATION);
        testRadioThingConsideringConfiguration(radioThing);
        turnTheRadioOn(radioThing);
        ChannelUID absoluteVolumeChannelUID = FSInternetRadioHandlerJavaTest.getChannelUID(radioThing, absoluteVolumeChannelID);
        Item volumeTestItem = initializeItem(absoluteVolumeChannelUID, FSInternetRadioHandlerJavaTest.DEFAULT_TEST_ITEM_NAME, absoluteAcceptedItemType);
        ChannelUID percentVolumeChannelUID = FSInternetRadioHandlerJavaTest.getChannelUID(radioThing, percentVolumeChannelID);
        testChannelWithINCREASEAndDECREASECommands(percentVolumeChannelUID, volumeTestItem);
    }

    /**
     * Verify the volume is updated through the CHANNEL_VOLUME_PERCENT using UP and DOWN commands.
     */
    @Test
    public void volumeChannelUpdatedPercUpDown() {
        /* The volume is set through the CHANNEL_VOLUME_PERCENT in order to check if
        the absolute volume will be updated properly.
         */
        String absoluteVolumeChannelID = CHANNEL_VOLUME_ABSOLUTE;
        String absoluteAcceptedItemType = FSInternetRadioHandlerJavaTest.acceptedItemTypes.get(absoluteVolumeChannelID);
        createChannel(FSInternetRadioHandlerJavaTest.DEFAULT_THING_UID, absoluteVolumeChannelID, absoluteAcceptedItemType);
        String percentVolumeChannelID = CHANNEL_VOLUME_PERCENT;
        String percentAcceptedItemType = FSInternetRadioHandlerJavaTest.acceptedItemTypes.get(percentVolumeChannelID);
        createChannel(FSInternetRadioHandlerJavaTest.DEFAULT_THING_UID, percentVolumeChannelID, percentAcceptedItemType);
        Thing radioThing = initializeRadioThing(FSInternetRadioHandlerJavaTest.DEFAULT_COMPLETE_CONFIGURATION);
        testRadioThingConsideringConfiguration(radioThing);
        turnTheRadioOn(radioThing);
        ChannelUID absoluteVolumeChannelUID = FSInternetRadioHandlerJavaTest.getChannelUID(radioThing, absoluteVolumeChannelID);
        Item volumeTestItem = initializeItem(absoluteVolumeChannelUID, FSInternetRadioHandlerJavaTest.DEFAULT_TEST_ITEM_NAME, absoluteAcceptedItemType);
        ChannelUID percentVolumeChannelUID = FSInternetRadioHandlerJavaTest.getChannelUID(radioThing, percentVolumeChannelID);
        testChannelWithUPAndDOWNCommands(percentVolumeChannelUID, volumeTestItem);
    }

    /**
     * Verify the valid and invalid values when updating CHANNEL_VOLUME_PERCENT are handled correctly.
     */
    @Test
    public void validInvalidPercVolume() {
        String absoluteVolumeChannelID = CHANNEL_VOLUME_ABSOLUTE;
        String absoluteAcceptedItemType = FSInternetRadioHandlerJavaTest.acceptedItemTypes.get(absoluteVolumeChannelID);
        createChannel(FSInternetRadioHandlerJavaTest.DEFAULT_THING_UID, absoluteVolumeChannelID, absoluteAcceptedItemType);
        String percentVolumeChannelID = CHANNEL_VOLUME_PERCENT;
        String percentAcceptedItemType = FSInternetRadioHandlerJavaTest.acceptedItemTypes.get(percentVolumeChannelID);
        createChannel(FSInternetRadioHandlerJavaTest.DEFAULT_THING_UID, percentVolumeChannelID, percentAcceptedItemType);
        Thing radioThing = initializeRadioThing(FSInternetRadioHandlerJavaTest.DEFAULT_COMPLETE_CONFIGURATION);
        testRadioThingConsideringConfiguration(radioThing);
        turnTheRadioOn(radioThing);
        ChannelUID absoluteVolumeChannelUID = FSInternetRadioHandlerJavaTest.getChannelUID(radioThing, absoluteVolumeChannelID);
        initializeItem(absoluteVolumeChannelUID, FSInternetRadioHandlerJavaTest.DEFAULT_TEST_ITEM_NAME, absoluteAcceptedItemType);
        ChannelUID percentVolumeChannelUID = FSInternetRadioHandlerJavaTest.getChannelUID(radioThing, percentVolumeChannelID);
        /* Giving the handler a valid percent value. According to the FrontierSiliconRadio's
        documentation 100 percents correspond to 32 absolute value
         */
        radioHandler.handleCommand(percentVolumeChannelUID, PercentType.valueOf("50"));
        waitForAssert(() -> {
            assertTrue("We should be able to set the volume correctly using percentages.", radioServiceDummy.containsRequestParameter(16, VOLUME));
            radioServiceDummy.clearRequestParameters();
        });
        radioHandler.handleCommand(percentVolumeChannelUID, PercentType.valueOf("15"));
        waitForAssert(() -> {
            assertTrue("We should be able to set the volume correctly using percentages.", radioServiceDummy.containsRequestParameter(4, VOLUME));
            radioServiceDummy.clearRequestParameters();
        });
    }

    /**
     * Verify the preset channel is updated.
     */
    @Test
    public void presetChannelUpdated() {
        String presetChannelID = CHANNEL_PRESET;
        String acceptedItemType = FSInternetRadioHandlerJavaTest.acceptedItemTypes.get(presetChannelID);
        createChannel(FSInternetRadioHandlerJavaTest.DEFAULT_THING_UID, presetChannelID, acceptedItemType);
        Thing radioThing = initializeRadioThing(FSInternetRadioHandlerJavaTest.DEFAULT_COMPLETE_CONFIGURATION);
        testRadioThingConsideringConfiguration(radioThing);
        turnTheRadioOn(radioThing);
        ChannelUID presetChannelUID = FSInternetRadioHandlerJavaTest.getChannelUID(radioThing, CHANNEL_PRESET);
        initializeItem(presetChannelUID, FSInternetRadioHandlerJavaTest.DEFAULT_TEST_ITEM_NAME, acceptedItemType);
        radioHandler.handleCommand(presetChannelUID, DecimalType.valueOf("100"));
        waitForAssert(() -> {
            assertTrue("We should be able to set value to the preset", radioServiceDummy.containsRequestParameter(100, PRESET));
            radioServiceDummy.clearRequestParameters();
        });
    }

    /**
     * Verify the playInfoName channel is updated.
     */
    @Test
    public void playInfoNameChannelUpdated() {
        String playInfoNameChannelID = CHANNEL_PLAY_INFO_NAME;
        String acceptedItemType = FSInternetRadioHandlerJavaTest.acceptedItemTypes.get(playInfoNameChannelID);
        createChannel(FSInternetRadioHandlerJavaTest.DEFAULT_THING_UID, playInfoNameChannelID, acceptedItemType);
        Thing radioThing = initializeRadioThingWithMockedHandler(FSInternetRadioHandlerJavaTest.DEFAULT_COMPLETE_CONFIGURATION);
        testRadioThingConsideringConfiguration(radioThing);
        turnTheRadioOn(radioThing);
        ChannelUID playInfoNameChannelUID = FSInternetRadioHandlerJavaTest.getChannelUID(radioThing, CHANNEL_PLAY_INFO_NAME);
        initializeItem(playInfoNameChannelUID, FSInternetRadioHandlerJavaTest.DEFAULT_TEST_ITEM_NAME, acceptedItemType);
        waitForAssert(() -> {
            verifyOnlineStatusIsSet();
        });
    }

    /**
     * Verify the playInfoText channel is updated.
     */
    @Test
    public void playInfoTextChannelUpdated() {
        String playInfoTextChannelID = CHANNEL_PLAY_INFO_TEXT;
        String acceptedItemType = FSInternetRadioHandlerJavaTest.acceptedItemTypes.get(playInfoTextChannelID);
        createChannel(FSInternetRadioHandlerJavaTest.DEFAULT_THING_UID, playInfoTextChannelID, acceptedItemType);
        Thing radioThing = initializeRadioThingWithMockedHandler(FSInternetRadioHandlerJavaTest.DEFAULT_COMPLETE_CONFIGURATION);
        testRadioThingConsideringConfiguration(radioThing);
        turnTheRadioOn(radioThing);
        ChannelUID playInfoTextChannelUID = FSInternetRadioHandlerJavaTest.getChannelUID(radioThing, CHANNEL_PLAY_INFO_TEXT);
        initializeItem(playInfoTextChannelUID, FSInternetRadioHandlerJavaTest.DEFAULT_TEST_ITEM_NAME, acceptedItemType);
        waitForAssert(() -> {
            verifyOnlineStatusIsSet();
        });
    }
}

