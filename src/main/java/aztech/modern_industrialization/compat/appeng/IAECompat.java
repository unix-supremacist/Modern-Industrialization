package aztech.modern_industrialization.compat.appeng;

import aztech.modern_industrialization.pipes.api.PipeNetworkData;
import aztech.modern_industrialization.pipes.api.PipeNetworkType;

public interface IAECompat {
    boolean isAvailable();

    PipeNetworkType registerNetworkType();

    PipeNetworkData getItemData();
}
