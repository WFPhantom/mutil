package se.mickelus.mutil;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = MUtilMod.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = MUtilMod.MOD_ID, value = Dist.CLIENT)
public class MUtilModClient {
    public MUtilModClient(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, ConfigHandler.SPEC);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        Perks.init(Minecraft.getInstance().getUser().getProfileId().toString());
    }
}
