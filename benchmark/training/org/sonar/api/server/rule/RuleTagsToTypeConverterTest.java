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
package org.sonar.api.server.rule;


import RuleType.BUG;
import RuleType.CODE_SMELL;
import RuleType.VULNERABILITY;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import org.sonar.test.TestUtils;


public class RuleTagsToTypeConverterTest {
    @Test
    public void type_is_bug_if_has_tag_bug() {
        assertThat(RuleTagsToTypeConverter.convert(Arrays.asList("misra", "bug"))).isEqualTo(BUG);
        // "bug" has priority on "security"
        assertThat(RuleTagsToTypeConverter.convert(Arrays.asList("security", "bug"))).isEqualTo(BUG);
    }

    @Test
    public void type_is_vulnerability_if_has_tag_security() {
        assertThat(RuleTagsToTypeConverter.convert(Arrays.asList("misra", "security"))).isEqualTo(VULNERABILITY);
    }

    @Test
    public void default_is_code_smell() {
        assertThat(RuleTagsToTypeConverter.convert(Arrays.asList("clumsy", "spring"))).isEqualTo(CODE_SMELL);
        assertThat(RuleTagsToTypeConverter.convert(Collections.<String>emptyList())).isEqualTo(CODE_SMELL);
    }

    @Test
    public void only_statics() {
        assertThat(TestUtils.hasOnlyPrivateConstructors(RuleTagsToTypeConverter.class)).isTrue();
    }
}

