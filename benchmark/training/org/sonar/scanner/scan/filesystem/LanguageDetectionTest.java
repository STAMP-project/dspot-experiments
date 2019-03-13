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
package org.sonar.scanner.scan.filesystem;


import CoreProperties.PROJECT_LANGUAGE_PROPERTY;
import junit.framework.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.resources.Language;
import org.sonar.api.resources.Languages;
import org.sonar.api.utils.MessageException;
import org.sonar.scanner.repository.language.LanguagesRepository;


public class LanguageDetectionTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private MapSettings settings;

    @Test
    public void test_sanitizeExtension() throws Exception {
        assertThat(LanguageDetection.sanitizeExtension(".cbl")).isEqualTo("cbl");
        assertThat(LanguageDetection.sanitizeExtension(".CBL")).isEqualTo("cbl");
        assertThat(LanguageDetection.sanitizeExtension("CBL")).isEqualTo("cbl");
        assertThat(LanguageDetection.sanitizeExtension("cbl")).isEqualTo("cbl");
    }

    @Test
    public void search_by_file_extension() throws Exception {
        LanguagesRepository languages = new org.sonar.scanner.repository.language.DefaultLanguagesRepository(new Languages(new LanguageDetectionTest.MockLanguage("java", "java", "jav"), new LanguageDetectionTest.MockLanguage("cobol", "cbl", "cob")));
        LanguageDetection detection = new LanguageDetection(settings.asConfig(), languages);
        assertThat(detectLanguage(detection, "Foo.java")).isEqualTo("java");
        assertThat(detectLanguage(detection, "src/Foo.java")).isEqualTo("java");
        assertThat(detectLanguage(detection, "Foo.JAVA")).isEqualTo("java");
        assertThat(detectLanguage(detection, "Foo.jav")).isEqualTo("java");
        assertThat(detectLanguage(detection, "Foo.Jav")).isEqualTo("java");
        assertThat(detectLanguage(detection, "abc.cbl")).isEqualTo("cobol");
        assertThat(detectLanguage(detection, "abc.CBL")).isEqualTo("cobol");
        assertThat(detectLanguage(detection, "abc.php")).isNull();
        assertThat(detectLanguage(detection, "abc")).isNull();
    }

    @Test
    public void should_not_fail_if_no_language() throws Exception {
        LanguageDetection detection = Mockito.spy(new LanguageDetection(settings.asConfig(), new org.sonar.scanner.repository.language.DefaultLanguagesRepository(new Languages())));
        assertThat(detectLanguage(detection, "Foo.java")).isNull();
    }

    @Test
    public void plugin_can_declare_a_file_extension_twice_for_case_sensitivity() throws Exception {
        LanguagesRepository languages = new org.sonar.scanner.repository.language.DefaultLanguagesRepository(new Languages(new LanguageDetectionTest.MockLanguage("abap", "abap", "ABAP")));
        LanguageDetection detection = new LanguageDetection(settings.asConfig(), languages);
        assertThat(detectLanguage(detection, "abc.abap")).isEqualTo("abap");
    }

    @Test
    public void language_with_no_extension() throws Exception {
        // abap does not declare any file extensions.
        // When analyzing an ABAP project, then all source files must be parsed.
        LanguagesRepository languages = new org.sonar.scanner.repository.language.DefaultLanguagesRepository(new Languages(new LanguageDetectionTest.MockLanguage("java", "java"), new LanguageDetectionTest.MockLanguage("abap")));
        // No side-effect on non-ABAP projects
        LanguageDetection detection = new LanguageDetection(settings.asConfig(), languages);
        assertThat(detectLanguage(detection, "abc")).isNull();
        assertThat(detectLanguage(detection, "abc.abap")).isNull();
        assertThat(detectLanguage(detection, "abc.java")).isEqualTo("java");
        settings.setProperty(PROJECT_LANGUAGE_PROPERTY, "abap");
        detection = new LanguageDetection(settings.asConfig(), languages);
        assertThat(detectLanguage(detection, "abc")).isEqualTo("abap");
        assertThat(detectLanguage(detection, "abc.txt")).isEqualTo("abap");
        assertThat(detectLanguage(detection, "abc.java")).isEqualTo("abap");
    }

    @Test
    public void force_language_using_deprecated_property() throws Exception {
        LanguagesRepository languages = new org.sonar.scanner.repository.language.DefaultLanguagesRepository(new Languages(new LanguageDetectionTest.MockLanguage("java", "java"), new LanguageDetectionTest.MockLanguage("php", "php")));
        settings.setProperty(PROJECT_LANGUAGE_PROPERTY, "java");
        LanguageDetection detection = new LanguageDetection(settings.asConfig(), languages);
        assertThat(detectLanguage(detection, "abc")).isNull();
        assertThat(detectLanguage(detection, "abc.php")).isNull();
        assertThat(detectLanguage(detection, "abc.java")).isEqualTo("java");
        assertThat(detectLanguage(detection, "src/abc.java")).isEqualTo("java");
    }

    @Test
    public void fail_if_invalid_language() {
        thrown.expect(MessageException.class);
        thrown.expectMessage("You must install a plugin that supports the language 'unknown'");
        LanguagesRepository languages = new org.sonar.scanner.repository.language.DefaultLanguagesRepository(new Languages(new LanguageDetectionTest.MockLanguage("java", "java"), new LanguageDetectionTest.MockLanguage("php", "php")));
        settings.setProperty(PROJECT_LANGUAGE_PROPERTY, "unknown");
        new LanguageDetection(settings.asConfig(), languages);
    }

    @Test
    public void fail_if_conflicting_language_suffix() throws Exception {
        LanguagesRepository languages = new org.sonar.scanner.repository.language.DefaultLanguagesRepository(new Languages(new LanguageDetectionTest.MockLanguage("xml", "xhtml"), new LanguageDetectionTest.MockLanguage("web", "xhtml")));
        LanguageDetection detection = new LanguageDetection(settings.asConfig(), languages);
        try {
            detectLanguage(detection, "abc.xhtml");
            Assert.fail();
        } catch (MessageException e) {
            assertThat(e.getMessage()).contains("Language of file 'abc.xhtml' can not be decided as the file matches patterns of both ").contains("sonar.lang.patterns.web : **/*.xhtml").contains("sonar.lang.patterns.xml : **/*.xhtml");
        }
    }

    @Test
    public void solve_conflict_using_filepattern() throws Exception {
        LanguagesRepository languages = new org.sonar.scanner.repository.language.DefaultLanguagesRepository(new Languages(new LanguageDetectionTest.MockLanguage("xml", "xhtml"), new LanguageDetectionTest.MockLanguage("web", "xhtml")));
        settings.setProperty("sonar.lang.patterns.xml", "xml/**");
        settings.setProperty("sonar.lang.patterns.web", "web/**");
        LanguageDetection detection = new LanguageDetection(settings.asConfig(), languages);
        assertThat(detectLanguage(detection, "xml/abc.xhtml")).isEqualTo("xml");
        assertThat(detectLanguage(detection, "web/abc.xhtml")).isEqualTo("web");
    }

    @Test
    public void fail_if_conflicting_filepattern() throws Exception {
        LanguagesRepository languages = new org.sonar.scanner.repository.language.DefaultLanguagesRepository(new Languages(new LanguageDetectionTest.MockLanguage("abap", "abap"), new LanguageDetectionTest.MockLanguage("cobol", "cobol")));
        settings.setProperty("sonar.lang.patterns.abap", "*.abap,*.txt");
        settings.setProperty("sonar.lang.patterns.cobol", "*.cobol,*.txt");
        LanguageDetection detection = new LanguageDetection(settings.asConfig(), languages);
        assertThat(detectLanguage(detection, "abc.abap")).isEqualTo("abap");
        assertThat(detectLanguage(detection, "abc.cobol")).isEqualTo("cobol");
        try {
            detectLanguage(detection, "abc.txt");
            Assert.fail();
        } catch (MessageException e) {
            assertThat(e.getMessage()).contains("Language of file 'abc.txt' can not be decided as the file matches patterns of both ").contains("sonar.lang.patterns.abap : *.abap,*.txt").contains("sonar.lang.patterns.cobol : *.cobol,*.txt");
        }
    }

    static class MockLanguage implements Language {
        private final String key;

        private final String[] extensions;

        MockLanguage(String key, String... extensions) {
            this.key = key;
            this.extensions = extensions;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getName() {
            return key;
        }

        @Override
        public String[] getFileSuffixes() {
            return extensions;
        }
    }
}

