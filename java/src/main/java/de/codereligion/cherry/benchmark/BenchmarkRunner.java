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
package de.codereligion.cherry.benchmark;

import com.google.common.base.Stopwatch;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Subscriber;

public class BenchmarkRunner {

    public static Observable<Output> start(final InputFactory inputFactory) {

        return Observable.create(new Observable.OnSubscribe<Output>() {
            @Override
            public void call(final Subscriber<? super Output> subscriber) {

                final List<Input> inputs = inputFactory.createInputs();
                int antiOptimizeCheckInt = warmUpWith(inputs);

                final Stopwatch stopwatch = Stopwatch.createUnstarted();
                for (final Input input : inputs) {

                    if (subscriber.isUnsubscribed()) {
                        return;
                    }

                    final Output output = new Output().withTags(input.getTags());

                    for (long reps = 0; reps < input.getRepetitions(); reps++) {

                        if (subscriber.isUnsubscribed()) {
                            return;
                        }

                        stopwatch.start();
                        antiOptimizeCheckInt |= input.run();
                        stopwatch.stop();

                        final long timeInNanos = stopwatch.elapsed(TimeUnit.NANOSECONDS);
                        output.addRunTime(timeInNanos);
                    }

                    subscriber.onNext(output);
                }

                subscriber.onCompleted();
                System.out.println("check int: " + antiOptimizeCheckInt);
            }
        });
    }

    private static int warmUpWith(final List<Input> inputs) {

        int antiOptimizeCheckInt = 0;

        for (final Input input : inputs) {
            antiOptimizeCheckInt |= input.run();
        }

        return antiOptimizeCheckInt;
    }

}
