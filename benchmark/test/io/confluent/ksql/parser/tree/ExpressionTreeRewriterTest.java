/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.confluent.ksql.parser.tree;


import com.google.common.collect.ImmutableList;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ExpressionTreeRewriterTest {
    private static final DereferenceExpression DEREF_0 = new DereferenceExpression(new QualifiedNameReference(QualifiedName.of("Bob")), "f0");

    private static final DereferenceExpression DEREF_1 = new DereferenceExpression(new QualifiedNameReference(QualifiedName.of("Jane")), "f1");

    private static final DereferenceExpression DEREF_2 = new DereferenceExpression(new QualifiedNameReference(QualifiedName.of("Vic")), "f2");

    @Mock
    private ExpressionRewriter<String> rewriter;

    @Test
    public void shouldRewriteFunctionCall() {
        // Given:
        final FunctionCall original = ExpressionTreeRewriterTest.givenFunctionCall();
        final FunctionCall expected = Mockito.mock(FunctionCall.class);
        Mockito.when(rewriter.rewriteFunctionCall(ArgumentMatchers.eq(original), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(expected);
        // When:
        final FunctionCall result = ExpressionTreeRewriter.rewriteWith(rewriter, original);
        // Then:
        MatcherAssert.assertThat(result, Matchers.is(expected));
    }

    @Test
    public void shouldRewriteFunctionCallArguments() {
        // Given:
        final FunctionCall original = ExpressionTreeRewriterTest.givenFunctionCall();
        Mockito.when(rewriter.rewriteDereferenceExpression(ArgumentMatchers.eq(ExpressionTreeRewriterTest.DEREF_0), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(ExpressionTreeRewriterTest.DEREF_2);
        // When:
        final FunctionCall result = ExpressionTreeRewriter.rewriteWith(rewriter, original);
        // Then:
        MatcherAssert.assertThat(result.getName(), Matchers.is(original.getName()));
        MatcherAssert.assertThat(result.getLocation(), Matchers.is(original.getLocation()));
        MatcherAssert.assertThat(result.isDistinct(), Matchers.is(original.isDistinct()));
        MatcherAssert.assertThat(result.getArguments(), Matchers.is(ImmutableList.of(ExpressionTreeRewriterTest.DEREF_2, ExpressionTreeRewriterTest.DEREF_1)));
    }
}

