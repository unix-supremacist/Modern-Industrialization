package aztech.modern_industrialization.compat.appeng;

import aztech.modern_industrialization.MIIdentifier;
import aztech.modern_industrialization.compat.appeng.cables.MeNetwork;
import aztech.modern_industrialization.compat.appeng.cables.MeNetworkData;
import aztech.modern_industrialization.compat.appeng.cables.MeNetworkNode;
import aztech.modern_industrialization.pipes.MIPipes;
import aztech.modern_industrialization.pipes.api.PipeNetworkData;
import aztech.modern_industrialization.pipes.api.PipeNetworkType;

import java.util.Arrays;

public class PresentAECompat implements IAECompat {
    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public PipeNetworkType registerNetworkType() {
        PipeNetworkType type = PipeNetworkType.register(
                new MIIdentifier("me"),
                MeNetwork::new,
                MeNetworkNode::new,
                -1,
                false,
                MIPipes.makeRenderer(Arrays.asList("me", "me_block"), false)
        );
        return type;
    }

    @Override
    public PipeNetworkData getItemData() {
        return new MeNetworkData();
    }
}
