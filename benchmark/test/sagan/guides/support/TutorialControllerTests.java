package sagan.guides.support;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import sagan.guides.GuideMetadata;


public class TutorialControllerTests {
    @Mock
    private Tutorials guides;

    private GuideMetadata guide;

    private TutorialController controller;

    @Test
    public void loadImage() {
        byte[] image = "animage".getBytes();
        BDDMockito.given(guides.findMetadata(ArgumentMatchers.anyString())).willReturn(guide);
        BDDMockito.given(guides.loadImage(guide, "welcome.png")).willReturn(image);
        ResponseEntity<byte[]> responseEntity = controller.loadImage("rest", "welcome.png");
        MatcherAssert.assertThat(responseEntity.getBody(), Matchers.is(image));
    }
}

