package com.gojek.daggers.postProcessors.internal.processor.constant;

import com.gojek.daggers.postProcessors.common.ColumnNameManager;
import com.gojek.daggers.postProcessors.external.common.RowManager;
import com.gojek.daggers.postProcessors.internal.InternalSourceConfig;
import com.gojek.daggers.postProcessors.internal.processor.InternalConfigProcessor;

import java.io.Serializable;

public class ConstantInternalConfigProcessor implements InternalConfigProcessor, Serializable {

    public static final String CONSTANT_CONFIG_HANDLER_TYPE = "constant";
    private ColumnNameManager columnNameManager;
    private InternalSourceConfig internalSourceConfig;

    public ConstantInternalConfigProcessor(ColumnNameManager columnNameManager, InternalSourceConfig internalSourceConfig) {
        this.columnNameManager = columnNameManager;
        this.internalSourceConfig = internalSourceConfig;
    }

    @Override
    public boolean canProcess(String type) {
        return CONSTANT_CONFIG_HANDLER_TYPE.equals(type);
    }

    @Override
    public void process(RowManager rowManager) {
        int outputFieldIndex = columnNameManager.getOutputIndex(internalSourceConfig.getOutputField());
        if (outputFieldIndex != -1) {
            rowManager.setInOutput(outputFieldIndex, internalSourceConfig.getValue());
        }
    }
}
