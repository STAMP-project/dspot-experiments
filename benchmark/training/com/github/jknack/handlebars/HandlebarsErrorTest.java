package com.github.jknack.handlebars;


import org.junit.Assert;
import org.junit.Test;


public class HandlebarsErrorTest {
    @Test
    public void properties() {
        HandlebarsError hbsError = new HandlebarsError("filename", 1, 3, "reason", "evidence", "message");
        Assert.assertEquals("filename", hbsError.filename);
        Assert.assertEquals(1, hbsError.line);
        Assert.assertEquals(3, hbsError.column);
        Assert.assertEquals("reason", hbsError.reason);
        Assert.assertEquals("evidence", hbsError.evidence);
        Assert.assertEquals("message", hbsError.message);
    }
}

