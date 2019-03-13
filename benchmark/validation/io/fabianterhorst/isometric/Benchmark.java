package io.fabianterhorst.isometric;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class Benchmark {
    private Isometric isometric;

    @Test
    public void test() throws Exception {
        double angle = 0;
        isometric.clear();
        for (int i = 0; i < 10; i++) {
            isometric.add(new io.fabianterhorst.isometric.shapes.Prism(new Point(1, (-1), 0), 4, 5, 2), new Color(33, 150, 243));
            isometric.add(new io.fabianterhorst.isometric.shapes.Prism(new Point(0, 0, 0), 1, 4, 1), new Color(33, 150, 243));
            isometric.add(new io.fabianterhorst.isometric.shapes.Prism(new Point((-1), 1, 0), 1, 3, 1), new Color(33, 150, 243));
            isometric.add(new io.fabianterhorst.isometric.shapes.Stairs(new Point((-1), 0, 0), 10), new Color(33, 150, 243));
            isometric.add(new io.fabianterhorst.isometric.shapes.Stairs(new Point(0, 3, 1), 10).rotateZ(new Point(0.5, 3.5, 1), ((-(Math.PI)) / 2)), new Color(33, 150, 243));
            isometric.add(new io.fabianterhorst.isometric.shapes.Prism(new Point(3, 0, 2), 2, 4, 1), new Color(33, 150, 243));
            isometric.add(new io.fabianterhorst.isometric.shapes.Prism(new Point(2, 1, 2), 1, 3, 1), new Color(33, 150, 243));
            isometric.add(new io.fabianterhorst.isometric.shapes.Stairs(new Point(2, 0, 2), 10).rotateZ(new Point(2.5, 0.5, 0), ((-(Math.PI)) / 2)), new Color(33, 150, 243));
            isometric.add(new io.fabianterhorst.isometric.shapes.Pyramid(new Point(2, 3, 3)).scale(new Point(2, 4, 3), 0.5), new Color(180, 180, 0));
            isometric.add(new io.fabianterhorst.isometric.shapes.Pyramid(new Point(4, 3, 3)).scale(new Point(5, 4, 3), 0.5), new Color(180, 0, 180));
            isometric.add(new io.fabianterhorst.isometric.shapes.Pyramid(new Point(4, 1, 3)).scale(new Point(5, 1, 3), 0.5), new Color(0, 180, 180));
            isometric.add(new io.fabianterhorst.isometric.shapes.Pyramid(new Point(2, 1, 3)).scale(new Point(2, 1, 3), 0.5), new Color(40, 180, 40));
            isometric.add(new io.fabianterhorst.isometric.shapes.Prism(new Point(3, 2, 3), 1, 1, 0.2), new Color(50, 50, 50));
            isometric.add(new io.fabianterhorst.isometric.shapes.Octahedron(new Point(3, 2, 3.2)).rotateZ(new Point(3.5, 2.5, 0), angle), new Color(0, 180, 180));
        }
        long time = runTest(new Benchmark.ShapesTest(), isometric, 1);
        System.out.println(("Time " + time));
    }

    interface TestTask<T> {
        void run(int i, T extra);
    }

    private class ShapesTest implements Benchmark.TestTask<Isometric> {
        @Override
        public void run(int i, Isometric isometric) {
            isometric.measure(1000, 1000, true, false, false);
        }
    }
}

