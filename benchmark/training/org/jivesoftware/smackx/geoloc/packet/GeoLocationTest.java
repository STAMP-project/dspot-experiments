/**
 * Copyright 2015-2017 Ishan Khanna, Fernando Ramirez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.smackx.geoloc.packet;


import GeoLocation.ELEMENT;
import GeoLocation.NAMESPACE;
import java.net.URI;
import java.util.Calendar;
import java.util.TimeZone;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.InitExtensions;
import org.jivesoftware.smackx.time.packet.Time;
import org.junit.Assert;
import org.junit.Test;
import org.jxmpp.util.XmppDateTime;


/**
 * Unit tests for GeoLocation.
 *
 * @author Ishan Khanna
 */
public class GeoLocationTest extends InitExtensions {
    @Test
    public void negativeTimezoneTest() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT-830"));
        Time time = new Time(calendar);
        GeoLocation geoLocation = new GeoLocation.Builder().setTzo(time.getTzo()).build();
        Assert.assertEquals("-8:30", geoLocation.getTzo());
    }

    @Test
    public void positiveTimezonTest() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+530"));
        Time time = new Time(calendar);
        GeoLocation geoLocation = new GeoLocation.Builder().setTzo(time.getTzo()).build();
        Assert.assertEquals("+5:30", geoLocation.getTzo());
    }

    @Test
    public void accuracyTest() {
        GeoLocation geoLocation = new GeoLocation.Builder().setAccuracy(1.34).build();
        Assert.assertEquals(((Double) (1.34)), geoLocation.getAccuracy());
    }

    @Test
    public void altAccuracyTest() {
        GeoLocation geoLocation = new GeoLocation.Builder().setAltAccuracy(1.52).build();
        Assert.assertEquals(((Double) (1.52)), geoLocation.getAltAccuracy());
    }

    @Test
    public void toXMLMethodTest() throws Exception {
        // @formatter:off
        final String geoLocationMessageString = "<message from='portia@merchantofvenice.lit'" + ((((((((((((((((((((((((((" to='bassanio@merchantofvenice.lit'>" + "<geoloc xmlns='http://jabber.org/protocol/geoloc'>") + "<accuracy>23</accuracy>") + "<alt>1000</alt>") + "<altaccuracy>10</altaccuracy>") + "<area>Delhi</area>") + "<bearing>10</bearing>") + "<building>Small Building</building>") + "<country>India</country>") + "<countrycode>IN</countrycode>") + "<description>My Description</description>") + "<error>90</error>") + "<floor>top</floor>") + "<lat>25.098345</lat>") + "<locality>awesome</locality>") + "<lon>77.992034</lon>") + "<postalcode>110085</postalcode>") + "<region>North</region>") + "<room>small</room>") + "<speed>250.0</speed>") + "<street>Wall Street</street>") + "<text>Unit Testing GeoLocation</text>") + "<timestamp>2004-02-19</timestamp>") + "<tzo>+5:30</tzo>") + "<uri>http://xmpp.org</uri>") + "</geoloc>") + "</message>");
        // @formatter:on
        Message messageWithGeoLocation = PacketParserUtils.parseStanza(geoLocationMessageString);
        Assert.assertNotNull(messageWithGeoLocation);
        GeoLocation geoLocation = messageWithGeoLocation.getExtension(ELEMENT, NAMESPACE);
        Assert.assertNotNull(geoLocation);
        Assert.assertNotNull(geoLocation.toXML());
        GeoLocation constructedGeoLocation = GeoLocation.builder().setAccuracy(23.0).setAlt(1000.0).setAltAccuracy(10.0).setArea("Delhi").setBearing(10.0).setBuilding("Small Building").setCountry("India").setCountryCode("IN").setDescription("My Description").setError(90.0).setFloor("top").setLat(25.098345).setLocality("awesome").setLon(77.992034).setPostalcode("110085").setRegion("North").setRoom("small").setSpeed(250.0).setStreet("Wall Street").setText("Unit Testing GeoLocation").setTimestamp(XmppDateTime.parseDate("2004-02-19")).setTzo("+5:30").setUri(new URI("http://xmpp.org")).build();
        Assert.assertEquals(constructedGeoLocation.toXML().toString(), geoLocation.toXML().toString());
    }
}

