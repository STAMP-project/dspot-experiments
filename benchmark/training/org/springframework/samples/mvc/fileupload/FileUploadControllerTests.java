package org.springframework.samples.mvc.fileupload;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.samples.mvc.AbstractContextControllerTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
public class FileUploadControllerTests extends AbstractContextControllerTests {
    @Test
    public void readString() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "orig", null, "bar".getBytes());
        webAppContextSetup(this.wac).build().perform(fileUpload("/fileupload").file(file)).andExpect(model().attribute("message", "File 'orig' uploaded successfully"));
    }
}

