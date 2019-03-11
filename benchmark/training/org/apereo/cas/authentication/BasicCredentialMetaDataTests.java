package org.apereo.cas.authentication;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.authentication.metadata.BasicCredentialMetaData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Scott Battaglia
 * @since 3.0
 */
public class BasicCredentialMetaDataTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "basicCredentialMetaData.json");

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    @Test
    @SneakyThrows
    public void verifySerializeABasicCredentialMetaDataToJson() {
        val credentialMetaDataWritten = new BasicCredentialMetaData(new UsernamePasswordCredential());
        BasicCredentialMetaDataTests.MAPPER.writeValue(BasicCredentialMetaDataTests.JSON_FILE, credentialMetaDataWritten);
        val credentialMetaDataRead = BasicCredentialMetaDataTests.MAPPER.readValue(BasicCredentialMetaDataTests.JSON_FILE, BasicCredentialMetaData.class);
        Assertions.assertEquals(credentialMetaDataWritten, credentialMetaDataRead);
    }
}

