/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.decisiontable;


import DecisionTableInputType.CSV;
import IoUtils.UTF8_CHARSET;
import ResourceType.DTABLE;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.io.Resource;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;


public class DumpGeneratedDrlTest {
    private final String DUMMY_DTABLE_CSV_SOURCE = "\"RuleSet\",\"org.drools.decisiontable\",,,\n" + ((((((((",,,,\n" + ",,,,\n") + "\"RuleTable agenda-group\",,,,\n") + "\"NAME\",\"CONDITION\",\"Lock-On-Active\",\"Auto-Focus\",\"ACTION\"\n") + ",,,,\n") + ",\"String(this == \"\"$param\"\")\",,,\n") + "\"rule names\",\"string for test\",,,\n") + "\"lockOnActiveRule\",\"lockOnActiveRule\",\"true\",,\n") + "\"autoFocusRule\",\"autoFocusRule\",,\"true\",");

    private File dumpDir;

    private String dumpDirPropOrigValue;

    @Test
    public void testGeneratedDrlFromIsDumpedIfSpecified() {
        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(CSV);
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Resource resource = ResourceFactory.newByteArrayResource(DUMMY_DTABLE_CSV_SOURCE.getBytes(UTF8_CHARSET));
        resource.setSourcePath("some/source/path/dummy-dtable.csv");
        kbuilder.add(resource, DTABLE, dtconf);
        if (kbuilder.hasErrors()) {
            Assert.fail(("Unexpected Drools compilation errors: " + (kbuilder.getErrors().toString())));
        }
        assertGeneratedDrlExists(dumpDir, "some_source_path_dummy-dtable.csv.drl");
    }

    @Test
    public void testDTableWithNullSrcPathIsCorrectlyDumped() {
        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(CSV);
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Resource resource = ResourceFactory.newByteArrayResource(DUMMY_DTABLE_CSV_SOURCE.getBytes(UTF8_CHARSET));
        kbuilder.add(resource, DTABLE, dtconf);
        if (kbuilder.hasErrors()) {
            Assert.fail(("Unexpected Drools compilation errors: " + (kbuilder.getErrors().toString())));
        }
        assertGeneratedDrlExists(dumpDir, null);
    }
}

