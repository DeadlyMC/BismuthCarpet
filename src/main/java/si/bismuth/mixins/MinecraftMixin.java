package si.bismuth.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.menu.InventoryMenuScreen;
import net.minecraft.inventory.slot.InventorySlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.HitResult;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import si.bismuth.BismuthClient;
import si.bismuth.network.server.QueryInventoryPacket;
import si.bismuth.network.server.FindItemPacket;
import si.bismuth.network.server.SortPacket;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow
    public HitResult crosshairTarget;

    @Inject(method = "handleGuiKeyBindings", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;getEventKeyState()Z", remap = false), locals = LocalCapture.CAPTURE_FAILHARD)
    private void sortInventoryPlease(CallbackInfo ci, int i) {
        if (!Keyboard.getEventKeyState()) {
            return;
        }

        if (i == BismuthClient.sortInventory.getKeyCode()) {
            BismuthClient.networking.sendPacket(new SortPacket(true));
        } else if (i == BismuthClient.sortContainer.getKeyCode()) {
            BismuthClient.networking.sendPacket(new SortPacket(false));
        } else if (i == BismuthClient.getinv.getKeyCode()) {
            BismuthClient.networking.sendPacket(new QueryInventoryPacket(this.crosshairTarget.getPos()));
        } else if (i == BismuthClient.finditem.getKeyCode()) {
            final Screen screen = Minecraft.getInstance().screen;
            if (screen instanceof InventoryMenuScreen) {
                final InventoryMenuScreen container = (InventoryMenuScreen) screen;
                final InventorySlot mouse = ((IInventoryMenuScreenMixin) container).getHoveredSlot();
                if (mouse != null) {
                    final ItemStack stack = mouse.getStack();
                    if (!stack.isEmpty()) {
                        BismuthClient.networking.sendPacket(new FindItemPacket(stack));
                    }
                }
            }
        }
    }
}
