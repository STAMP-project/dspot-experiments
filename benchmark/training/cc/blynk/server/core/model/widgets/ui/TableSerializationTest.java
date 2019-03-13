package cc.blynk.server.core.model.widgets.ui;


import JsonParser.MAPPER;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.widgets.ui.table.Column;
import cc.blynk.server.core.model.widgets.ui.table.Row;
import cc.blynk.server.core.model.widgets.ui.table.Table;
import org.junit.Assert;
import org.junit.Test;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 8/20/16.
 */
public class TableSerializationTest {
    @Test
    public void testTableNoRowsJson() throws Exception {
        Table table = new Table();
        table.columns = new Column[3];
        table.columns[0] = new Column("indicator");
        table.columns[1] = new Column("name");
        table.columns[2] = new Column("value");
        String json = MAPPER.writeValueAsString(table);
        Assert.assertEquals("{\"type\":\"TABLE\",\"id\":0,\"x\":0,\"y\":0,\"color\":0,\"width\":0,\"height\":0,\"tabId\":0,\"isDefaultColor\":false,\"deviceId\":0,\"pin\":-1,\"pwmMode\":false,\"rangeMappingOn\":false,\"min\":0.0,\"max\":0.0,\"columns\":[{\"name\":\"indicator\"},{\"name\":\"name\"},{\"name\":\"value\"}],\"currentRowIndex\":0,\"isReoderingAllowed\":false,\"isClickableRows\":false}", json);
    }

    @Test
    public void testTableSingleRowJson() throws Exception {
        Table table = new Table();
        table.columns = new Column[3];
        table.columns[0] = new Column("indicator");
        table.columns[1] = new Column("name");
        table.columns[2] = new Column("value");
        Row row = new Row(1, "Adskiy trash", "6:33", false);
        table.rows.add(row);
        String json = MAPPER.writeValueAsString(table);
        Assert.assertEquals("{\"type\":\"TABLE\",\"id\":0,\"x\":0,\"y\":0,\"color\":0,\"width\":0,\"height\":0,\"tabId\":0,\"isDefaultColor\":false,\"deviceId\":0,\"pin\":-1,\"pwmMode\":false,\"rangeMappingOn\":false,\"min\":0.0,\"max\":0.0,\"columns\":[{\"name\":\"indicator\"},{\"name\":\"name\"},{\"name\":\"value\"}],\"rows\":[{\"id\":1,\"name\":\"Adskiy trash\",\"value\":\"6:33\",\"isSelected\":false}],\"currentRowIndex\":0,\"isReoderingAllowed\":false,\"isClickableRows\":false}", json);
    }

    @Test
    public void testDeserializeTable() throws Exception {
        Table deserializedTable = ((Table) (JsonParser.parseWidget("{\"type\":\"TABLE\",\"id\":0,\"x\":0,\"y\":0,\"color\":0,\"width\":0,\"height\":0,\"tabId\":0,\"isDefaultColor\":false,\"deviceId\":0,\"pin\":-1,\"pwmMode\":false,\"rangeMappingOn\":false,\"min\":0.0,\"max\":0.0,\"columns\":[{\"name\":\"indicator\"},{\"name\":\"name\"},{\"name\":\"value\"}],\"rows\":[{\"id\":1,\"name\":\"Adskiy trash\",\"value\":\"6:33\",\"isSelected\":false}],\"currentRowIndex\":0,\"isReoderingAllowed\":false,\"isClickableRows\":false}")));
        Table table = new Table();
        table.columns = new Column[3];
        table.columns[0] = new Column("indicator");
        table.columns[1] = new Column("name");
        table.columns[2] = new Column("value");
        Row row = new Row(1, "Adskiy trash", "6:33", false);
        table.rows.add(row);
        Assert.assertEquals(deserializedTable.rows.peek().id, table.rows.peek().id);
        Assert.assertEquals(deserializedTable.rows.peek().name, table.rows.peek().name);
        Assert.assertEquals(deserializedTable.rows.peek().isSelected, table.rows.peek().isSelected);
        Assert.assertEquals(deserializedTable.rows.peek().value, table.rows.peek().value);
    }

    @Test
    public void testTableMultiRowJson() throws Exception {
        Table table = new Table();
        table.columns = new Column[3];
        table.columns[0] = new Column("indicator");
        table.columns[1] = new Column("name");
        table.columns[2] = new Column("value");
        Row row = new Row(1, "Adskiy trash", "6:33", false);
        table.rows.add(row);
        Row row2 = new Row(2, "Adskiy trash2", "6:332", false);
        table.rows.add(row2);
        String json = MAPPER.writeValueAsString(table);
        Assert.assertEquals("{\"type\":\"TABLE\",\"id\":0,\"x\":0,\"y\":0,\"color\":0,\"width\":0,\"height\":0,\"tabId\":0,\"isDefaultColor\":false,\"deviceId\":0,\"pin\":-1,\"pwmMode\":false,\"rangeMappingOn\":false,\"min\":0.0,\"max\":0.0,\"columns\":[{\"name\":\"indicator\"},{\"name\":\"name\"},{\"name\":\"value\"}],\"rows\":[{\"id\":1,\"name\":\"Adskiy trash\",\"value\":\"6:33\",\"isSelected\":false},{\"id\":2,\"name\":\"Adskiy trash2\",\"value\":\"6:332\",\"isSelected\":false}],\"currentRowIndex\":0,\"isReoderingAllowed\":false,\"isClickableRows\":false}", json);
    }
}

