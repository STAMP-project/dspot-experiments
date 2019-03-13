

import com.hackvg.domain.GetMovieDetailUsecase;
import com.hackvg.model.MediaDataSource;
import com.hackvg.model.entities.MovieDetail;
import com.squareup.otto.Bus;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class GetMovieDetailsUsecaseTest {
    // Class under test
    private GetMovieDetailUsecase mGetMovieDetailUsecase;

    private String movieTestId = "32";

    @Mock
    private Bus mockUiBus;

    @Mock
    private MediaDataSource mockDataSource;

    @Test
    public void testGetMovieDetailRequestExecution() {
        mGetMovieDetailUsecase.execute();
        Mockito.verify(mockDataSource, Mockito.times(1)).getDetailMovie(movieTestId);
    }

    @Test
    public void testConfigurationPost() {
        mGetMovieDetailUsecase.onMovieDetailResponse(new MovieDetail());
        Mockito.verify(mockUiBus, Mockito.times(1)).post(ArgumentMatchers.any(MovieDetail.class));
    }
}

