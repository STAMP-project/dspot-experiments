package butterknife;


import butterknife.compiler.ButterKnifeProcessor;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;


public class BindAnimTest {
    @Test
    public void typeMustBeAnimation() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ("" + (((("package test;\n" + "import butterknife.BindAnim;\n") + "public class Test {\n") + "  @BindAnim(1) String one;\n") + "}")));
        assertAbout(javaSource()).that(source).processedWith(new ButterKnifeProcessor()).failsToCompile().withErrorContaining("@BindAnim field type must be 'Animation'. (test.Test.one)").in(source).onLine(4);
    }
}

