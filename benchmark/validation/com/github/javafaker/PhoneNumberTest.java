package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


public class PhoneNumberTest extends AbstractFakerTest {
    @Test
    public void testCellPhone_enUS() {
        final Faker f = new Faker(Locale.US);
        Assert.assertThat(f.phoneNumber().cellPhone(), MatchesRegularExpression.matchesRegularExpression("\\(?\\d+\\)?([- .]\\d+){1,3}"));
    }

    @Test
    public void testPhone_esMx() {
        final Faker f = new Faker(new Locale("es_MX"));
        for (int i = 0; i < 100; i++) {
            Assert.assertThat(f.phoneNumber().cellPhone(), MatchesRegularExpression.matchesRegularExpression("(044 )?\\(?\\d+\\)?([- .]\\d+){1,3}"));
            Assert.assertThat(f.phoneNumber().phoneNumber(), MatchesRegularExpression.matchesRegularExpression("\\(?\\d+\\)?([- .]\\d+){1,3}"));
        }
    }

    @Test
    public void testPhone_enZA() {
        final Faker f = new Faker(new Locale("en_ZA"));
        for (int i = 0; i < 100; i++) {
            Assert.assertThat(f.phoneNumber().phoneNumber(), MatchesRegularExpression.matchesRegularExpression("\\(?\\d+\\)?([- .]\\d+){1,3}"));
        }
    }

    @Test
    public void testCellPhone() {
        Assert.assertThat(faker.phoneNumber().cellPhone(), MatchesRegularExpression.matchesRegularExpression("\\(?\\d+\\)?([- .]\\d+){1,3}"));
    }

    @Test
    public void testPhoneNumber() {
        Assert.assertThat(faker.phoneNumber().phoneNumber(), MatchesRegularExpression.matchesRegularExpression("\\(?\\d+\\)?([- .]x?\\d+){1,5}"));
    }
}

