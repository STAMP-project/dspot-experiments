package org.jabref.logic.util;


import Version.DevelopmentStage;
import Version.DevelopmentStage.ALPHA;
import Version.DevelopmentStage.BETA;
import Version.DevelopmentStage.STABLE;
import Version.DevelopmentStage.UNKNOWN;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class DevelopmentStageTest {
    @Test
    public void checkStabilityOrder() {
        Assertions.assertTrue(ALPHA.isMoreStableThan(UNKNOWN));
        Assertions.assertTrue(BETA.isMoreStableThan(ALPHA));
        Assertions.assertTrue(STABLE.isMoreStableThan(BETA));
        Assertions.assertEquals(4, DevelopmentStage.values().length, "It seems that the development stages have been changed, please adjust the test");
    }

    @Test
    public void parseStages() {
        Assertions.assertEquals(ALPHA, DevelopmentStage.parse("-alpha"));
        Assertions.assertEquals(BETA, DevelopmentStage.parse("-beta"));
        Assertions.assertEquals(STABLE, DevelopmentStage.parse(""));
    }

    @Test
    public void parseNull() {
        Assertions.assertEquals(UNKNOWN, DevelopmentStage.parse(null));
    }

    @Test
    public void parseUnknownString() {
        Assertions.assertEquals(UNKNOWN, DevelopmentStage.parse("asdf"));
    }
}

