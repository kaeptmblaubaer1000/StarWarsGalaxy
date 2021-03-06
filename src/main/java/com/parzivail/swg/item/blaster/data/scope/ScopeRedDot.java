package com.parzivail.swg.item.blaster.data.scope;

import com.parzivail.util.ui.Fx.D2;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class ScopeRedDot extends BlasterScope
{
	public ScopeRedDot()
	{
		super("redDot", 200);
	}

	@Override
	public void draw(ScaledResolution sr, EntityPlayer player, ItemStack stack)
	{
		GL11.glColor4f(1, 0, 0, 1);
		D2.DrawPoint(0, 0, 5);
	}

	@Override
	public float getZoomLevel()
	{
		return 1;
	}
}
