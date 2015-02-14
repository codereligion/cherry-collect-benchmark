package com.codereligion.cherry.collect.android.benchmark;

enum Test {

    FILTER(R.string.filterLabel),
    TRANSFORM(R.string.transformLabel),
    FILTER_AND_TRANSFORM(R.string.filterAndTransformLabel),
    TRANSFORM_TO_MAP(R.string.transformToMapLabel);

    private int stringId;

    Test(final int stringId) {
        this.stringId = stringId;
    }

    public int getStringId() {
        return stringId;
    }

    public void setStringId(final int stringId) {
        this.stringId = stringId;
    }

    public static Test findByOrdinal(final int ordinal) {
        for (final Test test : values()) {
            if (test.ordinal() == ordinal) {
                return test;
            }
        }

        throw new IllegalArgumentException("Could not find test for given ordinal: " + ordinal);
    }
}
