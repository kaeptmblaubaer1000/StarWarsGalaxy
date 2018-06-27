package com.parzivail.swg.item.blaster.data.powerpack;

import com.parzivail.swg.Resources;
import com.parzivail.swg.item.ItemBlasterPowerPack;
import com.parzivail.swg.item.blaster.data.BlasterAttachment;
import com.parzivail.swg.item.blaster.data.BlasterAttachmentType;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

public abstract class BlasterPowerPack extends BlasterAttachment
{
	public BlasterPowerPack(String name, int price)
	{
		super(BlasterAttachmentType.POWERPACK, Resources.modDot("blaster", "powerpack", name), price);
	}

	public static BlasterPowerPack getPackForItem(ItemStack stack)
	{
		if (stack == null || !(stack.getItem() instanceof ItemBlasterPowerPack))
			return null;
		return ItemBlasterPowerPack.itemStackAttachmentTypes.get(stack.getItem());
	}

	@Override
	public String getInfoText()
	{
		return I18n.format(Resources.guiDot("packInfo"), getNumShots());
	}

	public abstract int getNumShots();
}
