package yuuki1293.pccard.mixins;

import appeng.api.networking.IManagedGridNode;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yuuki1293.pccard.MachineTypeHolder;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Mixin(value = PatternProviderLogic.class, remap = false)
public abstract class PatternProviderLogicMixin implements IUpgradeableObject {
    @Unique
    private static Logger programmedCircuitCard$LOGGER = LogUtils.getLogger();

    @Unique
    private IUpgradeInventory programmedCircuitCard$upgrades;

    @Inject(method = "<init>(Lappeng/api/networking/IManagedGridNode;Lappeng/helpers/patternprovider/PatternProviderLogicHost;I)V", at = @At("RETURN"))
    private void init(IManagedGridNode mainNode, PatternProviderLogicHost host, int patternInventorySize, CallbackInfo ci) {
        try {
            var onUpgradesChanged = this.getClass().getDeclaredMethod("onUpgradesChanged");
            onUpgradesChanged.setAccessible(true);

            programmedCircuitCard$upgrades = UpgradeInventories.forMachine(MachineTypeHolder.MACHINE_TYPE, 1, () -> {
                try {
                    onUpgradesChanged.invoke(this);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    programmedCircuitCard$LOGGER.error("Can't invoke method", e);
                }
            });
        } catch (NoSuchMethodException e) {
            programmedCircuitCard$LOGGER.error("Can't find method", e);
        }
    }

    @Inject(method = "writeToNBT", at = @At("HEAD"))
    private void writeToNBT(CompoundTag tag, CallbackInfo ci) {
        this.programmedCircuitCard$upgrades.writeToNBT(tag, "upgrades");
    }

    @Inject(method = "readFromNBT", at = @At("HEAD"))
    private void readFromNBT(CompoundTag tag, CallbackInfo ci) {
        this.programmedCircuitCard$upgrades.readFromNBT(tag, "upgrades");
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return programmedCircuitCard$upgrades;
    }

    @Inject(method = "addDrops", at = @At("HEAD"))
    public void addDrops(List<ItemStack> drops, CallbackInfo ci) {
        for (var is : this.programmedCircuitCard$upgrades) {
            if (!is.isEmpty()) {
                drops.add(is);
            }
        }
    }

    @Inject(method = "clearContent", at = @At("HEAD"))
    public void clearContent(CallbackInfo ci) {
        this.programmedCircuitCard$upgrades.clear();
    }
}
