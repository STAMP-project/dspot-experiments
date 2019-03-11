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


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class VersionTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void test_parse() {
        VersionTest.assertVersion(Version.parse(""), 0, 0, 0, 0, "");
        VersionTest.assertVersion(Version.parse("1"), 1, 0, 0, 0, "");
        VersionTest.assertVersion(Version.parse("1.2"), 1, 2, 0, 0, "");
        VersionTest.assertVersion(Version.parse("1.2.3"), 1, 2, 3, 0, "");
        VersionTest.assertVersion(Version.parse("1.2-beta-1"), 1, 2, 0, 0, "beta-1");
        VersionTest.assertVersion(Version.parse("1.2.3-beta1"), 1, 2, 3, 0, "beta1");
        VersionTest.assertVersion(Version.parse("1.2.3-beta-1"), 1, 2, 3, 0, "beta-1");
        VersionTest.assertVersion(Version.parse("1.2.3.4567"), 1, 2, 3, 4567, "");
        VersionTest.assertVersion(Version.parse("1.2.3.4567-alpha"), 1, 2, 3, 4567, "alpha");
    }

    @Test
    public void parse_throws_IAE_if_more_than_4_fields() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Maximum 4 fields are accepted: 1.2.3.456.7");
        Version.parse("1.2.3.456.7");
    }

    @Test
    public void test_equals() {
        Version one = Version.parse("1");
        assertThat(one).isEqualTo(one);
        assertThat(one).isEqualTo(Version.parse("1"));
        assertThat(one).isEqualTo(Version.parse("1.0"));
        assertThat(one).isEqualTo(Version.parse("1.0.0"));
        assertThat(one).isNotEqualTo(Version.parse("1.2.3"));
        assertThat(one).isNotEqualTo("1");
        assertThat(Version.parse("1.2.3")).isEqualTo(Version.parse("1.2.3"));
        assertThat(Version.parse("1.2.3")).isNotEqualTo(Version.parse("1.2.4"));
        assertThat(Version.parse("1.2.3")).isEqualTo(Version.parse("1.2.3-b1"));
        assertThat(Version.parse("1.2.3-b1")).isEqualTo(Version.parse("1.2.3-b2"));
    }

    @Test
    public void test_hashCode() {
        assertThat(Version.parse("1").hashCode()).isEqualTo(Version.parse("1").hashCode());
        assertThat(Version.parse("1").hashCode()).isEqualTo(Version.parse("1.0.0").hashCode());
        assertThat(Version.parse("1.2.3-beta1").hashCode()).isEqualTo(Version.parse("1.2.3").hashCode());
    }

    @Test
    public void test_compareTo() {
        assertThat(Version.parse("1.2").compareTo(Version.parse("1.2.0"))).isEqualTo(0);
        assertThat(Version.parse("1.2.3").compareTo(Version.parse("1.2.3"))).isEqualTo(0);
        assertThat(Version.parse("1.2.3").compareTo(Version.parse("1.2.4"))).isLessThan(0);
        assertThat(Version.parse("1.2.3").compareTo(Version.parse("1.3"))).isLessThan(0);
        assertThat(Version.parse("1.2.3").compareTo(Version.parse("2.1"))).isLessThan(0);
        assertThat(Version.parse("1.2.3").compareTo(Version.parse("2.0.0"))).isLessThan(0);
        assertThat(Version.parse("2.0").compareTo(Version.parse("1.2"))).isGreaterThan(0);
    }

    @Test
    public void compareTo_handles_build_number() {
        assertThat(Version.parse("1.2").compareTo(Version.parse("1.2.0.0"))).isEqualTo(0);
        assertThat(Version.parse("1.2.3.1234").compareTo(Version.parse("1.2.3.4567"))).isLessThan(0);
        assertThat(Version.parse("1.2.3.1234").compareTo(Version.parse("1.2.3"))).isGreaterThan(0);
        assertThat(Version.parse("1.2.3.1234").compareTo(Version.parse("1.2.4"))).isLessThan(0);
        assertThat(Version.parse("1.2.3.9999").compareTo(Version.parse("1.2.4.1111"))).isLessThan(0);
    }

    @Test
    public void qualifier_is_ignored_from_comparison() {
        assertThat(Version.parse("1.2.3")).isEqualTo(Version.parse("1.2.3-build1"));
        assertThat(Version.parse("1.2.3")).isEqualTo(Version.parse("1.2.3-build1"));
        assertThat(Version.parse("1.2.3").compareTo(Version.parse("1.2.3-build1"))).isEqualTo(0);
    }

    @Test
    public void test_toString() {
        assertThat(Version.parse("1").toString()).isEqualTo("1.0");
        assertThat(Version.parse("1.2").toString()).isEqualTo("1.2");
        assertThat(Version.parse("1.2.3").toString()).isEqualTo("1.2.3");
        assertThat(Version.parse("1.2-b1").toString()).isEqualTo("1.2-b1");
        assertThat(Version.parse("1.2.3-b1").toString()).isEqualTo("1.2.3-b1");
        assertThat(Version.parse("1.2.3.4567").toString()).isEqualTo("1.2.3.4567");
        assertThat(Version.parse("1.2.3.4567-beta1").toString()).isEqualTo("1.2.3.4567-beta1");
        // do not display zero numbers when possible
        assertThat(Version.parse("1.2.0.0").toString()).isEqualTo("1.2");
        assertThat(Version.parse("1.2.0.1").toString()).isEqualTo("1.2.0.1");
        assertThat(Version.parse("1.2.1.0").toString()).isEqualTo("1.2.1");
        assertThat(Version.parse("1.2.1.0-beta").toString()).isEqualTo("1.2.1-beta");
    }

    @Test
    public void test_create() {
        VersionTest.assertVersion(Version.create(1, 2), 1, 2, 0, 0, "");
        VersionTest.assertVersion(Version.create(1, 2, 3), 1, 2, 3, 0, "");
        VersionTest.assertVersion(Version.create(1, 2, 0, ""), 1, 2, 0, 0, "");
        VersionTest.assertVersion(Version.create(1, 2, 3, "build1"), 1, 2, 3, 0, "build1");
        assertThat(Version.create(1, 2, 3, "build1").toString()).isEqualTo("1.2.3-build1");
    }
}

