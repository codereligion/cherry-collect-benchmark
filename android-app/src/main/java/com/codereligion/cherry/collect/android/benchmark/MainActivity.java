package com.codereligion.cherry.collect.android.benchmark;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import com.codereligion.cherry.benchmark.Input;
import com.codereligion.cherry.benchmark.Output;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import static com.codereligion.cherry.benchmark.BenchmarkRunner.benchMark;
import static com.codereligion.cherry.benchmark.BenchmarkRunner.warmUp;
import static com.codereligion.cherry.benchmark.collect.ListFilteringAndTransformationBenchmarkInputFactory.createListFilteringAndTransformingBenchmarkInput;
import static com.codereligion.cherry.benchmark.collect.ListFilteringBenchmarkInputFactory.createListFilteringBenchmarkInput;
import static rx.android.schedulers.AndroidSchedulers.mainThread;
import static rx.schedulers.Schedulers.newThread;


public class MainActivity extends ActionBarActivity {

    private ProgressBar progressBar;
    private ResultListFragment resultListFragment;
    private Observable.Operator<Output, Output> progressObserver = new Observable.Operator<Output, Output>() {
        @Override
        public Subscriber<? super Output> call(final Subscriber<? super Output> subscriber) {
            return new Subscriber<Output>() {

                @Override
                public void onStart() {
                    subscriber.onStart();
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCompleted() {
                    subscriber.onCompleted();
                    progressBar.setVisibility(View.INVISIBLE);
                    subscription = null;
                    observable = null;
                }

                @Override
                public void onError(final Throwable e) {
                    subscriber.onError(e);
                }

                @Override
                public void onNext(final Output output) {
                    subscriber.onNext(output);
                }
            };
        }
    };

    private Subscription subscription;
    private Observable<Output> observable;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        progressBar = (ProgressBar) findViewById(R.id.progress_spinner);
        resultListFragment = (ResultListFragment) getSupportFragmentManager().findFragmentById(R.id.result_list_fragment);
        recoverLastObservable();
    }

    private void recoverLastObservable() {
        observable = getLastCustomNonConfigurationInstance();
        if (observable != null) {
            observable.lift(progressObserver).subscribe();
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.list_actions, menu);
        return true;
    }

    @Override
    public Observable<Output> getLastCustomNonConfigurationInstance() {
        return (Observable<Output>) super.getLastCustomNonConfigurationInstance();
    }

    @Override
    public Observable<Output> onRetainCustomNonConfigurationInstance() {
        return observable;
    }

    @Override
    protected void onDestroy() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        final Observable<Input> inputObservable;

        switch (item.getItemId()) {
            case R.id.filterToArrayList: {
                inputObservable = createListFilteringBenchmarkInput();
                break;
            }
            case R.id.filterAndTransformToArrayList: {
                inputObservable = createListFilteringAndTransformingBenchmarkInput();
                break;
            }
            case R.id.shareResults: {
                final Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, resultListFragment.getResultsAsCsv());
                sendIntent.setType("text/csv");
                startActivity(sendIntent);
                return true;
            }
            default: {
                throw new IllegalArgumentException("Item with id: " + item.getItemId() + " not supported.");
            }
        }

        observable = inputObservable.subscribeOn(newThread())
                                                       .concatMap(warmUp())
                                                       .concatMap(benchMark())
                                                       .observeOn(mainThread())
                                                       .lift(progressObserver);

        subscription = resultListFragment.outputResults(observable).subscribe();

        return true;
    }
}
