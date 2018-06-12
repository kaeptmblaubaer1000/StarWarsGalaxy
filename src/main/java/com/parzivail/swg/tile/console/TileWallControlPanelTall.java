package com.parzivail.swg.tile.console;

import com.parzivail.swg.Resources;
import com.parzivail.util.block.TileRotatable;
import com.parzivail.util.math.MathUtil;

public class TileWallControlPanelTall extends TileRotatable
{
	public int color1 = 0;
	public int color2 = 0;
	public int color3 = 0;
	public int color4 = 0;
	public int color5 = 0;
	public int color6 = 0;
	public int color7 = 0;
	public int color8 = 0;

	@Override
	public void updateEntity()
	{
		if (MathUtil.oneIn(20))
			color1 = MathUtil.getRandomElement(Resources.PANEL_LIGHT_COLORS);
		if (MathUtil.oneIn(50))
			color2 = MathUtil.getRandomElement(Resources.PANEL_LIGHT_COLORS);
		if (MathUtil.oneIn(70))
			color3 = MathUtil.getRandomElement(Resources.PANEL_LIGHT_COLORS);
		if (MathUtil.oneIn(10))
			color4 = MathUtil.getRandomElement(Resources.PANEL_LIGHT_COLORS);
		if (MathUtil.oneIn(100))
			color5 = MathUtil.getRandomElement(Resources.PANEL_LIGHT_COLORS);
		if (MathUtil.oneIn(5))
			color6 = MathUtil.getRandomElement(Resources.PANEL_LIGHT_COLORS);
		if (MathUtil.oneIn(50))
			color7 = MathUtil.getRandomElement(Resources.PANEL_LIGHT_COLORS);
		if (MathUtil.oneIn(60))
			color8 = MathUtil.getRandomElement(Resources.PANEL_LIGHT_COLORS);
	}
}