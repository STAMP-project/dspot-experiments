/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.beans.propertyeditors;


import java.beans.PropertyEditor;
import java.net.URI;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.ClassUtils;


/**
 *
 *
 * @author Juergen Hoeller
 * @author Arjen Poutsma
 */
public class URIEditorTests {
    @Test
    public void standardURI() throws Exception {
        doTestURI("mailto:juergen.hoeller@interface21.com");
    }

    @Test
    public void withNonExistentResource() throws Exception {
        doTestURI("gonna:/freak/in/the/morning/freak/in/the.evening");
    }

    @Test
    public void standardURL() throws Exception {
        doTestURI("http://www.springframework.org");
    }

    @Test
    public void standardURLWithFragment() throws Exception {
        doTestURI("http://www.springframework.org#1");
    }

    @Test
    public void standardURLWithWhitespace() throws Exception {
        PropertyEditor uriEditor = new URIEditor();
        uriEditor.setAsText("  http://www.springframework.org  ");
        Object value = uriEditor.getValue();
        Assert.assertTrue((value instanceof URI));
        URI uri = ((URI) (value));
        Assert.assertEquals("http://www.springframework.org", uri.toString());
    }

    @Test
    public void classpathURL() throws Exception {
        PropertyEditor uriEditor = new URIEditor(getClass().getClassLoader());
        uriEditor.setAsText((((("classpath:" + (ClassUtils.classPackageAsResourcePath(getClass()))) + "/") + (ClassUtils.getShortName(getClass()))) + ".class"));
        Object value = uriEditor.getValue();
        Assert.assertTrue((value instanceof URI));
        URI uri = ((URI) (value));
        Assert.assertEquals(uri.toString(), uriEditor.getAsText());
        Assert.assertTrue((!(uri.getScheme().startsWith("classpath"))));
    }

    @Test
    public void classpathURLWithWhitespace() throws Exception {
        PropertyEditor uriEditor = new URIEditor(getClass().getClassLoader());
        uriEditor.setAsText((((("  classpath:" + (ClassUtils.classPackageAsResourcePath(getClass()))) + "/") + (ClassUtils.getShortName(getClass()))) + ".class  "));
        Object value = uriEditor.getValue();
        Assert.assertTrue((value instanceof URI));
        URI uri = ((URI) (value));
        Assert.assertEquals(uri.toString(), uriEditor.getAsText());
        Assert.assertTrue((!(uri.getScheme().startsWith("classpath"))));
    }

    @Test
    public void classpathURLAsIs() throws Exception {
        PropertyEditor uriEditor = new URIEditor();
        uriEditor.setAsText("classpath:test.txt");
        Object value = uriEditor.getValue();
        Assert.assertTrue((value instanceof URI));
        URI uri = ((URI) (value));
        Assert.assertEquals(uri.toString(), uriEditor.getAsText());
        Assert.assertTrue(uri.getScheme().startsWith("classpath"));
    }

    @Test
    public void setAsTextWithNull() throws Exception {
        PropertyEditor uriEditor = new URIEditor();
        uriEditor.setAsText(null);
        Assert.assertNull(uriEditor.getValue());
        Assert.assertEquals("", uriEditor.getAsText());
    }

    @Test
    public void getAsTextReturnsEmptyStringIfValueNotSet() throws Exception {
        PropertyEditor uriEditor = new URIEditor();
        Assert.assertEquals("", uriEditor.getAsText());
    }

    @Test
    public void encodeURI() throws Exception {
        PropertyEditor uriEditor = new URIEditor();
        uriEditor.setAsText("http://example.com/spaces and \u20ac");
        Object value = uriEditor.getValue();
        Assert.assertTrue((value instanceof URI));
        URI uri = ((URI) (value));
        Assert.assertEquals(uri.toString(), uriEditor.getAsText());
        Assert.assertEquals("http://example.com/spaces%20and%20%E2%82%AC", uri.toASCIIString());
    }

    @Test
    public void encodeAlreadyEncodedURI() throws Exception {
        PropertyEditor uriEditor = new URIEditor(false);
        uriEditor.setAsText("http://example.com/spaces%20and%20%E2%82%AC");
        Object value = uriEditor.getValue();
        Assert.assertTrue((value instanceof URI));
        URI uri = ((URI) (value));
        Assert.assertEquals(uri.toString(), uriEditor.getAsText());
        Assert.assertEquals("http://example.com/spaces%20and%20%E2%82%AC", uri.toASCIIString());
    }
}

