package de.zalando.ep.zalenium.registry;


import de.zalando.ep.zalenium.util.ZaleniumConfiguration;
import org.junit.Test;
import org.openqa.grid.internal.utils.configuration.GridHubConfiguration;
import org.openqa.grid.web.Hub;


public class ZaleniumRunningLocallyTest {
    /* Uncomment the two bottom lines to run Zalenium in development mode.
    Useful for implementing new features or debugging issues.
     */
    @Test
    public void runLocally() {
        GridHubConfiguration gridHubConfiguration = new GridHubConfiguration();
        gridHubConfiguration.registry = ZaleniumRegistry.class.getCanonicalName();
        gridHubConfiguration.port = 4445;
        ZaleniumConfiguration.setDesiredContainersOnStartup(0);
        ZaleniumConfiguration.setMaxDockerSeleniumContainers(30);
        Hub hub = new Hub(gridHubConfiguration);
        /* hub.start();
        try {
        Thread.sleep(1000 * 60 * 60);
        } catch (InterruptedException e) {
        e.printStackTrace();
        }
         */
    }
}

