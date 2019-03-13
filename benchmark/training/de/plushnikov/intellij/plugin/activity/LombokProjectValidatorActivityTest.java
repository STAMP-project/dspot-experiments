package de.plushnikov.intellij.plugin.activity;


import com.intellij.openapi.roots.OrderEntry;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class LombokProjectValidatorActivityTest {
    private LombokProjectValidatorActivity component;

    private OrderEntry orderEntry;

    @Test
    public void parseLombokVersionFromGradle() {
        Mockito.when(orderEntry.getPresentableName()).thenReturn("Gradle: org.projectlombok:lombok:1.16.8");
        Assert.assertEquals("1.16.8", component.parseLombokVersion(orderEntry));
    }

    @Test
    public void parseLombokVersionFromMaven() {
        Mockito.when(orderEntry.getPresentableName()).thenReturn("Maven: org.projectlombok:lombok:1.16.6");
        Assert.assertEquals("1.16.6", component.parseLombokVersion(orderEntry));
    }

    @Test
    public void parseLombokVersionFromUnknown() {
        Mockito.when(orderEntry.getPresentableName()).thenReturn("lombok");
        Assert.assertNull(component.parseLombokVersion(orderEntry));
    }

    @Test
    public void compareVersionString1_2() {
        Assert.assertEquals((-1), component.compareVersionString("1", "2"));
    }

    @Test
    public void compareVersionString__2() {
        Assert.assertEquals((-1), component.compareVersionString("", "2"));
    }

    @Test
    public void compareVersionString123_121() {
        Assert.assertEquals(1, component.compareVersionString("1.2.3", "1.2.1"));
    }

    @Test
    public void compareVersionString1166_1168() {
        Assert.assertEquals((-1), component.compareVersionString("1.16.6", "1.16.8"));
    }

    @Test
    public void compareVersionString1168_1168() {
        Assert.assertEquals(0, component.compareVersionString("1.16.8", "1.16.8"));
    }

    @Test
    public void compareVersionString0102_1168() {
        Assert.assertEquals((-1), component.compareVersionString("0.10.2", "1.16.8"));
    }

    @Test
    public void compareVersionString1169_1168() {
        Assert.assertEquals(1, component.compareVersionString("1.16.9", "1.16.8"));
    }
}

