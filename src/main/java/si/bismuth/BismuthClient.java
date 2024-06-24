package si.bismuth;

import net.minecraft.client.Minecraft;
import net.minecraft.client.options.KeyBinding;
import net.ornithemc.osl.entrypoints.api.client.ClientModInitializer;
import net.ornithemc.osl.keybinds.api.KeyBindingEvents;
import net.ornithemc.osl.lifecycle.api.client.MinecraftClientEvents;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import si.bismuth.network.client.ClientNetworking;

public class BismuthClient implements ClientModInitializer {
    public static final Logger log = LogManager.getLogger("BismuthClient");
    public static final KeyBinding sortInventory = new KeyBinding("SortInventory", Keyboard.KEY_NONE, "BismuthClient");
    public static final KeyBinding sortContainer = new KeyBinding("SortContainer", Keyboard.KEY_NONE, "BismuthClient");
    public static final KeyBinding getinv = new KeyBinding("GetInventory", Keyboard.KEY_NONE, "BismuthClient");
    public static final KeyBinding finditem = new KeyBinding("FindItem", Keyboard.KEY_NONE, "BismuthClient");
    public static final ClientNetworking networking = new ClientNetworking();
    public static Minecraft minecraft;
    @Override
    public void initClient() {
        MinecraftClientEvents.START.register(BismuthClient::init);
        MinecraftClientEvents.STOP.register(BismuthClient::stop);
        KeyBindingEvents.REGISTER_KEYBINDS.register(registry -> {
            registry.register(sortInventory);
            registry.register(sortContainer);
            registry.register(getinv);
            registry.register(finditem);
        });
    }

    public static void init(Minecraft minecraft) {
        BismuthClient.minecraft = minecraft;
    }

    public static void stop(Minecraft minecraft) {
        BismuthClient.minecraft = null;
    }
}
