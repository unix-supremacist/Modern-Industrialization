package aztech.modern_industrialization.compat.appeng;

import aztech.modern_industrialization.ModernIndustrialization;
import net.fabricmc.loader.api.FabricLoader;

public class AECompat {
    public static IAECompat INSTANCE = new AbsentAECompat();

    /**
     * Try to load compat
     */
    public static void tryLoad() {
        if(FabricLoader.getInstance().isModLoaded("appliedenergistics2")) {
            try {
                Class<?> presentCompat = Class.forName("aztech.modern_industrialization.compat.appeng.PresentAECompat");
                INSTANCE = (IAECompat) presentCompat.getConstructor().newInstance();
                ModernIndustrialization.LOGGER.info("Loaded Applied Energistics 2 compat!");
            } catch (Exception exception) {
                ModernIndustrialization.LOGGER.warn("Failed to load Applied Energistics 2 compat!", exception);
            }
        } else {
            ModernIndustrialization.LOGGER.info("Applied Energistics 2 is not loaded... skipping compat!");
        }
    }
}
