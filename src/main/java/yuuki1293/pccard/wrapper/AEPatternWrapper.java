package yuuki1293.pccard.wrapper;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;

public class AEPatternWrapper implements IPatternDetails {
    private final AEItemKey definition;
    private final IPatternDetails.IInput[] inputs;
    private final GenericStack[] outputs;
    private final int number;

    public AEPatternWrapper(AEItemKey definition, IInput[] inputs, GenericStack[] outputs, int number) {
        this.definition = definition;
        this.inputs = inputs;
        this.outputs = outputs;
        this.number = number;
    }

    @Override
    public AEItemKey getDefinition() {
        return definition;
    }

    @Override
    public IInput[] getInputs() {
        return inputs;
    }

    @Override
    public GenericStack[] getOutputs() {
        return outputs;
    }

    public int getNumber() {
        return number;
    }
}
