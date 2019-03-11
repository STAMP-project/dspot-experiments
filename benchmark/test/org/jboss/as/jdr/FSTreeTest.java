package org.jboss.as.jdr;


import java.io.File;
import org.jboss.as.jdr.util.FSTree;
import org.junit.Assert;
import org.junit.Test;


public class FSTreeTest {
    private File baseDirectory;

    @Test
    public void testTree() throws Exception {
        FSTree tree = new FSTree(baseDirectory.getPath());
        Assert.assertEquals(tree.toString(), "FSTreeTest\n");
    }
}

