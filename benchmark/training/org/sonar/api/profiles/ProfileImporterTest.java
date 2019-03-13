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
package org.sonar.api.profiles;


import java.io.Reader;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.sonar.api.utils.ValidationMessages;


public class ProfileImporterTest {
    @Test
    public void testSupportedLanguages() {
        ProfileImporter inmporter = new ProfileImporter("all", "All") {
            @Override
            public RulesProfile importProfile(Reader reader, ValidationMessages messages) {
                return null;
            }
        };
        inmporter.setSupportedLanguages("java", "php");
        Assert.assertThat(inmporter.getSupportedLanguages().length, Is.is(2));
        Assert.assertThat(inmporter.getSupportedLanguages()[0], Is.is("java"));
        Assert.assertThat(inmporter.getSupportedLanguages()[1], Is.is("php"));
    }

    @Test
    public void supportAllLanguages() {
        ProfileImporter importer = new ProfileImporter("all", "All") {
            @Override
            public RulesProfile importProfile(Reader reader, ValidationMessages messages) {
                return null;
            }
        };
        Assert.assertThat(importer.getSupportedLanguages().length, Is.is(0));
        importer.setSupportedLanguages();
        Assert.assertThat(importer.getSupportedLanguages().length, Is.is(0));
    }
}

