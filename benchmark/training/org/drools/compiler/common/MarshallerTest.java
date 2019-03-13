package org.drools.compiler.common;


import EnvironmentName.OBJECT_MARSHALLING_STRATEGIES;
import EqualityBehaviorOption.EQUALITY;
import ProtobufMessages.KnowledgeSession;
import ResourceType.DRL;
import org.drools.compiler.Person;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.marshalling.impl.ProtobufMessages;
import org.drools.core.marshalling.impl.ReadSessionResult;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;


@RunWith(Parameterized.class)
public class MarshallerTest {
    private Environment env;

    public MarshallerTest(ObjectMarshallingStrategy strategy) {
        this.env = EnvironmentFactory.newEnvironment();
        this.env.set(OBJECT_MARSHALLING_STRATEGIES, new ObjectMarshallingStrategy[]{ strategy });
    }

    @Test
    public void testAgendaDoNotSerializeObject() throws Exception {
        KieSession ksession = null;
        try {
            String str = "import java.util.Collection\n" + ((("rule R1 when\n" + "    String(this == \"x\" || this == \"y\" || this == \"z\")\n") + "then\n") + "end\n");
            KieBase kbase = new KieHelper().addContent(str, DRL).build();
            ksession = kbase.newKieSession(null, env);
            ksession.insert("x");
            ksession.insert("y");
            ksession.insert("z");
            Assert.assertEquals(3, ksession.fireAllRules());
            ReadSessionResult serialisedStatefulKnowledgeSession = SerializationHelper.getSerialisedStatefulKnowledgeSessionWithMessage(ksession, ksession.getKieBase(), true);
            ksession = serialisedStatefulKnowledgeSession.getSession();
            ProtobufMessages.KnowledgeSession deserializedMessage = serialisedStatefulKnowledgeSession.getDeserializedMessage();
            Assert.assertEquals(0, ksession.fireAllRules());
            Assert.assertFalse(deserializedMessage.getRuleData().getAgenda().getMatchList().stream().anyMatch(( ml) -> {
                return (ml.getTuple().getObjectList().size()) > 0;
            }));
        } finally {
            if (ksession != null) {
                ksession.dispose();
            }
        }
    }

    @Test
    public void testFromWithFireBeforeSerialization() throws Exception {
        String str = "import java.util.Collection\n" + ((("rule R1 when\n" + "    String() from [ \"x\", \"y\", \"z\" ]\n") + "then\n") + "end\n");
        KieBase kbase = new KieHelper().addContent(str, DRL).build();
        KieSession ksession = null;
        try {
            ksession = kbase.newKieSession(null, env);
            Assert.assertEquals(3, ksession.fireAllRules());
            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
            Assert.assertEquals(0, ksession.fireAllRules());
        } finally {
            if (ksession != null) {
                ksession.dispose();
            }
        }
    }

    @Test
    public void testFromWithFireAfterSerialization() throws Exception {
        String str = "import java.util.Collection\n" + ((("rule R1 when\n" + "    String() from [ \"x\", \"y\", \"z\" ]\n") + "then\n") + "end\n");
        KieBase kbase = new KieHelper().addContent(str, DRL).build();
        KieSession ksession = null;
        try {
            ksession = kbase.newKieSession(null, env);
            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
            Assert.assertEquals(3, ksession.fireAllRules());
        } finally {
            if (ksession != null) {
                ksession.dispose();
            }
        }
    }

    @Test
    public void testFromWithPartialFiring() throws Exception {
        String str = "import java.util.Collection\n" + ((("rule R1 when\n" + "    String() from [ \"x\", \"y\", \"z\" ]\n") + "then\n") + "end\n");
        KieBase kbase = new KieHelper().addContent(str, DRL).build();
        KieSession ksession = null;
        try {
            ksession = kbase.newKieSession(null, env);
            Assert.assertEquals(2, ksession.fireAllRules(2));
            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
            Assert.assertEquals(1, ksession.fireAllRules());
        } finally {
            if (ksession != null) {
                ksession.dispose();
            }
        }
    }

    @Test
    public void test2FromsWithPartialFiring() throws Exception {
        String str = "import java.util.Collection\n" + (((("rule R1 when\n" + "    String() from [ \"x\", \"y\", \"z\" ]\n") + "    String() from [ \"a\", \"b\", \"c\" ]\n") + "then\n") + "end\n");
        KieBase kbase = new KieHelper().addContent(str, DRL).build();
        KieSession ksession = null;
        try {
            ksession = kbase.newKieSession(null, env);
            Assert.assertEquals(5, ksession.fireAllRules(5));
            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
            Assert.assertEquals(4, ksession.fireAllRules());
        } finally {
            if (ksession != null) {
                ksession.dispose();
            }
        }
    }

    @Test
    public void testFromAndJoinWithPartialFiring() throws Exception {
        String str = "import java.util.Collection\n" + (((("rule R1 when\n" + "    String() from [ \"x\", \"y\", \"z\" ]\n") + "    Integer()\n") + "then\n") + "end\n");
        KieBase kbase = new KieHelper().addContent(str, DRL).build();
        KieSession ksession = null;
        try {
            ksession = kbase.newKieSession(null, env);
            ksession.insert(42);
            Assert.assertEquals(2, ksession.fireAllRules(2));
            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
            Assert.assertEquals(1, ksession.fireAllRules());
        } finally {
            if (ksession != null) {
                ksession.dispose();
            }
        }
    }

    @Test
    public void testAgendaReconciliationAccumulate() throws Exception {
        String str = (((((((("import " + (Person.class.getCanonicalName())) + ";") + "rule X when\n") + "  accumulate ( $p: Person ( getName().startsWith(\"M\")); \n") + "                $sum : sum($p.getAge())  \n") + "              )                          \n") + "then\n") + "  insert($sum);\n") + "end";
        KieBase kbase = new KieHelper().addContent(str, DRL).build();
        KieSession ksession = null;
        try {
            ksession = kbase.newKieSession(null, env);
            ksession.insert(new Person("Mark", 37));
            ksession.insert(new Person("Edson", 35));
            ksession.insert(new Person("Mario", 40));
            Assert.assertEquals(1, ksession.fireAllRules());
            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
            Assert.assertEquals(0, ksession.fireAllRules());
        } finally {
            if (ksession != null) {
                ksession.dispose();
            }
        }
    }

    @Test
    public void testAgendaReconciliationAccumulate2() throws Exception {
        String str = (((((((("import " + (Person.class.getCanonicalName())) + ";") + "rule X when\n") + "  accumulate ( $p: Person ( getName().startsWith(\"M\")); \n") + "                $sum : sum($p.getAge())  \n") + "              )                          \n") + "then\n") + "  insert($sum);\n") + "end";
        KieBase kbase = new KieHelper().addContent(str, DRL).build();
        KieSession ksession = null;
        try {
            ksession = kbase.newKieSession(null, env);
            ksession.insert(new Person("Mark", 37));
            ksession.insert(new Person("Edson", 35));
            ksession.insert(new Person("Mario", 40));
            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
            Assert.assertEquals(1, ksession.fireAllRules());
        } finally {
            if (ksession != null) {
                ksession.dispose();
            }
        }
    }

    @Test
    public void testSubnetwork() throws Exception {
        final String str = "rule R1 when\n" + ((("    String()\n" + "    Long()\n") + "    not( Long() and Integer() )\n") + "then end\n");
        KieBase kbase = new KieHelper().addContent(str, DRL).build(EQUALITY);
        KieSession ksession = null;
        try {
            ksession = kbase.newKieSession(null, env);
            ksession.insert("Luca");
            ksession.insert(2L);
            ksession.insert(10);
            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
            Assert.assertEquals(0, ksession.fireAllRules());
            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
            ksession.delete(ksession.getFactHandle(10));
            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
            Assert.assertEquals(1, ksession.fireAllRules());
        } finally {
            if (ksession != null) {
                ksession.dispose();
            }
        }
    }

    @Test
    public void testSubnetwork2() throws Exception {
        final String str = "rule R1 when\n" + ((("    String()\n" + "    Long()\n") + "    not( Long() and Integer() )\n") + "then end\n");
        KieBase kbase = new KieHelper().addContent(str, DRL).build(EQUALITY);
        KieSession ksession = null;
        try {
            ksession = kbase.newKieSession(null, env);
            ksession.insert("Luca");
            ksession.insert(2L);
            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
            Assert.assertEquals(1, ksession.fireAllRules());
            ksession.insert("Mario");
            ksession.insert(11);
            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
            Assert.assertEquals(0, ksession.fireAllRules());
        } finally {
            ksession.dispose();
        }
    }
}

