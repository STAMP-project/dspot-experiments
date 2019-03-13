package org.baeldung.order;


import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


@RunWith(SpringJUnit4ClassRunner.class)
@org.springframework.test.context.ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class RatingRetrieverUnitTest {
    @Configuration
    @ComponentScan(basePackages = { "org.baeldung.order" })
    static class ContextConfiguration {}

    @Autowired
    private List<Rating> ratings;

    @Test
    public void givenOrderOnComponents_whenInjected_thenAutowireByOrderValue() {
        Assert.assertThat(ratings.get(0).getRating(), CoreMatchers.is(CoreMatchers.equalTo(1)));
        Assert.assertThat(ratings.get(1).getRating(), CoreMatchers.is(CoreMatchers.equalTo(2)));
        Assert.assertThat(ratings.get(2).getRating(), CoreMatchers.is(CoreMatchers.equalTo(3)));
    }
}

