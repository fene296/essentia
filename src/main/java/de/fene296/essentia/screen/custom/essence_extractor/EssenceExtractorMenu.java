package de.fene296.essentia.screen.custom.essence_extractor;

import de.fene296.essentia.block.EssentiaBlocks;
import de.fene296.essentia.block.entity.EssenceExtractorEntity;
import de.fene296.essentia.item.EssentiaItems;
import de.fene296.essentia.screen.EssentiaMenuTypes;
import de.fene296.essentia.tags.EssentiaTags;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

/**
 * Server- and client-side container menu (GUI logic) for the Essence Extractor.
 * <p>
 * Defines the machine's 4 item slots (resource, dust, crystal, output) plus the
 * standard player inventory/hotbar slots, wires up progress-bar syncing via
 * {@link ContainerData}, and handles shift-click ("quick move") behavior between
 * the machine and the player's inventory.
 */
public class EssenceExtractorMenu extends AbstractContainerMenu {

    /** The block entity this menu is displaying/editing. */
    public final EssenceExtractorEntity blockEntity;
    private final Level level;

    /** Synced progress data (current/max crafting progress) used to render the progress arrow. */
    private final ContainerData data;

    /**
     * Client-side constructor: called automatically by the menu-opening network packet.
     * Builds placeholder inventory/data objects that get overwritten by the real synced
     * values from the server (the actual block entity + real data live server-side).
     */
    public EssenceExtractorMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()),
                new ItemStacksResourceHandler(4), new SimpleContainerData(4));
    }

    /**
     * Server-side constructor (and the one the client constructor above delegates to):
     * builds the actual slot layout tied to the real block entity's inventory and
     * progress data.
     */
    public EssenceExtractorMenu(int pContainerId, Inventory inv, BlockEntity entity, ItemStacksResourceHandler handler, ContainerData data) {
        super(EssentiaMenuTypes.ESSENCE_EXTRACTOR_MENU.get(), pContainerId);
        blockEntity = ((EssenceExtractorEntity) entity);
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv, 8, 84);
        addPlayerHotbar(inv, 8, 142);

        // Resource slot (slot 0): accepts anything valid per the active recipe - no
        // extra restriction here beyond what the recipe itself checks at craft time.
        this.addSlot(new ResourceHandlerSlot(handler, handler::set,0, 43,  17));

        // Dust slot (slot 1): restricted to only accept items tagged as "magic dust"
        // (e.g. Essence Dust and any other magical-dust-type items added to that tag later)
        this.addSlot(new ResourceHandlerSlot(handler, handler::set,1, 19,  52) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(EssentiaTags.Items.MAGIC_DUST);
            }
        });

        // Crystal slot (slot 2): same as above, unrestricted at the slot level.
        this.addSlot(new ResourceHandlerSlot(handler, handler::set,2, 67,  52));

        // Output slot (slot 3): players can only take items out, never place items in
        // directly - it's only ever filled by the crafting logic itself.
        this.addSlot(new ResourceHandlerSlot(handler, handler::set,3, 137, 34) {
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return false;
            }
        });

        addDataSlots(data);
    }

    /** True while a craft is actively in progress (used by the screen to show the progress arrow). */
    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    /**
     * Returns the current progress arrow width in pixels (0 to {@code arrowPixelSize}),
     * scaled from the synced progress/maxProgress values, for the screen to render.
     */
    public int getScaledArrowProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int arrowPixelSize = 24;

        return maxProgress != 0 && progress != 0 ? progress * arrowPixelSize / maxProgress : 0;
    }

    // --- Slot index bookkeeping, used by quickMoveStack (shift-click) below ---
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    private static final int TE_INVENTORY_SLOT_COUNT = 4;  // must be the number of slots you have!

    /**
     * Handles shift-clicking an item in this menu: moves it to "the other side"
     * (machine slots <-> player inventory) rather than just to the same side.
     * <p>
     * Note: this does NOT check {@code mayPlace} restrictions per-slot when merging
     * into the machine's 4 slots (e.g. it doesn't specifically try to put crystals
     * only into the crystal slot) - {@link #moveItemStackTo} tries slots in index
     * order and relies on each slot's own {@code mayPlace} to reject invalid items,
     * so shift-clicking a random item may just fail to move rather than land in the
     * "wrong" slot.
     */
    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    /** True as long as the block entity still exists and the player is close enough to use it. */
    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, EssentiaBlocks.ESSENCE_EXTRACTOR.get());
    }

    /**
     * Adds the standard 3x9 player inventory grid (below the hotbar in the GUI),
     * anchored at the given top-left pixel position.
     *
     * @param firstSlotX x pixel coordinate of the top-left slot
     * @param firstSlotY y pixel coordinate of the top-left slot
     */
    private void addPlayerInventory(Inventory playerInventory, int firstSlotX, int firstSlotY) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, firstSlotX + l * 18, firstSlotY + i * 18));
            }
        }
    }

    /**
     * Adds the player's hotbar (bottom row of the GUI), anchored at the given
     * top-left pixel position.
     *
     * @param firstSlotX x pixel coordinate of the first (leftmost) hotbar slot
     * @param firstSlotY y pixel coordinate of the hotbar row
     */
    private void addPlayerHotbar(Inventory playerInventory, int firstSlotX, int firstSlotY) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, firstSlotX + i * 18, firstSlotY));
        }
    }
}