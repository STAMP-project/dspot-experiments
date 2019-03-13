package custom.rapidoid.scan;


import cccccc.Ccccc;
import com.moja.Aaa;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.scan.Scan;
import org.rapidoid.test.AbstractCommonsTest;
import org.rapidoid.u.U;


@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class ClasspathScanTest extends AbstractCommonsTest {
    @SuppressWarnings("unchecked")
    @Test
    public void testClasspathScanByName() {
        List<Class<?>> classes = Scan.matching(".*ScanTest").loadAll();
        eq(U.set(classes), U.set(ClasspathScanTest.class));
        classes = Scan.in("custom").matching(".*Bar").loadAll();
        eq(U.set(classes), U.set(Bar.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testClasspathScanByAnnotation() {
        List<String> classes = Scan.annotated(MyAnnot.class).getAll();
        eq(U.set(classes), U.set(Foo.class.getName(), Bar.class.getName()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testScanAll() {
        List<String> classes = Scan.getAll();
        Set<String> expectedSubset = U.set(Foo.class.getName(), Bar.class.getName(), MyAnnot.class.getName(), ClasspathScanTest.class.getName(), Aaa.class.getName(), Cls.getClassIfExists("Bbb").getName(), Ccccc.class.getName());
        isTrue(U.set(classes).containsAll(expectedSubset));
    }
}

