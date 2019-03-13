package org.javaee7.jaxrs.fileupload;


import MediaType.APPLICATION_OCTET_STREAM;
import Status.UNSUPPORTED_MEDIA_TYPE;
import java.io.File;
import java.net.URL;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Arun Gupta
 * @author Xavier Coulon
 */
@RunWith(Arquillian.class)
public class MyResourceTest {
    private static WebTarget target;

    private static File tempFile;

    @ArquillianResource
    private URL base;

    @Test
    public void shouldPostOctetStreamContentAsInputStream() {
        // when
        Long uploadedFileSize = MyResourceTest.target.path("/upload").request().post(Entity.entity(MyResourceTest.tempFile, APPLICATION_OCTET_STREAM), Long.class);
        // then
        assertThat(uploadedFileSize).isEqualTo(1000);
    }

    @Test
    public void shouldNotPostImagePngContentAsInputStream() {
        // when
        final Response response = MyResourceTest.target.path("/upload").request().post(Entity.entity(MyResourceTest.tempFile, "image/png"));
        // then
        assertThat(response.getStatus()).isEqualTo(UNSUPPORTED_MEDIA_TYPE.getStatusCode());
    }

    @Test
    public void shouldPostOctetStreamContentAsFile() {
        // when
        Long uploadedFileSize = MyResourceTest.target.path("/upload2").request().post(Entity.entity(MyResourceTest.tempFile, APPLICATION_OCTET_STREAM), Long.class);
        // then
        assertThat(uploadedFileSize).isEqualTo(1000);
    }

    @Test
    public void shouldPostImagePngContentAsFile() {
        // when
        Long uploadedFileSize = MyResourceTest.target.path("/upload2").request().post(Entity.entity(MyResourceTest.tempFile, "image/png"), Long.class);
        // then
        assertThat(uploadedFileSize).isEqualTo(1000);
    }
}

