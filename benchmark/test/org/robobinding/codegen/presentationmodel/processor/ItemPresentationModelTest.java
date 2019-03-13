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
public class ItemPresentationModelTest {
    @Test
    public void shouldAllDataSetPropertiesTreatedAsProperties() {
        PresentationModelInfo result = processJavaFileOf(PresentationModelForDataSetProp.class);
        Assert.assertThat(result.properties().size(), Matchers.is(1));
        Assert.assertThat(result.dataSetProperties().size(), Matchers.is(0));
    }
}

