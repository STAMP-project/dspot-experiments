package tech.tablesaw.components;


import java.io.File;
import java.net.URL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.tablesaw.plotly.components.TemplateUtils;


public class TemplateUtilsTest {
    private double[] x = new double[]{ 1, 2, 3, 4, 5 };

    private double[] y = new double[]{ 1, 4, 9, 16, 25 };

    private String customTemplateString = "<!-- Custom page_template.html -->";

    @Test
    public void testDefaultTemplateLocation() {
        TemplateUtils.setTemplateLocations();
        String html = createPageHtml();
        Assertions.assertTrue(((html.indexOf(customTemplateString)) < 0));
    }

    @Test
    public void testCustomTemplateLocation() {
        URL url = this.getClass().getResource(((this.getClass().getSimpleName()) + ".class"));
        Assertions.assertNotNull(url, "Couldn't locate class (as resource), where template is also found");
        String path = url.getPath();
        Assertions.assertTrue(((path.lastIndexOf('/')) >= 0));
        String folderPath = path.substring(0, path.lastIndexOf('/'));
        File templateFile = new File((folderPath + "/page_template.html"));
        Assertions.assertTrue(templateFile.exists(), (templateFile + " doesn't exist"));
        TemplateUtils.setTemplateLocations(folderPath);
        String html = createPageHtml();
        Assertions.assertTrue(((html.indexOf(customTemplateString)) >= 0));
    }
}

