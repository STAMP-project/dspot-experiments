package android.graphics;


import Path.Direction.CW;
import Path.FillType.EVEN_ODD;
import androidx.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.internal.DoNotInstrument;


/**
 * Compatibility test for {@link Path}
 */
@DoNotInstrument
@RunWith(AndroidJUnit4.class)
public class PathTest {
    // Test constants
    private static final float LEFT = 10.0F;

    private static final float RIGHT = 50.0F;

    private static final float TOP = 10.0F;

    private static final float BOTTOM = 50.0F;

    private static final float XCOORD = 40.0F;

    private static final float YCOORD = 40.0F;

    @Test
    public void moveTo() {
        Path path = new Path();
        assertThat(path.isEmpty()).isTrue();
        path.moveTo(0, 0);
        assertThat(path.isEmpty()).isFalse();
    }

    @Test
    public void lineTo() {
        Path path = new Path();
        assertThat(path.isEmpty()).isTrue();
        path.lineTo(PathTest.XCOORD, PathTest.YCOORD);
        assertThat(path.isEmpty()).isFalse();
    }

    @Test
    public void quadTo() {
        Path path = new Path();
        assertThat(path.isEmpty()).isTrue();
        path.quadTo(20.0F, 20.0F, 40.0F, 40.0F);
        assertThat(path.isEmpty()).isFalse();
    }

    @Test
    public void addRect1() {
        Path path = new Path();
        assertThat(path.isEmpty()).isTrue();
        RectF rect = new RectF(PathTest.LEFT, PathTest.TOP, PathTest.RIGHT, PathTest.BOTTOM);
        path.addRect(rect, CW);
        assertThat(path.isEmpty()).isFalse();
    }

    @Test
    public void addRect2() {
        Path path = new Path();
        assertThat(path.isEmpty()).isTrue();
        path.addRect(PathTest.LEFT, PathTest.TOP, PathTest.RIGHT, PathTest.BOTTOM, CW);
        assertThat(path.isEmpty()).isFalse();
    }

    @Test
    public void getFillType() {
        Path path = new Path();
        path.setFillType(EVEN_ODD);
        assertThat(path.getFillType()).isEqualTo(EVEN_ODD);
    }

    @Test
    public void transform() {
        Path path = new Path();
        assertThat(path.isEmpty()).isTrue();
        Path dst = new Path();
        path.addRect(new RectF(PathTest.LEFT, PathTest.TOP, PathTest.RIGHT, PathTest.BOTTOM), CW);
        path.transform(new Matrix(), dst);
        assertThat(dst.isEmpty()).isFalse();
    }

    @Test
    public void testAddCircle() {
        // new the Path instance
        Path path = new Path();
        assertThat(path.isEmpty()).isTrue();
        path.addCircle(PathTest.XCOORD, PathTest.YCOORD, 10.0F, CW);
        assertThat(path.isEmpty()).isFalse();
    }

    @Test
    public void arcTo1() {
        Path path = new Path();
        assertThat(path.isEmpty()).isTrue();
        RectF oval = new RectF(PathTest.LEFT, PathTest.TOP, PathTest.RIGHT, PathTest.BOTTOM);
        path.arcTo(oval, 0.0F, 30.0F, true);
        assertThat(path.isEmpty()).isFalse();
    }

    @Test
    public void arcTo2() {
        Path path = new Path();
        assertThat(path.isEmpty()).isTrue();
        RectF oval = new RectF(PathTest.LEFT, PathTest.TOP, PathTest.RIGHT, PathTest.BOTTOM);
        path.arcTo(oval, 0.0F, 30.0F);
        assertThat(path.isEmpty()).isFalse();
    }

    @Test
    public void close() {
        Path path = new Path();
        assertThat(path.isEmpty()).isTrue();
        path.close();
    }
}

