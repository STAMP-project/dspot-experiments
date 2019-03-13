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
package org.sonar.api.utils.text;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class CsvWriterTest {
    @Test
    public void write_csv() throws Exception {
        StringWriter output = new StringWriter();
        CsvWriter writer = CsvWriter.of(output);
        writer.values("France", "Paris");
        writer.values("Sweden", "Stockholm");
        writer.close();
        BufferedReader reader = new BufferedReader(new StringReader(output.toString()));
        String line1 = reader.readLine();
        assertThat(line1).isEqualTo("France,Paris");
        String line2 = reader.readLine();
        assertThat(line2).isEqualTo("Sweden,Stockholm");
        assertThat(reader.readLine()).isNull();
    }

    @Test
    public void escape_value() throws Exception {
        StringWriter output = new StringWriter();
        CsvWriter writer = CsvWriter.of(output);
        writer.values("no double-quotes", "contains \"double-quotes\"", "contains , commas");
        writer.close();
        BufferedReader reader = new BufferedReader(new StringReader(output.toString()));
        assertThat(reader.readLine()).isEqualTo("no double-quotes,\"contains \"\"double-quotes\"\"\",\"contains , commas\"");
        assertThat(reader.readLine()).isNull();
    }

    @Test
    public void fail_to_write_to_stream() throws Exception {
        Writer output = Mockito.mock(Writer.class);
        IOException cause = new IOException("bad");
        Mockito.doThrow(cause).when(output).append(ArgumentMatchers.anyString());
        CsvWriter writer = CsvWriter.of(output);
        try {
            writer.values("foo");
            Assert.fail();
        } catch (WriterException e) {
            assertThat(e).hasMessage("Fail to generate CSV with value: foo");
            assertThat(e.getCause()).isSameAs(cause);
        }
    }

    @Test
    public void fail_to_close_stream() throws Exception {
        Writer output = Mockito.mock(Writer.class);
        IOException cause = new IOException("bad");
        Mockito.doThrow(cause).when(output).close();
        CsvWriter writer = CsvWriter.of(output);
        writer.values("foo");
        try {
            writer.close();
            Assert.fail();
        } catch (WriterException e) {
            assertThat(e).hasMessage("Fail to close CSV output");
            assertThat(e.getCause()).isSameAs(cause);
        }
    }
}

