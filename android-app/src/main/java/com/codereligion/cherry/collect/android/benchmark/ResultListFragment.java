package com.codereligion.cherry.collect.android.benchmark;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import rx.Observable;
import rx.Subscriber;

public class ResultListFragment extends android.support.v4.app.ListFragment {

    private ArrayAdapter<String> adapter;

    public ResultListFragment() {
        setRetainInstance(true);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        setListAdapter(adapter);
    }

    public Observable<String> outputResults(final Observable<String> inputObservable) {
        return inputObservable.lift(new Observable.Operator<String, String>() {
            @Override
            public Subscriber<? super String> call(final Subscriber<? super String> subscriber) {
                return new Subscriber<String>() {

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
                    public void onNext(final String string) {
                        adapter.insert(string, 0);
                        adapter.notifyDataSetChanged();
                    }
                };
            }
        });
    }
}
