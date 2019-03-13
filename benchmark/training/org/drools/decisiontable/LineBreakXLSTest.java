package org.drools.decisiontable;


import DecisionTableInputType.XLSX;
import ResourceType.DTABLE;
import com.sample.FactData;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;


public class LineBreakXLSTest {
    @Test
    public void makeSureAdditionalCodeLineEndsAreNotAdded() {
        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(XLSX);
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("testrule.xlsx", getClass()), DTABLE, dtconf);
        if (kbuilder.hasErrors()) {
            Assert.fail(kbuilder.getErrors().toString());
        }
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        KieSession ksession = kbase.newKieSession();
        FactData fd = new FactData();
        fd.set?((-1));
        ksession.insert(fd);
        ksession.fireAllRules();
        ksession.dispose();
        Assert.assertTrue(fd.get????????().contains("\u5024\u306b\u306f0\u4ee5\u4e0a\u3092\u6307\u5b9a\u3057\u3066\u304f\u3060\u3055\u3044\u3002\n\u6307\u5b9a\u3055\u308c\u305f\u5024\uff1a"));
    }
}

