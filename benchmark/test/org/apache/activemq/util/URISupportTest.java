/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.util;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.apache.activemq.util.URISupport.CompositeData;
import org.junit.Assert;
import org.junit.Test;


public class URISupportTest {
    @Test
    public void testEmptyCompositePath() throws Exception {
        CompositeData data = URISupport.parseComposite(new URI("broker:()/localhost?persistent=false"));
        Assert.assertEquals(0, data.getComponents().length);
    }

    @Test
    public void testCompositePath() throws Exception {
        CompositeData data = URISupport.parseComposite(new URI("test:(path)/path"));
        Assert.assertEquals("path", data.getPath());
        data = URISupport.parseComposite(new URI("test:path"));
        Assert.assertNull(data.getPath());
    }

    @Test
    public void testSimpleComposite() throws Exception {
        CompositeData data = URISupport.parseComposite(new URI("test:part1"));
        Assert.assertEquals(1, data.getComponents().length);
    }

    @Test
    public void testComposite() throws Exception {
        URI uri = new URI("test:(part1://host,part2://(sub1://part,sube2:part))");
        CompositeData data = URISupport.parseComposite(uri);
        Assert.assertEquals(2, data.getComponents().length);
    }

    @Test
    public void testEmptyCompositeWithParenthesisInParam() throws Exception {
        URI uri = new URI("failover://()?updateURIsURL=file:/C:/Dir(1)/a.csv");
        CompositeData data = URISupport.parseComposite(uri);
        Assert.assertEquals(0, data.getComponents().length);
        Assert.assertEquals(1, data.getParameters().size());
        Assert.assertTrue(data.getParameters().containsKey("updateURIsURL"));
        Assert.assertEquals("file:/C:/Dir(1)/a.csv", data.getParameters().get("updateURIsURL"));
    }

    @Test
    public void testCompositeWithParenthesisInParam() throws Exception {
        URI uri = new URI("failover://(test)?updateURIsURL=file:/C:/Dir(1)/a.csv");
        CompositeData data = URISupport.parseComposite(uri);
        Assert.assertEquals(1, data.getComponents().length);
        Assert.assertEquals(1, data.getParameters().size());
        Assert.assertTrue(data.getParameters().containsKey("updateURIsURL"));
        Assert.assertEquals("file:/C:/Dir(1)/a.csv", data.getParameters().get("updateURIsURL"));
    }

    @Test
    public void testCompositeWithComponentParam() throws Exception {
        CompositeData data = URISupport.parseComposite(new URI("test:(part1://host?part1=true)?outside=true"));
        Assert.assertEquals(1, data.getComponents().length);
        Assert.assertEquals(1, data.getParameters().size());
        Map<String, String> part1Params = URISupport.parseParameters(data.getComponents()[0]);
        Assert.assertEquals(1, part1Params.size());
        Assert.assertTrue(part1Params.containsKey("part1"));
    }

    @Test
    public void testParsingURI() throws Exception {
        URI source = new URI("tcp://localhost:61626/foo/bar?cheese=Edam&x=123");
        Map<String, String> map = URISupport.parseParameters(source);
        Assert.assertEquals(("Size: " + map), 2, map.size());
        assertMapKey(map, "cheese", "Edam");
        assertMapKey(map, "x", "123");
        URI result = URISupport.removeQuery(source);
        Assert.assertEquals("result", new URI("tcp://localhost:61626/foo/bar"), result);
    }

    @Test
    public void testParsingCompositeURI() throws URISyntaxException {
        CompositeData data = URISupport.parseComposite(new URI("broker://(tcp://localhost:61616)?name=foo"));
        Assert.assertEquals("one component", 1, data.getComponents().length);
        Assert.assertEquals(("Size: " + (data.getParameters())), 1, data.getParameters().size());
    }

    @Test
    public void testCheckParenthesis() throws Exception {
        String str = "fred:(((ddd))";
        Assert.assertFalse(URISupport.checkParenthesis(str));
        str += ")";
        Assert.assertTrue(URISupport.checkParenthesis(str));
    }

    @Test
    public void testCreateWithQuery() throws Exception {
        URI source = new URI("vm://localhost");
        URI dest = URISupport.createURIWithQuery(source, "network=true&one=two");
        Assert.assertEquals("correct param count", 2, URISupport.parseParameters(dest).size());
        Assert.assertEquals("same uri, host", source.getHost(), dest.getHost());
        Assert.assertEquals("same uri, scheme", source.getScheme(), dest.getScheme());
        Assert.assertFalse("same uri, ssp", dest.getQuery().equals(source.getQuery()));
    }

    @Test
    public void testParsingParams() throws Exception {
        URI uri = new URI("static:(http://localhost:61617?proxyHost=jo&proxyPort=90)?proxyHost=localhost&proxyPort=80");
        Map<String, String> parameters = URISupport.parseParameters(uri);
        verifyParams(parameters);
        uri = new URI("static://http://localhost:61617?proxyHost=localhost&proxyPort=80");
        parameters = URISupport.parseParameters(uri);
        verifyParams(parameters);
        uri = new URI("http://0.0.0.0:61616");
        parameters = URISupport.parseParameters(uri);
    }

    @Test
    public void testCompositeCreateURIWithQuery() throws Exception {
        String queryString = "query=value";
        URI originalURI = new URI("outerscheme:(innerscheme:innerssp)");
        URI querylessURI = originalURI;
        Assert.assertEquals(querylessURI, URISupport.createURIWithQuery(originalURI, null));
        Assert.assertEquals(querylessURI, URISupport.createURIWithQuery(originalURI, ""));
        Assert.assertEquals(new URI(((querylessURI + "?") + queryString)), URISupport.createURIWithQuery(originalURI, queryString));
        originalURI = new URI("outerscheme:(innerscheme:innerssp)?outerquery=0");
        Assert.assertEquals(querylessURI, URISupport.createURIWithQuery(originalURI, null));
        Assert.assertEquals(querylessURI, URISupport.createURIWithQuery(originalURI, ""));
        Assert.assertEquals(new URI(((querylessURI + "?") + queryString)), URISupport.createURIWithQuery(originalURI, queryString));
        originalURI = new URI("outerscheme:(innerscheme:innerssp?innerquery=0)");
        querylessURI = originalURI;
        Assert.assertEquals(querylessURI, URISupport.createURIWithQuery(originalURI, null));
        Assert.assertEquals(querylessURI, URISupport.createURIWithQuery(originalURI, ""));
        Assert.assertEquals(new URI(((querylessURI + "?") + queryString)), URISupport.createURIWithQuery(originalURI, queryString));
        originalURI = new URI("outerscheme:(innerscheme:innerssp?innerquery=0)?outerquery=0");
        Assert.assertEquals(querylessURI, URISupport.createURIWithQuery(originalURI, null));
        Assert.assertEquals(querylessURI, URISupport.createURIWithQuery(originalURI, ""));
        Assert.assertEquals(new URI(((querylessURI + "?") + queryString)), URISupport.createURIWithQuery(originalURI, queryString));
    }

    @Test
    public void testApplyParameters() throws Exception {
        URI uri = new URI("http://0.0.0.0:61616");
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("t.proxyHost", "localhost");
        parameters.put("t.proxyPort", "80");
        uri = URISupport.applyParameters(uri, parameters);
        Map<String, String> appliedParameters = URISupport.parseParameters(uri);
        Assert.assertEquals("all params applied  with no prefix", 2, appliedParameters.size());
        // strip off params again
        uri = URISupport.createURIWithQuery(uri, null);
        uri = URISupport.applyParameters(uri, parameters, "joe");
        appliedParameters = URISupport.parseParameters(uri);
        Assert.assertTrue("no params applied as none match joe", appliedParameters.isEmpty());
        uri = URISupport.applyParameters(uri, parameters, "t.");
        verifyParams(URISupport.parseParameters(uri));
    }

    @Test
    public void testIsCompositeURIWithQueryNoSlashes() throws URISyntaxException {
        URI[] compositeURIs = new URI[]{ new URI("test:(part1://host?part1=true)?outside=true"), new URI("broker:(tcp://localhost:61616)?name=foo") };
        for (URI uri : compositeURIs) {
            Assert.assertTrue((uri + " must be detected as composite URI"), URISupport.isCompositeURI(uri));
        }
    }

    @Test
    public void testIsCompositeURIWithQueryAndSlashes() throws URISyntaxException {
        URI[] compositeURIs = new URI[]{ new URI("test://(part1://host?part1=true)?outside=true"), new URI("broker://(tcp://localhost:61616)?name=foo") };
        for (URI uri : compositeURIs) {
            Assert.assertTrue((uri + " must be detected as composite URI"), URISupport.isCompositeURI(uri));
        }
    }

    @Test
    public void testIsCompositeURINoQueryNoSlashes() throws URISyntaxException {
        URI[] compositeURIs = new URI[]{ new URI("test:(part1://host,part2://(sub1://part,sube2:part))"), new URI("test:(path)/path") };
        for (URI uri : compositeURIs) {
            Assert.assertTrue((uri + " must be detected as composite URI"), URISupport.isCompositeURI(uri));
        }
    }

    @Test
    public void testIsCompositeURINoQueryNoSlashesNoParentheses() throws URISyntaxException {
        Assert.assertFalse(("test:part1" + " must be detected as non-composite URI"), URISupport.isCompositeURI(new URI("test:part1")));
    }

    @Test
    public void testIsCompositeURINoQueryWithSlashes() throws URISyntaxException {
        URI[] compositeURIs = new URI[]{ new URI("failover://(tcp://bla:61616,tcp://bla:61617)"), new URI("failover://(tcp://localhost:61616,ssl://anotherhost:61617)") };
        for (URI uri : compositeURIs) {
            Assert.assertTrue((uri + " must be detected as composite URI"), URISupport.isCompositeURI(uri));
        }
    }
}

