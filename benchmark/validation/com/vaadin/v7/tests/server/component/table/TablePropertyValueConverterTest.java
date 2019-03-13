package com.vaadin.v7.tests.server.component.table;


import com.vaadin.v7.data.Container;
import com.vaadin.v7.ui.Table;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class TablePropertyValueConverterTest {
    protected TablePropertyValueConverterTest.TestableTable table;

    protected Collection<?> initialProperties;

    @Test
    public void testRemovePropertyId() {
        Collection<Object> converters = table.getCurrentConverters();
        Assert.assertFalse("Set of converters was empty at the start.", converters.isEmpty());
        Object firstId = converters.iterator().next();
        removeContainerProperty(firstId);
        Collection<Object> converters2 = table.getCurrentConverters();
        Assert.assertTrue("FirstId was not removed", (!(converters2.contains(firstId))));
        Assert.assertTrue("The number of removed converters was not one.", (((converters.size()) - (converters2.size())) == 1));
        for (Object originalId : converters) {
            if (!(originalId.equals(firstId))) {
                Assert.assertTrue("The wrong converter was removed.", converters2.contains(originalId));
            }
        }
    }

    @Test
    public void testSetContainer() {
        table.setContainerDataSource(TablePropertyValueConverterTest.createContainer(new String[]{ "col1", "col3", "col4", "col5" }));
        Collection<Object> converters = table.getCurrentConverters();
        Assert.assertTrue("There should only have been one converter left.", ((converters.size()) == 1));
        Object onlyKey = converters.iterator().next();
        Assert.assertTrue("The incorrect key was left.", onlyKey.equals("col1"));
    }

    @Test
    public void testSetContainerWithInexactButCompatibleTypes() {
        TablePropertyValueConverterTest.TestableTable customTable = new TablePropertyValueConverterTest.TestableTable("Test table", TablePropertyValueConverterTest.createContainer(new String[]{ "col1", "col2", "col3" }, new Class[]{ String.class, TablePropertyValueConverterTest.BaseClass.class, TablePropertyValueConverterTest.DerivedClass.class }));
        customTable.setConverter("col1", new com.vaadin.v7.data.util.converter.Converter<String, String>() {
            private static final long serialVersionUID = 1L;

            @Override
            public String convertToModel(String value, Class<? extends String> targetType, Locale locale) throws ConversionException {
                return "model";
            }

            @Override
            public String convertToPresentation(String value, Class<? extends String> targetType, Locale locale) throws ConversionException {
                return "presentation";
            }

            @Override
            public Class<String> getModelType() {
                return String.class;
            }

            @Override
            public Class<String> getPresentationType() {
                return String.class;
            }
        });
        customTable.setConverter("col2", new com.vaadin.v7.data.util.converter.Converter<String, TablePropertyValueConverterTest.BaseClass>() {
            private static final long serialVersionUID = 1L;

            @Override
            public TablePropertyValueConverterTest.BaseClass convertToModel(String value, Class<? extends TablePropertyValueConverterTest.BaseClass> targetType, Locale locale) throws ConversionException {
                return new TablePropertyValueConverterTest.BaseClass("model");
            }

            @Override
            public Class<TablePropertyValueConverterTest.BaseClass> getModelType() {
                return TablePropertyValueConverterTest.BaseClass.class;
            }

            @Override
            public Class<String> getPresentationType() {
                return String.class;
            }

            @Override
            public String convertToPresentation(TablePropertyValueConverterTest.BaseClass value, Class<? extends String> targetType, Locale locale) throws ConversionException {
                return null;
            }
        });
        customTable.setConverter("col3", new com.vaadin.v7.data.util.converter.Converter<String, TablePropertyValueConverterTest.DerivedClass>() {
            private static final long serialVersionUID = 1L;

            @Override
            public TablePropertyValueConverterTest.DerivedClass convertToModel(String value, Class<? extends TablePropertyValueConverterTest.DerivedClass> targetType, Locale locale) throws ConversionException {
                return new TablePropertyValueConverterTest.DerivedClass(("derived" + 1001));
            }

            @Override
            public Class<TablePropertyValueConverterTest.DerivedClass> getModelType() {
                return TablePropertyValueConverterTest.DerivedClass.class;
            }

            @Override
            public Class<String> getPresentationType() {
                return String.class;
            }

            @Override
            public String convertToPresentation(TablePropertyValueConverterTest.DerivedClass value, Class<? extends String> targetType, Locale locale) throws ConversionException {
                return null;
            }
        });
        customTable.setContainerDataSource(TablePropertyValueConverterTest.createContainer(new String[]{ "col1", "col2", "col3" }, new Class[]{ TablePropertyValueConverterTest.DerivedClass.class, TablePropertyValueConverterTest.DerivedClass.class, TablePropertyValueConverterTest.BaseClass.class }));
        Set<Object> converters = customTable.getCurrentConverters();
        // TODO Test temporarily disabled as this feature
        // is not yet implemented in Table
        /* assertTrue("Incompatible types were not removed.", converters.size()
        <= 1); assertTrue("Even compatible types were removed",
        converters.size() == 1); assertTrue("Compatible type was missing.",
        converters.contains("col2"));
         */
    }

    @Test
    public void testPrimitiveTypeConverters() {
        TablePropertyValueConverterTest.TestableTable customTable = new TablePropertyValueConverterTest.TestableTable("Test table", TablePropertyValueConverterTest.createContainer(new String[]{ "col1", "col2", "col3" }, new Class[]{ int.class, TablePropertyValueConverterTest.BaseClass.class, TablePropertyValueConverterTest.DerivedClass.class }));
        setConverter("col1", new com.vaadin.v7.data.util.converter.Converter<String, Integer>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Integer convertToModel(String value, Class<? extends Integer> targetType, Locale locale) throws ConversionException {
                return 11;
            }

            @Override
            public String convertToPresentation(Integer value, Class<? extends String> targetType, Locale locale) throws ConversionException {
                return "presentation";
            }

            @Override
            public Class<Integer> getModelType() {
                return Integer.class;
            }

            @Override
            public Class<String> getPresentationType() {
                return String.class;
            }
        });
        Set<Object> converters = customTable.getCurrentConverters();
        Assert.assertFalse("Converter was not set.", converters.isEmpty());
    }

    @Test
    public void testInheritance() {
        Assert.assertTrue("BaseClass isn't assignable from DerivedClass", TablePropertyValueConverterTest.BaseClass.class.isAssignableFrom(TablePropertyValueConverterTest.DerivedClass.class));
        Assert.assertFalse("DerivedClass is assignable from BaseClass", TablePropertyValueConverterTest.DerivedClass.class.isAssignableFrom(TablePropertyValueConverterTest.BaseClass.class));
    }

    private class TestableTable extends Table {
        /**
         *
         *
         * @param string
         * 		
         * @param createContainer
         * 		
         */
        public TestableTable(String string, Container container) {
            super(string, container);
        }

        Set<Object> getCurrentConverters() {
            try {
                Field f = Table.class.getDeclaredField("propertyValueConverters");
                f.setAccessible(true);
                Map<Object, com.vaadin.v7.data.util.converter.Converter<String, Object>> pvc = ((Map<Object, com.vaadin.v7.data.util.converter.Converter<String, Object>>) (f.get(this)));
                Set<Object> currentConverters = new HashSet<Object>();
                for (Map.Entry<Object, com.vaadin.v7.data.util.converter.Converter<String, Object>> entry : pvc.entrySet()) {
                    currentConverters.add(entry.getKey());
                }
                return currentConverters;
            } catch (Exception e) {
                Assert.fail("Unable to retrieve propertyValueConverters");
                return null;
            }
        }
    }

    private static class BaseClass {
        private String title;

        public BaseClass(String title) {
            this.title = title;
        }
    }

    private static class DerivedClass extends TablePropertyValueConverterTest.BaseClass {
        public DerivedClass(String title) {
            super(title);
        }
    }
}

