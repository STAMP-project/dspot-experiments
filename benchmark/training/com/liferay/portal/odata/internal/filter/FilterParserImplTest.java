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


import BinaryExpression.Operation.EQ;
import BinaryExpression.Operation.GE;
import BinaryExpression.Operation.LE;
import LambdaFunctionExpression.Type.ANY;
import LiteralExpression.Type.BOOLEAN;
import LiteralExpression.Type.DATE;
import LiteralExpression.Type.DATE_TIME;
import LiteralExpression.Type.STRING;
import MethodExpression.Type.CONTAINS;
import MethodExpression.Type.STARTS_WITH;
import UnaryExpression.Operation.NOT;
import com.liferay.portal.odata.entity.BooleanEntityField;
import com.liferay.portal.odata.entity.DateEntityField;
import com.liferay.portal.odata.entity.DateTimeEntityField;
import com.liferay.portal.odata.entity.DoubleEntityField;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.entity.StringEntityField;
import com.liferay.portal.odata.filter.expression.BinaryExpression;
import com.liferay.portal.odata.filter.expression.CollectionPropertyExpression;
import com.liferay.portal.odata.filter.expression.ComplexPropertyExpression;
import com.liferay.portal.odata.filter.expression.Expression;
import com.liferay.portal.odata.filter.expression.ExpressionVisitException;
import com.liferay.portal.odata.filter.expression.LambdaFunctionExpression;
import com.liferay.portal.odata.filter.expression.LambdaVariableExpression;
import com.liferay.portal.odata.filter.expression.LiteralExpression;
import com.liferay.portal.odata.filter.expression.MemberExpression;
import com.liferay.portal.odata.filter.expression.MethodExpression;
import com.liferay.portal.odata.filter.expression.PrimitivePropertyExpression;
import com.liferay.portal.odata.filter.expression.UnaryExpression;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author David Arques
 */
public class FilterParserImplTest {
    @Test
    public void testParseNonexistingField() {
        String filterString = "(nonExistingField eq 'value')";
        AbstractThrowableAssert exception = Assertions.assertThatThrownBy(() -> _filterParserImpl.parse(filterString)).isInstanceOf(ExpressionVisitException.class);
        exception.hasMessage("Unknown property.");
    }

    @Test
    public void testParseWithContainsMethod() throws ExpressionVisitException {
        Expression expression = FilterParserImplTest._filterParserImpl.parse("contains(fieldExternal, 'value')");
        Assert.assertNotNull(expression);
        MethodExpression methodExpression = ((MethodExpression) (expression));
        Assert.assertEquals(CONTAINS, methodExpression.getType());
        List<Expression> expressions = methodExpression.getExpressions();
        MemberExpression memberExpression = ((MemberExpression) (expressions.get(0)));
        PrimitivePropertyExpression primitivePropertyExpression = ((PrimitivePropertyExpression) (memberExpression.getExpression()));
        Assert.assertEquals("fieldExternal", primitivePropertyExpression.getName());
        LiteralExpression literalExpression = ((LiteralExpression) (expressions.get(1)));
        Assert.assertEquals("'value'", literalExpression.getText());
    }

    @Test
    public void testParseWithContainsMethodAndBooleanType() {
        AbstractThrowableAssert exception = Assertions.assertThatThrownBy(() -> _filterParserImpl.parse("contains(booleanExternal, 7)")).isInstanceOf(ExpressionVisitException.class);
        exception.hasMessage("Incompatible types.");
    }

    @Test
    public void testParseWithContainsMethodAndDateType() {
        AbstractThrowableAssert exception = Assertions.assertThatThrownBy(() -> _filterParserImpl.parse("contains(dateExternal, 2012-05-29T09:13:28Z)")).isInstanceOf(ExpressionVisitException.class);
        exception.hasMessage("Incompatible types.");
    }

    @Test
    public void testParseWithContainsMethodAndDoubleType() {
        AbstractThrowableAssert exception = Assertions.assertThatThrownBy(() -> _filterParserImpl.parse("contains(doubleExternal, 7)")).isInstanceOf(ExpressionVisitException.class);
        exception.hasMessage("Incompatible types.");
    }

    @Test
    public void testParseWithEmptyFilter() {
        AbstractThrowableAssert exception = Assertions.assertThatThrownBy(() -> _filterParserImpl.parse("")).isInstanceOf(ExpressionVisitException.class);
        exception.hasMessage("Filter is null");
    }

    @Test
    public void testParseWithEqBinaryExpressionOnCollectionField() {
        try {
            FilterParserImplTest._filterParserImpl.parse("collectionFieldExternal eq 'value'");
            Assert.fail("Expected ExpressionVisitException was not thrown");
        } catch (ExpressionVisitException eve) {
            Assert.assertEquals("Collection not allowed.", eve.getMessage());
        }
    }

    @Test
    public void testParseWithEqBinaryExpressionWithBooleanFalse() throws ExpressionVisitException {
        Expression expression = FilterParserImplTest._filterParserImpl.parse("booleanExternal eq false");
        Assert.assertNotNull(expression);
        BinaryExpression binaryExpression = ((BinaryExpression) (expression));
        MemberExpression memberExpression = ((MemberExpression) (binaryExpression.getLeftOperationExpression()));
        Assert.assertEquals(EQ, binaryExpression.getOperation());
        PrimitivePropertyExpression primitivePropertyExpression = ((PrimitivePropertyExpression) (memberExpression.getExpression()));
        Assert.assertEquals("booleanExternal", primitivePropertyExpression.getName());
        LiteralExpression literalExpression = ((LiteralExpression) (binaryExpression.getRightOperationExpression()));
        Assert.assertEquals("false", literalExpression.getText());
        Assert.assertEquals(BOOLEAN, literalExpression.getType());
    }

    @Test
    public void testParseWithEqBinaryExpressionWithBooleanInvalid() {
        AbstractThrowableAssert exception = Assertions.assertThatThrownBy(() -> _filterParserImpl.parse("booleanExternal eq 'invalid'")).isInstanceOf(ExpressionVisitException.class);
        exception.hasMessage("Incompatible types.");
    }

    @Test
    public void testParseWithEqBinaryExpressionWithBooleanTrue() throws ExpressionVisitException {
        Expression expression = FilterParserImplTest._filterParserImpl.parse("booleanExternal eq true");
        Assert.assertNotNull(expression);
        BinaryExpression binaryExpression = ((BinaryExpression) (expression));
        MemberExpression memberExpression = ((MemberExpression) (binaryExpression.getLeftOperationExpression()));
        Assert.assertEquals(EQ, binaryExpression.getOperation());
        PrimitivePropertyExpression primitivePropertyExpression = ((PrimitivePropertyExpression) (memberExpression.getExpression()));
        Assert.assertEquals("booleanExternal", primitivePropertyExpression.getName());
        LiteralExpression literalExpression = ((LiteralExpression) (binaryExpression.getRightOperationExpression()));
        Assert.assertEquals("true", literalExpression.getText());
        Assert.assertEquals(BOOLEAN, literalExpression.getType());
    }

    @Test
    public void testParseWithEqBinaryExpressionWithDate() throws ExpressionVisitException {
        Expression expression = FilterParserImplTest._filterParserImpl.parse("dateExternal ge 2012-05-29");
        Assert.assertNotNull(expression);
        BinaryExpression binaryExpression = ((BinaryExpression) (expression));
        Assert.assertEquals(GE, binaryExpression.getOperation());
        MemberExpression memberExpression = ((MemberExpression) (binaryExpression.getLeftOperationExpression()));
        PrimitivePropertyExpression primitivePropertyExpression = ((PrimitivePropertyExpression) (memberExpression.getExpression()));
        Assert.assertEquals("dateExternal", primitivePropertyExpression.getName());
        LiteralExpression literalExpression = ((LiteralExpression) (binaryExpression.getRightOperationExpression()));
        Assert.assertEquals("2012-05-29", literalExpression.getText());
        Assert.assertEquals(DATE, literalExpression.getType());
    }

    @Test
    public void testParseWithEqBinaryExpressionWithDateTimeOffset() throws ExpressionVisitException {
        Expression expression = FilterParserImplTest._filterParserImpl.parse("dateTimeExternal ge 2012-05-29T09:13:28Z");
        Assert.assertNotNull(expression);
        BinaryExpression binaryExpression = ((BinaryExpression) (expression));
        Assert.assertEquals(GE, binaryExpression.getOperation());
        MemberExpression memberExpression = ((MemberExpression) (binaryExpression.getLeftOperationExpression()));
        PrimitivePropertyExpression primitivePropertyExpression = ((PrimitivePropertyExpression) (memberExpression.getExpression()));
        Assert.assertEquals("dateTimeExternal", primitivePropertyExpression.getName());
        LiteralExpression literalExpression = ((LiteralExpression) (binaryExpression.getRightOperationExpression()));
        Assert.assertEquals("2012-05-29T09:13:28Z", literalExpression.getText());
        Assert.assertEquals(DATE_TIME, literalExpression.getType());
    }

    @Test
    public void testParseWithEqBinaryExpressionWithDateTimeWithInvalidType() {
        AbstractThrowableAssert exception = Assertions.assertThatThrownBy(() -> _filterParserImpl.parse("dateTimeExternal ge 2012-05-29")).isInstanceOf(ExpressionVisitException.class);
        exception.hasMessageContaining("Incompatible types");
    }

    @Test
    public void testParseWithEqBinaryExpressionWithDateWithInvalidType() {
        AbstractThrowableAssert exception = Assertions.assertThatThrownBy(() -> _filterParserImpl.parse("dateExternal ge 2012-05-29T09:13:28Z")).isInstanceOf(ExpressionVisitException.class);
        exception.hasMessageContaining("Incompatible types");
    }

    @Test
    public void testParseWithEqBinaryExpressionWithMemberWithComplexProperty() throws ExpressionVisitException {
        Expression expression = FilterParserImplTest._filterParserImpl.parse("complexField/primitiveField eq 'value'");
        Assert.assertNotNull(expression);
        BinaryExpression binaryExpression = ((BinaryExpression) (expression));
        MemberExpression memberExpression = ((MemberExpression) (binaryExpression.getLeftOperationExpression()));
        ComplexPropertyExpression complexPropertyExpression = ((ComplexPropertyExpression) (memberExpression.getExpression()));
        Assert.assertEquals("complexField", complexPropertyExpression.getName());
        PrimitivePropertyExpression primitivePropertyExpression = ((PrimitivePropertyExpression) (complexPropertyExpression.getPropertyExpression()));
        Assert.assertEquals("primitiveField", primitivePropertyExpression.getName());
        LiteralExpression literalExpression = ((LiteralExpression) (binaryExpression.getRightOperationExpression()));
        Assert.assertEquals("'value'", literalExpression.getText());
        Assert.assertEquals(STRING, literalExpression.getType());
    }

    @Test
    public void testParseWithEqBinaryExpressionWithNoSingleQuotes() {
        String filterString = "(fieldExternal eq value)";
        AbstractThrowableAssert exception = Assertions.assertThatThrownBy(() -> _filterParserImpl.parse(filterString)).isInstanceOf(ExpressionVisitException.class);
        exception.hasMessage("Unknown property.");
    }

    @Test
    public void testParseWithEqBinaryExpressionWithSingleQuotes() throws ExpressionVisitException {
        Expression expression = FilterParserImplTest._filterParserImpl.parse("fieldExternal eq 'value'");
        Assert.assertNotNull(expression);
        BinaryExpression binaryExpression = ((BinaryExpression) (expression));
        Assert.assertEquals(EQ, binaryExpression.getOperation());
        MemberExpression memberExpression = ((MemberExpression) (binaryExpression.getLeftOperationExpression()));
        PrimitivePropertyExpression primitivePropertyExpression = ((PrimitivePropertyExpression) (memberExpression.getExpression()));
        Assert.assertEquals("fieldExternal", primitivePropertyExpression.getName());
        LiteralExpression literalExpression = ((LiteralExpression) (binaryExpression.getRightOperationExpression()));
        Assert.assertEquals("'value'", literalExpression.getText());
        Assert.assertEquals(STRING, literalExpression.getType());
    }

    @Test
    public void testParseWithEqBinaryExpressionWithSingleQuotesAndParentheses() throws ExpressionVisitException {
        Expression expression = FilterParserImplTest._filterParserImpl.parse("(fieldExternal eq 'value')");
        Assert.assertNotNull(expression);
        BinaryExpression binaryExpression = ((BinaryExpression) (expression));
        Assert.assertEquals(EQ, binaryExpression.getOperation());
        MemberExpression memberExpression = ((MemberExpression) (binaryExpression.getLeftOperationExpression()));
        PrimitivePropertyExpression primitivePropertyExpression = ((PrimitivePropertyExpression) (memberExpression.getExpression()));
        Assert.assertEquals("fieldExternal", primitivePropertyExpression.getName());
        LiteralExpression literalExpression = ((LiteralExpression) (binaryExpression.getRightOperationExpression()));
        Assert.assertEquals("'value'", literalExpression.getText());
        Assert.assertEquals(STRING, literalExpression.getType());
    }

    @Test
    public void testParseWithGeBinaryExpression() throws ExpressionVisitException {
        Expression expression = FilterParserImplTest._filterParserImpl.parse("fieldExternal ge 'value'");
        Assert.assertNotNull(expression);
        BinaryExpression binaryExpression = ((BinaryExpression) (expression));
        Assert.assertEquals(GE, binaryExpression.getOperation());
        MemberExpression memberExpression = ((MemberExpression) (binaryExpression.getLeftOperationExpression()));
        PrimitivePropertyExpression primitivePropertyExpression = ((PrimitivePropertyExpression) (memberExpression.getExpression()));
        Assert.assertEquals("fieldExternal", primitivePropertyExpression.getName());
        LiteralExpression literalExpression = ((LiteralExpression) (binaryExpression.getRightOperationExpression()));
        Assert.assertEquals("'value'", literalExpression.getText());
        Assert.assertEquals(STRING, literalExpression.getType());
    }

    @Test
    public void testParseWithLambdaAllOnCollectionField() {
        AbstractThrowableAssert exception = Assertions.assertThatThrownBy(() -> _filterParserImpl.parse("collectionFieldExternal/all(f:contains(f,'alu'))")).isInstanceOf(ExpressionVisitException.class);
        exception.hasMessage(("An expression cannot be obtained from URI resources " + "[collectionFieldExternal, all]"));
    }

    @Test
    public void testParseWithLambdaAnyContainsOnCollectionField() throws ExpressionVisitException {
        Expression expression = FilterParserImplTest._filterParserImpl.parse("collectionFieldExternal/any(f:contains(f,'alu'))");
        Assert.assertNotNull(expression);
        MemberExpression memberExpression = ((MemberExpression) (expression));
        CollectionPropertyExpression collectionPropertyExpression = ((CollectionPropertyExpression) (memberExpression.getExpression()));
        Assert.assertEquals("collectionFieldExternal", collectionPropertyExpression.getName());
        LambdaFunctionExpression lambdaFunctionExpression = collectionPropertyExpression.getLambdaFunctionExpression();
        Assert.assertEquals(ANY, lambdaFunctionExpression.getType());
        Assert.assertEquals("f", lambdaFunctionExpression.getVariableName());
        MethodExpression methodExpression = ((MethodExpression) (lambdaFunctionExpression.getExpression()));
        Assert.assertEquals(CONTAINS, methodExpression.getType());
        List<Expression> methodExpressionExpressions = methodExpression.getExpressions();
        Assert.assertNotNull(methodExpressionExpressions);
        Assert.assertEquals(methodExpressionExpressions.toString(), 2, methodExpressionExpressions.size());
        MemberExpression methodExpressionMemberExpression = ((MemberExpression) (methodExpressionExpressions.get(0)));
        LambdaVariableExpression lambdaVariableExpression = ((LambdaVariableExpression) (methodExpressionMemberExpression.getExpression()));
        Assert.assertNotNull(lambdaVariableExpression);
        Assert.assertEquals("f", lambdaVariableExpression.getVariableName());
        LiteralExpression literalExpression = ((LiteralExpression) (methodExpressionExpressions.get(1)));
        Assert.assertNotNull(literalExpression);
        Assert.assertEquals("'alu'", literalExpression.getText());
        Assert.assertEquals(STRING, literalExpression.getType());
    }

    @Test
    public void testParseWithLambdaAnyContainsOnNoncollectionField() {
        try {
            FilterParserImplTest._filterParserImpl.parse("fieldExternal/any(f:contains(f,'alu'))");
            Assert.fail("Expected ExpressionVisitException was not thrown");
        } catch (ExpressionVisitException eve) {
            Assert.assertEquals("Expected token 'QualifiedName' not found.", eve.getMessage());
        }
    }

    @Test
    public void testParseWithLambdaAnyEqOnCollectionFieldInComplexField() throws ExpressionVisitException {
        Expression expression = FilterParserImplTest._filterParserImpl.parse("complexField/collectionField/any(f:contains(f,'alu'))");
        Assert.assertNotNull(expression);
    }

    @Test
    public void testParseWithLeBinaryExpression() throws ExpressionVisitException {
        Expression expression = FilterParserImplTest._filterParserImpl.parse("fieldExternal le 'value'");
        Assert.assertNotNull(expression);
        BinaryExpression binaryExpression = ((BinaryExpression) (expression));
        Assert.assertEquals(LE, binaryExpression.getOperation());
        MemberExpression memberExpression = ((MemberExpression) (binaryExpression.getLeftOperationExpression()));
        PrimitivePropertyExpression primitivePropertyExpression = ((PrimitivePropertyExpression) (memberExpression.getExpression()));
        Assert.assertEquals("fieldExternal", primitivePropertyExpression.getName());
        LiteralExpression literalExpression = ((LiteralExpression) (binaryExpression.getRightOperationExpression()));
        Assert.assertEquals("'value'", literalExpression.getText());
        Assert.assertEquals(STRING, literalExpression.getType());
    }

    @Test
    public void testParseWithNotUnaryExpressionWithEqBinaryExpression() throws ExpressionVisitException {
        Expression expression = FilterParserImplTest._filterParserImpl.parse("not (booleanExternal eq true)");
        Assert.assertNotNull(expression);
        UnaryExpression unaryExpression = ((UnaryExpression) (expression));
        Assert.assertEquals(NOT, unaryExpression.getOperation());
        BinaryExpression binaryExpression = ((BinaryExpression) (unaryExpression.getExpression()));
        MemberExpression memberExpression = ((MemberExpression) (binaryExpression.getLeftOperationExpression()));
        PrimitivePropertyExpression primitivePropertyExpression = ((PrimitivePropertyExpression) (memberExpression.getExpression()));
        Assert.assertEquals("booleanExternal", primitivePropertyExpression.getName());
        LiteralExpression literalExpression = ((LiteralExpression) (binaryExpression.getRightOperationExpression()));
        Assert.assertEquals("true", literalExpression.getText());
        Assert.assertEquals(BOOLEAN, literalExpression.getType());
    }

    @Test
    public void testParseWithNullExpression() {
        AbstractThrowableAssert exception = Assertions.assertThatThrownBy(() -> _filterParserImpl.parse(null)).isInstanceOf(ExpressionVisitException.class);
        exception.hasMessage("Filter is null");
    }

    @Test
    public void testParseWithStarsWithMethod() throws ExpressionVisitException {
        Expression expression = FilterParserImplTest._filterParserImpl.parse("startswith(fieldExternal, 'value')");
        Assert.assertNotNull(expression);
        MethodExpression methodExpression = ((MethodExpression) (expression));
        Assert.assertEquals(STARTS_WITH, methodExpression.getType());
        List<Expression> expressions = methodExpression.getExpressions();
        MemberExpression memberExpression = ((MemberExpression) (expressions.get(0)));
        PrimitivePropertyExpression primitivePropertyExpression = ((PrimitivePropertyExpression) (memberExpression.getExpression()));
        Assert.assertEquals("fieldExternal", primitivePropertyExpression.getName());
        LiteralExpression literalExpression = ((LiteralExpression) (expressions.get(1)));
        Assert.assertEquals("'value'", literalExpression.getText());
    }

    @Test
    public void testParseWithStarsWithMethodAndBooleanType() {
        AbstractThrowableAssert exception = Assertions.assertThatThrownBy(() -> _filterParserImpl.parse("startswith(booleanExternal, 7)")).isInstanceOf(ExpressionVisitException.class);
        exception.hasMessage("Incompatible types.");
    }

    @Test
    public void testParseWithStarsWithMethodAndDateType() {
        AbstractThrowableAssert exception = Assertions.assertThatThrownBy(() -> _filterParserImpl.parse("contains(dateExternal, 2012-05-29T09:13:28Z)")).isInstanceOf(ExpressionVisitException.class);
        exception.hasMessage("Incompatible types.");
    }

    @Test
    public void testParseWithStarsWithMethodAnDoubleType() {
        AbstractThrowableAssert exception = Assertions.assertThatThrownBy(() -> _filterParserImpl.parse("contains(doubleExternal, 7)")).isInstanceOf(ExpressionVisitException.class);
        exception.hasMessage("Incompatible types.");
    }

    @Test
    public void testParseWithStartsWithMethodAndDateType() {
        AbstractThrowableAssert exception = Assertions.assertThatThrownBy(() -> _filterParserImpl.parse("startswith(dateExternal, 2012-05-29T09:13:28Z)")).isInstanceOf(ExpressionVisitException.class);
        exception.hasMessage("Incompatible types.");
    }

    private static final FilterParserImpl _filterParserImpl = new FilterParserImpl(new EntityModel() {
        @Override
        public Map<String, EntityField> getEntityFieldsMap() {
            return Stream.of(new BooleanEntityField("booleanExternal", ( locale) -> "booleanInternal"), new com.liferay.portal.odata.entity.CollectionEntityField(new StringEntityField("collectionFieldExternal", ( locale) -> "collectionFieldInternal")), new com.liferay.portal.odata.entity.ComplexEntityField("complexField", Stream.of(new com.liferay.portal.odata.entity.CollectionEntityField(new StringEntityField("collectionField", ( locale) -> "collectionFieldInternal")), new StringEntityField("primitiveField", ( locale) -> "primitiveFieldInternal")).collect(Collectors.toList())), new DateEntityField("dateExternal", ( locale) -> "dateInternal", ( locale) -> "dateInternal"), new DateTimeEntityField("dateTimeExternal", ( locale) -> "dateTimeInternal", ( locale) -> "dateTimeInternal"), new DoubleEntityField("doubleExternal", ( locale) -> "doubleInternal"), new StringEntityField("fieldExternal", ( locale) -> "fieldInternal")).collect(Collectors.toMap(EntityField::getName, Function.identity()));
        }

        @Override
        public String getName() {
            return "SomeEntityName";
        }
    });
}

