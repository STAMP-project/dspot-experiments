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
package org.graylog.plugins.cef.codec;


import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.StringJoiner;
import org.assertj.core.api.AbstractObjectAssert;
import org.graylog2.plugin.Message;
import org.graylog2.plugin.journal.RawMessage;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class CEFCodecFixturesTest {
    public static class Fixture {
        public String testString;

        public String description;

        // Configuration of the codec
        public Map<String, Object> codecConfiguration = Collections.emptyMap();

        // Remote address of the raw message
        public String remoteAddress;

        // Expected message fields
        public Date expectedTimestamp;

        public String expectedSource;

        // Expected CEF fields
        public Date timestamp;

        public String host;

        public int cefVersion;

        public String deviceVendor;

        public String deviceProduct;

        public String deviceVersion;

        public String deviceEventClassId;

        public String name;

        public String severity;

        public Map<String, Object> extensions = Collections.emptyMap();

        @Override
        public String toString() {
            return new StringJoiner(", ", ((this.getClass().getSimpleName()) + "["), "]").add(("description = " + (description))).add(("CEF = " + (testString))).toString();
        }
    }

    private final CEFCodecFixturesTest.Fixture fixture;

    private final RawMessage rawMessage;

    private Message message;

    @SuppressWarnings("unused")
    public CEFCodecFixturesTest(CEFCodecFixturesTest.Fixture fixture, String fileName, String description) {
        this.fixture = fixture;
        final byte[] bytes = fixture.testString.getBytes(StandardCharsets.UTF_8);
        final InetSocketAddress remoteAddress = ((fixture.remoteAddress) == null) ? null : new InetSocketAddress(fixture.remoteAddress, 0);
        this.rawMessage = new RawMessage(bytes, remoteAddress);
    }

    @Test
    public void timestamp() throws Exception {
        if ((fixture.expectedTimestamp) != null) {
            assertThat(message.getTimestamp().toDate()).isEqualTo(fixture.expectedTimestamp);
        }
    }

    @Test
    public void source() throws Exception {
        if ((fixture.expectedSource) != null) {
            assertThat(message.getSource()).isEqualTo(fixture.expectedSource);
        }
    }

    @Test
    public void deviceVendor() throws Exception {
        containsEntry("device_vendor", fixture.deviceVendor);
    }

    @Test
    public void deviceProduct() throws Exception {
        containsEntry("device_product", fixture.deviceProduct);
    }

    @Test
    public void deviceVersion() throws Exception {
        containsEntry("device_version", fixture.deviceVersion);
    }

    @Test
    public void deviceEventClassId() throws Exception {
        containsEntry("event_class_id", fixture.deviceEventClassId);
    }

    @Test
    public void name() throws Exception {
        containsEntry("name", fixture.name);
    }

    @Test
    public void severity() throws Exception {
        containsEntry("severity", fixture.severity);
    }

    @Test
    public void extensions() throws Exception {
        final Map<String, Object> extensions = fixture.extensions;
        if (!(extensions.isEmpty())) {
            for (Map.Entry<String, Object> extension : extensions.entrySet()) {
                assertThat(message.getFields()).containsKey(extension.getKey());
                // Because Java type system...
                final Object fieldContent = message.getField(extension.getKey());
                final AbstractObjectAssert<?, Object> assertFieldContent = assertThat(fieldContent).describedAs(extension.getKey());
                if (fieldContent instanceof Integer) {
                    assertFieldContent.isEqualTo(((Number) (extensions.get(extension.getKey()))).intValue());
                } else
                    if (fieldContent instanceof Long) {
                        assertFieldContent.isEqualTo(((Number) (extensions.get(extension.getKey()))).longValue());
                    } else
                        if (fieldContent instanceof Float) {
                            assertFieldContent.isEqualTo(((Number) (extensions.get(extension.getKey()))).floatValue());
                        } else
                            if (fieldContent instanceof Double) {
                                assertFieldContent.isEqualTo(((Number) (extensions.get(extension.getKey()))).doubleValue());
                            } else
                                if (fieldContent instanceof DateTime) {
                                    assertFieldContent.isEqualTo(DateTime.parse(((String) (extensions.get(extension.getKey())))));
                                } else {
                                    assertFieldContent.isEqualTo(extensions.get(extension.getKey()));
                                }




            }
        }
    }
}

