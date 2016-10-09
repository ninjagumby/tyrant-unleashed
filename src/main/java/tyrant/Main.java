package tyrant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LOGGER.info("Starting up.");
        TyrantClient client = new TyrantClient();

        TyrantGui gui = new TyrantGui(client);

        gui.setVisible(true);
    }

}
