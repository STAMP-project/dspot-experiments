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
package com.liferay.portal.odata.internal.filter;


import BinaryExpression.Operation;
import LiteralExpression.Type;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.entity.StringEntityField;
import com.liferay.portal.odata.filter.expression.BinaryExpression;
import com.liferay.portal.odata.filter.expression.ExpressionVisitException;
import com.liferay.portal.odata.filter.expression.MemberExpression;
import com.liferay.portal.odata.internal.filter.expression.LambdaVariableExpressionImpl;
import com.liferay.portal.odata.internal.filter.expression.MemberExpressionImpl;
import com.liferay.portal.odata.internal.filter.expression.PrimitivePropertyExpressionImpl;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;

import static LambdaFunctionExpression.Type.ANY;


/**
 * The type Expression convert impl test.
 *
 * @author Cristina Gonz?lez
 */
public class ExpressionConvertImplTest {
    @Test
    public void testConvertBinaryExpressionWithEqOnPrimitiveField() throws ExpressionVisitException {
        BinaryExpression binaryExpression = new com.liferay.portal.odata.internal.filter.expression.BinaryExpressionImpl(new MemberExpressionImpl(new PrimitivePropertyExpressionImpl("title")), Operation.EQ, new com.liferay.portal.odata.internal.filter.expression.LiteralExpressionImpl("test", Type.STRING));
        TermFilter termFilter = ((TermFilter) (_expressionConvert.convert(binaryExpression, Locale.getDefault(), ExpressionConvertImplTest._entityModel)));
        Assert.assertEquals("title", termFilter.getField());
        Assert.assertEquals("test", termFilter.getValue());
    }

    @Test
    public void testConvertMemberExpressionWithLambdaAnyEqOnCollectionField() throws ExpressionVisitException {
        MemberExpression memberExpression = new MemberExpressionImpl(new com.liferay.portal.odata.internal.filter.expression.CollectionPropertyExpressionImpl(new PrimitivePropertyExpressionImpl("keywords"), new com.liferay.portal.odata.internal.filter.expression.LambdaFunctionExpressionImpl(ANY, "k", new com.liferay.portal.odata.internal.filter.expression.BinaryExpressionImpl(new MemberExpressionImpl(new LambdaVariableExpressionImpl("k")), Operation.EQ, new com.liferay.portal.odata.internal.filter.expression.LiteralExpressionImpl("'keyword1'", Type.STRING)))));
        TermFilter termFilter = ((TermFilter) (_expressionConvert.convert(memberExpression, Locale.getDefault(), ExpressionConvertImplTest._entityModel)));
        Assert.assertNotNull(termFilter);
        Assert.assertEquals("keywords.raw", termFilter.getField());
        Assert.assertEquals("keyword1", termFilter.getValue());
    }

    private static final EntityModel _entityModel = new EntityModel() {
        @Override
        public Map<String, EntityField> getEntityFieldsMap() {
            return Stream.of(new com.liferay.portal.odata.entity.CollectionEntityField(new StringEntityField("keywords", ( locale) -> "keywords.raw")), new StringEntityField("title", ( locale) -> "title")).collect(Collectors.toMap(EntityField::getName, Function.identity()));
        }

        @Override
        public String getName() {
            return "SomeEntityName";
        }
    };

    private final ExpressionConvertImpl _expressionConvert = new ExpressionConvertImpl();
}

