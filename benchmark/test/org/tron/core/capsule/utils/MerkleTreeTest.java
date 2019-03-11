package org.tron.core.capsule.utils;


import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.utils.Sha256Hash;
import org.tron.core.capsule.utils.MerkleTree.Leaf;


@Slf4j
public class MerkleTreeTest {
    /**
     * Make a merkletree with no hash.
     * Will throw a exception.
     */
    @Test
    public void test0HashNum() {
        List<Sha256Hash> hashList = MerkleTreeTest.getHash(0);// Empty list.

        try {
            MerkleTree.getInstance().createTree(hashList);
            Assert.assertFalse(true);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
    }

    /**
     * Make a merkletree with 1 hash.
     *      root
     *      /    \
     *    H1   null
     *   /   \
     *  null null
     */
    @Test
    public void test1HashNum() {
        List<Sha256Hash> hashList = MerkleTreeTest.getHash(1);
        MerkleTree tree = MerkleTree.getInstance().createTree(hashList);
        Leaf root = tree.getRoot();
        Assert.assertEquals(root.getHash(), hashList.get(0));
        Leaf left = root.getLeft();
        Assert.assertEquals(left.getHash(), hashList.get(0));
        Assert.assertTrue(((left.getLeft()) == null));
        Assert.assertTrue(((left.getRight()) == null));
        Assert.assertTrue(((root.getRight()) == null));
    }

    /**
     * Make a merkletree with 2 hash.
     *        root
     *      /     \
     *    H1       H2
     *  /   \    /   \
     * null null null null
     */
    @Test
    public void test2HashNum() {
        List<Sha256Hash> hashList = MerkleTreeTest.getHash(2);
        MerkleTree tree = MerkleTree.getInstance().createTree(hashList);
        Leaf root = tree.getRoot();
        Assert.assertEquals(root.getHash(), MerkleTreeTest.computeHash(hashList.get(0), hashList.get(1)));
        Leaf left = root.getLeft();
        Assert.assertEquals(left.getHash(), hashList.get(0));
        Assert.assertTrue(((left.getLeft()) == null));
        Assert.assertTrue(((left.getRight()) == null));
        Leaf right = root.getRight();
        Assert.assertEquals(right.getHash(), hashList.get(1));
        Assert.assertTrue(((right.getLeft()) == null));
        Assert.assertTrue(((right.getRight()) == null));
    }

    /**
     * Make a merkletree with any num hash.
     *
     * rank0                                 root
     * rank1                  0                                    1
     * rank2          0                1                      2
     * rank3      0      1        2         3             4
     * rank4    0  1   2   3    4   5     6     7      8     9
     * rank5  0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18
     *
     * leftNum = 2 * headNum
     * rightNum = leftNum + 1
     * curBank < maxRank, there must have left child
     * if have left child but no right child,  headHash = leftHash
     * if both have left child and right child, headHash = SHA256(leftHash||rightHash)
     * curBank = maxRank, no child, it is real leaf. Its hash in hashList.
     */
    @Test
    public void testAnyHashNum() {
        int maxNum = 128;
        for (int hashNum = 1; hashNum <= maxNum; hashNum++) {
            int maxRank = MerkleTreeTest.getRank(hashNum);
            List<Sha256Hash> hashList = MerkleTreeTest.getHash(hashNum);
            MerkleTree tree = MerkleTree.getInstance().createTree(hashList);
            Leaf root = tree.getRoot();
            MerkleTreeTest.pareTree(root, hashList, maxRank, 0, 0);
        }
    }
}

