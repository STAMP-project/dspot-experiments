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
package org.sonar.server.component.index;


import ComponentTextSearchFeatureRepertoire.FAVORITE;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import org.junit.Test;
import org.sonar.db.component.ComponentDto;


public class ComponentIndexFeatureFavoriteTest extends ComponentIndexTest {
    @Test
    public void scoring_cares_about_favorites() {
        ComponentDto project1 = indexProject("sonarqube", "SonarQube");
        ComponentDto project2 = indexProject("recent", "SonarQube Recently");
        SuggestionQuery query1 = SuggestionQuery.builder().setQuery("SonarQube").setQualifiers(Collections.singletonList(PROJECT)).setFavoriteKeys(ImmutableSet.of(project1.getDbKey())).build();
        assertSearch(query1).containsExactly(ComponentIndexTest.uuids(project1, project2));
        SuggestionQuery query2 = SuggestionQuery.builder().setQuery("SonarQube").setQualifiers(Collections.singletonList(PROJECT)).setFavoriteKeys(ImmutableSet.of(project2.getDbKey())).build();
        assertSearch(query2).containsExactly(ComponentIndexTest.uuids(project2, project1));
    }

    @Test
    public void irrelevant_favorites_are_not_returned() {
        features.set(( q) -> termQuery(FIELD_KEY, "non-existing-value"), FAVORITE);
        ComponentDto project1 = indexProject("foo", "foo");
        SuggestionQuery query1 = SuggestionQuery.builder().setQuery("bar").setQualifiers(Collections.singletonList(PROJECT)).setFavoriteKeys(ImmutableSet.of(project1.getDbKey())).build();
        assertSearch(query1).isEmpty();
    }
}

