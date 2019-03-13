package com.vaadin.tests.server.component.grid;


import com.vaadin.data.PropertyDefinition;
import com.vaadin.data.PropertySet;
import com.vaadin.data.ValueProvider;
import com.vaadin.server.Setter;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public class GridCustomPropertySetTest {
    public static class MyBeanWithoutGetters {
        public String str;

        public int number;

        public MyBeanWithoutGetters(String str, int number) {
            this.str = str;
            this.number = number;
        }
    }

    public static class GridWithCustomPropertySet extends Grid<GridCustomPropertySetTest.MyBeanWithoutGetters> {
        private final class MyBeanPropertySet implements PropertySet<GridCustomPropertySetTest.MyBeanWithoutGetters> {
            private PropertyDefinition<GridCustomPropertySetTest.MyBeanWithoutGetters, String> strDef = new GridCustomPropertySetTest.GridWithCustomPropertySet.StrDefinition(this);

            private PropertyDefinition<GridCustomPropertySetTest.MyBeanWithoutGetters, Integer> numberDef = new GridCustomPropertySetTest.GridWithCustomPropertySet.NumberDefinition(this);

            @Override
            public Stream<PropertyDefinition<GridCustomPropertySetTest.MyBeanWithoutGetters, ?>> getProperties() {
                return Stream.of(strDef, numberDef);
            }

            @Override
            public Optional<PropertyDefinition<GridCustomPropertySetTest.MyBeanWithoutGetters, ?>> getProperty(String name) {
                return getProperties().filter(( pd) -> pd.getName().equals(name)).findFirst();
            }
        }

        private final class StrDefinition implements PropertyDefinition<GridCustomPropertySetTest.MyBeanWithoutGetters, String> {
            private PropertySet<GridCustomPropertySetTest.MyBeanWithoutGetters> propertySet;

            public StrDefinition(PropertySet<GridCustomPropertySetTest.MyBeanWithoutGetters> propertySet) {
                this.propertySet = propertySet;
            }

            @Override
            public ValueProvider<GridCustomPropertySetTest.MyBeanWithoutGetters, String> getGetter() {
                return ( bean) -> bean.str;
            }

            @Override
            public Optional<Setter<GridCustomPropertySetTest.MyBeanWithoutGetters, String>> getSetter() {
                return Optional.of(( bean, value) -> bean.str = value);
            }

            @Override
            public Class<String> getType() {
                return String.class;
            }

            @Override
            public Class<?> getPropertyHolderType() {
                return GridCustomPropertySetTest.MyBeanWithoutGetters.class;
            }

            @Override
            public String getName() {
                return "string";
            }

            @Override
            public String getCaption() {
                return "The String";
            }

            @Override
            public PropertySet<GridCustomPropertySetTest.MyBeanWithoutGetters> getPropertySet() {
                return propertySet;
            }
        }

        private final class NumberDefinition implements PropertyDefinition<GridCustomPropertySetTest.MyBeanWithoutGetters, Integer> {
            private PropertySet<GridCustomPropertySetTest.MyBeanWithoutGetters> propertySet;

            public NumberDefinition(PropertySet<GridCustomPropertySetTest.MyBeanWithoutGetters> propertySet) {
                this.propertySet = propertySet;
            }

            @Override
            public ValueProvider<GridCustomPropertySetTest.MyBeanWithoutGetters, Integer> getGetter() {
                return ( bean) -> bean.number;
            }

            @Override
            public Optional<Setter<GridCustomPropertySetTest.MyBeanWithoutGetters, Integer>> getSetter() {
                return Optional.of(( bean, value) -> bean.number = value);
            }

            @Override
            public Class<Integer> getType() {
                return Integer.class;
            }

            @Override
            public Class<?> getPropertyHolderType() {
                return GridCustomPropertySetTest.MyBeanWithoutGetters.class;
            }

            @Override
            public String getName() {
                return "numbah";
            }

            @Override
            public String getCaption() {
                return "The Number";
            }

            @Override
            public PropertySet<GridCustomPropertySetTest.MyBeanWithoutGetters> getPropertySet() {
                return propertySet;
            }
        }

        public GridWithCustomPropertySet() {
            super();
            setPropertySet(new GridCustomPropertySetTest.GridWithCustomPropertySet.MyBeanPropertySet());
        }
    }

    @Test
    public void customPropertySet() {
        GridCustomPropertySetTest.GridWithCustomPropertySet customGrid = new GridCustomPropertySetTest.GridWithCustomPropertySet();
        Assert.assertEquals(0, getColumns().size());
        Column<GridCustomPropertySetTest.MyBeanWithoutGetters, Integer> numberColumn = ((Column<GridCustomPropertySetTest.MyBeanWithoutGetters, Integer>) (addColumn("numbah")));
        Assert.assertEquals(1, getColumns().size());
        Assert.assertEquals("The Number", numberColumn.getCaption());
        Assert.assertEquals(24, ((int) (numberColumn.getValueProvider().apply(new GridCustomPropertySetTest.MyBeanWithoutGetters("foo", 24)))));
        Column<GridCustomPropertySetTest.MyBeanWithoutGetters, String> stringColumn = ((Column<GridCustomPropertySetTest.MyBeanWithoutGetters, String>) (addColumn("string")));
        Assert.assertEquals(2, getColumns().size());
        Assert.assertEquals("The String", stringColumn.getCaption());
        Assert.assertEquals("foo", stringColumn.getValueProvider().apply(new GridCustomPropertySetTest.MyBeanWithoutGetters("foo", 24)));
    }
}

