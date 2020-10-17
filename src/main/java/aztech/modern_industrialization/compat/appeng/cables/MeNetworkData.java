package aztech.modern_industrialization.compat.appeng.cables;

import aztech.modern_industrialization.pipes.api.PipeNetworkData;
import net.minecraft.nbt.CompoundTag;

public class MeNetworkData extends PipeNetworkData {
    @Override
    public PipeNetworkData clone() {
        return new MeNetworkData();
    }

    @Override
    public void fromTag(CompoundTag tag) {

    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        return tag;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MeNetworkData;
    }
}
