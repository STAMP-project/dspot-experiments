package com.querydsl.core.support;


import Ops.CONCAT;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import javax.annotation.Nullable;
import org.junit.Assert;
import org.junit.Test;


public class ReplaceVisitorTest {
    private static final ReplaceVisitor<Void> visitor = new ReplaceVisitor<Void>() {
        public Expression<?> visit(Path<?> expr, @Nullable
        Void context) {
            if (expr.getMetadata().isRoot()) {
                return ExpressionUtils.path(expr.getType(), ((expr.getMetadata().getName()) + "_"));
            } else {
                return super.visit(expr, context);
            }
        }
    };

    @Test
    public void operation() {
        Expression<String> str = Expressions.stringPath(ExpressionUtils.path(Object.class, "customer"), "name");
        Expression<String> str2 = Expressions.stringPath("str");
        Expression<String> concat = Expressions.stringOperation(CONCAT, str, str2);
        Assert.assertEquals("customer.name + str", concat.toString());
        Assert.assertEquals("customer_.name + str_", concat.accept(ReplaceVisitorTest.visitor, null).toString());
    }

    @Test
    public void templateExpression() {
        Expression<String> str = Expressions.stringPath(ExpressionUtils.path(Object.class, "customer"), "name");
        Expression<String> str2 = Expressions.stringPath("str");
        Expression<String> concat = Expressions.stringTemplate("{0} + {1}", str, str2);
        Assert.assertEquals("customer.name + str", concat.toString());
        Assert.assertEquals("customer_.name + str_", concat.accept(ReplaceVisitorTest.visitor, null).toString());
    }
}

