package tech.tablesaw.plotly;


import Layout.BarMode.OVERLAY;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.HistogramTrace;


@Disabled
public class HistogramTraceTest {
    private final double[] y1 = new double[]{ 1, 4, 9, 16, 11, 4, -1, 20, 4, 7, 9, 12, 8, 6, 28, 12 };

    private final double[] y2 = new double[]{ 3, 11, 19, 14, 11, 14, 5, 24, -4, 10, 15, 6, 5, 18 };

    @Test
    public void testAsJavascript() {
        HistogramTrace trace1 = HistogramTrace.builder(y1).build();
        System.out.println(trace1.asJavascript(1));
    }

    @Test
    public void show() {
        Layout layout = Layout.builder().barMode(OVERLAY).build();
        HistogramTrace trace1 = HistogramTrace.builder(y1).opacity(0.75).build();
        HistogramTrace trace2 = HistogramTrace.builder(y2).opacity(0.75).build();
        Plot.show(new tech.tablesaw.plotly.components.Figure(layout, trace1, trace2));
    }
}

