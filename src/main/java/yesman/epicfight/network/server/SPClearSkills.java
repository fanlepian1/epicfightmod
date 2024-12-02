package yesman.epicfight.network.server;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

public class SPClearSkills {
	public static SPClearSkills fromBytes(PacketBuffer buf) {
		return new SPClearSkills();
	}
	
	public static void toBytes(SPClearSkills msg, PacketBuffer buf) {
		
	}
	
	public static void handle(SPClearSkills msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Minecraft mc = Minecraft.getInstance();
			LocalPlayerPatch playerpatch = (LocalPlayerPatch) mc.player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (playerpatch != null) {
				playerpatch.getSkillCapability().clear();
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
