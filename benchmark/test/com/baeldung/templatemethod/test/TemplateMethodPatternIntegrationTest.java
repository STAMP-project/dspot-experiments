package com.baeldung.templatemethod.test;


import com.baeldung.pattern.templatemethod.model.Computer;
import com.baeldung.pattern.templatemethod.model.HighEndComputerBuilder;
import com.baeldung.pattern.templatemethod.model.StandardComputerBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TemplateMethodPatternIntegrationTest {
    private static StandardComputerBuilder standardComputerBuilder;

    private static HighEndComputerBuilder highEndComputerBuilder;

    @Test
    public void givenStandardMotherBoard_whenAddingMotherBoard_thenEqualAssertion() {
        TemplateMethodPatternIntegrationTest.standardComputerBuilder.addMotherboard();
        Assert.assertEquals("Standard Motherboard", TemplateMethodPatternIntegrationTest.standardComputerBuilder.getComputerParts().get("Motherboard"));
    }

    @Test
    public void givenStandardMotherboard_whenSetup_thenTwoEqualAssertions() {
        TemplateMethodPatternIntegrationTest.standardComputerBuilder.setupMotherboard();
        Assert.assertEquals("Screwing the standard motherboard to the case.", TemplateMethodPatternIntegrationTest.standardComputerBuilder.getMotherboardSetupStatus().get(0));
        Assert.assertEquals("Pluging in the power supply connectors.", TemplateMethodPatternIntegrationTest.standardComputerBuilder.getMotherboardSetupStatus().get(1));
    }

    @Test
    public void givenStandardProcessor_whenAddingProcessor_thenEqualAssertion() {
        TemplateMethodPatternIntegrationTest.standardComputerBuilder.addProcessor();
        Assert.assertEquals("Standard Processor", TemplateMethodPatternIntegrationTest.standardComputerBuilder.getComputerParts().get("Processor"));
    }

    @Test
    public void givenAllStandardParts_whenBuildingComputer_thenTwoParts() {
        TemplateMethodPatternIntegrationTest.standardComputerBuilder.buildComputer();
        Assert.assertEquals(2, TemplateMethodPatternIntegrationTest.standardComputerBuilder.getComputerParts().size());
    }

    @Test
    public void givenAllStandardParts_whenComputerisBuilt_thenComputerInstance() {
        Assert.assertThat(TemplateMethodPatternIntegrationTest.standardComputerBuilder.buildComputer(), CoreMatchers.instanceOf(Computer.class));
    }

    @Test
    public void givenHighEnddMotherBoard_whenAddingMotherBoard_thenEqualAssertion() {
        TemplateMethodPatternIntegrationTest.highEndComputerBuilder.addMotherboard();
        Assert.assertEquals("High-end Motherboard", TemplateMethodPatternIntegrationTest.highEndComputerBuilder.getComputerParts().get("Motherboard"));
    }

    @Test
    public void givenHighEnddMotheroboard_whenSetup_thenTwoEqualAssertions() {
        TemplateMethodPatternIntegrationTest.highEndComputerBuilder.setupMotherboard();
        Assert.assertEquals("Screwing the high-end motherboard to the case.", TemplateMethodPatternIntegrationTest.highEndComputerBuilder.getMotherboardSetupStatus().get(0));
        Assert.assertEquals("Pluging in the power supply connectors.", TemplateMethodPatternIntegrationTest.highEndComputerBuilder.getMotherboardSetupStatus().get(1));
    }

    @Test
    public void givenHightEndProcessor_whenAddingProcessor_thenEqualAssertion() {
        TemplateMethodPatternIntegrationTest.highEndComputerBuilder.addProcessor();
        Assert.assertEquals("High-end Processor", TemplateMethodPatternIntegrationTest.highEndComputerBuilder.getComputerParts().get("Processor"));
    }

    @Test
    public void givenAllHighEnddParts_whenBuildingComputer_thenTwoParts() {
        TemplateMethodPatternIntegrationTest.highEndComputerBuilder.buildComputer();
        Assert.assertEquals(2, TemplateMethodPatternIntegrationTest.highEndComputerBuilder.getComputerParts().size());
    }

    @Test
    public void givenAllHighEndParts_whenComputerisBuilt_thenComputerInstance() {
        Assert.assertThat(TemplateMethodPatternIntegrationTest.standardComputerBuilder.buildComputer(), CoreMatchers.instanceOf(Computer.class));
    }
}

