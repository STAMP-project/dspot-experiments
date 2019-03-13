package io.dropwizard.jdbi;


import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.jdbi.args.GuavaOptionalArgumentFactory;
import io.dropwizard.jdbi.args.GuavaOptionalInstantArgumentFactory;
import io.dropwizard.jdbi.args.GuavaOptionalJodaTimeArgumentFactory;
import io.dropwizard.jdbi.args.GuavaOptionalLocalDateArgumentFactory;
import io.dropwizard.jdbi.args.GuavaOptionalLocalDateTimeArgumentFactory;
import io.dropwizard.jdbi.args.GuavaOptionalOffsetTimeArgumentFactory;
import io.dropwizard.jdbi.args.GuavaOptionalZonedTimeArgumentFactory;
import io.dropwizard.jdbi.args.InstantArgumentFactory;
import io.dropwizard.jdbi.args.InstantMapper;
import io.dropwizard.jdbi.args.JodaDateTimeArgumentFactory;
import io.dropwizard.jdbi.args.JodaDateTimeMapper;
import io.dropwizard.jdbi.args.LocalDateArgumentFactory;
import io.dropwizard.jdbi.args.LocalDateMapper;
import io.dropwizard.jdbi.args.LocalDateTimeArgumentFactory;
import io.dropwizard.jdbi.args.LocalDateTimeMapper;
import io.dropwizard.jdbi.args.OffsetDateTimeArgumentFactory;
import io.dropwizard.jdbi.args.OffsetDateTimeMapper;
import io.dropwizard.jdbi.args.OptionalArgumentFactory;
import io.dropwizard.jdbi.args.OptionalDoubleArgumentFactory;
import io.dropwizard.jdbi.args.OptionalDoubleMapper;
import io.dropwizard.jdbi.args.OptionalInstantArgumentFactory;
import io.dropwizard.jdbi.args.OptionalIntArgumentFactory;
import io.dropwizard.jdbi.args.OptionalIntMapper;
import io.dropwizard.jdbi.args.OptionalJodaTimeArgumentFactory;
import io.dropwizard.jdbi.args.OptionalLocalDateArgumentFactory;
import io.dropwizard.jdbi.args.OptionalLocalDateTimeArgumentFactory;
import io.dropwizard.jdbi.args.OptionalLongArgumentFactory;
import io.dropwizard.jdbi.args.OptionalLongMapper;
import io.dropwizard.jdbi.args.OptionalOffsetDateTimeArgumentFactory;
import io.dropwizard.jdbi.args.OptionalZonedDateTimeArgumentFactory;
import io.dropwizard.jdbi.args.ZonedDateTimeArgumentFactory;
import io.dropwizard.jdbi.args.ZonedDateTimeMapper;
import java.util.ArrayDeque;
import java.util.Deque;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.tweak.ArgumentFactory;
import org.skife.jdbi.v2.tweak.ContainerFactory;
import org.skife.jdbi.v2.tweak.ResultColumnMapper;


public class DBIFactoryTest {
    @Test
    public void testDefaultConfiguration() {
        final DBI dbi = Mockito.mock(DBI.class);
        final PooledDataSourceFactory configuration = Mockito.mock(PooledDataSourceFactory.class);
        // Capture what is configured
        final Deque<Class<?>> af = new ArrayDeque<>();
        final Deque<Class<?>> cm = new ArrayDeque<>();
        final Deque<Class<?>> cf = new ArrayDeque<>();
        Mockito.doAnswer(( invocation) -> {
            final ArgumentFactory<?> x = invocation.getArgument(0);
            af.addLast(x.getClass());
            return null;
        }).when(dbi).registerArgumentFactory(Mockito.isA(ArgumentFactory.class));
        Mockito.doAnswer(( invocation) -> {
            final ResultColumnMapper<?> x = invocation.getArgument(0);
            cm.addLast(x.getClass());
            return null;
        }).when(dbi).registerColumnMapper(Mockito.isA(ResultColumnMapper.class));
        Mockito.doAnswer(( invocation) -> {
            final ContainerFactory<?> x = invocation.getArgument(0);
            cf.addLast(x.getClass());
            return null;
        }).when(dbi).registerContainerFactory(Mockito.isA(ContainerFactory.class));
        Mockito.when(configuration.getDriverClass()).thenReturn("io.dropwizard.fake.driver.Driver");
        final DBIFactory test = new DBIFactory();
        test.configure(dbi, configuration);
        // Verify we actually captured something
        Assertions.assertFalse(cm.isEmpty());
        Assertions.assertFalse(cf.isEmpty());
        Assertions.assertFalse(af.isEmpty());
        // Verify Argument Factory Order
        Assertions.assertEquals(GuavaOptionalArgumentFactory.class, af.removeFirst());
        Assertions.assertEquals(OptionalArgumentFactory.class, af.removeFirst());
        Assertions.assertEquals(OptionalDoubleArgumentFactory.class, af.removeFirst());
        Assertions.assertEquals(OptionalIntArgumentFactory.class, af.removeFirst());
        Assertions.assertEquals(OptionalLongArgumentFactory.class, af.removeFirst());
        Assertions.assertEquals(JodaDateTimeArgumentFactory.class, af.removeFirst());
        Assertions.assertEquals(LocalDateArgumentFactory.class, af.removeFirst());
        Assertions.assertEquals(LocalDateTimeArgumentFactory.class, af.removeFirst());
        Assertions.assertEquals(InstantArgumentFactory.class, af.removeFirst());
        Assertions.assertEquals(OffsetDateTimeArgumentFactory.class, af.removeFirst());
        Assertions.assertEquals(ZonedDateTimeArgumentFactory.class, af.removeFirst());
        Assertions.assertEquals(GuavaOptionalJodaTimeArgumentFactory.class, af.removeFirst());
        Assertions.assertEquals(GuavaOptionalLocalDateArgumentFactory.class, af.removeFirst());
        Assertions.assertEquals(GuavaOptionalLocalDateTimeArgumentFactory.class, af.removeFirst());
        Assertions.assertEquals(GuavaOptionalInstantArgumentFactory.class, af.removeFirst());
        Assertions.assertEquals(GuavaOptionalOffsetTimeArgumentFactory.class, af.removeFirst());
        Assertions.assertEquals(GuavaOptionalZonedTimeArgumentFactory.class, af.removeFirst());
        Assertions.assertEquals(OptionalJodaTimeArgumentFactory.class, af.removeFirst());
        Assertions.assertEquals(OptionalLocalDateArgumentFactory.class, af.removeFirst());
        Assertions.assertEquals(OptionalLocalDateTimeArgumentFactory.class, af.removeFirst());
        Assertions.assertEquals(OptionalInstantArgumentFactory.class, af.removeFirst());
        Assertions.assertEquals(OptionalOffsetDateTimeArgumentFactory.class, af.removeFirst());
        Assertions.assertEquals(OptionalZonedDateTimeArgumentFactory.class, af.removeFirst());
        // Verify Column Mapper Order
        Assertions.assertEquals(OptionalDoubleMapper.class, cm.removeFirst());
        Assertions.assertEquals(OptionalIntMapper.class, cm.removeFirst());
        Assertions.assertEquals(OptionalLongMapper.class, cm.removeFirst());
        Assertions.assertEquals(JodaDateTimeMapper.class, cm.removeFirst());
        Assertions.assertEquals(LocalDateMapper.class, cm.removeFirst());
        Assertions.assertEquals(LocalDateTimeMapper.class, cm.removeFirst());
        Assertions.assertEquals(InstantMapper.class, cm.removeFirst());
        Assertions.assertEquals(OffsetDateTimeMapper.class, cm.removeFirst());
        Assertions.assertEquals(ZonedDateTimeMapper.class, cm.removeFirst());
        // Verify Container Factory Order
        Assertions.assertEquals(ImmutableListContainerFactory.class, cf.removeFirst());
        Assertions.assertEquals(ImmutableSetContainerFactory.class, cf.removeFirst());
        Assertions.assertEquals(GuavaOptionalContainerFactory.class, cf.removeFirst());
        Assertions.assertEquals(OptionalContainerFactory.class, cf.removeFirst());
        // Verify we have accounted for everything
        Assertions.assertTrue(cm.isEmpty());
        Assertions.assertTrue(cf.isEmpty());
        Assertions.assertTrue(af.isEmpty());
    }
}

