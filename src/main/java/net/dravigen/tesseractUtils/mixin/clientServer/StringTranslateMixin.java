package net.dravigen.tesseractUtils.mixin.clientServer;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import net.dravigen.tesseractUtils.utils.ListsUtils;
import net.minecraft.src.StringTranslate;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

@Mixin(StringTranslate.class)
public class StringTranslateMixin {


    @Shadow @Final private static Pattern field_111053_a;

    @Shadow @Final private static Splitter field_135065_b;

    @Inject(method = "<init>",at = @At("TAIL"))
    private void addBTWTranslation(CallbackInfo ci){
        try {
            InputStream var1 = StringTranslate.class.getResourceAsStream("/assets/btw/lang/en_US.lang");
            assert var1 != null;
            for (String var3 : IOUtils.readLines(var1, Charsets.UTF_8)) {
                String[] var4;
                if (var3.isEmpty() || var3.charAt(0) == '#' || (var4 = Iterables.toArray(field_135065_b.split(var3), String.class)) == null || var4.length != 2) continue;
                String var5 = var4[0];
                String var6 = field_111053_a.matcher(var4[1]).replaceAll("%$1s");
                ListsUtils.englishLanguage.put(var5, var6);
            }
        } catch (IOException iOException) {
            // empty catch block
        }
        try {
            InputStream var1 = StringTranslate.class.getResourceAsStream("/assets/minecraft/lang/en_US.lang");
            assert var1 != null;
            for (String var3 : IOUtils.readLines(var1, Charsets.UTF_8)) {
                String[] var4;
                if (var3.isEmpty() || var3.charAt(0) == '#' || (var4 = Iterables.toArray(field_135065_b.split(var3), String.class)) == null || var4.length != 2) continue;
                String var5 = var4[0];
                String var6 = field_111053_a.matcher(var4[1]).replaceAll("%$1s");
                ListsUtils.englishLanguage.put(var5, var6);
            }
        } catch (IOException iOException) {
            // empty catch block
        }
    }

}
