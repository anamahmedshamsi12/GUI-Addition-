# Domain Name Information  Part 2 - Design Document


This document is meant to provide a tool for you to demonstrate the design process. You need to work on this before you code, and after have a finished product. That way you can compare the changes, and changes in design are normal as you work through a project. It is contrary to popular belief, but we are not perfect our first attempt. We need to iterate on our designs to make them better. This document is a tool to help you do that.


## (INITIAL DESIGN): Class Diagram

Place your class diagram below. If you are using the mermaid markdown, you may include the code for it here. For a reminder on the mermaid syntax, you may go [here](https://mermaid.js.org/syntax/classDiagram.html)

```mermaid
classDiagram
    direction TB

%% ── ENTRY POINT ───────────────────────────────────────────────────────────
    class BGArenaPlanner {
        +main(String[] args)$
    }

%% ── VIEW LAYER ────────────────────────────────────────────────────────────
    class JFrame {
        <<Swing>>
    }

    class GUIView {
        -GUIController controller
        +GUIView(GUIController controller)
    }

    class ConsoleApp {
        -IPlanner planner
        -IGameList gameList
        +ConsoleApp(IGameList gameList, IPlanner planner)
        +start() void
    }

%% ── CONTROLLER LAYER ──────────────────────────────────────────────────────
    class GUIController {
        -IPlanner planner
        -IGameList gameList
        -List~BoardGame~ currentFiltered
        +GUIController(IPlanner planner, IGameList gameList)
        +applyFilter(String filter, GameData sortOn, boolean ascending) List~BoardGame~
        +resetFilter() void
        +addToList(String str) void
        +removeFromList(String str) void
        +clearList() void
        +getListNames() List~String~
        +getListCount() int
        +saveList(String filename) void
    }

%% ── MODEL LAYER ───────────────────────────────────────────────────────────
    class IPlanner {
        <<interface>>
        +filter(String filter) Stream~BoardGame~
        +filter(String filter, GameData sortOn, boolean ascending) Stream~BoardGame~
        +reset() void
    }

    class IGameList {
        <<interface>>
        +addToList(String str, Stream~BoardGame~ filtered) void
        +removeFromList(String str) void
        +getGameNames() List~String~
        +clear() void
        +count() int
        +saveGame(String filename) void
    }

    class Planner {
        -Set~BoardGame~ allGames
        -Set~BoardGame~ filteredGames
        +Planner(Set~BoardGame~ games)
    }

    class GameList {
        -Set~BoardGame~ games
        +GameList()
    }

    class BoardGame {
        -String name
        -int id
        -int minPlayers
        -int maxPlayers
        -double averageRating
        -double difficulty
        -int rank
        +getName() String
        +getRating() double
        +getMinPlayers() int
        +getMaxPlayers() int
    }

    class GameData {
        <<enumeration>>
        NAME
        RATING
        DIFFICULTY
        RANK
        MIN_PLAYERS
        MAX_PLAYERS
        MIN_TIME
        MAX_TIME
        YEAR
        +getColumnName() String
        +fromString(String name)$ GameData
    }

    class GamesLoader {
        +loadGamesFile(String filename)$ Set~BoardGame~
    }

%% ── RELATIONSHIPS ─────────────────────────────────────────────────────────

%% Entry point creates everything at startup
    BGArenaPlanner ..> GUIController  : creates
    BGArenaPlanner ..> GUIView        : creates
    BGArenaPlanner ..> ConsoleApp     : creates
    BGArenaPlanner ..> GamesLoader    : uses

%% GUIView inherits window behavior from JFrame (inheritance)
    GUIView --|> JFrame

%% GUIView owns its controller (composition — view cannot exist without it)
    GUIView *-- GUIController         : has

%% ConsoleApp owns its model references (composition)
    ConsoleApp *-- IPlanner           : has
    ConsoleApp *-- IGameList          : has

%% GUIController owns its model references (composition)
    GUIController *-- IPlanner        : has
    GUIController *-- IGameList       : has

%% GUIController uses BoardGame and GameData as method parameter/return types
    GUIController ..> BoardGame       : returns List
    GUIController ..> GameData        : uses

%% Concrete model classes implement their interfaces
    Planner  ..|> IPlanner            : implements
    GameList ..|> IGameList           : implements

%% Model stores and operates on BoardGame objects
    Planner  ..> BoardGame            : stores / filters
    GameList ..> BoardGame            : stores
    GamesLoader ..> BoardGame         : creates
```

## (INITIAL DESIGN): Tests to Write - Brainstorm

Write a test (in english) that you can picture for the class diagram you have created. This is the brainstorming stage in the TDD process. 

> [!TIP]
> As a reminder, this is the TDD process we are following:
> 1. Figure out a number of tests by brainstorming (this step)
> 2. Write **one** test
> 3. Write **just enough** code to make that test pass
> 4. Refactor/update  as you go along
> 5. Repeat steps 2-4 until you have all the tests passing/fully built program

You should feel free to number your brainstorm. 

1. Test 1..
2. Test 2..




## (FINAL DESIGN): Class Diagram

Go through your completed code, and update your class diagram to reflect the final design. It is normal that the two diagrams don't match! Rarely (though possible) is your initial design perfect. 

> [!WARNING]
> If you resubmit your assignment for manual grading, this is a section that often needs updating. You should double check with every resubmit to make sure it is up to date.





## (FINAL DESIGN): Reflection/Retrospective

> [!IMPORTANT]
> The value of reflective writing has been highly researched and documented within computer science, from learning to information to showing higher salaries in the workplace. For this next part, we encourage you to take time, and truly focus on your retrospective.

Take time to reflect on how your design has changed. Write in *prose* (i.e. do not bullet point your answers - it matters in how our brain processes the information). Make sure to include what were some major changes, and why you made them. What did you learn from this process? What would you do differently next time? What was the most challenging part of this process? For most students, it will be a paragraph or two. 
