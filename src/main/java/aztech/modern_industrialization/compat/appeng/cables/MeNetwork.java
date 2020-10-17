package aztech.modern_industrialization.compat.appeng.cables;

import aztech.modern_industrialization.pipes.api.PipeNetwork;
import aztech.modern_industrialization.pipes.api.PipeNetworkData;

public class MeNetwork extends PipeNetwork {
    public MeNetwork(int id, PipeNetworkData data) {
        super(id, data == null ? new MeNetworkData() : data);
    }
}
