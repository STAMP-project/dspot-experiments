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


import ComponentQuery.Builder;
import java.util.Collections;
import java.util.Date;
import java.util.function.Supplier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class ComponentQueryTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void build_query() {
        ComponentQuery underTest = ComponentQuery.builder().setNameOrKeyQuery("key").setAnyBranchAnalyzedBefore(100L).setAnyBranchAnalyzedAfter(200L).setCreatedAfter(new Date(300L)).setQualifiers(PROJECT).build();
        assertThat(underTest.getNameOrKeyQuery()).isEqualTo("key");
        assertThat(underTest.getQualifiers()).containsOnly(PROJECT);
        assertThat(underTest.getAnyBranchAnalyzedBefore()).isEqualTo(100L);
        assertThat(underTest.getAnyBranchAnalyzedAfter()).isEqualTo(200L);
        assertThat(underTest.getCreatedAfter().getTime()).isEqualTo(300L);
        assertThat(underTest.isOnProvisionedOnly()).isFalse();
        assertThat(underTest.isPartialMatchOnKey()).isFalse();
        assertThat(underTest.hasEmptySetOfComponents()).isFalse();
    }

    @Test
    public void build_query_minimal_properties() {
        ComponentQuery underTest = ComponentQuery.builder().setQualifiers(PROJECT).build();
        assertThat(underTest.getNameOrKeyQuery()).isNull();
        assertThat(underTest.getQualifiers()).containsOnly(PROJECT);
    }

    @Test
    public void test_getNameOrKeyUpperLikeQuery() {
        ComponentQuery underTest = ComponentQuery.builder().setNameOrKeyQuery("NAME/key").setQualifiers(PROJECT).build();
        assertThat(underTest.getNameOrKeyUpperLikeQuery()).isEqualTo("%NAME//KEY%");
    }

    @Test
    public void empty_list_of_components() {
        Supplier<ComponentQuery.Builder> query = () -> ComponentQuery.builder().setQualifiers(PROJECT);
        assertThat(query.get().setComponentIds(Collections.emptySet()).build().hasEmptySetOfComponents()).isTrue();
        assertThat(query.get().setComponentKeys(Collections.emptySet()).build().hasEmptySetOfComponents()).isTrue();
        assertThat(query.get().setComponentUuids(Collections.emptySet()).build().hasEmptySetOfComponents()).isTrue();
        assertThat(query.get().setComponentIds(Collections.singleton(404L)).build().hasEmptySetOfComponents()).isFalse();
        assertThat(query.get().setComponentKeys(Collections.singleton("P1")).build().hasEmptySetOfComponents()).isFalse();
        assertThat(query.get().setComponentUuids(Collections.singleton("U1")).build().hasEmptySetOfComponents()).isFalse();
    }

    @Test
    public void fail_if_no_qualifier_provided() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("At least one qualifier must be provided");
        ComponentQuery.builder().build();
    }

    @Test
    public void fail_if_partial_match_on_key_without_a_query() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("A query must be provided if a partial match on key is specified.");
        ComponentQuery.builder().setQualifiers(PROJECT).setPartialMatchOnKey(false).build();
    }
}

