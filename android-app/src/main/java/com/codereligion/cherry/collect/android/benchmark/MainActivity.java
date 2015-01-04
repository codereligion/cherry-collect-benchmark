package com.codereligion.cherry.collect.android.benchmark;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import com.codereligion.cherry.benchmark.Input;
import com.codereligion.cherry.benchmark.Output;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;
import static com.codereligion.cherry.benchmark.BenchmarkRunner.benchMark;
import static com.codereligion.cherry.benchmark.BenchmarkRunner.warmUp;
import static com.codereligion.cherry.benchmark.collect.ListFilteringAndTransformationBenchmarkInputFactory.createListFilteringAndTransformingBenchmarkInput;
import static com.codereligion.cherry.benchmark.collect.ListFilteringBenchmarkInputFactory.createListFilteringBenchmarkInput;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static rx.android.schedulers.AndroidSchedulers.mainThread;
import static rx.schedulers.Schedulers.newThread;


public class MainActivity extends ActionBarActivity {

    private ProgressBar progressBar;
    private ResultListFragment resultListFragment;
    private Observable.Operator<String, String> progressObserver = new Observable.Operator<String, String>() {
        @Override
        public Subscriber<? super String> call(final Subscriber<? super String> subscriber) {
            return new Subscriber<String>() {

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
                public void onNext(final String input) {
                    subscriber.onNext(input);
                }
            };
        }
    };

    private Subscription subscription;
    private Observable<String> observable;

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
    public Observable<String> getLastCustomNonConfigurationInstance() {
        return (Observable<String>) super.getLastCustomNonConfigurationInstance();
    }

    @Override
    public Observable<String> onRetainCustomNonConfigurationInstance() {
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
            default: {
                throw new IllegalArgumentException("Item with id: " + item.getItemId() + " not supported.");
            }
        }

        observable = inputObservable.subscribeOn(newThread())
                                                       .concatMap(warmUp())
                                                       .concatMap(benchMark())
                                                       .concatMap(formatToString())
                                                       .observeOn(mainThread())
                                                       .lift(progressObserver);

        subscription = resultListFragment.outputResults(observable).subscribe();

        return true;
    }

    private Func1<Output, Observable<String>> formatToString() {
        return new Func1<Output, Observable<String>>() {

            @Override
            public Observable<String> call(final Output output) {
                final String formattedOutput = output.getSortedTags() +
                                               String.format("runs: %s, min: %s, max: %s, avg: %s",
                                                             output.getRunTimes().size(),
                                                             format(output.fastestRunTime(NANOSECONDS)),
                                                             format(output.slowestRunTime(NANOSECONDS)),
                                                             format(output.averageRepetitionTime(NANOSECONDS)));

                return Observable.just(formattedOutput);
            }
        };
    }

    private String format(final long time) {
        final DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setGroupingSeparator(":".charAt(0));
        final DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
        return decimalFormat.format(time);
    }
}
