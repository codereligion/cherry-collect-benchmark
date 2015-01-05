package com.codereligion.cherry.benchmark;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ContestantResult {

    private String name;
    private List<Long> runTimes = Lists.newArrayList();

    public static ContestantResult from(final Contestant contestant) {
        return new ContestantResult().withName(contestant.getName());
    }

    ContestantResult() {
        // disallow public instantiation
    }

    ContestantResult withName(final String name) {
        this.name = name;
        return this;
    }

    ContestantResult withRunTimes(final List<Long> runTimes) {
        this.runTimes = runTimes;
        return this;
    }

    public String getName() {
        return name;
    }

    public List<Long> getRunTimes() {
        return runTimes;
    }

    public long fastestRunTime(final TimeUnit timeUnit) {
        return timeUnit.convert(Collections.min(runTimes), TimeUnit.NANOSECONDS);
    }

    public long slowestRunTime(final TimeUnit timeUnit) {
        return timeUnit.convert(Collections.max(runTimes), TimeUnit.NANOSECONDS);
    }

    public long averageRepetitionTime(final TimeUnit timeUnit) {

        long sum = 0;
        for (final Long runTime : runTimes) {
            sum += runTime;
        }

        return timeUnit.convert(sum / runTimes.size(), TimeUnit.NANOSECONDS);
    }

    public void addRunTime(final long timeInNanos) {
        this.runTimes.add(timeInNanos);
    }
}
