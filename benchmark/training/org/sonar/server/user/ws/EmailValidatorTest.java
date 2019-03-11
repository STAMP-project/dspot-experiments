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
package org.sonar.server.user.ws;


import org.junit.Test;


public class EmailValidatorTest {
    @Test
    public void valid_if_absent_or_empty() {
        assertThat(EmailValidator.isValidIfPresent(null)).isTrue();
        assertThat(EmailValidator.isValidIfPresent("")).isTrue();
    }

    @Test
    public void various_examples_of_unusual_but_valid_emails() {
        assertThat(EmailValidator.isValidIfPresent("info@sonarsource.com")).isTrue();
        assertThat(EmailValidator.isValidIfPresent("guillaume.jambet+sonarsource-emailvalidatortest@gmail.com")).isTrue();
        assertThat(EmailValidator.isValidIfPresent("webmaster@kin?-beaut?.fr")).isTrue();
        assertThat(EmailValidator.isValidIfPresent("\"Fred Bloggs\"@example.com")).isTrue();
        assertThat(EmailValidator.isValidIfPresent("Chuck Norris <coup-de-pied-retourn?@chucknorris.com>")).isTrue();
        assertThat(EmailValidator.isValidIfPresent("pipo@127.0.0.1")).isTrue();
        assertThat(EmailValidator.isValidIfPresent("admin@admin")).isTrue();
    }

    @Test
    public void various_examples_of_invalid_emails() {
        assertThat(EmailValidator.isValidIfPresent("infosonarsource.com")).isFalse();
        assertThat(EmailValidator.isValidIfPresent("info@.sonarsource.com")).isFalse();
        assertThat(EmailValidator.isValidIfPresent("info\"@.sonarsource.com")).isFalse();
    }
}

