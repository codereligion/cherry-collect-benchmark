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

public class Output {

    private Context context;
    private ContestantResult guavaContestant;
    private ContestantResult cherryContestant;

    Output() {
        // disallow public instantiation
    }

    Output withGuavaContestant(final ContestantResult contestantResult) {
        this.guavaContestant = contestantResult;
        return this;
    }

    Output withCherryContestant(final ContestantResult contestantResult) {
        this.cherryContestant = contestantResult;
        return this;
    }

    Output withContext(final Context context) {
        this.context = context;
        return this;
    }

    public Context context() {
        return this.context;
    }

    public ContestantResult cherryResult() {
        return cherryContestant;
    }

    public ContestantResult guavaResult() {
        return guavaContestant;
    }


}
