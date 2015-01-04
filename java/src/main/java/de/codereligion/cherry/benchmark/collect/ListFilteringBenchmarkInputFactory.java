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
package de.codereligion.cherry.benchmark.collect;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.codereligion.cherry.benchmark.Input;
import de.codereligion.cherry.benchmark.InputFactory;
import de.codereligion.cherry.collect.ArrayLists;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ListFilteringBenchmarkInputFactory implements InputFactory {

    private static final int REPETITIONS = 40;
    private static final Predicate<Long> IS_EVEN = new Predicate<Long>() {
        @Override
        public boolean apply(final Long input) {
            return input % 2 == 0;
        }
    };

    public List<Input> createInputs() {

        final List<Input> inputs = Lists.newArrayList();
        final int powers = 20;
        for (int p = 1; p <= powers; p++) {
            final long numElements = (long) Math.pow(2, p);
            final List<Long> iterable = createLongsUntil(numElements);
            inputs.add(new ArrayListsInput(iterable));
            inputs.add(new FluentIterableInput(iterable));
        }

        return inputs;
    }

    private List<Long> createLongsUntil(final long limit) {
        final List<Long> list = Lists.newArrayList();

        for (long i = 0; i < limit; i++) {
            list.add(i);
        }

        return list;
    }

    private static class ArrayListsInput implements Input {

        private final Iterable<Long> iterable;

        private ArrayListsInput(final Iterable<Long> iterable) {
            this.iterable = iterable;
        }

        @Override
        public Set<String> getTags() {
            return Sets.newHashSet("filter", "ArrayLists", "elements: " + Iterables.size(iterable));
        }

        @Override
        public long getRepetitions() {
            return REPETITIONS;
        }

        @Override
        public int run() {
            return ArrayLists.from(iterable, IS_EVEN).size();
        }
    }

    private class FluentIterableInput implements Input {

        private final List<Long> iterable;

        public FluentIterableInput(final List<Long> iterable) {
            this.iterable = iterable;
        }

        @Override
        public Set<String> getTags() {
            return Sets.newHashSet("filter", "FluentIterable", "elements: " + Iterables.size(iterable));
        }

        @Override
        public long getRepetitions() {
            return REPETITIONS;
        }

        @Override
        public int run() {
            return FluentIterable.from(iterable).filter(IS_EVEN).copyInto(new ArrayList<Long>()).size();
        }
    }
}
