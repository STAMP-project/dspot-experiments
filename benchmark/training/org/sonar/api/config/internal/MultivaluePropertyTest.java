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
package org.sonar.api.config.internal;


import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(DataProviderRunner.class)
public class MultivaluePropertyTest {
    private static final String[] EMPTY_STRING_ARRAY = new String[]{  };

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void parseAsCsv_fails_with_ISE_if_value_can_not_be_parsed() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Property: \'multi\' doesn\'t contain a valid CSV value: \'\"a ,b\'");
        MultivalueProperty.parseAsCsv("multi", "\"a ,b");
    }

    @Test
    public void trimFieldsAndRemoveEmptyFields_throws_NPE_if_arg_is_null() {
        expectedException.expect(NullPointerException.class);
        MultivalueProperty.trimFieldsAndRemoveEmptyFields(null);
    }

    @Test
    public void trimAccordingToStringTrim() {
        String str = randomAlphanumeric(4);
        for (int i = 0; i <= ' '; i++) {
            String prefixed = ((char) (i)) + str;
            String suffixed = ((char) (i)) + str;
            String both = (((char) (i)) + str) + ((char) (i));
            assertThat(MultivalueProperty.trimFieldsAndRemoveEmptyFields(prefixed)).isEqualTo(prefixed.trim());
            assertThat(MultivalueProperty.trimFieldsAndRemoveEmptyFields(suffixed)).isEqualTo(suffixed.trim());
            assertThat(MultivalueProperty.trimFieldsAndRemoveEmptyFields(both)).isEqualTo(both.trim());
        }
    }

    @Test
    public void trimFieldsAndRemoveEmptyFields_supports_escaped_quote_in_quotes() {
        assertThat(MultivalueProperty.trimFieldsAndRemoveEmptyFields("\"f\"\"oo\"")).isEqualTo("\"f\"\"oo\"");
        assertThat(MultivalueProperty.trimFieldsAndRemoveEmptyFields("\"f\"\"oo\",\"bar\"\"\"")).isEqualTo("\"f\"\"oo\",\"bar\"\"\"");
    }

    @Test
    public void trimFieldsAndRemoveEmptyFields_does_not_fail_on_unbalanced_quotes() {
        assertThat(MultivalueProperty.trimFieldsAndRemoveEmptyFields("\"")).isEqualTo("\"");
        assertThat(MultivalueProperty.trimFieldsAndRemoveEmptyFields("\"foo")).isEqualTo("\"foo");
        assertThat(MultivalueProperty.trimFieldsAndRemoveEmptyFields("foo\"")).isEqualTo("foo\"");
        assertThat(MultivalueProperty.trimFieldsAndRemoveEmptyFields("\"foo\",\"")).isEqualTo("\"foo\",\"");
        assertThat(MultivalueProperty.trimFieldsAndRemoveEmptyFields("\",\"foo\"")).isEqualTo("\",\"foo\"");
        assertThat(MultivalueProperty.trimFieldsAndRemoveEmptyFields("\"foo\",\",  ")).isEqualTo("\"foo\",\",  ");
        assertThat(MultivalueProperty.trimFieldsAndRemoveEmptyFields(" a ,,b , c,  \"foo\",\"  ")).isEqualTo("a,b,c,\"foo\",\"  ");
        assertThat(MultivalueProperty.trimFieldsAndRemoveEmptyFields("\" a ,,b , c,  ")).isEqualTo("\" a ,,b , c,  ");
    }

    private static final char[] SOME_PRINTABLE_TRIMMABLE_CHARS = new char[]{ ' ', '\t', '\n', '\r' };
}

