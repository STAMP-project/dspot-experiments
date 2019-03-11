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
package org.openhab.binding.ntp.test;


import DateTimeType.DATE_PATTERN_WITH_TZ_AND_MS;
import NtpBindingConstants.CHANNEL_DATE_TIME;
import NtpBindingConstants.CHANNEL_STRING;
import NtpBindingConstants.PROPERTY_DATE_TIME_FORMAT;
import NtpBindingConstants.PROPERTY_TIMEZONE;
import NtpHandler.DATE_PATTERN_WITH_TZ;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.TimeZone;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.items.GenericItem;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.core.library.types.DateTimeType;
import org.eclipse.smarthome.core.thing.ManagedThingProvider;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingRegistry;
import org.eclipse.smarthome.core.thing.type.ChannelTypeProvider;
import org.eclipse.smarthome.core.thing.type.ChannelTypeUID;
import org.eclipse.smarthome.test.java.JavaOSGiTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.ntp.internal.handler.NtpHandler;
import org.openhab.binding.ntp.server.SimpleNTPServer;


/**
 * OSGi tests for the {@link NtpHandler}
 *
 * @author Petar Valchev - Initial Contribution
 * @author Markus Rathgeb - Migrated tests from Groovy to pure Java
 * @author Erdoan Hadzhiyusein - Migrated tests to Java 8 and integrated the new DateTimeType
 */
public class NtpOSGiTest extends JavaOSGiTest {
    private static TimeZone systemTimeZone;

    private static Locale locale;

    private NtpHandler ntpHandler;

    private Thing ntpThing;

    private GenericItem testItem;

    private ManagedThingProvider managedThingProvider;

    private ThingRegistry thingRegistry;

    private ItemRegistry itemRegistry;

    private ChannelTypeProvider channelTypeProvider;

    private static final ZoneId DEFAULT_TIME_ZONE_ID = ZoneId.of("Europe/Bucharest");

    private static final String TEST_TIME_ZONE_ID = "America/Los_Angeles";

    private static final String TEST_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss z";

    private static final String TEST_ITEM_NAME = "testItem";

    private static final String TEST_THING_ID = "testThingId";

    // No bundle in ESH is exporting a package from which we can use item types
    // as constants, so we will use String.
    private static final String ACCEPTED_ITEM_TYPE_STRING = "String";

    private static final String ACCEPTED_ITEM_TYPE_DATE_TIME = "DateTime";

    private static final String TEST_HOSTNAME = "127.0.0.1";

    private static final int TEST_PORT = 9002;

    static SimpleNTPServer timeServer;

    private ChannelTypeUID channelTypeUID;

    enum UpdateEventType {

        HANDLE_COMMAND("handleCommand"),
        CHANNEL_LINKED("channelLinked");
        private final String updateEventType;

        private UpdateEventType(String updateEventType) {
            this.updateEventType = updateEventType;
        }

        public String getUpdateEventType() {
            return updateEventType;
        }
    }

    @Test
    public void testStringChannelTimeZoneUpdate() {
        final String expectedTimeZonePDT = "PDT";
        final String expectedTimeZonePST = "PST";
        Configuration configuration = new Configuration();
        configuration.put(PROPERTY_TIMEZONE, NtpOSGiTest.TEST_TIME_ZONE_ID);
        Configuration channelConfig = new Configuration();
        /* Set the format of the date, so it is updated in the item registry in
        a format from which we can easily get the time zone.
         */
        channelConfig.put(PROPERTY_DATE_TIME_FORMAT, NtpOSGiTest.TEST_DATE_TIME_FORMAT);
        initialize(configuration, CHANNEL_STRING, NtpOSGiTest.ACCEPTED_ITEM_TYPE_STRING, channelConfig, null);
        String timeZoneFromItemRegistry = getStringChannelTimeZoneFromItemRegistry();
        Assert.assertThat(timeZoneFromItemRegistry, CoreMatchers.is(CoreMatchers.anyOf(CoreMatchers.equalTo(expectedTimeZonePDT), CoreMatchers.equalTo(expectedTimeZonePST))));
    }

    @Test
    public void testDateTimeChannelTimeZoneUpdate() {
        Configuration configuration = new Configuration();
        configuration.put(PROPERTY_TIMEZONE, NtpOSGiTest.TEST_TIME_ZONE_ID);
        initialize(configuration, CHANNEL_DATE_TIME, NtpOSGiTest.ACCEPTED_ITEM_TYPE_DATE_TIME, null, null);
        String testItemState = getItemState(NtpOSGiTest.ACCEPTED_ITEM_TYPE_DATE_TIME).toString();
        assertFormat(testItemState, DATE_PATTERN_WITH_TZ_AND_MS);
        ZonedDateTime timeZoneFromItemRegistry = getZonedDateTime();
        ZoneOffset expectedOffset = ZoneId.of(NtpOSGiTest.TEST_TIME_ZONE_ID).getRules().getOffset(timeZoneFromItemRegistry.toInstant());
        Assert.assertEquals(expectedOffset, timeZoneFromItemRegistry.getOffset());
    }

    @Test
    public void testDateTimeChannelCalendarTimeZoneUpdate() {
        Configuration configuration = new Configuration();
        configuration.put(PROPERTY_TIMEZONE, NtpOSGiTest.TEST_TIME_ZONE_ID);
        initialize(configuration, CHANNEL_DATE_TIME, NtpOSGiTest.ACCEPTED_ITEM_TYPE_DATE_TIME, null, null);
        ZonedDateTime timeZoneIdFromItemRegistry = getZonedDateTime();
        ZoneOffset expectedOffset = ZoneId.of(NtpOSGiTest.TEST_TIME_ZONE_ID).getRules().getOffset(timeZoneIdFromItemRegistry.toInstant());
        Assert.assertEquals(expectedOffset, timeZoneIdFromItemRegistry.getOffset());
    }

    @Test
    public void testStringChannelDefaultTimeZoneUpdate() {
        final String expectedTimeZoneEEST = "EEST";
        final String expectedTimeZoneEET = "EET";
        Configuration configuration = new Configuration();
        Configuration channelConfig = new Configuration();
        /* Set the format of the date, so it is updated in the item registry in
        a format from which we can easily get the time zone.
         */
        channelConfig.put(PROPERTY_DATE_TIME_FORMAT, NtpOSGiTest.TEST_DATE_TIME_FORMAT);
        // Initialize with configuration with no time zone property set.
        initialize(configuration, CHANNEL_STRING, NtpOSGiTest.ACCEPTED_ITEM_TYPE_STRING, null, null);
        String timeZoneFromItemRegistry = getStringChannelTimeZoneFromItemRegistry();
        Assert.assertThat(timeZoneFromItemRegistry, CoreMatchers.is(CoreMatchers.anyOf(CoreMatchers.equalTo(expectedTimeZoneEEST), CoreMatchers.equalTo(expectedTimeZoneEET))));
    }

    @Test
    public void testDateTimeChannelDefaultTimeZoneUpdate() {
        ZonedDateTime zoned = ZonedDateTime.now();
        ZoneOffset expectedTimeZone = zoned.getOffset();
        Configuration configuration = new Configuration();
        // Initialize with configuration with no time zone property set.
        initialize(configuration, CHANNEL_DATE_TIME, NtpOSGiTest.ACCEPTED_ITEM_TYPE_DATE_TIME, null, null);
        String testItemState = getItemState(NtpOSGiTest.ACCEPTED_ITEM_TYPE_DATE_TIME).toString();
        assertFormat(testItemState, DATE_PATTERN_WITH_TZ_AND_MS);
        ZoneOffset timeZoneFromItemRegistry = new DateTimeType(testItemState).getZonedDateTime().getOffset();
        Assert.assertEquals(expectedTimeZone, timeZoneFromItemRegistry);
    }

    @Test
    public void testStringChannelFormatting() {
        final String formatPattern = "EEE, d MMM yyyy HH:mm:ss z";
        Configuration configuration = new Configuration();
        Configuration channelConfig = new Configuration();
        channelConfig.put(PROPERTY_DATE_TIME_FORMAT, formatPattern);
        initialize(configuration, CHANNEL_STRING, NtpOSGiTest.ACCEPTED_ITEM_TYPE_STRING, channelConfig, null);
        String dateFromItemRegistry = getItemState(NtpOSGiTest.ACCEPTED_ITEM_TYPE_STRING).toString();
        assertFormat(dateFromItemRegistry, formatPattern);
    }

    @Test
    public void testStringChannelDefaultFormatting() {
        Configuration configuration = new Configuration();
        // Initialize with configuration with no property for formatting set.
        initialize(configuration, CHANNEL_STRING, NtpOSGiTest.ACCEPTED_ITEM_TYPE_STRING, null, null);
        String dateFromItemRegistryString = getItemState(NtpOSGiTest.ACCEPTED_ITEM_TYPE_STRING).toString();
        assertFormat(dateFromItemRegistryString, DATE_PATTERN_WITH_TZ);
    }

    @Test
    public void testEmptyStringPropertyFormatting() {
        Configuration configuration = new Configuration();
        Configuration channelConfig = new Configuration();
        // Empty string
        channelConfig.put(PROPERTY_DATE_TIME_FORMAT, "");
        initialize(configuration, CHANNEL_STRING, NtpOSGiTest.ACCEPTED_ITEM_TYPE_STRING, channelConfig, null);
        String dateFromItemRegistry = getItemState(NtpOSGiTest.ACCEPTED_ITEM_TYPE_STRING).toString();
        assertFormat(dateFromItemRegistry, DATE_PATTERN_WITH_TZ);
    }

    @Test
    public void testNullPropertyFormatting() {
        Configuration configuration = new Configuration();
        Configuration channelConfig = new Configuration();
        channelConfig.put(PROPERTY_DATE_TIME_FORMAT, null);
        initialize(configuration, CHANNEL_STRING, NtpOSGiTest.ACCEPTED_ITEM_TYPE_STRING, channelConfig, null);
        String dateFromItemRegistry = getItemState(NtpOSGiTest.ACCEPTED_ITEM_TYPE_STRING).toString();
        assertFormat(dateFromItemRegistry, DATE_PATTERN_WITH_TZ);
    }

    @Test
    public void testDateTimeChannelWithUnknownHost() {
        assertCommunicationError(NtpOSGiTest.ACCEPTED_ITEM_TYPE_DATE_TIME);
    }

    @Test
    public void testStringChannelWithUnknownHost() {
        assertCommunicationError(NtpOSGiTest.ACCEPTED_ITEM_TYPE_STRING);
    }

    @Test
    public void testStringChannelHandleCommand() {
        assertEventIsReceived(NtpOSGiTest.UpdateEventType.HANDLE_COMMAND, CHANNEL_STRING, NtpOSGiTest.ACCEPTED_ITEM_TYPE_STRING);
    }

    @Test
    public void testDateTimeChannelHandleCommand() {
        assertEventIsReceived(NtpOSGiTest.UpdateEventType.HANDLE_COMMAND, CHANNEL_DATE_TIME, NtpOSGiTest.ACCEPTED_ITEM_TYPE_DATE_TIME);
    }

    @Test
    public void testStringChannelLinking() {
        assertEventIsReceived(NtpOSGiTest.UpdateEventType.CHANNEL_LINKED, CHANNEL_STRING, NtpOSGiTest.ACCEPTED_ITEM_TYPE_STRING);
    }

    @Test
    public void testDateTimeChannelLinking() {
        assertEventIsReceived(NtpOSGiTest.UpdateEventType.CHANNEL_LINKED, CHANNEL_DATE_TIME, NtpOSGiTest.ACCEPTED_ITEM_TYPE_DATE_TIME);
    }
}

