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
package org.sonar.core.platform;


import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(DataProviderRunner.class)
public class ServerIdTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void parse_throws_NPE_if_argument_is_null() {
        expectedException.expect(NullPointerException.class);
        ServerId.parse(null);
    }

    @Test
    public void parse_parses_deprecated_format_serverId() {
        String deprecated = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        ServerId serverId = ServerId.parse(deprecated);
        assertThat(serverId.getFormat()).isEqualTo(Format.DEPRECATED);
        assertThat(serverId.getDatasetId()).isEqualTo(deprecated);
        assertThat(serverId.getDatabaseId()).isEmpty();
        assertThat(serverId.toString()).isEqualTo(deprecated);
    }

    @Test
    public void parse_does_not_support_deprecated_server_id_with_database_id() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("serverId does not have a supported length");
        ServerId.parse((((randomAlphabetic(ServerId.DATABASE_ID_LENGTH)) + (ServerId.SPLIT_CHARACTER)) + (randomAlphabetic(ServerId.DEPRECATED_SERVER_ID_LENGTH))));
    }

    @Test
    public void of_throws_NPE_if_datasetId_is_null() {
        expectedException.expect(NullPointerException.class);
        ServerId.of(randomAlphabetic(ServerId.DATABASE_ID_LENGTH), null);
    }

    @Test
    public void of_throws_IAE_if_datasetId_is_empty() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Illegal datasetId length (0)");
        ServerId.of(randomAlphabetic(ServerId.DATABASE_ID_LENGTH), "");
    }

    @Test
    public void of_throws_IAE_if_databaseId_is_empty() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Illegal databaseId length (0)");
        ServerId.of("", randomAlphabetic(ServerId.UUID_DATASET_ID_LENGTH));
    }
}

