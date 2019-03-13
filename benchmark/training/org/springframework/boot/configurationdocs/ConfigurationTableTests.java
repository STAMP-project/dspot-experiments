/**
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.configurationdocs;


import org.junit.Test;
import org.springframework.boot.configurationmetadata.ConfigurationMetadataProperty;


/**
 *
 *
 * @author Brian Clozel
 */
public class ConfigurationTableTests {
    private static String NEWLINE = System.lineSeparator();

    @Test
    public void simpleTable() {
        ConfigurationTable table = new ConfigurationTable("test");
        ConfigurationMetadataProperty first = new ConfigurationMetadataProperty();
        first.setId("spring.test.prop");
        first.setDefaultValue("something");
        first.setDescription("This is a description.");
        first.setType("java.lang.String");
        ConfigurationMetadataProperty second = new ConfigurationMetadataProperty();
        second.setId("spring.test.other");
        second.setDefaultValue("other value");
        second.setDescription("This is another description.");
        second.setType("java.lang.String");
        table.addEntry(new SingleKeyEntry(first));
        table.addEntry(new SingleKeyEntry(second));
        assertThat(table.toAsciidocTable()).isEqualTo((((((((((((((((((((((("[cols=\"1,1,2\", options=\"header\"]" + (ConfigurationTableTests.NEWLINE)) + "|===") + (ConfigurationTableTests.NEWLINE)) + "|Key|Default Value|Description") + (ConfigurationTableTests.NEWLINE)) + (ConfigurationTableTests.NEWLINE)) + "|`+spring.test.other+`") + (ConfigurationTableTests.NEWLINE)) + "|`+other value+`") + (ConfigurationTableTests.NEWLINE)) + "|+++This is another description.+++") + (ConfigurationTableTests.NEWLINE)) + (ConfigurationTableTests.NEWLINE)) + "|`+spring.test.prop+`") + (ConfigurationTableTests.NEWLINE)) + "|`+something+`") + (ConfigurationTableTests.NEWLINE)) + "|+++This is a description.+++") + (ConfigurationTableTests.NEWLINE)) + (ConfigurationTableTests.NEWLINE)) + "|===") + (ConfigurationTableTests.NEWLINE)));
    }
}

