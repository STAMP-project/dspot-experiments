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
public class PQLOperatorFactoryTest extends TestCase {
    @Test
    public void testNewPQLOperator() throws Exception {
        Set<String> availableOperators = PQLOperator.getAvailableOperators();
        for (String operator : availableOperators) {
            PQLOperatorFactory.newPQLOperator(operator);
        }
    }

    @Test
    public void testNewPQLOperatorError() throws Exception {
        Set<String> operators = new HashSet<>();
        operators.add(null);
        operators.add("bad");
        operators.add("bad value");
        operators.addAll(PQLModifier.getAvailableModifiers());
        for (String operator : operators) {
            _validateNewPQLOperatorError(operator, ("Invalid operator: " + operator));
        }
    }
}

