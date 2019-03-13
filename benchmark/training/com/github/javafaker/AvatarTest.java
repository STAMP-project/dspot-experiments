package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import com.github.javafaker.repeating.Repeat;
import org.junit.Assert;
import org.junit.Test;


public class AvatarTest extends AbstractFakerTest {
    @Test
    @Repeat(times = 10)
    public void testAvatar() {
        String avatar = faker.avatar().image();
        Assert.assertThat(avatar, MatchesRegularExpression.matchesRegularExpression("^https:\\/\\/s3.amazonaws\\.com\\/uifaces\\/faces\\/twitter\\/[a-zA-Z0-9_]+\\/128\\.jpg$"));
    }
}

