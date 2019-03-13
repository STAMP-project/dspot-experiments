package com.auth0.jwt;


import com.auth0.jwt.exceptions.JWTDecodeException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class TokenUtilsTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldSplitToken() throws Exception {
        String token = "eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJpc3MiOiJhdXRoMCJ9.W1mx_Y0hbAMbPmfW9whT605AAcxB7REFuJiDAHk2Sdc";
        String[] parts = TokenUtils.splitToken(token);
        Assert.assertThat(parts, is(notNullValue()));
        Assert.assertThat(parts, is(arrayWithSize(3)));
        Assert.assertThat(parts[0], is("eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0"));
        Assert.assertThat(parts[1], is("eyJpc3MiOiJhdXRoMCJ9"));
        Assert.assertThat(parts[2], is("W1mx_Y0hbAMbPmfW9whT605AAcxB7REFuJiDAHk2Sdc"));
    }

    @Test
    public void shouldSplitTokenWithEmptySignature() throws Exception {
        String token = "eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJpc3MiOiJhdXRoMCJ9.";
        String[] parts = TokenUtils.splitToken(token);
        Assert.assertThat(parts, is(notNullValue()));
        Assert.assertThat(parts, is(arrayWithSize(3)));
        Assert.assertThat(parts[0], is("eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0"));
        Assert.assertThat(parts[1], is("eyJpc3MiOiJhdXRoMCJ9"));
        Assert.assertThat(parts[2], is(isEmptyString()));
    }

    @Test
    public void shouldThrowOnSplitTokenWithMoreThan3Parts() throws Exception {
        exception.expect(JWTDecodeException.class);
        exception.expectMessage("The token was expected to have 3 parts, but got 4.");
        String token = "this.has.four.parts";
        TokenUtils.splitToken(token);
    }

    @Test
    public void shouldThrowOnSplitTokenWithLessThan3Parts() throws Exception {
        exception.expect(JWTDecodeException.class);
        exception.expectMessage("The token was expected to have 3 parts, but got 2.");
        String token = "two.parts";
        TokenUtils.splitToken(token);
    }
}

