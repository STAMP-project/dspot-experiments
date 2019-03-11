package com.github.javafaker;


import Internet.UserAgent;
import com.github.javafaker.matchers.CountOfCharactersMatcher;
import com.github.javafaker.matchers.MatchesRegularExpression;
import com.github.javafaker.repeating.Repeat;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Locale;
import org.apache.commons.validator.routines.EmailValidator;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class InternetTest extends AbstractFakerTest {
    @Test
    public void testEmailAddress() {
        String emailAddress = faker.internet().emailAddress();
        Assert.assertThat(EmailValidator.getInstance().isValid(emailAddress), Matchers.is(true));
    }

    @Test
    public void testEmailAddressWithLocalPartParameter() {
        String emailAddress = faker.internet().emailAddress("john");
        Assert.assertThat(emailAddress, Matchers.startsWith("john@"));
        Assert.assertThat(EmailValidator.getInstance().isValid(emailAddress), Matchers.is(true));
    }

    @Test
    public void testSafeEmailAddress() {
        List<String> emails = Lists.newArrayList();
        for (int i = 0; i < 100; i++) {
            String emailAddress = faker.internet().safeEmailAddress();
            Assert.assertThat(EmailValidator.getInstance().isValid(emailAddress), Matchers.is(true));
            emails.add(emailAddress);
        }
        final String safeDomain = faker.resolve("internet.safe_email");
        Assert.assertThat(("Should find at least one email from " + safeDomain), emails, Matchers.hasItem(Matchers.endsWith(("@" + safeDomain))));
    }

    @Test
    public void testSafeEmailAddressWithLocalPartParameter() {
        List<String> emails = Lists.newArrayList();
        for (int i = 0; i < 100; i++) {
            String emailAddress = faker.internet().safeEmailAddress("john");
            Assert.assertThat(emailAddress, Matchers.startsWith("john@"));
            Assert.assertThat(EmailValidator.getInstance().isValid(emailAddress), Matchers.is(true));
            emails.add(emailAddress);
        }
        final String safeDomain = faker.resolve("internet.safe_email");
        Assert.assertThat(("Should find at least one email from " + safeDomain), emails, Matchers.hasItem(Matchers.endsWith(("@" + safeDomain))));
    }

    @Test
    public void testEmailAddressDoesNotIncludeAccentsInTheLocalPart() {
        String emailAddress = faker.internet().emailAddress("?????");
        Assert.assertThat(emailAddress, Matchers.startsWith("aeiou@"));
    }

    @Test
    public void testSafeEmailAddressDoesNotIncludeAccentsInTheLocalPart() {
        String emailAddress = faker.internet().safeEmailAddress("?????");
        Assert.assertThat(emailAddress, Matchers.startsWith("aeiou@"));
    }

    @Test
    public void testUrl() {
        Assert.assertThat(faker.internet().url(), MatchesRegularExpression.matchesRegularExpression("www\\.(\\w|-)+\\.\\w+"));
    }

    @Test
    public void testAvatar() {
        Assert.assertThat(faker.internet().avatar(), MatchesRegularExpression.matchesRegularExpression("http.*/[^/]+/128.jpg$"));
    }

    @Test
    public void testImage() {
        String imageUrl = faker.internet().image();
        Assert.assertThat(imageUrl, MatchesRegularExpression.matchesRegularExpression("^http:\\/\\/lorempixel\\.com(/g)?/\\d{1,4}/\\d{1,4}/\\w+/$"));
    }

    @Test
    public void testDomainName() {
        Assert.assertThat(faker.internet().domainName(), MatchesRegularExpression.matchesRegularExpression("[a-z]+\\.\\w{2,4}"));
    }

    @Test
    public void testDomainWord() {
        Assert.assertThat(faker.internet().domainWord(), MatchesRegularExpression.matchesRegularExpression("[a-z]+"));
    }

    @Test
    public void testDomainSuffix() {
        Assert.assertThat(faker.internet().domainSuffix(), MatchesRegularExpression.matchesRegularExpression("\\w{2,4}"));
    }

    @Test
    public void testImageWithExplicitParams() {
        String imageUrl = faker.internet().image(800, 600, false, "bugs");
        Assert.assertThat(imageUrl, MatchesRegularExpression.matchesRegularExpression("^http:\\/\\/lorempixel\\.com/800/600/\\w+/bugs$"));
    }

    @Test
    public void testPassword() {
        Assert.assertThat(faker.internet().password(), MatchesRegularExpression.matchesRegularExpression("[a-z\\d]{8,16}"));
    }

    @Test
    public void testPasswordMinLengthMaxLength() {
        Assert.assertThat(faker.internet().password(10, 25), MatchesRegularExpression.matchesRegularExpression("[a-z\\d]{10,25}"));
    }

    @Test
    public void testPasswordMinLengthMaxLengthIncludeUpperCase() {
        Assert.assertThat(faker.internet().password(1, 2, false), MatchesRegularExpression.matchesRegularExpression("[a-z\\d]{1,2}"));
        Assert.assertThat(faker.internet().password(10, 25, true), MatchesRegularExpression.matchesRegularExpression("[a-zA-Z\\d]{10,25}"));
    }

    @Test
    public void testPasswordMinLengthMaxLengthIncludeUpperCaseIncludeSpecial() {
        Assert.assertThat(faker.internet().password(10, 25, false, false), MatchesRegularExpression.matchesRegularExpression("[a-z\\d]{10,25}"));
        Assert.assertThat(faker.internet().password(10, 25, false, true), MatchesRegularExpression.matchesRegularExpression("[a-z\\d!@#$%^&*]{10,25}"));
        Assert.assertThat(faker.internet().password(10, 25, true, true), MatchesRegularExpression.matchesRegularExpression("[a-zA-Z\\d!@#$%^&*]{10,25}"));
    }

    @Test
    public void testMacAddress() {
        Assert.assertThat(faker.internet().macAddress(), CountOfCharactersMatcher.countOf(':', Matchers.is(5)));
        Assert.assertThat(faker.internet().macAddress(""), CountOfCharactersMatcher.countOf(':', Matchers.is(5)));
        Assert.assertThat(faker.internet().macAddress("fa:fa:fa"), Matchers.startsWith("fa:fa:fa"));
        Assert.assertThat(faker.internet().macAddress("fa:fa:fa"), CountOfCharactersMatcher.countOf(':', Matchers.is(5)));
        Assert.assertThat(faker.internet().macAddress("01:02"), Matchers.startsWith("01:02"));
        Assert.assertThat(faker.internet().macAddress("01:02"), CountOfCharactersMatcher.countOf(':', Matchers.is(5)));
        // loop through 1000 times just to 'run it through the wringer'
        for (int i = 0; i < 1000; i++) {
            Assert.assertThat("Is valid mac format", faker.internet().macAddress(), MatchesRegularExpression.matchesRegularExpression("[0-9a-fA-F]{2}(\\:([0-9a-fA-F]{1,4})){5}"));
        }
    }

    @Test
    public void testIpV4Address() {
        Assert.assertThat(faker.internet().ipV4Address(), CountOfCharactersMatcher.countOf('.', Matchers.is(3)));
        for (int i = 0; i < 100; i++) {
            final String[] octets = faker.internet().ipV4Address().split("\\.");
            Assert.assertThat("first octet is 1-255", Integer.parseInt(octets[0]), Matchers.both(Matchers.greaterThan(0)).and(Matchers.lessThanOrEqualTo(255)));
            Assert.assertThat("second octet is 0-255", Integer.parseInt(octets[1]), Matchers.both(Matchers.greaterThanOrEqualTo(0)).and(Matchers.lessThanOrEqualTo(255)));
            Assert.assertThat("second octet is 0-255", Integer.parseInt(octets[2]), Matchers.both(Matchers.greaterThanOrEqualTo(0)).and(Matchers.lessThanOrEqualTo(255)));
            Assert.assertThat("second octet is 0-255", Integer.parseInt(octets[3]), Matchers.both(Matchers.greaterThanOrEqualTo(0)).and(Matchers.lessThanOrEqualTo(255)));
        }
    }

    @Test
    public void testIpV4Cidr() {
        Assert.assertThat(faker.internet().ipV4Cidr(), CountOfCharactersMatcher.countOf('.', Matchers.is(3)));
        Assert.assertThat(faker.internet().ipV4Cidr(), CountOfCharactersMatcher.countOf('/', Matchers.is(1)));
        for (int i = 0; i < 1000; i++) {
            Assert.assertThat(Integer.parseInt(faker.internet().ipV4Cidr().split("\\/")[1]), Matchers.both(Matchers.greaterThanOrEqualTo(1)).and(Matchers.lessThan(32)));
        }
    }

    @Test
    public void testPrivateIpV4Address() {
        String tenDot = "^10\\..+";
        String oneTwoSeven = "^127\\..+";
        String oneSixNine = "^169\\.254\\..+";
        String oneNineTwo = "^192\\.168\\..+";
        String oneSevenTwo = "^172\\.(16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31)\\..+";
        for (int i = 0; i < 1000; i++) {
            String addr = faker.internet().privateIpV4Address();
            Assert.assertThat(addr, Matchers.anyOf(MatchesRegularExpression.matchesRegularExpression(tenDot), MatchesRegularExpression.matchesRegularExpression(oneTwoSeven), MatchesRegularExpression.matchesRegularExpression(oneSixNine), MatchesRegularExpression.matchesRegularExpression(oneNineTwo), MatchesRegularExpression.matchesRegularExpression(oneSevenTwo)));
        }
    }

    @Test
    public void testPublicIpV4Address() {
        String tenDot = "^10\\.";
        String oneTwoSeven = "^127\\.";
        String oneSixNine = "^169\\.254";
        String oneNineTwo = "^192\\.168\\.";
        String oneSevenTwo = "^172\\.(16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31)\\.";
        for (int i = 0; i < 1000; i++) {
            String addr = faker.internet().publicIpV4Address();
            Assert.assertThat(addr.matches(tenDot), Matchers.is(false));
            Assert.assertThat(addr.matches(oneTwoSeven), Matchers.is(false));
            Assert.assertThat(addr.matches(oneSixNine), Matchers.is(false));
            Assert.assertThat(addr.matches(oneNineTwo), Matchers.is(false));
            Assert.assertThat(addr.matches(oneSevenTwo), Matchers.is(false));
        }
    }

    @Test
    public void testIpV6() {
        Assert.assertThat(faker.internet().ipV6Address(), CountOfCharactersMatcher.countOf(':', Matchers.is(7)));
        for (int i = 0; i < 1000; i++) {
            Assert.assertThat("Is valid ipv6 format", faker.internet().ipV6Address(), MatchesRegularExpression.matchesRegularExpression("[0-9a-fA-F]{1,4}(\\:([0-9a-fA-F]{1,4})){1,7}"));
        }
    }

    @Test
    public void testIpV6Cidr() {
        Assert.assertThat(faker.internet().ipV6Cidr(), CountOfCharactersMatcher.countOf(':', Matchers.is(7)));
        Assert.assertThat(faker.internet().ipV6Cidr(), CountOfCharactersMatcher.countOf('/', Matchers.is(1)));
        for (int i = 0; i < 1000; i++) {
            Assert.assertThat(Integer.parseInt(faker.internet().ipV6Cidr().split("\\/")[1]), Matchers.both(Matchers.greaterThanOrEqualTo(1)).and(Matchers.lessThan(128)));
        }
    }

    @Test
    @Repeat(times = 10)
    public void testSlugWithParams() {
        Assert.assertThat(faker.internet().slug(ImmutableList.of("a", "b"), "-"), MatchesRegularExpression.matchesRegularExpression("[a-zA-Z]+\\-[a-zA-Z]+"));
    }

    @Test
    @Repeat(times = 10)
    public void testSlug() {
        Assert.assertThat(faker.internet().slug(), MatchesRegularExpression.matchesRegularExpression("[a-zA-Z]+\\_[a-zA-Z]+"));
    }

    @Test
    @Repeat(times = 10)
    public void testUuid() {
        Assert.assertThat(faker.internet().uuid(), MatchesRegularExpression.matchesRegularExpression("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"));
    }

    @Test
    @Repeat(times = 100)
    public void testFarsiIDNs() {
        // in this case, we're just making sure Farsi doesn't blow up.
        // there have been issues with Farsi not being produced.
        final Faker f = new Faker(new Locale("fa"));
        Assert.assertThat(f.internet().domainName(), Matchers.not(Matchers.isEmptyOrNullString()));
        Assert.assertThat(f.internet().emailAddress(), Matchers.not(Matchers.isEmptyOrNullString()));
        Assert.assertThat(f.internet().safeEmailAddress(), Matchers.not(Matchers.isEmptyOrNullString()));
        Assert.assertThat(f.internet().url(), Matchers.not(Matchers.isEmptyOrNullString()));
    }

    @Test
    public void testUserAgent() {
        Internet[] agents = UserAgent.values();
        for (Internet.UserAgent agent : agents) {
            Assert.assertThat(faker.internet().userAgent(agent), Matchers.not(Matchers.isEmptyOrNullString()));
        }
        // Test faker.internet().userAgentAny() for random user_agent retrieval.
        Assert.assertThat(faker.internet().userAgentAny(), Matchers.not(Matchers.isEmptyOrNullString()));
    }
}

