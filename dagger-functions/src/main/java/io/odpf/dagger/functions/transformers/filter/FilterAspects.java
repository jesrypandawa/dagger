package io.odpf.dagger.functions.transformers.filter;

import io.odpf.dagger.common.metrics.aspects.AspectType;
import io.odpf.dagger.common.metrics.aspects.Aspects;

public enum FilterAspects implements Aspects {
    FILTERED_INVALID_RECORDS("filtered_invalid_records", AspectType.Counter);

    FilterAspects(String value, AspectType type) {
        this.value = value;
        this.type = type;
    }

    private String value;
    private AspectType type;

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public AspectType getAspectType() {
        return this.type;
    }
}
