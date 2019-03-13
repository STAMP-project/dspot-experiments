/**
 * The MIT License
 *
 * Copyright (c) 2018, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jenkins.security.stapler;


import View.People;
import com.cloudbees.hudson.plugins.folder.Folder;
import hudson.model.TopLevelItem;
import java.awt.Point;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jenkins.model.Jenkins;
import org.apache.commons.codec.Encoder;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Test;
import org.jvnet.hudson.test.For;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.TestExtension;


/**
 * To check the previous behavior you can use:
 * <pre>
 * {@link org.kohsuke.stapler.MetaClass#LEGACY_GETTER_MODE} = true;
 * </pre>
 * It will disable the usage of {@link TypedFilter}
 */
@Issue("SECURITY-400")
@For(TypedFilter.class)
public class GetterMethodFilterTest extends StaplerAbstractTest {
    @TestExtension
    public static class TestWithReturnJavaPlatformObject extends StaplerAbstractTest.AbstractUnprotectedRootAction {
        public static boolean called = false;

        public String getString() {
            return "a";
        }

        // cannot provide side-effect since the String has no side-effect methods
        public Object getObjectString() {
            return "a";
        }

        // but it opens wide range of potentially dangerous classes
        public Object getObjectCustom() {
            return new Object() {
                // in order to provide a web entry-point
                public void doIndex() {
                    StaplerAbstractTest.replyOk();
                }
            };
        }

        public Point getPoint() {
            return new Point(1, 2);
        }

        public Point getPointCustomChild() {
            return new Point() {
                // in order to provide a web entry-point
                public void doIndex() {
                    StaplerAbstractTest.replyOk();
                }
            };
        }

        public Point getPointWithListener() {
            return new Point() {
                @Override
                public double getX() {
                    // just to demonstrate the potential side-effect
                    GetterMethodFilterTest.TestWithReturnJavaPlatformObject.called = true;
                    return super.getX();
                }
            };
        }
    }

    @Test
    public void testWithReturnJavaPlatformObject_string() throws Exception {
        assertNotReachable("testWithReturnJavaPlatformObject/string/");
    }

    @Test
    public void testWithReturnJavaPlatformObject_objectString() throws Exception {
        assertNotReachable("testWithReturnJavaPlatformObject/objectString/");
    }

    @Test
    public void testWithReturnJavaPlatformObject_objectCustom() throws Exception {
        assertNotReachable("testWithReturnJavaPlatformObject/objectCustom/");
    }

    @Test
    public void testWithReturnJavaPlatformObject_point() throws Exception {
        assertNotReachable("testWithReturnJavaPlatformObject/point/");
    }

    // previously reachable and so potentially open to future security vulnerability
    @Test
    public void testWithReturnJavaPlatformObject_pointCustomChild() throws Exception {
        assertNotReachable("testWithReturnJavaPlatformObject/pointCustomChild/");
    }

    @Test
    public void testWithReturnJavaPlatformObject_pointWithListener() throws Exception {
        GetterMethodFilterTest.TestWithReturnJavaPlatformObject.called = false;
        Assert.assertFalse(GetterMethodFilterTest.TestWithReturnJavaPlatformObject.called);
        // could potentially trigger some side-effects
        assertNotReachable("testWithReturnJavaPlatformObject/pointWithListener/x/");
        Assert.assertFalse(GetterMethodFilterTest.TestWithReturnJavaPlatformObject.called);
    }

    @TestExtension
    public static class TestWithReturnMultiple extends StaplerAbstractTest.AbstractUnprotectedRootAction {
        public List<StaplerAbstractTest.Renderable> getList() {
            return Arrays.asList(new StaplerAbstractTest.Renderable(), new StaplerAbstractTest.Renderable());
        }

        // as we cannot determine the element class due to type erasure, this is reachable
        public List<? extends Point> getListOfPoint() {
            return Collections.singletonList(new StaplerAbstractTest.RenderablePoint());
        }

        public List<List<StaplerAbstractTest.Renderable>> getListOfList() {
            return Collections.singletonList(Arrays.asList(new StaplerAbstractTest.Renderable(), new StaplerAbstractTest.Renderable()));
        }

        public StaplerAbstractTest.Renderable[] getArray() {
            return new StaplerAbstractTest.Renderable[]{ new StaplerAbstractTest.Renderable(), new StaplerAbstractTest.Renderable() };
        }

        // will not be accepted since the componentType is from JVM
        public Point[] getArrayOfPoint() {
            return new Point[]{ new Point() {
                public void doIndex() {
                    StaplerAbstractTest.replyOk();
                }
            } };
        }

        public StaplerAbstractTest.Renderable[][] getArrayOfArray() {
            return new StaplerAbstractTest.Renderable[][]{ new StaplerAbstractTest.Renderable[]{ new StaplerAbstractTest.Renderable(), new StaplerAbstractTest.Renderable() } };
        }

        @SuppressWarnings("unchecked")
        public List<StaplerAbstractTest.Renderable>[] getArrayOfList() {
            List<StaplerAbstractTest.Renderable> list = Arrays.asList(new StaplerAbstractTest.Renderable(), new StaplerAbstractTest.Renderable());
            return ((List<StaplerAbstractTest.Renderable>[]) (Collections.singletonList(list).toArray(new List[0])));
        }

        public List<StaplerAbstractTest.Renderable[]> getListOfArray() {
            return Collections.singletonList(new StaplerAbstractTest.Renderable[]{ new StaplerAbstractTest.Renderable(), new StaplerAbstractTest.Renderable() });
        }

        public Map<String, StaplerAbstractTest.Renderable> getMap() {
            return new HashMap<String, StaplerAbstractTest.Renderable>() {
                {
                    put("a", new StaplerAbstractTest.Renderable());
                }
            };
        }
    }

    @Test
    public void testWithReturnMultiple_list() throws Exception {
        assertNotReachable("testWithReturnMultiple/list/");
        assertNotReachable("testWithReturnMultiple/list/0/");
        assertNotReachable("testWithReturnMultiple/list/1/");
        assertNotReachable("testWithReturnMultiple/list/2/");
    }

    @Test
    public void testWithReturnMultiple_listOfPoint() throws Exception {
        assertNotReachable("testWithReturnMultiple/listOfPoint/");
        assertNotReachable("testWithReturnMultiple/listOfPoint/0/");
        assertNotReachable("testWithReturnMultiple/listOfPoint/1/");
    }

    @Test
    public void testWithReturnMultiple_listOfList() throws Exception {
        assertNotReachable("testWithReturnMultiple/listOfList/");
        assertNotReachable("testWithReturnMultiple/listOfList/0/");
        assertNotReachable("testWithReturnMultiple/listOfList/1/");
        assertNotReachable("testWithReturnMultiple/listOfList/0/0/");
        assertNotReachable("testWithReturnMultiple/listOfList/0/1/");
        assertNotReachable("testWithReturnMultiple/listOfList/0/2/");
    }

    @Test
    public void testWithReturnMultiple_array() throws Exception {
        assertNotReachable("testWithReturnMultiple/array/");
        assertReachable("testWithReturnMultiple/array/0/");
        assertReachable("testWithReturnMultiple/array/1/");
        assertNotReachable("testWithReturnMultiple/array/2/");
    }

    @Test
    public void testWithReturnMultiple_arrayOfPoint() throws Exception {
        assertNotReachable("testWithReturnMultiple/arrayOfPoint/");
        assertNotReachable("testWithReturnMultiple/arrayOfPoint/0/");
        assertNotReachable("testWithReturnMultiple/arrayOfPoint/1/");
    }

    @Test
    public void testWithReturnMultiple_arrayOfArray() throws Exception {
        assertNotReachable("testWithReturnMultiple/arrayOfArray/");
        assertNotReachable("testWithReturnMultiple/arrayOfArray/0/");
        assertNotReachable("testWithReturnMultiple/arrayOfArray/1/");
        assertReachable("testWithReturnMultiple/arrayOfArray/0/0/");
        assertReachable("testWithReturnMultiple/arrayOfArray/0/1/");
        assertNotReachable("testWithReturnMultiple/arrayOfArray/0/2/");
    }

    @Test
    public void testWithReturnMultiple_arrayOfList() throws Exception {
        assertNotReachable("testWithReturnMultiple/arrayOfList/");
        assertNotReachable("testWithReturnMultiple/arrayOfList/0/");
        assertNotReachable("testWithReturnMultiple/arrayOfList/1/");
        assertNotReachable("testWithReturnMultiple/arrayOfList/0/0/");
        assertNotReachable("testWithReturnMultiple/arrayOfList/0/1/");
        assertNotReachable("testWithReturnMultiple/arrayOfList/0/2/");
    }

    @Test
    public void testWithReturnMultiple_listOfArray() throws Exception {
        assertNotReachable("testWithReturnMultiple/listOfArray/");
        assertNotReachable("testWithReturnMultiple/listOfArray/0/");
        assertNotReachable("testWithReturnMultiple/listOfArray/1/");
        assertNotReachable("testWithReturnMultiple/listOfArray/0/0/");
        assertNotReachable("testWithReturnMultiple/listOfArray/0/1/");
        assertNotReachable("testWithReturnMultiple/listOfArray/0/2/");
    }

    @Test
    public void testWithReturnMultiple_map() throws Exception {
        assertNotReachable("testWithReturnMultiple/map/");
        assertNotReachable("testWithReturnMultiple/map/a/");
        assertNotReachable("testWithReturnMultiple/map/b/");
    }

    @TestExtension
    public static class TestWithReturnCoreObject extends StaplerAbstractTest.AbstractUnprotectedRootAction {
        public People getPeople() {
            // provide an index jelly view
            return new hudson.model.View.People(Jenkins.getInstance());
        }
    }

    @Test
    public void testWithReturnCoreObject_people() throws Exception {
        assertReachableWithoutOk("testWithReturnCoreObject/people/");
    }

    @Test
    public void testTopLevelItemIsLegal() throws Exception {
        TopLevelItem item = j.createFreeStyleProject();
        assertReachableWithoutOk(("job/" + (item.getName())));
    }

    @TestExtension
    public static class TestWithReturnPluginObject extends StaplerAbstractTest.AbstractUnprotectedRootAction {
        public Folder getFolder() {
            return new Folder(Jenkins.getInstance(), "testFolder");
        }
    }

    @Test
    public void testWithReturnPluginObject_folder() throws Exception {
        // the search part is just to get something from the call
        assertReachableWithoutOk("testWithReturnPluginObject/folder/search/suggest/?query=xxx");
    }

    // full package name just to be explicit
    @TestExtension
    public static class TestWithReturnThirdPartyObject extends StaplerAbstractTest.AbstractUnprotectedRootAction {
        public Base64 getBase64() {
            return new Base64();
        }

        public Encoder getEncoder() {
            return new Base64();
        }

        public Encoder getEncoderCustomChild() {
            return new Encoder() {
                @Override
                public Object encode(Object source) throws EncoderException {
                    // it's not about implementation...
                    return null;
                }

                public void doIndex() {
                    // it's about sending a message
                    StaplerAbstractTest.replyOk();
                }
            };
        }
    }

    // the class itself was reachable but no more interaction are available and so return 404
    @Test
    public void testWithReturnThirdPartyObject_base32() throws Exception {
        assertNotReachable("testWithReturnThirdPartyObject/base32/");
    }

    // the class itself was reachable but no more interaction are available and so return 404,
    // in case there is some callable methods, we could create some side-effect even we got 404
    @Test
    public void testWithReturnThirdPartyObject_encoder() throws Exception {
        assertNotReachable("testWithReturnThirdPartyObject/encoder/");
    }

    // as we add a entry-point in the class, now it can propose some interaction,
    // dangerous behavior that is not prohibited
    @Test
    public void testWithReturnThirdPartyObject_encoderCustomChild() throws Exception {
        assertNotReachable("testWithReturnThirdPartyObject/encoderCustomChild/");
    }

    // ================================= getter methods with primitives =================================
    @TestExtension
    public static class TestWithReturnPrimitives extends StaplerAbstractTest.AbstractUnprotectedRootAction {
        public int getInteger() {
            return 1;
        }

        public Integer getIntegerObject() {
            return 1;
        }

        public long getLong() {
            return 1L;
        }

        public Long getLongObject() {
            return 1L;
        }

        public short getShort() {
            return ((short) (1));
        }

        public Short getShortObject() {
            return 1;
        }

        public byte getByte() {
            return ((byte) (1));
        }

        public Byte getByteObject() {
            return ((byte) (1));
        }

        public boolean getBoolean() {
            return true;
        }

        public Boolean getBooleanObject() {
            return Boolean.TRUE;
        }

        public char getChar() {
            return 'a';
        }

        public Character getCharObject() {
            return 'a';
        }

        public float getFloat() {
            return 1.0F;
        }

        public Float getFloatObject() {
            return 1.0F;
        }

        public double getDouble() {
            return 1.0;
        }

        public Double getDoubleObject() {
            return 1.0;
        }

        public void getVoid() {
        }

        public Void getVoidObject() {
            return null;
        }
    }

    @Test
    public void testTestWithReturnPrimitives_integer() throws Exception {
        assertNotReachable("testWithReturnPrimitives/integer/");
        assertGetMethodRequestWasBlockedAndResetFlag();
    }

    @Test
    public void testTestWithReturnPrimitives_integerObject() throws Exception {
        assertNotReachable("testWithReturnPrimitives/integerObject/");
        assertGetMethodRequestWasBlockedAndResetFlag();
    }

    @Test
    public void testTestWithReturnPrimitives_long() throws Exception {
        assertNotReachable("testWithReturnPrimitives/long/");
        assertGetMethodRequestWasBlockedAndResetFlag();
    }

    @Test
    public void testTestWithReturnPrimitives_longObject() throws Exception {
        assertNotReachable("testWithReturnPrimitives/longObject/");
        assertGetMethodRequestWasBlockedAndResetFlag();
    }

    @Test
    public void testTestWithReturnPrimitives_short() throws Exception {
        assertNotReachable("testWithReturnPrimitives/short/");
        assertGetMethodRequestWasBlockedAndResetFlag();
    }

    @Test
    public void testTestWithReturnPrimitives_shortObject() throws Exception {
        assertNotReachable("testWithReturnPrimitives/shortObject/");
        assertGetMethodRequestWasBlockedAndResetFlag();
    }

    @Test
    public void testTestWithReturnPrimitives_byte() throws Exception {
        assertNotReachable("testWithReturnPrimitives/byte/");
        assertGetMethodRequestWasBlockedAndResetFlag();
    }

    @Test
    public void testTestWithReturnPrimitives_byteObject() throws Exception {
        assertNotReachable("testWithReturnPrimitives/byteObject/");
        assertGetMethodRequestWasBlockedAndResetFlag();
    }

    @Test
    public void testTestWithReturnPrimitives_boolean() throws Exception {
        assertNotReachable("testWithReturnPrimitives/boolean/");
        assertGetMethodRequestWasBlockedAndResetFlag();
    }

    @Test
    public void testTestWithReturnPrimitives_booleanObject() throws Exception {
        assertNotReachable("testWithReturnPrimitives/booleanObject/");
        assertGetMethodRequestWasBlockedAndResetFlag();
    }

    @Test
    public void testTestWithReturnPrimitives_char() throws Exception {
        assertNotReachable("testWithReturnPrimitives/char/");
        assertGetMethodRequestWasBlockedAndResetFlag();
    }

    @Test
    public void testTestWithReturnPrimitives_charObject() throws Exception {
        assertNotReachable("testWithReturnPrimitives/charObject/");
        assertGetMethodRequestWasBlockedAndResetFlag();
    }

    @Test
    public void testTestWithReturnPrimitives_float() throws Exception {
        assertNotReachable("testWithReturnPrimitives/float/");
        assertGetMethodRequestWasBlockedAndResetFlag();
    }

    @Test
    public void testTestWithReturnPrimitives_floatObject() throws Exception {
        assertNotReachable("testWithReturnPrimitives/floatObject/");
        assertGetMethodRequestWasBlockedAndResetFlag();
    }

    @Test
    public void testTestWithReturnPrimitives_double() throws Exception {
        assertNotReachable("testWithReturnPrimitives/double/");
        assertGetMethodRequestWasBlockedAndResetFlag();
    }

    @Test
    public void testTestWithReturnPrimitives_doubleObject() throws Exception {
        assertNotReachable("testWithReturnPrimitives/doubleObject/");
        assertGetMethodRequestWasBlockedAndResetFlag();
    }

    @Test
    public void testTestWithReturnPrimitives_void() throws Exception {
        assertNotReachable("testWithReturnPrimitives/void/");
        assertGetMethodRequestWasBlockedAndResetFlag();
    }

    @Test
    public void testTestWithReturnPrimitives_voidObject() throws Exception {
        assertNotReachable("testWithReturnPrimitives/voidObject/");
        assertGetMethodRequestWasBlockedAndResetFlag();
    }

    // ================================= getter methods =================================
    @TestExtension
    public static class TestWithReturnWithinStaplerScope extends StaplerAbstractTest.AbstractUnprotectedRootAction {
        public StaplerAbstractTest.Renderable getRenderable() {
            return new StaplerAbstractTest.Renderable();
        }
    }

    @Test
    public void testWithReturnWithinStaplerScope_renderable() throws Exception {
        assertReachable("testWithReturnWithinStaplerScope/renderable/");
        assertReachable("testWithReturnWithinStaplerScope/renderable/valid/");
    }
}

