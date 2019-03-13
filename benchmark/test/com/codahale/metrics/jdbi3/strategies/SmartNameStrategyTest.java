package com.codahale.metrics.jdbi3.strategies;


import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jdbi3.InstrumentedTimingCollector;
import java.util.concurrent.TimeUnit;
import org.jdbi.v3.core.extension.ExtensionMethod;
import org.junit.Test;
import org.mockito.Mockito;


public class SmartNameStrategyTest extends AbstractStrategyTest {
    private StatementNameStrategy smartNameStrategy = new SmartNameStrategy();

    private InstrumentedTimingCollector collector;

    @Test
    public void updatesTimerForSqlObjects() throws Exception {
        Mockito.when(ctx.getExtensionMethod()).thenReturn(new ExtensionMethod(getClass(), getClass().getMethod("updatesTimerForSqlObjects")));
        collector.collect(TimeUnit.SECONDS.toNanos(1), ctx);
        String name = smartNameStrategy.getStatementName(ctx);
        assertThat(name).isEqualTo(MetricRegistry.name(getClass(), "updatesTimerForSqlObjects"));
        assertThat(getTimerMaxValue(name)).isEqualTo(1000000000);
    }

    @Test
    public void updatesTimerForRawSql() throws Exception {
        collector.collect(TimeUnit.SECONDS.toNanos(2), ctx);
        String name = smartNameStrategy.getStatementName(ctx);
        assertThat(name).isEqualTo(MetricRegistry.name("sql", "raw"));
        assertThat(getTimerMaxValue(name)).isEqualTo(2000000000);
    }

    @Test
    public void updatesTimerForNoRawSql() throws Exception {
        Mockito.reset(ctx);
        collector.collect(TimeUnit.SECONDS.toNanos(2), ctx);
        String name = smartNameStrategy.getStatementName(ctx);
        assertThat(name).isEqualTo(MetricRegistry.name("sql", "empty"));
        assertThat(getTimerMaxValue(name)).isEqualTo(2000000000);
    }

    @Test
    public void updatesTimerForContextClass() throws Exception {
        Mockito.when(ctx.getExtensionMethod()).thenReturn(new ExtensionMethod(getClass(), getClass().getMethod("updatesTimerForContextClass")));
        collector.collect(TimeUnit.SECONDS.toNanos(3), ctx);
        String name = smartNameStrategy.getStatementName(ctx);
        assertThat(name).isEqualTo(MetricRegistry.name(getClass(), "updatesTimerForContextClass"));
        assertThat(getTimerMaxValue(name)).isEqualTo(3000000000L);
    }
}

