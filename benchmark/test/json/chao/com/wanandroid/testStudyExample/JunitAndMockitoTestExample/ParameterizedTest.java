package json.chao.com.wanandroid.testStudyExample.JunitAndMockitoTestExample;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 *
 * @author quchao
 * @unknown 2018/6/5
 */
@RunWith(Parameterized.class)
public class ParameterizedTest {
    private int num;

    private boolean truth;

    public ParameterizedTest(int num, boolean truth) {
        this.num = num;
        this.truth = truth;
    }

    // //?????????????????????public?????
    // @Parameterized.Parameter
    // public int num;
    // //value = 1?????????Boolean?
    // @Parameterized.Parameter(value = 1)
    // public boolean truth;
    @Test
    public void printTest() {
        Assert.assertEquals(truth, print(num));
        System.out.println(num);
    }
}

