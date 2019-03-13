package tech.tablesaw.components;


import Symbol.DIAMOND_TALL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.tablesaw.plotly.components.Marker;


public class MarkerTest {
    @Test
    public void asJavascript() {
        Marker x = Marker.builder().size(12.0).symbol(DIAMOND_TALL).color("#c68486").build();
        Assertions.assertTrue(x.asJavascript().contains("color"));
        Assertions.assertTrue(x.asJavascript().contains("symbol"));
        Assertions.assertTrue(x.asJavascript().contains("size"));
    }
}

