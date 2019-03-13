/**
 * This file is part of a module with proprietary Enterprise Features.
 *
 * Licensed to Crate.io Inc. ("Crate.io") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 *
 * To use this file, Crate.io must have given you permission to enable and
 * use such Enterprise Features and you must have a valid Enterprise or
 * Subscription Agreement with Crate.io.  If you enable or use the Enterprise
 * Features, you represent and warrant that you have a valid Enterprise or
 * Subscription Agreement with Crate.io.  Your use of the Enterprise Features
 * if governed by the terms and conditions of your Enterprise or Subscription
 * Agreement with Crate.io.
 */
package io.crate.scalar.systeminformation;


import io.crate.auth.user.User;
import io.crate.expression.scalar.AbstractScalarFunctionsTest;
import io.crate.expression.symbol.Symbol;
import io.crate.expression.symbol.format.SymbolPrinter;
import org.hamcrest.Matchers;
import org.junit.Test;


public class UserFunctionTest extends AbstractScalarFunctionsTest {
    private static final User TEST_USER = User.of("testUser");

    @Test
    public void testNormalizeCurrentUser() {
        setupFunctionsFor(UserFunctionTest.TEST_USER);
        assertNormalize("current_user", isLiteral("testUser"), false);
    }

    @Test
    public void testNormalizeSessionUser() {
        setupFunctionsFor(UserFunctionTest.TEST_USER);
        assertNormalize("session_user", isLiteral("testUser"), false);
    }

    @Test
    public void testNormalizeUser() {
        setupFunctionsFor(UserFunctionTest.TEST_USER);
        assertNormalize("user", isLiteral("testUser"), false);
    }

    @Test
    public void testFormatFunctionsWithoutBrackets() {
        setupFunctionsFor(UserFunctionTest.TEST_USER);
        SymbolPrinter printer = new SymbolPrinter(sqlExpressions.functions());
        Symbol f = sqlExpressions.asSymbol("current_user");
        assertThat(printer.printQualified(f), Matchers.is("current_user"));
        f = sqlExpressions.asSymbol("session_user");
        assertThat(printer.printQualified(f), Matchers.is("session_user"));
        f = sqlExpressions.asSymbol("user");
        assertThat(printer.printQualified(f), Matchers.is("current_user"));
    }
}

