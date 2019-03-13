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
package org.graylog2.inputs.converters;


import org.graylog2.ConfigurationException;
import org.graylog2.plugin.inputs.Converter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;


public class DateConverterTest {
    @Test
    public void testBasicConvert() throws Exception {
        final DateConverter converter = new DateConverter(config("YYYY MMM dd HH:mm:ss", null, null));
        final DateTime result = ((DateTime) (converter.convert("2013 Aug 15 23:15:16")));
        assertThat(result).isNotNull().isEqualTo("2013-08-15T23:15:16.000Z");
    }

    @Test
    public void testAnotherBasicConvert() throws Exception {
        final DateConverter converter = new DateConverter(config("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZZ", "Etc/UTC", null));
        final DateTime date = ((DateTime) (converter.convert("2014-05-19T00:30:43.116+00:00")));
        assertThat(date).isNotNull().isEqualTo("2014-05-19T00:30:43.116Z");
    }

    @Test
    public void testNullInput() throws Exception {
        final DateConverter converter = new DateConverter(config("yyyy-MM-dd'T'HH:mm:ss.SSSZ", null, null));
        assertThat(((DateTime) (converter.convert(null)))).isNull();
    }

    @Test
    public void testEmptyInput() throws Exception {
        final DateConverter converter = new DateConverter(config("yyyy-MM-dd'T'HH:mm:ss.SSSZ", null, null));
        assertThat(((DateTime) (converter.convert("")))).isNull();
    }

    @Test
    public void testConvertWithoutYear() throws Exception {
        final DateConverter converter = new DateConverter(config("dd-MM HH:mm:ss", "Etc/UTC", null));
        final DateTime date = ((DateTime) (converter.convert("19-05 10:20:30")));
        assertThat(date).isNotNull().isEqualTo("2017-05-19T10:20:30.000Z");
    }

    @Test(expected = ConfigurationException.class)
    public void testWithEmptyDateFormat() throws Exception {
        final DateConverter converter = new DateConverter(config("", null, null));
        assertThat(((DateTime) (converter.convert("foo")))).isNull();
    }

    @Test(expected = ConfigurationException.class)
    public void testWithNullDateFormat() throws Exception {
        final DateConverter dateConverter = new DateConverter(config(null, null, null));
        assertThat(((DateTime) (dateConverter.convert("foo")))).isNull();
    }

    @Test
    public void convertObeysTimeZone() throws Exception {
        final DateTimeZone timeZone = DateTimeZone.forOffsetHours(12);
        final Converter c = new DateConverter(config("YYYY-MM-dd HH:mm:ss", timeZone.toString(), null));
        final DateTime dateOnly = ((DateTime) (c.convert("2014-03-12 10:00:00")));
        assertThat(dateOnly).isEqualTo("2014-03-12T10:00:00.000+12:00");
        final DateTime dateTime = ((DateTime) (c.convert("2014-03-12 12:34:00")));
        assertThat(dateTime).isEqualTo("2014-03-12T12:34:00.000+12:00");
    }

    @Test
    public void convertUsesEtcUTCIfTimeZoneSettingIsEmpty() throws Exception {
        final Converter c = new DateConverter(config("YYYY-MM-dd HH:mm:ss", "", null));
        final DateTime dateTime = ((DateTime) (c.convert("2014-03-12 10:00:00")));
        assertThat(dateTime).isEqualTo("2014-03-12T10:00:00.000Z");
    }

    @Test
    public void convertUsesEtcUTCIfTimeZoneSettingIsBlank() throws Exception {
        final Converter c = new DateConverter(config("YYYY-MM-dd HH:mm:ss", " ", null));
        final DateTime dateTime = ((DateTime) (c.convert("2014-03-12 10:00:00")));
        assertThat(dateTime).isEqualTo("2014-03-12T10:00:00.000Z");
    }

    @Test
    public void convertUsesEtcUTCIfTimeZoneSettingIsInvalid() throws Exception {
        final Converter c = new DateConverter(config("YYYY-MM-dd HH:mm:ss", "TEST", null));
        final DateTime dateTime = ((DateTime) (c.convert("2014-03-12 10:00:00")));
        assertThat(dateTime).isEqualTo("2014-03-12T10:00:00.000Z");
    }

    @Test
    public void convertIgnoresTZIfDatePatternContainsTZ() throws Exception {
        final Converter c = new DateConverter(config("YYYY-MM-dd'T'HH:mm:ss.SSSZ", "Etc/UTC", null));
        final DateTime actual = ((DateTime) (c.convert("2014-03-12T10:00:00.000+06:00")));
        assertThat(actual).isEqualTo("2014-03-12T10:00:00.000+06:00");
    }

    @Test
    public void convertUsesEnglishIfLocaleIsNull() throws Exception {
        final Converter c = new DateConverter(config("dd/MMM/YYYY HH:mm:ss Z", null, null));
        final DateTime dateTime = ((DateTime) (c.convert("11/May/2017 15:10:48 +0200")));
        assertThat(dateTime).isEqualTo("2017-05-11T13:10:48.000Z");
    }

    @Test
    public void convertUsesEnglishIfLocaleIsInvalid() throws Exception {
        final Converter c = new DateConverter(config("dd/MMM/YYYY HH:mm:ss Z", null, "Wurstweck"));
        final DateTime dateTime = ((DateTime) (c.convert("11/May/2017 15:10:48 +0200")));
        assertThat(dateTime).isEqualTo("2017-05-11T13:10:48.000Z");
    }

    @Test
    public void convertUsesCustomLocale() throws Exception {
        final Converter c = new DateConverter(config("dd/MMM/YYYY HH:mm:ss Z", null, "de-DE"));
        final DateTime dateTime = ((DateTime) (c.convert("11/M?rz/2017 15:10:48 +0200")));
        assertThat(dateTime).isEqualTo("2017-03-11T13:10:48.000Z");
    }

    /**
     * Test case for <a href="https://github.com/Graylog2/graylog2-server/issues/2648">#2648</a>.
     */
    @Test
    public void issue2648() throws Exception {
        final Converter utc = new DateConverter(config("YYYY-MM-dd HH:mm:ss", "UTC", null));
        final DateTime utcDate = ((DateTime) (utc.convert("2016-08-10 12:00:00")));
        assertThat(utcDate).isEqualTo("2016-08-10T12:00:00.000Z");
        final Converter cet = new DateConverter(config("YYYY-MM-dd HH:mm:ss", "CET", null));
        final DateTime cetDate = ((DateTime) (cet.convert("2016-08-10 12:00:00")));
        assertThat(cetDate).isEqualTo(new DateTime("2016-08-10T12:00:00.000", DateTimeZone.forID("CET")));
        final Converter berlin = new DateConverter(config("YYYY-MM-dd HH:mm:ss", "Europe/Berlin", null));
        final DateTime berlinDate = ((DateTime) (berlin.convert("2016-08-10 12:00:00")));
        assertThat(berlinDate).isEqualTo(new DateTime("2016-08-10T12:00:00.000", DateTimeZone.forID("Europe/Berlin")));
    }
}

