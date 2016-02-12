package am2.blocks;

import am2.AMCore;
import am2.api.blocks.IKeystoneLockable;
import am2.api.items.KeystoneAccessType;
import am2.blocks.tileentities.TileEntityKeystoneDoor;
import am2.guis.ArsMagicaGuiIdList;
import am2.items.ItemsCommonProxy;
import am2.lore.CompendiumUnlockHandler;
import am2.utility.KeystoneUtilities;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;

import java.util.Random;

public class BlockKeystoneDoor extends BlockDoor implements ITileEntityProvider{

	protected BlockKeystoneDoor(){
		super(Material.wood);
		this.setHardness(2.5f);
		this.setResistance(2.0f);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int direction, float xOffset, float yOffset, float zOffset){
		if (world.getBlock(x, y - 1, z) == BlocksCommonProxy.keystoneDoor)
			y--;

		TileEntity te = world.getTileEntity(x, y, z);

		player.swingItem();

		if (!world.isRemote){

			if (KeystoneUtilities.HandleKeystoneRecovery(player, (IKeystoneLockable)te))
				return true;

			if (KeystoneUtilities.instance.canPlayerAccess((IKeystoneLockable)te, player, KeystoneAccessType.USE)){
				if (player.isSneaking()){
					FMLNetworkHandler.openGui(player, AMCore.instance, ArsMagicaGuiIdList.GUI_KEYSTONE_LOCKABLE, world, x, y, z);
				}else{
					world.playSoundEffect(x, y, z, "random.door_open", 1.0f, 1.0f);
					activateNeighbors(world, x, y, z, player, direction, xOffset, yOffset, zOffset);
					CompendiumUnlockHandler.unlockEntry(this.getUnlocalizedName().replace("arsmagica2:", "").replace("tile.", ""));
					return super.onBlockActivated(world, x, y, z, player, direction, xOffset, yOffset, zOffset);
				}
			}
		}

		return false;
	}

	private void activateNeighbors(World world, int x, int y, int z, EntityPlayer player, int direction, float xOffset, float yOffset, float zOffset){
		if (world.getBlock(x + 1, y, z) == BlocksCommonProxy.keystoneDoor)
			super.onBlockActivated(world, x + 1, y, z, player, direction, xOffset, yOffset, zOffset);

		if (world.getBlock(x - 1, y, z) == BlocksCommonProxy.keystoneDoor)
			super.onBlockActivated(world, x - 1, y, z, player, direction, xOffset, yOffset, zOffset);

		if (world.getBlock(x, y, z + 1) == BlocksCommonProxy.keystoneDoor)
			super.onBlockActivated(world, x, y, z + 1, player, direction, xOffset, yOffset, zOffset);

		if (world.getBlock(x, y, z - 1) == BlocksCommonProxy.keystoneDoor)
			super.onBlockActivated(world, x, y, z - 1, player, direction, xOffset, yOffset, zOffset);
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z){
		if (world.isRemote)
			return false;

		if (world.getBlock(x, y - 1, z) == BlocksCommonProxy.keystoneDoor)
			y--;

		IKeystoneLockable lockable = (IKeystoneLockable)world.getTileEntity(x, y, z);

		if (lockable == null)
			return false;

		if (!KeystoneUtilities.instance.canPlayerAccess(lockable, player, KeystoneAccessType.BREAK)) return false;

		return super.removedByPlayer(world, player, x, y, z);
	}

	@Override
	public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player){
		if (world.isRemote)
			return;

		if (world.getBlock(x, y - 1, z) == BlocksCommonProxy.keystoneDoor)
			y--;

		IKeystoneLockable lockable = (IKeystoneLockable)world.getTileEntity(x, y, z);

		if (lockable == null)
			return;

		if (!KeystoneUtilities.instance.canPlayerAccess(lockable, player, KeystoneAccessType.BREAK))
			return;
		super.onBlockHarvested(world, x, y, z, meta, player);
	}
	
	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
	{
	  return ItemsCommonProxy.itemKeystoneDoor;
	}
	

	@Override
	public TileEntity createNewTileEntity(World world, int i){
		return new TileEntityKeystoneDoor();
	}

	@Override
	public int getRenderBlockPass(){
		return 1;
	}
}
