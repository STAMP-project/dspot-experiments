/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog.plugins.sidecar.collectors;


import com.lordofthejars.nosqlunit.annotation.CustomComparisonStrategy;
import com.lordofthejars.nosqlunit.mongodb.InMemoryMongoDb;
import com.lordofthejars.nosqlunit.mongodb.MongoFlexibleComparisonStrategy;
import org.graylog.plugins.sidecar.database.MongoConnectionRule;
import org.graylog.plugins.sidecar.rest.models.Configuration;
import org.graylog.plugins.sidecar.rest.models.ConfigurationVariable;
import org.graylog.plugins.sidecar.rest.models.NodeDetails;
import org.graylog.plugins.sidecar.rest.models.Sidecar;
import org.graylog.plugins.sidecar.services.ConfigurationService;
import org.graylog.plugins.sidecar.services.ConfigurationVariableService;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


@CustomComparisonStrategy(comparisonStrategy = MongoFlexibleComparisonStrategy.class)
public class ConfigurationServiceTest {
    private final String FILEBEAT_CONF_ID = "5b8fe5f97ad37b17a44e2a34";

    @Mock
    private Sidecar sidecar;

    @Mock
    private NodeDetails nodeDetails;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @ClassRule
    public static final InMemoryMongoDb IN_MEMORY_MONGO_DB = newInMemoryMongoDbRule().build();

    @Rule
    public MongoConnectionRule mongoRule = MongoConnectionRule.build("test");

    private ConfigurationService configurationService;

    private ConfigurationVariableService configurationVariableService;

    private Configuration configuration;

    @Test
    public void testTemplateRender() throws Exception {
        final String TEMPLATE = "foo bar\n nodename: ${sidecar.nodeName}\n";
        final String TEMPLATE_RENDERED = "foo bar\n nodename: mockymock\n";
        configuration = buildTestConfig(TEMPLATE);
        this.configurationService.save(configuration);
        Configuration result = this.configurationService.renderConfigurationForCollector(sidecar, configuration);
        Configuration configWithNewline = buildTestConfig(TEMPLATE_RENDERED);
        Assert.assertEquals(configWithNewline, result);
    }

    @Test
    public void testAddMissingNewline() throws Exception {
        configuration = buildTestConfig("template\n without\n newline");
        this.configurationService.save(configuration);
        Configuration result = this.configurationService.renderConfigurationForCollector(sidecar, configuration);
        Configuration configWithNewline = buildTestConfig(((configuration.template()) + "\n"));
        Assert.assertEquals(configWithNewline, result);
    }

    @Test
    public void testTemplateRenderWithConfigurationVariables() throws Exception {
        final String TEMPLATE = "foo bar\n myVariable: ${user.myVariable}\n";
        final String TEMPLATE_RENDERED = "foo bar\n myVariable: content of myVariable\n";
        configuration = buildTestConfig(TEMPLATE);
        this.configurationService.save(configuration);
        ConfigurationVariable myVariable = ConfigurationVariable.create("myVariable", "desc", "content of myVariable");
        this.configurationVariableService.save(myVariable);
        Configuration result = this.configurationService.renderConfigurationForCollector(sidecar, configuration);
        Assert.assertEquals(TEMPLATE_RENDERED, result.template());
    }
}

