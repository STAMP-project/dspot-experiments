package com.baeldung.spel;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class SpelIntegrationTest {
    @Autowired
    private SpelArithmetic spelArithmetic = new SpelArithmetic();

    @Autowired
    private SpelCollections spelCollections = new SpelCollections();

    @Autowired
    private SpelConditional spelConditional = new SpelConditional();

    @Autowired
    private SpelLogical spelLogical = new SpelLogical();

    @Autowired
    private SpelRegex spelRegex = new SpelRegex();

    @Autowired
    private SpelRelational spelRelational = new SpelRelational();

    @Test
    public void testArithmetic() throws Exception {
        Assert.assertThat(spelArithmetic.getAdd(), Matchers.equalTo(20.0));
        Assert.assertThat(spelArithmetic.getAddString(), Matchers.equalTo("Some string plus other string"));
        Assert.assertThat(spelArithmetic.getSubtract(), Matchers.equalTo(19.0));
        Assert.assertThat(spelArithmetic.getMultiply(), Matchers.equalTo(20.0));
        Assert.assertThat(spelArithmetic.getDivide(), Matchers.equalTo(18.0));
        Assert.assertThat(spelArithmetic.getDivideAlphabetic(), Matchers.equalTo(18.0));
        Assert.assertThat(spelArithmetic.getModulo(), Matchers.equalTo(7.0));
        Assert.assertThat(spelArithmetic.getModuloAlphabetic(), Matchers.equalTo(7.0));
        Assert.assertThat(spelArithmetic.getPowerOf(), Matchers.equalTo(512.0));
        Assert.assertThat(spelArithmetic.getBrackets(), Matchers.equalTo(17.0));
    }

    @Test
    public void testCollections() throws Exception {
        Assert.assertThat(spelCollections.getDriver1Car().getModel(), Matchers.equalTo("Model1"));
        Assert.assertThat(spelCollections.getDriver2Car().getModel(), Matchers.equalTo("Model2"));
        Assert.assertThat(spelCollections.getFirstCarInPark().getModel(), Matchers.equalTo("Model1"));
        Assert.assertThat(spelCollections.getNumberOfCarsInPark(), Matchers.equalTo(2));
    }

    @Test
    public void testConditional() throws Exception {
        Assert.assertThat(spelConditional.getTernary(), Matchers.equalTo("Something went wrong. There was false value"));
        Assert.assertThat(spelConditional.getTernary2(), Matchers.equalTo("Some model"));
        Assert.assertThat(spelConditional.getElvis(), Matchers.equalTo("Some model"));
    }

    @Test
    public void testLogical() throws Exception {
        Assert.assertThat(spelLogical.isAnd(), Matchers.equalTo(true));
        Assert.assertThat(spelLogical.isAndAlphabetic(), Matchers.equalTo(true));
        Assert.assertThat(spelLogical.isOr(), Matchers.equalTo(true));
        Assert.assertThat(spelLogical.isOrAlphabetic(), Matchers.equalTo(true));
        Assert.assertThat(spelLogical.isNot(), Matchers.equalTo(false));
        Assert.assertThat(spelLogical.isNotAlphabetic(), Matchers.equalTo(false));
    }

    @Test
    public void testRegex() throws Exception {
        Assert.assertThat(spelRegex.isValidNumericStringResult(), Matchers.equalTo(true));
        Assert.assertThat(spelRegex.isInvalidNumericStringResult(), Matchers.equalTo(false));
        Assert.assertThat(spelRegex.isValidAlphabeticStringResult(), Matchers.equalTo(true));
        Assert.assertThat(spelRegex.isInvalidAlphabeticStringResult(), Matchers.equalTo(false));
        Assert.assertThat(spelRegex.isValidFormatOfHorsePower(), Matchers.equalTo(true));
    }

    @Test
    public void testRelational() throws Exception {
        Assert.assertThat(spelRelational.isEqual(), Matchers.equalTo(true));
        Assert.assertThat(spelRelational.isEqualAlphabetic(), Matchers.equalTo(true));
        Assert.assertThat(spelRelational.isNotEqual(), Matchers.equalTo(false));
        Assert.assertThat(spelRelational.isNotEqualAlphabetic(), Matchers.equalTo(false));
        Assert.assertThat(spelRelational.isLessThan(), Matchers.equalTo(false));
        Assert.assertThat(spelRelational.isLessThanAlphabetic(), Matchers.equalTo(false));
        Assert.assertThat(spelRelational.isLessThanOrEqual(), Matchers.equalTo(true));
        Assert.assertThat(spelRelational.isLessThanOrEqualAlphabetic(), Matchers.equalTo(true));
        Assert.assertThat(spelRelational.isGreaterThan(), Matchers.equalTo(false));
        Assert.assertThat(spelRelational.isGreaterThanAlphabetic(), Matchers.equalTo(false));
        Assert.assertThat(spelRelational.isGreaterThanOrEqual(), Matchers.equalTo(true));
        Assert.assertThat(spelRelational.isGreaterThanOrEqualAlphabetic(), Matchers.equalTo(true));
        Assert.assertThat(spelRelational.isAnd(), Matchers.equalTo(true));
        Assert.assertThat(spelRelational.isAndAlphabetic(), Matchers.equalTo(true));
        Assert.assertThat(spelRelational.isOr(), Matchers.equalTo(true));
        Assert.assertThat(spelRelational.isOrAlphabetic(), Matchers.equalTo(true));
        Assert.assertThat(spelRelational.isNot(), Matchers.equalTo(false));
        Assert.assertThat(spelRelational.isNotAlphabetic(), Matchers.equalTo(false));
    }
}

