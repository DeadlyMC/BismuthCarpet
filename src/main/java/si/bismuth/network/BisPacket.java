package si.bismuth.network;

import net.ornithemc.osl.networking.api.CustomPayload;

public interface BisPacket extends CustomPayload {
	String getChannel();
}
