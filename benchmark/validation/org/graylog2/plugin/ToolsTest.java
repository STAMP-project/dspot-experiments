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
package org.graylog2.plugin;


import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.google.common.net.InetAddresses;
import java.io.EOFException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.graylog2.inputs.TestHelper;
import org.junit.Assert;
import org.junit.Test;


public class ToolsTest {
    @Test
    public void testGetUriWithPort() throws Exception {
        final URI uriWithPort = new URI("http://example.com:12345");
        final URI httpUriWithoutPort = new URI("http://example.com");
        final URI httpsUriWithoutPort = new URI("https://example.com");
        final URI uriWithUnknownSchemeAndWithoutPort = new URI("foobar://example.com");
        Assert.assertEquals(12345, Tools.getUriWithPort(uriWithPort, 1).getPort());
        Assert.assertEquals(80, Tools.getUriWithPort(httpUriWithoutPort, 1).getPort());
        Assert.assertEquals(443, Tools.getUriWithPort(httpsUriWithoutPort, 1).getPort());
        Assert.assertEquals(1, Tools.getUriWithPort(uriWithUnknownSchemeAndWithoutPort, 1).getPort());
    }

    @Test
    public void testGetUriWithScheme() throws Exception {
        Assert.assertEquals("gopher", Tools.getUriWithScheme(new URI("http://example.com"), "gopher").getScheme());
        Assert.assertNull(Tools.getUriWithScheme(new URI("http://example.com"), null).getScheme());
        Assert.assertNull(Tools.getUriWithScheme(null, "http"));
    }

    @Test
    public void testGetPID() {
        String result = Tools.getPID();
        Assert.assertTrue(((Integer.parseInt(result)) > 0));
    }

    @Test
    public void testGetUTCTimestamp() {
        Assert.assertTrue(((Tools.getUTCTimestamp()) > 0));
    }

    @Test
    public void testGetUTCTimestampWithMilliseconds() {
        Assert.assertTrue(((Tools.getUTCTimestampWithMilliseconds()) > 0.0));
        Assert.assertTrue(((Tools.getUTCTimestampWithMilliseconds(Instant.now().toEpochMilli())) > 0.0));
    }

    @Test
    public void testGetLocalHostname() {
        String hostname = Tools.getLocalHostname();
        Assert.assertFalse(hostname.isEmpty());
    }

    @Test
    public void testSyslogLevelToReadable() {
        Assert.assertEquals("Invalid", Tools.syslogLevelToReadable(1337));
        Assert.assertEquals("Emergency", Tools.syslogLevelToReadable(0));
        Assert.assertEquals("Critical", Tools.syslogLevelToReadable(2));
        Assert.assertEquals("Informational", Tools.syslogLevelToReadable(6));
    }

    @Test
    public void testSyslogFacilityToReadable() {
        Assert.assertEquals("Unknown", Tools.syslogFacilityToReadable(9001));
        Assert.assertEquals("kernel", Tools.syslogFacilityToReadable(0));
        Assert.assertEquals("FTP", Tools.syslogFacilityToReadable(11));
        Assert.assertEquals("local6", Tools.syslogFacilityToReadable(22));
    }

    @Test
    public void testGetSystemInformation() {
        String result = Tools.getSystemInformation();
        Assert.assertTrue(((result.trim().length()) > 0));
    }

    @Test
    public void testDecompressZlib() throws IOException {
        final String testString = "Teststring 123";
        final byte[] compressed = TestHelper.zlibCompress(testString);
        Assert.assertEquals(testString, Tools.decompressZlib(compressed));
    }

    @Test
    public void testDecompressZlibBomb() throws IOException, URISyntaxException {
        final URL url = Resources.getResource("org/graylog2/plugin/zlib64mb.raw");
        final byte[] testData = Files.readAllBytes(Paths.get(url.toURI()));
        assertThat(Tools.decompressZlib(testData, 1024)).hasSize(1024);
    }

    @Test
    public void testDecompressGzip() throws IOException {
        final String testString = "Teststring 123";
        final byte[] compressed = TestHelper.gzipCompress(testString);
        Assert.assertEquals(testString, Tools.decompressGzip(compressed));
    }

    @Test
    public void testDecompressGzipBomb() throws IOException, URISyntaxException {
        final URL url = Resources.getResource("org/graylog2/plugin/gzip64mb.gz");
        final byte[] testData = Files.readAllBytes(Paths.get(url.toURI()));
        assertThat(Tools.decompressGzip(testData, 1024)).hasSize(1024);
    }

    @Test(expected = EOFException.class)
    public void testDecompressGzipEmptyInput() throws IOException {
        Tools.decompressGzip(new byte[0]);
    }

    /**
     * ruby-1.9.2-p136 :001 > [Time.now.to_i, 2.days.ago.to_i]
     *  => [1322063329, 1321890529]
     */
    @Test
    public void testGetTimestampDaysAgo() {
        Assert.assertEquals(1321890529, Tools.getTimestampDaysAgo(1322063329, 2));
    }

    @Test
    public void testEncodeBase64() {
        Assert.assertEquals("bG9sd2F0LmVuY29kZWQ=", Tools.encodeBase64("lolwat.encoded"));
    }

    @Test
    public void testDecodeBase64() {
        Assert.assertEquals("lolwat.encoded", Tools.decodeBase64("bG9sd2F0LmVuY29kZWQ="));
    }

    @Test
    public void testGenerateServerId() {
        String id = Tools.generateServerId();
        /* Make sure it has dashes in it. We need that to build a short ID later.
        Short version: Everything falls apart if this is not an UUID-style ID.
         */
        Assert.assertTrue(id.contains("-"));
    }

    @Test
    public void testAsSortedSet() {
        List<Integer> sortMe = Lists.newArrayList();
        sortMe.add(0);
        sortMe.add(2);
        sortMe.add(6);
        sortMe.add(1);
        sortMe.add(10);
        sortMe.add(25);
        sortMe.add(11);
        SortedSet<Integer> expected = new TreeSet<>();
        expected.add(0);
        expected.add(1);
        expected.add(2);
        expected.add(6);
        expected.add(10);
        expected.add(11);
        expected.add(25);
        Assert.assertEquals(expected, Tools.asSortedSet(sortMe));
    }

    @Test
    public void testSafeSubstring() {
        Assert.assertNull(Tools.safeSubstring(null, 10, 20));
        Assert.assertNull(Tools.safeSubstring("", 10, 20));
        Assert.assertNull(Tools.safeSubstring("foo", (-1), 2));
        Assert.assertNull(Tools.safeSubstring("foo", 1, 0));
        Assert.assertNull(Tools.safeSubstring("foo", 5, 2));
        Assert.assertNull(Tools.safeSubstring("foo", 1, 1));
        Assert.assertNull(Tools.safeSubstring("foo", 2, 1));
        Assert.assertEquals("justatest", Tools.safeSubstring("justatest", 0, 9));
        Assert.assertEquals("tat", Tools.safeSubstring("justatest", 3, 6));
        Assert.assertEquals("just", Tools.safeSubstring("justatest", 0, 4));
        Assert.assertEquals("atest", Tools.safeSubstring("justatest", 4, 9));
    }

    @Test
    public void testGetDouble() throws Exception {
        Assert.assertEquals(null, Tools.getDouble(null));
        Assert.assertEquals(null, Tools.getDouble(""));
        Assert.assertEquals(0.0, Tools.getDouble(0), 0);
        Assert.assertEquals(1.0, Tools.getDouble(1), 0);
        Assert.assertEquals(1.42, Tools.getDouble(1.42), 0);
        Assert.assertEquals(9001.0, Tools.getDouble(9001), 0);
        Assert.assertEquals(9001.23, Tools.getDouble(9001.23), 0);
        Assert.assertEquals(1253453.0, Tools.getDouble(((long) (1253453))), 0);
        Assert.assertEquals(88.0, Tools.getDouble("88"), 0);
        Assert.assertEquals(1.42, Tools.getDouble("1.42"), 0);
        Assert.assertEquals(null, Tools.getDouble("lol NOT A NUMBER"));
        Assert.assertEquals(null, Tools.getDouble(new HashMap<String, String>()));
        Assert.assertEquals(42.23, Tools.getDouble(new Object() {
            @Override
            public String toString() {
                return "42.23";
            }
        }), 0);
    }

    @Test
    public void testGetNumberForDifferentFormats() {
        Assert.assertEquals(1, Tools.getNumber(1, null).intValue(), 1);
        Assert.assertEquals(1.0, Tools.getNumber(1, null).doubleValue(), 0.0);
        Assert.assertEquals(42, Tools.getNumber(42.23, null).intValue());
        Assert.assertEquals(42.23, Tools.getNumber(42.23, null).doubleValue(), 0.0);
        Assert.assertEquals(17, Tools.getNumber("17", null).intValue());
        Assert.assertEquals(17.0, Tools.getNumber("17", null).doubleValue(), 0.0);
        Assert.assertEquals(23, Tools.getNumber("23.42", null).intValue());
        Assert.assertEquals(23.42, Tools.getNumber("23.42", null).doubleValue(), 0.0);
        Assert.assertNull(Tools.getNumber(null, null));
        Assert.assertNull(Tools.getNumber(null, null));
        Assert.assertEquals(1, Tools.getNumber(null, 1).intValue());
        Assert.assertEquals(1.0, Tools.getNumber(null, 1).doubleValue(), 0.0);
    }

    @Test
    public void testTimeFormatterWithOptionalMilliseconds() {
        /* We can actually consider this working if it does not throw parser exceptions.
        Check the toString() representation to make sure though. (using startsWith()
        to avoid problems on test systems in other time zones, that are not CEST and do
        not end with a +02:00 or shit.)
         */
        Assert.assertTrue(org.joda.time.DateTime.parse("2013-09-15 02:21:02", Tools.timeFormatterWithOptionalMilliseconds()).toString().startsWith("2013-09-15T02:21:02.000"));
        Assert.assertTrue(org.joda.time.DateTime.parse("2013-09-15 02:21:02.123", Tools.timeFormatterWithOptionalMilliseconds()).toString().startsWith("2013-09-15T02:21:02.123"));
        Assert.assertTrue(org.joda.time.DateTime.parse("2013-09-15 02:21:02.12", Tools.timeFormatterWithOptionalMilliseconds()).toString().startsWith("2013-09-15T02:21:02.120"));
        Assert.assertTrue(org.joda.time.DateTime.parse("2013-09-15 02:21:02.1", Tools.timeFormatterWithOptionalMilliseconds()).toString().startsWith("2013-09-15T02:21:02.100"));
    }

    @Test
    public void testElasticSearchTimeFormatToISO8601() {
        Assert.assertTrue(Tools.elasticSearchTimeFormatToISO8601("2014-07-31 14:21:02.000").equals("2014-07-31T14:21:02.000Z"));
    }

    @Test
    public void testTimeFromDouble() {
        Assert.assertTrue(Tools.dateTimeFromDouble(1.381076986306509E9).toString().startsWith("2013-10-06T"));
        Assert.assertTrue(Tools.dateTimeFromDouble(1381076986).toString().startsWith("2013-10-06T"));
        Assert.assertTrue(Tools.dateTimeFromDouble(1.3810790856E9).toString().startsWith("2013-10-06T"));
        Assert.assertTrue(Tools.dateTimeFromDouble(1.38107908506E9).toString().startsWith("2013-10-06T"));
    }

    @Test
    public void uriWithTrailingSlashReturnsNullIfURIIsNull() {
        Assert.assertNull(Tools.uriWithTrailingSlash(null));
    }

    @Test
    public void uriWithTrailingSlashReturnsURIWithTrailingSlashIfTrailingSlashIsMissing() throws URISyntaxException {
        final String uri = "http://example.com/api/";
        Assert.assertEquals(URI.create(uri), Tools.uriWithTrailingSlash(URI.create("http://example.com/api")));
    }

    @Test
    public void uriWithTrailingSlashReturnsURIIfTrailingSlashIsPresent() {
        final URI uri = URI.create("http://example.com/api/");
        Assert.assertEquals(uri, Tools.uriWithTrailingSlash(uri));
    }

    @Test
    public void normalizeURIAddsSchemaAndPortAndPathWithTrailingSlash() {
        final URI uri = URI.create("foobar://example.com");
        Assert.assertEquals(URI.create("quux://example.com:1234/foobar/"), Tools.normalizeURI(uri, "quux", 1234, "/foobar"));
    }

    @Test
    public void normalizeURIReturnsNormalizedURI() {
        final URI uri = URI.create("foobar://example.com//foo/////bar");
        Assert.assertEquals(URI.create("quux://example.com:1234/foo/bar/"), Tools.normalizeURI(uri, "quux", 1234, "/baz"));
    }

    @Test
    public void normalizeURIReturnsNullIfURIIsNull() {
        Assert.assertNull(Tools.normalizeURI(null, "http", 1234, "/baz"));
    }

    @Test
    public void isWildcardAddress() {
        Assert.assertTrue(Tools.isWildcardInetAddress(InetAddresses.forString("0.0.0.0")));
        Assert.assertTrue(Tools.isWildcardInetAddress(InetAddresses.forString("::")));
        Assert.assertFalse(Tools.isWildcardInetAddress(null));
        Assert.assertFalse(Tools.isWildcardInetAddress(InetAddresses.forString("127.0.0.1")));
        Assert.assertFalse(Tools.isWildcardInetAddress(InetAddresses.forString("::1")));
        Assert.assertFalse(Tools.isWildcardInetAddress(InetAddresses.forString("198.51.100.23")));
        Assert.assertFalse(Tools.isWildcardInetAddress(InetAddresses.forString("2001:DB8::42")));
    }
}

