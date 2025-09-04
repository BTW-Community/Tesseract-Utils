package net.dravigen.tesseractUtils.mixin.client;

import com.prupe.mcpatcher.cc.ColorizeWorld;
import com.prupe.mcpatcher.cc.Colorizer;
import net.dravigen.tesseractUtils.utils.PacketUtils;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dravigen.tesseractUtils.enums.EnumConfig.*;

import java.util.Arrays;
@Mixin(value = EntityRenderer.class,priority = 1001)
public abstract class EntityRendererMixin {
    @Shadow private Minecraft mc;

    @Shadow
    float fogColorBlue;

    @Shadow
    float fogColorGreen;

    @Shadow
    float fogColorRed;

    @Shadow private float witherEffectIntensity;

    @Shadow private float previousWithEffectIntensity;

    @Shadow private float fogColor1;

    @Shadow private float fogColor2;

    @Shadow private boolean cloudFog;

    @Shadow @Final private DynamicTexture lightmapTexture;

    @Inject(method = "updateFogColor",at = @At("TAIL"))
    private void vanillaNightVis(float par1, CallbackInfo ci){
        if (this.mc.thePlayer.capabilities.isCreativeMode&& PacketUtils.isPlayerOPClient &&this.mc.thePlayer.isPotionActive(Potion.nightVision)&&(boolean) VANILLA_NIGHTVIS.getValue()) {

            float var17;
            float var22;
            float var11;
            WorldClient var2 = this.mc.theWorld;
            EntityLivingBase var3 = this.mc.renderViewEntity;
            float var4 = 1.0f / (float) (4 - this.mc.gameSettings.renderDistance);
            var4 = 1.0f - (float) Math.pow(var4, 0.25);
            Vec3 var5 = var2.getSkyColor(this.mc.renderViewEntity, par1);
            float var6 = (float) var5.xCoord;
            float var7 = (float) var5.yCoord;
            float var8 = (float) var5.zCoord;
            Vec3 var9 = var2.getFogColor(par1);
            this.fogColorRed = (float) var9.xCoord;
            this.fogColorGreen = (float) var9.yCoord;
            this.fogColorBlue = (float) var9.zCoord;
            if (this.mc.gameSettings.renderDistance < 2) {
                float[] var12;
                Vec3 var10 = MathHelper.sin(var2.getCelestialAngleRadians(par1)) > 0.0f ? var2.getWorldVec3Pool().getVecFromPool(-1.0, 0.0, 0.0) : var2.getWorldVec3Pool().getVecFromPool(1.0, 0.0, 0.0);
                var11 = (float) var3.getLook(par1).dotProduct(var10);
                if (var11 < 0.0f) {
                    var11 = 0.0f;
                }
                if (var11 > 0.0f && (var12 = var2.provider.calcSunriseSunsetColors(var2.getCelestialAngle(par1), par1)) != null) {
                    this.fogColorRed = this.fogColorRed * (1.0f - (var11 *= var12[3])) + var12[0] * var11;
                    this.fogColorGreen = this.fogColorGreen * (1.0f - var11) + var12[1] * var11;
                    this.fogColorBlue = this.fogColorBlue * (1.0f - var11) + var12[2] * var11;
                }
            }
            this.fogColorRed += (var6 - this.fogColorRed) * var4;
            this.fogColorGreen += (var7 - this.fogColorGreen) * var4;
            this.fogColorBlue += (var8 - this.fogColorBlue) * var4;
            float var19 = var2.getRainStrength(par1);
            if (var19 > 0.0f) {
                var11 = 1.0f - var19 * 0.5f;
                float var20 = 1.0f - var19 * 0.4f;
                this.fogColorRed *= var11;
                this.fogColorGreen *= var11;
                this.fogColorBlue *= var20;
            }
            if ((var11 = var2.getWeightedThunderStrength(par1)) > 0.0f) {
                float var20 = 1.0f - var11 * 0.5f;
                this.fogColorRed *= var20;
                this.fogColorGreen *= var20;
                this.fogColorBlue *= var20;
            }
            int var21 = ActiveRenderInfo.getBlockIdAtEntityViewpoint(this.mc.theWorld, var3, par1);
            if (this.cloudFog) {
                Vec3 var13 = var2.getCloudColour(par1);
                this.fogColorRed = (float) var13.xCoord;
                this.fogColorGreen = (float) var13.yCoord;
                this.fogColorBlue = (float) var13.zCoord;
            } else if (var21 != 0 && Block.blocksList[var21].blockMaterial == Material.water) {
                var22 = Math.min((float) EnchantmentHelper.getRespiration(var3), 3.0f) * 0.2f;
                this.fogColorRed = 0.02f + var22;
                this.fogColorGreen = 0.02f + var22;
                this.fogColorBlue = 0.2f + var22;
                if (ColorizeWorld.computeUnderwaterColor()) {
                    this.fogColorRed = Colorizer.setColor[0];
                    this.fogColorGreen = Colorizer.setColor[1];
                    this.fogColorBlue = Colorizer.setColor[2];
                }
            } else if (var21 != 0 && Block.blocksList[var21].blockMaterial == Material.lava) {
                this.fogColorRed = 0.6f;
                this.fogColorGreen = 0.1f;
                this.fogColorBlue = 0.0f;
                if (ColorizeWorld.computeUnderlavaColor()) {
                    this.fogColorRed = Colorizer.setColor[0];
                    this.fogColorGreen = Colorizer.setColor[1];
                    this.fogColorBlue = Colorizer.setColor[2];
                }
            }
            var22 = this.fogColor2 + (this.fogColor1 - this.fogColor2) * par1;
            this.fogColorRed *= var22;
            this.fogColorGreen *= var22;
            this.fogColorBlue *= var22;
            double var14 = (var3.lastTickPosY + (var3.posY - var3.lastTickPosY) * (double) par1) * var2.provider.getVoidFogYFactor();
            if (var3.isPotionActive(Potion.blindness)) {
                int var16 = var3.getActivePotionEffect(Potion.blindness).getDuration();
                var14 = var16 < 20 ? var14 * (1.0f - (float) var16 / 20.0f) : 0.0;
            } else if (this.mc.gameSettings.thirdPersonView == 0 && var3.hasHeadCrabbedSquid()) {
                var14 = 0.2;
            }
            if (var14 < 1.0) {
                if (var14 < 0.0) {
                    var14 = 0.0;
                }
                var14 *= var14;
                this.fogColorRed = (float) ((double) this.fogColorRed * var14);
                this.fogColorGreen = (float) ((double) this.fogColorGreen * var14);
                this.fogColorBlue = (float) ((double) this.fogColorBlue * var14);
            }
            if (this.witherEffectIntensity > 0.0f) {
                float var23 = this.previousWithEffectIntensity + (this.witherEffectIntensity - this.previousWithEffectIntensity) * par1;
                this.fogColorRed = this.fogColorRed * (1.0f - var23) + this.fogColorRed * 0.7f * var23;
                this.fogColorGreen = this.fogColorGreen * (1.0f - var23) + this.fogColorGreen * 0.6f * var23;
                this.fogColorBlue = this.fogColorBlue * (1.0f - var23) + this.fogColorBlue * 0.6f * var23;
            }
            if (this.mc.gameSettings.anaglyph) {
                float var23 = (this.fogColorRed * 30.0f + this.fogColorGreen * 59.0f + this.fogColorBlue * 11.0f) / 100.0f;
                var17 = (this.fogColorRed * 30.0f + this.fogColorGreen * 70.0f) / 100.0f;
                float var18 = (this.fogColorRed * 30.0f + this.fogColorBlue * 70.0f) / 100.0f;
                this.fogColorRed = var23;
                this.fogColorGreen = var17;
                this.fogColorBlue = var18;
            }
            GL11.glClearColor(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 0.0f);
        }
    }

/*
    @Redirect(method = "updateFogColor",at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityLivingBase;isPotionActive(Lnet/minecraft/src/Potion;)Z"))
    private boolean vanillaNV(EntityLivingBase instance, Potion par1Potion){
        if (this.mc.thePlayer.capabilities.isCreativeMode&&(boolean) VANILLA_NIGHTVIS.getValue()&& PacketUtils.isPlayerOPClient){
            return false;
        }else return instance.isPotionActive(par1Potion);
    }*/
    /*@ModifyArg(method = "modUpdateLightmapOverworld", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/TextureUtil;uploadTexture(I[III)V"),index = 1)
    private int[] vanillaNVColor(int[] par1ArrayOfInteger) {
        if (this.mc.thePlayer.capabilities.isCreativeMode&& PacketUtils.isPlayerOPClient &&this.mc.thePlayer.isPotionActive(Potion.nightVision)&&(boolean) VANILLA_NIGHTVIS.getValue()) {
            int[] numbers = new int[256];
            Arrays.fill(numbers, -1);
            return numbers;
        }
        return par1ArrayOfInteger;
    }*/

    @Inject(method = "modUpdateLightmapOverworld",at = @At("TAIL"))
    private void setNewLightmap(WorldClient world, float fPartialTicks, CallbackInfo ci){
        if (this.mc.thePlayer.capabilities.isCreativeMode&& PacketUtils.isPlayerOPClient &&this.mc.thePlayer.isPotionActive(Potion.nightVision)&&(boolean) VANILLA_NIGHTVIS.getValue()) {
            int[] numbers = new int[256];
            Arrays.fill(numbers, -1);
            TextureUtil.uploadTexture(this.lightmapTexture.getGlTextureId(), numbers, 16, 16);
        }
    }
}