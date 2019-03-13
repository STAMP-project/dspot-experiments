/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.poshi.runner.elements;


import com.liferay.poshi.runner.util.Dom4JUtil;
import com.liferay.poshi.runner.util.FileUtil;
import org.dom4j.Element;
import org.dom4j.Node;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Kenji Heigel
 */
public class PoshiElementFactoryTest {
    @Test
    public void testConditionalPoshiScriptLineNumbers() throws Exception {
        PoshiElement rootPoshiElement = PoshiElementFactoryTest._getPoshiElement("ConditionalPoshiScript.macro");
        PoshiElement commandElement = ((PoshiElement) (rootPoshiElement.element("command")));
        Element ifElement = commandElement.element("if");
        AndPoshiElement andPoshiElement = ((AndPoshiElement) (ifElement.element("and")));
        Assert.assertEquals(3, andPoshiElement.getPoshiScriptLineNumber());
        int[] expectedLineNumbers = new int[]{ 4, 6 };
        PoshiElement thenPoshiElement = ((PoshiElement) (ifElement.element("then")));
        int i = 0;
        for (Node node : Dom4JUtil.toNodeList(thenPoshiElement.content())) {
            PoshiElement childPoshiElement = ((PoshiElement) (node));
            Assert.assertEquals("The the expected line number does not match", expectedLineNumbers[i], childPoshiElement.getPoshiScriptLineNumber());
            i++;
        }
    }

    @Test
    public void testPoshiScriptFunctionToXML() throws Exception {
        PoshiElement actualElement = PoshiElementFactoryTest._getPoshiElement("PoshiScript.function");
        Element expectedElement = PoshiElementFactoryTest._getDom4JElement("PoshiSyntax.function");
        PoshiElementFactoryTest._assertEqualElements(actualElement, expectedElement, "Poshi script syntax does not translate to Poshi XML");
    }

    @Test
    public void testPoshiScriptLineNumbers() throws Exception {
        PoshiElement rootPoshiElement = PoshiElementFactoryTest._getPoshiElement("PoshiScript.macro");
        int[] expectedLineNumbers = new int[]{ 3, 8, 9, 11, 16, 20, 25, 27, 34, 36 };
        int i = 0;
        for (Node node : Dom4JUtil.toNodeList(rootPoshiElement.content())) {
            if (node instanceof PoshiElement) {
                PoshiElement poshiElement = ((PoshiElement) (node));
                for (Node childNode : Dom4JUtil.toNodeList(poshiElement.content())) {
                    PoshiNode childPoshiNode = ((PoshiNode) (childNode));
                    Assert.assertEquals("The the expected line number does not match", expectedLineNumbers[i], childPoshiNode.getPoshiScriptLineNumber());
                    i++;
                }
            }
        }
    }

    @Test
    public void testPoshiScriptMacroToXML() throws Exception {
        PoshiElement actualElement = PoshiElementFactoryTest._getPoshiElement("PoshiScript.macro");
        Element expectedElement = PoshiElementFactoryTest._getDom4JElement("PoshiSyntax.macro");
        PoshiElementFactoryTest._assertEqualElements(actualElement, expectedElement, "Poshi script syntax does not translate to Poshi XML");
    }

    @Test
    public void testPoshiScriptTestToPoshiXML() throws Exception {
        PoshiElement actualElement = PoshiElementFactoryTest._getPoshiElement("PoshiScript.testcase");
        Element expectedElement = PoshiElementFactoryTest._getDom4JElement("PoshiSyntax.testcase");
        PoshiElementFactoryTest._assertEqualElements(actualElement, expectedElement, "Poshi script syntax does not translate to Poshi XML");
    }

    @Test
    public void testPoshiXMLFunctionToPoshiScript() throws Exception {
        String expected = FileUtil.read(PoshiElementFactoryTest._getFile("PoshiScript.function"));
        PoshiElement poshiElement = PoshiElementFactoryTest._getPoshiElement("PoshiSyntax.function");
        String actual = poshiElement.toPoshiScript();
        PoshiElementFactoryTest._assertEqualStrings(actual, expected, "Poshi XML syntax does not translate to Poshi script syntax");
    }

    @Test
    public void testPoshiXMLMacroFormat() throws Exception {
        PoshiElement actualElement = PoshiElementFactoryTest._getPoshiElement("UnformattedPoshiScript.macro");
        Element expectedElement = PoshiElementFactoryTest._getDom4JElement("PoshiSyntax.macro");
        PoshiElementFactoryTest._assertEqualElements(actualElement, expectedElement, "Poshi script syntax does not translate to Poshi XML");
    }

    @Test
    public void testPoshiXMLMacroToPoshiScript() throws Exception {
        String expected = FileUtil.read(PoshiElementFactoryTest._getFile("PoshiScript.macro"));
        PoshiElement poshiElement = PoshiElementFactoryTest._getPoshiElement("PoshiSyntax.macro");
        String actual = poshiElement.toPoshiScript();
        PoshiElementFactoryTest._assertEqualStrings(actual, expected, "Poshi XML syntax does not translate to Poshi script syntax");
    }

    @Test
    public void testPoshiXMLTestToPoshiScript() throws Exception {
        String expected = FileUtil.read(PoshiElementFactoryTest._getFile("PoshiScript.testcase"));
        PoshiElement poshiElement = PoshiElementFactoryTest._getPoshiElement("PoshiSyntax.testcase");
        String actual = poshiElement.toPoshiScript();
        PoshiElementFactoryTest._assertEqualStrings(actual, expected, "Poshi XML syntax does not translate to Poshi script syntax");
    }

    @Test
    public void testPoshiXMLTestToPoshiScriptToPoshiXML() throws Exception {
        String fileName = "PoshiSyntax.testcase";
        PoshiElement poshiElement = PoshiElementFactoryTest._getPoshiElement(fileName);
        String poshiScript = poshiElement.toPoshiScript();
        PoshiNodeFactory.setValidatePoshiScript(false);
        PoshiElement actualElement = ((PoshiElement) (PoshiNodeFactory.newPoshiNode(poshiScript, FileUtil.getURL(PoshiElementFactoryTest._getFile(fileName)))));
        Element expectedElement = PoshiElementFactoryTest._getDom4JElement("PoshiSyntax.testcase");
        PoshiElementFactoryTest._assertEqualElements(actualElement, expectedElement, "Poshi XML syntax is not preserved in full translation");
    }

    @Test
    public void testPoshiXMLTestToXML() throws Exception {
        PoshiElement actualElement = PoshiElementFactoryTest._getPoshiElement("PoshiSyntax.testcase");
        Element expectedElement = PoshiElementFactoryTest._getDom4JElement("PoshiSyntax.testcase");
        PoshiElementFactoryTest._assertEqualElements(actualElement, expectedElement, "Poshi XML syntax does not translate to XML");
    }

    private static final String _BASE_DIR = "src/test/resources/com/liferay/poshi/runner/dependencies/elements/";
}

