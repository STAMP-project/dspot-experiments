/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.drools.compiler.integrationtests;


import MBeansOption.ENABLED;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.definition.rule.Rule;


@RunWith(Parameterized.class)
public class AnnotationsCepTest {
    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public AnnotationsCepTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Test
    public void testRuleAnnotation() {
        final String drl = ((((((((((("package org.drools.compiler.integrationtests\n" + "import ") + (Person.class.getCanonicalName())) + "; \n") + "rule X\n") + "    @author(\"John Doe\")\n") + "    @output(Hello World!)\n")// backward compatibility
         + "    @value( 10 + 10 )\n") + "    @alt( \"Hello \"+\"World!\" )\n") + "when\n") + "    Person()\n") + "then\n") + "end";
        kieBaseTestConfiguration.setAdditionalKieBaseOptions(ENABLED);
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("annotations-cep-test", kieBaseTestConfiguration, drl);
        final Rule rule = kbase.getRule("org.drools.compiler.integrationtests", "X");
        Assert.assertEquals("John Doe", rule.getMetaData().get("author"));
        Assert.assertEquals("Hello World!", rule.getMetaData().get("output"));
        Assert.assertEquals(20, ((Number) (rule.getMetaData().get("value"))).intValue());
        Assert.assertEquals("Hello World!", rule.getMetaData().get("alt"));
    }

    @Test
    public void testRuleAnnotation2() {
        final String drl = (((((((("package org.drools.compiler.integrationtests\n" + "import ") + (Person.class.getCanonicalName())) + "; \n") + "rule X\n") + "    @alt(\" \\\"<- these are supposed to be the only quotes ->\\\" \")\n") + "when\n") + "    Person()\n") + "then\n") + "end";
        kieBaseTestConfiguration.setAdditionalKieBaseOptions(ENABLED);
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("annotations-cep-test", kieBaseTestConfiguration, drl);
        final Rule rule = kbase.getRule("org.drools.compiler.integrationtests", "X");
        Assert.assertEquals(" \"<- these are supposed to be the only quotes ->\" ", rule.getMetaData().get("alt"));
    }
}

