package org.jabref.model.database.event;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class AutosaveEventTest {
    @Test
    public void givenNothingWhenCreatingThenNotNull() {
        AutosaveEvent e = new AutosaveEvent();
        Assertions.assertNotNull(e);
    }
}

