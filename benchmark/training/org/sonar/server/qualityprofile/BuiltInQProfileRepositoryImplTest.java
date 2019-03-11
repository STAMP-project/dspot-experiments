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
package org.sonar.server.qualityprofile;


import System2.INSTANCE;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.resources.Language;
import org.sonar.api.resources.Languages;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.db.DbClient;
import org.sonar.db.DbTester;
import org.sonar.server.language.LanguageTesting;


public class BuiltInQProfileRepositoryImplTest {
    private static final Language FOO_LANGUAGE = LanguageTesting.newLanguage("foo", "foo", "foo");

    private static final String SONAR_WAY_QP_NAME = "Sonar way";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    private DbClient dbClient = dbTester.getDbClient();

    @Test
    public void get_throws_ISE_if_called_before_initialize() {
        BuiltInQProfileRepositoryImpl underTest = new BuiltInQProfileRepositoryImpl(Mockito.mock(DbClient.class), new Languages());
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("initialize must be called first");
        underTest.get();
    }

    @Test
    public void initialize_throws_ISE_if_called_twice() {
        BuiltInQProfileRepositoryImpl underTest = new BuiltInQProfileRepositoryImpl(Mockito.mock(DbClient.class), new Languages());
        underTest.initialize();
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("initialize must be called only once");
        underTest.initialize();
    }

    @Test
    public void initialize_throws_ISE_if_language_has_no_builtin_qp() {
        BuiltInQProfileRepository underTest = new BuiltInQProfileRepositoryImpl(Mockito.mock(DbClient.class), new Languages(BuiltInQProfileRepositoryImplTest.FOO_LANGUAGE));
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("The following languages have no built-in quality profiles: foo");
        underTest.initialize();
    }

    @Test
    public void initialize_creates_no_BuiltInQProfile_when_all_definitions_apply_to_non_defined_languages() {
        BuiltInQProfileRepository underTest = new BuiltInQProfileRepositoryImpl(Mockito.mock(DbClient.class), new Languages(), new BuiltInQProfileRepositoryImplTest.DummyProfileDefinition("foo", "P1", false));
        underTest.initialize();
        assertThat(underTest.get()).isEmpty();
    }

    @Test
    public void initialize_makes_single_profile_of_a_language_default_even_if_not_flagged_as_so() {
        BuiltInQProfileRepository underTest = new BuiltInQProfileRepositoryImpl(dbClient, new Languages(BuiltInQProfileRepositoryImplTest.FOO_LANGUAGE), new BuiltInQProfileRepositoryImplTest.DummyProfileDefinition("foo", "foo1", false));
        underTest.initialize();
        assertThat(underTest.get()).extracting(BuiltInQProfile::getLanguage, BuiltInQProfile::isDefault).containsExactly(tuple(BuiltInQProfileRepositoryImplTest.FOO_LANGUAGE.getKey(), true));
    }

    @Test
    public void initialize_makes_single_profile_of_a_language_default_even_if_flagged_as_so() {
        BuiltInQProfileRepository underTest = new BuiltInQProfileRepositoryImpl(dbClient, new Languages(BuiltInQProfileRepositoryImplTest.FOO_LANGUAGE), new BuiltInQProfileRepositoryImplTest.DummyProfileDefinition("foo", "foo1", true));
        underTest.initialize();
        assertThat(underTest.get()).extracting(BuiltInQProfile::getLanguage, BuiltInQProfile::isDefault).containsExactly(tuple(BuiltInQProfileRepositoryImplTest.FOO_LANGUAGE.getKey(), true));
    }

    @Test
    public void initialize_makes_first_profile_of_a_language_default_when_none_flagged_as_so() {
        List<BuiltInQProfileRepositoryImplTest.DummyProfileDefinition> definitions = new ArrayList<>(Arrays.asList(new BuiltInQProfileRepositoryImplTest.DummyProfileDefinition("foo", "foo1", false), new BuiltInQProfileRepositoryImplTest.DummyProfileDefinition("foo", "foo2", false)));
        Collections.shuffle(definitions);
        String firstName = definitions.get(0).getName();
        String secondName = definitions.get(1).getName();
        BuiltInQProfileRepository underTest = new BuiltInQProfileRepositoryImpl(dbClient, new Languages(BuiltInQProfileRepositoryImplTest.FOO_LANGUAGE), definitions.toArray(new BuiltInQualityProfilesDefinition[0]));
        underTest.initialize();
        assertThat(underTest.get()).extracting(BuiltInQProfile::getName, BuiltInQProfile::isDefault).containsExactlyInAnyOrder(tuple(firstName, true), tuple(secondName, false));
    }

    @Test
    public void initialize_fails_with_ISE_when_two_profiles_with_different_name_are_default_for_the_same_language() {
        BuiltInQProfileRepository underTest = new BuiltInQProfileRepositoryImpl(dbClient, new Languages(BuiltInQProfileRepositoryImplTest.FOO_LANGUAGE), new BuiltInQProfileRepositoryImplTest.DummyProfileDefinition("foo", "foo1", true), new BuiltInQProfileRepositoryImplTest.DummyProfileDefinition("foo", "foo2", true));
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Several Quality profiles are flagged as default for the language foo: [foo1, foo2]");
        underTest.initialize();
    }

    @Test
    public void initialize_creates_profile_Sonar_Way_as_default_if_none_other_is_defined_default_for_a_given_language() {
        BuiltInQProfileRepository underTest = new BuiltInQProfileRepositoryImpl(dbClient, new Languages(BuiltInQProfileRepositoryImplTest.FOO_LANGUAGE), new BuiltInQProfileRepositoryImplTest.DummyProfileDefinition("foo", "doh", false), new BuiltInQProfileRepositoryImplTest.DummyProfileDefinition("foo", "boo", false), new BuiltInQProfileRepositoryImplTest.DummyProfileDefinition("foo", BuiltInQProfileRepositoryImplTest.SONAR_WAY_QP_NAME, false), new BuiltInQProfileRepositoryImplTest.DummyProfileDefinition("foo", "goo", false));
        underTest.initialize();
        assertThat(underTest.get()).filteredOn(( b) -> FOO_LANGUAGE.getKey().equals(b.getLanguage())).filteredOn(BuiltInQProfile::isDefault).extracting(BuiltInQProfile::getName).containsExactly(BuiltInQProfileRepositoryImplTest.SONAR_WAY_QP_NAME);
    }

    @Test
    public void initialize_does_not_create_Sonar_Way_as_default_if_other_profile_is_defined_as_default() {
        BuiltInQProfileRepository underTest = new BuiltInQProfileRepositoryImpl(dbClient, new Languages(BuiltInQProfileRepositoryImplTest.FOO_LANGUAGE), new BuiltInQProfileRepositoryImplTest.DummyProfileDefinition("foo", BuiltInQProfileRepositoryImplTest.SONAR_WAY_QP_NAME, false), new BuiltInQProfileRepositoryImplTest.DummyProfileDefinition("foo", "goo", true));
        underTest.initialize();
        assertThat(underTest.get()).filteredOn(( b) -> FOO_LANGUAGE.getKey().equals(b.getLanguage())).filteredOn(BuiltInQProfile::isDefault).extracting(BuiltInQProfile::getName).containsExactly("goo");
    }

    @Test
    public void initialize_matches_Sonar_Way_default_with_case_sensitivity() {
        String sonarWayInOtherCase = BuiltInQProfileRepositoryImplTest.SONAR_WAY_QP_NAME.toUpperCase();
        BuiltInQProfileRepository underTest = new BuiltInQProfileRepositoryImpl(dbClient, new Languages(BuiltInQProfileRepositoryImplTest.FOO_LANGUAGE), new BuiltInQProfileRepositoryImplTest.DummyProfileDefinition("foo", "goo", false), new BuiltInQProfileRepositoryImplTest.DummyProfileDefinition("foo", sonarWayInOtherCase, false));
        underTest.initialize();
        assertThat(underTest.get()).filteredOn(( b) -> FOO_LANGUAGE.getKey().equals(b.getLanguage())).filteredOn(BuiltInQProfile::isDefault).extracting(BuiltInQProfile::getName).containsExactly("goo");
    }

    private static final class DummyProfileDefinition implements BuiltInQualityProfilesDefinition {
        private final String language;

        private final String name;

        private final boolean defaultProfile;

        private DummyProfileDefinition(String language, String name, boolean defaultProfile) {
            this.language = language;
            this.name = name;
            this.defaultProfile = defaultProfile;
        }

        @Override
        public void define(Context context) {
            context.createBuiltInQualityProfile(name, language).setDefault(defaultProfile).done();
        }

        String getName() {
            return name;
        }
    }
}

