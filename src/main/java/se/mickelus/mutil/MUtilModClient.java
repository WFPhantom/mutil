package se.mickelus.mutil;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(value = MUtilMod.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = MUtilMod.MOD_ID, value = Dist.CLIENT)
public class MUtilModClient {
    public MUtilModClient(ModContainer modContainer) {
        ConfigHandler.setup(modContainer);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        Perks.init(Minecraft.getInstance().getUser().getProfileId().toString());
    }
}