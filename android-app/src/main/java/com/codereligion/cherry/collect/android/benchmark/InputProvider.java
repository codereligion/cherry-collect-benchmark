package com.codereligion.cherry.collect.android.benchmark;

enum InputProvider {

    ARRAY_LIST(R.string.arrayList),
    LINKED_LIST(R.string.linkedList),
    HASH_SET(R.string.hashSet);

    private int stringId;

    InputProvider(final int stringId) {
        this.stringId = stringId;
    }

    public int getStringId() {
        return stringId;
    }

    public void setStringId(final int stringId) {
        this.stringId = stringId;
    }

    public static InputProvider findByOrdinal(final int ordinal) {
        for (final InputProvider inputProvider : values()) {
            if (inputProvider.ordinal() == ordinal) {
                return inputProvider;
            }
        }

        throw new IllegalArgumentException("Could not find inputProvider for given ordinal: " + ordinal);
    }
}
