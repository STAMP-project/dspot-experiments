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
package org.sonar.scanner.report;


import ScannerReport.ContextProperty;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.scanner.config.DefaultConfiguration;
import org.sonar.scanner.protocol.output.ScannerReport;
import org.sonar.scanner.protocol.output.ScannerReportWriter;
import org.sonar.scanner.repository.ContextPropertiesCache;


public class ContextPropertiesPublisherTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ScannerReportWriter writer = Mockito.mock(ScannerReportWriter.class);

    private ContextPropertiesCache cache = new ContextPropertiesCache();

    private DefaultConfiguration config = Mockito.mock(DefaultConfiguration.class);

    private Map<String, String> props = new HashMap<>();

    private ContextPropertiesPublisher underTest = new ContextPropertiesPublisher(cache, config);

    @Test
    public void publish_writes_properties_to_report() {
        cache.put("foo1", "bar1");
        cache.put("foo2", "bar2");
        underTest.publish(writer);
        List<ScannerReport.ContextProperty> expected = Arrays.asList(ContextPropertiesPublisherTest.newContextProperty("foo1", "bar1"), ContextPropertiesPublisherTest.newContextProperty("foo2", "bar2"));
        expectWritten(expected);
    }

    @Test
    public void publish_writes_no_properties_to_report() {
        underTest.publish(writer);
        expectWritten(Collections.emptyList());
    }

    @Test
    public void publish_settings_prefixed_with_sonar_analysis_for_webhooks() {
        props.put("foo", "should not be exported");
        props.put("sonar.analysis.revision", "ab45b3");
        props.put("sonar.analysis.build.number", "B123");
        underTest.publish(writer);
        List<ScannerReport.ContextProperty> expected = Arrays.asList(ContextPropertiesPublisherTest.newContextProperty("sonar.analysis.revision", "ab45b3"), ContextPropertiesPublisherTest.newContextProperty("sonar.analysis.build.number", "B123"));
        expectWritten(expected);
    }
}

