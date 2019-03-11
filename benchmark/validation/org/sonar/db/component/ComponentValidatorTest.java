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
package org.sonar.db.component;


import com.google.common.base.Strings;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class ComponentValidatorTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void check_name() {
        String name = Strings.repeat("a", 500);
        assertThat(ComponentValidator.checkComponentName(name)).isEqualTo(name);
    }

    @Test
    public void fail_when_name_longer_than_500_characters() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Component name length");
        ComponentValidator.checkComponentName(Strings.repeat("a", (500 + 1)));
    }

    @Test
    public void check_key() {
        String key = Strings.repeat("a", 400);
        assertThat(ComponentValidator.checkComponentKey(key)).isEqualTo(key);
    }

    @Test
    public void fail_when_key_longer_than_400_characters() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Component key length");
        String key = Strings.repeat("a", (400 + 1));
        ComponentValidator.checkComponentKey(key);
    }

    @Test
    public void check_qualifier() {
        String qualifier = Strings.repeat("a", 10);
        assertThat(ComponentValidator.checkComponentQualifier(qualifier)).isEqualTo(qualifier);
    }

    @Test
    public void fail_when_qualifier_is_longer_than_10_characters() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Component qualifier length");
        ComponentValidator.checkComponentQualifier(Strings.repeat("a", (10 + 1)));
    }

    @Test
    public void private_constructor() {
        assertThat(hasOnlyPrivateConstructors(ComponentValidator.class)).isTrue();
    }
}

