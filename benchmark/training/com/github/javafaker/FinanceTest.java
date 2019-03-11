package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class FinanceTest extends AbstractFakerTest {
    @Test
    public void creditCard() {
        for (int i = 0; i < 100; i++) {
            final String creditCard = faker.finance().creditCard();
            assertCardLuhnDigit(creditCard);
        }
    }

    @Test
    public void bic() {
        Assert.assertThat(faker.finance().bic(), MatchesRegularExpression.matchesRegularExpression("([A-Z]){4}([A-Z]){2}([0-9A-Z]){2}([0-9A-Z]{3})?"));
    }

    @Test
    public void iban() {
        Assert.assertThat(faker.finance().iban(), MatchesRegularExpression.matchesRegularExpression("[A-Z]{2}\\p{Alnum}{13,30}"));
    }

    @Test
    public void ibanWithCountryCode() {
        Assert.assertThat(faker.finance().iban("DE"), MatchesRegularExpression.matchesRegularExpression("DE\\d{20}"));
    }

    @Test
    public void creditCardWithType() {
        for (CreditCardType type : CreditCardType.values()) {
            final String creditCard = faker.finance().creditCard(type);
            assertCardLuhnDigit(creditCard);
        }
    }
}

