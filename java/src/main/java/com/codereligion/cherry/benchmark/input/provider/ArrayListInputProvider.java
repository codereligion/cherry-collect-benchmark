package com.codereligion.cherry.benchmark.input.provider;

import java.util.ArrayList;
import java.util.Collection;

public class ArrayListInputProvider extends AbstractIterableInputProvider {

    public ArrayListInputProvider(final long limit) {
        super(limit);
    }

    @Override
    protected Collection<Long> collection() {
        return new ArrayList<>();
    }
}
