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

import com.codereligion.cherry.benchmark.Output;
import org.junit.Test;
import rx.functions.Action1;
import static com.codereligion.cherry.benchmark.BenchmarkRunner.benchMark;
import static com.codereligion.cherry.benchmark.BenchmarkRunner.warmUp;
import static com.codereligion.cherry.benchmark.collect.ListFilteringAndTransformationBenchmarkInputFactory.createListFilteringAndTransformingBenchmarkInput;
import static com.codereligion.cherry.benchmark.collect.ListFilteringBenchmarkInputFactory.createListFilteringBenchmarkInput;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class BenchmarkTest {

    private final Action1<Output> printOutput = new Action1<Output>() {
        @Override
        public void call(final Output output) {

            final float cherry = output.getCherryResult().fastestRunTime(NANOSECONDS);
            final float guava = output.getGuavaResult().fastestRunTime(NANOSECONDS);
            final float cherryPercentageChange = ((guava - cherry) / cherry) * 100;

            System.out.printf("numElements: %s, cherry-improvement: %.2f%%", output.getNumElements(), cherryPercentageChange);
            System.out.println();
        }
    };

    @Test
    public void benchmarkFilterToArrayList() {
        createListFilteringBenchmarkInput().concatMap(warmUp()).concatMap(benchMark()).subscribe(printOutput);
    }

    @Test
    public void benchmarkFilterAndTransformToArrayList() {
        createListFilteringAndTransformingBenchmarkInput().concatMap(warmUp()).concatMap(benchMark()).subscribe(printOutput);
    }
}