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
package org.jivesoftware.smackx.geoloc.provider;


import GeoLocation.ELEMENT;
import GeoLocation.NAMESPACE;
import java.net.URI;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.InitExtensions;
import org.jivesoftware.smackx.geoloc.packet.GeoLocation;
import org.junit.Assert;
import org.junit.Test;
import org.jxmpp.util.XmppDateTime;


public class GeoLocationProviderTest extends InitExtensions {
    @Test
    public void testGeoLocationProviderWithNoDatumSet() throws Exception {
        // @formatter:off
        final String geoLocationString = "<message from='portia@merchantofvenice.lit'" + ((((((((((((((((((((((((((" to='bassanio@merchantofvenice.lit'>" + "<geoloc xmlns='http://jabber.org/protocol/geoloc'>") + "<accuracy>23</accuracy>") + "<alt>1000</alt>") + "<altaccuracy>10</altaccuracy>") + "<area>Delhi</area>") + "<bearing>10</bearing>") + "<building>Small Building</building>") + "<country>India</country>") + "<countrycode>IN</countrycode>") + "<description>My Description</description>") + "<error>90</error>") + "<floor>top</floor>") + "<lat>25.098345</lat>") + "<locality>awesome</locality>") + "<lon>77.992034</lon>") + "<postalcode>110085</postalcode>") + "<region>North</region>") + "<room>small</room>") + "<speed>250.0</speed>") + "<street>Wall Street</street>") + "<text>Unit Testing GeoLocation</text>") + "<timestamp>2004-02-19</timestamp>") + "<tzo>+5:30</tzo>") + "<uri>http://xmpp.org</uri>") + "</geoloc>") + "</message>");
        // @formatter:on
        Message messageWithGeoLocation = PacketParserUtils.parseStanza(geoLocationString);
        Assert.assertNotNull(messageWithGeoLocation);
        GeoLocation geoLocation = messageWithGeoLocation.getExtension(ELEMENT, NAMESPACE);
        Assert.assertNotNull(geoLocation);
        Assert.assertEquals(((Double) (23.0)), geoLocation.getAccuracy());
        Assert.assertEquals(((Double) (1000.0)), geoLocation.getAlt());
        Assert.assertEquals(((Double) (10.0)), geoLocation.getAltAccuracy());
        Assert.assertEquals("Delhi", geoLocation.getArea());
        Assert.assertEquals(((Double) (10.0)), geoLocation.getBearing());
        Assert.assertEquals("Small Building", geoLocation.getBuilding());
        Assert.assertEquals("India", geoLocation.getCountry());
        Assert.assertEquals("IN", geoLocation.getCountryCode());
        Assert.assertEquals("WGS84", geoLocation.getDatum());
        Assert.assertEquals("My Description", geoLocation.getDescription());
        Assert.assertNull(geoLocation.getError());
        Assert.assertEquals("top", geoLocation.getFloor());
        Assert.assertEquals(((Double) (25.098345)), geoLocation.getLat());
        Assert.assertEquals("awesome", geoLocation.getLocality());
        Assert.assertEquals(((Double) (77.992034)), geoLocation.getLon());
        Assert.assertEquals("110085", geoLocation.getPostalcode());
        Assert.assertEquals("North", geoLocation.getRegion());
        Assert.assertEquals("small", geoLocation.getRoom());
        Assert.assertEquals(((Double) (250.0)), geoLocation.getSpeed());
        Assert.assertEquals("Wall Street", geoLocation.getStreet());
        Assert.assertEquals("Unit Testing GeoLocation", geoLocation.getText());
        Assert.assertEquals(XmppDateTime.parseDate("2004-02-19"), geoLocation.getTimestamp());
        Assert.assertEquals("+5:30", geoLocation.getTzo());
        Assert.assertEquals(new URI("http://xmpp.org"), geoLocation.getUri());
    }

    @Test
    public void testGeoLocationWithDatumSet() throws Exception {
        // @formatter:off
        final String geoLocationString = "<message from='portia@merchantofvenice.lit'" + (((((((((((((((((((((((((((" to='bassanio@merchantofvenice.lit'>" + "<geoloc xmlns='http://jabber.org/protocol/geoloc'>") + "<accuracy>23</accuracy>") + "<alt>1000</alt>") + "<altaccuracy>10</altaccuracy>") + "<area>Delhi</area>") + "<bearing>10</bearing>") + "<building>Small Building</building>") + "<country>India</country>") + "<countrycode>IN</countrycode>") + "<datum>Test Datum</datum>") + "<description>My Description</description>") + "<error>90</error>") + "<floor>top</floor>") + "<lat>25.098345</lat>") + "<locality>awesome</locality>") + "<lon>77.992034</lon>") + "<postalcode>110085</postalcode>") + "<region>North</region>") + "<room>small</room>") + "<speed>250.0</speed>") + "<street>Wall Street</street>") + "<text>Unit Testing GeoLocation</text>") + "<timestamp>2004-02-19</timestamp>") + "<tzo>+5:30</tzo>") + "<uri>http://xmpp.org</uri>") + "</geoloc>") + "</message>");
        // @formatter:on
        Message messageWithGeoLocation = PacketParserUtils.parseStanza(geoLocationString);
        Assert.assertNotNull(messageWithGeoLocation);
        GeoLocation geoLocation = messageWithGeoLocation.getExtension(ELEMENT, NAMESPACE);
        Assert.assertNotNull(geoLocation);
        Assert.assertEquals(((Double) (23.0)), geoLocation.getAccuracy());
        Assert.assertEquals(((Double) (1000.0)), geoLocation.getAlt());
        Assert.assertEquals(((Double) (10.0)), geoLocation.getAltAccuracy());
        Assert.assertEquals("Delhi", geoLocation.getArea());
        Assert.assertEquals(((Double) (10.0)), geoLocation.getBearing());
        Assert.assertEquals("Small Building", geoLocation.getBuilding());
        Assert.assertEquals("India", geoLocation.getCountry());
        Assert.assertEquals("IN", geoLocation.getCountryCode());
        Assert.assertEquals("Test Datum", geoLocation.getDatum());
        Assert.assertEquals("My Description", geoLocation.getDescription());
        Assert.assertNull(geoLocation.getError());
        Assert.assertEquals("top", geoLocation.getFloor());
        Assert.assertEquals(((Double) (25.098345)), geoLocation.getLat());
        Assert.assertEquals("awesome", geoLocation.getLocality());
        Assert.assertEquals(((Double) (77.992034)), geoLocation.getLon());
        Assert.assertEquals("110085", geoLocation.getPostalcode());
        Assert.assertEquals("North", geoLocation.getRegion());
        Assert.assertEquals("small", geoLocation.getRoom());
        Assert.assertEquals(((Double) (250.0)), geoLocation.getSpeed());
        Assert.assertEquals("Wall Street", geoLocation.getStreet());
        Assert.assertEquals("Unit Testing GeoLocation", geoLocation.getText());
        Assert.assertEquals(XmppDateTime.parseDate("2004-02-19"), geoLocation.getTimestamp());
        Assert.assertEquals("+5:30", geoLocation.getTzo());
        Assert.assertEquals(new URI("http://xmpp.org"), geoLocation.getUri());
    }

    @Test
    public void testGeoLocationWithoutAccuracySetAndWithErrorSet() throws Exception {
        // @formatter:off
        final String geoLocationString = "<message from='portia@merchantofvenice.lit'" + ((((" to='bassanio@merchantofvenice.lit'>" + "<geoloc xmlns='http://jabber.org/protocol/geoloc'>") + "<error>90</error>") + "</geoloc>") + "</message>");
        // @formatter:on
        Message messageWithGeoLocation = PacketParserUtils.parseStanza(geoLocationString);
        GeoLocation geoLocation = messageWithGeoLocation.getExtension(ELEMENT, NAMESPACE);
        Assert.assertEquals(((Double) (90.0)), geoLocation.getError());
    }

    @Test
    public void testGeoLocationWithAccuracySetAndWithoutErrorSet() throws Exception {
        // @formatter:off
        final String geoLocationString = "<message from='portia@merchantofvenice.lit'" + ((((" to='bassanio@merchantofvenice.lit'>" + "<geoloc xmlns='http://jabber.org/protocol/geoloc'>") + "<accuracy>90</accuracy>") + "</geoloc>") + "</message>");
        // @formatter:on
        Message messageWithGeoLocation = PacketParserUtils.parseStanza(geoLocationString);
        GeoLocation geoLocation = messageWithGeoLocation.getExtension(ELEMENT, NAMESPACE);
        Assert.assertEquals(((Double) (90.0)), geoLocation.getAccuracy());
    }

    @Test
    public void testGeoLocationWithAccuracySetAndErrorSet() throws Exception {
        // @formatter:off
        final String geoLocationString = "<message from='portia@merchantofvenice.lit'" + (((((" to='bassanio@merchantofvenice.lit'>" + "<geoloc xmlns='http://jabber.org/protocol/geoloc'>") + "<accuracy>90</accuracy>") + "<error>100</error>") + "</geoloc>") + "</message>");
        // @formatter:on
        Message messageWithGeoLocation = PacketParserUtils.parseStanza(geoLocationString);
        GeoLocation geoLocation = messageWithGeoLocation.getExtension(ELEMENT, NAMESPACE);
        Assert.assertEquals(((Double) (90.0)), geoLocation.getAccuracy());
        Assert.assertNull(geoLocation.getError());
    }
}

