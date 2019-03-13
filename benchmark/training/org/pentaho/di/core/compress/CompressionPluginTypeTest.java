/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.core.compress;


import java.lang.annotation.Annotation;
import org.junit.Assert;
import org.junit.Test;

import static CompressionPluginType.pluginType;


public class CompressionPluginTypeTest {
    @Test
    public void testGetInstance() {
        CompressionPluginType instance = CompressionPluginType.getInstance();
        CompressionPluginType instance2 = CompressionPluginType.getInstance();
        Assert.assertTrue((instance == instance2));
        Assert.assertNotNull(instance);
        pluginType = null;
        CompressionPluginType instance3 = CompressionPluginType.getInstance();
        Assert.assertFalse((instance == instance3));
    }

    @Test
    public void testGetPluginInfo() {
        CompressionPluginType instance = CompressionPluginType.getInstance();
        Annotation a = new CompressionPluginTypeTest.FakePlugin().getClass().getAnnotation(CompressionPlugin.class);
        Assert.assertNotNull(a);
        Assert.assertEquals("", instance.extractCategory(a));
        Assert.assertEquals("Fake", instance.extractID(a));
        Assert.assertEquals("FakePlugin", instance.extractName(a));
        Assert.assertEquals("", instance.extractCasesUrl(a));
        Assert.assertEquals("Compression Plugin", instance.extractDesc(a));
        Assert.assertEquals("", instance.extractDocumentationUrl(a));
        Assert.assertEquals("", instance.extractForumUrl(a));
        Assert.assertEquals("", instance.extractI18nPackageName(a));
        Assert.assertNull(instance.extractImageFile(a));
        Assert.assertFalse(instance.extractSeparateClassLoader(a));
    }

    @CompressionPlugin(id = "Fake", name = "FakePlugin")
    private class FakePlugin {}
}

