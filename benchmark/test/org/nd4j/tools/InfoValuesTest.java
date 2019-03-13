package org.nd4j.tools;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author clavvis
 */
public class InfoValuesTest {
    // 
    private String[] t1_titleA = new String[]{ "T0", "T1", "T2", "T3", "T4", "T5" };

    // 
    private String[] t2_titleA = new String[]{ "", "T1", "T2" };

    // 
    @Test
    public void testconstructor() throws Exception {
        // 
        InfoValues iv;
        // 
        iv = new InfoValues(t1_titleA);
        Assert.assertEquals("T0", iv.titleA[0]);
        Assert.assertEquals("T1", iv.titleA[1]);
        Assert.assertEquals("T2", iv.titleA[2]);
        Assert.assertEquals("T3", iv.titleA[3]);
        Assert.assertEquals("T4", iv.titleA[4]);
        Assert.assertEquals("T5", iv.titleA[5]);
        // 
        iv = new InfoValues(t2_titleA);
        Assert.assertEquals("", iv.titleA[0]);
        Assert.assertEquals("T1", iv.titleA[1]);
        Assert.assertEquals("T2", iv.titleA[2]);
        Assert.assertEquals("", iv.titleA[3]);
        Assert.assertEquals("", iv.titleA[4]);
        Assert.assertEquals("", iv.titleA[5]);
        // 
    }

    @Test
    public void testgetValues() throws Exception {
        // 
        InfoValues iv;
        // 
        iv = new InfoValues("Test");
        iv.vsL.add(" AB ");
        iv.vsL.add(" CD ");
        // 
        Assert.assertEquals(" AB | CD |", iv.getValues());
        // 
    }
}

