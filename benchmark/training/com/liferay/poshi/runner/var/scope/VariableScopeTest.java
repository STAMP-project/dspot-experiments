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
package com.liferay.poshi.runner.var.scope;


import com.liferay.poshi.runner.PoshiRunnerTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Kenji Heigel
 */
public class VariableScopeTest extends PoshiRunnerTestCase {
    @Test
    public void testAssertExecuteVarInheritance() throws Exception {
        runPoshiTest("VariableScope#ExecuteVarInheritance");
    }

    @Test
    public void testAssertRootVarInheritance() throws Exception {
        runPoshiTest("VariableScope#RootVarInheritance");
    }

    @Test
    public void testAssertStaticMacroVarInheritance() throws Exception {
        runPoshiTest("VariableScope#MacroStaticVarInheritance");
    }

    @Test
    public void testAssertStaticTestVarInheritance() throws Exception {
        runPoshiTest("VariableScope#TestCaseStaticVarInheritance");
    }

    private static final String _TEST_BASE_DIR_NAME = "src/test/resources/com/liferay/poshi/runner/dependencies/var/scope";
}

