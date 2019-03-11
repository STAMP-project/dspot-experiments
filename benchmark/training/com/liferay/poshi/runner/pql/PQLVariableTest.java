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


import junit.framework.TestCase;
import org.junit.Test;


/**
 *
 *
 * @author Michael Hashimoto
 */
public class PQLVariableTest extends TestCase {
    @Test
    public void testGetPQLResult() throws Exception {
        _validateGetPQLResult("false", Boolean.FALSE);
        _validateGetPQLResult("'false'", Boolean.FALSE);
        _validateGetPQLResult("\"false\"", Boolean.FALSE);
        _validateGetPQLResult("true", Boolean.TRUE);
        _validateGetPQLResult("'true'", Boolean.TRUE);
        _validateGetPQLResult("\"true\"", Boolean.TRUE);
        _validateGetPQLResult("3.2", 3.2);
        _validateGetPQLResult("'3.2'", 3.2);
        _validateGetPQLResult("\"3.2\"", 3.2);
        _validateGetPQLResult("2016.0", 2016.0);
        _validateGetPQLResult("'2016.0'", 2016.0);
        _validateGetPQLResult("\"2016.0\"", 2016.0);
        _validateGetPQLResult("2016", 2016);
        _validateGetPQLResult("'2016'", 2016);
        _validateGetPQLResult("\"2016\"", 2016);
        _validateGetPQLResult("test", "test");
        _validateGetPQLResult("'test'", "test");
        _validateGetPQLResult("\"test\"", "test");
        _validateGetPQLResult("'test test'", "test test");
        _validateGetPQLResult("\"test test\"", "test test");
    }

    @Test
    public void testGetPQLResultError() throws Exception {
        _validateGetPQLResultError("invalid.property", "Invalid testcase property: invalid.property");
        _validateGetPQLResultError(null, "Invalid variable: null");
        _validateGetPQLResultError("test == test", "Invalid value: test == test");
        _validateGetPQLResultError("test OR test", "Invalid value: test OR test");
    }
}

