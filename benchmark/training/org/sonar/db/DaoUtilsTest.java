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
package org.sonar.db;


import org.junit.Test;


public class DaoUtilsTest {
    @Test
    public void buildLikeValue_with_special_characters() {
        String escapedValue = "like-\\/_/%//-value";
        String wildcard = "%";
        assertThat(DaoUtils.buildLikeValue("like-\\_%/-value", WildcardPosition.BEFORE)).isEqualTo((wildcard + escapedValue));
        assertThat(DaoUtils.buildLikeValue("like-\\_%/-value", WildcardPosition.AFTER)).isEqualTo((escapedValue + wildcard));
        assertThat(DaoUtils.buildLikeValue("like-\\_%/-value", WildcardPosition.BEFORE_AND_AFTER)).isEqualTo(((wildcard + escapedValue) + wildcard));
    }
}

