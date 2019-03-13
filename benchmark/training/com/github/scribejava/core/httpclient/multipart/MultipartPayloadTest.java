package com.github.scribejava.core.httpclient.multipart;


import org.hamcrest.core.StringStartsWith;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class MultipartPayloadTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testValidCheckBoundarySyntax() {
        MultipartPayload.checkBoundarySyntax("0aA'()+_,-./:=?");
        MultipartPayload.checkBoundarySyntax("0aA'()+_,- ./:=?");
        MultipartPayload.checkBoundarySyntax(" 0aA'()+_,-./:=?");
        MultipartPayload.checkBoundarySyntax("1234567890123456789012345678901234567890123456789012345678901234567890");
    }

    @Test
    public void testNonValidLastWhiteSpaceCheckBoundarySyntax() {
        final String boundary = "0aA'()+_,-./:=? ";
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(StringStartsWith.startsWith((("{'boundary'='" + boundary) + "'} has invaid syntax. Should be '")));
        MultipartPayload.checkBoundarySyntax(boundary);
    }

    @Test
    public void testNonValidEmptyCheckBoundarySyntax() {
        final String boundary = "";
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(StringStartsWith.startsWith((("{'boundary'='" + boundary) + "'} has invaid syntax. Should be '")));
        MultipartPayload.checkBoundarySyntax(boundary);
    }

    @Test
    public void testNonValidIllegalSymbolCheckBoundarySyntax() {
        final String boundary = "0aA'()+_;,-./:=? ";
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(StringStartsWith.startsWith((("{'boundary'='" + boundary) + "'} has invaid syntax. Should be '")));
        MultipartPayload.checkBoundarySyntax(boundary);
    }

    @Test
    public void testNonValidTooLongCheckBoundarySyntax() {
        final String boundary = "12345678901234567890123456789012345678901234567890123456789012345678901";
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(StringStartsWith.startsWith((("{'boundary'='" + boundary) + "'} has invaid syntax. Should be '")));
        MultipartPayload.checkBoundarySyntax(boundary);
    }

    @Test
    public void testNonValidNullCheckBoundarySyntax() {
        final String boundary = null;
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(StringStartsWith.startsWith((("{'boundary'='" + boundary) + "'} has invaid syntax. Should be '")));
        MultipartPayload.checkBoundarySyntax(boundary);
    }

    @Test
    public void testParseBoundaryFromHeader() {
        Assert.assertNull(MultipartPayload.parseBoundaryFromHeader(null));
        Assert.assertEquals("0aA'()+_,-./:=?", MultipartPayload.parseBoundaryFromHeader("multipart/subtype; boundary=\"0aA\'()+_,-./:=?\""));
        Assert.assertEquals("0aA'()+_, -./:=?", MultipartPayload.parseBoundaryFromHeader("multipart/subtype; boundary=\"0aA\'()+_, -./:=?\""));
        Assert.assertEquals("0aA'()+_, -./:=?", MultipartPayload.parseBoundaryFromHeader("multipart/subtype; boundary=\"0aA\'()+_, -./:=? \""));
        Assert.assertEquals("0aA'()+_,-./:=?", MultipartPayload.parseBoundaryFromHeader("multipart/subtype; boundary=0aA'()+_,-./:=?"));
        Assert.assertEquals("0aA'()+_, -./:=?", MultipartPayload.parseBoundaryFromHeader("multipart/subtype; boundary=0aA'()+_, -./:=?"));
        Assert.assertEquals("0aA'()+_, -./:=?", MultipartPayload.parseBoundaryFromHeader("multipart/subtype; boundary=0aA'()+_, -./:=? "));
        Assert.assertEquals(" 0aA'()+_, -./:=?", MultipartPayload.parseBoundaryFromHeader("multipart/subtype; boundary= 0aA'()+_, -./:=?"));
        Assert.assertNull(MultipartPayload.parseBoundaryFromHeader("multipart/subtype; boundar=0aA'()+_, -./:=? "));
        Assert.assertNull(MultipartPayload.parseBoundaryFromHeader("multipart/subtype; "));
        Assert.assertNull(MultipartPayload.parseBoundaryFromHeader("multipart/subtype;"));
        Assert.assertNull(MultipartPayload.parseBoundaryFromHeader("multipart/subtype"));
        Assert.assertNull(MultipartPayload.parseBoundaryFromHeader("multipart/subtype; boundary="));
        Assert.assertEquals("0aA'()+_,", MultipartPayload.parseBoundaryFromHeader("multipart/subtype; boundary=0aA'()+_,; -./:=? "));
        Assert.assertEquals("0aA'()+_, -./:=?", MultipartPayload.parseBoundaryFromHeader("multipart/subtype; boundary=\"0aA\'()+_, -./:=?"));
        Assert.assertEquals("0aA'()+_, -./:=?", MultipartPayload.parseBoundaryFromHeader("multipart/subtype; boundary=0aA\'()+_, -./:=?\""));
        Assert.assertEquals("1234567890123456789012345678901234567890123456789012345678901234567890", MultipartPayload.parseBoundaryFromHeader(("multipart/subtype; " + "boundary=1234567890123456789012345678901234567890123456789012345678901234567890")));
        Assert.assertEquals("1234567890123456789012345678901234567890123456789012345678901234567890", MultipartPayload.parseBoundaryFromHeader(("multipart/subtype; " + "boundary=12345678901234567890123456789012345678901234567890123456789012345678901")));
        Assert.assertNull(MultipartPayload.parseBoundaryFromHeader("multipart/subtype; boundary="));
        Assert.assertNull(MultipartPayload.parseBoundaryFromHeader("multipart/subtype; boundary=\"\""));
        Assert.assertNull(MultipartPayload.parseBoundaryFromHeader("multipart/subtype; boundary=;123"));
        Assert.assertNull(MultipartPayload.parseBoundaryFromHeader("multipart/subtype; boundary=\"\"123"));
    }
}

