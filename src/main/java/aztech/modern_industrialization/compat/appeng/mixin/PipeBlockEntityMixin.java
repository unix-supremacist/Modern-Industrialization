package aztech.modern_industrialization.compat.appeng.mixin;

import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import aztech.modern_industrialization.compat.appeng.cables.MeNetworkNode;
import aztech.modern_industrialization.pipes.api.PipeEndpointType;
import aztech.modern_industrialization.pipes.api.PipeNetworkNode;
import aztech.modern_industrialization.pipes.impl.PipeBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.SortedSet;

@Mixin(PipeBlockEntity.class)
public abstract class PipeBlockEntityMixin extends BlockEntity implements IGridHost {
    @Shadow
    @Final
    private SortedSet<PipeNetworkNode> pipes;

    @Shadow
    protected abstract void loadPipes();

    public PipeBlockEntityMixin(BlockEntityType<?> type) {
        super(type);
    }

    @Override
    public IGridNode getGridNode(@NotNull AEPartLocation dir) {
        for(PipeNetworkNode node : pipes) {
            if(node instanceof MeNetworkNode) {
                MeNetworkNode meNode = (MeNetworkNode) node;
                PipeEndpointType[] connections = meNode.getConnections(getPos());
                if(dir == AEPartLocation.INTERNAL || connections[dir.getOpposite().getFacing().getId()] == PipeEndpointType.BLOCK) {
                    return meNode.getGridNode();
                }
            }
        }
        return null;
    }

    @Override
    public @NotNull AECableType getCableConnectionType(@NotNull AEPartLocation dir) {
        for(PipeNetworkNode node : pipes) {
            if(node instanceof MeNetworkNode) {
                MeNetworkNode meNode = (MeNetworkNode) node;
                PipeEndpointType[] connections = meNode.getConnections(getPos());
                if(dir == AEPartLocation.INTERNAL || connections[dir.getOpposite().getFacing().getId()] != null) {
                    return AECableType.GLASS;
                }
            }
        }
        return AECableType.NONE;
    }

    @Override
    public void securityBreak() {

    }
}
