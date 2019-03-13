package cn.hutool.core.comparator;


import VersionComparator.INSTANCE;
import org.junit.Assert;
import org.junit.Test;


/**
 * ????????
 *
 * @author looly
 */
public class VersionComparatorTest {
    @Test
    public void versionComparatorTest1() {
        int compare = INSTANCE.compare("1.2.1", "1.12.1");
        Assert.assertTrue((compare < 0));
    }

    @Test
    public void versionComparatorTest2() {
        int compare = INSTANCE.compare("1.12.1", "1.12.1c");
        Assert.assertTrue((compare < 0));
    }

    @Test
    public void versionComparatorTest3() {
        int compare = INSTANCE.compare(null, "1.12.1c");
        Assert.assertTrue((compare < 0));
    }

    @Test
    public void versionComparatorTest4() {
        int compare = INSTANCE.compare("1.13.0", "1.12.1c");
        Assert.assertTrue((compare > 0));
    }

    @Test
    public void versionComparatorTest5() {
        int compare = INSTANCE.compare("V1.2", "V1.1");
        Assert.assertTrue((compare > 0));
    }

    @Test
    public void versionComparatorTes6() {
        int compare = INSTANCE.compare("V0.0.20170102", "V0.0.20170101");
        Assert.assertTrue((compare > 0));
    }
}

