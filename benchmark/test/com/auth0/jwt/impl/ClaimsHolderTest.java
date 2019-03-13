package com.auth0.jwt.impl;


import java.util.HashMap;
import java.util.Map;
import org.hamcrest.collection.IsMapContaining;
import org.junit.Assert;
import org.junit.Test;


public class ClaimsHolderTest {
    @SuppressWarnings("RedundantCast")
    @Test
    public void shouldGetClaims() throws Exception {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("iss", "auth0");
        ClaimsHolder holder = new ClaimsHolder(claims);
        Assert.assertThat(holder, is(notNullValue()));
        Assert.assertThat(holder.getClaims(), is(notNullValue()));
        Assert.assertThat(holder.getClaims(), is(instanceOf(Map.class)));
        Assert.assertThat(holder.getClaims(), is(IsMapContaining.hasEntry("iss", ((Object) ("auth0")))));
    }

    @Test
    public void shouldGetNotNullClaims() throws Exception {
        ClaimsHolder holder = new ClaimsHolder(null);
        Assert.assertThat(holder, is(notNullValue()));
        Assert.assertThat(holder.getClaims(), is(notNullValue()));
        Assert.assertThat(holder.getClaims(), is(instanceOf(Map.class)));
    }
}

