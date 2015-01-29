/**
 * Copyright 2014 www.codereligion.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codereligion.cherry.collect.android.benchmark;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import com.codereligion.cherry.benchmark.Input;
import com.codereligion.cherry.benchmark.Output;
import com.codereligion.cherry.benchmark.collect.ListFilteringAndTransformationBenchmarkInputFactory;
import com.codereligion.cherry.benchmark.collect.ListToImmutableMapBenchmarkInputFactory;
import com.codereligion.cherry.benchmark.collect.ListFilteringBenchmarkInputFactory;
import com.google.common.collect.Lists;
import java.util.List;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import static com.codereligion.cherry.benchmark.BenchmarkRunner.benchMark;
import static com.codereligion.cherry.benchmark.BenchmarkRunner.warmUp;
import static rx.android.schedulers.AndroidSchedulers.mainThread;
import static rx.schedulers.Schedulers.newThread;


public class MainActivity extends ActionBarActivity {

    private static final int DEFAULT_NUM_REPS = 20;
    private Spinner numElementsSpinner;
    private Spinner numRepsSpinner;
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
        numElementsSpinner = (Spinner) findViewById(R.id.numElements);
        numRepsSpinner = (Spinner) findViewById(R.id.numReps);
        progressBar = (ProgressBar) findViewById(R.id.progress_spinner);
        resultListFragment = (ResultListFragment) getSupportFragmentManager().findFragmentById(R.id.result_list_fragment);
        recoverLastObservable();
        setSpinnerAdapters();
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
                inputObservable = ListFilteringBenchmarkInputFactory.create(getNumElementsSpinner(), getNumRepsSpinner());
                break;
            }
            case R.id.filterAndTransformToArrayList: {
                inputObservable = ListFilteringAndTransformationBenchmarkInputFactory.create(getNumElementsSpinner(), getNumRepsSpinner());
                break;
            }
            case R.id.transformToMap: {
                inputObservable = ListToImmutableMapBenchmarkInputFactory.create(getNumElementsSpinner(), getNumRepsSpinner());
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

        observable = inputObservable.subscribeOn(newThread()).concatMap(warmUp()).concatMap(benchMark()).observeOn(mainThread()).lift(progressObserver);

        subscription = resultListFragment.outputResults(observable).subscribe();

        return true;
    }

    private void setSpinnerAdapters() {
        final SpinnerAdapter numElementsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, createNumElements());
        numElementsSpinner.setAdapter(numElementsAdapter);
        numElementsSpinner.setSelection(numElementsAdapter.getCount() - 1);

        final List<Integer> numReps = createNumReps();
        final SpinnerAdapter numRepsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, numReps);
        numRepsSpinner.setAdapter(numRepsAdapter);
        numRepsSpinner.setSelection(numReps.indexOf(DEFAULT_NUM_REPS));
    }

    private List<Long> createNumElements() {
        final int maxPowers = 20;

        final List<Long> numElements = Lists.newArrayList();
        for (int i = 1; i <= maxPowers; i++) {
            numElements.add((long) Math.pow(2, i));
        }

        return numElements;
    }

    private List<Integer> createNumReps() {
        return Lists.newArrayList(1, 2, 5, 10, 20, 30, 40);
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

    public long getNumElementsSpinner() {
        return (long) numElementsSpinner.getSelectedItem();
    }

    public int getNumRepsSpinner() {
        return (int) numRepsSpinner.getSelectedItem();
    }
}
