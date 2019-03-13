package io.dropwizard.util;


import java.util.Optional;
import org.junit.jupiter.api.Test;


public class JarLocationTest {
    @Test
    public void isHumanReadable() throws Exception {
        assertThat(new JarLocation(JarLocationTest.class).toString()).isEqualTo("project.jar");
    }

    @Test
    public void hasAVersion() throws Exception {
        assertThat(new JarLocation(JarLocationTest.class).getVersion()).isEqualTo(Optional.empty());
    }
}

