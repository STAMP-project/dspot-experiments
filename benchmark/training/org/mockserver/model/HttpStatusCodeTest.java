package org.mockserver.model;


import HttpStatusCode.BAD_GATEWAY_502;
import HttpStatusCode.FOUND_302;
import HttpStatusCode.INSUFFICIENT_STORAGE_507;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author jamesdbloom
 */
public class HttpStatusCodeTest {
    @Test
    public void shouldFindEnumForCode() {
        Assert.assertEquals(FOUND_302, HttpStatusCode.code(302));
        Assert.assertEquals(BAD_GATEWAY_502, HttpStatusCode.code(502));
        Assert.assertEquals(INSUFFICIENT_STORAGE_507, HttpStatusCode.code(507));
        Assert.assertNull(HttpStatusCode.code(600));
    }

    @Test
    public void shouldReturnCorrectValues() {
        Assert.assertEquals(FOUND_302.reasonPhrase(), "Moved Temporarily");
        Assert.assertEquals(FOUND_302.code(), 302);
    }
}

