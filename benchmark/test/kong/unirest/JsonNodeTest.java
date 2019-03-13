/**
 * The MIT License
 *
 * Copyright for portions of unirest-java are held by Kong Inc (c) 2013.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package kong.unirest;


import org.junit.Assert;
import org.junit.Test;


public class JsonNodeTest {
    @Test
    public void canParseARegularObject() {
        String json = "{\"foo\":\"bar\"}";
        JsonNode node = new JsonNode(json);
        Assert.assertEquals(false, node.isArray());
        Assert.assertEquals("bar", node.getObject().getString("foo"));
        Assert.assertEquals("bar", node.getArray().getJSONObject(0).getString("foo"));
        Assert.assertEquals(json, node.toString());
    }

    @Test
    public void canParseArrayObject() {
        String json = "[{\"foo\":\"bar\"}]";
        JsonNode node = new JsonNode(json);
        Assert.assertEquals(true, node.isArray());
        Assert.assertEquals("bar", node.getArray().getJSONObject(0).getString("foo"));
        Assert.assertEquals(null, node.getObject());
        Assert.assertEquals(json, node.toString());
    }

    @Test
    public void nullAndEmptyObjectsResultInEmptyJson() {
        Assert.assertEquals("{}", new JsonNode("").toString());
        Assert.assertEquals("{}", new JsonNode(null).toString());
    }
}

