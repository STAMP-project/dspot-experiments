package com.auth0.jwt;


import com.auth0.jwt.interfaces.Clock;
import java.util.Date;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ClockImplTest {
    @Test
    public void shouldGetToday() throws Exception {
        Clock clock = new ClockImpl();
        Date clockToday = clock.getToday();
        Assert.assertThat(clockToday, Matchers.is(Matchers.notNullValue()));
    }
}

