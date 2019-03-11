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
package org.graylog2.jackson;


import Semver.SemverType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vdurmont.semver4j.Semver;
import java.io.IOException;
import org.junit.Test;


public class SemverDeserializerTest {
    private ObjectMapper objectMapper;

    @Test
    public void successfullyDeserializesString() throws IOException {
        final Semver version = objectMapper.readValue("\"1.3.7-rc.2+build.2.b8f12d7\"", Semver.class);
        assertThat(version).isEqualTo(new Semver("1.3.7-rc.2+build.2.b8f12d7", SemverType.NPM));
    }

    @Test
    public void successfullyDeserializesInteger() throws IOException {
        final Semver version = objectMapper.readValue("5", Semver.class);
        assertThat(version).isEqualTo(new Semver("5", SemverType.LOOSE));
    }

    @Test
    public void successfullyDeserializesNull() throws IOException {
        final Semver version = objectMapper.readValue("null", Semver.class);
        assertThat(version).isNull();
    }

    @Test
    public void failsForInvalidVersion() {
        assertThatThrownBy(() -> objectMapper.readValue("\"foobar\"", .class)).isInstanceOf(JsonMappingException.class).hasMessageStartingWith("Invalid version (no major version): foobar");
    }

    @Test
    public void failsForInvalidType() {
        assertThatThrownBy(() -> objectMapper.readValue("[]", .class)).isInstanceOf(JsonMappingException.class).hasMessageStartingWith("Unexpected token (START_ARRAY), expected VALUE_STRING: expected String or Number");
    }
}

