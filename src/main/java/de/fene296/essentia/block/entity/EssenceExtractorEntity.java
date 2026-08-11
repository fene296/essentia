package de.fene296.essentia.block.entity;

import de.fene296.essentia.block.EssentiaBlockEntities;
import de.fene296.essentia.block.custom.EssenceExtractor;
import de.fene296.essentia.recipe.EssentiaRecipes;
import de.fene296.essentia.recipe.essence_extractor.EssenceExtractorRecipe;
import de.fene296.essentia.recipe.essence_extractor.EssenceExtractorRecipeInput;
import de.fene296.essentia.screen.custom.essence_extractor.EssenceExtractorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * Block entity for the {@link EssenceExtractor} machine.
 * <p>
 * Holds the 4 item slots (resource, essence dust, primordial crystal, output), runs
 * the recipe-matching + crafting-progress logic every tick, provides the GUI (as a
 * {@link MenuProvider}), and handles saving/loading its inventory and progress to disk.
 */
public class EssenceExtractorEntity extends BlockEntity implements MenuProvider {

    /**
     * Holds the actual items in the machine's 4 slots. Using NeoForge's newer
     * "Transfer API" resource handler instead of a classic {@code Container}/{@code ItemStack[]}.
     * {@code onContentsChanged} is overridden purely to make sure {@link #setChanged()}
     * is called (marks the block entity dirty / needing a save) whenever a slot changes.
     */
    public final ItemStacksResourceHandler inventory = new ItemStacksResourceHandler(4) {
        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            super.onContentsChanged(index, previousContents);
            EssenceExtractorEntity.this.setChanged();
        }
    };

    // Slot indices within `inventory`, in the order the recipe expects them.
    private static final int INPUT_SLOT = 0;
    private static final int DUST_SLOT = 1;
    private static final int CRYSTAL_SLOT = 2;
    private static final int OUTPUT_SLOT = 3;

    /**
     * Synced data (progress bar values) shared between server and client so the GUI
     * can render an accurate progress arrow. See the anonymous {@link ContainerData}
     * built in the constructor for how index 0/1 map to progress/maxProgress.
     */
    protected final ContainerData data;

    /** How many ticks the current craft has been running for. */
    private int progress = 0;

    /** How many ticks the current recipe needs in total (defaults to 72 = 3.6s if no recipe is active). */
    private int maxProgress = 72;

    public EssenceExtractorEntity(BlockPos worldPosition, BlockState blockState) {
        super(EssentiaBlockEntities.ESSENCE_EXTRACTOR_BE.get(), worldPosition, blockState);
        data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> EssenceExtractorEntity.this.progress;
                    case 1 -> EssenceExtractorEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                switch (i) {
                    case 0 -> EssenceExtractorEntity.this.progress = value;
                    case 1 -> EssenceExtractorEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    /** Display name shown in the GUI's title bar. */
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.essentia.essence_extractor");
    }

    /**
     * Builds the actual GUI menu when a player opens this block (called by
     * {@link EssenceExtractor#useWithoutItem}). Passes both the item inventory and the
     * synced progress {@link #data} so the client-side menu/screen can render correctly.
     */
    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new EssenceExtractorMenu(containerId, inventory, this, this.inventory, this.data);
    }

    /**
     * Spills all items currently stored in this machine into the world as item entities.
     * Called from {@link EssenceExtractor#onDestroyedByPlayer} right before the block
     * itself is actually removed, so nothing is lost when the machine is broken.
     */
    public void drops() {
        SimpleContainer inv = new SimpleContainer(inventory.size());
        for (int i = 0; i < inventory.size(); i++) {
            ItemAccess itemAccess = ItemAccess.forHandlerIndex(inventory, i);
            inv.setItem(i, new ItemStack(itemAccess.getResource().getItem(), itemAccess.getAmount()));
        }
        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    /** Writes progress + the full inventory contents to the block entity's save data (NBT). */
    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("essence_extractor.progress", progress);
        output.putInt("essence_extractor.max_progress", maxProgress);

        output.putChild("inventory", inventory);
    }

    /** Restores progress + inventory contents from saved data (NBT) when the chunk loads. */
    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        progress = input.getIntOr("essence_extractor.progress", 0);
        maxProgress = input.getIntOr("essence_extractor.max_progress", 72);

        input.child("inventory").ifPresent(inventory::deserialize);
    }

    /**
     * Runs every server tick (wired up via {@link EssenceExtractor#getTicker}).
     * <p>
     * If a valid recipe currently matches the input slots and the output slot has
     * room for the result: advances crafting progress by 1 tick, spawns some portal
     * particles above the block as a visual cue, and actually crafts the item once
     * {@link #maxProgress} ticks have elapsed. Otherwise resets progress back to 0
     * (e.g. if the player removes an ingredient mid-craft).
     */
    public void tick(Level level, BlockPos pos, BlockState state) {
        getCurrentRecipe().ifPresent(recipeHolder -> maxProgress = recipeHolder.value().duration());

        if(hasRecipe() && isOutputSlotEmptyOrReceivable()) {
            increaseCratingProgress();
            setChanged(level, pos, state);

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        net.minecraft.core.particles.ParticleTypes.PORTAL,
                        pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5,
                        3,
                        0.3, 0.2, 0.3,
                        0.02
                );
            }

            if(hasCraftingFinished()) {
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    /** True if the output slot is empty, or has room for more of its current item. */
    private boolean isOutputSlotEmptyOrReceivable() {
        return inventory.getResource(OUTPUT_SLOT).isEmpty() ||
                inventory.getResource(OUTPUT_SLOT).test(stack -> stack.count() < stack.getMaxStackSize());
    }

    /**
     * Actually performs the craft: consumes the recipe's required amount from each
     * input slot and adds the crafted result to the output slot. Wrapped in a
     * {@link Transaction} so the extraction + insertion either all succeed together
     * or (on failure) roll back together, avoiding item duplication/loss bugs.
     */
    private void craftItem() {
        Optional<RecipeHolder<EssenceExtractorRecipe>> recipe = getCurrentRecipe();
        EssenceExtractorRecipe value = recipe.get().value();
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

    /** Resets crafting progress, e.g. after finishing a craft or when ingredients go missing. */
    private void resetProgress() {
        progress = 0;
        maxProgress = 72;
    }

    /** True once enough ticks have passed to finish the current craft. */
    private boolean hasCraftingFinished() {
        return this.progress >= this.maxProgress;
    }

    private void increaseCratingProgress() {
        progress++;
    }

    /**
     * Checks whether a recipe currently matches the input slots AND the result
     * would actually fit in the output slot (right item, enough remaining space).
     */
    private boolean hasRecipe() {
        Optional<RecipeHolder<EssenceExtractorRecipe>> recipe = getCurrentRecipe();
        if(recipe.isEmpty()) {
            return false;
        }

        ItemStack output = recipe.get().value().assemble(currentInput());

        boolean outputSlotAmount = canInsertAmountIntoOutputSlot(output.getCount());
        boolean outputSlotItem = canInsertItemIntoOutputSlot(output);

        return outputSlotAmount && outputSlotItem;
    }

    /**
     * Builds a {@link EssenceExtractorRecipeInput} representing the actual current
     * contents (item + real amount, not just "is a slot filled") of the resource,
     * dust, and crystal slots, for recipe matching.
     */
    private EssenceExtractorRecipeInput currentInput() {
        ItemAccess ressourceAccess = ItemAccess.forHandlerIndex(inventory, INPUT_SLOT);
        ItemAccess dustAccess = ItemAccess.forHandlerIndex(inventory, DUST_SLOT);
        ItemAccess crystalAccess = ItemAccess.forHandlerIndex(inventory, CRYSTAL_SLOT);

        return new EssenceExtractorRecipeInput(
                ressourceAccess.getResource().toStack(ressourceAccess.getAmount()),
                dustAccess.getResource().toStack(dustAccess.getAmount()),
                crystalAccess.getResource().toStack(crystalAccess.getAmount())
        );
    }

    /** Looks up the currently-matching recipe (if any) from the server's recipe manager. */
    private Optional<RecipeHolder<EssenceExtractorRecipe>> getCurrentRecipe() {
        return ((ServerLevel) level).recipeAccess()
                .getRecipeFor(EssentiaRecipes.ESSENCE_EXTRACTOR_TYPE.get(), currentInput(), level);
    }

    /** True if the output slot is empty, or already holds the same item as the given result. */
    private boolean canInsertItemIntoOutputSlot(ItemStack output) {
        return inventory.getResource(OUTPUT_SLOT).isEmpty() ||
                inventory.getResource(OUTPUT_SLOT).is(output.getItem());
    }

    /** True if adding `count` more items to the output slot wouldn't exceed its max stack size. */
    private boolean canInsertAmountIntoOutputSlot(int count) {
        int maxCount = inventory.getResource(OUTPUT_SLOT).isEmpty() ? 64 : inventory.getResource(OUTPUT_SLOT).getMaxStackSize();
        int currentCount = inventory.getAmountAsInt(OUTPUT_SLOT);

        return maxCount >= currentCount + count;
    }


    /* BLOCK ENTITY SYNC */

    /** Packet sent to nearby clients whenever this block entity's data changes (e.g. slot contents). */
    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    /** NBT payload used for the initial sync when a chunk containing this block entity loads for a client. */
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }


}