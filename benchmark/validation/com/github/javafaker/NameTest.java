package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import com.github.javafaker.repeating.Repeat;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class NameTest extends AbstractFakerTest {
    @Test
    public void testName() {
        Assert.assertThat(faker.name().name(), MatchesRegularExpression.matchesRegularExpression("([\\w\']+\\.?( )?){2,3}"));
    }

    @Test
    public void testNameWithMiddle() {
        Assert.assertThat(faker.name().nameWithMiddle(), MatchesRegularExpression.matchesRegularExpression("([\\w\']+\\.?( )?){3,4}"));
    }

    @Test
    @Repeat(times = 10)
    public void testNameWithMiddleDoesNotHaveRepeatedName() {
        String nameWithMiddle = faker.name().nameWithMiddle();
        String[] splitNames = nameWithMiddle.split(" ");
        String firstName = splitNames[0];
        String middleName = splitNames[1];
        Assert.assertThat(firstName, IsNot.not(IsEqual.equalTo(middleName)));
    }

    @Test
    public void testFullName() {
        Assert.assertThat(faker.name().fullName(), MatchesRegularExpression.matchesRegularExpression("([\\w\']+\\.?( )?){2,4}"));
    }

    @Test
    public void testFirstName() {
        Assert.assertThat(faker.name().firstName(), MatchesRegularExpression.matchesRegularExpression("\\w+"));
    }

    @Test
    public void testLastName() {
        Assert.assertThat(faker.name().lastName(), MatchesRegularExpression.matchesRegularExpression("[A-Za-z']+"));
    }

    @Test
    public void testPrefix() {
        Assert.assertThat(faker.name().prefix(), MatchesRegularExpression.matchesRegularExpression("\\w+\\.?"));
    }

    @Test
    public void testSuffix() {
        Assert.assertThat(faker.name().suffix(), MatchesRegularExpression.matchesRegularExpression("\\w+\\.?"));
    }

    @Test
    public void testTitle() {
        Assert.assertThat(faker.name().title(), MatchesRegularExpression.matchesRegularExpression("(\\w+\\.?( )?){3}"));
    }

    @Test
    public void testUsername() {
        Assert.assertThat(faker.name().username(), MatchesRegularExpression.matchesRegularExpression("^(\\w+)\\.(\\w+)$"));
    }

    @Test
    public void testUsernameWithSpaces() {
        final Name name = Mockito.spy(new Name(faker));
        Mockito.doReturn("Compound Name").when(name).firstName();
        Mockito.doReturn(name).when(faker).name();
        Assert.assertThat(faker.name().username(), MatchesRegularExpression.matchesRegularExpression("^(\\w+)\\.(\\w+)$"));
    }
}

