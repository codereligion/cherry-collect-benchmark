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
                    System.out.println("warmUp check int cherry contestant:" + input.getCherryResult().run());
                    System.out.println("warmUp check int guava contestant:" + input.getGuavaResult().run());
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
                final Output output = Output.from(input);

                output.withGuavaContestant(benchMark(input.getRepetitions(), input.getGuavaResult()));
                output.withCherryContestant(benchMark(input.getRepetitions(), input.getCherryResult()));

                return Observable.just(output);
            }

            private ContestantResult benchMark(final long repetitions, final Contestant contestant) {

                int checkInt = 0;
                final Stopwatch stopwatch = Stopwatch.createUnstarted();
                final ContestantResult contestantResult = ContestantResult.from(contestant);

                for (long reps = 0; reps < repetitions; reps++) {

                    stopwatch.start();
                    checkInt |= contestant.run();
                    stopwatch.stop();

                    final long timeInNanos = stopwatch.elapsed(TimeUnit.NANOSECONDS);
                    contestantResult.addRunTime(timeInNanos);
                    stopwatch.reset();
                }

                System.out.println("check int:" + checkInt);

                return contestantResult;
            }
        };
    }
}

