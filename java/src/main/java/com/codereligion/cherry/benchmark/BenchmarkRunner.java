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

    public static Func1<Benchmark, Observable<Benchmark>> warmUp() {
        return new Func1<Benchmark, Observable<Benchmark>>() {

            private boolean executed = false;

            @Override
            public Observable<Benchmark> call(final Benchmark benchmark) {
                if (!executed) {
                    System.out.println("warmUp check int cherry contestant:" + benchmark.cherryContestant().run());
                    System.out.println("warmUp check int guava contestant:" + benchmark.guavaContestant().run());
                    executed = true;
                }
                return Observable.just(benchmark);
            }
        };
    }

    public static Func1<Benchmark, Observable<Output>> benchMarkIterable() {
        return new Func1<Benchmark, Observable<Output>>() {
            @Override
            public Observable<Output> call(final Benchmark benchmark) {
                final Output output = new Output().withContext(benchmark.context());

                output.withCherryContestant(benchMark(benchmark.context().numReps(), benchmark.cherryContestant()));
                output.withGuavaContestant(benchMark(benchmark.context().numReps(), benchmark.guavaContestant()));

                return Observable.just(output);
            }

            private ContestantResult benchMark(final long numReps, final Contestant contestant) {

                int checkInt = 0;
                final Stopwatch stopwatch = Stopwatch.createUnstarted();
                final ContestantResult contestantResult = ContestantResult.from(contestant);

                for (long reps = 0; reps < numReps; reps++) {

                    System.gc();
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

