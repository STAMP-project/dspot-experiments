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
package org.sonar.server.platform.monitoring;


import ProtobufSystemInfo.Section;
import org.elasticsearch.ElasticsearchException;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.process.systeminfo.protobuf.ProtobufSystemInfo;
import org.sonar.server.es.EsClient;
import org.sonar.server.es.EsTester;


public class EsIndexesSectionTest {
    @Rule
    public EsTester es = EsTester.create();

    private EsIndexesSection underTest = new EsIndexesSection(es.client());

    @Test
    public void name() {
        assertThat(underTest.toProtobuf().getName()).isEqualTo("Search Indexes");
    }

    @Test
    public void index_attributes() {
        ProtobufSystemInfo.Section section = underTest.toProtobuf();
        // one index "issues"
        assertThat(attribute(section, "Index issues - Docs").getLongValue()).isEqualTo(0L);
        assertThat(attribute(section, "Index issues - Shards").getLongValue()).isGreaterThan(0);
        assertThat(attribute(section, "Index issues - Store Size").getStringValue()).isNotNull();
    }

    @Test
    public void attributes_displays_exception_message_when_cause_null_when_client_fails() {
        EsClient esClientMock = Mockito.mock(EsClient.class);
        EsIndexesSection underTest = new EsIndexesSection(esClientMock);
        Mockito.when(esClientMock.prepareStats()).thenThrow(new RuntimeException("RuntimeException with no cause"));
        ProtobufSystemInfo.Section section = underTest.toProtobuf();
        SystemInfoTesting.assertThatAttributeIs(section, "Error", "RuntimeException with no cause");
    }

    @Test
    public void attributes_displays_exception_message_when_cause_is_not_ElasticSearchException_when_client_fails() {
        EsClient esClientMock = Mockito.mock(EsClient.class);
        EsIndexesSection underTest = new EsIndexesSection(esClientMock);
        Mockito.when(esClientMock.prepareStats()).thenThrow(new RuntimeException("RuntimeException with cause not ES", new IllegalArgumentException("some cause message")));
        ProtobufSystemInfo.Section section = underTest.toProtobuf();
        SystemInfoTesting.assertThatAttributeIs(section, "Error", "RuntimeException with cause not ES");
    }

    @Test
    public void attributes_displays_cause_message_when_cause_is_ElasticSearchException_when_client_fails() {
        EsClient esClientMock = Mockito.mock(EsClient.class);
        EsIndexesSection underTest = new EsIndexesSection(esClientMock);
        Mockito.when(esClientMock.prepareStats()).thenThrow(new RuntimeException("RuntimeException with ES cause", new ElasticsearchException("some cause message")));
        ProtobufSystemInfo.Section section = underTest.toProtobuf();
        SystemInfoTesting.assertThatAttributeIs(section, "Error", "some cause message");
    }
}

