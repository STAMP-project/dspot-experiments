/**
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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
package org.drools.compiler.lang.api;


import AttributeDescr.Type.BOOLEAN;
import GroupElement.Type;
import ResourceType.DRL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.StockTick;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.lang.DrlDumper;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.io.impl.ByteArrayResource;
import org.drools.core.rule.GroupElement;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.RuleConditionElement;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.definition.type.FactType;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


/**
 * DescrBuilderTest
 */
public class DescrBuilderTest extends CommonTestMethodBase {
    @Test
    public void testPackage() {
        PackageDescr pkg = DescrFactory.newPackage().name("org.drools").attribute("dialect").value("mvel").end().getDescr();
        Assert.assertEquals("org.drools", pkg.getName());
        Assert.assertEquals("mvel", pkg.getAttribute("dialect").getValue());
        Assert.assertNull(pkg.getAttribute("salience"));
        KiePackage kpkg = compilePkgDescr(pkg);
        Assert.assertEquals("org.drools", kpkg.getName());
    }

    @Test
    public void testPackageAttributes() {
        PackageDescr pkg = // third syntax
        // second syntax
        // first syntax
        DescrFactory.newPackage().name("org.drools").attribute("dialect").value("mvel").end().attribute("salience", "10").attribute("lock-on-active", "true", BOOLEAN).getDescr();
        Assert.assertEquals("org.drools", pkg.getName());
        Assert.assertEquals(3, pkg.getAttributes().size());
        Assert.assertEquals("mvel", pkg.getAttribute("dialect").getValue());
        Assert.assertEquals("10", pkg.getAttribute("salience").getValue());
        Assert.assertEquals("true", pkg.getAttribute("lock-on-active").getValue());
        Assert.assertEquals(BOOLEAN, getType());
        Assert.assertNull(pkg.getAttribute("no-loop"));
        KiePackage kpkg = compilePkgDescr(pkg);
        Assert.assertEquals("org.drools", kpkg.getName());
    }

    @Test
    public void testPackageImports() {
        PackageDescr pkg = DescrFactory.newPackage().name("org.drools").newImport().target("java.util.List").end().newImport().target("org.drools.examples.*").end().getDescr();
        Assert.assertEquals(2, pkg.getImports().size());
        Assert.assertEquals("java.util.List", pkg.getImports().get(0).getTarget());
        Assert.assertEquals("org.drools.examples.*", pkg.getImports().get(1).getTarget());
    }

    @Test
    public void testGlobals() {
        PackageDescr pkg = DescrFactory.newPackage().name("org.drools.compiler").newGlobal().type("java.util.List").identifier("list").end().newGlobal().type("Person").identifier("bob").end().getDescr();
        Assert.assertEquals(2, pkg.getGlobals().size());
        Assert.assertEquals("java.util.List", getType());
        Assert.assertEquals("list", pkg.getGlobals().get(0).getIdentifier());
        Assert.assertEquals("Person", getType());
        Assert.assertEquals("bob", pkg.getGlobals().get(1).getIdentifier());
        KiePackage kpkg = compilePkgDescr(pkg);
        Assert.assertEquals("org.drools.compiler", kpkg.getName());
    }

    @Test
    public void testFunctions() {
        PackageDescr pkg = // rule
        // functions
        DescrFactory.newPackage().name("org.drools").newFunctionImport().target("java.lang.Math.max").end().newFunction().returnType("long").name("myMax").parameter("long", "v1").parameter("long", "v2").body("return max(v1, v2);").end().newRule().name("test").lhs().eval().constraint("myMax(5, 10) == 10").end().end().rhs("// do something").end().getDescr();
        Assert.assertEquals(1, pkg.getFunctionImports().size());
        Assert.assertEquals(1, pkg.getFunctions().size());
        Assert.assertEquals(1, pkg.getRules().size());
        KiePackage kpkg = compilePkgDescr(pkg);
        Assert.assertEquals("org.drools", kpkg.getName());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(Collections.singletonList(kpkg));
        KieSession ksession = createKnowledgeSession(kbase);
        int rules = ksession.fireAllRules();
        Assert.assertEquals(1, rules);
    }

    @Test
    public void testNamedConsequence() {
        PackageDescr pkg = DescrFactory.newPackage().name("org.drools.compiler").newRule().name("test").lhs().pattern("Cheese").constraint("type == \"stilton\"").end().namedConsequence().name("c1").end().pattern("Cheese").constraint("type == \"cheddar\"").end().end().rhs("// do something").namedRhs("c1", "// do something else").end().getDescr();
        Assert.assertEquals(1, pkg.getRules().size());
        KiePackage kpkg = compilePkgDescr(pkg);
        Assert.assertEquals("org.drools.compiler", kpkg.getName());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(Collections.singletonList(kpkg));
        KieSession ksession = createKnowledgeSession(kbase);
        Cheese stilton = new Cheese("stilton", 5);
        Cheese cheddar = new Cheese("cheddar", 7);
        Cheese brie = new Cheese("brie", 5);
        ksession.insert(stilton);
        ksession.insert(cheddar);
        ksession.insert(brie);
        int rules = ksession.fireAllRules();
        Assert.assertEquals(2, rules);
    }

    @Test
    public void testConditionalBranch() {
        String expected = "packageorg.drools.compiler\n" + ((((((((("rule \"test\"\n" + "when\n") + "    Cheese( type == \"stilton\" )  \n") + "    if ( price < 10 ) do[c1] \n") + "    Cheese( type == \"cheddar\" )  \n") + "then\n") + "// do something\n") + "then[c1]\n") + "// do something else\n") + "end");
        PackageDescr pkg = DescrFactory.newPackage().name("org.drools.compiler").newRule().name("test").lhs().pattern("Cheese").constraint("type == \"stilton\"").end().conditionalBranch().condition().constraint("price < 10").end().consequence().name("c1").end().end().pattern("Cheese").constraint("type == \"cheddar\"").end().end().rhs("// do something").namedRhs("c1", "// do something else").end().getDescr();
        Assert.assertEquals(1, pkg.getRules().size());
        String drl = new DrlDumper().dump(pkg);
        Assertions.assertThat(expected).isEqualToIgnoringWhitespace(drl);
        KiePackage kpkg = compilePkgDescr(pkg);
        Assert.assertEquals("org.drools.compiler", kpkg.getName());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(Collections.singletonList(kpkg));
        KieSession ksession = createKnowledgeSession(kbase);
        Cheese stilton = new Cheese("stilton", 5);
        Cheese cheddar = new Cheese("cheddar", 7);
        Cheese brie = new Cheese("brie", 5);
        ksession.insert(stilton);
        ksession.insert(cheddar);
        ksession.insert(brie);
        int rules = ksession.fireAllRules();
        Assert.assertEquals(2, rules);
    }

    @Test
    public void testDeclare() throws IllegalAccessException, InstantiationException {
        PackageDescr pkg = // declare
        DescrFactory.newPackage().name("org.beans").newDeclare().type().name("StockTick").newAnnotation("role").value("event").end().newAnnotation("author").value("bob").end().newField("symbol").type("String").end().newField("price").type("double").end().end().newDeclare().enumerative().name("Planets").newAnnotation("kind").value("enum").end().newEnumLiteral("earth").constructorArg("6.0").constructorArg("10.0").end().newEnumLiteral("jupiter").constructorArg("44.0").constructorArg("50.0").end().newField("mass").type("double").end().newField("radius").type("double").end().end().getDescr();
        Assert.assertEquals(1, pkg.getTypeDeclarations().size());
        Assert.assertEquals(1, pkg.getEnumDeclarations().size());
        KiePackage kpkg = compilePkgDescr(pkg);
        Assert.assertEquals("org.beans", kpkg.getName());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(Collections.singletonList(kpkg));
        FactType stType = kbase.getFactType("org.beans", "StockTick");
        Assert.assertNotNull(stType);
        Object st = stType.newInstance();
        stType.set(st, "symbol", "RHT");
        stType.set(st, "price", 10);
        Assert.assertEquals("RHT", stType.get(st, "symbol"));
        // stType.getAnnotation("author"); TODO: implement support for this
    }

    @Test
    public void testDeclareEntryPoint() throws IllegalAccessException, InstantiationException {
        PackageDescr pkg = // declare
        DescrFactory.newPackage().name("org.drools").newDeclare().entryPoint().entryPointId("ep1").end().newDeclare().entryPoint().entryPointId("ep3").end().getDescr();
        Assert.assertEquals(2, pkg.getEntryPointDeclarations().size());
        KiePackage kpkg = compilePkgDescr(pkg);
        Assert.assertEquals("org.drools", kpkg.getName());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(Collections.singletonList(kpkg));
        Assert.assertEquals(2, kbase.getEntryPointIds().size());
    }

    @Test
    public void testRuleRHSOptional() throws IllegalAccessException, InstantiationException {
        PackageDescr pkg = DescrFactory.newPackage().name("org.drools.compiler").newRule().name("r1").lhs().pattern("StockTick").constraint("company == \"RHT\"").end().end().end().getDescr();
        KiePackage kpkg = compilePkgDescr(pkg);
        Assert.assertEquals("org.drools.compiler", kpkg.getName());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(Collections.singletonList(kpkg));
        KieSession ksession = kbase.newKieSession();
        ksession.insert(new StockTick(1, "RHT", 80, 1));
        int rules = ksession.fireAllRules();
        Assert.assertEquals(1, rules);
    }

    @Test
    public void testRuleRHSComment() throws IllegalAccessException, InstantiationException {
        PackageDescr pkg = DescrFactory.newPackage().name("org.drools.compiler").newRule().name("r1").lhs().pattern("StockTick").constraint("company == \"RHT\"").end().end().rhs("// some comment").end().getDescr();
        KiePackage kpkg = compilePkgDescr(pkg);
        Assert.assertEquals("org.drools.compiler", kpkg.getName());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(Collections.singletonList(kpkg));
        KieSession ksession = kbase.newKieSession();
        ksession.insert(new StockTick(1, "RHT", 80, 1));
        int rules = ksession.fireAllRules();
        Assert.assertEquals(1, rules);
    }

    @Test
    public void testTopLevelAccumulate() throws IllegalAccessException, InstantiationException {
        PackageDescr pkg = DescrFactory.newPackage().name("org.drools.compiler").newRule().name("r1").lhs().accumulate().source().pattern("StockTick").constraint("company == \"RHT\"").bind("$p", "price", false).end().end().function("sum", "$sum", false, "$p").function("count", "$cnt", false, "$p").end().end().rhs("// some comment").end().getDescr();
        KiePackage kpkg = compilePkgDescr(pkg);
        Assert.assertEquals("org.drools.compiler", kpkg.getName());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(Collections.singletonList(kpkg));
        KieSession ksession = kbase.newKieSession();
        AgendaEventListener ael = Mockito.mock(AgendaEventListener.class);
        ksession.addEventListener(ael);
        ksession.insert(new StockTick(1, "RHT", 80, 1));
        ksession.insert(new StockTick(2, "RHT", 100, 10));
        int rules = ksession.fireAllRules();
        Assert.assertEquals(1, rules);
        ArgumentCaptor<AfterMatchFiredEvent> cap = ArgumentCaptor.forClass(AfterMatchFiredEvent.class);
        Mockito.verify(ael).afterMatchFired(cap.capture());
        Assert.assertThat(((Number) (cap.getValue().getMatch().getDeclarationValue("$sum"))).intValue(), CoreMatchers.is(180));
        Assert.assertThat(((Number) (cap.getValue().getMatch().getDeclarationValue("$cnt"))).intValue(), CoreMatchers.is(2));
    }

    @Test
    public void testRule() throws IllegalAccessException, InstantiationException {
        PackageDescrBuilder packBuilder = DescrFactory.newPackage().name("org.drools.compiler").newRule().name("r1").lhs().and().or().pattern("StockTick").constraint("price > 100").end().pattern("StockTick").constraint("price < 10").end().end().pattern("StockTick").constraint("company == \"RHT\"").end().end().end().rhs("    System.out.println(\"foo\");\n").end();
        PackageDescr pkg = packBuilder.getDescr();
        String drl = new DrlDumper().dump(packBuilder.getDescr());
        System.out.println(drl);
        KiePackage kpkg = compilePkgDescr(pkg);
        Assert.assertEquals("org.drools.compiler", kpkg.getName());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(Collections.singletonList(kpkg));
        KieSession ksession = createKnowledgeSession(kbase);
        ksession.insert(new StockTick(1, "RHT", 80, 1));
        int rules = ksession.fireAllRules();
        Assert.assertEquals(0, rules);
        ksession = kbase.newKieSession();
        ksession.insert(new StockTick(2, "RHT", 150, 1));
        rules = ksession.fireAllRules();
        Assert.assertEquals(1, rules);
    }

    @Test
    public void testFromEntryPoint() throws IllegalAccessException, InstantiationException {
        PackageDescr pkg = DescrFactory.newPackage().name("org.drools").newRule().name("from rule").lhs().pattern("String").id("s", false).from().entryPoint("EventStream").end().end().rhs("//System.out.println(s);").end().getDescr();
        KiePackage kpkg = compilePkgDescr(pkg, "org.drools");
        Assert.assertEquals("org.drools", kpkg.getName());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(Collections.singletonList(kpkg));
        KieSession ksession = createKnowledgeSession(kbase);
        EntryPoint ep = ksession.getEntryPoint("EventStream");
        ep.insert("Hello World!");
        int rules = ksession.fireAllRules();
        Assert.assertEquals(1, rules);
    }

    @Test
    public void testDumperFromPkg() {
        // DROOLS-109
        PackageDescr pkg = DescrFactory.newPackage().name("org.test").newRule().name("org.test").lhs().and().or().pattern().id("$x", false).type("Integer").constraint("this > 10").end().pattern().id("$x", false).type("Integer").constraint("this < 20").end().end().pattern().type("Integer").constraint("this == $x").constraint("this == 42").end().end().end().rhs("").end().end().getDescr();
        String drl = new DrlDumper().dump(pkg);
        System.out.println(drl);
        KnowledgeBuilderImpl knowledgeBuilder = ((KnowledgeBuilderImpl) (KnowledgeBuilderFactory.newKnowledgeBuilder()));
        knowledgeBuilder.add(new ByteArrayResource(drl.getBytes()), DRL);
        System.err.println(knowledgeBuilder.getErrors());
        Assert.assertFalse(knowledgeBuilder.getErrors().toString(), knowledgeBuilder.hasErrors());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(knowledgeBuilder.getKnowledgePackages());
        KieSession knowledgeSession = kbase.newKieSession();
        KiePackage rebuiltPkg = knowledgeBuilder.getPackage("org.test");
        Rule rule = rebuiltPkg.getRules().iterator().next();
        RuleImpl r = ((RuleImpl) (rule));
        Assert.assertEquals(2, getChildren().size());
        Iterator<RuleConditionElement> iter = getChildren().iterator();
        RuleConditionElement arg1 = iter.next();
        Assert.assertTrue(((arg1 instanceof GroupElement) && ((getType()) == (Type.OR))));
        Assert.assertEquals(2, getChildren().size());
        RuleConditionElement arg2 = iter.next();
        Assert.assertTrue((arg2 instanceof Pattern));
    }

    @Test
    public void testAccumulate() throws IllegalAccessException, InstantiationException {
        PackageDescrBuilder packBuilder = DescrFactory.newPackage().newGlobal().identifier("list").type(List.class.getName()).end().name("org.drools.compiler").newRule().name("r1").lhs().pattern().id("$tot", true).type(Double.class.getName()).end().accumulate().source().pattern().id("$i", false).type(Integer.class.getName()).end().end().function("sum", "$tot", true, "$i").constraint("$tot > 15").end().end().rhs("list.add( $tot );").end().newRule().name("r2").attribute("dialect", "mvel").lhs().pattern().id("$tot", true).type(Double.class.getName()).end().accumulate().source().pattern().id("$i", false).type(Integer.class.getName()).end().end().function("sum", "$tot", true, "$i").constraint("$tot > 15").end().end().rhs("list.add( $tot * 2 );").end();
        String drl = new DrlDumper().dump(packBuilder.getDescr());
        System.out.println(drl);
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add(new ByteArrayResource(drl.getBytes()), DRL);
        System.err.println(knowledgeBuilder.getErrors());
        Assert.assertFalse(knowledgeBuilder.getErrors().toString(), knowledgeBuilder.hasErrors());
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(knowledgeBuilder.getKnowledgePackages());
        KieSession knowledgeSession = kbase.newKieSession();
        List list = new ArrayList();
        knowledgeSession.setGlobal("list", list);
        knowledgeSession.insert(3);
        knowledgeSession.insert(39);
        knowledgeSession.insert(24.0);
        knowledgeSession.insert(42.0);
        knowledgeSession.fireAllRules();
        Assert.assertEquals(Arrays.asList(42.0, 84.0), list);
    }

    @Test
    public void testDumperPositional() {
        PackageDescr pkg = DescrFactory.newPackage().name("org.test").newRule().name("org.test").lhs().pattern().type("Integer").constraint("this > 10", true).constraint("this > 11", true).constraint("this > 12", false).constraint("this > 13", false).end().end().rhs("").end().end().getDescr();
        String drl = new DrlDumper().dump(pkg);
        Assert.assertTrue(drl.contains("Integer( this > 10, this > 11; this > 12, this > 13 )"));
    }

    @Test
    public void testDumperDuration() {
        PackageDescr pkg = DescrFactory.newPackage().name("org.test").newRule().name("org.test").attribute("duration").value("int: 0 3600000; repeat-limit = 6").end().lhs().end().rhs("").end().end().getDescr();
        String drl = new DrlDumper().dump(pkg);
        Assert.assertTrue(drl.contains("duration (int: 0 3600000; repeat-limit = 6)"));
    }

    @Test
    public void testDumperTimer() {
        PackageDescr pkg = DescrFactory.newPackage().name("org.test").newRule().name("org.test").attribute("timer").value("cron:0/5 * * * * ?").end().lhs().end().rhs("").end().end().getDescr();
        String drl = new DrlDumper().dump(pkg);
        Assert.assertTrue(drl.contains("timer (cron:0/5 * * * * ?)"));
    }

    @Test
    public void testBehaviorForSlidingWindow() throws IllegalAccessException, InstantiationException {
        // DROOLS-852
        List<String> myParams = new LinkedList<String>();
        myParams.add("5s");
        PackageDescr pkg = DescrFactory.newPackage().name("org.drools").newRule().name("from rule").lhs().not().pattern().type("StockTick").constraint("price > 10").behavior().type("window", "time").parameters(myParams).end().end().end().end().rhs("//System.out.println(s);").end().getDescr();
        String drl = new DrlDumper().dump(pkg);
        Assert.assertTrue(drl.contains("window:time(5s)"));
    }
}

