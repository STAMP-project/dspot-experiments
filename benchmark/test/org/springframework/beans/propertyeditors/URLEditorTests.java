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
import java.net.URL;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.ClassUtils;


/**
 *
 *
 * @author Rick Evans
 * @author Chris Beams
 */
public class URLEditorTests {
    @Test(expected = IllegalArgumentException.class)
    public void testCtorWithNullResourceEditor() throws Exception {
        new URLEditor(null);
    }

    @Test
    public void testStandardURI() throws Exception {
        PropertyEditor urlEditor = new URLEditor();
        urlEditor.setAsText("mailto:juergen.hoeller@interface21.com");
        Object value = urlEditor.getValue();
        Assert.assertTrue((value instanceof URL));
        URL url = ((URL) (value));
        Assert.assertEquals(url.toExternalForm(), urlEditor.getAsText());
    }

    @Test
    public void testStandardURL() throws Exception {
        PropertyEditor urlEditor = new URLEditor();
        urlEditor.setAsText("http://www.springframework.org");
        Object value = urlEditor.getValue();
        Assert.assertTrue((value instanceof URL));
        URL url = ((URL) (value));
        Assert.assertEquals(url.toExternalForm(), urlEditor.getAsText());
    }

    @Test
    public void testClasspathURL() throws Exception {
        PropertyEditor urlEditor = new URLEditor();
        urlEditor.setAsText((((("classpath:" + (ClassUtils.classPackageAsResourcePath(getClass()))) + "/") + (ClassUtils.getShortName(getClass()))) + ".class"));
        Object value = urlEditor.getValue();
        Assert.assertTrue((value instanceof URL));
        URL url = ((URL) (value));
        Assert.assertEquals(url.toExternalForm(), urlEditor.getAsText());
        Assert.assertTrue((!(url.getProtocol().startsWith("classpath"))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithNonExistentResource() throws Exception {
        PropertyEditor urlEditor = new URLEditor();
        urlEditor.setAsText("gonna:/freak/in/the/morning/freak/in/the.evening");
    }

    @Test
    public void testSetAsTextWithNull() throws Exception {
        PropertyEditor urlEditor = new URLEditor();
        urlEditor.setAsText(null);
        Assert.assertNull(urlEditor.getValue());
        Assert.assertEquals("", urlEditor.getAsText());
    }

    @Test
    public void testGetAsTextReturnsEmptyStringIfValueNotSet() throws Exception {
        PropertyEditor urlEditor = new URLEditor();
        Assert.assertEquals("", urlEditor.getAsText());
    }
}

