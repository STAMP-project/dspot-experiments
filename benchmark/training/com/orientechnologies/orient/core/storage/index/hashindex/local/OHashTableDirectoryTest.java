package com.orientechnologies.orient.core.storage.index.hashindex.local;


import ODirectoryFirstPage.NODES_PER_PAGE;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.storage.impl.local.paginated.atomicoperations.OAtomicOperation;
import java.io.IOException;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import static ODirectoryFirstPage.NODES_PER_PAGE;
import static OLocalHashTable.MAX_LEVEL_SIZE;


/**
 *
 *
 * @author Andrey Lomakin (a.lomakin-at-orientdb.com)
 * @since 5/15/14
 */
public class OHashTableDirectoryTest {
    private static ODatabaseDocumentTx databaseDocumentTx;

    private static OHashTableDirectory directory;

    @Test
    public void addFirstLevel() throws IOException {
        OAtomicOperation atomicOperation = OHashTableDirectoryTest.startTx();
        long[] level = new long[MAX_LEVEL_SIZE];
        for (int i = 0; i < (level.length); i++)
            level[i] = i;

        int index = OHashTableDirectoryTest.directory.addNewNode(((byte) (2)), ((byte) (3)), ((byte) (4)), level, atomicOperation);
        Assert.assertEquals(index, 0);
        Assert.assertEquals(OHashTableDirectoryTest.directory.getMaxLeftChildDepth(0, atomicOperation), 2);
        Assert.assertEquals(OHashTableDirectoryTest.directory.getMaxRightChildDepth(0, atomicOperation), 3);
        Assert.assertEquals(OHashTableDirectoryTest.directory.getNodeLocalDepth(0, atomicOperation), 4);
        Assertions.assertThat(OHashTableDirectoryTest.directory.getNode(0, atomicOperation)).isEqualTo(level);
        for (int i = 0; i < (level.length); i++)
            Assert.assertEquals(OHashTableDirectoryTest.directory.getNodePointer(0, i, atomicOperation), i);

        OHashTableDirectoryTest.rollbackTx();
    }

    @Test
    public void changeFirstLevel() throws IOException {
        OAtomicOperation atomicOperation = OHashTableDirectoryTest.startTx();
        long[] level = new long[MAX_LEVEL_SIZE];
        for (int i = 0; i < (level.length); i++)
            level[i] = i;

        OHashTableDirectoryTest.directory.addNewNode(((byte) (2)), ((byte) (3)), ((byte) (4)), level, atomicOperation);
        for (int i = 0; i < (level.length); i++)
            OHashTableDirectoryTest.directory.setNodePointer(0, i, (i + 100), atomicOperation);

        OHashTableDirectoryTest.directory.setMaxLeftChildDepth(0, ((byte) (100)), atomicOperation);
        OHashTableDirectoryTest.directory.setMaxRightChildDepth(0, ((byte) (101)), atomicOperation);
        OHashTableDirectoryTest.directory.setNodeLocalDepth(0, ((byte) (102)), atomicOperation);
        for (int i = 0; i < (level.length); i++)
            Assert.assertEquals(OHashTableDirectoryTest.directory.getNodePointer(0, i, atomicOperation), (i + 100));

        Assert.assertEquals(OHashTableDirectoryTest.directory.getMaxLeftChildDepth(0, atomicOperation), 100);
        Assert.assertEquals(OHashTableDirectoryTest.directory.getMaxRightChildDepth(0, atomicOperation), 101);
        Assert.assertEquals(OHashTableDirectoryTest.directory.getNodeLocalDepth(0, atomicOperation), 102);
        OHashTableDirectoryTest.rollbackTx();
    }

    @Test
    public void addThreeRemoveSecondAddNewAndChange() throws IOException {
        OAtomicOperation atomicOperation = OHashTableDirectoryTest.startTx();
        long[] level = new long[MAX_LEVEL_SIZE];
        for (int i = 0; i < (level.length); i++)
            level[i] = i;

        int index = OHashTableDirectoryTest.directory.addNewNode(((byte) (2)), ((byte) (3)), ((byte) (4)), level, atomicOperation);
        Assert.assertEquals(index, 0);
        for (int i = 0; i < (level.length); i++)
            level[i] = i + 100;

        index = OHashTableDirectoryTest.directory.addNewNode(((byte) (2)), ((byte) (3)), ((byte) (4)), level, atomicOperation);
        Assert.assertEquals(index, 1);
        for (int i = 0; i < (level.length); i++)
            level[i] = i + 200;

        index = OHashTableDirectoryTest.directory.addNewNode(((byte) (2)), ((byte) (3)), ((byte) (4)), level, atomicOperation);
        Assert.assertEquals(index, 2);
        OHashTableDirectoryTest.directory.deleteNode(1, atomicOperation);
        for (int i = 0; i < (level.length); i++)
            level[i] = i + 300;

        index = OHashTableDirectoryTest.directory.addNewNode(((byte) (5)), ((byte) (6)), ((byte) (7)), level, atomicOperation);
        Assert.assertEquals(index, 1);
        for (int i = 0; i < (level.length); i++)
            Assert.assertEquals(OHashTableDirectoryTest.directory.getNodePointer(1, i, atomicOperation), (i + 300));

        Assert.assertEquals(OHashTableDirectoryTest.directory.getMaxLeftChildDepth(1, atomicOperation), 5);
        Assert.assertEquals(OHashTableDirectoryTest.directory.getMaxRightChildDepth(1, atomicOperation), 6);
        Assert.assertEquals(OHashTableDirectoryTest.directory.getNodeLocalDepth(1, atomicOperation), 7);
        OHashTableDirectoryTest.rollbackTx();
    }

    @Test
    public void addRemoveChangeMix() throws IOException {
        OAtomicOperation atomicOperation = OHashTableDirectoryTest.startTx();
        long[] level = new long[MAX_LEVEL_SIZE];
        for (int i = 0; i < (level.length); i++)
            level[i] = i;

        int index = OHashTableDirectoryTest.directory.addNewNode(((byte) (2)), ((byte) (3)), ((byte) (4)), level, atomicOperation);
        Assert.assertEquals(index, 0);
        for (int i = 0; i < (level.length); i++)
            level[i] = i + 100;

        index = OHashTableDirectoryTest.directory.addNewNode(((byte) (2)), ((byte) (3)), ((byte) (4)), level, atomicOperation);
        Assert.assertEquals(index, 1);
        for (int i = 0; i < (level.length); i++)
            level[i] = i + 200;

        index = OHashTableDirectoryTest.directory.addNewNode(((byte) (2)), ((byte) (3)), ((byte) (4)), level, atomicOperation);
        Assert.assertEquals(index, 2);
        for (int i = 0; i < (level.length); i++)
            level[i] = i + 300;

        index = OHashTableDirectoryTest.directory.addNewNode(((byte) (2)), ((byte) (3)), ((byte) (4)), level, atomicOperation);
        Assert.assertEquals(index, 3);
        for (int i = 0; i < (level.length); i++)
            level[i] = i + 400;

        index = OHashTableDirectoryTest.directory.addNewNode(((byte) (2)), ((byte) (3)), ((byte) (4)), level, atomicOperation);
        Assert.assertEquals(index, 4);
        OHashTableDirectoryTest.directory.deleteNode(1, atomicOperation);
        OHashTableDirectoryTest.directory.deleteNode(3, atomicOperation);
        for (int i = 0; i < (level.length); i++)
            level[i] = i + 500;

        index = OHashTableDirectoryTest.directory.addNewNode(((byte) (5)), ((byte) (6)), ((byte) (7)), level, atomicOperation);
        Assert.assertEquals(index, 3);
        for (int i = 0; i < (level.length); i++)
            level[i] = i + 600;

        index = OHashTableDirectoryTest.directory.addNewNode(((byte) (8)), ((byte) (9)), ((byte) (10)), level, atomicOperation);
        Assert.assertEquals(index, 1);
        for (int i = 0; i < (level.length); i++)
            level[i] = i + 700;

        index = OHashTableDirectoryTest.directory.addNewNode(((byte) (11)), ((byte) (12)), ((byte) (13)), level, atomicOperation);
        Assert.assertEquals(index, 5);
        for (int i = 0; i < (level.length); i++)
            Assert.assertEquals(OHashTableDirectoryTest.directory.getNodePointer(3, i, atomicOperation), (i + 500));

        Assert.assertEquals(OHashTableDirectoryTest.directory.getMaxLeftChildDepth(3, atomicOperation), 5);
        Assert.assertEquals(OHashTableDirectoryTest.directory.getMaxRightChildDepth(3, atomicOperation), 6);
        Assert.assertEquals(OHashTableDirectoryTest.directory.getNodeLocalDepth(3, atomicOperation), 7);
        for (int i = 0; i < (level.length); i++)
            Assert.assertEquals(OHashTableDirectoryTest.directory.getNodePointer(1, i, atomicOperation), (i + 600));

        Assert.assertEquals(OHashTableDirectoryTest.directory.getMaxLeftChildDepth(1, atomicOperation), 8);
        Assert.assertEquals(OHashTableDirectoryTest.directory.getMaxRightChildDepth(1, atomicOperation), 9);
        Assert.assertEquals(OHashTableDirectoryTest.directory.getNodeLocalDepth(1, atomicOperation), 10);
        for (int i = 0; i < (level.length); i++)
            Assert.assertEquals(OHashTableDirectoryTest.directory.getNodePointer(5, i, atomicOperation), (i + 700));

        Assert.assertEquals(OHashTableDirectoryTest.directory.getMaxLeftChildDepth(5, atomicOperation), 11);
        Assert.assertEquals(OHashTableDirectoryTest.directory.getMaxRightChildDepth(5, atomicOperation), 12);
        Assert.assertEquals(OHashTableDirectoryTest.directory.getNodeLocalDepth(5, atomicOperation), 13);
        OHashTableDirectoryTest.rollbackTx();
    }

    @Test
    public void addThreePages() throws IOException {
        OAtomicOperation atomicOperation = OHashTableDirectoryTest.startTx();
        int firsIndex = -1;
        int secondIndex = -1;
        int thirdIndex = -1;
        long[] level = new long[MAX_LEVEL_SIZE];
        for (int n = 0; n < (NODES_PER_PAGE); n++) {
            for (int i = 0; i < (level.length); i++)
                level[i] = i + (n * 100);

            int index = OHashTableDirectoryTest.directory.addNewNode(((byte) (5)), ((byte) (6)), ((byte) (7)), level, atomicOperation);
            if (firsIndex < 0)
                firsIndex = index;

        }
        for (int n = 0; n < (ODirectoryPage.NODES_PER_PAGE); n++) {
            for (int i = 0; i < (level.length); i++)
                level[i] = i + (n * 100);

            int index = OHashTableDirectoryTest.directory.addNewNode(((byte) (5)), ((byte) (6)), ((byte) (7)), level, atomicOperation);
            if (secondIndex < 0)
                secondIndex = index;

        }
        for (int n = 0; n < (ODirectoryPage.NODES_PER_PAGE); n++) {
            for (int i = 0; i < (level.length); i++)
                level[i] = i + (n * 100);

            int index = OHashTableDirectoryTest.directory.addNewNode(((byte) (5)), ((byte) (6)), ((byte) (7)), level, atomicOperation);
            if (thirdIndex < 0)
                thirdIndex = index;

        }
        Assert.assertEquals(firsIndex, 0);
        Assert.assertEquals(secondIndex, NODES_PER_PAGE);
        Assert.assertEquals(thirdIndex, ((NODES_PER_PAGE) + (ODirectoryPage.NODES_PER_PAGE)));
        OHashTableDirectoryTest.directory.deleteNode(secondIndex, atomicOperation);
        OHashTableDirectoryTest.directory.deleteNode(firsIndex, atomicOperation);
        OHashTableDirectoryTest.directory.deleteNode(thirdIndex, atomicOperation);
        for (int i = 0; i < (level.length); i++)
            level[i] = i + 1000;

        int index = OHashTableDirectoryTest.directory.addNewNode(((byte) (8)), ((byte) (9)), ((byte) (10)), level, atomicOperation);
        Assert.assertEquals(index, thirdIndex);
        for (int i = 0; i < (level.length); i++)
            level[i] = i + 2000;

        index = OHashTableDirectoryTest.directory.addNewNode(((byte) (11)), ((byte) (12)), ((byte) (13)), level, atomicOperation);
        Assert.assertEquals(index, firsIndex);
        for (int i = 0; i < (level.length); i++)
            level[i] = i + 3000;

        index = OHashTableDirectoryTest.directory.addNewNode(((byte) (14)), ((byte) (15)), ((byte) (16)), level, atomicOperation);
        Assert.assertEquals(index, secondIndex);
        for (int i = 0; i < (level.length); i++)
            level[i] = i + 4000;

        index = OHashTableDirectoryTest.directory.addNewNode(((byte) (17)), ((byte) (18)), ((byte) (19)), level, atomicOperation);
        Assert.assertEquals(index, ((NODES_PER_PAGE) + (2 * (ODirectoryPage.NODES_PER_PAGE))));
        Assert.assertEquals(OHashTableDirectoryTest.directory.getMaxLeftChildDepth(thirdIndex, atomicOperation), 8);
        Assert.assertEquals(OHashTableDirectoryTest.directory.getMaxRightChildDepth(thirdIndex, atomicOperation), 9);
        Assert.assertEquals(OHashTableDirectoryTest.directory.getNodeLocalDepth(thirdIndex, atomicOperation), 10);
        for (int i = 0; i < (level.length); i++)
            Assert.assertEquals(OHashTableDirectoryTest.directory.getNodePointer(thirdIndex, i, atomicOperation), (i + 1000));

        Assert.assertEquals(OHashTableDirectoryTest.directory.getMaxLeftChildDepth(firsIndex, atomicOperation), 11);
        Assert.assertEquals(OHashTableDirectoryTest.directory.getMaxRightChildDepth(firsIndex, atomicOperation), 12);
        Assert.assertEquals(OHashTableDirectoryTest.directory.getNodeLocalDepth(firsIndex, atomicOperation), 13);
        for (int i = 0; i < (level.length); i++)
            Assert.assertEquals(OHashTableDirectoryTest.directory.getNodePointer(firsIndex, i, atomicOperation), (i + 2000));

        Assert.assertEquals(OHashTableDirectoryTest.directory.getMaxLeftChildDepth(secondIndex, atomicOperation), 14);
        Assert.assertEquals(OHashTableDirectoryTest.directory.getMaxRightChildDepth(secondIndex, atomicOperation), 15);
        Assert.assertEquals(OHashTableDirectoryTest.directory.getNodeLocalDepth(secondIndex, atomicOperation), 16);
        for (int i = 0; i < (level.length); i++)
            Assert.assertEquals(OHashTableDirectoryTest.directory.getNodePointer(secondIndex, i, atomicOperation), (i + 3000));

        final int lastIndex = (NODES_PER_PAGE) + (2 * (ODirectoryPage.NODES_PER_PAGE));
        Assert.assertEquals(OHashTableDirectoryTest.directory.getMaxLeftChildDepth(lastIndex, atomicOperation), 17);
        Assert.assertEquals(OHashTableDirectoryTest.directory.getMaxRightChildDepth(lastIndex, atomicOperation), 18);
        Assert.assertEquals(OHashTableDirectoryTest.directory.getNodeLocalDepth(lastIndex, atomicOperation), 19);
        for (int i = 0; i < (level.length); i++)
            Assert.assertEquals(OHashTableDirectoryTest.directory.getNodePointer(lastIndex, i, atomicOperation), (i + 4000));

        OHashTableDirectoryTest.rollbackTx();
    }

    @Test
    public void changeLastNodeSecondPage() throws IOException {
        OAtomicOperation atomicOperation = OHashTableDirectoryTest.startTx();
        long[] level = new long[MAX_LEVEL_SIZE];
        for (int n = 0; n < (NODES_PER_PAGE); n++) {
            for (int i = 0; i < (level.length); i++)
                level[i] = i + (n * 100);

            OHashTableDirectoryTest.directory.addNewNode(((byte) (5)), ((byte) (6)), ((byte) (7)), level, atomicOperation);
        }
        for (int n = 0; n < (ODirectoryPage.NODES_PER_PAGE); n++) {
            for (int i = 0; i < (level.length); i++)
                level[i] = i + (n * 100);

            OHashTableDirectoryTest.directory.addNewNode(((byte) (5)), ((byte) (6)), ((byte) (7)), level, atomicOperation);
        }
        for (int n = 0; n < (ODirectoryPage.NODES_PER_PAGE); n++) {
            for (int i = 0; i < (level.length); i++)
                level[i] = i + (n * 100);

            OHashTableDirectoryTest.directory.addNewNode(((byte) (5)), ((byte) (6)), ((byte) (7)), level, atomicOperation);
        }
        OHashTableDirectoryTest.directory.deleteNode((((NODES_PER_PAGE) + (ODirectoryPage.NODES_PER_PAGE)) - 1), atomicOperation);
        for (int i = 0; i < (level.length); i++)
            level[i] = i + 1000;

        int index = OHashTableDirectoryTest.directory.addNewNode(((byte) (8)), ((byte) (9)), ((byte) (10)), level, atomicOperation);
        Assert.assertEquals(index, (((NODES_PER_PAGE) + (ODirectoryPage.NODES_PER_PAGE)) - 1));
        OHashTableDirectoryTest.directory.setMaxLeftChildDepth((index - 1), ((byte) (10)), atomicOperation);
        OHashTableDirectoryTest.directory.setMaxRightChildDepth((index - 1), ((byte) (11)), atomicOperation);
        OHashTableDirectoryTest.directory.setNodeLocalDepth((index - 1), ((byte) (12)), atomicOperation);
        for (int i = 0; i < (level.length); i++)
            OHashTableDirectoryTest.directory.setNodePointer((index - 1), i, (i + 2000), atomicOperation);

        OHashTableDirectoryTest.directory.setMaxLeftChildDepth((index + 1), ((byte) (13)), atomicOperation);
        OHashTableDirectoryTest.directory.setMaxRightChildDepth((index + 1), ((byte) (14)), atomicOperation);
        OHashTableDirectoryTest.directory.setNodeLocalDepth((index + 1), ((byte) (15)), atomicOperation);
        for (int i = 0; i < (level.length); i++)
            OHashTableDirectoryTest.directory.setNodePointer((index + 1), i, (i + 3000), atomicOperation);

        Assert.assertEquals(OHashTableDirectoryTest.directory.getMaxLeftChildDepth((index - 1), atomicOperation), 10);
        Assert.assertEquals(OHashTableDirectoryTest.directory.getMaxRightChildDepth((index - 1), atomicOperation), 11);
        Assert.assertEquals(OHashTableDirectoryTest.directory.getNodeLocalDepth((index - 1), atomicOperation), 12);
        for (int i = 0; i < (level.length); i++)
            Assert.assertEquals(OHashTableDirectoryTest.directory.getNodePointer((index - 1), i, atomicOperation), (i + 2000));

        Assert.assertEquals(OHashTableDirectoryTest.directory.getMaxLeftChildDepth(index, atomicOperation), 8);
        Assert.assertEquals(OHashTableDirectoryTest.directory.getMaxRightChildDepth(index, atomicOperation), 9);
        Assert.assertEquals(OHashTableDirectoryTest.directory.getNodeLocalDepth(index, atomicOperation), 10);
        for (int i = 0; i < (level.length); i++)
            Assert.assertEquals(OHashTableDirectoryTest.directory.getNodePointer(index, i, atomicOperation), (i + 1000));

        Assert.assertEquals(OHashTableDirectoryTest.directory.getMaxLeftChildDepth((index + 1), atomicOperation), 13);
        Assert.assertEquals(OHashTableDirectoryTest.directory.getMaxRightChildDepth((index + 1), atomicOperation), 14);
        Assert.assertEquals(OHashTableDirectoryTest.directory.getNodeLocalDepth((index + 1), atomicOperation), 15);
        for (int i = 0; i < (level.length); i++)
            Assert.assertEquals(OHashTableDirectoryTest.directory.getNodePointer((index + 1), i, atomicOperation), (i + 3000));

        OHashTableDirectoryTest.rollbackTx();
    }
}

