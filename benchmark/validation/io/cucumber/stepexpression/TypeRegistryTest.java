package io.cucumber.stepexpression;


import io.cucumber.cucumberexpressions.ParameterByTypeTransformer;
import io.cucumber.cucumberexpressions.ParameterType;
import io.cucumber.datatable.DataTable;
import io.cucumber.datatable.DataTableType;
import io.cucumber.datatable.TableCellByTypeTransformer;
import io.cucumber.datatable.TableEntryByTypeTransformer;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class TypeRegistryTest {
    private final TypeRegistry registry = new TypeRegistry(Locale.ENGLISH);

    @Test
    public void should_define_parameter_type() {
        ParameterType<Object> expected = new ParameterType("example", ".*", Object.class, new io.cucumber.cucumberexpressions.Transformer<Object>() {
            @Override
            public Object transform(String s) {
                return null;
            }
        });
        registry.defineParameterType(expected);
        Assert.assertEquals(expected, registry.parameterTypeRegistry().lookupByTypeName("example"));
    }

    @Test
    public void should_define_data_table_parameter_type() {
        DataTableType expected = new DataTableType(Date.class, new io.cucumber.datatable.TableTransformer<Date>() {
            @Override
            public Date transform(DataTable dataTable) {
                return null;
            }
        });
        registry.defineDataTableType(expected);
        Assert.assertEquals(expected, registry.dataTableTypeRegistry().lookupTableTypeByType(Date.class));
    }

    @Test
    public void should_set_default_parameter_transformer() {
        ParameterByTypeTransformer expected = new ParameterByTypeTransformer() {
            @Override
            public Object transform(String fromValue, Type toValueType) {
                return null;
            }
        };
        registry.setDefaultParameterTransformer(expected);
        Assert.assertEquals(expected, registry.parameterTypeRegistry().getDefaultParameterTransformer());
    }

    @Test
    public void should_set_default_table_cell_transformer() {
        TableCellByTypeTransformer expected = new TableCellByTypeTransformer() {
            @Override
            public <T> T transform(String s, Class<T> aClass) {
                return null;
            }
        };
        registry.setDefaultDataTableCellTransformer(expected);
    }

    @Test
    public void should_set_default_table_entry_transformer() {
        TableEntryByTypeTransformer expected = new TableEntryByTypeTransformer() {
            @Override
            public <T> T transform(Map<String, String> map, Class<T> aClass, TableCellByTypeTransformer tableCellByTypeTransformer) {
                return null;
            }
        };
        registry.setDefaultDataTableEntryTransformer(expected);
    }
}

