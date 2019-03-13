/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.testcoverage.functional;


import DecisionTableInputType.CSV;
import KieServices.Factory;
import ResourceType.DRL;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.ResourceUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.Resource;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.api.runtime.rule.Variable;
import org.kie.internal.utils.KieHelper;


/**
 * Tests Drools engine capabilities regarding Unicode characters
 */
@RunWith(Parameterized.class)
public class UnicodeTest {
    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public UnicodeTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Test
    public void testJapanese() {
        final KieServices kieServices = Factory.get();
        final Resource resource = kieServices.getResources().newClassPathResource("unicode.drl", getClass());
        final KieBase kbase = KieBaseUtil.getKieBaseFromResources(kieBaseTestConfiguration, resource);
        final KieSession ksession = kbase.newKieSession();
        final List<Command<?>> commands = new ArrayList<>();
        final List<UnicodeTest.?> ?? = new ArrayList<>();
        commands.add(kieServices.getCommands().newSetGlobal("??", ??));
        // let's create person yokozuna
        final UnicodeTest.? ?? = new UnicodeTest.?();
        ??.set?(30);
        ??.set???("??");
        ??.set??(true);
        commands.add(kieServices.getCommands().newInsert(??));
        commands.add(kieServices.getCommands().newFireAllRules("firedRulesCount"));
        final ExecutionResults results = ksession.execute(kieServices.getCommands().newBatchExecution(commands, null));
        Assertions.assertThat(results.getValue("firedRulesCount")).isEqualTo(2);
        Assertions.assertThat(??.size()).isEqualTo(1);
        Assertions.assertThat(??.iterator().next().get???()).isEqualTo("??");
    }

    @Test
    public void testCzech() {
        final KieServices kieServices = Factory.get();
        final Resource resource = kieServices.getResources().newClassPathResource("unicode.drl", getClass());
        final KieBase kbase = KieBaseUtil.getKieBaseFromResources(kieBaseTestConfiguration, resource);
        final KieSession ksession = kbase.newKieSession();
        final List<Command<?>> commands = new ArrayList<>();
        final List<UnicodeTest.?lov?k> lid? = new ArrayList<>();
        commands.add(kieServices.getCommands().newSetGlobal("lid?", lid?));
        final UnicodeTest.?lov?k ?eho? = new UnicodeTest.?lov?k();
        ?eho?.setV?k(30);
        ?eho?.setJm?no("?eho?");
        commands.add(kieServices.getCommands().newInsert(?eho?));
        commands.add(kieServices.getCommands().newFireAllRules());
        ksession.execute(kieServices.getCommands().newBatchExecution(commands, null));
        Assertions.assertThat(kbase.getRule(TestConstants.PACKAGE_FUNCTIONAL, "p??li? ?lu?ou?k? k?? ?p?l ??belsk? ?dy")).isNotNull();
        final Map<String, Object> metaData = kbase.getRule(TestConstants.PACKAGE_FUNCTIONAL, "p??li? ?lu?ou?k? k?? ?p?l ??belsk? ?dy").getMetaData();
        Assertions.assertThat(metaData.get("Pr?vn?Po?adavek")).isEqualTo("Osoba star?? osmn?cti let");
        Assertions.assertThat(lid?.size()).isEqualTo(2);
        Assertions.assertThat(lid?.get(0).getJm?no()).isEqualTo("?eho?");
        Assertions.assertThat(lid?.get(1).getJm?no()).isEqualTo("Old?i?ka");
    }

    @Test
    public void testCzechDomainSpecificLanguage() {
        final KieServices kieServices = Factory.get();
        final Resource dsl = kieServices.getResources().newClassPathResource("unicode.dsl", getClass());
        final Resource dslr = kieServices.getResources().newClassPathResource("unicode.dslr", getClass());
        final KieBase kbase = KieBaseUtil.getKieBaseFromResources(kieBaseTestConfiguration, dsl, dslr);
        final KieSession ksession = kbase.newKieSession();
        final List<Command<?>> commands = new ArrayList<>();
        final List<UnicodeTest.?lov?k> dosp?l? = new ArrayList<>();
        commands.add(kieServices.getCommands().newSetGlobal("dosp?l?", dosp?l?));
        final UnicodeTest.?lov?k ?eho? = new UnicodeTest.?lov?k();
        ?eho?.setV?k(30);
        ?eho?.setJm?no("?eho?");
        commands.add(kieServices.getCommands().newInsert(?eho?));
        commands.add(kieServices.getCommands().newFireAllRules());
        ksession.execute(kieServices.getCommands().newBatchExecution(commands, null));
        Assertions.assertThat(kbase.getRule(TestConstants.PACKAGE_FUNCTIONAL, "pokusn? dom?nov? specifick? pravidlo")).isNotNull();
        Assertions.assertThat(dosp?l?.size()).isEqualTo(1);
        Assertions.assertThat(dosp?l?.iterator().next().getJm?no()).isEqualTo("?eho?");
    }

    @Test
    public void testCzechXLSDecisionTable() throws FileNotFoundException {
        final KieServices kieServices = Factory.get();
        final Resource resource = kieServices.getResources().newClassPathResource("unicode.xls", getClass());
        final KieBase kbase = KieBaseUtil.getKieBaseFromResources(kieBaseTestConfiguration, resource);
        final KieSession ksession = kbase.newKieSession();
        final List<Command<?>> commands = new ArrayList<>();
        final List<UnicodeTest.?lov?k> dosp?l? = new ArrayList<>();
        commands.add(kieServices.getCommands().newSetGlobal("dosp?l?", dosp?l?));
        final UnicodeTest.?lov?k ?eho? = new UnicodeTest.?lov?k();
        ?eho?.setV?k(30);
        ?eho?.setJm?no("?eho?");
        commands.add(kieServices.getCommands().newInsert(?eho?));
        commands.add(kieServices.getCommands().newFireAllRules());
        ksession.execute(kieServices.getCommands().newBatchExecution(commands, null));
        Assertions.assertThat(kbase.getRule(TestConstants.PACKAGE_FUNCTIONAL, "p?idej k dosp?l?m")).isNotNull();
        Assertions.assertThat(dosp?l?.size()).isEqualTo(1);
        Assertions.assertThat(dosp?l?.iterator().next().getJm?no()).isEqualTo("?eho?");
    }

    @Test
    public void testCzechCSVDecisionTable() throws FileNotFoundException {
        final KieServices kieServices = Factory.get();
        final Resource decisionTable = ResourceUtil.getDecisionTableResourceFromClasspath("unicode.csv", getClass(), CSV);
        final KieBase kbase = KieBaseUtil.getKieBaseFromResources(kieBaseTestConfiguration, decisionTable);
        final KieSession ksession = kbase.newKieSession();
        final List<Command<?>> commands = new ArrayList<>();
        final List<UnicodeTest.?lov?k> dosp?l? = new ArrayList<>();
        commands.add(kieServices.getCommands().newSetGlobal("dosp?l?", dosp?l?));
        final UnicodeTest.?lov?k ?eho? = new UnicodeTest.?lov?k();
        ?eho?.setV?k(30);
        ?eho?.setJm?no("?eho?");
        commands.add(kieServices.getCommands().newInsert(?eho?));
        commands.add(kieServices.getCommands().newFireAllRules());
        ksession.execute(kieServices.getCommands().newBatchExecution(commands, null));
        Assertions.assertThat(kbase.getRule(TestConstants.PACKAGE_FUNCTIONAL, "pokusn? pravidlo rozhodovac? tabulky")).isNotNull();
        Assertions.assertThat(dosp?l?.size()).isEqualTo(1);
        Assertions.assertThat(dosp?l?.iterator().next().getJm?no()).isEqualTo("?eho?");
    }

    // test queries in Czech language
    @Test
    public void testQueryCallFromJava() throws IllegalAccessException, InstantiationException {
        final KieServices kieServices = Factory.get();
        final Resource resource = kieServices.getResources().newClassPathResource("unicode.drl", getClass());
        final KieBase kbase = KieBaseUtil.getKieBaseFromResources(kieBaseTestConfiguration, resource);
        final KieSession ksession = kbase.newKieSession();
        final FactType locationType = kbase.getFactType(TestConstants.PACKAGE_FUNCTIONAL, "Um?st?n?");
        // a pear is in the kitchen
        final Object hru?ka = locationType.newInstance();
        locationType.set(hru?ka, "v?c", "hru?ka");
        locationType.set(hru?ka, "m?sto", "kuchyn?");
        // a desk is in the office
        final Object st?l = locationType.newInstance();
        locationType.set(st?l, "v?c", "st?l");
        locationType.set(st?l, "m?sto", "kancel??");
        // a flashlight is on the desk
        final Object sv?tilna = locationType.newInstance();
        locationType.set(sv?tilna, "v?c", "sv?tilna");
        locationType.set(sv?tilna, "m?sto", "st?l");
        // an envelope is on the desk
        final Object ob?lka = locationType.newInstance();
        locationType.set(ob?lka, "v?c", "ob?lka");
        locationType.set(ob?lka, "m?sto", "st?l");
        // a key is in the envelope
        final Object kl?? = locationType.newInstance();
        locationType.set(kl??, "v?c", "kl??");
        locationType.set(kl??, "m?sto", "ob?lka");
        // create working memory objects
        final List<Command<?>> commands = new ArrayList<>();
        // Location instances
        commands.add(kieServices.getCommands().newInsert(hru?ka));
        commands.add(kieServices.getCommands().newInsert(st?l));
        commands.add(kieServices.getCommands().newInsert(sv?tilna));
        commands.add(kieServices.getCommands().newInsert(ob?lka));
        commands.add(kieServices.getCommands().newInsert(kl??));
        // fire all rules
        final String queryAlias = "obsa?eno";
        commands.add(kieServices.getCommands().newQuery(queryAlias, "jeObsa?en", new Object[]{ Variable.v, "kancel??" }));
        final ExecutionResults results = ksession.execute(kieServices.getCommands().newBatchExecution(commands, null));
        final QueryResults qResults = ((QueryResults) (results.getValue(queryAlias)));
        final List<String> l = new ArrayList<>();
        for (final QueryResultsRow r : qResults) {
            l.add(((String) (r.get("x"))));
        }
        // items in the office should be the following
        Assertions.assertThat(l.size()).isEqualTo(4);
        Assertions.assertThat(l.contains("st?l")).isTrue();
        Assertions.assertThat(l.contains("sv?tilna")).isTrue();
        Assertions.assertThat(l.contains("ob?lka")).isTrue();
        Assertions.assertThat(l.contains("kl??")).isTrue();
    }

    // japanese person
    public static class ? {
        // age
        private int ?;

        // name
        private String ???;

        // married
        private boolean ??;

        public void set?(final int ?) {
            this.? = ?;
        }

        public int get?() {
            return ?;
        }

        public void set???(final String ???) {
            this.??? = ???;
        }

        public String get???() {
            return ???;
        }

        public boolean is??() {
            return ??;
        }

        public void set??(final boolean ??) {
            this.?? = ??;
        }
    }

    // czech person
    public static class ?lov?k {
        private int v?k;

        private String jm?no;

        public void setV?k(final int v?k) {
            this.v?k = v?k;
        }

        public int getV?k() {
            return v?k;
        }

        public void setJm?no(final String jm?no) {
            this.jm?no = jm?no;
        }

        public String getJm?no() {
            return jm?no;
        }
    }

    @Test
    public void testMutibyteJavaDialect() {
        // DROOLS-1200
        final String drl = "rule R dialect \"java\" when\n" + ((("  \uff24: String( )\n" + "then\n") + "  System.out.println( \uff24.toString() );\n") + "end\n");
        final KieSession kieSession = new KieHelper().addContent(drl, DRL).build().newKieSession();
        kieSession.insert("Hello");
        final int fired = kieSession.fireAllRules();
        Assertions.assertThat(fired).isEqualTo(1);
    }
}

