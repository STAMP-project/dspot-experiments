package io.dropwizard.lifecycle;


import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class AutoCloseableManagerTest {
    private final AutoCloseable managed = Mockito.mock(AutoCloseable.class);

    private final AutoCloseableManager closeableManager = new AutoCloseableManager(this.managed);

    @Test
    public void startsAndStops() throws Exception {
        this.closeableManager.start();
        this.closeableManager.stop();
        Mockito.verify(this.managed).close();
    }
}

