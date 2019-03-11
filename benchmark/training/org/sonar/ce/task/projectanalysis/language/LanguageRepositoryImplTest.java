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
package org.sonar.ce.task.projectanalysis.language;


import java.util.Optional;
import org.junit.Test;
import org.sonar.api.resources.Language;


public class LanguageRepositoryImplTest {
    private static final String ANY_KEY = "Any_Key";

    private static final String SOME_LANGUAGE_KEY = "SoMe language_Key";

    private static final Language SOME_LANGUAGE = LanguageRepositoryImplTest.createLanguage(LanguageRepositoryImplTest.SOME_LANGUAGE_KEY, "_name");

    @Test(expected = IllegalArgumentException.class)
    public void constructor_fails_is_language_have_the_same_key() {
        new LanguageRepositoryImpl(LanguageRepositoryImplTest.createLanguage(LanguageRepositoryImplTest.SOME_LANGUAGE_KEY, " 1"), LanguageRepositoryImplTest.createLanguage(LanguageRepositoryImplTest.SOME_LANGUAGE_KEY, " 2"));
    }

    @Test
    public void find_on_empty_LanguageRepository_returns_absent() {
        assertThat(new LanguageRepositoryImpl().find(LanguageRepositoryImplTest.ANY_KEY).isPresent()).isFalse();
    }

    @Test
    public void find_by_key_returns_the_same_object() {
        LanguageRepositoryImpl languageRepository = new LanguageRepositoryImpl(LanguageRepositoryImplTest.SOME_LANGUAGE);
        Optional<Language> language = languageRepository.find(LanguageRepositoryImplTest.SOME_LANGUAGE_KEY);
        assertThat(language.isPresent()).isTrue();
        assertThat(language.get()).isSameAs(LanguageRepositoryImplTest.SOME_LANGUAGE);
    }

    @Test
    public void find_by_other_key_returns_absent() {
        LanguageRepositoryImpl languageRepository = new LanguageRepositoryImpl(LanguageRepositoryImplTest.SOME_LANGUAGE);
        Optional<Language> language = languageRepository.find(LanguageRepositoryImplTest.ANY_KEY);
        assertThat(language.isPresent()).isFalse();
    }
}

