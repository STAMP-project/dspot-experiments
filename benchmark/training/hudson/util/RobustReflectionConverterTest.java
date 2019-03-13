/**
 * The MIT License
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., Kohsuke Kawaguchi
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
package hudson.util;


import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class RobustReflectionConverterTest {
    static {
        Logger.getLogger(RobustReflectionConverter.class.getName()).setLevel(Level.OFF);
    }

    @Test
    public void robustUnmarshalling() {
        Point p = read(new XStream2());
        Assert.assertEquals(p.x, 1);
        Assert.assertEquals(p.y, 2);
    }

    @Test
    public void ifWorkaroundNeeded() {
        try {
            read(new XStream());
            Assert.fail();
        } catch (ConversionException e) {
            // expected
            Assert.assertTrue(e.getMessage().contains("z"));
        }
    }

    @Test
    public void classOwnership() throws Exception {
        XStream xs = new XStream2(new XStream2.ClassOwnership() {
            @Override
            public String ownerOf(Class<?> clazz) {
                RobustReflectionConverterTest.Owner o = clazz.getAnnotation(RobustReflectionConverterTest.Owner.class);
                return o != null ? o.value() : null;
            }
        });
        String prefix1 = (RobustReflectionConverterTest.class.getName()) + "_-";
        String prefix2 = (RobustReflectionConverterTest.class.getName()) + "$";
        RobustReflectionConverterTest.Enchufla s1 = new RobustReflectionConverterTest.Enchufla();
        s1.number = 1;
        s1.direction = "North";
        RobustReflectionConverterTest.Moonwalk s2 = new RobustReflectionConverterTest.Moonwalk();
        s2.number = 2;
        s2.boot = new RobustReflectionConverterTest.Boot();
        s2.lover = new RobustReflectionConverterTest.Billy();
        RobustReflectionConverterTest.Moonwalk s3 = new RobustReflectionConverterTest.Moonwalk();
        s3.number = 3;
        s3.boot = new RobustReflectionConverterTest.Boot();
        s3.jacket = new RobustReflectionConverterTest.Jacket();
        s3.lover = new RobustReflectionConverterTest.Jean();
        RobustReflectionConverterTest.Bild b = new RobustReflectionConverterTest.Bild();
        b.steppes = new RobustReflectionConverterTest.Steppe[]{ s1, s2, s3 };
        RobustReflectionConverterTest.Projekt p = new RobustReflectionConverterTest.Projekt();
        p.bildz = new RobustReflectionConverterTest.Bild[]{ b };
        Assert.assertEquals(("<Projekt><bildz><Bild><steppes>" + ((("<Enchufla plugin='p1'><number>1</number><direction>North</direction></Enchufla>" + // note no plugin='p2' on <boot/> since that would be redundant; <jacket/> is quiet even though unowned
        "<Moonwalk plugin='p2'><number>2</number><boot/><lover class='Billy' plugin='p3'/></Moonwalk>") + "<Moonwalk plugin='p2'><number>3</number><boot/><jacket/><lover class='Jean' plugin='p4'/></Moonwalk>") + "</steppes></Bild></bildz></Projekt>")), xs.toXML(p).replace(prefix1, "").replace(prefix2, "").replaceAll("\r?\n *", "").replace('"', '\''));
        RobustReflectionConverterTest.Moonwalk s = ((RobustReflectionConverterTest.Moonwalk) (xs.fromXML((((((("<" + prefix1) + "Moonwalk plugin='p2'><lover class='") + prefix2) + "Billy' plugin='p3'/></") + prefix1) + "Moonwalk>"))));
        Assert.assertEquals(RobustReflectionConverterTest.Billy.class, s.lover.getClass());
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface Owner {
        String value();
    }

    public static class Projekt {
        RobustReflectionConverterTest.Bild[] bildz;
    }

    public static class Bild {
        RobustReflectionConverterTest.Steppe[] steppes;
    }

    public abstract static class Steppe {
        int number;
    }

    @RobustReflectionConverterTest.Owner("p1")
    public static class Enchufla extends RobustReflectionConverterTest.Steppe {
        String direction;
    }

    @RobustReflectionConverterTest.Owner("p2")
    public static class Moonwalk extends RobustReflectionConverterTest.Steppe {
        RobustReflectionConverterTest.Boot boot;

        RobustReflectionConverterTest.Jacket jacket;

        RobustReflectionConverterTest.Lover lover;
    }

    @RobustReflectionConverterTest.Owner("p2")
    public static class Boot {}

    public static class Jacket {}

    @RobustReflectionConverterTest.Owner("p2")
    public abstract static class Lover {}

    @RobustReflectionConverterTest.Owner("p3")
    public static class Billy extends RobustReflectionConverterTest.Lover {}

    @RobustReflectionConverterTest.Owner("p4")
    public static class Jean extends RobustReflectionConverterTest.Lover {}
}

