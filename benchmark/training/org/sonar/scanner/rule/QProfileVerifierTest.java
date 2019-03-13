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
package org.sonar.scanner.rule;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.utils.MessageException;
import org.sonar.scanner.scan.filesystem.InputComponentStore;


public class QProfileVerifierTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private InputComponentStore store;

    private QualityProfiles profiles;

    private MapSettings settings = new MapSettings();

    @Test
    public void should_log_all_used_profiles() {
        store.put("foo", new TestInputFileBuilder("foo", "src/Bar.java").setLanguage("java").build());
        store.put("foo", new TestInputFileBuilder("foo", "src/Baz.cbl").setLanguage("cobol").build());
        QProfileVerifier profileLogger = new QProfileVerifier(settings.asConfig(), store, profiles);
        Logger logger = Mockito.mock(Logger.class);
        profileLogger.execute(logger);
        Mockito.verify(logger).info("Quality profile for {}: {}", "java", "My Java profile");
        Mockito.verify(logger).info("Quality profile for {}: {}", "cobol", "My Cobol profile");
    }

    @Test
    public void should_fail_if_default_profile_not_used() {
        store.put("foo", new TestInputFileBuilder("foo", "src/Bar.java").setLanguage("java").build());
        settings.setProperty("sonar.profile", "Unknown");
        QProfileVerifier profileLogger = new QProfileVerifier(settings.asConfig(), store, profiles);
        thrown.expect(MessageException.class);
        thrown.expectMessage("sonar.profile was set to 'Unknown' but didn't match any profile for any language. Please check your configuration.");
        profileLogger.execute();
    }

    @Test
    public void should_not_fail_if_no_language_on_project() {
        settings.setProperty("sonar.profile", "Unknown");
        QProfileVerifier profileLogger = new QProfileVerifier(settings.asConfig(), store, profiles);
        profileLogger.execute();
    }

    @Test
    public void should_not_fail_if_default_profile_used_at_least_once() {
        store.put("foo", new TestInputFileBuilder("foo", "src/Bar.java").setLanguage("java").build());
        settings.setProperty("sonar.profile", "My Java profile");
        QProfileVerifier profileLogger = new QProfileVerifier(settings.asConfig(), store, profiles);
        profileLogger.execute();
    }
}

