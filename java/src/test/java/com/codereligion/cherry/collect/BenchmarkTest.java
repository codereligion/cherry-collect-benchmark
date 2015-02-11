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
package com.codereligion.cherry.collect;

import com.codereligion.cherry.benchmark.Benchmark;
import com.codereligion.cherry.benchmark.Context;
import com.codereligion.cherry.benchmark.FilterAndTransformToArrayListBenchmark;
import com.codereligion.cherry.benchmark.FilterToArrayListBenchmark;
import com.codereligion.cherry.benchmark.IterableInputProvider;
import com.codereligion.cherry.benchmark.ListToImmutableMapBenchmark;
import com.codereligion.cherry.benchmark.Output;
import com.codereligion.cherry.benchmark.TransformToArrayListBenchmark;
import com.codereligion.cherry.benchmark.input.provider.ArrayListInputProvider;
import com.codereligion.cherry.benchmark.input.provider.HashSetInputProvider;
import com.codereligion.cherry.benchmark.input.provider.LinkedListInputProvider;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import static com.codereligion.cherry.benchmark.BenchmarkRunner.benchMarkIterable;
import static com.codereligion.cherry.benchmark.BenchmarkRunner.warmUp;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class BenchmarkTest {

    private static final int NUM_REPS = 40;
    private static final List<Long> NUM_ELEMENTS = Lists.newArrayList(1024L, 4096L, 8192L, 16384L, 32768L, 65536L, 131072L, 262144L, 524288L, 1048576L);
    private static final List<IterableInputProvider> INPUT_PROVIDERS = createInputProviders();

    private static List<IterableInputProvider> createInputProviders() {

        final List<IterableInputProvider> all = Lists.newArrayList();
        for (final Long numElements : NUM_ELEMENTS) {
            all.add(new ArrayListInputProvider(numElements));
            all.add(new LinkedListInputProvider(numElements));
            all.add(new HashSetInputProvider(numElements));
        }

        Collections.sort(all);

        return all;
    }

    private final Action1<Output> printOutput = new Action1<Output>() {
        @Override
        public void call(final Output output) {

            final float cherry = output.cherryResult().fastestRunTime(NANOSECONDS);
            final float guava = output.guavaResult().fastestRunTime(NANOSECONDS);
            final float cherryPercentageChange = ((guava - cherry) / cherry) * 100;

            final Context context = output.context();

            System.out.printf("elements: %s, operation: %s, inputType: %s, outputType: %s, improvement: %.2f%%",
                              context.numElements(),
                              context.operation(),
                              context.inputType().getSimpleName(),
                              context.outputType().getSimpleName(),
                              cherryPercentageChange);
            System.out.println();
        }
    };

    @Test
    public void benchmarkFilterToArrayList() {
        run(new Callable<Benchmark>() {
            @Override
            public Benchmark call(final IterableInputProvider iterableInputProvider) {
                return new FilterToArrayListBenchmark(iterableInputProvider, NUM_REPS);
            }
        });
    }

    @Test
    public void benchmarkTransformToArrayList() {
        run(new Callable<Benchmark>() {
            @Override
            public Benchmark call(final IterableInputProvider iterableInputProvider) {
                return new TransformToArrayListBenchmark(iterableInputProvider, NUM_REPS);
            }
        });
    }

    @Test
    public void benchmarkFilterAndTransformToArrayList() {
        run(new Callable<Benchmark>() {
            @Override
            public Benchmark call(final IterableInputProvider iterableInputProvider) {
                return new FilterAndTransformToArrayListBenchmark(iterableInputProvider, NUM_REPS);
            }
        });
    }

    @Test
    public void benchmarkListToImmutableMap() {
        run(new Callable<Benchmark>() {
            @Override
            public Benchmark call(final IterableInputProvider iterableInputProvider) {
                return new ListToImmutableMapBenchmark(iterableInputProvider, NUM_REPS);
            }
        });
    }

    private void run(final Callable<Benchmark> callable) {
        for (final IterableInputProvider iterableInputProvider : INPUT_PROVIDERS) {
            Observable.create(new Observable.OnSubscribe<Benchmark>() {
                @Override
                public void call(final Subscriber<? super Benchmark> subscriber) {
                    subscriber.onNext(callable.call(iterableInputProvider));
                    subscriber.onCompleted();
                }
            }).concatMap(warmUp()).concatMap(benchMarkIterable()).subscribe(printOutput);
        }
    }

    private static interface Callable<T> {
        T call(IterableInputProvider iterableInputProvider);
    }
}