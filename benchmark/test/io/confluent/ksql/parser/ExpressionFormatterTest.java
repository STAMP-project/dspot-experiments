/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.parser;


import IntervalLiteral.IntervalField;
import IntervalLiteral.IntervalField.MONTH;
import IntervalLiteral.Sign;
import LogicalBinaryExpression.Type;
import SqlType.BIGINT;
import SqlType.BOOLEAN;
import SqlType.INTEGER;
import SqlType.STRING;
import io.confluent.ksql.parser.tree.Array;
import io.confluent.ksql.parser.tree.BetweenPredicate;
import io.confluent.ksql.parser.tree.BooleanLiteral;
import io.confluent.ksql.parser.tree.Cast;
import io.confluent.ksql.parser.tree.DecimalLiteral;
import io.confluent.ksql.parser.tree.DoubleLiteral;
import io.confluent.ksql.parser.tree.FunctionCall;
import io.confluent.ksql.parser.tree.LikePredicate;
import io.confluent.ksql.parser.tree.LogicalBinaryExpression;
import io.confluent.ksql.parser.tree.LongLiteral;
import io.confluent.ksql.parser.tree.Map;
import io.confluent.ksql.parser.tree.NodeLocation;
import io.confluent.ksql.parser.tree.NullLiteral;
import io.confluent.ksql.parser.tree.PrimitiveType;
import io.confluent.ksql.parser.tree.QualifiedName;
import io.confluent.ksql.parser.tree.SearchedCaseExpression;
import io.confluent.ksql.parser.tree.SimpleCaseExpression;
import io.confluent.ksql.parser.tree.StringLiteral;
import io.confluent.ksql.parser.tree.Struct;
import io.confluent.ksql.parser.tree.SymbolReference;
import io.confluent.ksql.parser.tree.TimeLiteral;
import io.confluent.ksql.parser.tree.TimestampLiteral;
import java.util.Collections;
import java.util.Optional;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

import static ArithmeticBinaryExpression.Type.ADD;
import static ArithmeticUnaryExpression.Sign.MINUS;
import static ComparisonExpression.Type.EQUAL;


public class ExpressionFormatterTest {
    @Test
    public void shouldFormatBooleanLiteral() {
        Assert.assertThat(ExpressionFormatter.formatExpression(new BooleanLiteral("true")), IsEqual.equalTo("true"));
    }

    @Test
    public void shouldFormatStringLiteral() {
        Assert.assertThat(ExpressionFormatter.formatExpression(new StringLiteral("string")), IsEqual.equalTo("'string'"));
    }

    @Test
    public void shouldFormatSubscriptExpression() {
        Assert.assertThat(ExpressionFormatter.formatExpression(new io.confluent.ksql.parser.tree.SubscriptExpression(new StringLiteral("abc"), new DoubleLiteral("3.0"))), IsEqual.equalTo("'abc'[3.0]"));
    }

    @Test
    public void shouldFormatLongLiteral() {
        Assert.assertThat(ExpressionFormatter.formatExpression(new LongLiteral(1)), IsEqual.equalTo("1"));
    }

    @Test
    public void shouldFormatDoubleLiteral() {
        Assert.assertThat(ExpressionFormatter.formatExpression(new DoubleLiteral("2.0")), IsEqual.equalTo("2.0"));
    }

    @Test
    public void shouldFormatDecimalLiteral() {
        Assert.assertThat(ExpressionFormatter.formatExpression(new DecimalLiteral("3.5")), IsEqual.equalTo("DECIMAL '3.5'"));
    }

    @Test
    public void shouldFormatTimeLiteral() {
        Assert.assertThat(ExpressionFormatter.formatExpression(new TimeLiteral("17/9/2017")), IsEqual.equalTo("TIME '17/9/2017'"));
    }

    @Test
    public void shouldFormatTimestampLiteral() {
        Assert.assertThat(ExpressionFormatter.formatExpression(new TimestampLiteral("15673839303")), IsEqual.equalTo("TIMESTAMP '15673839303'"));
    }

    @Test
    public void shouldFormatNullLiteral() {
        Assert.assertThat(ExpressionFormatter.formatExpression(new NullLiteral()), IsEqual.equalTo("null"));
    }

    @Test
    public void shouldFormatIntervalLiteralWithoutEnd() {
        Assert.assertThat(ExpressionFormatter.formatExpression(new io.confluent.ksql.parser.tree.IntervalLiteral("10/1/2012", Sign.POSITIVE, IntervalField.SECOND)), IsEqual.equalTo("INTERVAL  '10/1/2012' SECOND"));
    }

    @Test
    public void shouldFormatIntervalLiteralWithEnd() {
        Assert.assertThat(ExpressionFormatter.formatExpression(new io.confluent.ksql.parser.tree.IntervalLiteral("10/1/2012", Sign.POSITIVE, IntervalField.SECOND, Optional.of(MONTH))), IsEqual.equalTo("INTERVAL  '10/1/2012' SECOND TO MONTH"));
    }

    @Test
    public void shouldFormatQualifiedNameReference() {
        Assert.assertThat(ExpressionFormatter.formatExpression(new io.confluent.ksql.parser.tree.QualifiedNameReference(QualifiedName.of("name"))), IsEqual.equalTo("name"));
    }

    @Test
    public void shouldFormatSymbolReference() {
        Assert.assertThat(ExpressionFormatter.formatExpression(new SymbolReference("symbol")), IsEqual.equalTo("symbol"));
    }

    @Test
    public void shouldFormatDereferenceExpression() {
        Assert.assertThat(ExpressionFormatter.formatExpression(new io.confluent.ksql.parser.tree.DereferenceExpression(new StringLiteral("foo"), "name")), IsEqual.equalTo("'foo'->name"));
    }

    @Test
    public void shouldFormatFunctionCallWithCount() {
        final FunctionCall functionCall = new FunctionCall(QualifiedName.of("function", "COUNT"), Collections.singletonList(new StringLiteral("name")));
        Assert.assertThat(ExpressionFormatter.formatExpression(functionCall), IsEqual.equalTo("function.COUNT('name')"));
    }

    @Test
    public void shouldFormatFunctionCountStar() {
        final FunctionCall functionCall = new FunctionCall(QualifiedName.of("function", "COUNT"), Collections.emptyList());
        Assert.assertThat(ExpressionFormatter.formatExpression(functionCall), IsEqual.equalTo("function.COUNT(*)"));
    }

    @Test
    public void shouldFormatFunctionWithDistinct() {
        final FunctionCall functionCall = new FunctionCall(new NodeLocation(1, 1), QualifiedName.of("function", "COUNT"), true, Collections.singletonList(new StringLiteral("name")));
        Assert.assertThat(ExpressionFormatter.formatExpression(functionCall), IsEqual.equalTo("function.COUNT(DISTINCT 'name')"));
    }

    @Test
    public void shouldFormatLogicalBinaryExpression() {
        final LogicalBinaryExpression expression = new LogicalBinaryExpression(Type.AND, new StringLiteral("a"), new StringLiteral("b"));
        Assert.assertThat(ExpressionFormatter.formatExpression(expression), IsEqual.equalTo("('a' AND 'b')"));
    }

    @Test
    public void shouldFormatNotExpression() {
        Assert.assertThat(ExpressionFormatter.formatExpression(new io.confluent.ksql.parser.tree.NotExpression(new LongLiteral(1))), IsEqual.equalTo("(NOT 1)"));
    }

    @Test
    public void shouldFormatComparisonExpression() {
        Assert.assertThat(ExpressionFormatter.formatExpression(new io.confluent.ksql.parser.tree.ComparisonExpression(EQUAL, new LongLiteral(1), new LongLiteral(1))), IsEqual.equalTo("(1 = 1)"));
    }

    @Test
    public void shouldFormatIsNullPredicate() {
        Assert.assertThat(ExpressionFormatter.formatExpression(new io.confluent.ksql.parser.tree.IsNullPredicate(new StringLiteral("name"))), IsEqual.equalTo("('name' IS NULL)"));
    }

    @Test
    public void shouldFormatIsNotNullPredicate() {
        Assert.assertThat(ExpressionFormatter.formatExpression(new io.confluent.ksql.parser.tree.IsNotNullPredicate(new StringLiteral("name"))), IsEqual.equalTo("('name' IS NOT NULL)"));
    }

    @Test
    public void shouldFormatArithmeticUnary() {
        Assert.assertThat(ExpressionFormatter.formatExpression(new io.confluent.ksql.parser.tree.ArithmeticUnaryExpression(MINUS, new LongLiteral(1))), IsEqual.equalTo("-1"));
    }

    @Test
    public void shouldFormatArithmeticBinary() {
        Assert.assertThat(ExpressionFormatter.formatExpression(new io.confluent.ksql.parser.tree.ArithmeticBinaryExpression(ADD, new LongLiteral(1), new LongLiteral(2))), IsEqual.equalTo("(1 + 2)"));
    }

    @Test
    public void shouldFormatLikePredicate() {
        final LikePredicate predicate = new LikePredicate(new StringLiteral("string"), new StringLiteral("*"), new StringLiteral("\\"));
        Assert.assertThat(ExpressionFormatter.formatExpression(predicate), IsEqual.equalTo("(\'string\' LIKE \'*\' ESCAPE \'\\\')"));
    }

    @Test
    public void shouldFormatCast() {
        // Given:
        final Cast cast = new Cast(new NodeLocation(0, 0), new LongLiteral(1), PrimitiveType.of("DOUBLE"));
        // When:
        final String result = ExpressionFormatter.formatExpression(cast);
        // Then:
        Assert.assertThat(result, IsEqual.equalTo("CAST(1 AS DOUBLE)"));
    }

    @Test
    public void shouldFormatSearchedCaseExpression() {
        final SearchedCaseExpression expression = new SearchedCaseExpression(Collections.singletonList(new io.confluent.ksql.parser.tree.WhenClause(new StringLiteral("foo"), new LongLiteral(1))), Optional.empty());
        Assert.assertThat(ExpressionFormatter.formatExpression(expression), IsEqual.equalTo("(CASE WHEN 'foo' THEN 1 END)"));
    }

    @Test
    public void shouldFormatSearchedCaseExpressionWithDefaultValue() {
        final SearchedCaseExpression expression = new SearchedCaseExpression(Collections.singletonList(new io.confluent.ksql.parser.tree.WhenClause(new StringLiteral("foo"), new LongLiteral(1))), Optional.of(new LongLiteral(2)));
        Assert.assertThat(ExpressionFormatter.formatExpression(expression), IsEqual.equalTo("(CASE WHEN 'foo' THEN 1 ELSE 2 END)"));
    }

    @Test
    public void shouldFormatSimpleCaseExpressionWithDefaultValue() {
        final SimpleCaseExpression expression = new SimpleCaseExpression(new StringLiteral("operand"), Collections.singletonList(new io.confluent.ksql.parser.tree.WhenClause(new StringLiteral("foo"), new LongLiteral(1))), Optional.of(new LongLiteral(2)));
        Assert.assertThat(ExpressionFormatter.formatExpression(expression), IsEqual.equalTo("(CASE 'operand' WHEN 'foo' THEN 1 ELSE 2 END)"));
    }

    @Test
    public void shouldFormatSimpleCaseExpression() {
        final SimpleCaseExpression expression = new SimpleCaseExpression(new StringLiteral("operand"), Collections.singletonList(new io.confluent.ksql.parser.tree.WhenClause(new StringLiteral("foo"), new LongLiteral(1))), Optional.empty());
        Assert.assertThat(ExpressionFormatter.formatExpression(expression), IsEqual.equalTo("(CASE 'operand' WHEN 'foo' THEN 1 END)"));
    }

    @Test
    public void shouldFormatWhen() {
        Assert.assertThat(ExpressionFormatter.formatExpression(new io.confluent.ksql.parser.tree.WhenClause(new LongLiteral(1), new LongLiteral(2))), IsEqual.equalTo("WHEN 1 THEN 2"));
    }

    @Test
    public void shouldFormatBetweenPredicate() {
        final BetweenPredicate predicate = new BetweenPredicate(new StringLiteral("blah"), new LongLiteral(5), new LongLiteral(10));
        Assert.assertThat(ExpressionFormatter.formatExpression(predicate), IsEqual.equalTo("('blah' BETWEEN 5 AND 10)"));
    }

    @Test
    public void shouldFormatInPredicate() {
        Assert.assertThat(ExpressionFormatter.formatExpression(new io.confluent.ksql.parser.tree.InPredicate(new StringLiteral("foo"), new StringLiteral("a"))), IsEqual.equalTo("('foo' IN 'a')"));
    }

    @Test
    public void shouldFormatInListExpression() {
        Assert.assertThat(ExpressionFormatter.formatExpression(new io.confluent.ksql.parser.tree.InListExpression(Collections.singletonList(new StringLiteral("a")))), IsEqual.equalTo("('a')"));
    }

    @Test
    public void shouldFormatStruct() {
        final Struct struct = Struct.builder().addField("field1", PrimitiveType.of(INTEGER)).addField("field2", PrimitiveType.of(STRING)).build();
        Assert.assertThat(ExpressionFormatter.formatExpression(struct), IsEqual.equalTo("STRUCT<field1 INTEGER, field2 STRING>"));
    }

    @Test
    public void shouldFormatStructWithColumnWithReservedWordName() {
        final Struct struct = Struct.builder().addField("END", PrimitiveType.of(INTEGER)).build();
        Assert.assertThat(ExpressionFormatter.formatExpression(struct), IsEqual.equalTo("STRUCT<`END` INTEGER>"));
    }

    @Test
    public void shouldFormatMap() {
        final Map map = Map.of(PrimitiveType.of(BIGINT));
        Assert.assertThat(ExpressionFormatter.formatExpression(map), IsEqual.equalTo("MAP<VARCHAR, BIGINT>"));
    }

    @Test
    public void shouldFormatArray() {
        final Array array = Array.of(PrimitiveType.of(BOOLEAN));
        Assert.assertThat(ExpressionFormatter.formatExpression(array), IsEqual.equalTo("ARRAY<BOOLEAN>"));
    }
}

