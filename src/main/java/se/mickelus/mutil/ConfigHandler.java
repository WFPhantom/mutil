package se.mickelus.mutil;

import net.neoforged.neoforge.common.ModConfigSpec;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
class ConfigHandler {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue QUERY_PERKS = BUILDER
            .comment("Controls if perks data should be queried on startup")
            .define("query_perks", true);

    static final ModConfigSpec SPEC = BUILDER.build();
}