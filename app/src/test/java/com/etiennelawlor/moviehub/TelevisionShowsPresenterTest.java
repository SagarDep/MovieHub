package com.etiennelawlor.moviehub;

import com.etiennelawlor.moviehub.data.network.response.TelevisionShow;
import com.etiennelawlor.moviehub.data.repositories.tv.models.TelevisionShowsPage;
import com.etiennelawlor.moviehub.domain.TelevisionShowsDomainContract;
import com.etiennelawlor.moviehub.presentation.televisionshows.TelevisionShowsPresenter;
import com.etiennelawlor.moviehub.presentation.televisionshows.TelevisionShowsUiContract;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rx.Subscriber;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Created by etiennelawlor on 2/9/17.
 */

public class TelevisionShowsPresenterTest {

    // region Test Doubles

    // Mocks
    @Mock
    private TelevisionShowsUiContract.View mockTelevisionShowsView;
    @Mock
    private TelevisionShowsDomainContract.UseCase mockTelevisionShowsUseCase;

    // Stubs
    private ArgumentCaptor<Subscriber> subscriberArgumentCaptor;
    // endregion

    // region Member Variables
    private TelevisionShowsPage televisionShowsPage;
    private TelevisionShowsPresenter televisionShowsPresenter;
    // endregion

    @Before
    public void setUp() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        televisionShowsPresenter = new TelevisionShowsPresenter(mockTelevisionShowsView, mockTelevisionShowsUseCase);
    }

    // region Test Methods
//    @Test(expected = IOException.class)
    @Test
    public void onLoadPopularTelevisionShows_shouldShowError_whenFirstPageRequestFailed() {
        // 1. (Given) Set up conditions required for the test
        televisionShowsPage = new TelevisionShowsPage(getListOfTelevisionShows(0), 1, true, Calendar.getInstance().getTime());

        // 2. (When) Then perform one or more actions
        televisionShowsPresenter.onLoadPopularTelevisionShows(televisionShowsPage.getPageNumber());

        // 3. (Then) Afterwards, verify that the state you are expecting is actually achieved
        verify(mockTelevisionShowsView).hideEmptyView();
        verify(mockTelevisionShowsView).hideErrorView();
        verify(mockTelevisionShowsView).showLoadingView();

        subscriberArgumentCaptor = ArgumentCaptor.forClass(Subscriber.class);
        verify(mockTelevisionShowsUseCase).getPopularTelevisionShows(anyInt(), subscriberArgumentCaptor.capture());
        subscriberArgumentCaptor.getValue().onError(new UnknownHostException());

        verify(mockTelevisionShowsView).hideLoadingView();
        verify(mockTelevisionShowsView).setErrorText(anyString());
        verify(mockTelevisionShowsView).showErrorView();
    }

    @Test
    public void onLoadPopularTelevisionShows_shouldShowError_whenNextPageRequestFailed() {
        // 1. (Given) Set up conditions required for the test
        televisionShowsPage = new TelevisionShowsPage(getListOfTelevisionShows(0), 2, true, Calendar.getInstance().getTime());

        // 2. (When) Then perform one or more actions
        televisionShowsPresenter.onLoadPopularTelevisionShows(televisionShowsPage.getPageNumber());

        // 3. (Then) Afterwards, verify that the state you are expecting is actually achieved
        verify(mockTelevisionShowsView).showLoadingFooter();

        subscriberArgumentCaptor = ArgumentCaptor.forClass(Subscriber.class);
        verify(mockTelevisionShowsUseCase).getPopularTelevisionShows(anyInt(), subscriberArgumentCaptor.capture());
        subscriberArgumentCaptor.getValue().onError(new UnknownHostException());

        verify(mockTelevisionShowsView).showErrorFooter();
    }

    @Test
    public void onLoadPopularTelevisionShowss_shouldShowEmpty_whenFirstPageHasNoTelevisionShows() {
        // 1. (Given) Set up conditions required for the test
        televisionShowsPage = new TelevisionShowsPage(getListOfTelevisionShows(0), 1, true, Calendar.getInstance().getTime());

        // 2. (When) Then perform one or more actions
        televisionShowsPresenter.onLoadPopularTelevisionShows(televisionShowsPage.getPageNumber());

        // 3. (Then) Afterwards, verify that the state you are expecting is actually achieved
        verify(mockTelevisionShowsView).hideEmptyView();
        verify(mockTelevisionShowsView).hideErrorView();
        verify(mockTelevisionShowsView).showLoadingView();

        subscriberArgumentCaptor = ArgumentCaptor.forClass(Subscriber.class);
        verify(mockTelevisionShowsUseCase).getPopularTelevisionShows(anyInt(), subscriberArgumentCaptor.capture());
        subscriberArgumentCaptor.getValue().onNext(televisionShowsPage);

        verify(mockTelevisionShowsView).hideLoadingView();
        verify(mockTelevisionShowsView).showEmptyView();
        verify(mockTelevisionShowsView).setTelevisionShowsPage(televisionShowsPage);
    }

    @Test
    public void onLoadPopularTelevisionShows_shouldNotAddTelevisionShows_whenNextPageHasNoTelevisionShows() {
        // 1. (Given) Set up conditions required for the test
        televisionShowsPage = new TelevisionShowsPage(getListOfTelevisionShows(0), 2, true, Calendar.getInstance().getTime());

        // 2. (When) Then perform one or more actions
        televisionShowsPresenter.onLoadPopularTelevisionShows(televisionShowsPage.getPageNumber());

        // 3. (Then) Afterwards, verify that the state you are expecting is actually achieved
        verify(mockTelevisionShowsView).showLoadingFooter();

        subscriberArgumentCaptor = ArgumentCaptor.forClass(Subscriber.class);
        verify(mockTelevisionShowsUseCase).getPopularTelevisionShows(anyInt(), subscriberArgumentCaptor.capture());
        subscriberArgumentCaptor.getValue().onNext(televisionShowsPage);

        verify(mockTelevisionShowsView).removeFooter();
        verify(mockTelevisionShowsView).setTelevisionShowsPage(televisionShowsPage);
    }

    @Test
    public void onLoadPopularTelevisionShows_shouldAddTelevisionShows_whenFirstPageHasTelevisionShowsAndIsLastPage() {
        // 1. (Given) Set up conditions required for the test
        televisionShowsPage = new TelevisionShowsPage(getListOfTelevisionShows(5), 1, true, Calendar.getInstance().getTime());

        // 2. (When) Then perform one or more actions
        televisionShowsPresenter.onLoadPopularTelevisionShows(televisionShowsPage.getPageNumber());

        // 3. (Then) Afterwards, verify that the state you are expecting is actually achieved
        verify(mockTelevisionShowsView).hideEmptyView();
        verify(mockTelevisionShowsView).hideErrorView();
        verify(mockTelevisionShowsView).showLoadingView();

        subscriberArgumentCaptor = ArgumentCaptor.forClass(Subscriber.class);
        verify(mockTelevisionShowsUseCase).getPopularTelevisionShows(anyInt(), subscriberArgumentCaptor.capture());
        subscriberArgumentCaptor.getValue().onNext(televisionShowsPage);

        verify(mockTelevisionShowsView).hideLoadingView();
        verify(mockTelevisionShowsView).addHeader();
        verify(mockTelevisionShowsView).addTelevisionShowsToAdapter(televisionShowsPage.getTelevisionShows());
        verify(mockTelevisionShowsView).setTelevisionShowsPage(televisionShowsPage);
    }

    @Test
    public void onLoadPopularTelevisionShows_shouldAddTelevisionShows_whenFirstPageHasTelevisionShowsAndIsNotLastPage() {
        // 1. (Given) Set up conditions required for the test
        televisionShowsPage = new TelevisionShowsPage(getListOfTelevisionShows(5), 1, false, Calendar.getInstance().getTime());

        // 2. (When) Then perform one or more actions
        televisionShowsPresenter.onLoadPopularTelevisionShows(televisionShowsPage.getPageNumber());

        // 3. (Then) Afterwards, verify that the state you are expecting is actually achieved
        verify(mockTelevisionShowsView).hideEmptyView();
        verify(mockTelevisionShowsView).hideErrorView();
        verify(mockTelevisionShowsView).showLoadingView();

        subscriberArgumentCaptor = ArgumentCaptor.forClass(Subscriber.class);
        verify(mockTelevisionShowsUseCase).getPopularTelevisionShows(anyInt(), subscriberArgumentCaptor.capture());
        subscriberArgumentCaptor.getValue().onNext(televisionShowsPage);

        verify(mockTelevisionShowsView).hideLoadingView();
        verify(mockTelevisionShowsView).addHeader();
        verify(mockTelevisionShowsView).addTelevisionShowsToAdapter(televisionShowsPage.getTelevisionShows());
        verify(mockTelevisionShowsView).addFooter();
        verify(mockTelevisionShowsView).setTelevisionShowsPage(televisionShowsPage);
    }

    @Test
    public void onLoadPopularTelevisionShows_shouldAddTelevisionShows_whenNextPageHasTelevisionShowsAndIsLastPage() {
        // 1. (Given) Set up conditions required for the test
        televisionShowsPage = new TelevisionShowsPage(getListOfTelevisionShows(5), 2, true, Calendar.getInstance().getTime());

        // 2. (When) Then perform one or more actions
        televisionShowsPresenter.onLoadPopularTelevisionShows(televisionShowsPage.getPageNumber());

        // 3. (Then) Afterwards, verify that the state you are expecting is actually achieved
        verify(mockTelevisionShowsView).showLoadingFooter();

        subscriberArgumentCaptor = ArgumentCaptor.forClass(Subscriber.class);
        verify(mockTelevisionShowsUseCase).getPopularTelevisionShows(anyInt(), subscriberArgumentCaptor.capture());
        subscriberArgumentCaptor.getValue().onNext(televisionShowsPage);

        verify(mockTelevisionShowsView).removeFooter();
        verify(mockTelevisionShowsView).addTelevisionShowsToAdapter(televisionShowsPage.getTelevisionShows());
        verify(mockTelevisionShowsView).setTelevisionShowsPage(televisionShowsPage);
    }

    @Test
    public void onLoadPopularTelevisionShows_shouldAddTelevisionShows_whenNextPageHasTelevisionShowsAndIsNotLastPage() {
        // 1. (Given) Set up conditions required for the test
        televisionShowsPage = new TelevisionShowsPage(getListOfTelevisionShows(5), 2, false, Calendar.getInstance().getTime());

        // 2. (When) Then perform one or more actions
        televisionShowsPresenter.onLoadPopularTelevisionShows(televisionShowsPage.getPageNumber());

        // 3. (Then) Afterwards, verify that the state you are expecting is actually achieved
        verify(mockTelevisionShowsView).showLoadingFooter();

        subscriberArgumentCaptor = ArgumentCaptor.forClass(Subscriber.class);
        verify(mockTelevisionShowsUseCase).getPopularTelevisionShows(anyInt(), subscriberArgumentCaptor.capture());
        subscriberArgumentCaptor.getValue().onNext(televisionShowsPage);

        verify(mockTelevisionShowsView).removeFooter();
        verify(mockTelevisionShowsView).addTelevisionShowsToAdapter(televisionShowsPage.getTelevisionShows());
        verify(mockTelevisionShowsView).addFooter();
        verify(mockTelevisionShowsView).setTelevisionShowsPage(televisionShowsPage);
//        verify(mockTelevisionShowsView, times(1)).setModel(any(TelevisionShowsWrapper.class)); // Alternative verify check
    }

    @Test
    public void onTelevisionShowClick_shouldOpenTelevisionShowDetails() {
        // 1. (Given) Set up conditions required for the test
        TelevisionShow televisionShow = new TelevisionShow();

        // 2. (When) Then perform one or more actions
        televisionShowsPresenter.onTelevisionShowClick(televisionShow);

        // 3. (Then) Afterwards, verify that the state you are expecting is actually achieved
        verify(mockTelevisionShowsView).openTelevisionShowDetails(televisionShow);

        verifyZeroInteractions(mockTelevisionShowsUseCase);
    }

    @Test
    public void onScrollToEndOfList_shouldLoadMoreItems() {
        // 1. (Given) Set up conditions required for the test

        // 2. (When) Then perform one or more actions
        televisionShowsPresenter.onScrollToEndOfList();

        // 3. (Then) Afterwards, verify that the state you are expecting is actually achieved
        verify(mockTelevisionShowsView).loadMoreItems();

        verifyZeroInteractions(mockTelevisionShowsUseCase);
    }

    @Test
    public void onDestroyView_shouldClearSubscriptions() {
        // 1. (Given) Set up conditions required for the test

        // 2. (When) Then perform one or more actions
        televisionShowsPresenter.onDestroyView();
        // 3. (Then) Afterwards, verify that the state you are expecting is actually achieved
        verifyZeroInteractions(mockTelevisionShowsView);
        verify(mockTelevisionShowsUseCase).clearSubscriptions();
    }

    // endregion

    // region Helper Methods
    private List<TelevisionShow> getListOfTelevisionShows(int numOfTelevisionShows){
        List<TelevisionShow> televisionShows = new ArrayList<>();
        for(int i=0; i<numOfTelevisionShows; i++){
            TelevisionShow televisionShow = new TelevisionShow();
            televisionShows.add(televisionShow);
        }
        return televisionShows;
    }
    // endregion
}
