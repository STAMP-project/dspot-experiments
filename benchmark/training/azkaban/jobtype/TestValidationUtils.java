package azkaban.jobtype;


import azkaban.jobtype.javautils.ValidationUtils;
import azkaban.utils.Props;
import azkaban.utils.UndefinedPropertyException;
import org.junit.Test;


public class TestValidationUtils {
    private static final Props PROPS = new Props();

    static {
        TestValidationUtils.PROPS.put("a", "a");
        TestValidationUtils.PROPS.put("b", "b");
        TestValidationUtils.PROPS.put("c", "c");
        TestValidationUtils.PROPS.put("d", "d");
    }

    @Test
    public void testAllExistSucess() {
        String[] keys = new String[]{ "a", "b", "c", "d" };
        ValidationUtils.validateAllOrNone(TestValidationUtils.PROPS, keys);
        ValidationUtils.validateAllNotEmpty(TestValidationUtils.PROPS, keys);
    }

    @Test(expected = UndefinedPropertyException.class)
    public void testAllExistFail() {
        ValidationUtils.validateAllNotEmpty(TestValidationUtils.PROPS, "x", "y");
    }

    @Test(expected = UndefinedPropertyException.class)
    public void testAllExistFail2() {
        ValidationUtils.validateAllNotEmpty(TestValidationUtils.PROPS, "a", "y");
    }

    @Test
    public void testNoneExistSuccess() {
        ValidationUtils.validateAllOrNone(TestValidationUtils.PROPS, "z");
        ValidationUtils.validateAllOrNone(TestValidationUtils.PROPS, "e", "f", "g");
    }
}

