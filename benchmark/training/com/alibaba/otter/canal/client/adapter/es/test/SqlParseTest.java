package com.alibaba.otter.canal.client.adapter.es.test;


import com.alibaba.otter.canal.client.adapter.es.config.SchemaItem;
import com.alibaba.otter.canal.client.adapter.es.config.SchemaItem.FieldItem;
import com.alibaba.otter.canal.client.adapter.es.config.SchemaItem.TableItem;
import com.alibaba.otter.canal.client.adapter.es.config.SqlParser;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class SqlParseTest {
    @Test
    public void parseTest() {
        String sql = "select a.id, concat(a.name,'_test') as name, a.role_id, b.name as role_name, c.labels from user a " + (("left join role b on a.role_id=b.id " + "left join (select user_id, group_concat(label,',') as labels from user_label ") + "group by user_id) c on c.user_id=a.id");
        SchemaItem schemaItem = SqlParser.parse(sql);
        // ????? TableItem
        List<TableItem> tableItems = schemaItem.getTableItemAliases().get("user_label".toLowerCase());
        tableItems.forEach(( tableItem) -> Assert.assertEquals("c", tableItem.getAlias()));
        TableItem tableItem = tableItems.get(0);
        Assert.assertFalse(tableItem.isMain());
        Assert.assertTrue(tableItem.isSubQuery());
        // ?????? FieldItem
        List<FieldItem> fieldItems = schemaItem.getColumnFields().get(((tableItem.getAlias()) + (".labels".toLowerCase())));
        fieldItems.forEach(( fieldItem) -> Assert.assertEquals("c.labels", (((fieldItem.getOwner()) + ".") + (fieldItem.getFieldName()))));
        // ???????????
        Map<FieldItem, List<FieldItem>> relationTableFields = tableItem.getRelationTableFields();
        relationTableFields.keySet().forEach(( fieldItem) -> Assert.assertEquals("user_id", fieldItem.getColumn().getColumnName()));
        // ???????select??????
        // List<FieldItem> relationSelectFieldItem =
        // tableItem.getRelationKeyFieldItems();
        // relationSelectFieldItem.forEach(fieldItem -> Assert.assertEquals("c.labels",
        // fieldItem.getOwner() + "." + fieldItem.getColumn().getColumnName()));
    }
}

