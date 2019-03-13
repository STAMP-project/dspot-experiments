package org.jabref.model.entry.identifier;


import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class MathSciNetIdTest {
    @Test
    public void parseRemovesNewLineCharacterAtEnd() throws Exception {
        Optional<MathSciNetId> id = MathSciNetId.parse("3014184\n");
        Assertions.assertEquals(Optional.of(new MathSciNetId("3014184")), id);
    }
}

