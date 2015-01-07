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
package com.codereligion.cherry.benchmark.collect;

import com.codereligion.cherry.benchmark.Contestant;
import com.codereligion.cherry.benchmark.Input;
import com.codereligion.cherry.collect.HashMaps;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import java.util.List;
import rx.Observable;
import rx.Subscriber;
import static com.google.common.base.Functions.toStringFunction;

public class ListToImmutableMapBenchmarkInputFactory {

    private static final long REPETITIONS = 40;

    public static Observable<Input> create() {

        return Observable.create(new Observable.OnSubscribe<Input>() {
            @Override
            public void call(final Subscriber<? super Input> subscriber) {

                final int powers = 20;
                for (int p = 1; p <= powers; p++) {

                    if (subscriber.isUnsubscribed()) {
                        return;
                    }

                    final long numElements = (long) Math.pow(2, p);
                    final Iterable<Long> iterable = createLongsUntil(numElements);

                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(new Input().withOperation("filter")
                                                     .withNumElements(numElements)
                                                     .withRepetitions(REPETITIONS)
                                                     .withCherryContestant(new ArrayListsInput(iterable))
                                                     .withGuavaContestant(new FluentIterableInput(iterable)));
                    }
                }

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }
        });
    }

    private static Iterable<Long> createLongsUntil(final long limit) {
        final List<Long> list = Lists.newArrayList();

        for (long i = 0; i < limit; i++) {
            list.add(i);
        }

        return list;
    }

    private static class ArrayListsInput implements Contestant {

        private final Iterable<Long> iterable;

        private ArrayListsInput(final Iterable<Long> iterable) {
            this.iterable = iterable;
        }

        @Override
        public String getName() {
            return "Cherry-Collect";
        }

        @Override
        public int run() {
            return HashMaps.from(iterable, toStringFunction()).size();
        }
    }

    private static class FluentIterableInput implements Contestant {

        private final Iterable<Long> iterable;

        public FluentIterableInput(final Iterable<Long> iterable) {
            this.iterable = iterable;
        }

        @Override
        public String getName() {
            return "FluentIterable";
        }

        @Override
        public int run() {
            return FluentIterable.from(iterable).uniqueIndex(toStringFunction()).size();
        }
    }
}
