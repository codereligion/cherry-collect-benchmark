package com.codereligion.cherry.benchmark;

public interface IterableInputProvider extends Comparable<IterableInputProvider> {

    Iterable<Long> get();

    long numElements();
}
