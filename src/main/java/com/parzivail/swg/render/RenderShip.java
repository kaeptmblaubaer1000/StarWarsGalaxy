package com.parzivail.swg.render;

import com.parzivail.swg.entity.EntityShip;
import com.parzivail.util.math.RotatedAxes;
import com.parzivail.util.ui.Fx;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import com.parzivail.util.ui.gltk.PrimitiveType;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

/**
 * Created by colby on 12/26/2017.
 */
public class RenderShip extends Render<EntityShip>
{
	public RenderShip(RenderManager renderManagerIn)
	{
		super(renderManagerIn);
		this.shadowSize = 0.5F;
	}

	@Override
	public void doRender(EntityShip ship, double x, double y, double z, float entityYaw, float partialTicks)
	{
		GL11.glPushMatrix();
		GL.PushAttrib(AttribMask.EnableBit);
		GL11.glShadeModel(GL11.GL_SMOOTH);

		RotatedAxes rotation = ship.getRotation();
		float yaw = MathHelper.wrapDegrees(rotation.getYaw());
		float pitch = wrapAngleTo90_float(rotation.getPitch());
		float roll = MathHelper.wrapDegrees(rotation.getRoll());

		GL.Translate(x, y, z);

		GL.Enable(EnableCap.Texture2D);

		// TODO: fix
		//FxMC.enableSunBasedLighting(ship, partialTicks);

		GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-pitch, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(-roll, 0.0F, 0.0F, 1.0F);

		GL.Rotate(-90, 1, 0, 0);
		GL.Rotate(-90, 0, 0, 1);

		GL.Disable(EnableCap.Texture2D);
		Fx.D3.DrawSolidBox();

		GL.Disable(EnableCap.Lighting);
		GL.PushMatrix();
		GL.Scale(0.25f);
		GL11.glLineWidth(2);
		GL.Color(1f, 0, 0);
		GL.Begin(PrimitiveType.LineStrip);
		GL.Vertex3(0.0D, 0.0D, 0.0D);
		GL.Vertex3((double)10, 0.0D, 0.0D);
		GL.End();

		GL.Color(0, 1f, 0);
		GL.Begin(PrimitiveType.LineStrip);
		GL.Vertex3(0.0D, 0.0D, 0.0D);
		GL.Vertex3(0.0D, (double)10, 0.0D);
		GL.End();

		GL.Color(0, 0, 1f);
		GL.Begin(PrimitiveType.LineStrip);
		GL.Vertex3(0.0D, 0.0D, 0.0D);
		GL.Vertex3(0.0D, 0.0D, (double)10);
		GL.End();
		GL.PopMatrix();

		GL.Color(0xFFFFFFFF);

		GL.PopAttrib();
		GL11.glPopMatrix();
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntityShip entity)
	{
		return null;
	}

	public static float wrapAngleTo90_float(float a)
	{
		a %= 180.0F;

		if (a >= 90)
			a -= 180;

		if (a < -90)
			a += 180;

		return a;
	}
}