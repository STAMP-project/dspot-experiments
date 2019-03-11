package com.codahale.metrics.jdbi3.strategies;


import com.codahale.metrics.MetricRegistry;
import org.jdbi.v3.core.extension.ExtensionMethod;
import org.junit.Test;
import org.mockito.Mockito;


public class BasicSqlNameStrategyTest extends AbstractStrategyTest {
    private BasicSqlNameStrategy basicSqlNameStrategy = new BasicSqlNameStrategy();

    @Test
    public void producesMethodNameAsMetric() throws Exception {
        Mockito.when(ctx.getExtensionMethod()).thenReturn(new ExtensionMethod(getClass(), getClass().getMethod("producesMethodNameAsMetric")));
        String name = basicSqlNameStrategy.getStatementName(ctx);
        assertThat(name).isEqualTo(MetricRegistry.name(getClass(), "producesMethodNameAsMetric"));
    }
}

