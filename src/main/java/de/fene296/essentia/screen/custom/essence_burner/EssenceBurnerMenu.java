package de.fene296.essentia.screen.custom.essence_burner;

import de.fene296.essentia.block.EssentiaBlocks;
import de.fene296.essentia.block.entity.EssenceBurnerEntity;
import de.fene296.essentia.screen.EssentiaMenuTypes;
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
 * Server- and client-side container menu (GUI logic) for the Essence Burner.
 * <p>
 * Defines the single crystal slot plus the standard player inventory/hotbar slots,
 * wires up burn-progress syncing via {@link ContainerData}, and handles shift-click
 * ("quick move") behavior between the burner and the player's inventory.
 */
public class EssenceBurnerMenu extends AbstractContainerMenu {

    /** The block entity this menu is displaying/editing. */
    public final EssenceBurnerEntity blockEntity;
    private final Level level;

    /** Synced burn data: index 0 = remaining burn time, index 1 = active essence type ordinal (-1 = none). */
    private final ContainerData data;

    /**
     * Client-side constructor: called automatically by the menu-opening network packet.
     * Builds placeholder inventory/data objects that get overwritten by the real synced
     * values from the server.
     */
    public EssenceBurnerMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()),
                new ItemStacksResourceHandler(1), new SimpleContainerData(2));
    }

    /**
     * Server-side constructor (and the one the client constructor above delegates to):
     * builds the actual slot layout tied to the real block entity's inventory and
     * burn-progress data.
     */
    public EssenceBurnerMenu(int pContainerId, Inventory inv, BlockEntity entity, ItemStacksResourceHandler handler, ContainerData data) {
        super(EssentiaMenuTypes.ESSENCE_BURNER_MENU.get(), pContainerId);
        blockEntity = ((EssenceBurnerEntity) entity);
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv, 8, 84);
        addPlayerHotbar(inv, 8, 142);

        // Crystal slot (slot 0): the only slot on this machine.
        this.addSlot(new ResourceHandlerSlot(handler, handler::set, 0, 80, 28));

        addDataSlots(data);
    }

    /** True while the burner is actively burning a crystal (used by the screen to animate the flame). */
    public boolean isBurning() {
        return data.get(0) > 0;
    }

    /**
     * Returns the remaining burn time scaled to a pixel height (0 to {@code flamePixelSize}),
     * for the screen to render a furnace-style shrinking flame icon.
     */
    public int getScaledBurnProgress(int flamePixelSize) {
        int burnTime = data.get(0);
        return burnTime * flamePixelSize / EssenceBurnerEntity.BURN_DURATION;
    }

    /** The currently active essence type's ordinal (-1 if the burner isn't active), for the screen to use e.g. as a tint/icon. */
    public int getActiveTypeOrdinal() {
        return data.get(1);
    }

    // --- Slot index bookkeeping, used by quickMoveStack (shift-click) below ---
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    private static final int TE_INVENTORY_SLOT_COUNT = 1;  // must be the number of slots you have!

    /**
     * Handles shift-clicking an item in this menu: moves it to "the other side"
     * (burner slot <-> player inventory) rather than just to the same side.
     */
    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }

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
                pPlayer, EssentiaBlocks.ESSENCE_BURNER.get());
    }

    private void addPlayerInventory(Inventory playerInventory, int firstSlotX, int firstSlotY) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, firstSlotX + l * 18, firstSlotY + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory, int firstSlotX, int firstSlotY) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, firstSlotX + i * 18, firstSlotY));
        }
    }
}

