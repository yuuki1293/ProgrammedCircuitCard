package yuuki1293.pccard;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;

public class CompetitionFixer {
    private static Boolean eIPPCache = null;

    public static boolean hasPatternProviderUpgrade() {
        if (eIPPCache != null) return eIPPCache;

        ModList modList = ModList.get();

        eIPPCache = modList.getMods().stream()
            .map(IModInfo::getModId)
            .anyMatch(id -> id.equals("appflux")); // detect Applied Flux

        return eIPPCache;
    }
}
