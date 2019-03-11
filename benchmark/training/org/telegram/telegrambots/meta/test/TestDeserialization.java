package org.telegram.telegrambots.meta.test;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ApiResponse;


/**
 *
 *
 * @author Ruben Bermudez
 * @version 1.0
 */
public class TestDeserialization {
    private ObjectMapper mapper;

    @Test
    public void TestUpdateDeserialization() throws Exception {
        Update update = mapper.readValue(TelegramBotsHelper.GetUpdate(), Update.class);
        assertUpdate(update);
    }

    @Test
    public void TestResponseWithoutErrorDeserialization() throws IOException {
        ApiResponse<ArrayList<Update>> result = mapper.readValue(TelegramBotsHelper.GetResponseWithoutError(), new TypeReference<ApiResponse<ArrayList<Update>>>() {});
        Assert.assertNotNull(result);
        Assert.assertTrue(result.getOk());
        Assert.assertEquals(1, result.getResult().size());
        assertUpdate(result.getResult().get(0));
    }

    @Test
    public void TestResponseWithErrorDeserialization() throws IOException {
        ApiResponse<ArrayList<Update>> result = mapper.readValue(TelegramBotsHelper.GetResponseWithError(), new TypeReference<ApiResponse<ArrayList<Update>>>() {});
        Assert.assertNotNull(result);
        Assert.assertFalse(result.getOk());
        Assert.assertEquals(Integer.valueOf(400), result.getErrorCode());
        Assert.assertEquals("Error descriptions", result.getErrorDescription());
        Assert.assertNotNull(result.getParameters());
        Assert.assertEquals(Long.valueOf(12345), result.getParameters().getMigrateToChatId());
        Assert.assertEquals(Integer.valueOf(12), result.getParameters().getRetryAfter());
    }
}

