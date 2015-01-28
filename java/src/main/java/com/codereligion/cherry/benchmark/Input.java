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

public class Input {

    private Contestant cherryContestant;
    private Contestant guavaContestant;
    private String operation;
    private long numElements;
    private int repetitions;

    public Input withCherryContestant(final Contestant contestant) {
        this.cherryContestant = contestant;
        return this;
    }

    public Input withGuavaContestant(final Contestant contestant) {
        this.guavaContestant = contestant;
        return this;
    }

    public Input withOperation(final String operation) {
        this.operation = operation;
        return this;
    }

    public Input withNumElements(final long numElements) {
        this.numElements = numElements;
        return this;
    }

    public Input withRepetitions(final int repetitions) {
        this.repetitions = repetitions;
        return this;
    }

    public Contestant getCherryResult() {
        return cherryContestant;
    }

    public Contestant getGuavaResult() {
        return guavaContestant;
    }

    public String getOperation() {
        return operation;
    }

    public long getNumElements() {
        return numElements;
    }

    public int getRepetitions() {
        return repetitions;
    }
}
