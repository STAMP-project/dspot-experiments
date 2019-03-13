package com.orientechnologies.orient.server;


import java.util.concurrent.TimeUnit;
import org.junit.Test;


/**
 * Created by frank on 19/11/2015.
 */
public class OServerShutdownMainTest {
    private OServer server;

    private boolean allowJvmShutdownPrev;

    private String prevPassword;

    private String prevOrientHome;

    @Test
    public void shouldShutdownServerWithDirectCall() throws Exception {
        OServerShutdownMain shutdownMain = new OServerShutdownMain("localhost", "2424", "root", "rootPassword");
        shutdownMain.connect(5000);
        TimeUnit.SECONDS.sleep(2);
        assertThat(server.isActive()).isFalse();
    }

    @Test
    public void shouldShutdownServerParsingShortArguments() throws Exception {
        OServerShutdownMain.main(new String[]{ "-h", "localhost", "-P", "2424", "-p", "rootPassword", "-u", "root" });
        TimeUnit.SECONDS.sleep(2);
        assertThat(server.isActive()).isFalse();
    }

    @Test
    public void shouldShutdownServerParsingLongArguments() throws Exception {
        OServerShutdownMain.main(new String[]{ "--host", "localhost", "--ports", "2424", "--password", "rootPassword", "--user", "root" });
        TimeUnit.SECONDS.sleep(2);
        assertThat(server.isActive()).isFalse();
    }
}

