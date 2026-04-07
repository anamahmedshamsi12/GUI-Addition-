# Manual

This folder could contain information and screenshot on how to use the program.  If following this, we will get
an idea of what elements you have in the GUI, and to fully use it. Feel free to add additional files and link them.
---
## Launching the App

To open the GUI run this command from inside the Board-Game-Arena-Planner directory:

```
./gradlew run --args="-g"
```

If you want the original console version instead you can run:

```
./gradlew run --args="-c"
```

---

## The Main Window

When the app opens all 753 games load automatically into the Search Results table sorted A to Z by name. My Game List on the right starts empty. You do not have to do anything to load the games.

![Main window](../screenshots/home_page.png)

The window has three sections. The purple filter bar at the top is where you type filter expressions and control sorting. The Search Results table on the left shows the games matching your current filter. My Game List on the right is your personal list of games you want to play.

---

## Filtering Games

Type a filter expression into the Filter field and either press Enter or click Apply Filter. The table updates to show only the matching games and the status bar shows how many results came back.

Here are the filter operators you can use:

| Expression | What it does |
|---|---|
| `name~=dungeon` | name contains "dungeon" (case insensitive) |
| `name==Agricola` | name is exactly "Agricola" |
| `rating>8` | rating is greater than 8 |
| `minPlayers>=4` | minimum players is 4 or more |
| `maxPlayers<6` | maximum players is less than 6 |
| `rating>8,minPlayers<=4` | both conditions must be true |

The column names you can filter on are: `name`, `rating`, `difficulty`, `rank`, `minPlayers`, `maxPlayers`, `minTime`, `maxTime`, and `year`.

For example typing `name~=dungeon` returns all games whose name contains dungeon:

![Filter by name](../screenshots/filter_name.png)

---

## Sorting Results

Use the Sort by dropdown to pick which column to sort on. The Ascending checkbox controls the direction. Uncheck it to sort descending.

For example switching Sort by to Rating and unchecking Ascending shows the highest rated games first:

![Sort by rating descending](../screenshots/sort_rating.png)

---

## Resetting the Filter

Click the Reset button to clear whatever filter is active and go back to all 753 games sorted A to Z. This also resets the sort back to Name ascending.

---

## Adding Games to My List

To add a specific game click its row in the Search Results table to select it (the row turns purple) and then click Add Selected to My List. You can hold Cmd on Mac or Ctrl on Windows to select multiple rows at once before clicking the button.

![Add selected game](../screenshots/add_selected.png)

To add every game currently showing in the Search Results table at once click Add All to My List. If all 753 games are showing all 753 get added:

![Add all games](../screenshots/add_all.png)

My Game List always stays sorted A to Z regardless of what order you added things in. If you try to add the same game twice it just gets ignored since duplicates are not allowed.

---

## Removing Games from My List

Click a game in My Game List to select it (it turns purple) and then click the red Remove Selected button. You can select multiple games at once the same way as in the results table.

Before removing:

![Before removing](../screenshots/remove_selected_before.png)

After removing 13 Clues the count drops from 753 to 752:

![After removing](../screenshots/remove_selected_after.png)

To remove everything at once click the red Clear All button. A confirmation dialog will appear showing how many games are about to be removed:

![Clear all dialog](../screenshots/clear_all.png)

Click Yes to confirm and the list will be completely emptied:

![List emptied](../screenshots/empty_list.png)

---

## Saving Your List

Click the blue Save List button. A file chooser dialog will open:

![Save dialog](../screenshots/save_dialog.png)

Pick a location and a filename (it defaults to games_list.txt) and click Save. The saved file has one game name per line sorted A to Z:

![File contents](../screenshots/save_confirmation.png)
