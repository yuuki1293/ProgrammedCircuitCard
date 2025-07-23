package yuuki1293.pccard.xmod;

import com.google.common.base.Suppliers;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;

import java.util.function.Supplier;

public class CompetitionFixer {
    public static Supplier<Boolean> existAppflux = Suppliers.memoize(CompetitionFixer::hasPatternProviderUpgrade);

    private static boolean hasPatternProviderUpgrade() {
        ModList modList = ModList.get();

        return modList.getMods().stream()
            .map(IModInfo::getModId)
            .anyMatch(id -> id.equals("appflux")); // detect Applied Flux
    }
}
