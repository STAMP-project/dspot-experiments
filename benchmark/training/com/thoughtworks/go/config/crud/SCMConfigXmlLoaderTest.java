/**
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.config.crud;


import com.thoughtworks.go.config.CaseInsensitiveString;
import com.thoughtworks.go.config.GoConfigHolder;
import com.thoughtworks.go.config.PipelineConfig;
import com.thoughtworks.go.config.exceptions.GoConfigInvalidException;
import com.thoughtworks.go.config.materials.Filter;
import com.thoughtworks.go.config.materials.IgnoredFiles;
import com.thoughtworks.go.config.materials.PluggableSCMMaterialConfig;
import com.thoughtworks.go.domain.config.Configuration;
import com.thoughtworks.go.domain.scm.SCM;
import com.thoughtworks.go.security.GoCipher;
import com.thoughtworks.go.util.GoConstants;
import com.thoughtworks.go.util.XsdValidationException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class SCMConfigXmlLoaderTest extends BaseConfigXmlLoaderTest {
    static final String VALID_SCM = " <scm id='scm-id' name='name1'><pluginConfiguration id='id' version='1.0'/><configuration><property><key>url</key><value>http://go</value></property></configuration></scm>";

    static final String VALID_SCM_WITH_ID_NAME = " <scm id='%s' name='%s'><pluginConfiguration id='id' version='1.0'/><configuration><property><key>url</key><value>http://go</value></property></configuration></scm>";

    static final String SCM_WITH_MISSING_ID = " <scm name='name1'><pluginConfiguration id='id' version='1.0'/><configuration><property><key>url</key><value>http://go</value></property></configuration></scm>";

    static final String SCM_WITH_INVALID_ID = " <scm id='id with space' name='name1'><pluginConfiguration id='id' version='1.0'/><configuration><property><key>url</key><value>http://go</value></property></configuration></scm>";

    static final String SCM_WITH_EMPTY_ID = " <scm id='' name='name1'><pluginConfiguration id='id' version='1.0'/><configuration><property><key>url</key><value>http://go</value></property></configuration></scm>";

    static final String SCM_WITH_MISSING_NAME = " <scm id='id' ><pluginConfiguration id='id' version='1.0'/><configuration><property><key>url</key><value>http://go</value></property></configuration></scm>";

    static final String SCM_WITH_INVALID_NAME = " <scm id='id' name='name with space'><pluginConfiguration id='id' version='1.0'/><configuration><property><key>url</key><value>http://go</value></property></configuration></scm>";

    static final String SCM_WITH_EMPTY_NAME = " <scm id='id' name=''><pluginConfiguration id='id' version='1.0'/><configuration><property><key>url</key><value>http://go</value></property></configuration></scm>";

    @Test
    public void shouldThrowXsdValidationWhenSCMIdsAreDuplicate() throws Exception {
        String xml = (((("<cruise schemaVersion='" + (GoConstants.CONFIG_SCHEMA_VERSION)) + "\'><scms>\n") + (SCMConfigXmlLoaderTest.VALID_SCM)) + (SCMConfigXmlLoaderTest.VALID_SCM)) + " </scms></cruise>";
        try {
            xmlLoader.loadConfigHolder(xml);
            Assert.fail("should have thrown XsdValidationException");
        } catch (XsdValidationException e) {
            Assert.assertThat(e.getMessage(), anyOf(Matchers.is("Duplicate unique value [scm-id] declared for identity constraint of element \"cruise\"."), Matchers.is("Duplicate unique value [scm-id] declared for identity constraint \"uniqueSCMId\" of element \"cruise\".")));
        }
    }

    @Test
    public void shouldThrowXsdValidationWhenSCMIdIsEmpty() throws Exception {
        String xml = ((("<cruise schemaVersion='" + (GoConstants.CONFIG_SCHEMA_VERSION)) + "\'><scms>\n") + (SCMConfigXmlLoaderTest.SCM_WITH_EMPTY_ID)) + " </scms></cruise>";
        try {
            xmlLoader.loadConfigHolder(xml);
            Assert.fail("should have thrown XsdValidationException");
        } catch (XsdValidationException e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Scm id is invalid. \"\" should conform to the pattern - [a-zA-Z0-9_\\-]{1}[a-zA-Z0-9_\\-.]*"));
        }
    }

    @Test
    public void shouldThrowXsdValidationWhenSCMIdIsInvalid() throws Exception {
        String xml = ((("<cruise schemaVersion='" + (GoConstants.CONFIG_SCHEMA_VERSION)) + "\'><scms>\n") + (SCMConfigXmlLoaderTest.SCM_WITH_INVALID_ID)) + " </scms></cruise>";
        try {
            xmlLoader.loadConfigHolder(xml);
            Assert.fail("should have thrown XsdValidationException");
        } catch (XsdValidationException e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Scm id is invalid. \"id with space\" should conform to the pattern - [a-zA-Z0-9_\\-]{1}[a-zA-Z0-9_\\-.]*"));
        }
    }

    @Test
    public void shouldThrowXsdValidationWhenSCMNamesAreDuplicate() throws Exception {
        String xml = (((("<cruise schemaVersion='" + (GoConstants.CONFIG_SCHEMA_VERSION)) + "\'><scms>\n") + (String.format(SCMConfigXmlLoaderTest.VALID_SCM_WITH_ID_NAME, "1", "scm-name"))) + (String.format(SCMConfigXmlLoaderTest.VALID_SCM_WITH_ID_NAME, "2", "scm-name"))) + " </scms></cruise>";
        try {
            xmlLoader.loadConfigHolder(xml);
            Assert.fail("should have thrown XsdValidationException");
        } catch (XsdValidationException e) {
            Assert.assertThat(e.getMessage(), anyOf(Matchers.is("Duplicate unique value [scm-name] declared for identity constraint of element \"scms\"."), Matchers.is("Duplicate unique value [scm-name] declared for identity constraint \"uniqueSCMName\" of element \"scms\".")));
        }
    }

    @Test
    public void shouldThrowXsdValidationWhenSCMNameIsMissing() throws Exception {
        String xml = ((("<cruise schemaVersion='" + (GoConstants.CONFIG_SCHEMA_VERSION)) + "\'><scms>\n") + (SCMConfigXmlLoaderTest.SCM_WITH_MISSING_NAME)) + " </scms></cruise>";
        try {
            xmlLoader.loadConfigHolder(xml);
            Assert.fail("should have thrown XsdValidationException");
        } catch (XsdValidationException e) {
            Assert.assertThat(e.getMessage(), Matchers.is("\"Name\" is required for Scm"));
        }
    }

    @Test
    public void shouldThrowXsdValidationWhenSCMNameIsEmpty() throws Exception {
        String xml = ((("<cruise schemaVersion='" + (GoConstants.CONFIG_SCHEMA_VERSION)) + "\'><scms>\n") + (SCMConfigXmlLoaderTest.SCM_WITH_EMPTY_NAME)) + " </scms></cruise>";
        try {
            xmlLoader.loadConfigHolder(xml);
            Assert.fail("should have thrown XsdValidationException");
        } catch (XsdValidationException e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Name is invalid. \"\" should conform to the pattern - [a-zA-Z0-9_\\-]{1}[a-zA-Z0-9_\\-.]*"));
        }
    }

    @Test
    public void shouldThrowXsdValidationWhenSCMNameIsInvalid() throws Exception {
        String xml = ((("<cruise schemaVersion='" + (GoConstants.CONFIG_SCHEMA_VERSION)) + "\'><scms>\n") + (SCMConfigXmlLoaderTest.SCM_WITH_INVALID_NAME)) + " </scms></cruise>";
        try {
            xmlLoader.loadConfigHolder(xml);
            Assert.fail("should have thrown XsdValidationException");
        } catch (XsdValidationException e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Name is invalid. \"name with space\" should conform to the pattern - [a-zA-Z0-9_\\-]{1}[a-zA-Z0-9_\\-.]*"));
        }
    }

    @Test
    public void shouldGenerateSCMIdWhenMissing() throws Exception {
        String xml = ((("<cruise schemaVersion='" + (GoConstants.CONFIG_SCHEMA_VERSION)) + "\'><scms>\n") + (SCMConfigXmlLoaderTest.SCM_WITH_MISSING_ID)) + " </scms></cruise>";
        GoConfigHolder configHolder = xmlLoader.loadConfigHolder(xml);
        Assert.assertThat(configHolder.config.getSCMs().get(0).getId(), Matchers.is(notNullValue()));
    }

    @Test
    public void shouldFailValidationIfSCMWithDuplicateFingerprintExists() throws Exception {
        SCMPropertyConfiguration scmConfiguration = new SCMPropertyConfiguration();
        scmConfiguration.add(new SCMProperty("SCM-KEY1"));
        scmConfiguration.add(new SCMProperty("SCM-KEY2").with(REQUIRED, false).with(PART_OF_IDENTITY, false));
        scmConfiguration.add(new SCMProperty("SCM-KEY3").with(REQUIRED, false).with(PART_OF_IDENTITY, false).with(SECURE, true));
        SCMMetadataStore.getInstance().addMetadataFor("plugin-1", new SCMConfigurations(scmConfiguration), null);
        String xml = (((((((((((((((((((((((((((((((((((((("<cruise schemaVersion='" + (GoConstants.CONFIG_SCHEMA_VERSION)) + "\'>\n") + "<scms>\n") + "    <scm id=\'scm-id-1\' name=\'name-1\'>\n") + "\t\t<pluginConfiguration id=\'plugin-1\' version=\'1.0\'/>\n") + "      <configuration>\n") + "        <property>\n") + "          <key>SCM-KEY1</key>\n") + "          <value>scm-key1</value>\n") + "        </property>\n") + "        <property>\n") + "          <key>SCM-KEY2</key>\n") + "          <value>scm-key2</value>\n") + "        </property>\n") + "        <property>\n") + "          <key>SCM-KEY3</key>\n") + "          <value>scm-key3</value>\n") + "        </property>\n") + "      </configuration>\n") + "    </scm>\n") + "    <scm id=\'scm-id-2\' name=\'name-2\'>\n") + "\t\t<pluginConfiguration id=\'plugin-1\' version=\'1.0\'/>\n") + "      <configuration>\n") + "        <property>\n") + "          <key>SCM-KEY1</key>\n") + "          <value>scm-key1</value>\n") + "        </property>\n") + "        <property>\n") + "          <key>SCM-KEY2</key>\n") + "          <value>another-scm-key2</value>\n") + "        </property>\n") + "        <property>\n") + "          <key>SCM-KEY3</key>\n") + "          <value>another-scm-key3</value>\n") + "        </property>\n") + "      </configuration>\n") + "    </scm>\n") + "  </scms>") + "</cruise>";
        try {
            xmlLoader.loadConfigHolder(xml);
            Assert.fail("should have thrown duplicate fingerprint exception");
        } catch (GoConfigInvalidException e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Cannot save SCM, found duplicate SCMs. name-1, name-2"));
        }
    }

    @Test
    public void shouldLoadAutoUpdateValueForSCMWhenLoadedFromConfigFile() throws Exception {
        String configTemplate = ((((((((((((("<cruise schemaVersion='" + (GoConstants.CONFIG_SCHEMA_VERSION)) + "'>") + "<scms>") + "	<scm id='2ef830d7-dd66-42d6-b393-64a84646e557' name='scm-name' autoUpdate='%s' >") + "		<pluginConfiguration id='yum' version='1' />") + "       <configuration>") + "           <property>") + "               <key>SCM_URL</key>") + "               <value>http://fake-scm/git/go-cd</value>") + "               </property>") + "       </configuration>") + "   </scm>") + "</scms>") + "</cruise>";
        String configContent = String.format(configTemplate, false);
        GoConfigHolder holder = xmlLoader.loadConfigHolder(configContent);
        SCM scm = holder.config.getSCMs().find("2ef830d7-dd66-42d6-b393-64a84646e557");
        Assert.assertThat(scm.isAutoUpdate(), Matchers.is(false));
        configContent = String.format(configTemplate, true);
        holder = xmlLoader.loadConfigHolder(configContent);
        scm = holder.config.getSCMs().find("2ef830d7-dd66-42d6-b393-64a84646e557");
        Assert.assertThat(scm.isAutoUpdate(), Matchers.is(true));
    }

    @Test
    public void shouldResolveSCMReferenceElementForAMaterialInConfig() throws Exception {
        String xml = (((((((((((((((((((((((("<cruise schemaVersion='" + (GoConstants.CONFIG_SCHEMA_VERSION)) + "\'>\n") + "<scms>\n") + "    <scm id=\'scm-id\' name=\'scm-name\'>\n") + "\t\t<pluginConfiguration id=\'plugin-id\' version=\'1.0\'/>\n") + "      <configuration>\n") + "        <property>\n") + "          <key>url</key>\n") + "          <value>http://go</value>\n") + "        </property>\n") + "      </configuration>\n") + "    </scm>\n") + "  </scms>") + "<pipelines group=\"group_name\">\n") + "  <pipeline name=\"new_name\">\n") + "    <materials>\n") + "      <scm ref=\'scm-id\' />\n") + "    </materials>\n") + "    <stage name=\"stage_name\">\n") + "      <jobs>\n") + "        <job name=\"job_name\" />\n") + "      </jobs>\n") + "    </stage>\n") + "  </pipeline>\n") + "</pipelines></cruise>";
        GoConfigHolder goConfigHolder = xmlLoader.loadConfigHolder(xml);
        PipelineConfig pipelineConfig = goConfigHolder.config.pipelineConfigByName(new CaseInsensitiveString("new_name"));
        PluggableSCMMaterialConfig pluggableSCMMaterialConfig = ((PluggableSCMMaterialConfig) (pipelineConfig.materialConfigs().get(0)));
        Assert.assertThat(pluggableSCMMaterialConfig.getSCMConfig(), Matchers.is(goConfigHolder.config.getSCMs().get(0)));
        Assert.assertThat(pluggableSCMMaterialConfig.getFolder(), Matchers.is(nullValue()));
        Assert.assertThat(pluggableSCMMaterialConfig.filter(), Matchers.is(new Filter()));
    }

    @Test
    public void shouldReadFolderAndFilterForPluggableSCMMaterialConfig() throws Exception {
        String xml = ((((((((((((((((((((((((((((("<cruise schemaVersion='" + (GoConstants.CONFIG_SCHEMA_VERSION)) + "\'>\n") + "<scms>\n") + "    <scm id=\'scm-id\' name=\'scm-name\'>\n") + "\t\t<pluginConfiguration id=\'plugin-id\' version=\'1.0\'/>\n") + "      <configuration>\n") + "        <property>\n") + "          <key>url</key>\n") + "          <value>http://go</value>\n") + "        </property>\n") + "      </configuration>\n") + "    </scm>\n") + "  </scms>") + "<pipelines group=\"group_name\">\n") + "  <pipeline name=\"new_name\">\n") + "    <materials>\n") + "      <scm ref=\'scm-id\' dest=\'dest\'>\n") + "            <filter>\n") + "                <ignore pattern=\"x\"/>\n") + "                <ignore pattern=\"y\"/>\n") + "            </filter>\n") + "      </scm>\n") + "    </materials>\n") + "    <stage name=\"stage_name\">\n") + "      <jobs>\n") + "        <job name=\"job_name\" />\n") + "      </jobs>\n") + "    </stage>\n") + "  </pipeline>\n") + "</pipelines></cruise>";
        GoConfigHolder goConfigHolder = xmlLoader.loadConfigHolder(xml);
        PipelineConfig pipelineConfig = goConfigHolder.config.pipelineConfigByName(new CaseInsensitiveString("new_name"));
        PluggableSCMMaterialConfig pluggableSCMMaterialConfig = ((PluggableSCMMaterialConfig) (pipelineConfig.materialConfigs().get(0)));
        Assert.assertThat(pluggableSCMMaterialConfig.getSCMConfig(), Matchers.is(goConfigHolder.config.getSCMs().get(0)));
        Assert.assertThat(pluggableSCMMaterialConfig.getFolder(), Matchers.is("dest"));
        Assert.assertThat(pluggableSCMMaterialConfig.filter(), Matchers.is(new Filter(new IgnoredFiles("x"), new IgnoredFiles("y"))));
    }

    @Test
    public void shouldBeAbleToResolveSecureConfigPropertiesForSCMs() throws Exception {
        String encryptedValue = new GoCipher().encrypt("secure-two");
        String xml = (((((((((((((((((((((((((((((((((("<cruise schemaVersion='" + (GoConstants.CONFIG_SCHEMA_VERSION)) + "\'>\n") + "<scms>\n") + "    <scm id=\'scm-id\' name=\'name\'>\n") + "\t\t<pluginConfiguration id=\'plugin-id\' version=\'1.0\'/>\n") + "      <configuration>\n") + "        <property>\n") + "          <key>plain</key>\n") + "          <value>value</value>\n") + "        </property>\n") + "        <property>\n") + "          <key>secure-one</key>\n") + "          <value>secure-value</value>\n") + "        </property>\n") + "        <property>\n") + "          <key>secure-two</key>\n") + "          <encryptedValue>") + encryptedValue) + "</encryptedValue>\n") + "        </property>\n") + "      </configuration>\n") + "    </scm>\n") + "  </scms>") + "<pipelines group=\"group_name\">\n") + "  <pipeline name=\"new_name\">\n") + "    <materials>\n") + "      <scm ref=\'scm-id\' />\n") + "    </materials>\n") + "    <stage name=\"stage_name\">\n") + "      <jobs>\n") + "        <job name=\"job_name\" />\n") + "      </jobs>\n") + "    </stage>\n") + "  </pipeline>\n") + "</pipelines></cruise>";
        // meta data of scm
        SCMPropertyConfiguration scmConfiguration = new SCMPropertyConfiguration();
        scmConfiguration.add(new SCMProperty("plain"));
        scmConfiguration.add(new SCMProperty("secure-one").with(SCMConfiguration.SECURE, true));
        scmConfiguration.add(new SCMProperty("secure-two").with(SCMConfiguration.SECURE, true));
        SCMMetadataStore.getInstance().addMetadataFor("plugin-id", new SCMConfigurations(scmConfiguration), null);
        GoConfigHolder goConfigHolder = xmlLoader.loadConfigHolder(xml);
        SCM scmConfig = goConfigHolder.config.getSCMs().first();
        PipelineConfig pipelineConfig = goConfigHolder.config.pipelineConfigByName(new CaseInsensitiveString("new_name"));
        PluggableSCMMaterialConfig pluggableSCMMaterialConfig = ((PluggableSCMMaterialConfig) (pipelineConfig.materialConfigs().get(0)));
        Assert.assertThat(pluggableSCMMaterialConfig.getSCMConfig(), Matchers.is(scmConfig));
        Configuration configuration = pluggableSCMMaterialConfig.getSCMConfig().getConfiguration();
        Assert.assertThat(configuration.get(0).getConfigurationValue().getValue(), Matchers.is("value"));
        Assert.assertThat(configuration.get(1).getEncryptedValue(), Matchers.is(new GoCipher().encrypt("secure-value")));
        Assert.assertThat(configuration.get(2).getEncryptedValue(), Matchers.is(encryptedValue));
    }
}

