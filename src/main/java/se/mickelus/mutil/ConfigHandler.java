package se.mickelus.mutil;


import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.ModConfigSpec;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
class ConfigHandler {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue queryPerks = BUILDER
            .comment("Controls if perks data should be queried on startup")
            .define("query_perks", true);

    static final ModConfigSpec CLIENT_SPEC = BUILDER.build();

    public static void setup(ModContainer modContainer) {
        if (FMLEnvironment.getDist().isClient()) {
            modContainer.registerConfig(ModConfig.Type.CLIENT, CLIENT_SPEC);
        }
    }
}