/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.properties;


import PropertyDescriptorField.LEGAL_PACKAGES;
import ValueParserConstants.METHOD_PARSER;
import java.lang.reflect.Method;
import java.util.Map;
import net.sourceforge.pmd.properties.modules.MethodPropertyModule;
import net.sourceforge.pmd.util.ClassUtil;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


/**
 * Evaluates the functionality of the MethodProperty descriptor by testing its
 * ability to catch creation errors (illegal args), flag invalid methods per the
 * allowable packages, and serialize/deserialize groups of methods onto/from a
 * string buffer.
 *
 * We're using methods from java.lang classes for 'normal' constructors and
 * applying ones from java.util types as ones we expect to fail.
 *
 * @author Brian Remedios
 */
public class MethodPropertyTest extends AbstractPackagedPropertyDescriptorTester<Method> {
    private static final Method[] ALL_METHODS = String.class.getDeclaredMethods();

    private static final String[] METHOD_SIGNATURES = new String[]{ "String#indexOf(int)", "String#substring(int,int)", "java.lang.String#substring(int,int)", "Integer#parseInt(String)", "java.util.HashMap#put(Object,Object)", "HashMap#containsKey(Object)" };

    public MethodPropertyTest() {
        super("Method");
    }

    @Override
    @Test
    public void testMissingPackageNames() {
        Map<PropertyDescriptorField, String> attributes = getPropertyDescriptorValues();
        attributes.remove(LEGAL_PACKAGES);
        new MethodProperty("p", "d", MethodPropertyTest.ALL_METHODS[1], null, 1.0F);// no exception, null is ok

        new MethodMultiProperty("p", "d", new Method[]{ MethodPropertyTest.ALL_METHODS[2], MethodPropertyTest.ALL_METHODS[3] }, null, 1.0F);// no exception, null is ok

    }

    @Test
    public void testAsStringOn() {
        Method method;
        for (String methodSignature : MethodPropertyTest.METHOD_SIGNATURES) {
            method = METHOD_PARSER.valueOf(methodSignature);
            Assert.assertNotNull(("Unable to identify method: " + methodSignature), method);
        }
    }

    @Test
    public void testAsMethodOn() {
        Method[] methods = new Method[MethodPropertyTest.METHOD_SIGNATURES.length];
        for (int i = 0; i < (MethodPropertyTest.METHOD_SIGNATURES.length); i++) {
            methods[i] = METHOD_PARSER.valueOf(MethodPropertyTest.METHOD_SIGNATURES[i]);
            Assert.assertNotNull(("Unable to identify method: " + (MethodPropertyTest.METHOD_SIGNATURES[i])), methods[i]);
        }
        String translatedMethod;
        for (int i = 0; i < (methods.length); i++) {
            translatedMethod = MethodPropertyModule.asString(methods[i]);
            Assert.assertTrue("Translated method does not match", ClassUtil.withoutPackageName(MethodPropertyTest.METHOD_SIGNATURES[i]).equals(ClassUtil.withoutPackageName(translatedMethod)));
        }
    }

    @Override
    @Test
    public void testFactorySingleValue() {
        Assume.assumeTrue("MethodProperty cannot be built from XPath (#762)", false);
    }

    @Override
    @Test
    public void testFactoryMultiValueCustomDelimiter() {
        Assume.assumeTrue("MethodProperty cannot be built from XPath (#762)", false);
    }

    @Override
    @Test
    public void testFactoryMultiValueDefaultDelimiter() {
        Assume.assumeTrue("MethodProperty cannot be built from XPath (#762)", false);
    }
}

