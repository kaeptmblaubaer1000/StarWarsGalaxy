package com.parzivail.swg.registry;

import com.parzivail.scarif.ScarifEngine;
import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;

public class StructureRegister
{
	public static ScarifEngine structureEngine;

	public static void register()
	{
		structureEngine = new ScarifEngine(Resources.MODID);

		//structureEngine.load(StarWarsGalaxy.config.getDimIdTatooine(), Resources.location("structures/interdictor.scrf"));
		structureEngine.load(StarWarsGalaxy.config.getDimIdTatooine(), Resources.location("structures/isd_test.scrf"));
	}
}
