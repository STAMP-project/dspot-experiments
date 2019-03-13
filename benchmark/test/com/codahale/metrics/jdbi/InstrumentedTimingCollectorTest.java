package com.codahale.metrics.jdbi;


import NameStrategies.STATEMENT_CLASS;
import NameStrategies.STATEMENT_GROUP;
import NameStrategies.STATEMENT_NAME;
import NameStrategies.STATEMENT_TYPE;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.jdbi.strategies.ShortNameStrategy;
import com.codahale.metrics.jdbi.strategies.SmartNameStrategy;
import com.codahale.metrics.jdbi.strategies.StatementNameStrategy;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.mockito.Mockito;
import org.skife.jdbi.v2.StatementContext;


public class InstrumentedTimingCollectorTest {
    private final MetricRegistry registry = new MetricRegistry();

    @Test
    public void updatesTimerForSqlObjects() throws Exception {
        final StatementNameStrategy strategy = new SmartNameStrategy();
        final InstrumentedTimingCollector collector = new InstrumentedTimingCollector(registry, strategy);
        final StatementContext ctx = Mockito.mock(StatementContext.class);
        Mockito.doReturn("SELECT 1").when(ctx).getRawSql();
        Mockito.doReturn(getClass()).when(ctx).getSqlObjectType();
        Mockito.doReturn(getClass().getMethod("updatesTimerForSqlObjects")).when(ctx).getSqlObjectMethod();
        collector.collect(TimeUnit.SECONDS.toNanos(1), ctx);
        final String name = strategy.getStatementName(ctx);
        final Timer timer = registry.timer(name);
        assertThat(name).isEqualTo(MetricRegistry.name(getClass(), "updatesTimerForSqlObjects"));
        assertThat(timer.getSnapshot().getMax()).isEqualTo(1000000000);
    }

    @Test
    public void updatesTimerForSqlObjectsWithoutMethod() throws Exception {
        final StatementNameStrategy strategy = new SmartNameStrategy();
        final InstrumentedTimingCollector collector = new InstrumentedTimingCollector(registry, strategy);
        final StatementContext ctx = Mockito.mock(StatementContext.class);
        Mockito.doReturn("SELECT 1").when(ctx).getRawSql();
        Mockito.doReturn(getClass()).when(ctx).getSqlObjectType();
        collector.collect(TimeUnit.SECONDS.toNanos(1), ctx);
        final String name = strategy.getStatementName(ctx);
        final Timer timer = registry.timer(name);
        assertThat(name).isEqualTo(MetricRegistry.name(getClass(), "SELECT 1"));
        assertThat(timer.getSnapshot().getMax()).isEqualTo(1000000000);
    }

    @Test
    public void updatesTimerForRawSql() throws Exception {
        final StatementNameStrategy strategy = new SmartNameStrategy();
        final InstrumentedTimingCollector collector = new InstrumentedTimingCollector(registry, strategy);
        final StatementContext ctx = Mockito.mock(StatementContext.class);
        Mockito.doReturn("SELECT 1").when(ctx).getRawSql();
        collector.collect(TimeUnit.SECONDS.toNanos(2), ctx);
        final String name = strategy.getStatementName(ctx);
        final Timer timer = registry.timer(name);
        assertThat(name).isEqualTo(MetricRegistry.name("sql", "raw", "SELECT 1"));
        assertThat(timer.getSnapshot().getMax()).isEqualTo(2000000000);
    }

    @Test
    public void updatesTimerForNoRawSql() throws Exception {
        final StatementNameStrategy strategy = new SmartNameStrategy();
        final InstrumentedTimingCollector collector = new InstrumentedTimingCollector(registry, strategy);
        final StatementContext ctx = Mockito.mock(StatementContext.class);
        collector.collect(TimeUnit.SECONDS.toNanos(2), ctx);
        final String name = strategy.getStatementName(ctx);
        final Timer timer = registry.timer(name);
        assertThat(name).isEqualTo(MetricRegistry.name("sql", "empty"));
        assertThat(timer.getSnapshot().getMax()).isEqualTo(2000000000);
    }

    @Test
    public void updatesTimerForNonSqlishRawSql() throws Exception {
        final StatementNameStrategy strategy = new SmartNameStrategy();
        final InstrumentedTimingCollector collector = new InstrumentedTimingCollector(registry, strategy);
        final StatementContext ctx = Mockito.mock(StatementContext.class);
        Mockito.doReturn("don't know what it is but it's not SQL").when(ctx).getRawSql();
        collector.collect(TimeUnit.SECONDS.toNanos(3), ctx);
        final String name = strategy.getStatementName(ctx);
        final Timer timer = registry.timer(name);
        assertThat(name).isEqualTo(MetricRegistry.name("sql", "raw", "don't know what it is but it's not SQL"));
        assertThat(timer.getSnapshot().getMax()).isEqualTo(3000000000L);
    }

    @Test
    public void updatesTimerForContextClass() throws Exception {
        final StatementNameStrategy strategy = new SmartNameStrategy();
        final InstrumentedTimingCollector collector = new InstrumentedTimingCollector(registry, strategy);
        final StatementContext ctx = Mockito.mock(StatementContext.class);
        Mockito.doReturn("SELECT 1").when(ctx).getRawSql();
        Mockito.doReturn(getClass().getName()).when(ctx).getAttribute(STATEMENT_CLASS);
        Mockito.doReturn("updatesTimerForContextClass").when(ctx).getAttribute(STATEMENT_NAME);
        collector.collect(TimeUnit.SECONDS.toNanos(3), ctx);
        final String name = strategy.getStatementName(ctx);
        final Timer timer = registry.timer(name);
        assertThat(name).isEqualTo(MetricRegistry.name(getClass(), "updatesTimerForContextClass"));
        assertThat(timer.getSnapshot().getMax()).isEqualTo(3000000000L);
    }

    @Test
    public void updatesTimerForTemplateFile() throws Exception {
        final StatementNameStrategy strategy = new SmartNameStrategy();
        final InstrumentedTimingCollector collector = new InstrumentedTimingCollector(registry, strategy);
        final StatementContext ctx = Mockito.mock(StatementContext.class);
        Mockito.doReturn("SELECT 1").when(ctx).getRawSql();
        Mockito.doReturn("foo/bar.stg").when(ctx).getAttribute(STATEMENT_GROUP);
        Mockito.doReturn("updatesTimerForTemplateFile").when(ctx).getAttribute(STATEMENT_NAME);
        collector.collect(TimeUnit.SECONDS.toNanos(4), ctx);
        final String name = strategy.getStatementName(ctx);
        final Timer timer = registry.timer(name);
        assertThat(name).isEqualTo(MetricRegistry.name("foo", "bar", "updatesTimerForTemplateFile"));
        assertThat(timer.getSnapshot().getMax()).isEqualTo(4000000000L);
    }

    @Test
    public void updatesTimerForContextGroupAndName() throws Exception {
        final StatementNameStrategy strategy = new SmartNameStrategy();
        final InstrumentedTimingCollector collector = new InstrumentedTimingCollector(registry, strategy);
        final StatementContext ctx = Mockito.mock(StatementContext.class);
        Mockito.doReturn("SELECT 1").when(ctx).getRawSql();
        Mockito.doReturn("my-group").when(ctx).getAttribute(STATEMENT_GROUP);
        Mockito.doReturn("updatesTimerForContextGroupAndName").when(ctx).getAttribute(STATEMENT_NAME);
        collector.collect(TimeUnit.SECONDS.toNanos(4), ctx);
        final String name = strategy.getStatementName(ctx);
        final Timer timer = registry.timer(name);
        assertThat(name).isEqualTo(MetricRegistry.name("my-group", "updatesTimerForContextGroupAndName", ""));
        assertThat(timer.getSnapshot().getMax()).isEqualTo(4000000000L);
    }

    @Test
    public void updatesTimerForContextGroupTypeAndName() throws Exception {
        final StatementNameStrategy strategy = new SmartNameStrategy();
        final InstrumentedTimingCollector collector = new InstrumentedTimingCollector(registry, strategy);
        final StatementContext ctx = Mockito.mock(StatementContext.class);
        Mockito.doReturn("SELECT 1").when(ctx).getRawSql();
        Mockito.doReturn("my-group").when(ctx).getAttribute(STATEMENT_GROUP);
        Mockito.doReturn("my-type").when(ctx).getAttribute(STATEMENT_TYPE);
        Mockito.doReturn("updatesTimerForContextGroupTypeAndName").when(ctx).getAttribute(STATEMENT_NAME);
        collector.collect(TimeUnit.SECONDS.toNanos(5), ctx);
        final String name = strategy.getStatementName(ctx);
        final Timer timer = registry.timer(name);
        assertThat(name).isEqualTo(MetricRegistry.name("my-group", "my-type", "updatesTimerForContextGroupTypeAndName"));
        assertThat(timer.getSnapshot().getMax()).isEqualTo(5000000000L);
    }

    @Test
    public void updatesTimerForShortSqlObjectStrategy() throws Exception {
        final StatementNameStrategy strategy = new ShortNameStrategy("jdbi");
        final InstrumentedTimingCollector collector = new InstrumentedTimingCollector(registry, strategy);
        final StatementContext ctx = Mockito.mock(StatementContext.class);
        Mockito.doReturn("SELECT 1").when(ctx).getRawSql();
        Mockito.doReturn(getClass()).when(ctx).getSqlObjectType();
        Mockito.doReturn(getClass().getMethod("updatesTimerForShortSqlObjectStrategy")).when(ctx).getSqlObjectMethod();
        collector.collect(TimeUnit.SECONDS.toNanos(1), ctx);
        final String name = strategy.getStatementName(ctx);
        final Timer timer = registry.timer(name);
        assertThat(name).isEqualTo(MetricRegistry.name("jdbi", getClass().getSimpleName(), "updatesTimerForShortSqlObjectStrategy"));
        assertThat(timer.getSnapshot().getMax()).isEqualTo(1000000000);
    }

    @Test
    public void updatesTimerForShortContextClassStrategy() throws Exception {
        final StatementNameStrategy strategy = new ShortNameStrategy("jdbi");
        final InstrumentedTimingCollector collector = new InstrumentedTimingCollector(registry, strategy);
        final StatementContext ctx = Mockito.mock(StatementContext.class);
        Mockito.doReturn("SELECT 1").when(ctx).getRawSql();
        Mockito.doReturn(getClass().getName()).when(ctx).getAttribute(STATEMENT_CLASS);
        Mockito.doReturn("updatesTimerForShortContextClassStrategy").when(ctx).getAttribute(STATEMENT_NAME);
        collector.collect(TimeUnit.SECONDS.toNanos(3), ctx);
        final String name = strategy.getStatementName(ctx);
        final Timer timer = registry.timer(name);
        assertThat(name).isEqualTo(MetricRegistry.name("jdbi", getClass().getSimpleName(), "updatesTimerForShortContextClassStrategy"));
        assertThat(timer.getSnapshot().getMax()).isEqualTo(3000000000L);
    }
}

