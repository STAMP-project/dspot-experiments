package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import java.text.DecimalFormatSymbols;
import org.junit.Assert;
import org.junit.Test;


public class CommerceTest extends AbstractFakerTest {
    private static final char decimalSeparator = new DecimalFormatSymbols().getDecimalSeparator();

    @Test
    public void testColor() {
        Assert.assertThat(faker.commerce().color(), MatchesRegularExpression.matchesRegularExpression("(\\w+ ?){1,2}"));
    }

    @Test
    public void testDepartment() {
        Assert.assertThat(faker.commerce().department(), MatchesRegularExpression.matchesRegularExpression("(\\w+(, | & )?){1,3}"));
    }

    @Test
    public void testProductName() {
        Assert.assertThat(faker.commerce().productName(), MatchesRegularExpression.matchesRegularExpression("(\\w+ ?){3,4}"));
    }

    @Test
    public void testMaterial() {
        Assert.assertThat(faker.commerce().material(), MatchesRegularExpression.matchesRegularExpression("\\w+"));
    }

    @Test
    public void testPrice() {
        Assert.assertThat(faker.commerce().price(), MatchesRegularExpression.matchesRegularExpression((("\\d{1,3}\\" + (CommerceTest.decimalSeparator)) + "\\d{2}")));
    }

    @Test
    public void testPriceMinMax() {
        Assert.assertThat(faker.commerce().price(100, 1000), MatchesRegularExpression.matchesRegularExpression((("\\d{3,4}\\" + (CommerceTest.decimalSeparator)) + "\\d{2}")));
    }

    @Test
    public void testPromotionCode() {
        Assert.assertThat(faker.commerce().promotionCode(), MatchesRegularExpression.matchesRegularExpression("[A-Z][a-z]+[A-Z][a-z]+\\d{6}"));
    }

    @Test
    public void testPromotionCodeDigits() {
        Assert.assertThat(faker.commerce().promotionCode(3), MatchesRegularExpression.matchesRegularExpression("[A-Z][a-z]+[A-Z][a-z]+\\d{3}"));
    }
}

