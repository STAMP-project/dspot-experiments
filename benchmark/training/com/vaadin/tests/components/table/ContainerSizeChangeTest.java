package com.vaadin.tests.components.table;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;


public class ContainerSizeChangeTest extends MultiBrowserTest {
    @Test
    public void tableShouldLoadCorrectItems() throws IOException, InterruptedException {
        openTestURL();
        ButtonElement decreaseSize = $(ButtonElement.class).caption("Decrease size").first();
        decreaseSize.click();// decreasing container size from 50 to 40

        decreaseSize.click();// decreasing container size from 40 to 30

        TableElement table = $(TableElement.class).first();
        // TableElement scroll not working properly, so we need to do this.
        // http://dev.vaadin.com/ticket/13826
        testBenchElement(table.findElement(By.className("v-scrollable"))).scroll(1000);
        // waitforvaadin not worky currently for table scroll, so we need to use
        // thread sleep :(
        Thread.sleep(1500);
        assertThatRowExists(table, 29);
        assertRowDoesNotExist(table, 30);
    }
}

