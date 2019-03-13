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
package org.openhab.transform.xslt.internal;


import org.eclipse.smarthome.core.transform.TransformationException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Thomas.Eichstaedt-Engelen
 */
public class XsltTransformationServiceTest extends AbstractTransformationServiceTest {
    private XsltTransformationService processor;

    @Test
    public void testTransformByXSLT() throws TransformationException {
        // method under test
        String transformedResponse = processor.transform("http/google_weather.xsl", source);
        // Asserts
        Assert.assertEquals("8", transformedResponse);
    }
}

