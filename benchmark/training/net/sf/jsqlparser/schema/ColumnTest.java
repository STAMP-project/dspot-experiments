/**
 * -
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.schema;


import net.sf.jsqlparser.expression.Alias;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author tw
 */
public class ColumnTest {
    public ColumnTest() {
    }

    @Test
    public void testMissingTableAlias() {
        Table myTable = new Table("myTable");
        myTable.setAlias(new Alias("tb"));
        Column myColumn = new Column(myTable, "myColumn");
        Assert.assertEquals("tb.myColumn", myColumn.toString());
    }
}

