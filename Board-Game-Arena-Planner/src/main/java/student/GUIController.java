package student;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the GUI version of the Board Game Arena Planner.
 *
 * <p>This class is the Controller in the MVC pattern. It sits between
 * the GUIView and the model layer (IPlanner and IGameList), so that
 * the view never directly touches the model.
 *
 * <p>Built incrementally using Test Driven Development -- one test
 * at a time. Methods are added only as tests require them.
 *
 * @author CS5004 Student
 * @version 1.0
 */
public class GUIController {

    /** The planner used to filter and sort the board game collection. */
    private final IPlanner planner;

    /** The personal game list the user builds during a session. */
    private final IGameList gameList;

    /**
     * The most recent filtered result from the planner.
     * Stored here so that addToList calls can reference it without
     * the view needing to hold any model state.
     */
    private List<BoardGame> currentFiltered;

    /**
     * Constructs a GUIController with the given planner and game list.
     *
     * @param planner  the planner to use for filtering and sorting
     * @param gameList the personal game list to manage
     */
    public GUIController(IPlanner planner, IGameList gameList) {
        this.planner = planner;
        this.gameList = gameList;
        this.currentFiltered = new ArrayList<>();
    }

    /**
     * Returns the number of games currently in the personal list.
     *
     * @return count of games in the personal list
     */
    public int getListCount() {
        return gameList.count();
    }

    /**
     * Applies a filter to the planner and caches the result.
     * The cached result is used as the source for addToList calls.
     *
     * @param filter    the filter expression
     * @param sortOn    the column to sort on
     * @param ascending true for ascending, false for descending
     * @return filtered and sorted list of games
     */
    public List<BoardGame> applyFilter(String filter, GameData sortOn, boolean ascending) {
        String cleaned = filter == null ? "" : filter.replaceAll("\\s+", "").toLowerCase();
        currentFiltered = planner.filter(cleaned, sortOn, ascending)
                .collect(java.util.stream.Collectors.toList());
        return new ArrayList<>(currentFiltered);
    }

    /**
     * Adds games to the personal list from the current filtered result.
     *
     * @param str selector string: name, index, range, or "all"
     * @throws IllegalArgumentException if the selector is invalid
     */
    public void addToList(String str) throws IllegalArgumentException {
        gameList.addToList(str, currentFiltered.stream());
    }

    /**
     * Returns the names of all games in the personal list, sorted A-Z.
     *
     * @return sorted list of game names
     */
    public List<String> getListNames() {
        return gameList.getGameNames();
    }

    /**
     * Removes games from the personal list.
     *
     * @param str selector string: name, index, range, or "all"
     * @throws IllegalArgumentException if the selector is invalid
     */
    public void removeFromList(String str) throws IllegalArgumentException {
        gameList.removeFromList(str);
    }

    /**
     * Removes all games from the personal list.
     */
    public void clearList() {
        gameList.clear();
    }

    /**
     * Resets the planner so the next filter starts from the full collection.
     */
    public void resetFilter() {
        planner.reset();
        currentFiltered = new ArrayList<>();
    }

    /**
     * Saves the personal list to a file, one game name per line.
     *
     * @param filename the path/filename to save to
     */
    public void saveList(String filename) {
        gameList.saveGame(filename);
    }
}
