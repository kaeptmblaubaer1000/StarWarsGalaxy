package com.parzivail.util.block;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import net.minecraft.block.BlockLog;

public class PBlockPillar extends BlockLog implements INameProvider
{
	public final String name;

	public PBlockPillar(String name)
	{
		this.name = name;
		setCreativeTab(StarWarsGalaxy.tab);
		setUnlocalizedName(Resources.modDot(this.name));
	}

	@Override
	public String getName()
	{
		return name;
	}
}
