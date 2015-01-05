package com.codereligion.cherry.collect.android.benchmark;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private OutputArrayAdapter adapter;

    public ResultListFragment() {
        setRetainInstance(true);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new OutputArrayAdapter(getActivity(), android.R.layout.simple_list_item_1);
        setListAdapter(adapter);
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
            stringBuilder.append(cherryResult.averageRepetitionTime(NANOSECONDS));
            stringBuilder.append(",");
            stringBuilder.append(guavaResult.averageRepetitionTime(NANOSECONDS));
            stringBuilder.append(getLineSeparator());
        }
        return stringBuilder.toString();
    }

    private String getLineSeparator() {
        return System.getProperty("line.separator");
    }

    private static class OutputArrayAdapter extends ArrayAdapter<Output> {

        private final DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        private final DecimalFormat decimalFormat = new DecimalFormat();
        private final LayoutInflater inflater;
        private final int resource;

        public OutputArrayAdapter(final Context context, final int resource) {
            super(context, resource);
            this.resource = resource;
            decimalFormatSymbols.setGroupingSeparator(":".charAt(0));
            decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
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

            final float cherryAvg = output.getCherryResult().averageRepetitionTime(NANOSECONDS);
            final float guavaAvg = output.getGuavaResult().averageRepetitionTime(NANOSECONDS);
            final float cherryImprovement = 100 - ((cherryAvg / guavaAvg) * 100);

            return String.format("numElements: %s, cherry-improvement: %.2f%%", output.getNumElements(), cherryImprovement);
        }
    }
}
