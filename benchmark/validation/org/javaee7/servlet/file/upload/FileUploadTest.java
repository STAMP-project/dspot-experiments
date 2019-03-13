package org.javaee7.servlet.file.upload;


import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Jakub Marchwicki
 */
@RunWith(Arquillian.class)
public class FileUploadTest {
    @ArquillianResource
    private URL base;

    @Test
    public void uploadFile() throws IOException, URISyntaxException {
        // HttpClient client = new DefaultHttpClient();
        // HttpPost postRequest = new HttpPost(new URL(base, "TestServlet").toURI());
        // 
        // MultipartEntity multiPartEntity = new MultipartEntity();
        // FileBody fileBody = new FileBody(new File("pom.xml"));
        // multiPartEntity.addPart("attachment", fileBody);
        // 
        // postRequest.setEntity(multiPartEntity);
        // HttpResponse response = client.execute(postRequest);
        // 
        // String servletOutput = EntityUtils.toString(response.getEntity());
        // 
        // assertThat(response.getStatusLine().getStatusCode(), is(equalTo(200)));
        // assertThat(servletOutput, containsString("Received 1 parts"));
        // assertThat(servletOutput, containsString("writing pom.xml part"));
        // assertThat(servletOutput, containsString("uploaded to: /tmp/pom.xml"));
    }
}

