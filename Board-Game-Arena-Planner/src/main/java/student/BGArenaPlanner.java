package student;

/**
 * Main entry point for the Board Game Arena Planner application.
 *
 * <p>Supports three launch modes selected via a command-line flag:
 * <ul>
 *   <li>{@code -c} – run the console version (default)</li>
 *   <li>{@code -g} – launch the Swing GUI version</li>
 *   <li>{@code -h} – print usage help and exit</li>
 * </ul>
 *
 * @author CS5004 Student
 * @version 1.0
 */
public final class BGArenaPlanner {

    /** Default location of the game collection CSV. */
    private static final String DEFAULT_COLLECTION = "/collection.csv";

    /** Private constructor — static utility class. */
    private BGArenaPlanner() {
    }

    /**
     * Main entry point.
     *
     * @param args command-line arguments; first argument selects the mode
     */
    public static void main(String[] args) {
        String mode = (args.length > 0) ? args[0] : "-c";

        switch (mode) {
            case "-g":
                IPlanner guiPlanner = new Planner(GamesLoader.loadGamesFile(DEFAULT_COLLECTION));
                IGameList guiList = new GameList();
                GUIController controller = new GUIController(guiPlanner, guiList);
                javax.swing.SwingUtilities.invokeLater(() -> new GUIView(controller));
                break;
            case "-h":
                System.out.println("Usage: BGArenaPlanner [-c | -g | -h]");
                System.out.println("  -c   Console version (default)");
                System.out.println("  -g   GUI version");
                System.out.println("  -h   Help");
                break;
            case "-c":
            default:
                IPlanner planner = new Planner(GamesLoader.loadGamesFile(DEFAULT_COLLECTION));
                IGameList list = new GameList();
                ConsoleApp app = new ConsoleApp(list, planner);
                app.start();
                break;
        }
    }
}
