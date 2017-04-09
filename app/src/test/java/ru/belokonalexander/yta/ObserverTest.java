package ru.belokonalexander.yta;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class ObserverTest {

    @Test
    public void observerShouldBeDisposed()  {
        TestObserver observer = new TestObserver();
        Observable.just("").subscribe(observer);
        observer.awaitTerminalEvent();

        observer.assertNoErrors();
        observer.assertComplete();
        assertThat(observer.isDisposed(), is(true));
    }

}
