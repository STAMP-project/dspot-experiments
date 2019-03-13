package com.orientechnologies.orient.server;


import com.orientechnologies.common.exception.OException;
import com.orientechnologies.orient.server.config.OServerConfiguration;
import org.junit.Test;


/**
 * Created by frank on 21/01/2016.
 */
public class OServerTest {
    private String prevPassword;

    private String prevOrientHome;

    private boolean allowJvmShutdownPrev;

    private OServer server;

    private OServerConfiguration conf;

    @Test
    public void shouldShutdownOnPluginStartupException() {
        try {
            server = new OServer(false);
            server.startup(conf);
            server.activate();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(OException.class);
        }
        assertThat(server.isActive()).isFalse();
        server.shutdown();
    }
}

