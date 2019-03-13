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
package com.liferay.dynamic.data.mapping.expression.internal;


import CreateExpressionRequest.Builder;
import com.liferay.dynamic.data.mapping.expression.CreateExpressionRequest;
import com.liferay.dynamic.data.mapping.expression.DDMExpression;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunction;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunctionTracker;
import com.liferay.dynamic.data.mapping.expression.internal.functions.PowFunction;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;


/**
 *
 *
 * @author Leonardo Barros
 */
@RunWith(MockitoJUnitRunner.class)
public class DDMExpressionFactoryImplTest extends PowerMockito {
    @Test
    public void testCreateDDMExpression() throws Exception {
        DDMExpressionFactoryImpl ddmExpressionFactory = new DDMExpressionFactoryImpl();
        ddmExpressionFactory.ddmExpressionFunctionTracker = _ddmExpressionFunctionTracker;
        Map<String, DDMExpressionFunction> functions = new HashMap() {
            {
                put("pow", new PowFunction());
            }
        };
        when(_ddmExpressionFunctionTracker.getDDMExpressionFunctions(Matchers.any())).thenReturn(functions);
        CreateExpressionRequest.Builder builder = Builder.newBuilder("pow(2,3)");
        DDMExpression<BigDecimal> ddmExpression = ddmExpressionFactory.createExpression(builder.build());
        BigDecimal actual = ddmExpression.evaluate();
        Assert.assertEquals(0, actual.compareTo(new BigDecimal(8)));
    }

    @Mock
    private DDMExpressionFunctionTracker _ddmExpressionFunctionTracker;
}

