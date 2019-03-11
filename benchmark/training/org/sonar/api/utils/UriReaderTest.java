/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.api.utils;


import UriReader.SchemeProcessor;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class UriReaderTest {
    private static URI testFile;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void file_processor_is_always_available() {
        UriReader uriReader = new UriReader(new UriReader.SchemeProcessor[0]);
        assertThat(uriReader.searchForSupportedProcessor(UriReaderTest.testFile)).isNotNull();
    }

    @Test
    public void file_readString() {
        UriReader uriReader = new UriReader(new UriReader.SchemeProcessor[0]);
        assertThat(uriReader.readString(UriReaderTest.testFile, StandardCharsets.UTF_8)).isEqualTo("in foo");
    }

    @Test
    public void file_readBytes() {
        UriReader uriReader = new UriReader(new UriReader.SchemeProcessor[0]);
        assertThat(new String(uriReader.readBytes(UriReaderTest.testFile))).isEqualTo("in foo");
    }

    @Test
    public void file_readString_fails_if_file_not_found() throws Exception {
        thrown.expect(RuntimeException.class);
        UriReader uriReader = new UriReader(new UriReader.SchemeProcessor[0]);
        uriReader.readString(new URI("file:/notfound"), StandardCharsets.UTF_8);
    }

    @Test
    public void file_readBytes_fails_if_file_not_found() throws Exception {
        thrown.expect(RuntimeException.class);
        UriReader uriReader = new UriReader(new UriReader.SchemeProcessor[0]);
        uriReader.readBytes(new URI("file:/notfound"));
    }

    @Test
    public void file_description() {
        UriReader uriReader = new UriReader(new UriReader.SchemeProcessor[0]);
        // the prefix file:/ is removed
        assertThat(uriReader.description(UriReaderTest.testFile)).doesNotMatch("file:/.*");
        assertThat(uriReader.description(UriReaderTest.testFile)).matches(".*foo\\.txt");
    }

    @Test
    public void fail_if_unknown_scheme() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        UriReader uriReader = new UriReader(new UriReader.SchemeProcessor[0]);
        uriReader.readBytes(new URI("ftp://sonarsource.org"));
    }

    @Test
    public void register_processors() throws Exception {
        UriReader.SchemeProcessor processor = Mockito.mock(SchemeProcessor.class);
        Mockito.when(processor.getSupportedSchemes()).thenReturn(new String[]{ "ftp" });
        UriReader uriReader = new UriReader(new UriReader.SchemeProcessor[]{ processor });
        assertThat(uriReader.searchForSupportedProcessor(new URI("ftp://sonarsource.org"))).isNotNull();
    }
}

