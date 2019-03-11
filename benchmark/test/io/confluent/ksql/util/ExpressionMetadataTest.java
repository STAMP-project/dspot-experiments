package io.confluent.ksql.util;


import com.google.common.collect.ImmutableList;
import io.confluent.ksql.GenericRow;
import io.confluent.ksql.function.udf.Kudf;
import io.confluent.ksql.parser.tree.Expression;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.connect.data.Schema;
import org.codehaus.commons.compiler.IExpressionEvaluator;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class ExpressionMetadataTest {
    private static final Long RETURN_VALUE = 12345L;

    @Mock
    private IExpressionEvaluator expressionEvaluator;

    private List<Kudf> udfs;

    @Mock
    private Kudf udf;

    private final Schema expressionType = Schema.OPTIONAL_INT64_SCHEMA;

    @Mock
    private GenericRowValueTypeEnforcer typeEnforcer;

    @Mock
    private Object parameter1;

    @Mock
    private Object parameter2;

    @Mock
    private Expression expression;

    private ExpressionMetadata expressionMetadata;

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void shouldEvaluateExpressionWithNoUdfsCorrectly() throws InvocationTargetException {
        // Given:
        expressionMetadata = new ExpressionMetadata(expressionEvaluator, ImmutableList.of(1, 0), Collections.emptyList(), expressionType, typeEnforcer, expression);
        // When:
        final Object result = expressionMetadata.evaluate(new GenericRow(123, 456));
        // Then:
        Assert.assertThat(result, Matchers.equalTo(ExpressionMetadataTest.RETURN_VALUE));
        Mockito.verify(typeEnforcer, Mockito.times(1)).enforceFieldType(1, 456);
        Mockito.verify(typeEnforcer, Mockito.times(1)).enforceFieldType(0, 123);
        Mockito.verify(expressionEvaluator).evaluate(new Object[]{ parameter1, parameter2 });
    }

    @Test
    public void shouldEvaluateExpressionWithUdfsCorrectly() throws InvocationTargetException {
        // Given:
        expressionMetadata = new ExpressionMetadata(expressionEvaluator, ImmutableList.of((-1), 0), udfs, expressionType, typeEnforcer, expression);
        // When:
        final Object result = expressionMetadata.evaluate(new GenericRow(123));
        // Then:
        Assert.assertThat(result, Matchers.equalTo(ExpressionMetadataTest.RETURN_VALUE));
        Mockito.verify(typeEnforcer, Mockito.times(1)).enforceFieldType(0, 123);
        Mockito.verify(expressionEvaluator).evaluate(new Object[]{ udf, parameter1 });
    }

    @Test
    public void shouldPerformThreadSafeParameterEvaluation() throws InterruptedException, InvocationTargetException {
        // Given:
        final CountDownLatch threadLatch = new CountDownLatch(1);
        final CountDownLatch mainLatch = new CountDownLatch(1);
        final Object thread1Param1 = 1;
        final Object thread1Param2 = 2;
        final Object thread2Param1 = 3;
        final Object thread2Param2 = 4;
        Mockito.reset(typeEnforcer);
        Mockito.when(typeEnforcer.enforceFieldType(0, 123)).thenReturn(thread1Param1);
        Mockito.when(typeEnforcer.enforceFieldType(1, 456)).thenAnswer(( invocation) -> {
            threadLatch.countDown();
            assertThat(mainLatch.await(10, TimeUnit.SECONDS), is(true));
            return thread1Param2;
        });
        Mockito.when(typeEnforcer.enforceFieldType(0, 100)).thenReturn(thread2Param1);
        Mockito.when(typeEnforcer.enforceFieldType(1, 200)).thenReturn(thread2Param2);
        expressionMetadata = new ExpressionMetadata(expressionEvaluator, ImmutableList.of(0, 1), Collections.emptyList(), expressionType, typeEnforcer, expression);
        // When:
        final Thread thread = new Thread(() -> expressionMetadata.evaluate(new GenericRow(123, 456)));
        thread.start();
        Assert.assertThat(threadLatch.await(10, TimeUnit.SECONDS), Matchers.is(true));
        expressionMetadata.evaluate(new GenericRow(100, 200));
        mainLatch.countDown();
        thread.join();
        // Then:
        Mockito.verify(expressionEvaluator, Mockito.times(1)).evaluate(new Object[]{ thread1Param1, thread1Param2 });
        Mockito.verify(expressionEvaluator, Mockito.times(1)).evaluate(new Object[]{ thread2Param1, thread2Param2 });
    }
}

