package com.github.javafaker;


import com.github.javafaker.matchers.MatchesRegularExpression;
import org.junit.Assert;
import org.junit.Test;


public class EducatorTest extends AbstractFakerTest {
    @Test
    public void testUniversity() {
        Assert.assertThat(faker.educator().university(), MatchesRegularExpression.matchesRegularExpression("(\\w+ ?){2,3}"));
    }

    @Test
    public void testCourse() {
        Assert.assertThat(faker.educator().course(), MatchesRegularExpression.matchesRegularExpression("(\\(?\\w+\\)? ?){3,6}"));
    }

    @Test
    public void testSecondarySchool() {
        Assert.assertThat(faker.educator().secondarySchool(), MatchesRegularExpression.matchesRegularExpression("(\\w+ ?){2,3}"));
    }

    @Test
    public void testCampus() {
        Assert.assertThat(faker.educator().campus(), MatchesRegularExpression.matchesRegularExpression("(\\w+ ?){1,2}"));
    }
}

