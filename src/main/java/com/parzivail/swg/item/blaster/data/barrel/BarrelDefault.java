package com.parzivail.swg.item.blaster.data.barrel;

public class BarrelDefault extends BlasterBarrel
{
	public BarrelDefault()
	{
		super("default", 0);
	}

	@Override
	public float getHorizontalRecoilReduction()
	{
		return 0;
	}

	@Override
	public float getHorizontalSpreadReduction()
	{
		return 0;
	}

	@Override
	public float getVerticalRecoilReduction()
	{
		return 0;
	}

	@Override
	public float getVerticalSpreadReduction()
	{
		return 0;
	}

	@Override
	public float getNoiseReduction()
	{
		return 0;
	}

	@Override
	public float getRangeIncrease()
	{
		return 0;
	}
}
