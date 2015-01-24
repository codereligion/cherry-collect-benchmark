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

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.codereligion.cherry.benchmark.ContestantResult;
import com.codereligion.cherry.benchmark.Output;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import rx.Observable;
import rx.Subscriber;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class ResultListFragment extends android.support.v4.app.ListFragment {

    private final DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
    private final DecimalFormat decimalFormat = new DecimalFormat();
    private OutputArrayAdapter adapter;

    public ResultListFragment() {
        setRetainInstance(true);
        decimalFormatSymbols.setGroupingSeparator(":".charAt(0));
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new OutputArrayAdapter(getActivity(), android.R.layout.simple_list_item_1);
        setListAdapter(adapter);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                final Output output = adapter.getItem(position);
                outputRunTimes(output.getCherryResult());
                outputRunTimes(output.getGuavaResult());
                return true;
            }

            private void outputRunTimes(final ContestantResult contestantResult) {
                System.out.println(contestantResult.getName() + " RunTimes:");
                for (final Long runtTime : contestantResult.getRunTimes()) {
                    System.out.println(decimalFormat.format(runtTime));
                }
            }
        });
    }

    public Observable<Output> outputResults(final Observable<Output> inputObservable) {
        return inputObservable.lift(new Observable.Operator<Output, Output>() {
            @Override
            public Subscriber<? super Output> call(final Subscriber<? super Output> subscriber) {
                return new Subscriber<Output>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        adapter.clear();
                    }

                    @Override
                    public void onCompleted() {
                        // noop
                    }

                    @Override
                    public void onError(final Throwable e) {
                        // noop
                    }

                    @Override
                    public void onNext(final Output output) {
                        adapter.insert(output, 0);
                        adapter.notifyDataSetChanged();
                    }
                };
            }
        });
    }

    public String getResultsAsCsv() {

        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("numElements,cherry,guava");
        stringBuilder.append(getLineSeparator());
        for (int i = 0; i < adapter.getCount(); i++) {
            final Output output = adapter.getItem(i);
            final ContestantResult cherryResult = output.getCherryResult();
            final ContestantResult guavaResult = output.getGuavaResult();

            stringBuilder.append(output.getNumElements());
            stringBuilder.append(",");
            stringBuilder.append(cherryResult.fastestRunTime(NANOSECONDS));
            stringBuilder.append(",");
            stringBuilder.append(guavaResult.fastestRunTime(NANOSECONDS));
            stringBuilder.append(getLineSeparator());
        }
        return stringBuilder.toString();
    }

    private String getLineSeparator() {
        return System.getProperty("line.separator");
    }

    private static class OutputArrayAdapter extends ArrayAdapter<Output> {

        private final LayoutInflater inflater;
        private final int resource;

        public OutputArrayAdapter(final Context context, final int resource) {
            super(context, resource);
            this.resource = resource;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            final TextView textView;

            if (convertView == null) {
                textView = (TextView) inflater.inflate(resource, parent, false);
            } else {
                textView = (TextView) convertView;
            }

            textView.setText(format(getItem(position)));

            return textView;
        }

        private String format(final Output output) {

            final float cherry = output.getCherryResult().fastestRunTime(NANOSECONDS);
            final float guava = output.getGuavaResult().fastestRunTime(NANOSECONDS);
            final float cherryPercentageChange = ((guava - cherry) / cherry) * 100;

            return String.format("numElements: %s, cherry-improvement: %.2f%%", output.getNumElements(), cherryPercentageChange);
        }
    }
}
