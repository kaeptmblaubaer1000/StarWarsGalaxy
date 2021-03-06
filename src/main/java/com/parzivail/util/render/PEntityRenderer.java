package com.parzivail.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;

/**
 * Created by colby on 1/6/2018.
 */
public class PEntityRenderer extends EntityRenderer
{
	private EntityLivingBase camera;
	private boolean usingCamera;

	public PEntityRenderer(Minecraft minecraft, IResourceManager resourceManager)
	{
		super(minecraft, resourceManager);
	}

	@Override
	public void renderWorld(float partialTicks, long updateNano)
	{
		Minecraft mc = Minecraft.getMinecraft();
		if (usingCamera)
			mc.renderViewEntity = getCamera();
		super.renderWorld(partialTicks, updateNano);
	}

	public EntityLivingBase getCamera()
	{
		Minecraft mc = Minecraft.getMinecraft();
		if (camera == null)
			camera = new EntityCamera(mc.theWorld);
		if (camera.worldObj != mc.theWorld)
			camera.setWorld(mc.theWorld);
		return camera;
	}

	public void useCamera(double x, double y, double z, float pitch, float yaw)
	{
		usingCamera = true;
		camera.setPosition(x, y, z);

		camera.rotationYaw = yaw % 360.0F;
		camera.rotationPitch = pitch % 360.0F;
	}

	public void useCamera(double x, double y, double z)
	{
		useCamera(x, y, z, camera.rotationPitch, camera.rotationYaw);
	}

	public void releaseCamera()
	{
		usingCamera = false;
	}
}
