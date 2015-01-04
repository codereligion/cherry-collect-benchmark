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
package com.codereligion.cherry.benchmark;

import com.google.common.base.Stopwatch;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.functions.Func1;

public class BenchmarkRunner {

    public static Func1<Input, Observable<Input>> warmUp() {
        return new Func1<Input, Observable<Input>>() {

            private boolean executed = false;

            @Override
            public Observable<Input> call(final Input input) {
                if (!executed) {
                    System.out.println("warmUp check int:" + input.run());
                    executed = true;
                }
                return Observable.just(input);
            }
        };
    }

    public static Func1<Input, Observable<Output>> benchMark() {
        return new Func1<Input, Observable<Output>>() {
            @Override
            public Observable<Output> call(final Input input) {
                int checkInt = 0;

                final Stopwatch stopwatch = Stopwatch.createUnstarted();
                final Output output = new Output().withTags(input.getTags());

                for (long reps = 0; reps < input.getRepetitions(); reps++) {

                    stopwatch.start();
                    checkInt |= input.run();
                    stopwatch.stop();

                    final long timeInNanos = stopwatch.elapsed(TimeUnit.NANOSECONDS);
                    output.addRunTime(timeInNanos);
                }

                System.out.println("check int:"  + checkInt);
                return Observable.just(output);
            }
        };
    }
}

