package net.dravigen.tesseractUtils.mixin.client;

import net.minecraft.src.GuiChat;
import net.minecraft.src.GuiTextField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiChat.class)
public class GuiChatMixin {
    @Shadow protected GuiTextField inputField;

    @Redirect(method = "initGui",at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GuiTextField;setMaxStringLength(I)V"))
    private void increaseChatLimit(GuiTextField instance, int par1){
        this.inputField.setMaxStringLength(512);
    }
}
