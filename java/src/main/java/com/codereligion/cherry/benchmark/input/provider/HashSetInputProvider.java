package com.codereligion.cherry.benchmark.input.provider;

import java.util.Collection;
import java.util.HashSet;

public class HashSetInputProvider extends AbstractIterableInputProvider {

    public HashSetInputProvider(final long limit) {
        super(limit);
    }

    @Override
    protected Collection<Long> collection() {
        return new HashSet<>();
    }
}
