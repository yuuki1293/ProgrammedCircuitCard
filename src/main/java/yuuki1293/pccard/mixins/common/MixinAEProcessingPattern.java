package yuuki1293.pccard.mixins.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import appeng.crafting.pattern.AEProcessingPattern;
import yuuki1293.pccard.wrapper.IAEPattern;

@Mixin(value = AEProcessingPattern.class, remap = false)
public class MixinAEProcessingPattern implements IAEPattern {

    @Unique
    private int pCCard$number = 0;

    @Override
    public void pCCard$setNumber(int number) {
        pCCard$number = number;
    }

    @Override
    public int pCCard$getNumber() {
        return pCCard$number;
    }
}
