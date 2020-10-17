package aztech.modern_industrialization.compat.appeng;

import aztech.modern_industrialization.pipes.api.PipeNetworkData;
import aztech.modern_industrialization.pipes.api.PipeNetworkType;

public class AbsentAECompat implements IAECompat {
    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public PipeNetworkType registerNetworkType() {
        return null;
    }

    @Override
    public PipeNetworkData getItemData() {
        return null;
    }
}
