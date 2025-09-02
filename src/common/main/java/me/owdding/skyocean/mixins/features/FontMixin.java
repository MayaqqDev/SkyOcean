package me.owdding.skyocean.mixins.features;

import com.mojang.blaze3d.font.GlyphInfo;
import me.owdding.skyocean.SkyOcean;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Mixin(targets = "net.minecraft.client.gui.Font$PreparedTextBuilder")
public abstract class FontMixin {

    @Shadow
    abstract void addGlyph(BakedGlyph.GlyphInstance glyph);
    @Shadow
    float x;
    @Shadow
    float y;
    @Shadow
    abstract int getTextColor(@Nullable TextColor textColor);
    @Shadow
    abstract int getShadowColor(Style style, int textColor);
    @Shadow @Final
    Font this$0;

    @Inject(
        method = "Lnet/minecraft/client/gui/Font$PreparedTextBuilder;accept(ILnet/minecraft/network/chat/Style;I)Z",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font$PreparedTextBuilder;addGlyph(Lnet/minecraft/client/gui/font/glyphs/BakedGlyph$GlyphInstance;)V")
    )
    private void onAddGlyph(int index, Style style, int codepoint, CallbackInfoReturnable<Boolean> info) {
        int shadowColor = getShadowColor(style, getTextColor(style.getColor()));
        FontSet shadowFontSet = this$0.getFontSet(SkyOcean.INSTANCE.id("full_shadow"));
        GlyphInfo glyphInfo = shadowFontSet.getGlyphInfo(codepoint, this$0.filterFishyGlyphs);
        this.addGlyph(new BakedGlyph.GlyphInstance(
                this.x - 1,
                this.y - 1,
                shadowColor,
                0xFFFFFF,
                shadowFontSet.getGlyph(codepoint),
                style,
                style.isBold() ? glyphInfo.getBoldOffset() : 0.0F,
                0.0F
            )
        );
    }
}
