package org.nd4j.tools;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author clavvis
 */
public class InfoLineTest {
    // 
    @Test
    public void testAll() throws Exception {
        // 
        InfoValues iv0 = new InfoValues(" A", " B");
        InfoValues iv1 = new InfoValues(" C", " D");
        InfoValues iv2 = new InfoValues(" E", " F", " G", " H");
        // 
        iv0.vsL.add(" ab ");
        iv1.vsL.add(" cd ");
        iv2.vsL.add(" ef ");
        // 
        InfoLine il = new InfoLine();
        // 
        il.ivL.add(iv0);
        il.ivL.add(iv1);
        il.ivL.add(iv2);
        // 
        int mtLv = 2;
        // 
        Assert.assertEquals(".. | A  | C  | E  |", il.getTitleLine(mtLv, 0));
        Assert.assertEquals(".. | B  | D  | F  |", il.getTitleLine(mtLv, 1));
        Assert.assertEquals(".. |    |    | G  |", il.getTitleLine(mtLv, 2));
        Assert.assertEquals(".. |    |    | H  |", il.getTitleLine(mtLv, 3));
        Assert.assertEquals(".. | ab | cd | ef |", il.getValuesLine(mtLv));
        // 
    }
}

