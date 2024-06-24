package si.bismuth.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tessellator;
import net.minecraft.client.entity.particle.SmokeParticle;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class ParticleShowItems extends SmokeParticle {
    public ParticleShowItems(World world, double x, double y, double z, double dX, double dY, double dZ, float scale) {
        super(world, x, y, z, dX, dY, dZ, scale);
        this.lifetime *= 7;
    }

    @Override
    public void tick() {
        super.tick();
        this.velocityY = 0D;
    }

    @Override
    public void render(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        final Tessellator tessellator = Tessellator.getInstance();
        tessellator.end();
        GlStateManager.disableDepthTest();
        GlStateManager.color3f(1F, 1F, 1F);
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormat.PARTICLE);
        super.render(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
        tessellator.end();
        GlStateManager.enableDepthTest();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormat.PARTICLE);
    }
}
