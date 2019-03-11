package edu.umd.cs.findbugs.ba;


import NullnessAnnotation.Parser;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Verifies {@link NullnessAnnotation} parser.
 *
 * @author kzaikin
 */
@RunWith(Parameterized.class)
public class NullnessAnnotationTest {
    @Parameterized.Parameter(0)
    public String annotation;

    @Parameterized.Parameter(1)
    public NullnessAnnotation ofType;

    @Test
    public void annotationRecognition() {
        Assert.assertThat(Parser.parse(annotation), Is.is(ofType));
    }
}

