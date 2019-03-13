package org.nd4j.imports;


import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.autodiff.samediff.SameDiff;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.factory.Nd4jBackend;


@Slf4j
@RunWith(Parameterized.class)
public class NameTests extends BaseNd4jTest {
    public NameTests(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testNameExtraction_1() throws Exception {
        val str = "Name";
        val exp = "Name";
        val pair = SameDiff.parseVariable(str);
        Assert.assertEquals(exp, pair.getFirst());
        Assert.assertEquals(0, pair.getSecond().intValue());
    }

    @Test
    public void testNameExtraction_2() throws Exception {
        val str = "Name_2";
        val exp = "Name_2";
        val pair = SameDiff.parseVariable(str);
        Assert.assertEquals(exp, pair.getFirst());
        Assert.assertEquals(0, pair.getSecond().intValue());
    }

    @Test
    public void testNameExtraction_3() throws Exception {
        val str = "Name_1:2";
        val exp = "Name_1";
        val pair = SameDiff.parseVariable(str);
        Assert.assertEquals(exp, pair.getFirst());
        Assert.assertEquals(2, pair.getSecond().intValue());
    }

    @Test
    public void testNameExtraction_4() throws Exception {
        val str = "Name_1:1:2";
        val exp = "Name_1:1";
        val pair = SameDiff.parseVariable(str);
        Assert.assertEquals(exp, pair.getFirst());
        Assert.assertEquals(2, pair.getSecond().intValue());
    }
}

