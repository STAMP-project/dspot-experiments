package liquibase.structure.core;


import org.junit.Assert;
import org.junit.Test;


public class PrimaryKeyTest {
    @Test
    public void setColumn_singlePKColumn() {
        PrimaryKey pk = new PrimaryKey();
        pk.addColumn(0, new Column("id"));
        Assert.assertEquals(1, pk.getColumnNamesAsList().size());
    }

    @Test
    public void setColumn_outOfOrder() {
        PrimaryKey pk = new PrimaryKey();
        pk.addColumn(1, new Column("id2"));
        pk.addColumn(0, new Column("id1"));
        Assert.assertEquals(2, pk.getColumnNamesAsList().size());
        Assert.assertEquals("id1", pk.getColumnNamesAsList().get(0));
        Assert.assertEquals("id2", pk.getColumnNamesAsList().get(1));
    }

    @Test
    public void setColumn_inOrder() {
        PrimaryKey pk = new PrimaryKey();
        pk.addColumn(0, new Column("id1"));
        pk.addColumn(1, new Column("id2"));
        Assert.assertEquals(2, pk.getColumnNamesAsList().size());
        Assert.assertEquals("id1", pk.getColumnNamesAsList().get(0));
        Assert.assertEquals("id2", pk.getColumnNamesAsList().get(1));
    }
}

