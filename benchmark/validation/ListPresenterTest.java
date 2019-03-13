

import java.util.ArrayList;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import rx.Observable;
import saulmm.avengers.GetCharactersUsecase;
import saulmm.avengers.entities.MarvelCharacter;
import saulmm.avengers.mvp.presenters.CharacterListPresenter;
import saulmm.avengers.mvp.views.CharacterListView;


public class ListPresenterTest {
    @Mock
    CharacterListView mockCharacterListView;

    @Mock
    GetCharactersUsecase mockGetCharacterUsecase;

    @Test
    public void testThatCharactersArePassedToTheView() throws Exception {
        CharacterListPresenter listPresenter = givenAListPresenter();
        ArrayList<MarvelCharacter> fakeCharacterList = givenAFakeCharacterList();
        listPresenter.onCharactersReceived(fakeCharacterList);
        Mockito.verify(mockCharacterListView, Mockito.times(1)).bindCharacterList(fakeCharacterList);
    }

    @Test
    public void testThatPresenterRequestCharacters() throws Exception {
        CharacterListPresenter listPresenter = givenAListPresenter();
        Mockito.when(mockGetCharacterUsecase.execute()).thenReturn(getFakeObservableCharacterList());
        listPresenter.askForCharacters();
        Mockito.verify(mockGetCharacterUsecase, Mockito.times(1)).execute();
    }

    @Test
    public void testThatPresenterShowsErrorWhenLoadingCharacters() throws Exception {
        CharacterListPresenter listPresenter = givenAListPresenter();
        Mockito.when(mockGetCharacterUsecase.execute()).thenReturn(Observable.error(new Exception()));
        listPresenter.askForCharacters();
        Mockito.verify(mockCharacterListView, Mockito.times(1)).showUknownErrorMessage();
    }

    @Test
    public void testThatPresenterShowsALightErrorLoadingMoreCharacters() throws Exception {
        CharacterListPresenter listPresenter = givenAListPresenter();
        Mockito.when(mockGetCharacterUsecase.execute()).thenReturn(Observable.error(new Exception()));
        listPresenter.askForNewCharacters();
        Mockito.verify(mockCharacterListView, Mockito.times(1)).showLightError();
    }

    @Test
    public void testThatPresenterRequestMoreCharacters() throws Exception {
        CharacterListPresenter listPresenter = givenAListPresenter();
        Mockito.when(mockGetCharacterUsecase.execute()).thenReturn(getFakeObservableCharacterList());
        listPresenter.askForNewCharacters();
        Mockito.verify(mockGetCharacterUsecase, Mockito.only()).execute();
    }
}

