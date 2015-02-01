package com.codereligion.cherry.benchmark.input.provider;

import java.util.Collection;
import java.util.LinkedList;

public class LinkedListInputProvider extends AbstractIterableInputProvider {

    public LinkedListInputProvider(final long limit) {
        super(limit);
    }

    @Override
    protected Collection<Long> collection() {
        return new LinkedList<>();
    }
}
