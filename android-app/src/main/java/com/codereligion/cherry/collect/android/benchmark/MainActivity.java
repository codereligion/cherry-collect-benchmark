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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import com.codereligion.cherry.benchmark.Benchmark;
import com.codereligion.cherry.benchmark.FilterAndTransformToArrayListBenchmark;
import com.codereligion.cherry.benchmark.FilterToArrayListBenchmark;
import com.codereligion.cherry.benchmark.ListToImmutableMapBenchmark;
import com.codereligion.cherry.benchmark.Output;
import com.codereligion.cherry.benchmark.input.provider.ArrayListInputProvider;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import static com.codereligion.cherry.benchmark.BenchmarkRunner.benchMarkIterable;
import static com.codereligion.cherry.benchmark.BenchmarkRunner.warmUp;
import static rx.android.schedulers.AndroidSchedulers.mainThread;
import static rx.schedulers.Schedulers.newThread;


// TODO make type of input configurable
public class MainActivity extends ActionBarActivity {

    private static final int DEFAULT_NUM_REPS = 20;
    private static final int FILTER_TO_ARRAY_LIST_TEST = 0;
    private static final int FILTER_AND_TRANSFORM_TO_ARRAY_LIST_TEST = 1;
    private static final int TRANSFORM_TO_MAP_TEST = 2;
    private static final Map<Integer, Integer> TEST_ID_TO_LABEL_ID = Maps.newHashMap();

    static {
        TEST_ID_TO_LABEL_ID.put(FILTER_TO_ARRAY_LIST_TEST, R.string.filterToArrayListLabel);
        TEST_ID_TO_LABEL_ID.put(FILTER_AND_TRANSFORM_TO_ARRAY_LIST_TEST, R.string.filterAndTransformToArrayListLabel);
        TEST_ID_TO_LABEL_ID.put(TRANSFORM_TO_MAP_TEST, R.string.transformToMapLabel);
    }

    private Spinner numElementsSpinner;
    private Spinner numRepsSpinner;
    private Spinner testsSpinner;
    private Button runButton;
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
        numElementsSpinner = (Spinner) findViewById(R.id.numElementsSpinner);
        numRepsSpinner = (Spinner) findViewById(R.id.numRepsSpinner);
        testsSpinner = (Spinner) findViewById(R.id.testSpinner);
        runButton = (Button) findViewById(R.id.runButton);
        progressBar = (ProgressBar) findViewById(R.id.progress_spinner);
        resultListFragment = (ResultListFragment) getSupportFragmentManager().findFragmentById(R.id.result_list_fragment);
        setSpinnerAdapters();
        registerButtonListener();
        recoverLastObservable();
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
        switch (item.getItemId()) {
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
    }

    private void registerButtonListener() {
        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                observable = Observable.create(new Observable.OnSubscribe<Benchmark>() {
                    @Override
                    public void call(final Subscriber<? super Benchmark> subscriber) {
                        subscriber.onNext(getTest());
                        subscriber.onCompleted();
                    }
                }).subscribeOn(newThread()).concatMap(warmUp()).concatMap(benchMarkIterable()).observeOn(mainThread()).lift(progressObserver);
                subscription = resultListFragment.outputResults(observable).subscribe();
            }
        });
    }

    private void setSpinnerAdapters() {
        final List<Long> numElements = createNumElementsLabels();
        numElementsSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, numElements));
        numElementsSpinner.setSelection(numElements.size() - 1);

        final List<Integer> numReps = createNumRepsLabels();
        numRepsSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, numReps));
        numRepsSpinner.setSelection(numReps.indexOf(DEFAULT_NUM_REPS));

        final List<TestItem> tests = createTestItems();
        testsSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tests));
    }

    private List<Long> createNumElementsLabels() {
        final int maxPowers = 20;

        final List<Long> numElements = Lists.newArrayList();
        for (int i = 1; i <= maxPowers; i++) {
            numElements.add((long) Math.pow(2, i));
        }

        return numElements;
    }

    private List<Integer> createNumRepsLabels() {
        return Lists.newArrayList(1, 2, 5, 10, 20, 30, 40);
    }

    private List<TestItem> createTestItems() {

        final List<TestItem> labels = Lists.newArrayList();
        for (final Map.Entry<Integer, Integer> entry : TEST_ID_TO_LABEL_ID.entrySet()) {
            final Integer id = entry.getKey();
            final String label = getString(entry.getValue());
            labels.add(new TestItem(id, label));
        }

        return labels;
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

    public Benchmark getTest() {

        final TestItem selectedItem = (TestItem) testsSpinner.getSelectedItem();

        switch (selectedItem.getId()) {

            case FILTER_TO_ARRAY_LIST_TEST: {
                return new FilterToArrayListBenchmark(new ArrayListInputProvider(getNumElements()), getNumReps());
            }
            case FILTER_AND_TRANSFORM_TO_ARRAY_LIST_TEST: {
                return new FilterAndTransformToArrayListBenchmark(new ArrayListInputProvider(getNumElements()), getNumReps());
            }
            case TRANSFORM_TO_MAP_TEST: {
                return new ListToImmutableMapBenchmark(new ArrayListInputProvider(getNumElements()), getNumReps());
            }
            default: {
                throw new IllegalStateException("Could not find test for: " + selectedItem.getLabel());
            }
        }
    }

    public long getNumElements() {
        return (long) numElementsSpinner.getSelectedItem();
    }

    public int getNumReps() {
        return (int) numRepsSpinner.getSelectedItem();
    }

    private static class TestItem {
        private int id;
        private String label;

        private TestItem(final int id, final String label) {
            this.id = id;
            this.label = label;
        }

        public int getId() {
            return id;
        }

        public String getLabel() {
            return label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
