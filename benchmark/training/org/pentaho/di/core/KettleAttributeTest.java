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
package org.pentaho.di.core;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class KettleAttributeTest {
    @Test
    public void testClass() {
        final String key = "key";
        final String xmlCode = "xmlCode";
        final String repCode = "repCode";
        final String description = "description";
        final String tooltip = "tooltip";
        final int type = 6;
        final KettleAttributeInterface parent = Mockito.mock(KettleAttributeInterface.class);
        KettleAttribute attribute = new KettleAttribute(key, xmlCode, repCode, description, tooltip, type, parent);
        Assert.assertSame(key, attribute.getKey());
        Assert.assertSame(xmlCode, attribute.getXmlCode());
        Assert.assertSame(repCode, attribute.getRepCode());
        Assert.assertSame(description, attribute.getDescription());
        Assert.assertSame(tooltip, attribute.getTooltip());
        Assert.assertEquals(type, attribute.getType());
        Assert.assertSame(parent, attribute.getParent());
        attribute.setKey(null);
        Assert.assertNull(attribute.getKey());
        attribute.setXmlCode(null);
        Assert.assertNull(attribute.getXmlCode());
        attribute.setRepCode(null);
        Assert.assertNull(attribute.getRepCode());
        attribute.setDescription(null);
        Assert.assertNull(attribute.getDescription());
        attribute.setTooltip(null);
        Assert.assertNull(attribute.getTooltip());
        attribute.setType((-6));
        Assert.assertEquals((-6), attribute.getType());
        attribute.setParent(null);
        Assert.assertNull(attribute.getParent());
    }
}

