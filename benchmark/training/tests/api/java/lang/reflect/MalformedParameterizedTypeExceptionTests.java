package tests.api.java.lang.reflect;


import java.lang.reflect.Constructor;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.Modifier;
import junit.framework.TestCase;


public class MalformedParameterizedTypeExceptionTests extends TestCase {
    /**
     * java.lang.reflect.MalformedParameterizedTypeException#MalformedParameterizedTypeException()
     */
    public void test_Constructor() throws Exception {
        Constructor<MalformedParameterizedTypeException> ctor = MalformedParameterizedTypeException.class.getDeclaredConstructor();
        TestCase.assertNotNull("Parameterless constructor does not exist.", ctor);
        TestCase.assertTrue("Constructor is not protected", Modifier.isPublic(ctor.getModifiers()));
        TestCase.assertNotNull(ctor.newInstance());
    }
}

