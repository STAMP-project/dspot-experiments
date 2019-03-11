/**
 * *****************************************************************************
 * Copyright (c) 2009, 2019 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *
 * *****************************************************************************
 */
package org.jacoco.examples.parser;


import java.io.IOException;
import org.junit.Test;


public class ExpressionParserTest {
    @Test
    public void expression1() throws IOException {
        ExpressionParserTest.assertExpression("2 * 3 + 4", 10);
    }

    @Test
    public void expression2() throws IOException {
        ExpressionParserTest.assertExpression("2 + 3 * 4", 14);
    }

    @Test
    public void expression3() throws IOException {
        ExpressionParserTest.assertExpression("(2 + 3) * 4", 20);
    }

    @Test
    public void expression4() throws IOException {
        ExpressionParserTest.assertExpression("2 * 2 * 2 * 2", 16);
    }

    @Test
    public void expression5() throws IOException {
        ExpressionParserTest.assertExpression("1 + 2 + 3 + 4", 10);
    }

    @Test
    public void expression6() throws IOException {
        ExpressionParserTest.assertExpression("2 * 3 + 2 * 5", 16);
    }
}

