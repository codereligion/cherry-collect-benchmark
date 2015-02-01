package com.codereligion.cherry.benchmark;

import com.codereligion.cherry.collect.ArrayLists;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import java.util.ArrayList;

public class FilterToArrayListBenchmark implements Benchmark {

    private static final Predicate<Long> IS_EVEN = new Predicate<Long>() {
        @Override
        public boolean apply(final Long input) {
            return input % 2 == 0;
        }
    };

    private Iterable<Long> iterable;
    private Context context;

    public FilterToArrayListBenchmark(final IterableInputProvider iterableInputProvider, final int numReps) {
        this.iterable = iterableInputProvider.get();
        this.context = new Context().withInputType(iterable.getClass())
                                    .withOutputType(ArrayList.class)
                                    .withNumElements(iterableInputProvider.numElements())
                                    .withNumReps(numReps)
                                    .withOperation("filter");
    }

    @Override
    public Contestant cherryContestant() {
        return new CherryContestant() {
            @Override
            public int run() {
                return ArrayLists.createFrom(iterable, IS_EVEN).size();
            }
        };
    }

    @Override
    public Contestant guavaContestant() {
        return new GuavaContestant() {
            @Override
            public int run() {
                return FluentIterable.from(iterable).filter(IS_EVEN).copyInto(new ArrayList<Long>()).size();
            }
        };
    }

    @Override
    public Context context() {
        return context;
    }
}
