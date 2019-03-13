/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog.plugins.pipelineprocessor.parser;


import org.graylog.plugins.pipelineprocessor.BaseParserTest;
import org.graylog.plugins.pipelineprocessor.ast.Rule;
import org.graylog.plugins.pipelineprocessor.ast.expressions.AndExpression;
import org.graylog.plugins.pipelineprocessor.ast.expressions.BooleanExpression;
import org.graylog.plugins.pipelineprocessor.ast.expressions.ComparisonExpression;
import org.graylog.plugins.pipelineprocessor.ast.expressions.EqualityExpression;
import org.graylog.plugins.pipelineprocessor.ast.expressions.LogicalExpression;
import org.graylog.plugins.pipelineprocessor.ast.expressions.NotExpression;
import org.graylog.plugins.pipelineprocessor.ast.expressions.OrExpression;
import org.graylog2.plugin.Message;
import org.graylog2.plugin.Tools;
import org.junit.Test;


public class PrecedenceTest extends BaseParserTest {
    @Test
    public void orVsEquality() {
        final Rule rule = PrecedenceTest.parseRule("rule \"test\" when true == false || true then end");
        final LogicalExpression when = rule.when();
        assertThat(when).isInstanceOf(OrExpression.class);
        OrExpression orEprx = ((OrExpression) (when));
        assertThat(orEprx.left()).isInstanceOf(EqualityExpression.class);
        assertThat(orEprx.right()).isInstanceOf(BooleanExpression.class);
    }

    @Test
    public void andVsEquality() {
        final Rule rule = PrecedenceTest.parseRule("rule \"test\" when true == false && true then end");
        final LogicalExpression when = rule.when();
        assertThat(when).isInstanceOf(AndExpression.class);
        AndExpression andExpr = ((AndExpression) (when));
        assertThat(andExpr.left()).isInstanceOf(EqualityExpression.class);
        assertThat(andExpr.right()).isInstanceOf(BooleanExpression.class);
    }

    @Test
    public void parenGroup() {
        final Rule rule = PrecedenceTest.parseRule("rule \"test\" when true == (false == false) then end");
        final LogicalExpression when = rule.when();
        assertThat(when).isInstanceOf(EqualityExpression.class);
        EqualityExpression topEqual = ((EqualityExpression) (when));
        assertThat(topEqual.left()).isInstanceOf(BooleanExpression.class);
        assertThat(topEqual.right()).isInstanceOf(EqualityExpression.class);
        final BooleanExpression trueExpr = ((BooleanExpression) (topEqual.left()));
        assertThat(trueExpr.evaluateBool(null)).isTrue();
        final EqualityExpression falseFalse = ((EqualityExpression) (topEqual.right()));
        assertThat(falseFalse.evaluateBool(null)).isTrue();
    }

    @Test
    public void comparisonVsEqual() {
        final Rule rule = PrecedenceTest.parseRule("rule \"test\" when 1 > 2 == false then end");
        final LogicalExpression when = rule.when();
        assertThat(when).isInstanceOf(EqualityExpression.class);
        EqualityExpression topEqual = ((EqualityExpression) (when));
        assertThat(topEqual.left()).isInstanceOf(ComparisonExpression.class);
        assertThat(topEqual.right()).isInstanceOf(BooleanExpression.class);
    }

    @Test
    public void notVsAndOr() {
        final Rule rule = PrecedenceTest.parseRule("rule \"test\" when !true && false then end");
        final LogicalExpression when = rule.when();
        assertThat(when).isInstanceOf(AndExpression.class);
        AndExpression and = ((AndExpression) (when));
        assertThat(and.left()).isInstanceOf(NotExpression.class);
        assertThat(and.right()).isInstanceOf(BooleanExpression.class);
    }

    @Test(expected = ParseException.class)
    public void literalsMustBeQuotedInFieldref() {
        final Rule rule = PrecedenceTest.parseRule("rule \"test\" when to_string($message.true) == to_string($message.false) then end");
    }

    @Test
    public void quotedLiteralInFieldRef() {
        final Rule rule = PrecedenceTest.parseRule("rule \"test\" when to_string($message.`true`) == \"true\" then end");
        final Message message = new Message("hallo", "test", Tools.nowUTC());
        message.addField("true", "true");
        final Message result = evaluateRule(rule, message);
        assertThat(result).isNotNull();
    }
}

