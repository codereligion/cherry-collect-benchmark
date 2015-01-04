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
package de.codereligion.cherry.collect;

import de.codereligion.cherry.benchmark.BenchmarkRunner;
import de.codereligion.cherry.benchmark.Output;
import de.codereligion.cherry.benchmark.collect.ListFilteringAndTransformationBenchmarkInputFactory;
import de.codereligion.cherry.benchmark.collect.ListFilteringBenchmarkInputFactory;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import org.junit.Test;
import rx.functions.Action1;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class BenchmarkTest {

    private final Action1<Output> printOutput = new Action1<Output>() {
        @Override
        public void call(final Output output) {
            System.out.printf("%s min: %s, max: %s, avg: %s" + System.getProperty("line.separator"),
                              output.getSortedTags(),
                              format(output.fastestRunTime(NANOSECONDS)),
                              format(output.slowestRunTime(NANOSECONDS)),
                              format(output.averageRepetitionTime(NANOSECONDS)));
        }
    };

    private String format(final long time) {
        final DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setGroupingSeparator(":".charAt(0));
        final DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
        return decimalFormat.format(time);
    }

    @Test
    public void benchmarkFilterToArrayList() {
        BenchmarkRunner.start(new ListFilteringBenchmarkInputFactory()).subscribe(printOutput);
    }

    @Test
    public void benchmarkFilterAndTransformToArrayList() {
        BenchmarkRunner.start(new ListFilteringAndTransformationBenchmarkInputFactory()).subscribe(printOutput);
    }
}