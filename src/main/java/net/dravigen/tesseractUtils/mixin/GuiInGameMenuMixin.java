package net.dravigen.tesseractUtils.mixin;

import net.dravigen.tesseractUtils.GUI.GuiButtonCustom;
import net.dravigen.tesseractUtils.GUI.GuiTUSettings;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngameMenu.class)
public abstract class GuiInGameMenuMixin extends GuiScreen {
    @Unique
    private static final int TESSERACT_SETTINGS_BUTTON_ID = 451;

    @Inject(method = "initGui", at = @At("RETURN"))
    private void addCustomButton(CallbackInfo ci) {
        if (MinecraftServer.getServer().getConfigurationManager().isPlayerOpped(this.mc.thePlayer.getCommandSenderName())) {
            this.buttonList.add(new GuiButtonCustom(TESSERACT_SETTINGS_BUTTON_ID, this.width / 2 + 100 + 4, this.height / 4 + 80, 20, 20, 20, "", new ResourceLocation("tesseract_utils:textures/gui/tesseract.png")));
        }
    }
    @Inject(method = "actionPerformed", at = @At("HEAD"))
    private void onActionPerformed(GuiButton button, CallbackInfo ci) {
        if (button.id == TESSERACT_SETTINGS_BUTTON_ID) {
            this.mc.displayGuiScreen(new GuiTUSettings(this));
        }
    }
}