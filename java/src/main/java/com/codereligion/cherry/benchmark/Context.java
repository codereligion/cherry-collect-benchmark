package com.codereligion.cherry.benchmark;

public class Context {

    private long numElements;
    private int numReps;
    private Class<?> inputType;
    private Class<?> outputType;
    private String operation;

    public Context withOperation(final String operation) {
        this.operation = operation;
        return this;
    }

    public Context withNumElements(final long numElements) {
        this.numElements = numElements;
        return this;
    }

    public Context withInputType(final Class<?> inputType) {
        this.inputType = inputType;
        return this;
    }

    public Context withOutputType(final Class<?> outputType) {
        this.outputType = outputType;
        return this;
    }

    public int numReps() {
        return this.numReps;
    }

    public Context withNumReps(final int numReps) {
        this.numReps = numReps;
        return this;
    }

    public Class<?> outputType() {
        return this.outputType;
    }

    public String operation() {
        return operation;
    }

    public long numElements() {
        return numElements;
    }

    public Class<?> inputType() {
        return this.inputType;
    }
}
