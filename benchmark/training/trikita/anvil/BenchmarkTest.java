package trikita.anvil;


import Anvil.Renderable;
import org.junit.Test;


public class BenchmarkTest extends Utils {
    private static final int N = 100000;

    private int mode;

    @Test
    public void testRenderBenchmark() {
        long start;
        Anvil.Renderable r = new Anvil.Renderable() {
            public void view() {
                for (int i = 0; i < 10; i++) {
                    final int fi = i;
                    group(transform(i), new Anvil.Renderable() {
                        public void view() {
                            for (int j = 0; j < 10; j++) {
                                item(transform(((fi * 10) + j)));
                            }
                        }
                    });
                }
            }
        };
        mode = 0;
        Anvil.mount(container, r);
        start = System.currentTimeMillis();
        for (int i = 0; i < (BenchmarkTest.N); i++) {
            Anvil.render();
        }
        System.out.println((("render/no-changes: " + ((((System.currentTimeMillis()) - start) * 1000) / (BenchmarkTest.N))) + "us"));
        Anvil.unmount(container, true);
        mode = 1;
        Anvil.mount(container, r);
        start = System.currentTimeMillis();
        for (int i = 0; i < (BenchmarkTest.N); i++) {
            Anvil.render();
        }
        System.out.println((("render/small-changes: " + ((((System.currentTimeMillis()) - start) * 1000) / (BenchmarkTest.N))) + "us"));
        Anvil.unmount(container, true);
        mode = 2;
        Anvil.mount(container, r);
        start = System.currentTimeMillis();
        for (int i = 0; i < (BenchmarkTest.N); i++) {
            Anvil.render();
        }
        System.out.println((("render/big-changes: " + ((((System.currentTimeMillis()) - start) * 1000) / (BenchmarkTest.N))) + "us"));
    }
}

