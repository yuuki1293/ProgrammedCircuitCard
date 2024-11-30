package yuuki1293.pccard.mixins;

import appeng.api.upgrades.MachineUpgradesChanged;
import appeng.api.upgrades.UpgradeInventories;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import yuuki1293.pccard.CompetitionFixer;
import yuuki1293.pccard.Holder;

@Mixin(value = UpgradeInventories.class, remap = false)
public class UpgradeInventoriesMixin {
    @Unique
    private static ItemLike pCCard$machineType;

    @ModifyVariable(method = "forMachine", at = @At("HEAD"), argsOnly = true)
    private static ItemLike captureMachineType(ItemLike item) {
        pCCard$machineType = item;
        return item;
    }

    @ModifyVariable(method = "forMachine", at = @At("HEAD"), argsOnly = true)
    private static MachineUpgradesChanged injectedCallback(MachineUpgradesChanged changeCallback) {
        if (!CompetitionFixer.hasPatternProviderUpgrade()) return changeCallback;

        var machineId = pCCard$machineType.asItem().getDescriptionId();

        if (machineId.equals("ae2:pattern_provider")
            || machineId.equals("ae2:cable_pattern_provider")
            || machineId.equals("expatternprovider:ex_pattern_provider")
            || machineId.equals("expatternprovider:ex_pattern_provider_part")) {
            return (MachineUpgradesChanged) () -> {
                changeCallback.onUpgradesChanged();
                Holder.callback.onUpgradesChanged();
            };
        }

        return changeCallback;
    }
}
