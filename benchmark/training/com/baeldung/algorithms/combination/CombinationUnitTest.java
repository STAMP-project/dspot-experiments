package com.baeldung.algorithms.combination;


import java.util.List;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;


public class CombinationUnitTest {
    private static final int N = 5;

    private static final int R = 3;

    private static final int nCr = 10;

    @Test
    public void givenSetAndSelectionSize_whenCalculatedUsingSetRecursiveAlgorithm_thenExpectedCount() {
        SetRecursiveCombinationGenerator generator = new SetRecursiveCombinationGenerator();
        List<int[]> selection = generator.generate(CombinationUnitTest.N, CombinationUnitTest.R);
        Assertions.assertEquals(CombinationUnitTest.nCr, selection.size());
    }

    @Test
    public void givenSetAndSelectionSize_whenCalculatedUsingSelectionRecursiveAlgorithm_thenExpectedCount() {
        SelectionRecursiveCombinationGenerator generator = new SelectionRecursiveCombinationGenerator();
        List<int[]> selection = generator.generate(CombinationUnitTest.N, CombinationUnitTest.R);
        Assertions.assertEquals(CombinationUnitTest.nCr, selection.size());
    }

    @Test
    public void givenSetAndSelectionSize_whenCalculatedUsingIterativeAlgorithm_thenExpectedCount() {
        IterativeCombinationGenerator generator = new IterativeCombinationGenerator();
        List<int[]> selection = generator.generate(CombinationUnitTest.N, CombinationUnitTest.R);
        Assertions.assertEquals(CombinationUnitTest.nCr, selection.size());
    }
}

