package com.vaadin.v7.tests.design;


import com.vaadin.ui.declarative.DesignContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.junit.Test;


/**
 * Parse and write a legacy design (using the "v-" prefix).
 */
public class WriteLegacyDesignTest {
    // The context is used for accessing the created component hierarchy.
    private DesignContext ctx;

    @Test
    public void designIsSerializedWithCorrectPrefixesAndPackageNames() throws IOException {
        ByteArrayOutputStream out = serializeDesign(ctx);
        Document doc = Jsoup.parse(out.toString("UTF-8"));
        for (Node child : doc.body().childNodes()) {
            checkNode(child);
        }
    }
}

