package com.parzivail.util.render.pipeline;

import com.parzivail.swg.block.PDecorativeBlock;
import com.parzivail.util.binary.PIO;
import com.parzivail.util.common.Lumberjack;
import com.parzivail.util.ui.GLPalette;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class JsonModelRenderer
{
	protected static final int MAX_BRIGHTNESS = 0xf0;
	protected static final String MISSING_MODEL_MESH = "{    'textures': {       'particle': 'missingno',       'missingno': 'missingno'    },    'elements': [         {  'from': [ 0, 0, 0 ],            'to': [ 16, 16, 16 ],            'faces': {                'down':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'down',  'texture': '#missingno' },                'up':    { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'up',    'texture': '#missingno' },                'north': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'north', 'texture': '#missingno' },                'south': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'south', 'texture': '#missingno' },                'west':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'west',  'texture': '#missingno' },                'east':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'east',  'texture': '#missingno' }            }        }    ]}".replaceAll("'", "\"");
	protected static final ArrayList<String> unloadedTextures = new ArrayList<>();
	private static final float SCALE_ROTATION_22_5 = 1.0F / (float)Math.cos(0.39269909262657166D) - 1.0F;
	private static final float SCALE_ROTATION_GENERAL = 1.0F / (float)Math.cos((Math.PI / 4D)) - 1.0F;
	protected ModelBlock model;
	protected int displayList;
	protected boolean compiled;

	public JsonModelRenderer(ResourceLocation modelLocation)
	{
		InputStream resource = PIO.getResource(JsonModelRenderer.class, modelLocation);
		if (resource == null)
			model = ModelBlock.deserialize(MISSING_MODEL_MESH);
		else
			model = ModelBlock.deserialize(new InputStreamReader(resource));

		for (Map.Entry<String, String> entry : model.textures.entrySet())
		{
			String tex = translateTextureName(entry.getValue());
			if (!unloadedTextures.contains(tex))
				unloadedTextures.add(tex);
		}
	}

	public static void loadTextures(TextureMap map)
	{
		for (String tex : unloadedTextures)
			map.registerIcon(tex);
		Lumberjack.debug("Registered %s JSON model icons", unloadedTextures.size());
	}

	protected static String translateTextureName(String name)
	{
		return name.replace("blocks/", "");
	}

	private static javax.vecmath.Vector3f getVertexPos(int[] data, int vertex)
	{
		int idx = vertex * 7;

		float x = Float.intBitsToFloat(data[idx]);
		float y = Float.intBitsToFloat(data[idx + 1]);
		float z = Float.intBitsToFloat(data[idx + 2]);

		return new javax.vecmath.Vector3f(x, y, z);
	}

	private static float[] getPositionsDiv16(Vector3f pos1, Vector3f pos2)
	{
		float[] afloat = new float[EnumFacing.values().length];
		afloat[EnumFaceDirection.Constants.WEST_INDEX] = pos1.x / 16.0F;
		afloat[EnumFaceDirection.Constants.DOWN_INDEX] = pos1.y / 16.0F;
		afloat[EnumFaceDirection.Constants.NORTH_INDEX] = pos1.z / 16.0F;
		afloat[EnumFaceDirection.Constants.EAST_INDEX] = pos2.x / 16.0F;
		afloat[EnumFaceDirection.Constants.UP_INDEX] = pos2.y / 16.0F;
		afloat[EnumFaceDirection.Constants.SOUTH_INDEX] = pos2.z / 16.0F;
		return afloat;
	}

	private static void drawQuad(BlockFaceUV uvs, IIcon sprite, EnumFacing orientation, float[] p_188012_4_, BlockPartRotation partRotation, ITransformation transformation, int brightness, PartType type, boolean forceAmbientBrightness)
	{
		for (int i = 0; i < 4; ++i)
			drawVertex(i, orientation, uvs, p_188012_4_, sprite, partRotation, transformation, brightness, type, forceAmbientBrightness);
	}

	private static void drawVertex(int storeIndex, EnumFacing facing, BlockFaceUV faceUV, float[] p_188015_5_, IIcon sprite, BlockPartRotation rotation, ITransformation transformation, int brightness, PartType type, boolean forceAmbientBrightness)
	{
		EnumFacing enumfacing = transformation.rotate(facing);
		int shadeColor = getFaceShadeColor(enumfacing);
		EnumFaceDirection.VertexInformation vertexInformation = EnumFaceDirection.getFacing(facing).getVertexInformation(storeIndex);
		Vector3f position = new Vector3f(p_188015_5_[vertexInformation.xIndex], p_188015_5_[vertexInformation.yIndex], p_188015_5_[vertexInformation.zIndex]);
		rotatePart(position, rotation);
		rotateVertex(position, facing, storeIndex, transformation);
		int[] faceData = new int[28];
		storeVertexData(faceData, storeIndex, storeIndex, position, shadeColor, sprite, faceUV);

		javax.vecmath.Vector3f v1 = getVertexPos(faceData, 3);
		javax.vecmath.Vector3f t = getVertexPos(faceData, 1);
		javax.vecmath.Vector3f v2 = getVertexPos(faceData, 2);
		v1.sub(t);
		t.set(getVertexPos(faceData, 0));
		v2.sub(t);
		v1.cross(v2, v1);
		v1.normalize();

		Tessellator.instance.setNormal(v1.x, v1.y, v1.z);
		if (type == PartType.Lit)
		{
			Tessellator.instance.setBrightness(MAX_BRIGHTNESS);
			Tessellator.instance.setColorOpaque_I(GLPalette.WHITE);
		}
		else
		{
			if (forceAmbientBrightness)
				Tessellator.instance.setBrightness(brightness);
			Tessellator.instance.setColorOpaque_I(shadeColor);
		}
		Tessellator.instance.addVertexWithUV(position.x, position.y, position.z, sprite.getInterpolatedU((double)faceUV.getVertexU(storeIndex) * .999 + faceUV.getVertexU((storeIndex + 2) % 4) * .001), sprite.getInterpolatedV((double)faceUV.getVertexV(storeIndex) * .999 + faceUV.getVertexV((storeIndex + 2) % 4) * .001));
	}

	private static void rotateVertex(Vector3f p_188011_1_, EnumFacing p_188011_2_, int p_188011_3_, ITransformation p_188011_4_)
	{
		if (p_188011_4_ != ModelRotation.X0_Y0)
		{
			transform(p_188011_1_, p_188011_4_.getMatrix());
			p_188011_4_.rotate(p_188011_2_, p_188011_3_);
		}
	}

	private static void transform(Vector3f vec, javax.vecmath.Matrix4f m)
	{
		javax.vecmath.Vector4f tmp = new javax.vecmath.Vector4f(vec.x, vec.y, vec.z, 1f);
		m.transform(tmp);
		if (Math.abs(tmp.w - 1f) > 1e-5)
			tmp.scale(1f / tmp.w);
		vec.set(tmp.x, tmp.y, tmp.z);
	}

	private static void storeVertexData(int[] faceData, int storeIndex, int vertexIndex, Vector3f position, int shadeColor, IIcon sprite, BlockFaceUV faceUV)
	{
		int i = storeIndex * 7;
		faceData[i] = Float.floatToRawIntBits(position.x);
		faceData[i + 1] = Float.floatToRawIntBits(position.y);
		faceData[i + 2] = Float.floatToRawIntBits(position.z);
		faceData[i + 3] = shadeColor;
		faceData[i + 4] = Float.floatToRawIntBits(sprite.getInterpolatedU((double)faceUV.getVertexU(vertexIndex) * .999 + faceUV.getVertexU((vertexIndex + 2) % 4) * .001));
		faceData[i + 4 + 1] = Float.floatToRawIntBits(sprite.getInterpolatedV((double)faceUV.getVertexV(vertexIndex) * .999 + faceUV.getVertexV((vertexIndex + 2) % 4) * .001));
	}

	private static void rotatePart(Vector3f p_178407_1_, @Nullable BlockPartRotation partRotation)
	{
		if (partRotation != null)
		{
			Matrix4f matrix4f = getMatrixIdentity();
			Vector3f vector3f = new Vector3f(0.0F, 0.0F, 0.0F);

			switch (partRotation.axis)
			{
				case X:
					Matrix4f.rotate(partRotation.angle * 0.017453292F, new Vector3f(1.0F, 0.0F, 0.0F), matrix4f, matrix4f);
					vector3f.set(0.0F, 1.0F, 1.0F);
					break;
				case Y:
					Matrix4f.rotate(partRotation.angle * 0.017453292F, new Vector3f(0.0F, 1.0F, 0.0F), matrix4f, matrix4f);
					vector3f.set(1.0F, 0.0F, 1.0F);
					break;
				case Z:
					Matrix4f.rotate(partRotation.angle * 0.017453292F, new Vector3f(0.0F, 0.0F, 1.0F), matrix4f, matrix4f);
					vector3f.set(1.0F, 1.0F, 0.0F);
			}

			if (partRotation.rescale)
			{
				if (Math.abs(partRotation.angle) == 22.5F)
				{
					vector3f.scale(SCALE_ROTATION_22_5);
				}
				else
				{
					vector3f.scale(SCALE_ROTATION_GENERAL);
				}

				Vector3f.add(vector3f, new Vector3f(1.0F, 1.0F, 1.0F), vector3f);
			}
			else
			{
				vector3f.set(1.0F, 1.0F, 1.0F);
			}

			rotateScale(p_178407_1_, new Vector3f(partRotation.origin), matrix4f, vector3f);
		}
	}

	private static void rotateScale(Vector3f position, Vector3f rotationOrigin, Matrix4f rotationMatrix, Vector3f scale)
	{
		Vector4f vector4f = new Vector4f(position.x - rotationOrigin.x, position.y - rotationOrigin.y, position.z - rotationOrigin.z, 1.0F);
		Matrix4f.transform(rotationMatrix, vector4f, vector4f);
		vector4f.x *= scale.x;
		vector4f.y *= scale.y;
		vector4f.z *= scale.z;
		position.set(vector4f.x + rotationOrigin.x, vector4f.y + rotationOrigin.y, vector4f.z + rotationOrigin.z);
	}

	private static Matrix4f getMatrixIdentity()
	{
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.setIdentity();
		return matrix4f;
	}

	private static int getFaceShadeColor(EnumFacing facing)
	{
		float f = getFaceBrightness(facing);
		int i = MathHelper.clamp_int((int)(f * 255.0F), 0, 255);
		return -16777216 | i << 16 | i << 8 | i;
	}

	private static float getFaceBrightness(EnumFacing facing)
	{
		switch (facing)
		{
			case DOWN:
				return 0.5F;
			case UP:
				return 1.0F;
			case NORTH:
			case SOUTH:
				return 0.8F;
			case WEST:
			case EAST:
				return 0.6F;
			default:
				return 1.0F;
		}
	}

	protected void drawBlock(IBlockAccess world, int x, int y, int z, Block block, ITransformation modelRotationIn, int brightness)
	{
		RenderBlocks.getInstance().setRenderBoundsFromBlock(block);
		for (BlockPart blockpart : model.getElements())
		{
			if (world != null)
			{
				if (blockpart.name.endsWith("!onlynorth"))
				{
					Block b = world.getBlock(x, y, z + 1);
					if (shouldntConnectTo(block, b))
						continue;
				}

				if (blockpart.name.endsWith("!onlysouth"))
				{
					Block b = world.getBlock(x, y, z - 1);
					if (shouldntConnectTo(block, b))
						continue;
				}

				if (blockpart.name.endsWith("!onlyeast"))
				{
					Block b = world.getBlock(x - 1, y, z);
					if (shouldntConnectTo(block, b))
						continue;
				}

				if (blockpart.name.endsWith("!onlywest"))
				{
					Block b = world.getBlock(x + 1, y, z);
					if (shouldntConnectTo(block, b))
						continue;
				}

				if (blockpart.name.endsWith("!onlyup"))
				{
					Block b = world.getBlock(x, y + 1, z);
					if (shouldntConnectTo(block, b))
						continue;
				}

				if (blockpart.name.endsWith("!neverup"))
				{
					Block b = world.getBlock(x, y + 1, z);
					if (!shouldntConnectTo(block, b))
						continue;
				}

				if (blockpart.name.endsWith("!onlydown"))
				{
					Block b = world.getBlock(x, y - 1, z);
					if (shouldntConnectTo(block, b))
						continue;
				}
			}
			else if (blockpart.name.contains("!only"))
				continue;

			Minecraft mc = Minecraft.getMinecraft();
			for (EnumFacing enumfacing : blockpart.mapFaces.keySet())
			{
				BlockPartFace blockpartface = blockpart.mapFaces.get(enumfacing);
				String texName = translateTextureName(model.resolveTextureName(blockpartface.texture));
				IIcon sprite = mc.getTextureMapBlocks().getAtlasSprite(texName);
				PartType type = PartType.Textured;

				if (texName.startsWith("pswg:model/special_lit"))
					type = PartType.Lit;
				else if (texName.equals("pswg:model/special_inheritBelow"))
				{
					type = PartType.Hidden;
					if (world != null && !world.isAirBlock(x, y - 1, z))
					{
						type = PartType.InheritBelow;
						int metadata = world.getBlockMetadata(x, y - 1, z);
						sprite = world.getBlock(x, y - 1, z).getIcon(1, metadata);
					}
				}

				if (type != PartType.Hidden)
					drawQuad(blockpartface.blockFaceUV, sprite, enumfacing, getPositionsDiv16(blockpart.positionFrom, blockpart.positionTo), blockpart.partRotation, modelRotationIn, brightness, type, true);
			}
		}
	}

	protected void drawItem(ITransformation modelRotationIn, int brightness)
	{
		Minecraft mc = Minecraft.getMinecraft();
		for (BlockPart blockpart : model.getElements())
		{
			if (blockpart.name.contains("!only"))
				continue;

			for (EnumFacing enumfacing : blockpart.mapFaces.keySet())
			{
				BlockPartFace blockpartface = blockpart.mapFaces.get(enumfacing);
				String texName = translateTextureName(model.resolveTextureName(blockpartface.texture));
				IIcon sprite = mc.getTextureMapBlocks().getAtlasSprite(texName);
				PartType type = PartType.Textured;

				if (texName.startsWith("pswg:model/special_lit"))
					type = PartType.Lit;
				else if (texName.equals("pswg:model/special_inheritBelow"))
					type = PartType.Hidden;

				if (type != PartType.Hidden)
					drawQuad(blockpartface.blockFaceUV, sprite, enumfacing, getPositionsDiv16(blockpart.positionFrom, blockpart.positionTo), blockpart.partRotation, modelRotationIn, brightness, type, false);
			}
		}
	}

	private boolean shouldntConnectTo(Block block, Block otherBlock)
	{
		if (block instanceof PDecorativeBlock)
			return !((PDecorativeBlock)block).doesConnectTo(otherBlock);
		else
			return otherBlock != block;
	}
}
