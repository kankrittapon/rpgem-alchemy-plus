package net.kankrittapon.rpgem.alchemy.block.entity;

import net.kankrittapon.rpgem.alchemy.init.ModAlchemyBlockEntities;
import net.kankrittapon.rpgem.alchemy.init.ModAlchemyItems;
import net.kankrittapon.rpgem.alchemy.menu.AlchemyTableMenu;
import net.kankrittapon.rpgem.forging.init.ModForgingItems;
import net.kankrittapon.rpgem.alchemy.item.SequentialInfinitePotion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AlchemyTableBlockEntity extends BlockEntity implements MenuProvider {
    public final ItemStackHandler itemHandler = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return switch (slot) {
                case 0 -> stack.getItem() == ModAlchemyItems.ETHERNAL_BOTTLE.get() ||
                        stack.getItem() == ModAlchemyItems.INFINITE_POTION_TIER_1.get() ||
                        stack.getItem() == ModAlchemyItems.INFINITE_POTION_TIER_2.get() ||
                        stack.getItem() == ModForgingItems.UPGRADE_STONE_TIER_2.get() ||
                        stack.getItem() == ModForgingItems.FORGED_STONE_FORTITUDE.get() ||
                        stack.getItem() == ModForgingItems.FORGED_STONE_AGILITY.get() ||
                        stack.getItem() == ModForgingItems.FORGED_STONE_DESTRUCTION.get();
                case 1, 2, 3 -> stack.getItem() == ModAlchemyItems.ZOMBIE_HEART.get() ||
                        stack.getItem() == ModAlchemyItems.BONE_OF_MAZE.get() ||
                        stack.getItem() == ModAlchemyItems.COSMIC_EMERALD.get() ||
                        stack.getItem() == ModForgingItems.UPGRADE_STONE_TIER_3.get();
                case 4 -> false; // Output slot
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 200;

    public AlchemyTableBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModAlchemyBlockEntities.ALCHEMY_TABLE_BE.get(), pos, blockState);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> AlchemyTableBlockEntity.this.progress;
                    case 1 -> AlchemyTableBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> AlchemyTableBlockEntity.this.progress = value;
                    case 1 -> AlchemyTableBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        net.minecraft.world.Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("inventory", itemHandler.serializeNBT(registries));
        tag.putInt("alchemy_table.progress", progress);
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        progress = tag.getInt("alchemy_table.progress");
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) {
            return;
        }

        boolean isCrafting = progress > 0;
        boolean shouldCheckRecipe = level.getGameTime() % 10 == 0 || !isCrafting;

        if (shouldCheckRecipe) {
            if (hasRecipe()) {
                increaseCraftingProgress();
                setChanged(level, pos, state);

                if (hasProgressFinished()) {
                    craftItem();
                    resetProgress();
                }
            } else {
                resetProgress();
            }
        } else if (isCrafting) {
            increaseCraftingProgress();
            setChanged(level, pos, state);
            if (hasProgressFinished()) {
                if (hasRecipe()) {
                    craftItem();
                }
                resetProgress();
            }
        }
    }

    private boolean hasRecipe() {
        ItemStack resultSlot = itemHandler.getStackInSlot(4);
        if (resultSlot.getCount() >= resultSlot.getMaxStackSize()) {
            return false;
        }

        ItemStack input = itemHandler.getStackInSlot(0);
        if (input.isEmpty())
            return false;

        List<ItemStack> ingredients = List.of(
                itemHandler.getStackInSlot(1),
                itemHandler.getStackInSlot(2),
                itemHandler.getStackInSlot(3));

        boolean hasIngredients = false;
        for (ItemStack ing : ingredients) {
            if (!ing.isEmpty()) {
                hasIngredients = true;
                break;
            }
        }
        if (!hasIngredients)
            return false;

        CraftingContext ctx = getCraftingContext(input, ingredients);
        if (ctx != null) {
            this.maxProgress = ctx.processTime;
            return resultSlot.isEmpty()
                    || (resultSlot.getItem() == ctx.outputItem && resultSlot.getCount() < resultSlot.getMaxStackSize());
        }

        return false;
    }

    private void craftItem() {
        ItemStack input = itemHandler.getStackInSlot(0);
        List<ItemStack> ingredients = List.of(
                itemHandler.getStackInSlot(1),
                itemHandler.getStackInSlot(2),
                itemHandler.getStackInSlot(3));

        CraftingContext ctx = getCraftingContext(input, ingredients);
        if (ctx != null) {
            if (ctx.outputItem instanceof SequentialInfinitePotion) {
                String ingredientName = getIngredientName(ctx.ingredientUsed);
                ItemStack newResult = new ItemStack(ctx.outputItem);
                applyUsedIngredients(newResult, ctx.previousUsed, ingredientName);

                ItemStack resultStack = itemHandler.getStackInSlot(4);
                if (resultStack.isEmpty()) {
                    itemHandler.setStackInSlot(4, newResult);
                } else {
                    resultStack.grow(1);
                }
            } else {
                ItemStack resultStack = itemHandler.getStackInSlot(4);
                if (resultStack.isEmpty()) {
                    itemHandler.setStackInSlot(4, new ItemStack(ctx.outputItem));
                } else {
                    resultStack.grow(1);
                }
            }

            input.shrink(1);
            ctx.ingredientUsed.shrink(1);
        }
    }

    private void applyUsedIngredients(ItemStack stack, List<String> previousUsed, String newIngredient) {
        List<String> used = new ArrayList<>(previousUsed);
        used.add(newIngredient);

        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        for (String s : used) {
            listTag.add(StringTag.valueOf(s));
        }
        tag.put("IngredientHistory", listTag);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    private String getIngredientName(ItemStack stack) {
        if (stack.is(ModAlchemyItems.ZOMBIE_HEART.get()))
            return "H";
        if (stack.is(ModAlchemyItems.BONE_OF_MAZE.get()))
            return "B";
        if (stack.is(ModAlchemyItems.COSMIC_EMERALD.get()))
            return "C";
        return stack.getItem().toString();
    }

    private record CraftingContext(net.minecraft.world.item.Item outputItem, int processTime, List<String> previousUsed,
            net.minecraft.world.item.ItemStack ingredientUsed) {
    }

    private CraftingContext getCraftingContext(ItemStack input, List<ItemStack> availableIngredients) {
        if (input.isEmpty())
            return null;

        List<String> usedIngredients = new ArrayList<>();
        if (input.has(DataComponents.CUSTOM_DATA)) {
            CustomData customData = input.get(DataComponents.CUSTOM_DATA);
            CompoundTag tag = customData.copyTag();
            if (tag.contains("IngredientHistory")) {
                ListTag list = tag.getList("IngredientHistory", 8);
                for (int i = 0; i < list.size(); i++) {
                    usedIngredients.add(list.getString(i));
                }
            }
        }

        Item output = null;
        int time = 0;

        if (input.is(ModAlchemyItems.ETHERNAL_BOTTLE.get())) {
            output = ModAlchemyItems.INFINITE_POTION_TIER_1.get();
            time = 200;
        } else if (input.is(ModAlchemyItems.INFINITE_POTION_TIER_1.get())) {
            output = ModAlchemyItems.INFINITE_POTION_TIER_2.get();
            time = 300;
        } else if (input.is(ModAlchemyItems.INFINITE_POTION_TIER_2.get())) {
            output = ModAlchemyItems.INFINITE_POTION_TIER_3.get();
            time = 400;
        } else if (input.is(ModForgingItems.UPGRADE_STONE_TIER_2.get())) {
            time = 300;
            for (ItemStack ing : availableIngredients) {
                if (ing.is(ModAlchemyItems.ZOMBIE_HEART.get()))
                    return new CraftingContext(ModForgingItems.FORGED_STONE_FORTITUDE.get(), time, usedIngredients,
                            ing);
                if (ing.is(ModAlchemyItems.BONE_OF_MAZE.get()))
                    return new CraftingContext(ModForgingItems.FORGED_STONE_AGILITY.get(), time, usedIngredients, ing);
                if (ing.is(ModAlchemyItems.COSMIC_EMERALD.get()))
                    return new CraftingContext(ModForgingItems.FORGED_STONE_DESTRUCTION.get(), time, usedIngredients,
                            ing);
            }
        } else if (input.is(ModForgingItems.FORGED_STONE_FORTITUDE.get())) {
            for (ItemStack ing : availableIngredients)
                if (ing.is(ModForgingItems.UPGRADE_STONE_TIER_3.get()))
                    return new CraftingContext(ModForgingItems.FORGED_STONE_ULTIMATE_FORTITUDE.get(), 500,
                            usedIngredients,
                            ing);
        } else if (input.is(ModForgingItems.FORGED_STONE_AGILITY.get())) {
            for (ItemStack ing : availableIngredients)
                if (ing.is(ModForgingItems.UPGRADE_STONE_TIER_3.get()))
                    return new CraftingContext(ModForgingItems.FORGED_STONE_ULTIMATE_AGILITY.get(), 500,
                            usedIngredients, ing);
        } else if (input.is(ModForgingItems.FORGED_STONE_DESTRUCTION.get())) {
            for (ItemStack ing : availableIngredients)
                if (ing.is(ModForgingItems.UPGRADE_STONE_TIER_3.get()))
                    return new CraftingContext(ModForgingItems.FORGED_STONE_ULTIMATE_DESTRUCTION.get(), 500,
                            usedIngredients,
                            ing);
        }

        if (output == null)
            return null;

        for (ItemStack ing : availableIngredients) {
            if (!ing.isEmpty()) {
                String name = getIngredientName(ing);
                if (!usedIngredients.contains(name)) {
                    return new CraftingContext(output, time, usedIngredients, ing);
                }
            }
        }

        return null;
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private void increaseCraftingProgress() {
        this.progress++;
    }

    public int getProgress() {
        return this.progress;
    }

    public int getMaxProgress() {
        return this.maxProgress;
    }

    private boolean hasProgressFinished() {
        return this.progress >= this.maxProgress;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.rpgem_alchemy.alchemy_table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory,
            net.minecraft.world.entity.player.Player player) {
        return new AlchemyTableMenu(containerId, playerInventory, this, this.data);
    }
}
