

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import saulmm.avengers.CharacterDetailsUsecase;
import saulmm.avengers.mvp.presenters.CharacterDetailPresenter;
import saulmm.avengers.mvp.views.CharacterDetailView;


public class DetailPresenterTest {
    @Mock
    CharacterDetailView mockDetailView;

    @Mock
    CharacterDetailsUsecase mockDetailsUsecase;

    final int FAKE_CHARACTER_ID = 0;

    @Test
    public void testThatPresenterAsksForCharacterDetails() throws Exception {
        CharacterDetailPresenter characterListPresenter = givenACharacterDetailPresenter();
        Mockito.when(mockDetailsUsecase.execute()).thenReturn(getFakeObservableCharacter());
        characterListPresenter.askForCharacterDetails();
        Mockito.verify(mockDetailsUsecase, Mockito.times(1)).execute();
    }

    @Test
    public void testThatPresentersOpensComicsView() throws Exception {
        CharacterDetailPresenter characterDetailPresenter = givenACharacterDetailPresenter();
        characterDetailPresenter.onComicsIndicatorPressed();
        Mockito.verify(mockDetailView, Mockito.times(1)).goToCharacterComicsView(FAKE_CHARACTER_ID);
    }

    @Test
    public void testThatPresentersOpensSeriesView() throws Exception {
        CharacterDetailPresenter characterDetailPresenter = givenACharacterDetailPresenter();
        characterDetailPresenter.onSeriesIndicatorPressed();
        Mockito.verify(mockDetailView, Mockito.times(1)).goToCharacterSeriesView(FAKE_CHARACTER_ID);
    }

    @Test
    public void testThatPresentersOpensStoriesView() throws Exception {
        CharacterDetailPresenter characterDetailPresenter = givenACharacterDetailPresenter();
        characterDetailPresenter.onStoriesIndicatorPressed();
        Mockito.verify(mockDetailView, Mockito.times(1)).goToCharacterStoriesView(FAKE_CHARACTER_ID);
    }

    @Test
    public void testThatPresentersOpensEventsView() throws Exception {
        CharacterDetailPresenter characterDetailPresenter = givenACharacterDetailPresenter();
        characterDetailPresenter.onEventIndicatorPressed();
        Mockito.verify(mockDetailView, Mockito.times(1)).goToCharacterEventsView(FAKE_CHARACTER_ID);
    }
}

