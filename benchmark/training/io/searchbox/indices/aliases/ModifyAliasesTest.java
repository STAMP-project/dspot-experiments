package io.searchbox.indices.aliases;


import ElasticsearchVersion.UNKNOWN;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author cihat keser
 */
public class ModifyAliasesTest {
    Map<String, Object> userFilter = new org.elasticsearch.common.collect.MapBuilder<String, Object>().put("term", org.elasticsearch.common.collect.MapBuilder.newMapBuilder().put("user", "kimchy").immutableMap()).immutableMap();

    AliasMapping addMapping = new AddAliasMapping.Builder("t_add_index", "t_add_alias").setFilter(userFilter).build();

    AliasMapping removeMapping = new RemoveAliasMapping.Builder("t_remove_index", "t_remove_alias").addRouting("1").build();

    @Test
    public void testBasicUriGeneration() {
        ModifyAliases modifyAliases = build();
        Assert.assertEquals("POST", modifyAliases.getRestMethodName());
        Assert.assertEquals("/_aliases", modifyAliases.getURI(UNKNOWN));
    }

    @Test
    public void equalsReturnsTrueForSameMappings() {
        ModifyAliases modifyAliases1 = build();
        ModifyAliases modifyAliases1Duplicate = build();
        Assert.assertEquals(modifyAliases1, modifyAliases1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentMappings() {
        ModifyAliases modifyAliases1 = build();
        ModifyAliases modifyAliases2 = build();
        Assert.assertNotEquals(modifyAliases1, modifyAliases2);
    }
}

