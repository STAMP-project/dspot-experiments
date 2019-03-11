package org.deeplearning4j.arbiter.optimize.genetic.crossover;


import java.util.ArrayList;
import java.util.List;
import org.deeplearning4j.arbiter.optimize.generator.genetic.Chromosome;
import org.deeplearning4j.arbiter.optimize.genetic.TestParentSelection;
import org.junit.Assert;
import org.junit.Test;


public class ParentSelectionTests {
    @Test
    public void ParentSelection_InitializeInstance_ShouldInitPopulation() {
        TestParentSelection sut = new TestParentSelection();
        List<Chromosome> population = new ArrayList<>();
        sut.initializeInstance(population);
        Assert.assertSame(population, sut.getPopulation());
    }
}

