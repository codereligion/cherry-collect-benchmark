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

import com.codereligion.cherry.collect.ImmutableMaps;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import javax.annotation.Nullable;

public class ListToImmutableMapBenchmark implements Benchmark {

    private static final Function<Long, Long> FUNCTION = new Function<Long, Long>() {
        @Override
        public Long apply(@Nullable final Long input) {
            return input;
        }
    };

    private Iterable<Long> iterable;
    private Context context;

    public ListToImmutableMapBenchmark(final IterableInputProvider iterableInputProvider, final int numReps) {
        this.iterable = iterableInputProvider.get();
        this.context = new Context().withInputType(iterable.getClass())
                                    .withOutputType(ImmutableMap.class)
                                    .withNumElements(iterableInputProvider.numElements())
                                    .withNumReps(numReps)
                                    .withOperation("filter");
    }

    @Override
    public Contestant cherryContestant() {
        return new CherryContestant() {
            @Override
            public int run() {
                return ImmutableMaps.createFrom(iterable, FUNCTION).size();
            }
        };
    }

    @Override
    public Contestant guavaContestant() {
        return new GuavaContestant() {
            @Override
            public int run() {
                return FluentIterable.from(iterable).uniqueIndex(FUNCTION).size();
            }
        };
    }

    @Override
    public Context context() {
        return context;
    }
}
