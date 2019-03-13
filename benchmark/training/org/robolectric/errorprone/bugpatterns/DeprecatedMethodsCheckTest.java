package org.robolectric.errorprone.bugpatterns;


import com.google.errorprone.BugCheckerRefactoringTestHelper;
import com.google.errorprone.BugPattern;
import com.google.errorprone.BugPattern.ProvidesFix;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 *
 *
 * @author christianw@google.com (Christian Williams)
 */
@RunWith(JUnit4.class)
@SuppressWarnings("LineLength")
public class DeprecatedMethodsCheckTest {
    private BugCheckerRefactoringTestHelper testHelper;

    @Test
    public void replaceShadowApplicationGetInstance() throws IOException {
        // removable
        testHelper.addInputLines("in/SomeTest.java", "import android.content.Context;", "import org.junit.Test;", "import xxx.XShadowApplication;", "", "public class SomeTest {", "  Context application;", "  @Test void theTest() {", "    XShadowApplication.getInstance().runBackgroundTasks();", "    application = XShadowApplication.getInstance().getApplicationContext();", "  }", "}").addOutputLines("in/SomeTest.java", "import static xxx.XShadows.shadowOf;", "", "import android.content.Context;", "import org.junit.Test;", "import org.robolectric.RuntimeEnvironment;", "import xxx.XShadowApplication;", "", "public class SomeTest {", "  Context application;", "  @Test void theTest() {", "    shadowOf(RuntimeEnvironment.application).runBackgroundTasks();", "    application = RuntimeEnvironment.application;", "  }", "}").doTest();
    }

    @Test
    public void replaceShadowApplicationGetLatestStuff() throws IOException {
        // removable
        testHelper.addInputLines("in/SomeTest.java", "import static xxx.XShadows.shadowOf;", "", "import org.junit.Test;", "import org.robolectric.RuntimeEnvironment;", "import xxx.XShadowApplication;", "import xxx.XShadowAlertDialog;", "import xxx.XShadowDialog;", "import xxx.XShadowPopupMenu;", "", "public class SomeTest {", "  @Test void theTest() {", "    XShadowAlertDialog ad = shadowOf(RuntimeEnvironment.application).getLatestAlertDialog();", "    XShadowDialog d = shadowOf(RuntimeEnvironment.application).getLatestDialog();", "    XShadowPopupMenu pm = shadowOf(RuntimeEnvironment.application).getLatestPopupMenu();", "  }", "}").addOutputLines("in/SomeTest.java", "import static xxx.XShadows.shadowOf;", "", "import org.junit.Test;", "import org.robolectric.RuntimeEnvironment;", "import xxx.XShadowApplication;", "import xxx.XShadowAlertDialog;", "import xxx.XShadowDialog;", "import xxx.XShadowPopupMenu;", "", "public class SomeTest {", "  @Test void theTest() {", "    XShadowAlertDialog ad = shadowOf(XShadowAlertDialog.getLatestAlertDialog());", "    XShadowDialog d = shadowOf(XShadowDialog.getLatestDialog());", "    XShadowPopupMenu pm = shadowOf(XShadowPopupMenu.getLatestPopupMenu());", "  }", "}").doTest();
    }

    @Test
    public void useShadowsNonStaticIfAlreadyImported() throws IOException {
        // removable
        testHelper.addInputLines("in/SomeTest.java", "import android.content.Context;", "import org.junit.Test;", "import xxx.XShadows;", "import xxx.XShadowApplication;", "", "public class SomeTest {", "  Context application;", "  @Test void theTest() {", "    XShadowApplication.getInstance().runBackgroundTasks();", "    application = XShadowApplication.getInstance().getApplicationContext();", "  }", "}").addOutputLines("in/SomeTest.java", "import android.content.Context;", "import org.junit.Test;", "import org.robolectric.RuntimeEnvironment;", "import xxx.XShadowApplication;", "import xxx.XShadows;", "", "public class SomeTest {", "  Context application;", "  @Test void theTest() {", "    XShadows.shadowOf(RuntimeEnvironment.application).runBackgroundTasks();", "    application = RuntimeEnvironment.application;", "  }", "}").doTest();
    }

    @BugPattern(name = "DeprecatedMethods", providesFix = ProvidesFix.REQUIRES_HUMAN_ATTENTION, summary = "", severity = WARNING)
    private static class DeprecatedMethodsCheckForTest extends DeprecatedMethodsCheck {
        @Override
        String shadowName(String className) {
            return className.replaceAll("org\\.robolectric\\..*Shadow", "xxx.XShadow");
        }

        @Override
        String shortShadowName(String className) {
            return className.replaceAll("Shadow", "XShadow");
        }
    }
}

