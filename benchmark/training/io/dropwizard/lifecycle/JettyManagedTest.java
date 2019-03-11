package io.dropwizard.lifecycle;


import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class JettyManagedTest {
    private final Managed managed = Mockito.mock(Managed.class);

    private final JettyManaged jettyManaged = new JettyManaged(managed);

    @Test
    public void startsAndStops() throws Exception {
        jettyManaged.start();
        jettyManaged.stop();
        final InOrder inOrder = Mockito.inOrder(managed);
        inOrder.verify(managed).start();
        inOrder.verify(managed).stop();
    }
}

