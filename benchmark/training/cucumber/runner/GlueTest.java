package cucumber.runner;


import cucumber.runtime.DuplicateStepDefinitionException;
import cucumber.runtime.HookDefinition;
import cucumber.runtime.StepDefinition;
import gherkin.pickles.PickleStep;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class GlueTest {
    private Glue glue;

    @Test
    public void throws_duplicate_error_on_dupe_stepdefs() {
        Glue glue = new Glue();
        StepDefinition a = Mockito.mock(StepDefinition.class);
        Mockito.when(a.getPattern()).thenReturn("hello");
        Mockito.when(a.getLocation(true)).thenReturn("foo.bf:10");
        glue.addStepDefinition(a);
        StepDefinition b = Mockito.mock(StepDefinition.class);
        Mockito.when(b.getPattern()).thenReturn("hello");
        Mockito.when(b.getLocation(true)).thenReturn("bar.bf:90");
        try {
            glue.addStepDefinition(b);
            Assert.fail("should have failed");
        } catch (DuplicateStepDefinitionException expected) {
            Assert.assertEquals("Duplicate step definitions in foo.bf:10 and bar.bf:90", expected.getMessage());
        }
    }

    @Test
    public void removes_glue_that_is_scenario_scoped() {
        // This test is a bit fragile - it is testing state, not behaviour.
        // But it was too much hassle creating a better test without refactoring RuntimeGlue
        // and probably some of its immediate collaborators... Aslak.
        StepDefinition sd = Mockito.mock(StepDefinition.class);
        Mockito.when(sd.isScenarioScoped()).thenReturn(true);
        Mockito.when(sd.getPattern()).thenReturn("pattern");
        glue.addStepDefinition(sd);
        HookDefinition bh = Mockito.mock(HookDefinition.class);
        Mockito.when(bh.isScenarioScoped()).thenReturn(true);
        glue.addBeforeHook(bh);
        HookDefinition ah = Mockito.mock(HookDefinition.class);
        Mockito.when(ah.isScenarioScoped()).thenReturn(true);
        glue.addAfterHook(ah);
        Assert.assertEquals(1, glue.stepDefinitionsByPattern.size());
        Assert.assertEquals(1, glue.beforeHooks.size());
        Assert.assertEquals(1, glue.afterHooks.size());
        glue.removeScenarioScopedGlue();
        Assert.assertEquals(0, glue.stepDefinitionsByPattern.size());
        Assert.assertEquals(0, glue.beforeHooks.size());
        Assert.assertEquals(0, glue.afterHooks.size());
    }

    @Test
    public void removes_scenario_scoped_cache_entries() {
        StepDefinition sd = GlueTest.getStepDefinitionMockWithPattern("pattern");
        Mockito.when(sd.isScenarioScoped()).thenReturn(true);
        glue.addStepDefinition(sd);
        String featurePath = "someFeature.feature";
        String stepText = "pattern";
        PickleStep pickleStep1 = GlueTest.getPickleStep(stepText);
        Assert.assertEquals(sd, glue.stepDefinitionMatch(featurePath, pickleStep1).getStepDefinition());
        Assert.assertEquals(1, glue.stepDefinitionsByStepText.size());
        glue.removeScenarioScopedGlue();
        Assert.assertEquals(0, glue.stepDefinitionsByStepText.size());
    }

    @Test
    public void returns_null_if_no_matching_steps_found() {
        StepDefinition stepDefinition = GlueTest.getStepDefinitionMockWithPattern("pattern1");
        glue.addStepDefinition(stepDefinition);
        String featurePath = "someFeature.feature";
        PickleStep pickleStep = GlueTest.getPickleStep("pattern");
        Assert.assertNull(glue.stepDefinitionMatch(featurePath, pickleStep));
        Mockito.verify(stepDefinition).matchedArguments(pickleStep);
    }

    @Test
    public void returns_match_from_cache_if_single_found() {
        StepDefinition stepDefinition1 = GlueTest.getStepDefinitionMockWithPattern("^pattern1");
        StepDefinition stepDefinition2 = GlueTest.getStepDefinitionMockWithPattern("^pattern2");
        glue.addStepDefinition(stepDefinition1);
        glue.addStepDefinition(stepDefinition2);
        String featurePath = "someFeature.feature";
        String stepText = "pattern1";
        PickleStep pickleStep1 = GlueTest.getPickleStep(stepText);
        Assert.assertEquals(stepDefinition1, glue.stepDefinitionMatch(featurePath, pickleStep1).getStepDefinition());
        // verify if all defs are checked
        Mockito.verify(stepDefinition1).matchedArguments(pickleStep1);
        Mockito.verify(stepDefinition2).matchedArguments(pickleStep1);
        // check cache
        StepDefinition entry = glue.stepDefinitionsByStepText.get(stepText);
        Assert.assertEquals(stepDefinition1, entry);
        PickleStep pickleStep2 = GlueTest.getPickleStep(stepText);
        Assert.assertEquals(stepDefinition1, glue.stepDefinitionMatch(featurePath, pickleStep2).getStepDefinition());
        // verify that only cached step definition has called matchedArguments again
        Mockito.verify(stepDefinition1, Mockito.times(2)).matchedArguments(ArgumentMatchers.any(PickleStep.class));
        Mockito.verify(stepDefinition2).matchedArguments(ArgumentMatchers.any(PickleStep.class));
    }

    @Test
    public void returns_match_from_cache_for_step_with_table() {
        StepDefinition stepDefinition1 = GlueTest.getStepDefinitionMockWithPattern("^pattern1");
        StepDefinition stepDefinition2 = GlueTest.getStepDefinitionMockWithPattern("^pattern2");
        glue.addStepDefinition(stepDefinition1);
        glue.addStepDefinition(stepDefinition2);
        String featurePath = "someFeature.feature";
        String stepText = "pattern1";
        PickleStep pickleStep1 = GlueTest.getPickleStepWithSingleCellTable(stepText, "cell 1");
        PickleStepDefinitionMatch match1 = glue.stepDefinitionMatch(featurePath, pickleStep1);
        Assert.assertEquals(stepDefinition1, match1.getStepDefinition());
        // verify if all defs are checked
        Mockito.verify(stepDefinition1).matchedArguments(pickleStep1);
        Mockito.verify(stepDefinition2).matchedArguments(pickleStep1);
        // check cache
        StepDefinition entry = glue.stepDefinitionsByStepText.get(stepText);
        Assert.assertEquals(stepDefinition1, entry);
        // check arguments
        Assert.assertEquals("cell 1", cell(0, 0));
        // check second match
        PickleStep pickleStep2 = GlueTest.getPickleStepWithSingleCellTable(stepText, "cell 2");
        PickleStepDefinitionMatch match2 = glue.stepDefinitionMatch(featurePath, pickleStep2);
        // verify that only cached step definition has called matchedArguments again
        Mockito.verify(stepDefinition1, Mockito.times(2)).matchedArguments(ArgumentMatchers.any(PickleStep.class));
        Mockito.verify(stepDefinition2).matchedArguments(ArgumentMatchers.any(PickleStep.class));
        // check arguments
        Assert.assertEquals("cell 2", cell(0, 0));
    }

    @Test
    public void returns_match_from_cache_for_ste_with_doc_string() {
        StepDefinition stepDefinition1 = GlueTest.getStepDefinitionMockWithPattern("^pattern1");
        StepDefinition stepDefinition2 = GlueTest.getStepDefinitionMockWithPattern("^pattern2");
        glue.addStepDefinition(stepDefinition1);
        glue.addStepDefinition(stepDefinition2);
        String featurePath = "someFeature.feature";
        String stepText = "pattern1";
        PickleStep pickleStep1 = GlueTest.getPickleStepWithDocString(stepText, "doc string 1");
        PickleStepDefinitionMatch match1 = glue.stepDefinitionMatch(featurePath, pickleStep1);
        Assert.assertEquals(stepDefinition1, match1.getStepDefinition());
        // verify if all defs are checked
        Mockito.verify(stepDefinition1).matchedArguments(pickleStep1);
        Mockito.verify(stepDefinition2).matchedArguments(pickleStep1);
        // check cache
        StepDefinition entry = glue.stepDefinitionsByStepText.get(stepText);
        Assert.assertEquals(stepDefinition1, entry);
        // check arguments
        Assert.assertEquals("doc string 1", match1.getArguments().get(0).getValue());
        // check second match
        PickleStep pickleStep2 = GlueTest.getPickleStepWithDocString(stepText, "doc string 2");
        PickleStepDefinitionMatch match2 = glue.stepDefinitionMatch(featurePath, pickleStep2);
        // verify that only cached step definition has called matchedArguments again
        Mockito.verify(stepDefinition1, Mockito.times(2)).matchedArguments(ArgumentMatchers.any(PickleStep.class));
        Mockito.verify(stepDefinition2).matchedArguments(ArgumentMatchers.any(PickleStep.class));
        // check arguments
        Assert.assertEquals("doc string 2", match2.getArguments().get(0).getValue());
    }

    @Test
    public void throws_ambiguous_steps_def_exception_when_many_patterns_match() {
        StepDefinition stepDefinition1 = GlueTest.getStepDefinitionMockWithPattern("pattern1");
        StepDefinition stepDefinition2 = GlueTest.getStepDefinitionMockWithPattern("^pattern2");
        StepDefinition stepDefinition3 = GlueTest.getStepDefinitionMockWithPattern("^pattern[1,3]");
        glue.addStepDefinition(stepDefinition1);
        glue.addStepDefinition(stepDefinition2);
        glue.addStepDefinition(stepDefinition3);
        String featurePath = "someFeature.feature";
        checkAmbiguousCalled(featurePath);
        // try again to verify if we don't cache when there is ambiguous step
        checkAmbiguousCalled(featurePath);
    }
}

