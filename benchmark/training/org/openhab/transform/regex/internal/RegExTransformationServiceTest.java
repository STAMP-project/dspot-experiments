/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.transform.regex.internal;


import org.eclipse.smarthome.core.transform.TransformationException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Thomas.Eichstaedt-Engelen
 */
public class RegExTransformationServiceTest extends AbstractTransformationServiceTest {
    private RegExTransformationService processor;

    @Test
    public void testTransformByRegex() throws TransformationException {
        // method under test
        String transformedResponse = processor.transform(".*?<current_conditions>.*?<temp_c data=\"(.*?)\".*", source);
        // Asserts
        Assert.assertEquals("8", transformedResponse);
    }

    @Test
    public void testTransformByRegex_noGroup() throws TransformationException {
        // method under test
        String transformedResponse = processor.transform(".*", source);
        // Asserts
        Assert.assertEquals("", transformedResponse);
    }

    @Test
    public void testTransformByRegex_moreThanOneGroup() throws TransformationException {
        // method under test
        String transformedResponse = processor.transform(".*?<current_conditions>.*?<temp_c data=\"(.*?)\"(.*)", source);
        // Asserts
        Assert.assertEquals("8", transformedResponse);
    }

    @Test
    public void testTransformByRegex_substituteFirst() throws TransformationException {
        // method under test
        String transformedResponse = processor.transform("s/^OP:(.*?),ARG:(.*)$/$1($2)/", "OP:SetMode,ARG:42");
        // Asserts
        Assert.assertEquals("SetMode(42)", transformedResponse);
    }

    @Test
    public void testTransformByRegex_substituteAll() throws TransformationException {
        // method under test
        String transformedResponse = processor.transform("s/([A-Z]+)([0-9]+),*/var$1=$2 /g", "X12,Y54");
        // Asserts
        Assert.assertEquals("varX=12 varY=54 ", transformedResponse);
    }
}

