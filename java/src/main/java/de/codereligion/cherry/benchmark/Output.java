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
package de.codereligion.cherry.benchmark;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class Output {

    private List<Long> runTimes = Lists.newArrayList();
    private Set<String> tags = Sets.newHashSet();

    Output addRunTime(final long runTimeInNanos) {
        this.runTimes.add(runTimeInNanos);
        return this;
    }

    Output withTags(final Set<String> tags) {
        this.tags = tags;
        return this;
    }

    public Set<String> getTags() {
        return tags;
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

    public SortedSet<String> getSortedTags() {
        return new TreeSet<String>(tags);
    }
}
