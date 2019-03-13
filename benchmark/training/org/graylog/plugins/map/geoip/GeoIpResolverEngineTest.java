/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog.plugins.map.geoip;


import com.codahale.metrics.MetricRegistry;
import com.eaio.uuid.UUID;
import com.google.common.collect.Maps;
import com.google.common.net.InetAddresses;
import java.util.Map;
import org.graylog.plugins.map.ConditionalRunner;
import org.graylog.plugins.map.ResourceExistsCondition;
import org.graylog.plugins.map.config.GeoIpResolverConfig;
import org.graylog2.plugin.Message;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(ConditionalRunner.class)
@ResourceExistsCondition(GeoIpResolverEngineTest.GEO_LITE2_CITY_MMDB)
public class GeoIpResolverEngineTest {
    static final String GEO_LITE2_CITY_MMDB = "/GeoLite2-City.mmdb";

    private MetricRegistry metricRegistry;

    private GeoIpResolverConfig config;

    @Test
    public void getIpFromFieldValue() {
        final GeoIpResolverEngine resolver = new GeoIpResolverEngine(config, metricRegistry);
        final String ip = "127.0.0.1";
        Assert.assertEquals(InetAddresses.forString(ip), resolver.getIpFromFieldValue(ip));
        Assert.assertNull(resolver.getIpFromFieldValue("Message from \"127.0.0.1\""));
        Assert.assertNull(resolver.getIpFromFieldValue("Test message with no IP"));
    }

    @Test
    public void trimFieldValueBeforeLookup() {
        final GeoIpResolverEngine resolver = new GeoIpResolverEngine(config, metricRegistry);
        final String ip = "   2001:4860:4860::8888\t\n";
        Assert.assertNotNull(resolver.getIpFromFieldValue(ip));
    }

    @Test
    public void extractGeoLocationInformation() {
        final GeoIpResolverEngine resolver = new GeoIpResolverEngine(config, metricRegistry);
        Assert.assertTrue("Should extract geo location information from public addresses", resolver.extractGeoLocationInformation("1.2.3.4").isPresent());
        Assert.assertFalse("Should not extract geo location information from private addresses", resolver.extractGeoLocationInformation("192.168.0.1").isPresent());
        Assert.assertFalse("Should not extract geo location information numeric fields", resolver.extractGeoLocationInformation(42).isPresent());
        Assert.assertTrue("Should extract geo location information IP address fields", resolver.extractGeoLocationInformation(InetAddresses.forString("1.2.3.4")).isPresent());
    }

    @Test
    public void disabledFilterTest() {
        final GeoIpResolverEngine resolver = new GeoIpResolverEngine(config.toBuilder().enabled(false).build(), metricRegistry);
        final Map<String, Object> messageFields = Maps.newHashMap();
        messageFields.put("_id", new UUID().toString());
        messageFields.put("source", "192.168.0.1");
        messageFields.put("message", "Hello from 1.2.3.4");
        messageFields.put("extracted_ip", "1.2.3.4");
        messageFields.put("ipv6", "2001:4860:4860::8888");
        final Message message = new Message(messageFields);
        final boolean filtered = resolver.filter(message);
        Assert.assertFalse("Message should not be filtered out", filtered);
        Assert.assertEquals("Filter should not add new message fields", messageFields.size(), message.getFields().size());
    }

    @Test
    public void filterResolvesIpGeoLocation() {
        final GeoIpResolverEngine resolver = new GeoIpResolverEngine(config, metricRegistry);
        final Map<String, Object> messageFields = Maps.newHashMap();
        messageFields.put("_id", new UUID().toString());
        messageFields.put("source", "192.168.0.1");
        messageFields.put("message", "Hello from 1.2.3.4");
        messageFields.put("extracted_ip", "1.2.3.4");
        messageFields.put("gl2_remote_ip", "1.2.3.4");
        messageFields.put("ipv6", "2001:4860:4860::8888");
        final Message message = new Message(messageFields);
        final boolean filtered = resolver.filter(message);
        Assert.assertFalse("Message should not be filtered out", filtered);
        Assert.assertEquals("Should have looked up three IPs", 3, metricRegistry.timer(name(GeoIpResolverEngine.class, "resolveTime")).getCount());
        assertFieldNotResolved(message, "source", "Should not have resolved private IP");
        assertFieldNotResolved(message, "message", "Should have resolved public IP");
        assertFieldNotResolved(message, "gl2_remote_ip", "Should not have resolved text with an IP");
        assertFieldResolved(message, "extracted_ip", "Should have resolved public IP");
        assertFieldResolved(message, "ipv6", "Should have resolved public IPv6");
    }
}

