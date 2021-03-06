package com.parzivail.swg.render.worldext;

import com.parzivail.swg.proxy.Client;
import com.parzivail.util.ui.Fx;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderExtHealthBar
{
	public static void render(EntityLivingBase e)
	{
		Minecraft m = Minecraft.getMinecraft();

		/*
			Setup
		 */
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glPushAttrib(GL11.GL_LINE_BIT);

		GL.PushMatrix();
		m.entityRenderer.disableLightmap(0);

		GL.Disable(EnableCap.Lighting);
		GL.Disable(EnableCap.Blend);
		GL.Disable(EnableCap.Texture2D);

		Vec3 playerPos = m.renderViewEntity.getPosition(Client.renderPartialTicks);
		GL.Translate(e.posX - playerPos.xCoord, e.posY - playerPos.yCoord, e.posZ - playerPos.zCoord);

		/*
			2D info render
		 */
		GL11.glRotatef(-RenderManager.instance.playerViewY, 0, 1, 0);
		GL11.glLineWidth(2);

		float health = e.getHealth() / e.getMaxHealth();

		if (health >= 0.3f)
			GL11.glColor4f(1, 1, 1, 1);
		else
			GL11.glColor4f(1, 0, 0, 1);
		Fx.D2.DrawSolidRectangle(-e.width / 4, e.height + 0.2f, (e.width / 2) * health, 0.1f);

		GL11.glColor4f(0, 0, 0, 1);
		Fx.D2.DrawWireRectangle(-e.width / 4, e.height + 0.2f, e.width / 2, 0.1f);

		GL.PopMatrix();
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glPopAttrib();
		GL11.glPopAttrib();
		m.entityRenderer.enableLightmap(0);
	}
}
