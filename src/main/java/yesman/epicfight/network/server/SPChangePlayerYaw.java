package yesman.epicfight.network.server;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class SPChangePlayerYaw {
	private int entityId;
	private float yaw;

	public SPChangePlayerYaw() {
		this.entityId = 0;
		this.yaw = 0;
	}

	public SPChangePlayerYaw(int entityId, float yaw) {
		this.entityId = entityId;
		this.yaw = yaw;
	}

	public static SPChangePlayerYaw fromBytes(PacketBuffer buf) {
		return new SPChangePlayerYaw(buf.readInt(), buf.readFloat());
	}

	public static void toBytes(SPChangePlayerYaw msg, PacketBuffer buf) {
		buf.writeInt(msg.entityId);
		buf.writeFloat(msg.yaw);
	}
	
	public static void handle(SPChangePlayerYaw msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Minecraft mc = Minecraft.getInstance();
			Entity entity = mc.player.level.getEntity(msg.entityId);
			if (entity != null) {
				PlayerPatch<?> playerpatch = (PlayerPatch<?>) entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				if (playerpatch != null) {
					playerpatch.changeYaw(msg.yaw);
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}