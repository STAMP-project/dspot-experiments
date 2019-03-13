/**
 * Copyright 2005 JBoss Inc
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
package org.drools.modelcompiler;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.drools.core.common.EventFactHandle;
import org.drools.core.time.impl.PseudoClockScheduler;
import org.drools.modelcompiler.domain.StockFact;
import org.drools.modelcompiler.domain.StockTick;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.time.SessionClock;
import org.kie.api.time.SessionPseudoClock;


public class CepTest extends BaseModelTest {
    public CepTest(BaseModelTest.RUN_TYPE testRunType) {
        super(testRunType);
    }

    @Test
    public void testAfter() throws Exception {
        String str = ((((((("import " + (StockTick.class.getCanonicalName())) + ";") + "rule R when\n") + "    $a : StockTick( company == \"DROO\" )\n") + "    $b : StockTick( company == \"ACME\", this after[5s,8s] $a )\n") + "then\n") + "  System.out.println(\"fired\");\n") + "end\n";
        KieSession ksession = getKieSession(CepTest.getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();
        ksession.insert(new StockTick("DROO"));
        clock.advanceTime(6, TimeUnit.SECONDS);
        ksession.insert(new StockTick("ACME"));
        Assert.assertEquals(1, ksession.fireAllRules());
        clock.advanceTime(4, TimeUnit.SECONDS);
        ksession.insert(new StockTick("ACME"));
        Assert.assertEquals(0, ksession.fireAllRules());
    }

    @Test
    public void testNegatedAfter() throws Exception {
        String str = ((((((("import " + (StockTick.class.getCanonicalName())) + ";") + "rule R when\n") + "    $a : StockTick( company == \"DROO\" )\n") + "    $b : StockTick( company == \"ACME\", this not after[5s,8s] $a )\n") + "then\n") + "  System.out.println(\"fired\");\n") + "end\n";
        KieSession ksession = getKieSession(CepTest.getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();
        ksession.insert(new StockTick("DROO"));
        clock.advanceTime(6, TimeUnit.SECONDS);
        ksession.insert(new StockTick("ACME"));
        Assert.assertEquals(0, ksession.fireAllRules());
        clock.advanceTime(4, TimeUnit.SECONDS);
        ksession.insert(new StockTick("ACME"));
        Assert.assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testAfterWithEntryPoints() throws Exception {
        String str = ((((((("import " + (StockTick.class.getCanonicalName())) + ";") + "rule R when\n") + "    $a : StockTick( company == \"DROO\" ) from entry-point ep1\n") + "    $b : StockTick( company == \"ACME\", this after[5s,8s] $a ) from entry-point ep2\n") + "then\n") + "  System.out.println(\"fired\");\n") + "end\n";
        KieSession ksession = getKieSession(CepTest.getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();
        ksession.getEntryPoint("ep1").insert(new StockTick("DROO"));
        clock.advanceTime(6, TimeUnit.SECONDS);
        ksession.getEntryPoint("ep1").insert(new StockTick("ACME"));
        Assert.assertEquals(0, ksession.fireAllRules());
        clock.advanceTime(1, TimeUnit.SECONDS);
        ksession.getEntryPoint("ep2").insert(new StockTick("ACME"));
        Assert.assertEquals(1, ksession.fireAllRules());
        clock.advanceTime(4, TimeUnit.SECONDS);
        ksession.getEntryPoint("ep2").insert(new StockTick("ACME"));
        Assert.assertEquals(0, ksession.fireAllRules());
    }

    @Test
    public void testSlidingWindow() throws Exception {
        String str = (((((("import " + (StockTick.class.getCanonicalName())) + ";\n") + "rule R when\n") + "    $a : StockTick( company == \"DROO\" ) over window:length( 2 )\n") + "then\n") + "  System.out.println(\"fired\");\n") + "end\n";
        KieSession ksession = getKieSession(CepTest.getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();
        clock.advanceTime(1, TimeUnit.SECONDS);
        ksession.insert(new StockTick("DROO"));
        clock.advanceTime(1, TimeUnit.SECONDS);
        ksession.insert(new StockTick("DROO"));
        clock.advanceTime(1, TimeUnit.SECONDS);
        ksession.insert(new StockTick("ACME"));
        clock.advanceTime(1, TimeUnit.SECONDS);
        ksession.insert(new StockTick("DROO"));
        Assert.assertEquals(2, ksession.fireAllRules());
    }

    @Test
    public void testNotAfter() throws Exception {
        String str = ((((((("import " + (StockTick.class.getCanonicalName())) + ";") + "rule R when\n") + "    $a : StockTick( company == \"DROO\" )\n") + "    not( StockTick( company == \"ACME\", this after[5s,8s] $a ) )\n") + "then\n") + "  System.out.println(\"fired\");\n") + "end\n";
        KieSession ksession = getKieSession(CepTest.getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();
        ksession.insert(new StockTick("DROO"));
        clock.advanceTime(6, TimeUnit.SECONDS);
        ksession.insert(new StockTick("ACME"));
        clock.advanceTime(10, TimeUnit.SECONDS);
        Assert.assertEquals(0, ksession.fireAllRules());
        ksession.insert(new StockTick("DROO"));
        clock.advanceTime(3, TimeUnit.SECONDS);
        ksession.insert(new StockTick("ACME"));
        clock.advanceTime(10, TimeUnit.SECONDS);
        Assert.assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testDeclaredSlidingWindow() throws Exception {
        String str = ((((((((("import " + (StockTick.class.getCanonicalName())) + ";\n") + "declare window DeclaredWindow\n") + "    StockTick( company == \"DROO\" ) over window:time( 5s )\n") + "end\n") + "rule R when\n") + "    $a : StockTick() from window DeclaredWindow\n") + "then\n") + "  System.out.println($a.getCompany());\n") + "end\n";
        KieSession ksession = getKieSession(CepTest.getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();
        clock.advanceTime(2, TimeUnit.SECONDS);
        ksession.insert(new StockTick("DROO"));
        clock.advanceTime(2, TimeUnit.SECONDS);
        ksession.insert(new StockTick("DROO"));
        clock.advanceTime(2, TimeUnit.SECONDS);
        ksession.insert(new StockTick("ACME"));
        clock.advanceTime(2, TimeUnit.SECONDS);
        ksession.insert(new StockTick("DROO"));
        Assert.assertEquals(2, ksession.fireAllRules());
    }

    @Test
    public void testDeclaredSlidingWindowWithEntryPoint() throws Exception {
        String str = ((((((((("import " + (StockTick.class.getCanonicalName())) + ";\n") + "declare window DeclaredWindow\n") + "    StockTick( company == \"DROO\" ) over window:time( 5s ) from entry-point ticks\n") + "end\n") + "rule R when\n") + "    $a : StockTick() from window DeclaredWindow\n") + "then\n") + "  System.out.println($a.getCompany());\n") + "end\n";
        KieSession ksession = getKieSession(CepTest.getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();
        EntryPoint ep = ksession.getEntryPoint("ticks");
        clock.advanceTime(2, TimeUnit.SECONDS);
        ep.insert(new StockTick("DROO"));
        clock.advanceTime(2, TimeUnit.SECONDS);
        ep.insert(new StockTick("DROO"));
        clock.advanceTime(2, TimeUnit.SECONDS);
        ep.insert(new StockTick("ACME"));
        clock.advanceTime(2, TimeUnit.SECONDS);
        ep.insert(new StockTick("DROO"));
        Assert.assertEquals(2, ksession.fireAllRules());
    }

    @Test
    public void testDeclaredSlidingWindowOnEventInTypeDeclaration() throws Exception {
        String str = "declare String\n" + ((((((((("  @role( event )\n" + "end\n") + "declare window DeclaredWindow\n") + "    String( ) over window:time( 5s )\n") + "end\n") + "rule R when\n") + "    $a : String( this == \"DROO\" ) from window DeclaredWindow\n") + "then\n") + "  System.out.println($a);\n") + "end\n");
        KieSession ksession = getKieSession(CepTest.getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();
        ksession.insert("ACME");
        ksession.insert("DROO");
        Assert.assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testDeclaredSlidingWindowOnDeclaredType() throws Exception {
        String str = "declare MyEvent\n" + ((((((((((((("  @role( event )\n" + "end\n") + "declare window DeclaredWindow\n") + "    MyEvent( ) over window:time( 5s )\n") + "end\n") + "rule Init when\n") + "then\n") + "  insert(new MyEvent());\n") + "end\n") + "rule R when\n") + "    $a : MyEvent() from window DeclaredWindow\n") + "then\n") + "  System.out.println($a);\n") + "end\n");
        KieSession ksession = getKieSession(CepTest.getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();
        Assert.assertEquals(2, ksession.fireAllRules());
    }

    @Test
    public void testDeclaredSlidingWindowWith2Arguments() throws Exception {
        String str = "declare String\n" + ((((((((("  @role( event )\n" + "end\n") + "declare window DeclaredWindow\n") + "    String( length == 4, this.startsWith(\"D\") ) over window:time( 5s )\n") + "end\n") + "rule R when\n") + "    $a : String() from window DeclaredWindow\n") + "then\n") + "  System.out.println($a);\n") + "end\n");
        KieSession ksession = getKieSession(CepTest.getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();
        ksession.insert("ACME");
        ksession.insert("DROO");
        Assert.assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testWithDeclaredEvent() throws Exception {
        String str = (((((((("import " + (StockFact.class.getCanonicalName())) + ";\n") + "declare StockFact @role( event ) end;\n") + "rule R when\n") + "    $a : StockFact( company == \"DROO\" )\n") + "    $b : StockFact( company == \"ACME\", this after[5s,8s] $a )\n") + "then\n") + "  System.out.println(\"fired\");\n") + "end\n";
        KieSession ksession = getKieSession(CepTest.getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();
        ksession.insert(new StockFact("DROO"));
        clock.advanceTime(6, TimeUnit.SECONDS);
        ksession.insert(new StockFact("ACME"));
        Assert.assertEquals(1, ksession.fireAllRules());
        clock.advanceTime(4, TimeUnit.SECONDS);
        ksession.insert(new StockFact("ACME"));
        Assert.assertEquals(0, ksession.fireAllRules());
    }

    @Test
    public void testExpireEventOnEndTimestamp() throws Exception {
        String str = ((((((((((("package org.drools.compiler;\n" + "import ") + (StockTick.class.getCanonicalName())) + ";\n") + "global java.util.List resultsAfter;\n") + "\n") + "rule \"after[60,80]\"\n") + "when\n") + "$a : StockTick( company == \"DROO\" )\n") + "$b : StockTick( company == \"ACME\", this after[60,80] $a )\n") + "then\n") + "       resultsAfter.add( $b );\n") + "end";
        KieSession ksession = getKieSession(CepTest.getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();
        List<StockTick> resultsAfter = new ArrayList<StockTick>();
        ksession.setGlobal("resultsAfter", resultsAfter);
        // inserting new StockTick with duration 30 at time 0 => rule
        // after[60,80] should fire when ACME lasts at 100-120
        ksession.insert(new StockTick("DROO", 30));
        clock.advanceTime(100, TimeUnit.MILLISECONDS);
        ksession.insert(new StockTick("ACME", 20));
        ksession.fireAllRules();
        Assert.assertEquals(1, resultsAfter.size());
    }

    @Test
    public void testExpireEventOnEndTimestampWithDeclaredEvent() throws Exception {
        String str = (((((((((((((((("package org.drools.compiler;\n" + "import ") + (StockFact.class.getCanonicalName())) + ";\n") + "global java.util.List resultsAfter;\n") + "\n") + "declare StockFact\n") + "    @role( event )\n") + "    @duration( duration )\n") + "end\n") + "\n") + "rule \"after[60,80]\"\n") + "when\n") + "$a : StockFact( company == \"DROO\" )\n") + "$b : StockFact( company == \"ACME\", this after[60,80] $a )\n") + "then\n") + "       resultsAfter.add( $b );\n") + "end";
        KieSession ksession = getKieSession(CepTest.getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();
        List<StockTick> resultsAfter = new ArrayList<StockTick>();
        ksession.setGlobal("resultsAfter", resultsAfter);
        // inserting new StockTick with duration 30 at time 0 => rule
        // after[60,80] should fire when ACME lasts at 100-120
        ksession.insert(new StockFact("DROO", 30));
        clock.advanceTime(100, TimeUnit.MILLISECONDS);
        ksession.insert(new StockFact("ACME", 20));
        ksession.fireAllRules();
        Assert.assertEquals(1, resultsAfter.size());
    }

    @Test
    public void testExpires() throws Exception {
        String str = ((((((((((((("package org.drools.compiler;\n" + "import ") + (StockFact.class.getCanonicalName())) + ";\n") + "\n") + "declare StockFact\n") + "    @role( value = event )\n") + "    @expires( value = 2s, policy = TIME_SOFT )\n") + "end\n") + "\n") + "rule \"expiration\"\n") + "when\n") + "   StockFact( company == \"DROO\" )\n") + "then\n") + "end";
        KieSession ksession = getKieSession(CepTest.getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();
        ksession.insert(new StockFact("DROO"));
        clock.advanceTime(1, TimeUnit.SECONDS);
        ksession.fireAllRules();
        Assert.assertEquals(1, ksession.getObjects().size());
        clock.advanceTime(2, TimeUnit.SECONDS);
        ksession.fireAllRules();
        Assert.assertEquals(0, ksession.getObjects().size());
    }

    @Test
    public void testDeclareAndExpires() throws Exception {
        String str = "package org.drools.compiler;\n" + ((((((((((("declare StockFact\n" + "    @role( value = event )\n") + "    @expires( value = 2s, policy = TIME_SOFT )\n") + "    company : String\n") + "    duration : long\n") + "end\n") + "\n") + "rule \"expiration\"\n") + "when\n") + "   StockFact( company == \"DROO\" )\n") + "then\n") + "end");
        KieSession ksession = getKieSession(CepTest.getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();
        FactType stockFactType = ksession.getKieBase().getFactType("org.drools.compiler", "StockFact");
        Object DROO = stockFactType.newInstance();
        stockFactType.set(DROO, "company", "DROO");
        ksession.insert(DROO);
        clock.advanceTime(1, TimeUnit.SECONDS);
        ksession.fireAllRules();
        Assert.assertEquals(1, ksession.getObjects().size());
        clock.advanceTime(2, TimeUnit.SECONDS);
        ksession.fireAllRules();
        Assert.assertEquals(0, ksession.getObjects().size());
    }

    @Test
    public void testNoEvent() {
        String str = "declare BaseEvent\n" + (((((((((((((((((((((("  @role(event)\n" + "end\n") + "\n") + "declare Event extends BaseEvent\n") + "  @role(event)\n") + "  property : String\n") + "end\n") + "\n") + "declare NotEvent extends BaseEvent\n") + "  @role(event)\n") + "  property : String\n") + "end\n") + "\n") + "rule \"not equal\" when\n") + "    not (\n") + "      ( and\n") + "          $e : BaseEvent( ) over window:length(3) from entry-point entryPoint\n") + "          NotEvent( this == $e, property == \"value\" ) from entry-point entryPoint\n") + "      )\n") + "    )\n") + "then\n") + "\n") + "end");
        KieSession ksession = getKieSession(CepTest.getCepKieModuleModel(), str);
        Assert.assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testIntervalTimer() throws Exception {
        String str = "";
        str += "package org.simple \n";
        str += "global java.util.List list \n";
        str += "rule xxx \n";
        str += "  timer (int:30s 10s) ";
        str += "when \n";
        str += "then \n";
        str += "  list.add(\"fired\"); \n";
        str += "end  \n";
        KieSession ksession = getKieSession(CepTest.getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();
        List list = new ArrayList();
        PseudoClockScheduler timeService = ((PseudoClockScheduler) (ksession.<SessionClock>getSessionClock()));
        timeService.advanceTime(new Date().getTime(), TimeUnit.MILLISECONDS);
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        Assert.assertEquals(0, list.size());
        timeService.advanceTime(20, TimeUnit.SECONDS);
        ksession.fireAllRules();
        Assert.assertEquals(0, list.size());
        timeService.advanceTime(15, TimeUnit.SECONDS);
        ksession.fireAllRules();
        Assert.assertEquals(1, list.size());
        timeService.advanceTime(3, TimeUnit.SECONDS);
        ksession.fireAllRules();
        Assert.assertEquals(1, list.size());
        timeService.advanceTime(2, TimeUnit.SECONDS);
        ksession.fireAllRules();
        Assert.assertEquals(2, list.size());
        timeService.advanceTime(10, TimeUnit.SECONDS);
        ksession.fireAllRules();
        Assert.assertEquals(3, list.size());
    }

    @Test
    public void testAfterWithAnd() throws Exception {
        String str = ((((((("import " + (StockTick.class.getCanonicalName())) + ";") + "rule R when\n") + "    $a : StockTick( company == \"DROO\" )\n") + "    $b : StockTick( company == \"ACME\" && this after[5s,8s] $a )\n") + "then\n") + "  System.out.println(\"fired\");\n") + "end\n";
        KieSession ksession = getKieSession(CepTest.getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();
        ksession.insert(new StockTick("DROO"));
        clock.advanceTime(6, TimeUnit.SECONDS);
        ksession.insert(new StockTick("ACME"));
        Assert.assertEquals(1, ksession.fireAllRules());
        clock.advanceTime(4, TimeUnit.SECONDS);
        ksession.insert(new StockTick("ACME"));
        Assert.assertEquals(0, ksession.fireAllRules());
    }

    @Test
    public void testAfterOnLongFields() throws Exception {
        String str = ((((((("import " + (StockTick.class.getCanonicalName())) + ";") + "rule R when\n") + "    $a : StockTick( company == \"DROO\" )\n") + "    $b : StockTick( company == \"ACME\", timeFieldAsLong after[5,8] $a.timeFieldAsLong )\n") + "then\n") + "  System.out.println(\"fired\");\n") + "end\n";
        KieSession ksession = getKieSession(CepTest.getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();
        ksession.insert(new StockTick("DROO").setTimeField(0));
        ksession.insert(new StockTick("ACME").setTimeField(6));
        Assert.assertEquals(1, ksession.fireAllRules());
        ksession.insert(new StockTick("ACME").setTimeField(10));
        Assert.assertEquals(0, ksession.fireAllRules());
    }

    @Test
    public void testAfterOnDateFields() throws Exception {
        String str = ((((((("import " + (StockTick.class.getCanonicalName())) + ";") + "rule R when\n") + "    $a : StockTick( company == \"DROO\" )\n") + "    $b : StockTick( company == \"ACME\", timeFieldAsDate after[5,8] $a.timeFieldAsDate )\n") + "then\n") + "  System.out.println(\"fired\");\n") + "end\n";
        KieSession ksession = getKieSession(CepTest.getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();
        ksession.insert(new StockTick("DROO").setTimeField(0));
        ksession.insert(new StockTick("ACME").setTimeField(6));
        Assert.assertEquals(1, ksession.fireAllRules());
        ksession.insert(new StockTick("ACME").setTimeField(10));
        Assert.assertEquals(0, ksession.fireAllRules());
    }

    @Test
    public void testAfterOnDateFieldsWithBinding() throws Exception {
        String str = ((((((("import " + (StockTick.class.getCanonicalName())) + ";") + "rule R when\n") + "    $a : StockTick( company == \"DROO\", $aTime : timeFieldAsDate )\n") + "    $b : StockTick( company == \"ACME\", timeFieldAsDate after[5,8] $aTime )\n") + "then\n") + "  System.out.println(\"fired\");\n") + "end\n";
        KieSession ksession = getKieSession(CepTest.getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();
        clock.advanceTime(100, TimeUnit.MILLISECONDS);
        ksession.insert(new StockTick("DROO").setTimeField(0));
        ksession.insert(new StockTick("ACME").setTimeField(6));
        Assert.assertEquals(1, ksession.fireAllRules());
        ksession.insert(new StockTick("ACME").setTimeField(10));
        Assert.assertEquals(0, ksession.fireAllRules());
    }

    @Test
    public void testAfterOnFactAndField() throws Exception {
        String str = ((((((("import " + (StockTick.class.getCanonicalName())) + ";") + "rule R when\n") + "    $a : StockTick( company == \"DROO\" )\n") + "    $b : StockTick( company == \"ACME\", timeFieldAsLong after[5,8] $a )\n") + "then\n") + "  System.out.println(\"fired\");\n") + "end\n";
        KieSession ksession = getKieSession(CepTest.getCepKieModuleModel(), str);
        SessionPseudoClock clock = ksession.getSessionClock();
        ksession.insert(new StockTick("DROO").setTimeField(0));
        clock.advanceTime(6, TimeUnit.MILLISECONDS);
        ksession.insert(new StockTick("ACME").setTimeField(6));
        Assert.assertEquals(1, ksession.fireAllRules());
        clock.advanceTime(4, TimeUnit.MILLISECONDS);
        ksession.insert(new StockTick("ACME").setTimeField(10));
        Assert.assertEquals(0, ksession.fireAllRules());
    }

    @Test
    public void testComplexTimestamp() {
        final String str = (((((((("import " + (CepTest.Message.class.getCanonicalName())) + "\n") + "declare ") + (CepTest.Message.class.getCanonicalName())) + "\n") + "   @role( event ) \n") + "   @timestamp( getProperties().get( \'timestamp\' ) - 1 ) \n") + "   @duration( getProperties().get( \'duration\' ) + 1 ) \n") + "end\n";
        KieSession ksession = getKieSession(CepTest.getCepKieModuleModel(), str);
        try {
            final CepTest.Message msg = new CepTest.Message();
            final Properties props = new Properties();
            props.put("timestamp", 99);
            props.put("duration", 52);
            msg.setProperties(props);
            final EventFactHandle efh = ((EventFactHandle) (ksession.insert(msg)));
            Assert.assertEquals(98, efh.getStartTimestamp());
            Assert.assertEquals(53, efh.getDuration());
        } finally {
            ksession.dispose();
        }
    }

    public static class Message {
        private Properties properties;

        private Timestamp timestamp;

        private Long duration;

        public Properties getProperties() {
            return properties;
        }

        public void setProperties(final Properties properties) {
            this.properties = properties;
        }

        public Timestamp getStartTime() {
            return timestamp;
        }

        public void setStartTime(final Timestamp timestamp) {
            this.timestamp = timestamp;
        }

        public Long getDuration() {
            return duration;
        }

        public void setDuration(final Long duration) {
            this.duration = duration;
        }
    }

    @Test
    public void testTimerWithMillisPrecision() {
        final String drl = ((((((((((((((((((((("import " + (CepTest.MyEvent.class.getCanonicalName())) + "\n") + "import ") + (AtomicInteger.class.getCanonicalName())) + "\n") + "declare MyEvent\n") + "    @role( event )\n") + "    @timestamp( timestamp )\n") + "    @expires( 10ms )\n") + "end\n") + "\n") + "rule R\n") + "    timer (int: 0 1; start=$startTime, repeat-limit=0 )\n") + "    when\n") + "       $event: MyEvent ($startTime : timestamp)\n") + "       $counter : AtomicInteger(get() > 0)\n") + "    then\n") + "        System.out.println(\"RG_TEST_TIMER WITH \" + $event + \" AND \" + $counter);\n") + "        modify($counter){\n") + "            decrementAndGet()\n") + "        }\n") + "end";
        KieSession ksession = getKieSession(CepTest.getCepKieModuleModel(), drl);
        try {
            final long now = 1000;
            final PseudoClockScheduler sessionClock = ksession.getSessionClock();
            sessionClock.setStartupTime((now - 10));
            final AtomicInteger counter = new AtomicInteger(1);
            final CepTest.MyEvent event1 = new CepTest.MyEvent((now - 8));
            final CepTest.MyEvent event2 = new CepTest.MyEvent((now - 7));
            final CepTest.MyEvent event3 = new CepTest.MyEvent((now - 6));
            ksession.insert(counter);
            ksession.insert(event1);
            ksession.insert(event2);
            ksession.insert(event3);
            ksession.fireAllRules();// Nothing Happens

            Assert.assertEquals(1, counter.get());
            sessionClock.advanceTime(10, TimeUnit.MILLISECONDS);
            ksession.fireAllRules();
            Assert.assertEquals(0, counter.get());
        } finally {
            ksession.dispose();
        }
    }

    public static class MyEvent {
        private long timestamp;

        public MyEvent(final long timestamp) {
            this.timestamp = timestamp;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(final long timestamp) {
            this.timestamp = timestamp;
        }

        public String toString() {
            return (("MyEvent{" + "timestamp=") + (timestamp)) + '}';
        }
    }
}

