package org.robobinding.codegen.presentationmodel.processor;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.robobinding.codegen.presentationmodel.PresentationModelInfo;


/**
 *
 *
 * @since 1.0
 * @author Cheng Wei
 */
public class PresentationModelTest {
    @Test
    public void whenProcessPresentationModel_thenGetExpectedResult() {
        PresentationModelInfo result = processJavaFileOf(SelfDescribedPresentationModel.class);
        Assert.assertThat(result.properties().size(), Matchers.is(SelfDescribedPresentationModel.numProperties));
        Assert.assertThat(result.dataSetProperties().size(), Matchers.is(SelfDescribedPresentationModel.numDataSetProperties));
        Assert.assertThat(result.eventMethods().size(), Matchers.is(SelfDescribedPresentationModel.numEventMethods));
    }

    @Test
    public void shouldGetPresentationModelChangeSupportMethodIgnored() {
        PresentationModelInfo result = processJavaFileOf(GetPresentationModelChangeSupportIgnored.class);
        Assert.assertThat(result.properties().size(), Matchers.is(0));
    }

    @Test
    public void shouldInvaidPropertiesIgnored() {
        PresentationModelInfo result = processJavaFileOf(InvalidPropertiesIgnored.class);
        Assert.assertThat(result.properties().size(), Matchers.is(0));
    }

    @Test
    public void shouldMethodForDataSetPropertyIgnored() {
        PresentationModelInfo result = processJavaFileOf(MethodsForDataSetPropertyIgnored.class);
        Assert.assertThat(result.properties().size(), Matchers.is(0));
    }

    @Test
    public void shouldInvalidEventMethodIgnored() {
        PresentationModelInfo result = processJavaFileOf(InvalidEventMethodIgnored.class);
        Assert.assertThat(result.eventMethods().size(), Matchers.is(0));
    }

    @Test
    public void shouldHierarchicalPresentationModelRecognized() {
        PresentationModelInfo result = processJavaFileOf(HierarchicalPresentationModel.class);
        Assert.assertThat(result.properties().size(), Matchers.is(HierarchicalPresentationModel.numProperties));
        Assert.assertThat(result.eventMethods().size(), Matchers.is(HierarchicalPresentationModel.numEventMethods));
    }
}

