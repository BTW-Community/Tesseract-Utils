package net.dravigen.tesseractUtils.mixin.client;

import btw.item.items.PlaceAsBlockItem;
import net.dravigen.tesseractUtils.enums.EnumShape;
import net.dravigen.tesseractUtils.packet.PacketSender;
import net.dravigen.tesseractUtils.utils.PacketUtils;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

import static net.dravigen.tesseractUtils.TesseractUtilsAddon.*;
import static net.dravigen.tesseractUtils.TesseractUtilsAddon.checkedOP;
import static net.dravigen.tesseractUtils.configs.BlockSelectionManager.*;
import static net.dravigen.tesseractUtils.enums.EnumBuildMode.*;
import static net.dravigen.tesseractUtils.utils.RayTracingUtils.*;
import static net.dravigen.tesseractUtils.enums.EnumConfig.*;

@Mixin(EntityPlayerSP.class)
public abstract class EntityPlayerSPMixin extends AbstractClientPlayer {
    @Shadow
    protected Minecraft mc;

    public EntityPlayerSPMixin(World par1World, String par2Str) {
        super(par1World, par2Str);
    }

    @Shadow
    protected abstract boolean isBlockTranslucent(int par1, int par2, int par3);

    @Shadow
    public abstract ItemStack getHeldItem();

    @Shadow
    public abstract boolean isUsingSpecialKey();

    @Redirect(method = "pushOutOfBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerSP;isBlockTranslucent(III)Z", ordinal = 0))
    private boolean onPushOutOfBlocks(EntityPlayerSP instance, int par1, int par2, int par3) {
        if (modeState == 2 && this.mc.thePlayer.capabilities.isCreativeMode) {
            return false;
        }
        return this.isBlockTranslucent(par1, par2, par3);
    }

    @Redirect(method = "pushOutOfBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerSP;isBlockTranslucent(III)Z", ordinal = 1))
    private boolean onPushOutOfBlocks1(EntityPlayerSP instance, int par1, int par2, int par3) {
        if (modeState == 2 && this.mc.thePlayer.capabilities.isCreativeMode) {
            return false;
        }
        return this.isBlockTranslucent(par1, par2, par3);
    }

    @Unique
    private static boolean keyPressed = false;
    @Unique
    private static boolean rightClickPressed = false;
    @Unique
    private static boolean leftClickPressed = false;
    @Unique
    private static boolean middleClickPressed = false;
    @Unique
    private static boolean undoPressed = false;
    @Unique
    private static boolean redoPressed = false;
    @Unique
    private static boolean pressed = false;
    @Unique
    private List<String> shapeList = new ArrayList<>();


    @Inject(method = "onLivingUpdate", at = @At("TAIL"))
    private void update(CallbackInfo ci) {
        if (!checkedOP) {
            PacketSender.sendClientToServerMessage("isPlayerOP:" + this.getEntityName());
            modeState = this.capabilities.isCreativeMode ? 0 : 1;
            checkedOP = true;
        }
        if (this.mc.currentScreen == null) {
            ItemStack heldItem = this.getHeldItem();
            boolean isItem = false;
            int itemId = 0;
            boolean isBuildTool=false;
            if (heldItem != null) {
                itemId = heldItem.itemID;
                isItem = true;
                if (heldItem.hasTagCompound()) {
                    isBuildTool = heldItem.getTagCompound().hasKey("BuildingParams")&&currentBuildingMode==8;
                }
            }
            isBuildTool = !isBuildTool ? currentBuildingMode== SHAPE_MODE.getIndex() : isBuildTool;
            isBuildTool = mc.currentScreen == null && isBuildTool;
            if (this.capabilities.isCreativeMode && PacketUtils.isPlayerOPClient) {
                int x1 = (int) block1.xCoord;
                int y1 = (int) block1.yCoord;
                int z1 = (int) block1.zCoord;
                int x2 = (int) block2.xCoord;
                int y2 = (int) block2.yCoord;
                int z2 = (int) block2.zCoord;
                final boolean isPosValid = x1 != 9999999 && y1 != 9999999 && z1 != 9999999 && x2 != 9999999 && y2 != 9999999 && z2 != 9999999;
                int flag = NO_UPDATE.getBoolValue() ? 2 : 3;
                boolean ignoreAir = IGNORE_AIR.getBoolValue();
                boolean isSelectionMode = (isItem && itemId == Item.axeWood.itemID) || currentBuildingMode == SELECTION_MODE.getIndex() || (this.isUsingSpecialKey() && getEnumFromIndex(currentBuildingMode).getCanSelect());
                boolean isExtrudeMode = (isItem && itemId == Item.shovelWood.itemID) || currentBuildingMode == EXTRUDE_MODE.getIndex();
                boolean isDeleteBuildingMode = currentBuildingMode == DELETE_MODE.getIndex();
                boolean isCopyMode = currentBuildingMode == COPY_MODE.getIndex();
                boolean isBlockSetMode = currentBuildingMode == BLOCK_SET_MODE.getIndex();
                boolean isSetPermanentMode = isItem && itemId == Item.hoeWood.itemID;
                if (Mouse.isButtonDown(0)) {
                    pressed=true;
                    if (!leftClickPressed) {
                        if (!isBuildTool) {
                            if (isSelectionMode) {
                                BlockFromRayTrace result = getBlockFromRayTrace(this);
                                if (result.block() != null) {
                                    setBlock1(result.x(), result.y(), result.z());
                                    this.sendChatToPlayer(ChatMessageComponent.createFromText("§dFirst position set to (" + result.x() + ", " + result.y() + ", " + result.z() + ")"));
                                }
                            } else if (isExtrudeMode) {
                                BlockFromRayTrace result = getBlockFromRayTrace(this);
                                if (result.block() != null)
                                    PacketSender.sendClientToServerMessage("extrudeShrink:" + result.x() + "," + result.y() + "," + result.z() + "," + result.sideHit() + "," + FUZZY_EXTRUDER.getBoolValue() + "," + EXTRUDE_LIMIT.getIntValue());
                            } else if (isDeleteBuildingMode) {
                                BlockFromRayTrace result = getBlockFromRayTrace(this);
                                if (result.block() != null)
                                    PacketSender.sendClientToServerMessage("deleteBlocks:" + result.x() + "," + result.y() + "," + result.z() + "," + FUZZY_EXTRUDER.getBoolValue() + "," + (this.isSneaking() ? 1 : EXTRUDE_LIMIT.getIntValue()) + "," + !this.isSneaking() + "," + flag);
                            } else if (isCopyMode) {
                                if (isPosValid)
                                    if (this.isSneaking())
                                        PacketSender.sendClientToServerMessage("cut:" + x1 + "," + y1 + "," + z1 + "," + x2 + "," + y2 + "," + z2 + "," + ignoreAir + "," + flag);
                                    else
                                        PacketSender.sendClientToServerMessage("copy:" + x1 + "," + y1 + "," + z1 + "," + x2 + "," + y2 + "," + z2 + "," + ignoreAir);
                                else
                                    this.sendChatToPlayer(ChatMessageComponent.createFromText("§cYou need to select an area"));
                            } else if (isBlockSetMode) {
                                if (isPosValid)
                                        PacketSender.sendClientToServerMessage("set:" + x1 + "," + y1 + "," + z1 + "," + x2 + "," + y2 + "," + z2 + "," + flag);
                                else
                                    this.sendChatToPlayer(ChatMessageComponent.createFromText("§cYou need to select an area"));
                            }
                        }
                        leftClickPressed = true;
                    }
                    if (!isBuildTool) {
                        if (isItem && itemId == Item.swordWood.itemID) {
                            Entity entityHit = getEntityFromRayTrace(this, 256, partialTick);
                            if (this.isUsingSpecialKey()) {
                                if (entityHit != null)
                                    PacketSender.sendClientToServerMessage("killEntities:" + entityHit.posX + "," + entityHit.posY + "," + entityHit.posZ);
                                else {
                                    BlockFromRayTrace result = getBlockFromRayTrace(this);
                                    if (result.block() != null)
                                        PacketSender.sendClientToServerMessage("killEntities:" + result.x() + "," + result.y() + "," + result.z());
                                }
                            } else {
                                if (entityHit != null)
                                    PacketSender.sendClientToServerMessage("killEntity:" + entityHit.entityId);
                            }
                        }
                    }
                    else {
                        BlockFromRayTrace result = getBlockFromRayTrace(this);
                        if (result.block() != null) {
                            String pos = result.x() + "," + result.y() + "," + result.z();
                            List<String> list = new ArrayList<>();
                            if (shapeList.isEmpty()) {
                                shapeList.add(pos);
                            }else {
                                for (String s : shapeList) {
                                    if (!s.equalsIgnoreCase(pos)){
                                        list.add(pos);
                                    }
                                }
                                if (!list.isEmpty()) shapeList.add(pos);
                            }
                        }
                    }
                } else {
                    leftClickPressed=false;
                    pressed=false;
                }
                if (!pressed&&!shapeList.isEmpty()) {
                    StringBuilder finalString = new StringBuilder();
                    for (String s : shapeList) {
                        finalString.append(s).append(";");
                    }
                    String var1 = String.valueOf(finalString);
                    PacketSender.sendClientToServerMessage("useBuildToolList:" + var1 + EnumShape.getParameters() + flag + "," + (currentBuildingMode == SHAPE_MODE.getIndex()));
                    shapeList.clear();
                }

                if (Mouse.isButtonDown(1)) {
                    if (!rightClickPressed) {
                        if (!isBuildTool) {
                            if (isSelectionMode) {
                                BlockFromRayTrace result = getBlockFromRayTrace(this);
                                if (result.block() != null) {
                                    setBlock2(result.x(), result.y(), result.z());
                                    this.sendChatToPlayer(ChatMessageComponent.createFromText("§dSecond position set to (" + result.x() + ", " + result.y() + ", " + result.z() + ")"));
                                } else if (this.isSneaking()) {
                                    clear();
                                    this.sendChatToPlayer(ChatMessageComponent.createFromText("§dSelections cleared"));
                                }
                            } else if (isExtrudeMode) {
                                BlockFromRayTrace result = getBlockFromRayTrace(this);
                                if (result.block() != null)
                                    PacketSender.sendClientToServerMessage("extrudeExpand:" + result.x() + "," + result.y() + "," + result.z() + "," + result.sideHit() + "," + FUZZY_EXTRUDER.getBoolValue() + "," + EXTRUDE_LIMIT.getIntValue());
                            } else {
                                if (isSetPermanentMode) {
                                    Entity entityHit = getEntityFromRayTrace(this, 256, partialTick);
                                    if (entityHit instanceof EntityLiving living)
                                        PacketSender.sendClientToServerMessage("updatePermanentMob:" + entityHit.entityId);
                                } else if (!this.isSneaking() && !this.isUsingSpecialKey() && isItem && heldItem.getItem() instanceof PlaceAsBlockItem && (currentBuildingMode == REPLACE_MODE.getIndex())) {
                                    BlockFromRayTrace result = getBlockFromRayTrace(this);
                                    if (result.block() != null) {
                                        Vec3 direction = result.hitVec3();
                                        float clickX = (float) direction.xCoord - (float) result.x();
                                        float clickY = (float) direction.yCoord - (float) result.y();
                                        float clickZ = (float) direction.zCoord - (float) result.z();
                                        PacketSender.sendClientToServerMessage("replace:" + result.x() + "," + result.y() + "," + result.z() + "," + clickX + "," + clickY + "," + clickZ + "," + result.sideHit() + "," + flag);
                                    }
                                } else if (isCopyMode) {
                                    if (isPosValid)
                                        PacketSender.sendClientToServerMessage("paste:" + x1 + "," + y1 + "," + z1 + "," + x2 + "," + y2 + "," + z2 + "," + ignoreAir + "," + flag);
                                    else
                                        this.sendChatToPlayer(ChatMessageComponent.createFromText("§cYou need to select an area"));
                                } else if (isBlockSetMode) {
                                    if (isPosValid)
                                        PacketSender.sendClientToServerMessage("replaceArea:" + x1 + "," + y1 + "," + z1 + "," + x2 + "," + y2 + "," + z2 + "," + flag);
                                    else
                                        this.sendChatToPlayer(ChatMessageComponent.createFromText("§cYou need to select an area"));
                                }
                            }
                        }
                        else {
                            BlockFromRayTrace result = getBlockFromRayTrace(this);
                            if (result.block() != null){
                                String pos = result.x() + "," + result.y() + "," + result.z();
                                PacketSender.sendClientToServerMessage("useBuildToolList:" + pos + ";" + EnumShape.getParameters() + flag + ","  + (currentBuildingMode== SHAPE_MODE.getIndex()));
                            }
                        }
                        rightClickPressed = true;
                    }
                    if (!isBuildTool) {
                        if (this.isUsingSpecialKey() && isItem && heldItem.getItem() instanceof PlaceAsBlockItem && (currentBuildingMode == REPLACE_MODE.getIndex())) {
                            BlockFromRayTrace result = getBlockFromRayTrace(this);
                            if (result.block() != null) {
                                Vec3 direction = result.hitVec3();
                                float clickX = (float) direction.xCoord - (float) result.x();
                                float clickY = (float) direction.yCoord - (float) result.y();
                                float clickZ = (float) direction.zCoord - (float) result.z();
                                PacketSender.sendClientToServerMessage("replace:" + result.x() + "," + result.y() + "," + result.z() + "," + clickX + "," + clickY + "," + clickZ + "," + result.sideHit()+ "," + flag);
                            }
                        } else if (isItem && itemId == Item.swordWood.itemID || isDeleteBuildingMode) {
                            Entity entityHit = getEntityFromRayTrace(this, 256, partialTick);
                            if (this.isUsingSpecialKey()) {
                                if (entityHit != null)
                                    PacketSender.sendClientToServerMessage("killEntities:" + entityHit.posX + "," + entityHit.posY + "," + entityHit.posZ);
                                else {
                                    BlockFromRayTrace result = getBlockFromRayTrace(this);
                                    if (result.block() != null)
                                        PacketSender.sendClientToServerMessage("killEntities:" + result.x() + "," + result.y() + "," + result.z());
                                }
                            } else if (entityHit != null)
                                PacketSender.sendClientToServerMessage("killEntity:" + entityHit.entityId);
                        }
                    }
                } else rightClickPressed = false;
                if (Mouse.isButtonDown(2)) {
                    if (!middleClickPressed) {
                        if (getEnumFromIndex(currentBuildingMode).getCanSelect()) {
                            BlockFromRayTrace block = getBlockFromRayTrace(this);
                            if (block != null) {
                                int xDiff = (int) (block2.xCoord - block1.xCoord);
                                int yDiff = (int) (block2.yCoord - block1.yCoord);
                                int zDiff = (int) (block2.zCoord - block1.zCoord);
                                if (this.isUsingSpecialKey()) {
                                    setBlock2(block.x(), block.y(), block.z());
                                    setBlock1(block.x() - xDiff, block.y() - yDiff, block.z() - zDiff);
                                } else {
                                    setBlock1(block.x(), block.y(), block.z());
                                    setBlock2(block.x() + xDiff, block.y() + yDiff, block.z() + zDiff);
                                }
                            }
                        }
                        middleClickPressed = true;
                    }
                } else middleClickPressed = false;
                if (Keyboard.isKeyDown(UNDO_KEY.getIntValue())) {
                    if (!undoPressed){
                        PacketSender.sendClientToServerMessage("undo:"+ flag);
                        undoPressed=true;
                    }
                } else undoPressed = false;
                if (Keyboard.isKeyDown(REDO_KEY.getIntValue())) {
                    if (!redoPressed){
                        PacketSender.sendClientToServerMessage("redo:"+ flag);
                        redoPressed=true;
                    }
                } else redoPressed = false;


            }
            if (!Keyboard.isKeyDown(Keyboard.KEY_F3) && Keyboard.isKeyDown((int) BAR_SWAP_KEY.getValue())) {
                if (!keyPressed) {
                    int windowId = this.inventoryContainer.windowId;
                    PlayerControllerMP controllerMP = this.mc.playerController;
                    if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
                        for (int column = 0; column <= 8; column++) {
                            int hotbarSlot = 9 + column;
                            int row1Slot = 18 + column;
                            int row2Slot = 27 + column;
                            int row3Slot = 36 + column;
                            controllerMP.windowClick(windowId, row1Slot, 0, 0, this);
                            controllerMP.windowClick(windowId, row2Slot, 0, 0, this);
                            controllerMP.windowClick(windowId, row3Slot, 0, 0, this);
                            controllerMP.windowClick(windowId, hotbarSlot, 0, 0, this);
                            controllerMP.windowClick(windowId, row1Slot, 0, 0, this);
                        }
                    } else {
                        int current = this.inventory.currentItem;
                        int hotbarSlot = 9 + current;
                        int row1Slot = 18 + current;
                        int row2Slot = 27 + current;
                        int row3Slot = 36 + current;
                        controllerMP.windowClick(windowId, row1Slot, 0, 0, this);
                        controllerMP.windowClick(windowId, row2Slot, 0, 0, this);
                        controllerMP.windowClick(windowId, row3Slot, 0, 0, this);
                        controllerMP.windowClick(windowId, hotbarSlot, 0, 0, this);
                        controllerMP.windowClick(windowId, row1Slot, 0, 0, this);
                    }
                    keyPressed = true;
                }
            } else keyPressed = false;
        }
    }
}
