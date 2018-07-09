package core.task.blockPets;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockPetsStarter {
    private static Logger logger = LoggerFactory.getLogger(BlockPetsStarter.class);
    private static final String LOG_PREFIX = "【Block Pets】";

    public void start() {
        while (true){
            BlockPetsWalkTask.excute();
        }
    }
}
