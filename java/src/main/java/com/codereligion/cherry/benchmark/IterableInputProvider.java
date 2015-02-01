package com.codereligion.cherry.benchmark;

public interface IterableInputProvider {

    Iterable<Long> get();

    long numElements();
}
