package org.stagemonitor.configuration.converter;


import org.junit.Test;


public class ClassInstanceValueConverterTest {
    interface Strategy {}

    public static class StrategyImpl implements ClassInstanceValueConverterTest.Strategy {}

    static class PrivateStrategyImpl implements ClassInstanceValueConverterTest.Strategy {
        private PrivateStrategyImpl() {
        }
    }

    @Test
    public void testSuccess() throws Exception {
        final ClassInstanceValueConverter<ClassInstanceValueConverterTest.Strategy> converter = ClassInstanceValueConverter.of(ClassInstanceValueConverterTest.Strategy.class);
        assertThat(converter.convert(ClassInstanceValueConverterTest.StrategyImpl.class.getName())).isInstanceOf(ClassInstanceValueConverterTest.StrategyImpl.class);
    }

    @Test
    public void testPrivateConstructor() throws Exception {
        final ClassInstanceValueConverter<ClassInstanceValueConverterTest.Strategy> converter = ClassInstanceValueConverter.of(ClassInstanceValueConverterTest.Strategy.class);
        assertThatThrownBy(() -> converter.convert(.class.getName())).isInstanceOf(IllegalArgumentException.class).hasMessageStartingWith("Did not find a public no arg constructor for");
    }

    @Test
    public void testNotAnInstanceOfStrategy() throws Exception {
        final ClassInstanceValueConverter<ClassInstanceValueConverterTest.Strategy> converter = ClassInstanceValueConverter.of(ClassInstanceValueConverterTest.Strategy.class);
        assertThatThrownBy(() -> converter.convert(.class.getName())).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("is not an instance of");
    }
}

