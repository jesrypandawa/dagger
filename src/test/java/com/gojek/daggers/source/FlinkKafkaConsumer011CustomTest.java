package com.gojek.daggers.source;

import com.gojek.daggers.metrics.ErrorStatsReporter;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.flink.streaming.runtime.tasks.ExceptionInChainedOperatorException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import java.util.Properties;
import java.util.regex.Pattern;

import static com.gojek.daggers.utils.Constants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class FlinkKafkaConsumer011CustomTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private SourceFunction.SourceContext sourceContext;

    @Mock
    private Configuration configuration;

    @Mock
    private KafkaDeserializationSchema kafkaDeserializationSchema;

    @Mock
    private RuntimeContext runtimeContext;

    @Mock
    private Properties properties;

    @Mock
    private ErrorStatsReporter errorStatsReporter;

    private FlinkKafkaConsumerCustomStub flinkKafkaConsumer011Custom;

    @Before
    public void setup() {
        initMocks(this);
        flinkKafkaConsumer011Custom = new FlinkKafkaConsumerCustomStub(Pattern.compile("test_topics"), kafkaDeserializationSchema, properties, configuration, new RuntimeException("test exception"));
    }

    @org.junit.Test
    public void shouldReportIfTelemetryEnabled() throws Exception {
        when(configuration.getBoolean(TELEMETRY_ENABLED_KEY, TELEMETRY_ENABLED_VALUE_DEFAULT)).thenReturn(true);
        when(configuration.getLong(SHUTDOWN_PERIOD_KEY, SHUTDOWN_PERIOD_DEFAULT)).thenReturn(0L);

        try {
            flinkKafkaConsumer011Custom.run(sourceContext);
        } catch (Exception e) {
            Assert.assertEquals("test exception", e.getMessage());
        }
        verify(errorStatsReporter, times(1)).reportFatalException(any(RuntimeException.class));
    }

    @org.junit.Test
    public void shouldNotReportIfTelemetryDisabled() throws Exception {
        when(configuration.getBoolean(TELEMETRY_ENABLED_KEY, TELEMETRY_ENABLED_VALUE_DEFAULT)).thenReturn(false);
        Throwable throwable = new Throwable();
        flinkKafkaConsumer011Custom = new FlinkKafkaConsumerCustomStub(Pattern.compile("test_topics"), kafkaDeserializationSchema, properties, configuration, new ExceptionInChainedOperatorException("chaining exception", throwable));
        try {
            flinkKafkaConsumer011Custom.run(sourceContext);
        } catch (Exception e) {
            Assert.assertEquals("chaining exception", e.getMessage());
        }
        verify(errorStatsReporter, times(0)).reportFatalException(any(RuntimeException.class));
    }


    @org.junit.Test
    public void shouldNotReportIfExceptionInChainedOperatorException() throws Exception {
        when(configuration.getBoolean(TELEMETRY_ENABLED_KEY, TELEMETRY_ENABLED_VALUE_DEFAULT)).thenReturn(true);
        when(configuration.getLong(SHUTDOWN_PERIOD_KEY, SHUTDOWN_PERIOD_DEFAULT)).thenReturn(0L);

        try {
            flinkKafkaConsumer011Custom.run(sourceContext);
        } catch (Exception e) {
            Assert.assertEquals("test exception", e.getMessage());
        }
        verify(errorStatsReporter, times(1)).reportFatalException(any(RuntimeException.class));
    }

    @Test
    public void shouldReturnErrorStatsReporter() {
        when(configuration.getLong(SHUTDOWN_PERIOD_KEY, SHUTDOWN_PERIOD_DEFAULT)).thenReturn(0L);
        ErrorStatsReporter expectedErrorStatsReporter = new ErrorStatsReporter(runtimeContext, configuration);
        Assert.assertEquals(expectedErrorStatsReporter.getClass(), flinkKafkaConsumer011Custom.getErrorStatsReporter().getClass());
    }


    public class FlinkKafkaConsumerCustomStub extends FlinkKafkaConsumer011Custom {
        private Exception exception;

        public FlinkKafkaConsumerCustomStub(Pattern subscriptionPattern, KafkaDeserializationSchema deserializer,
                                            Properties props, Configuration configuration, Exception exception) {
            super(subscriptionPattern, deserializer, props, configuration);
            this.exception = exception;
        }

        @Override
        public RuntimeContext getRuntimeContext() {
            return runtimeContext;
        }

        protected void runBaseConsumer(SourceContext sourceContext) throws Exception {
            throw exception;
        }

        protected ErrorStatsReporter getErrorStatsReporter(RuntimeContext runtimeContext) {
            return errorStatsReporter;
        }

        private ErrorStatsReporter getErrorStatsReporter() {
            return super.getErrorStatsReporter(runtimeContext);
        }
    }
}