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
package com.liferay.dynamic.data.mapping.form.evaluator.internal.function;


import GetFieldPropertyResponse.Builder;
import StringPool.BLANK;
import com.liferay.dynamic.data.mapping.expression.GetFieldPropertyRequest;
import com.liferay.dynamic.data.mapping.expression.GetFieldPropertyResponse;
import java.math.BigDecimal;
import java.util.function.Function;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Leonardo Barros
 */
public class GetValueFunctionTest {
    @Test
    public void testArray() {
        String[] expected = new String[]{ "test1", "test2" };
        DefaultDDMExpressionFieldAccessor ddmExpressionFieldAccessor = new DefaultDDMExpressionFieldAccessor();
        GetFieldPropertyResponse.Builder builder = Builder.newBuilder(expected);
        ddmExpressionFieldAccessor.setGetFieldPropertyResponseFunction(( getFieldPropertyRequest) -> builder.build());
        _getValueFunction.setDDMExpressionFieldAccessor(ddmExpressionFieldAccessor);
        Object result = _getValueFunction.apply("field");
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testNullObserver() {
        Object result = _getValueFunction.apply("field");
        Assert.assertEquals(BLANK, result);
    }

    @Test
    public void testSingleValue() {
        DefaultDDMExpressionFieldAccessor ddmExpressionFieldAccessor = new DefaultDDMExpressionFieldAccessor();
        GetFieldPropertyResponse.Builder builder = Builder.newBuilder(new BigDecimal(10));
        ddmExpressionFieldAccessor.setGetFieldPropertyResponseFunction(( getFieldPropertyRequest) -> builder.build());
        _getValueFunction.setDDMExpressionFieldAccessor(ddmExpressionFieldAccessor);
        Object result = _getValueFunction.apply("field");
        Assert.assertEquals(new BigDecimal(10), result);
    }

    private GetValueFunction _getValueFunction;
}

