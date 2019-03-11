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
package com.liferay.poshi.runner.prose;


import com.liferay.poshi.runner.PoshiRunnerGetterUtil;
import com.liferay.poshi.runner.util.Dom4JUtil;
import java.io.File;
import java.net.URI;
import junit.framework.TestCase;
import org.dom4j.Element;
import org.junit.Test;


/**
 *
 *
 * @author Yi-Chen Tsai
 */
public class PoshiProseDefinitionTest extends TestCase {
    @Test
    public void testProseToXMLTranslation() throws Exception {
        Element actual = _poshiProseDefinition.toElement();
        File file = new File(((PoshiProseDefinitionTest._TEST_BASE_DIR_NAME) + (PoshiProseDefinitionTest._POSHI_TESTCASE_FILE_NAME)));
        URI uri = file.toURI();
        Element expected = PoshiRunnerGetterUtil.getRootElementFromURL(uri.toURL(), false);
        Dom4JUtil.removeWhiteSpaceTextNodes(expected);
        PoshiProseDefinitionTest._assertEqualElements(actual, expected, "Translated actual XML Element does not match expected Element");
    }

    private static final String _POSHI_PROSE_FILE_NAME = "PoshiProseSyntax.prose";

    private static final String _POSHI_TESTCASE_FILE_NAME = "PoshiXMLSyntax.testcase";

    private static final String _TEST_BASE_DIR_NAME = "src/test/resources/com/liferay/poshi/runner/dependencies/prose/";

    private PoshiProseDefinition _poshiProseDefinition;

    private File _testBaseDir;
}

