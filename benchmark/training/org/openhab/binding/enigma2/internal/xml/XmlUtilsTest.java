/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.enigma2.internal.xml;


import org.junit.Assert;
import org.junit.Test;


/**
 * Test class for XMLUtils
 *
 * @author Sebastian Kutschbach
 * @since 1.6.0
 */
public class XmlUtilsTest {
    private static final String VALID_CONTENT = "<e2powerstate>" + ("<e2instandby>false</e2instandby>" + "</e2powerstate>");

    private static final String EMPTY_CONTENT = "<e2powerstate>" + ("<e2instandby></e2instandby>" + "</e2powerstate>");

    private static final String MISSING_CONTENT = "<e2powerstate></e2powerstate>";

    @Test
    public void testGetContentOfElementValidContent() {
        Assert.assertEquals("false", XmlUtils.getContentOfElement(XmlUtilsTest.VALID_CONTENT, "e2instandby"));
    }

    @Test
    public void testGetContentOfElementEmptyContent() {
        Assert.assertEquals("", XmlUtils.getContentOfElement(XmlUtilsTest.EMPTY_CONTENT, "e2instandby"));
    }

    @Test
    public void testGetContentOfElementMissingContent() {
        Assert.assertNull("false", XmlUtils.getContentOfElement(XmlUtilsTest.MISSING_CONTENT, "e2instandby"));
    }
}

