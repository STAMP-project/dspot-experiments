package butterknife;


import butterknife.compiler.ButterKnifeProcessor;
import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;


public final class BindBoolTest {
    @Test
    public void typeMustBeBoolean() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ("" + (((("package test;\n" + "import butterknife.BindBool;\n") + "public class Test {\n") + "  @BindBool(1) String one;\n") + "}")));
        assertAbout(javaSource()).that(source).processedWith(new ButterKnifeProcessor()).failsToCompile().withErrorContaining("@BindBool field type must be 'boolean'. (test.Test.one)").in(source).onLine(4);
    }
}

