package dev.nikipro50.tabscroller.mixin;

import dev.nikipro50.tabscroller.config.manager.ConfigManager;
import dev.nikipro50.tabscroller.storage.LocalStorage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class ScrollMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void tabScroller$onScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (client.player == null) return;
        if (!ConfigManager.CONFIG.scrollMouse) return;
        if (!client.options.playerListKey.isPressed()) return;
        if (client.getNetworkHandler() == null) return;

        int total = client.getNetworkHandler().getPlayerList().size();
        int maxPages = Math.max(1, (int) Math.ceil(Math.max(0, total - 80) / 20.0));

        if (vertical > 0) {
            if (ConfigManager.CONFIG.wrapNavigation) {
                LocalStorage.TAB_PAGE =
                        (LocalStorage.TAB_PAGE - 1 + maxPages) % maxPages;
            } else {
                LocalStorage.TAB_PAGE = Math.max(0, LocalStorage.TAB_PAGE - 1);
            }
        }

        if (vertical < 0) {
            if (ConfigManager.CONFIG.wrapNavigation) {
                LocalStorage.TAB_PAGE =
                        (LocalStorage.TAB_PAGE + 1) % maxPages;
            } else {
                LocalStorage.TAB_PAGE = Math.min(maxPages - 1, LocalStorage.TAB_PAGE + 1);
            }
        }

        ci.cancel();
    }
}
