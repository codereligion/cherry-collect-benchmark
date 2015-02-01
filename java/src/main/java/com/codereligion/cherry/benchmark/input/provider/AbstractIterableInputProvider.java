package com.codereligion.cherry.benchmark.input.provider;

import com.codereligion.cherry.benchmark.IterableInputProvider;
import java.util.Collection;

public abstract class AbstractIterableInputProvider implements IterableInputProvider {

    private final long limit;

    AbstractIterableInputProvider(final long limit) {
        this.limit = limit;
    }

    @Override
    public Iterable<Long> get() {
        final Collection<Long> collection = collection();

        for (long i = 0; i < limit; i++) {
            collection.add(i);
        }

        return collection;
    }

    @Override
    public long numElements() {
        return limit;
    }

    protected abstract Collection<Long> collection();
}
