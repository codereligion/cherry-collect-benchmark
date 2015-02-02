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

import com.codereligion.cherry.collect.ArrayLists;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import java.util.ArrayList;
import javax.annotation.Nullable;

public class FilterAndTransformToArrayListBenchmark implements Benchmark {

    private static final Predicate<Long> PREDICATE = new Predicate<Long>() {
        @Override
        public boolean apply(final Long input) {
            return input % 2 == 0;
        }
    };
    private static final Function<Object, Integer> FUNCTION = new Function<Object, Integer>() {
        @Override
        public Integer apply(@Nullable final Object input) {
            return input.hashCode();
        }
    };

    private Iterable<Long> iterable;
    private Context context;

    public FilterAndTransformToArrayListBenchmark(final IterableInputProvider iterableInputProvider, final int numReps) {
        this.iterable = iterableInputProvider.get();
        this.context = new Context().withInputType(iterable.getClass())
                                    .withOutputType(ArrayList.class)
                                    .withNumElements(iterableInputProvider.numElements())
                                    .withNumReps(numReps)
                                    .withOperation("filter + transform");
    }

    @Override
    public Contestant cherryContestant() {
        return new CherryContestant() {
            @Override
            public int run() {
                return ArrayLists.createFrom(iterable, PREDICATE, FUNCTION).size();
            }
        };
    }

    @Override
    public Contestant guavaContestant() {
        return new GuavaContestant() {
            @Override
            public int run() {
                return FluentIterable.from(iterable).filter(PREDICATE).transform(FUNCTION).copyInto(new ArrayList<Integer>()).size();
            }
        };
    }

    @Override
    public Context context() {
        return context;
    }

}
