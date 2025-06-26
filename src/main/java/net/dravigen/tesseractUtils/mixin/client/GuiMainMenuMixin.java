package net.dravigen.tesseractUtils.mixin.client;

import net.dravigen.tesseractUtils.GUI.GuiButtonCustom;
import net.dravigen.tesseractUtils.GUI.GuiConfigSettingsScreen;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMainMenu.class)
public abstract class GuiMainMenuMixin extends GuiScreen {
    @Unique
    private static final int TESSERACT_SETTINGS_BUTTON_ID = 450;

    @Inject(method = "initGui", at = @At("RETURN"))
    private void addCustomButton(CallbackInfo ci) {
        this.buttonList.add(new GuiButtonCustom(TESSERACT_SETTINGS_BUTTON_ID, this.width / 2 + 2 + 98 + 4,  this.height / 4 + 48 + 72 + 12, 20, 20,20,20,"" ,new ResourceLocation("tesseract_utils:textures/gui/tesseract.png")));
    }
    @Inject(method = "actionPerformed", at = @At("HEAD"))
    private void onActionPerformed(GuiButton button, CallbackInfo ci) {
        if (button.id == TESSERACT_SETTINGS_BUTTON_ID) {
            this.mc.displayGuiScreen(new GuiConfigSettingsScreen(this));
        }
    }
}