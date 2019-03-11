/**
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.core.types;


import com.google.common.collect.ImmutableSet;
import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.QueryException;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ExpressionUtilsTest {
    private static final StringPath str = Expressions.stringPath("str");

    private static final StringPath str2 = Expressions.stringPath("str2");

    @Test
    public void likeToRegex() {
        Assert.assertEquals(".*", regex(ConstantImpl.create("%")));
        Assert.assertEquals("^abc.*", regex(ConstantImpl.create("abc%")));
        Assert.assertEquals(".*abc$", regex(ConstantImpl.create("%abc")));
        Assert.assertEquals("^.$", regex(ConstantImpl.create("_")));
        StringPath path = Expressions.stringPath("path");
        Assert.assertEquals("path + .*", regex(path.append("%")));
        Assert.assertEquals(".* + path", regex(path.prepend("%")));
        Assert.assertEquals("path + .", regex(path.append("_")));
        Assert.assertEquals(". + path", regex(path.prepend("_")));
    }

    @Test
    public void likeToRegex_escape() {
        Assert.assertEquals("^\\.$", regex(ConstantImpl.create(".")));
    }

    @Test
    public void regexToLike() {
        Assert.assertEquals("%", like(ConstantImpl.create(".*")));
        Assert.assertEquals("_", like(ConstantImpl.create(".")));
        Assert.assertEquals(".", like(ConstantImpl.create("\\.")));
        StringPath path = Expressions.stringPath("path");
        Assert.assertEquals("path + %", like(path.append(".*")));
        Assert.assertEquals("% + path", like(path.prepend(".*")));
        Assert.assertEquals("path + _", like(path.append(".")));
        Assert.assertEquals("_ + path", like(path.prepend(".")));
    }

    @Test(expected = QueryException.class)
    public void regexToLike_fail() {
        like(ConstantImpl.create("a*"));
    }

    @Test(expected = QueryException.class)
    public void regexToLike_fail2() {
        like(ConstantImpl.create("\\d"));
    }

    @Test(expected = QueryException.class)
    public void regexToLike_fail3() {
        like(ConstantImpl.create("[ab]"));
    }

    @Test
    public void count() {
        Assert.assertEquals("count(str)", ExpressionUtils.count(ExpressionUtilsTest.str).toString());
    }

    @Test
    public void eqConst() {
        Assert.assertEquals("str = X", ExpressionUtils.eqConst(ExpressionUtilsTest.str, "X").toString());
    }

    @Test
    public void eq() {
        Assert.assertEquals("str = str2", ExpressionUtils.eq(ExpressionUtilsTest.str, ExpressionUtilsTest.str2).toString());
    }

    @Test
    public void in() {
        Assert.assertEquals("str in [a, b, c]", ExpressionUtils.in(ExpressionUtilsTest.str, Arrays.asList("a", "b", "c")).toString());
    }

    @Test
    public void in_subQuery() {
        String s = ExpressionUtils.in(ExpressionUtilsTest.str, new SubQueryExpressionImpl<String>(String.class, new DefaultQueryMetadata())).toString();
        Assert.assertTrue(s.startsWith("str in com.querydsl.core.DefaultQueryMetadata@c"));
    }

    @Test
    public void inAny() {
        ImmutableSet<List<String>> of = ImmutableSet.of(Arrays.asList("a", "b", "c"), Arrays.asList("d", "e", "f"));
        Assert.assertEquals("str in [a, b, c] || str in [d, e, f]", ExpressionUtils.inAny(ExpressionUtilsTest.str, of).toString());
    }

    @Test
    public void isNull() {
        Assert.assertEquals("str is null", ExpressionUtils.isNull(ExpressionUtilsTest.str).toString());
    }

    @Test
    public void isNotNull() {
        Assert.assertEquals("str is not null", ExpressionUtils.isNotNull(ExpressionUtilsTest.str).toString());
    }

    @Test
    public void neConst() {
        Assert.assertEquals("str != X", ExpressionUtils.neConst(ExpressionUtilsTest.str, "X").toString());
    }

    @Test
    public void ne() {
        Assert.assertEquals("str != str2", ExpressionUtils.ne(ExpressionUtilsTest.str, ExpressionUtilsTest.str2).toString());
    }

    @Test
    public void notInAny() {
        ImmutableSet<List<String>> of = ImmutableSet.of(Arrays.asList("a", "b", "c"), Arrays.asList("d", "e", "f"));
        Assert.assertEquals("str not in [a, b, c] && str not in [d, e, f]", ExpressionUtils.notInAny(ExpressionUtilsTest.str, of).toString());
    }

    @Test
    public void notIn_subQuery() {
        String s = ExpressionUtils.notIn(ExpressionUtilsTest.str, new SubQueryExpressionImpl<String>(String.class, new DefaultQueryMetadata())).toString();
        Assert.assertTrue(s.startsWith("str not in com.querydsl.core.DefaultQueryMetadata@c"));
    }
}

