package de.linusdev.mixin;

import de.linusdev.SodiumCoreShaderSupport;
import net.caffeinemc.mods.sodium.client.gl.shader.GlShader;
import net.caffeinemc.mods.sodium.client.gl.shader.ShaderConstants;
import net.caffeinemc.mods.sodium.client.gl.shader.ShaderLoader;
import net.caffeinemc.mods.sodium.client.gl.shader.ShaderType;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Mixin(ShaderLoader.class)
public class MixinShaderLoader {

    @Inject(at = @At("HEAD"), method = "loadShader")
    private static void loadShaderInject(
            ShaderType type, Identifier name, ShaderConstants constants, CallbackInfoReturnable<GlShader> cir
    ) {
        SodiumCoreShaderSupport.LOGGER.info("Start loading shader in namespace '"  + name.getNamespace() + "': " + name.getPath());
    }

    /**
     * @author LinusDev
     * @reason Load shaders from resources, loaded by then ResourceManager instead of reading them as java resource.
     */
    @Overwrite
    public static String getShaderSource(Identifier name) {
        var nameSpace = SodiumCoreShaderSupport.shaders.get(name.getNamespace());

        if(nameSpace == null)
            throw new RuntimeException("No Shaders available for namespace '" + name.getNamespace() + "'");

        var shaderResource = nameSpace.get(name.getPath());

        if(shaderResource == null)
            throw new RuntimeException("No Shader found in namespace '" + name.getNamespace()
                    + "' for shader '" + name.getPath() + "'");

        try {
            return IOUtils.toString(shaderResource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Exception while reading shader source in namespace '" + name.getNamespace()
                    + "' for shader '" + name.getPath() + "'", e);
        }
    }

}