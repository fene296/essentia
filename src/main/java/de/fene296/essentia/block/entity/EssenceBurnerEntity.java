package de.fene296.essentia.block.entity;

import de.fene296.essentia.block.EssentiaBlockEntities;
import de.fene296.essentia.screen.custom.essence_burner.EssenceBurnerMenu;
import de.fene296.essentia.util.EssenceType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.DustParticleOptions;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

public class EssenceBurnerEntity extends BlockEntity implements MenuProvider {

    private static final int CRYSTAL_SLOT = 0;

    public static final int BURN_DURATION = 1200;

    public static final int RANGE = 2;

    public final ItemStacksResourceHandler inventory = new ItemStacksResourceHandler(1) {
        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            super.onContentsChanged(index, previousContents);
            EssenceBurnerEntity.this.setChanged();

            if(level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    };

    private int burnTime = 0;

    private int activeTypeOrdinal = -1;

    protected final ContainerData data;

    public EssenceBurnerEntity(BlockPos worldPosition, BlockState blockState) {
        super(EssentiaBlockEntities.ESSENCE_BURNER_BE.get(), worldPosition, blockState);
        data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> burnTime;
                    case 1 -> activeTypeOrdinal;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                switch (i) {
                    case 0 -> burnTime = value;
                    case 1 -> activeTypeOrdinal = value;
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
        return Component.translatable("block.essentia.essence_burner");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new EssenceBurnerMenu(i, inventory, this, this.inventory, this.data);
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(inventory.size());
        for (int i = 0; i < inventory.size(); i++) {
            ItemAccess itemAccess = ItemAccess.forHandlerIndex(inventory, i);
            inv.setItem(i, new ItemStack(itemAccess.getResource().getItem(), itemAccess.getAmount()));
        }
        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (burnTime > 0) {
            burnTime--;
            if (burnTime == 0) {
                activeTypeOrdinal = -1;
            }
            setChanged();

            spawnActiveParticles(level, pos);
        } else {
            tryStartBurning();
        }
    }

    /**
     * Spawns a thin vertical stream of particles above the burner, colored to match
     * the currently active {@link EssenceType} (see {@link EssenceType#getColor()}).
     */
    private void spawnActiveParticles(Level level, BlockPos pos) {
        EssenceType type = getActiveType();
        if (type == null || !(level instanceof ServerLevel serverLevel)) {
            return;
        }

        DustParticleOptions particleOptions = new DustParticleOptions(type.getColor(), 1.0f);

        double x = pos.getX() + 0.5;
        double z = pos.getZ() + 0.5;
        for (double y = pos.getY() + 1.0; y <= pos.getY() + 1.5; y += 0.15) {
            serverLevel.sendParticles(particleOptions, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    private void tryStartBurning() {
        ItemStack crystalStack = inventory.getResource(CRYSTAL_SLOT).toStack(1);
        EssenceType.fromItem(crystalStack.getItem()).ifPresent(type -> {
            try (Transaction transaction = Transaction.openRoot()) {
                inventory.extract(CRYSTAL_SLOT, inventory.getResource(CRYSTAL_SLOT), 1, transaction);
                transaction.commit();
            }
            burnTime = BURN_DURATION;
            activeTypeOrdinal = type.ordinal();
            setChanged();
        });
    }

    /** True while this burner is currently burning a crystal (i.e. has an active essence type). */
    public boolean isActive() {
        return burnTime > 0 && activeTypeOrdinal >= 0;
    }

    @Nullable
    public EssenceType getActiveType() {
        return isActive() ? EssenceType.values()[activeTypeOrdinal] : null;
    }

    /**
     * Checks whether an Essence Burner actively burning the given {@link EssenceType}
     * exists within {@link #RANGE} blocks of {@code pos}. Intended for other machines
     * to call from their own tick logic to gate crafting on "is the right burner nearby".
     * <p>
     * NOTE: this scans every block position in a ({@code 2*RANGE+1})^3 cube each call,
     * which is fine for occasional per-tick checks from a handful of machines, but
     * could get expensive if called very frequently or from many machines at once -
     * worth revisiting (e.g. caching) if that becomes a problem.
     */
    public static boolean isTypeActiveNearby(Level level, BlockPos pos, EssenceType requiredType) {
        return findActiveNearby(level, pos, requiredType).isPresent();
    }

    /**
     * Same search as {@link #isTypeActiveNearby}, but returns the position of the
     * matching burner (if found) instead of just a boolean - useful for reporting
     * back to the player, debugging, etc.
     */
    public static java.util.Optional<BlockPos> findActiveNearby(Level level, BlockPos pos, EssenceType requiredType) {
        BlockPos min = pos.offset(-RANGE, -RANGE, -RANGE);
        BlockPos max = pos.offset(RANGE, RANGE, RANGE);

        for (BlockPos checkPos : BlockPos.betweenClosed(min, max)) {
            if (level.getBlockEntity(checkPos) instanceof EssenceBurnerEntity burner
                    && burner.isActive()
                    && burner.getActiveType() == requiredType) {
                return java.util.Optional.of(checkPos.immutable());
            }
        }
        return java.util.Optional.empty();
    }

    /**
     * A single found burner: its position and which essence type it's currently
     * burning. Returned by {@link #findAllActiveNearby}.
     */
    public record ActiveBurner(BlockPos pos, EssenceType type) {}

    /**
     * Finds every currently-active Essence Burner (any essence type) within
     * {@link #RANGE} blocks of {@code pos} - unlike {@link #findActiveNearby}, which
     * only looks for one specific type and stops at the first match.
     */
    public static java.util.List<ActiveBurner> findAllActiveNearby(Level level, BlockPos pos) {
        BlockPos min = pos.offset(-RANGE, -RANGE, -RANGE);
        BlockPos max = pos.offset(RANGE, RANGE, RANGE);

        java.util.List<ActiveBurner> found = new java.util.ArrayList<>();
        for (BlockPos checkPos : BlockPos.betweenClosed(min, max)) {
            if (level.getBlockEntity(checkPos) instanceof EssenceBurnerEntity burner && burner.isActive()) {
                found.add(new ActiveBurner(checkPos.immutable(), burner.getActiveType()));
            }
        }
        return found;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("essence_burner.burn_time", burnTime);
        output.putInt("essence_burner.active_type", activeTypeOrdinal);
        output.putChild("inventory", inventory);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        burnTime = input.getIntOr("essence_burner.burn_time", 0);
        activeTypeOrdinal = input.getIntOr("essence_burner.active_type", -1);
        input.child("inventory").ifPresent(inventory::deserialize);
    }

    /* BLOCK ENTITY SYNC */
    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }


}
