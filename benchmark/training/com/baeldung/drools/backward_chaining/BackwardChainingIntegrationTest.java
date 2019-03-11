package com.baeldung.drools.backward_chaining;


import com.baeldung.drools.model.Fact;
import com.baeldung.drools.model.Result;
import junit.framework.TestCase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;


public class BackwardChainingIntegrationTest {
    private Result result;

    private KieSession ksession;

    @Test
    public void whenWallOfChinaIsGiven_ThenItBelongsToPlanetEarth() {
        ksession.setGlobal("result", result);
        ksession.insert(new Fact("Asia", "Planet Earth"));
        ksession.insert(new Fact("China", "Asia"));
        ksession.insert(new Fact("Great Wall of China", "China"));
        ksession.fireAllRules();
        // Assert Decision one
        TestCase.assertEquals(result.getValue(), "Decision one taken: Great Wall of China BELONGS TO Planet Earth");
    }
}

