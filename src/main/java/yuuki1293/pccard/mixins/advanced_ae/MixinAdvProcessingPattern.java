package yuuki1293.pccard.mixins.advanced_ae;

import net.pedroksl.advanced_ae.common.patterns.AdvProcessingPattern;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import yuuki1293.pccard.wrapper.IAEPattern;

@Mixin(value = AdvProcessingPattern.class, remap = false)
public class MixinAdvProcessingPattern implements IAEPattern {

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
