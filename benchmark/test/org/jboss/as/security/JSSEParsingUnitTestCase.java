/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.security;


import ModelDescriptionConstants.OUTCOME;
import ModelDescriptionConstants.RESULT;
import ModelDescriptionConstants.SUCCESS;
import javax.xml.stream.XMLStreamException;
import org.jboss.as.controller.Extension;
import org.jboss.as.controller.extension.ExtensionRegistry;
import org.jboss.as.model.test.ModelTestParser;
import org.jboss.dmr.ModelNode;
import org.jboss.staxmapper.XMLMapper;
import org.junit.Assert;
import org.junit.Test;

import static SecurityExtension.SUBSYSTEM_NAME;


/**
 * TODO class javadoc.
 *
 * @author Brian Stansberry (c) 2011 Red Hat Inc.
 */
public class JSSEParsingUnitTestCase {
    private static final ModelNode SUCCESS;

    static {
        SUCCESS = new ModelNode();
        JSSEParsingUnitTestCase.SUCCESS.get(OUTCOME).set(ModelDescriptionConstants.SUCCESS);
        JSSEParsingUnitTestCase.SUCCESS.get(RESULT);
        JSSEParsingUnitTestCase.SUCCESS.protect();
    }

    private ExtensionRegistry extensionParsingRegistry;

    private ModelTestParser testParser;

    private XMLMapper xmlMapper;

    private final String TEST_NAMESPACE = "urn.org.jboss.test:1.0";

    protected final String mainSubsystemName = SUBSYSTEM_NAME;

    private final Extension mainExtension = new SecurityExtension();

    @Test
    public void testParseMissingPasswordJSSE() throws Exception {
        try {
            parse("securityErrorMissingPassword.xml");
            Assert.fail("There should have been an error.");
        } catch (XMLStreamException ex) {
            Assert.assertTrue(ex.getMessage(), ex.getMessage().contains("WFLYSEC0023"));
        }
    }

    @Test
    public void testParseWrongJSSE() throws Exception {
        try {
            parse("securityParserError.xml");
            Assert.fail("There should have been an error.");
        } catch (XMLStreamException ex) {
            Assert.assertTrue(ex.getMessage(), ex.getMessage().contains("WFLYSEC0023"));
        }
    }

    @Test
    public void testParseValidJSSE() throws Exception {
        parse("securityParserValidJSSE.xml");
    }
}

