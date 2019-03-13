package sagan.guides.support;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.client.RestClientException;
import sagan.guides.GettingStartedGuide;
import sagan.projects.support.ProjectMetadataService;


/**
 * Unit tests for {@link GettingStartedGuideController}.
 */
public class GettingStartedGuideControllerTests {
    @Mock
    private GettingStartedGuides guides;

    @Mock
    private ProjectMetadataService projectMetadataService;

    private GettingStartedGuide guide;

    private ExtendedModelMap model;

    private GettingStartedGuideController controller;

    @Test
    public void viewGuide() {
        BDDMockito.given(guides.find("rest-service")).willReturn(guide);
        String view = controller.viewGuide("rest-service", model);
        MatcherAssert.assertThat(view, Matchers.is("guides/gs/guide"));
    }

    @Test
    public void guideIsInModel() {
        BDDMockito.given(guides.find("rest-service")).willReturn(guide);
        controller.viewGuide("rest-service", model);
        MatcherAssert.assertThat(((GettingStartedGuide) (model.get("guide"))), Matchers.is(guide));
    }

    @Test(expected = RestClientException.class)
    public void viewGuide_fails() {
        BDDMockito.given(guides.find("rest-service")).willThrow(new RestClientException("Is GitHub down?"));
        controller.viewGuide("rest-service", model);
    }

    @Test
    public void loadImage() {
        byte[] image = "animage".getBytes();
        BDDMockito.given(guides.find(ArgumentMatchers.anyString())).willReturn(guide);
        BDDMockito.given(guides.loadImage(guide, "welcome.png")).willReturn(image);
        ResponseEntity<byte[]> responseEntity = controller.loadImage("rest-service", "welcome.png");
        MatcherAssert.assertThat(responseEntity.getBody(), Matchers.is(image));
    }
}

