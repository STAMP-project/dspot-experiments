/**
 * Copyright 2009-2012 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.parsing;


import java.io.InputStream;
import org.apache.ibatis.io.Resources;
import org.junit.Assert;
import org.junit.Test;


public class XPathParserTest {
    @Test
    public void shouldTestXPathParserMethods() throws Exception {
        String resource = "resources/nodelet_test.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        XPathParser parser = new XPathParser(inputStream, false, null, null);
        Assert.assertEquals(((Long) (1970L)), parser.evalLong("/employee/birth_date/year"));
        Assert.assertEquals(((short) (6)), ((short) (parser.evalShort("/employee/birth_date/month"))));
        Assert.assertEquals(((Integer) (15)), parser.evalInteger("/employee/birth_date/day"));
        Assert.assertEquals(((Float) (5.8F)), parser.evalFloat("/employee/height"));
        Assert.assertEquals(((Double) (5.8)), parser.evalDouble("/employee/height"));
        Assert.assertEquals("${id_var}", parser.evalString("/employee/@id"));
        Assert.assertEquals(Boolean.TRUE, parser.evalBoolean("/employee/active"));
        Assert.assertEquals("<id>${id_var}</id>", parser.evalNode("/employee/@id").toString().trim());
        Assert.assertEquals(7, parser.evalNodes("/employee/*").size());
        XNode node = parser.evalNode("/employee/height");
        Assert.assertEquals("employee/height", node.getPath());
        Assert.assertEquals("employee[${id_var}]_height", node.getValueBasedIdentifier());
    }
}

