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


import java.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.db.organization.OrganizationTesting;


public class ComponentTreeQueryTest {
    private static final String BASE_UUID = "ABCD";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void create_query() {
        ComponentTreeQuery query = ComponentTreeQuery.builder().setBaseUuid(ComponentTreeQueryTest.BASE_UUID).setStrategy(Strategy.CHILDREN).setQualifiers(Arrays.asList("FIL", "DIR")).setNameOrKeyQuery("teSt").build();
        assertThat(query.getBaseUuid()).isEqualTo(ComponentTreeQueryTest.BASE_UUID);
        assertThat(query.getStrategy()).isEqualTo(Strategy.CHILDREN);
        assertThat(query.getQualifiers()).containsOnly("FIL", "DIR");
        assertThat(query.getNameOrKeyQuery()).isEqualTo("teSt");
        assertThat(query.getNameOrKeyUpperLikeQuery()).isEqualTo("%TEST%");
    }

    @Test
    public void create_minimal_query() {
        ComponentTreeQuery query = ComponentTreeQuery.builder().setBaseUuid(ComponentTreeQueryTest.BASE_UUID).setStrategy(Strategy.CHILDREN).build();
        assertThat(query.getBaseUuid()).isEqualTo(ComponentTreeQueryTest.BASE_UUID);
        assertThat(query.getStrategy()).isEqualTo(Strategy.CHILDREN);
        assertThat(query.getQualifiers()).isNull();
        assertThat(query.getNameOrKeyQuery()).isNull();
        assertThat(query.getNameOrKeyUpperLikeQuery()).isNull();
    }

    @Test
    public void test_getUuidPath() {
        assertThat(ComponentTreeQuery.builder().setBaseUuid(ComponentTreeQueryTest.BASE_UUID).setStrategy(Strategy.CHILDREN).build().getUuidPath(ComponentTesting.newPrivateProjectDto(OrganizationTesting.newOrganizationDto(), "PROJECT_UUID"))).isEqualTo(".PROJECT_UUID.");
        assertThat(ComponentTreeQuery.builder().setBaseUuid(ComponentTreeQueryTest.BASE_UUID).setStrategy(Strategy.LEAVES).build().getUuidPath(ComponentTesting.newPrivateProjectDto(OrganizationTesting.newOrganizationDto(), "PROJECT_UUID"))).isEqualTo(".PROJECT/_UUID.%");
    }

    @Test
    public void fail_when_no_base_uuid() {
        expectedException.expect(NullPointerException.class);
        ComponentTreeQuery.builder().setStrategy(Strategy.CHILDREN).build();
    }

    @Test
    public void fail_when_no_strategy() {
        expectedException.expect(NullPointerException.class);
        ComponentTreeQuery.builder().setBaseUuid(ComponentTreeQueryTest.BASE_UUID).build();
    }
}

