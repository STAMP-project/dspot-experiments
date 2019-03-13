package org.robobinding.codegen.presentationmodel.processor;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.robobinding.codegen.presentationmodel.nestedIPM.ItemPresentationModelExample;
import org.robobinding.codegen.presentationmodel.nestedIPM.PresentationModelExample;
import org.robobinding.codegen.presentationmodel.sharedfactorymethod.SharedFacotryMethodPresentationModel;


/**
 *
 *
 * @since 1.0
 * @author Cheng Wei
 */
public class PresentationModelProcessorTest {
    private PresentationModelProcessor processor;

    @Test
    public void shouldSuccessWhenProcessSample1() {
        ProcessorAssert.compilesWithoutErrorWhenProcess(PresentationModelSample1.class, processor);
    }

    @Test
    public void shouldSuccessWhenProcessNestedIPMExample() {
        ProcessorAssert.compilesWithoutErrorWhenProcess(processor, PresentationModelExample.class, ItemPresentationModelExample.class);
    }

    @Test
    public void shouldSuccessWhenProcessSharedFactoryMethodExample() {
        ProcessorAssert.compilesWithoutErrorWhenProcess(processor, SharedFacotryMethodPresentationModel.class);
    }

    @Test
    public void shouldGetExpectedErrors() {
        try {
            ProcessorAssert.failsToCompileWhenProcess(PresentationModelWithErrors.class, processor);
            Assert.fail("expect errors");
        } catch (PresentationModelErrors errors) {
            Assert.assertThat(errors.propertyErrors.size(), Matchers.is(PresentationModelWithErrors.numPropertyErrors));
            Assert.assertThat(errors.propertyDependencyErrors.size(), Matchers.is(PresentationModelWithErrors.numPropertyDependencyErrors));
        }
    }
}

