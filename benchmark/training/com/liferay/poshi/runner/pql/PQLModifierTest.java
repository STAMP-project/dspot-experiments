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
package com.liferay.poshi.runner.pql;


import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;
import org.junit.Test;


/**
 *
 *
 * @author Michael Hashimoto
 */
public class PQLModifierTest extends TestCase {
    @Test
    public void testGetModifier() throws Exception {
        Set<String> availableModifiers = PQLModifier.getAvailableModifiers();
        for (String modifier : availableModifiers) {
            PQLModifier pqlModifier = PQLModifierFactory.newPQLModifier(modifier);
            _compareString(pqlModifier.getModifier(), modifier);
        }
    }

    @Test
    public void testGetPQLResult() throws Exception {
        _validateGetPQLResult("NOT", Boolean.TRUE, Boolean.FALSE);
        _validateGetPQLResult("NOT", Boolean.FALSE, Boolean.TRUE);
    }

    @Test
    public void testGetPQLResultError() throws Exception {
        _validateGetPQLResultError("NOT", null, "Modifier must be used with a boolean value: NOT");
        _validateGetPQLResultError("NOT", "test", "Modifier must be used with a boolean value: NOT");
        _validateGetPQLResultError("NOT", 10.0, "Modifier must be used with a boolean value: NOT");
        _validateGetPQLResultError("NOT", 10, "Modifier must be used with a boolean value: NOT");
    }

    @Test
    public void testModifierValidate() throws Exception {
        Set<String> availableModifiers = PQLModifier.getAvailableModifiers();
        for (String modifier : availableModifiers) {
            PQLModifier.validateModifier(modifier);
        }
    }

    @Test
    public void testModifierValidateError() throws Exception {
        Set<String> modifiers = new HashSet<>();
        modifiers.add(null);
        modifiers.add("bad");
        modifiers.add("NOT bad");
        modifiers.addAll(PQLOperator.getAvailableOperators());
        for (String modifier : modifiers) {
            _validatePQLModifierError(modifier, ("Invalid modifier: " + modifier));
        }
    }
}

