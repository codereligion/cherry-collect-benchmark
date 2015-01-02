package com.codereligion.cherry.collect.android.benchmark;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.common.base.Strings;
import de.codereligion.cherry.benchmark.BenchmarkRunner;
import de.codereligion.cherry.benchmark.InputFactory;
import de.codereligion.cherry.benchmark.Output;
import de.codereligion.cherry.benchmark.collect.ListFilteringAndTransformationBenchmarkInputFactory;
import de.codereligion.cherry.benchmark.collect.ListFilteringBenchmarkInputFactory;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import rx.Subscriber;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static rx.android.observables.AndroidObservable.bindActivity;
import static rx.android.schedulers.AndroidSchedulers.mainThread;
import static rx.schedulers.Schedulers.newThread;


public class MainActivity extends ActionBarActivity {

    private static final String LAST_BENCHMARK_RESULT_STORE = "LAST_BENCHMARK_RESULT_STORE";
    private static final String LAST_BENCHMARK_RESULT_KEY = "LAST_BENCHMARK_RESULT_KEY";

    private ProgressBar progressBar;
    private TextView textView;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(LAST_BENCHMARK_RESULT_STORE, Context.MODE_PRIVATE);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        progressBar = (ProgressBar) findViewById(R.id.progress_spinner);
        textView = (TextView) findViewById(R.id.textView);

        restoreState();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.list_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filterToArrayList: {
                startBenchmark(new ListFilteringBenchmarkInputFactory());
                return true;
            }
            case R.id.filterAndTransformToArrayList: {
                startBenchmark(new ListFilteringAndTransformationBenchmarkInputFactory());
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        sharedPreferences.edit().putString(LAST_BENCHMARK_RESULT_KEY, textView.getText().toString()).apply();
    }

    private void restoreState() {
        final String lastResultOrDefault = sharedPreferences.getString(LAST_BENCHMARK_RESULT_KEY, getString(R.string.defaultText));
        textView.setText(lastResultOrDefault);
    }

    private void startBenchmark(final InputFactory inputFactory) {

        bindActivity(this, BenchmarkRunner.start(inputFactory).subscribeOn(newThread()).observeOn(mainThread())).subscribe(new Subscriber<Output>() {

            private int counter = 0;

            @Override
            public void onStart() {
                progressBar.setVisibility(View.VISIBLE);
                textView.setText("");
            }

            @Override
            public void onCompleted() {
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(final Throwable e) {
                // not expected
            }

            @Override
            public void onNext(final Output output) {
                final String lineSeparator = System.getProperty("line.separator");
                final String formattedOutput = output.getSortedTags() +
                                               lineSeparator +
                                               String.format("runs: %s, min: %s, max: %s, avg: %s",
                                                             output.getRunTimes().size(),
                                                             format(output.fastestRunTime(NANOSECONDS)),
                                                             format(output.slowestRunTime(NANOSECONDS)),
                                                             format(output.averageRepetitionTime(NANOSECONDS)));
                final String newLine;
                if (counter % 2 == 0) {
                    newLine = Strings.repeat(lineSeparator, 2);
                } else {
                    newLine = lineSeparator;
                }

                textView.setText(formattedOutput + newLine + textView.getText());

                counter++;
            }
        });
    }

    private String format(final long time) {
        final DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setGroupingSeparator(":".charAt(0));
        final DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
        return decimalFormat.format(time);
    }
}
