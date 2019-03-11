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
package org.sonar.server.l18n;


import java.util.Locale;
import org.junit.Test;
import org.sonar.api.utils.internal.TestSystem2;


public class ServerI18nTest {
    private TestSystem2 system2 = new TestSystem2();

    private ServerI18n underTest;

    @Test
    public void get_english_labels() {
        assertThat(underTest.message(Locale.ENGLISH, "any", null)).isEqualTo("Any");
        assertThat(underTest.message(Locale.ENGLISH, "coreext.rule1.name", null)).isEqualTo("Rule one");
    }

    @Test
    public void get_english_labels_when_default_locale_is_not_english() {
        Locale defaultLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.FRENCH);
            assertThat(underTest.message(Locale.ENGLISH, "any", null)).isEqualTo("Any");
            assertThat(underTest.message(Locale.ENGLISH, "coreext.rule1.name", null)).isEqualTo("Rule one");
        } finally {
            Locale.setDefault(defaultLocale);
        }
    }

    @Test
    public void get_labels_from_french_pack() {
        assertThat(underTest.message(Locale.FRENCH, "coreext.rule1.name", null)).isEqualTo("Rule un");
        assertThat(underTest.message(Locale.FRENCH, "any", null)).isEqualTo("Tous");
    }
}

