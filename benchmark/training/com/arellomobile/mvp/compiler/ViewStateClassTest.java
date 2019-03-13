package com.arellomobile.mvp.compiler;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableTable;
import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import java.io.IOException;
import java.util.regex.Pattern;
import javax.tools.Diagnostic;
import org.junit.Assert;
import org.junit.Test;


/**
 * Date: 26.02.2016
 * Time: 10:50
 *
 * @author Savin Mikhail
 */
public class ViewStateClassTest extends CompilerTest {
    @Test
    public void viewStateForClassView_throw() {
        try {
            getThat(JavaFileObjects.forResource("view/CounterTestView.java"), JavaFileObjects.forResource("presenter/InjectViewStateForClassPresenter.java")).failsToCompile();
            Assert.fail();
        } catch (RuntimeException e) {
            Truth.assertThat(e.getLocalizedMessage().contains("must be INTERFACE, or not mark it as"));
        }
    }

    @Test
    public void positiveViewState() {
        try {
            assertCompilationResultIs(ImmutableTable.<Diagnostic.Kind, Integer, Pattern>of(), ImmutableList.of(getString("com/arellomobile/mvp/view/PositiveViewStateView$$State.java")));
        } catch (IOException e) {
            Assert.fail(e.getLocalizedMessage());
        }
    }
}

