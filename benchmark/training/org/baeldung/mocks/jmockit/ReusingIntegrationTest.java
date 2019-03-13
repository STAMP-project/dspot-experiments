package org.baeldung.mocks.jmockit;


import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(JMockit.class)
public class ReusingIntegrationTest {
    @Injectable
    private Collaborator collaborator;

    @Mocked
    private Model model;

    @Tested
    private Performer performer;

    @Test
    public void testWithSetup() {
        performer.perform(model);
        verifyTrueCalls(1);
    }

    final class TrueCallsVerification extends Verifications {
        public TrueCallsVerification(int calls) {
            collaborator.receive(true);
            times = calls;
        }
    }

    @Test
    public void testWithFinalClass() {
        performer.perform(model);
        new ReusingIntegrationTest.TrueCallsVerification(1);
    }
}

