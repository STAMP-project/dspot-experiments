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
package org.sonar.scanner;


import CoreProperties.PROJECT_DATE_PROPERTY;
import CoreProperties.PROJECT_VERSION_PROPERTY;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.time.Clock;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.utils.MessageException;


@RunWith(DataProviderRunner.class)
public class ProjectInfoTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private MapSettings settings = new MapSettings();

    private Clock clock = Mockito.mock(Clock.class);

    private ProjectInfo underTest = new ProjectInfo(settings.asConfig(), clock);

    @Test
    public void testSimpleDateTime() {
        OffsetDateTime date = OffsetDateTime.of(2017, 1, 1, 12, 13, 14, 0, ZoneOffset.ofHours(2));
        settings.appendProperty(PROJECT_DATE_PROPERTY, "2017-01-01T12:13:14+0200");
        settings.appendProperty(PROJECT_VERSION_PROPERTY, "version");
        underTest.start();
        assertThat(underTest.getAnalysisDate()).isEqualTo(Date.from(date.toInstant()));
        assertThat(underTest.getProjectVersion()).contains("version");
    }

    @Test
    public void testSimpleDate() {
        LocalDate date = LocalDate.of(2017, 1, 1);
        settings.appendProperty(PROJECT_DATE_PROPERTY, "2017-01-01");
        underTest.start();
        assertThat(underTest.getAnalysisDate()).isEqualTo(Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
    }

    @Test
    public void emptyDate() {
        settings.setProperty(PROJECT_DATE_PROPERTY, "");
        settings.setProperty(PROJECT_VERSION_PROPERTY, "version");
        expectedException.expect(RuntimeException.class);
        underTest.start();
    }

    @Test
    public void fail_with_too_long_version() {
        String version = randomAlphabetic(101);
        settings.setProperty(PROJECT_DATE_PROPERTY, "2017-01-01");
        settings.setProperty(PROJECT_VERSION_PROPERTY, version);
        expectedException.expect(MessageException.class);
        expectedException.expectMessage(((("\"" + version) + "\" is not a valid project version. ") + "The maximum length is 100 characters."));
        underTest.start();
    }
}

