/**
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.xml;


import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;


/**
 * Tests that we both report and retrieve attributes using the appropriate
 * names for different combinations of namespaces and namespace prefixes.
 */
public class NamespacedAttributesLookupTest extends TestCase {
    private static final String SAX_PROPERTY_NS = "http://xml.org/sax/features/namespaces";

    private static final String SAX_PROPERTY_NS_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";

    private static String xml = "<?xml version='1.0' encoding='UTF-8'?>" + ((("<test xmlns='http://foo' xmlns:bar='http://bar' xmlns:baz='http://baz' baz:c='a'>" + "<b c='w' bar:c='x'/>") + "<bar:e baz:c='y' bar:c='z'/>") + "</test>");

    public void testNamespace() throws Exception {
        List<String> expected = Arrays.asList(("http://foo,test\n" + (("  http://baz,c\n" + "  http://bar+c=null,\n") + "  bar:c=null\n")), ("http://foo,b\n" + ((("  ,c\n" + "  http://bar,c\n") + "  http://bar+c=x,\n") + "  bar:c=x\n")), ("http://bar,e\n" + ((("  http://baz,c\n" + "  http://bar,c\n") + "  http://bar+c=z,\n") + "  bar:c=z\n")));
        boolean namespace = true;
        boolean namespacePrefixes = false;
        TestCase.assertEquals(expected, getStartElements(NamespacedAttributesLookupTest.xml, namespace, namespacePrefixes));
    }

    public void testNamespacePrefixes() throws Exception {
        List<String> expected = Arrays.asList(("test\n" + ((((("  xmlns\n" + "  xmlns:bar\n") + "  xmlns:baz\n") + "  baz:c\n") + "  http://bar+c=null,\n") + "  bar:c=null\n")), ("b\n" + ((("  c\n" + "  bar:c\n") + "  http://bar+c=null,\n") + "  bar:c=x\n")), ("bar:e\n" + ((("  baz:c\n" + "  bar:c\n") + "  http://bar+c=null,\n") + "  bar:c=z\n")));
        boolean namespace = false;
        boolean namespacePrefixes = true;
        TestCase.assertEquals(expected, getStartElements(NamespacedAttributesLookupTest.xml, namespace, namespacePrefixes));
    }
}

