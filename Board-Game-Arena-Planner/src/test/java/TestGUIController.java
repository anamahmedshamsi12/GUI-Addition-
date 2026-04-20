import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import student.BoardGame;
import student.GameList;
import student.GUIController;
import student.IGameList;
import student.IPlanner;
import student.Planner;
import student.GameData;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import java.util.List;
import student.BoardGame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestGUIController {

    static Set<BoardGame> games;
    GUIController controller;

    @BeforeAll
    public static void setupGames() {
        games = new HashSet<>();
        games.add(new BoardGame("17 days", 6, 1, 8, 70, 70, 9.0, 600, 9.0, 2005));
        games.add(new BoardGame("Chess", 7, 2, 2, 10, 20, 10.0, 700, 10.0, 2006));
        games.add(new BoardGame("Go", 1, 2, 5, 30, 30, 8.0, 100, 7.5, 2000));
        games.add(new BoardGame("Go Fish", 2, 2, 10, 20, 120, 3.0, 200, 6.5, 2001));
        games.add(new BoardGame("golang", 4, 2, 7, 50, 55, 7.0, 400, 9.5, 2003));
        games.add(new BoardGame("GoRami", 3, 6, 6, 40, 42, 5.0, 300, 8.5, 2002));
        games.add(new BoardGame("Monopoly", 8, 6, 10, 20, 1000, 1.0, 800, 5.0, 2007));
        games.add(new BoardGame("Tucano", 5, 10, 20, 60, 90, 6.0, 500, 8.0, 2004));
    }

    @BeforeEach
    public void setupController() {
        IPlanner planner = new Planner(games);
        IGameList list = new GameList();
        controller = new GUIController(planner, list);
    }

    @Test
    public void controllerListStartsEmpty() {
        assertEquals(0, controller.getListCount());
    }

    @Test
    public void controllerAddGameByName() {
        controller.applyFilter("", GameData.NAME, true);
        controller.addToList("Chess");
        assertEquals(1, controller.getListCount());
    }

    @Test
    public void controllerAddAllGamesFromFilter() {
        controller.applyFilter("name~=go", GameData.NAME, true);
        controller.addToList("all");
        assertEquals(4, controller.getListCount());
    }

    @Test
    public void controllerAddGameByPosition() {
        controller.applyFilter("name~=go", GameData.NAME, true);
        controller.addToList("1");
        assertEquals(1, controller.getListCount());
        assertEquals("Go", controller.getListNames().get(0));
    }

    @Test
    public void controllerAddInvalidNameThrowsException() {
        controller.applyFilter("name~=go", GameData.NAME, true);
        assertThrows(IllegalArgumentException.class, () -> {
            controller.addToList("Chess");
        });
    }

    @Test
    public void controllerRemoveGameByName() {
        controller.applyFilter("name~=go", GameData.NAME, true);
        controller.addToList("all");
        controller.removeFromList("Go");
        assertEquals(3, controller.getListCount());
        assertFalse(controller.getListNames().contains("Go"));
    }

    @Test
    public void controllerRemoveGameNotInListThrows() {
        controller.applyFilter("name~=go", GameData.NAME, true);
        controller.addToList("all");
        assertThrows(IllegalArgumentException.class, () -> {
            controller.removeFromList("Chess");
        });
    }

    @Test
    public void controllerClearRemovesAllGames() {
        controller.applyFilter("", GameData.NAME, true);
        controller.addToList("all");
        controller.clearList();
        assertEquals(0, controller.getListCount());
    }

    @Test
    public void controllerListNamesAlphabetical() {
        controller.applyFilter("name~=go", GameData.NAME, true);
        controller.addToList("all");
        List<String> names = controller.getListNames();
        assertEquals("Go", names.get(0));
        assertEquals("Go Fish", names.get(1));
        assertEquals("golang", names.get(2));
        assertEquals("GoRami", names.get(3));
    }

    @Test
    public void controllerNoDuplicatesInList() {
        controller.applyFilter("name==Go", GameData.NAME, true);
        controller.addToList("Go");
        controller.resetFilter();
        controller.applyFilter("name==Go", GameData.NAME, true);
        controller.addToList("Go");
        assertEquals(1, controller.getListCount());
    }

    @Test
    public void controllerEmptyFilterReturnsFullCollection() {
        List<BoardGame> result = controller.applyFilter("", GameData.NAME, true);
        assertEquals(8, result.size());
        assertEquals("17 days", result.get(0).getName());
    }

    @Test
    public void controllerFilterByMinPlayersFloor() {
        List<BoardGame> result = controller.applyFilter("minPlayers>=4", GameData.NAME, true);
        assertEquals(3, result.size());
        for (BoardGame game : result) {
            assertTrue(game.getMinPlayers() >= 4);
        }
    }

    @Test
    public void controllerFilterByNamePartialMatch() {
        List<BoardGame> result = controller.applyFilter("name~=go", GameData.NAME, true);
        assertEquals(4, result.size());
        for (BoardGame game : result) {
            assertTrue(game.getName().toLowerCase().contains("go"));
        }
    }

    @Test
    public void controllerFiltersStackWithoutReset() {
        List<BoardGame> first = controller.applyFilter("name~=go", GameData.NAME, true);
        assertEquals(4, first.size());
        List<BoardGame> second = controller.applyFilter("minPlayers>=6", GameData.NAME, true);
        assertEquals(1, second.size());
        assertEquals("GoRami", second.get(0).getName());
    }

    @Test
    public void controllerResetRestoresFullCollection() {
        controller.applyFilter("name==Go", GameData.NAME, true);
        controller.resetFilter();
        List<BoardGame> result = controller.applyFilter("", GameData.NAME, true);
        assertEquals(8, result.size());
    }

    @Test
    public void controllerSortDescendingPutsHighestFirst() {
        List<BoardGame> result = controller.applyFilter("", GameData.RATING, false);
        assertEquals("Chess", result.get(0).getName());
    }
}

