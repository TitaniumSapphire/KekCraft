package com.kekcraft.blocks.machines;

import java.awt.Dimension;

import com.kekcraft.KekCraft;
import com.kekcraft.RecipeHandler;
import com.kekcraft.Tabs;
import com.kekcraft.api.GameFactory;
import com.kekcraft.api.ParticleColor;
import com.kekcraft.api.ui.ElectricMachine;
import com.kekcraft.api.ui.ElectricMachineTileEntity;
import com.kekcraft.api.ui.FaceType;
import com.kekcraft.api.ui.MachineContainer;
import com.kekcraft.api.ui.MachineTileEntity;
import com.kekcraft.api.ui.MachineUpgrade;
import com.kekcraft.api.ui.UIMainScreen;
import com.kekcraft.api.ui.UIOptionsScreen;
import com.kekcraft.api.ui.UIUpgradesScreen;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class BlockCircuitFabricator extends ElectricMachine {
	public BlockCircuitFabricator(GameFactory factory) {
		super(Material.glass, KekCraft.modInstance, 5, "CircuitFabricator");

		factory.initializeBlock(this, "Circuit Fabricator", "CircuitFabricator", Tabs.DEFAULT, "CircuitFabricator");

		RecipeHandler.FUTURES.add(new Runnable() {
			@Override
			public void run() {
				GameRegistry
						.addRecipe(new ShapedOreRecipe(new ItemStack(KekCraft.factory.getBlock("CircuitFabricator")),
								"ABA", "CDC", "AEA", 'A', "ingotAluminum", 'B', "blockSilicon", 'C', "gearIron", 'D',
								KekCraft.factory.getItem("MachineCore"), 'E', "ingotGold"));
			}
		});
		setWindowDimensions(new Dimension(-1, 189));
		initializeSpecialIcons();
		setParticleColor(ParticleColor.GREEN);
	}

	@Override
	public Class<? extends TileEntity> getTileEntityClass() {
		return BlockCircuitFabricatorTileEntity.class;
	}

	@Override
	public void drawTiles(MachineContainer container) {
		container.setNormalizer(12);

		container.drawMinecraftInventory(8, 107);
		container.addSlotToContainer(container.createSlot(0, 56, 34));
		container.addSlotToContainer(container.createSlot(1, 56, 57));
		container.addSlotToContainer(container.createSlot(2, 56, 80));
		container.addSlotToContainer(container.createOutputSlot(3, 135, 58));
		container.addSlotToContainer(container.createUpgradeSlot(4, 96, 47));
		container.addSlotToContainer(container.createUpgradeSlot(5, 96, 68));
	}

	public static class BlockCircuitFabricatorTileEntity extends ElectricMachineTileEntity {
		public BlockCircuitFabricatorTileEntity() {
			super(6, 20);

			setItemSlots(new int[] { 0, 1, 2, 4, 5 });
			setOutputSlots(new int[] { 3 });
			setUpgradeSlots(new int[] { 4, 5 });
			setValidUpgrades(new MachineUpgrade[] { MachineUpgrade.ENERGY_EFFICIENCY, MachineUpgrade.SPEED });

			energy.setCapacity(100000);
			energy.setMaxTransfer(128);
			energy.setEnergyStored(KekCraft.ENERGY_MODE_DEV ? 100000 : 0);

			addRecipe(new CircuitFabricatorRecipe(new ItemStack(Items.redstone),
					new ItemStack(KekCraft.factory.getItem("RefinedSilicon")),
					KekCraft.factory.getItem("CircuitRedstone")));
			addRecipe(new CircuitFabricatorRecipe(new ItemStack(Items.iron_ingot),
					new ItemStack(KekCraft.factory.getItem("CircuitRedstone")),
					KekCraft.factory.getItem("CircuitIron")));
			addRecipe(new CircuitFabricatorRecipe(new ItemStack(Items.gold_ingot),
					new ItemStack(KekCraft.factory.getItem("CircuitIron")), KekCraft.factory.getItem("CircuitGold")));
			addRecipe(new CircuitFabricatorRecipe(new ItemStack(Items.diamond),
					new ItemStack(KekCraft.factory.getItem("CircuitGold")),
					KekCraft.factory.getItem("CircuitDiamond")));
			addRecipe(new CircuitFabricatorRecipe(new ItemStack(Items.emerald),
					new ItemStack(KekCraft.factory.getItem("CircuitDiamond")),
					KekCraft.factory.getItem("CircuitEmerald")));

			setChangeMeta(true);

			onUISet = new Runnable() {
				@Override
				public void run() {
					ui.addScreen(new UIMainScreen(ui) {
						@Override
						public void render(MachineTileEntity m) {
							BlockCircuitFabricatorTileEntity e = (BlockCircuitFabricatorTileEntity) m;

							int barWidth = 7;
							int barHeight = 74;
							int targetHeight = (barHeight - (e.energy.getMaxEnergyStored() - e.energy.getEnergyStored())
									* barHeight / e.energy.getMaxEnergyStored());
							drawUV(ui.left + 26, ui.top + 29 + (barHeight - targetHeight), 176,
									23 + barHeight - targetHeight, barWidth, targetHeight);
							drawTooltip(ui.left + 26, ui.top + 29, barWidth, barHeight,
									(int) e.energy.getEnergyStored() + " RF");

							int arrowWidth = 43;
							int arrowHeight = 16;
							if (e.getCurrentCookTime() > 0) {
								int width = (int) (((e.getCookTime() - e.getCurrentCookTime()) * arrowWidth
										/ (double) e.getCookTime()) + 1);
								if (e.getCurrentCookTime() == 0) {
									width = 0;
								}
								drawUV(ui.left + 79, ui.top + 57, 176, 97, width, arrowHeight);
								drawTooltip(ui.left + 79, ui.top + 57, arrowWidth, arrowHeight,
										100 - (int) ((e.getCurrentCookTime() / (double) e.getCookTime()) * 100) + "%");
							} else {
								drawTooltip(ui.left + 79, ui.top + 57, arrowWidth, arrowHeight, "0%");
							}
						}
					});
					ui.addScreen(new UIOptionsScreen(ui, FaceType.NONE, FaceType.ENERGY, FaceType.ITEM));
					ui.addScreen(new UIUpgradesScreen(ui));
					ui.setCurrentUIScreen("MainScreen");
				}
			};
		}
	}
}
