package aztech.modern_industrialization.compat.appeng.cables;

import appeng.api.networking.*;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.api.util.AEColor;
import appeng.api.util.AEPartLocation;
import appeng.api.util.DimensionalCoord;
import appeng.core.Api;
import appeng.parts.misc.ToggleBusPart;
import appeng.parts.networking.QuartzFiberPart;
import appeng.parts.p2p.MEP2PTunnelPart;
import aztech.modern_industrialization.pipes.MIPipes;
import aztech.modern_industrialization.pipes.api.PipeEndpointType;
import aztech.modern_industrialization.pipes.api.PipeNetworkNode;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static aztech.modern_industrialization.pipes.api.PipeEndpointType.BLOCK;
import static aztech.modern_industrialization.pipes.api.PipeEndpointType.PIPE;

public class MeNetworkNode extends PipeNetworkNode {
    private byte disabledConnections = 0;
    private final EnumSet<Direction> connections = EnumSet.noneOf(Direction.class);

    private IGridNode gridNode = null;
    private IGridBlock gridBlock = null;

    public IGridNode getGridNode() {
        return gridNode;
    }

    @Override
    public void updateConnections(World world, BlockPos pos) {
        if(gridBlock == null) {
            gridBlock = new GridBlock(world, pos);
        }

        if(gridNode == null) {
            gridNode = Api.instance().grid().createGridNode(gridBlock);
            gridNode.updateState();
        }

        boolean needsUpdate = false;
        for (Direction direction : Direction.values()) {
            if(canConnect(world, pos, direction)) {
                if(connections.add(direction)) {
                    needsUpdate = true;
                }
            } else {
                if(connections.remove(direction)) {
                    needsUpdate = true;
                }
            }
        }
        if(needsUpdate) {
            gridNode.updateState();
        }
    }

    @Override
    public void onRemove() {
        if(gridNode != null) {
            gridNode.destroy();
            gridNode = null;
        }
        gridBlock = null;
    }

    @Override
    public void onUnload() {
        if(gridNode != null) {
            gridNode.destroy();
            gridNode = null;
        }
        gridBlock = null;
    }

    @Override
    public PipeEndpointType[] getConnections(BlockPos pos) {
        PipeEndpointType[] connections = new PipeEndpointType[6];
        for (Direction direction : network.manager.getNodeLinks(pos)) {
            connections[direction.getId()] = PIPE;
        }
        for (Direction connection : this.connections) {
            connections[connection.getId()] = BLOCK;
        }
        return connections;
    }

    @Override
    public void removeConnection(World world, BlockPos pos, Direction direction) {
        disabledConnections |= 1 << direction.getId();
        updateConnections(world, pos);
    }

    @Override
    public void addConnection(World world, BlockPos pos, Direction direction) {
        if((disabledConnections & (1 << direction.getId())) > 0) {
            disabledConnections ^= 1 << direction.getId();
        }
        updateConnections(world, pos);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putByte("disabled", disabledConnections);
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        disabledConnections = tag.getByte("disabled");
    }

    private boolean canConnect(World world, BlockPos pos, Direction direction) {
        if((disabledConnections & (1 << direction.getId())) > 0) {
            return false;
        }

        BlockEntity be = world.getBlockEntity(pos.offset(direction));

        // Thank you EnderIO! :-)
        if (be instanceof IPartHost) { // try to connect to a multipart
            IPart part = ((IPartHost) be).getPart(direction.getOpposite());
            if (part == null) {
                part = ((IPartHost) be).getPart(AEPartLocation.INTERNAL);
                return part != null; // connect if there is nothing on the side, but something in the center
            }
            if (part.getExternalFacingNode() != null) {
                return true; // connect if there is something on the other side of a tunnel
            }
            // otherwise connect to P2P tunnels, quartz fibers or toggle busses
            return part instanceof MEP2PTunnelPart || part instanceof QuartzFiberPart || part instanceof ToggleBusPart;
        } else if (be instanceof IGridHost) { // try to connect to a single block
            IGridNode node = ((IGridHost) be).getGridNode(AEPartLocation.fromFacing(direction.getOpposite()));
            if (node == null) {
                node = ((IGridHost) be).getGridNode(AEPartLocation.INTERNAL);
            }
            if (node != null) {
                return node.getGridBlock().getConnectableSides().contains(direction.getOpposite());
            }
        }
        return false;
    }

    private class GridBlock implements IGridBlock {
        private final DimensionalCoord coord;
        private final IGridHost host;

        public GridBlock(World world, BlockPos pos) {
            this.coord = new DimensionalCoord(world, pos);
            this.host = (IGridHost) world.getBlockEntity(pos);
        }

        @Override
        public double getIdlePowerUsage() {
            return 0;
        }

        @Override
        public @NotNull EnumSet<GridFlags> getFlags() {
            return EnumSet.of(GridFlags.DENSE_CAPACITY);
        }

        @Override
        public boolean isWorldAccessible() {
            return true;
        }

        @Override
        public @NotNull DimensionalCoord getLocation() {
            return coord;
        }

        @Override
        public @NotNull AEColor getGridColor() {
            return AEColor.TRANSPARENT;
        }

        @Override
        public void onGridNotification(@NotNull GridNotification notification) {

        }

        @Override
        public @NotNull EnumSet<Direction> getConnectableSides() {
            EnumSet<Direction> directions = EnumSet.noneOf(Direction.class);
            PipeEndpointType[] endpoints = getConnections(coord.getBlockPos());
            for (Direction direction : Direction.values()) {
                if (endpoints[direction.getId()] != null) {
                    directions.add(direction);
                }
            }
            return directions;
        }

        @Override
        public @NotNull IGridHost getMachine() {
            return host;
        }

        @Override
        public void gridChanged() {
            updateConnections(coord.getWorld(), coord.getBlockPos());
        }

        @Override
        public @NotNull ItemStack getMachineRepresentation() {
            return new ItemStack(MIPipes.INSTANCE.getPipeItem(getType()));
        }
    }
}
