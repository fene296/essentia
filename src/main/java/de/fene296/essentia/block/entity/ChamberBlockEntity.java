package de.fene296.essentia.block.entity;

import de.fene296.essentia.item.EssentiaItems;
import de.fene296.essentia.recipe.ChamberRecipe;
import de.fene296.essentia.recipe.ChamberRecipeInput;
import de.fene296.essentia.recipe.EssentiaRecipes;
import de.fene296.essentia.screen.custom.ChamberMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

import javax.swing.plaf.basic.BasicComboBoxUI;
import java.util.Optional;

public class ChamberBlockEntity extends BlockEntity implements MenuProvider {

    public final ItemStacksResourceHandler inventory = new ItemStacksResourceHandler(4) {
        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            super.onContentsChanged(index, previousContents);
            ChamberBlockEntity.this.setChanged();
        }
    };

    private static final int INPUT_SLOT = 0;
    private static final int DUST_SLOT = 1;
    private static final int CRYSTAL_SLOT = 2;
    private static final int OUTPUT_SLOT = 3;

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 72;

    public ChamberBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EssentiaBlockEntities.CHAMBER_BE.get(), worldPosition, blockState);
        data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> ChamberBlockEntity.this.progress;
                    case 1 -> ChamberBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                switch (i) {
                    case 0: ChamberBlockEntity.this.progress = value;
                    case 1: ChamberBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.esseentia.chamber");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new ChamberMenu(containerId, inventory, this, this.inventory, this.data);
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(inventory.size());
        for (int i = 0; i < inventory.size(); i++) {
            ItemAccess itemAccess = ItemAccess.forHandlerIndex(inventory, i);
            inv.setItem(i, new ItemStack(itemAccess.getResource().getItem(), itemAccess.getAmount()));
        }
        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("chamber.progress", progress);
        output.putInt("chamber.max_progress", maxProgress);

        output.putChild("inventory", inventory);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        progress = input.getIntOr("chamber.progress", 0);
        maxProgress = input.getIntOr("chamber.max_progress", 72);

        input.child("inventory").ifPresent(inventory::deserialize);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if(hasRecipe() && isOutputSlotEmptyOrReceivable()) {
            increaseCratingProgress();
            setChanged(level, pos, state);

            if(hasCraftingFinished()) {
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    private boolean isOutputSlotEmptyOrReceivable() {
        return inventory.getResource(OUTPUT_SLOT).isEmpty() ||
                inventory.getResource(OUTPUT_SLOT).test(stack -> stack.count() < stack.getMaxStackSize());
    }

    private void craftItem() {
        Optional<RecipeHolder<ChamberRecipe>> recipe = getCurrentRecipe();
        ChamberRecipe value = recipe.get().value();
        ItemStack output = value.assemble(currentInput());

        try(Transaction transaction = Transaction.openRoot()) {
            ItemAccess itemAccess = ItemAccess.forHandlerIndex(inventory, OUTPUT_SLOT);

            inventory.extract(INPUT_SLOT, inventory.getResource(INPUT_SLOT), value.resourceCount(), transaction);
            inventory.extract(DUST_SLOT, inventory.getResource(DUST_SLOT), value.dustCount(), transaction);
            inventory.extract(CRYSTAL_SLOT, inventory.getResource(CRYSTAL_SLOT), value.crystalCount(), transaction);
            inventory.set(OUTPUT_SLOT, ItemResource.of(output), itemAccess.getAmount() + output.getCount());

            transaction.commit();
        }
    }

    private void resetProgress() {
        progress = 0;
        maxProgress = 72;
    }

    private boolean hasCraftingFinished() {
        return this.progress >= this.maxProgress;
    }

    private void increaseCratingProgress() {
        progress++;
    }

    private boolean hasRecipe() {
        Optional<RecipeHolder<ChamberRecipe>> recipe = getCurrentRecipe();
        if(recipe.isEmpty()) {
            return false;
        }

        ItemStack output = recipe.get().value().assemble(currentInput());

        boolean outputSlotAmount = canInsertAmountIntoOutputSlot(output.getCount());
        boolean outputSlotItem = canInsertItemIntoOutputSlot(output);

        return outputSlotAmount && outputSlotItem;
    }

    private ChamberRecipeInput currentInput() {
        ItemAccess ressourceAccess = ItemAccess.forHandlerIndex(inventory, INPUT_SLOT);
        ItemAccess dustAccess = ItemAccess.forHandlerIndex(inventory, DUST_SLOT);
        ItemAccess crystalAccess = ItemAccess.forHandlerIndex(inventory, CRYSTAL_SLOT);

        return new ChamberRecipeInput(
                ressourceAccess.getResource().toStack(ressourceAccess.getAmount()),
                dustAccess.getResource().toStack(dustAccess.getAmount()),
                crystalAccess.getResource().toStack(crystalAccess.getAmount())
        );
    }

    private Optional<RecipeHolder<ChamberRecipe>> getCurrentRecipe() {
        return ((ServerLevel) level).recipeAccess()
                .getRecipeFor(EssentiaRecipes.CHAMBER_TYPE.get(), currentInput(), level);
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack output) {
        return inventory.getResource(OUTPUT_SLOT).isEmpty() ||
                inventory.getResource(OUTPUT_SLOT).is(output.getItem());
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        int maxCount = inventory.getResource(OUTPUT_SLOT).isEmpty() ? 64 : inventory.getResource(OUTPUT_SLOT).getMaxStackSize();
        int currentCount = inventory.getAmountAsInt(OUTPUT_SLOT);

        return maxCount >= currentCount + count;
    }


    /* BLOCK ENTITY SYNC */
    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    @Override
    public void onDataPacket(Connection net, ValueInput valueInput) {
        super.onDataPacket(net, valueInput);
    }


}
