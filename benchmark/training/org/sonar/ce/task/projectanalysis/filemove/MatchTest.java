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
package org.sonar.ce.task.projectanalysis.filemove;


import org.junit.Test;


public class MatchTest {
    private static final String SOME_REPORT_KEY = "reportKey";

    private static final String SOME_KEY = "key";

    private Match underTest = new Match(MatchTest.SOME_KEY, MatchTest.SOME_REPORT_KEY);

    @Test
    public void constructor_key_argument_can_be_null() {
        new Match(null, MatchTest.SOME_REPORT_KEY);
    }

    @Test
    public void constructor_reportKey_argument_can_be_null() {
        new Match(MatchTest.SOME_KEY, null);
    }

    @Test
    public void getDbKey_returns_first_constructor_argument() {
        assertThat(underTest.getDbUuid()).isEqualTo(MatchTest.SOME_KEY);
    }

    @Test
    public void getDbKey_returns_second_constructor_argument() {
        assertThat(underTest.getReportUuid()).isEqualTo(MatchTest.SOME_REPORT_KEY);
    }

    @Test
    public void equals_is_based_on_both_properties() {
        assertThat(underTest).isEqualTo(new Match(MatchTest.SOME_KEY, MatchTest.SOME_REPORT_KEY));
        assertThat(underTest).isNotEqualTo(new Match("other key", MatchTest.SOME_REPORT_KEY));
        assertThat(underTest).isNotEqualTo(new Match(MatchTest.SOME_KEY, "other report key"));
        assertThat(underTest).isNotEqualTo(new Match(null, MatchTest.SOME_REPORT_KEY));
        assertThat(underTest).isNotEqualTo(new Match(MatchTest.SOME_KEY, null));
        assertThat(underTest).isNotEqualTo(new Match(null, null));
    }

    @Test
    public void hashcode_is_base_on_both_properties() {
        int hashCode = underTest.hashCode();
        assertThat(hashCode).isEqualTo(new Match(MatchTest.SOME_KEY, MatchTest.SOME_REPORT_KEY).hashCode());
        assertThat(hashCode).isNotEqualTo(new Match("other key", MatchTest.SOME_REPORT_KEY).hashCode());
        assertThat(hashCode).isNotEqualTo(new Match(MatchTest.SOME_KEY, "other report key").hashCode());
        assertThat(hashCode).isNotEqualTo(new Match(null, MatchTest.SOME_REPORT_KEY).hashCode());
        assertThat(hashCode).isNotEqualTo(new Match(MatchTest.SOME_KEY, null).hashCode());
        assertThat(hashCode).isNotEqualTo(new Match(null, null).hashCode());
    }

    @Test
    public void toString_prints_both_properties() {
        assertThat(underTest.toString()).isEqualTo("{key=>reportKey}");
    }
}

