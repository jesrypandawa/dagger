package io.odpf.dagger.common.core;

public class Constants {
    public static final String SCHEMA_REGISTRY_STENCIL_ENABLE_KEY = "SCHEMA_REGISTRY_STENCIL_ENABLE";
    public static final boolean SCHEMA_REGISTRY_STENCIL_ENABLE_DEFAULT = false;
    public static final String SCHEMA_REGISTRY_STENCIL_URLS_KEY = "SCHEMA_REGISTRY_STENCIL_URLS";
    public static final String SCHEMA_REGISTRY_STENCIL_URLS_DEFAULT = "";
    public static final String SCHEMA_REGISTRY_STENCIL_REFRESH_CACHE_KEY = "SCHEMA_REGISTRY_STENCIL_REFRESH_CACHE";
    public static final String SCHEMA_REGISTRY_STENCIL_REFRESH_CACHE_DEFAULT = "false";

    public static final String UDF_TELEMETRY_GROUP_KEY = "udf";
    public static final String GAUGE_ASPECT_NAME = "value";

    public static final long SLIDING_TIME_WINDOW = 10;
}
