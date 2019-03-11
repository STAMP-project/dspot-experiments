package hudson.model;


import ChoiceParameterDefinition.DescriptorImpl;
import FormValidation.Kind.ERROR;
import FormValidation.Kind.OK;
import org.junit.Assert;
import org.junit.Test;


public class ChoiceParameterDefinitionTest {
    @Test
    public void shouldValidateChoices() {
        Assert.assertFalse(ChoiceParameterDefinition.areValidChoices(""));
        Assert.assertFalse(ChoiceParameterDefinition.areValidChoices("        "));
        Assert.assertTrue(ChoiceParameterDefinition.areValidChoices("abc"));
        Assert.assertTrue(ChoiceParameterDefinition.areValidChoices("abc\ndef"));
        Assert.assertTrue(ChoiceParameterDefinition.areValidChoices("abc\r\ndef"));
    }

    @Test
    public void testCheckChoices() throws Exception {
        ChoiceParameterDefinition.DescriptorImpl descriptorImpl = new ChoiceParameterDefinition.DescriptorImpl();
        Assert.assertEquals(OK, descriptorImpl.doCheckChoices("abc\ndef").kind);
        Assert.assertEquals(ERROR, descriptorImpl.doCheckChoices("").kind);
    }
}

