package student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

/**
 * Swing-based GUI view for the Board Game Arena Planner.
 *
 * <p>This class is the View in the MVC pattern. It displays data
 * to the user and delegates all actions to the GUIController.
 * It never directly touches the model layer.
 *
 * @author CS5004 Student
 * @version 1.0
 */
public class GUIView extends JFrame {

    // colors
    /** Purple used for the filter bar background. */
    private static final Color PURPLE      = new Color(98, 71, 170);
    /** Lighter purple for hover accents. */
    private static final Color PURPLE_LIGHT = new Color(149, 117, 205);
    /** Green used for add buttons. */
    private static final Color GREEN       = new Color(56, 161, 105);
    /** Red used for remove and clear buttons. */
    private static final Color RED         = new Color(220, 53, 69);
    /** Blue used for the save button. */
    private static final Color BLUE        = new Color(49, 130, 206);
    /** Cream background for panels. */
    private static final Color CREAM       = new Color(255, 253, 245);
    /** Light purple for the table header. */
    private static final Color HEADER_BG   = new Color(179, 157, 219);

    /** Controller this view delegates all data operations to. */
    private final GUIController controller;

    /** Text field where the user types a filter expression. */
    private JTextField filterField;

    /** Drop-down for choosing which column to sort on. */
    private JComboBox<GameData> sortCombo;

    /** Checkbox toggling ascending vs descending sort. */
    private JCheckBox ascendingCheck;

    /** Label showing "Showing N games" after each filter. */
    private JLabel statusLabel;

    /** The table that shows filtered game results. */
    private JTable resultsTable;

    /** Backing model for the results table. */
    private DefaultTableModel resultsModel;

    /** Backing model for the personal game list. */
    private DefaultListModel<String> listModel;

    /** Visual list widget showing games the user has added. */
    private JList<String> myList;

    /** Label showing how many games are in the personal list. */
    private JLabel listCountLabel;

    /**
     * Creates and displays the main application window.
     *
     * @param controller the controller to delegate data operations to
     */
    public GUIView(GUIController controller) {
        this.controller = controller;
        initUI();
        loadAllGames();
    }

    /**
     * Builds and wires up all UI components, then makes the window visible.
     */
    private void initUI() {
        setTitle("Board Game Arena Planner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1250, 720);
        setMinimumSize(new Dimension(900, 500));
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(CREAM);

        add(buildFilterPanel(), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                buildResultsPanel(),
                buildMyListPanel());
        splitPane.setDividerLocation(820);
        splitPane.setResizeWeight(0.7);
        splitPane.setBackground(CREAM);
        add(splitPane, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Builds the top filter bar with a purple background.
     *
     * @return the assembled filter panel
     */
    private JPanel buildFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(PURPLE);
        panel.setBorder(new EmptyBorder(4, 8, 4, 8));

        JLabel filterLabel = new JLabel("Filter:");
        filterLabel.setFont(filterLabel.getFont().deriveFont(Font.BOLD, 14f));
        filterLabel.setForeground(Color.WHITE);
        panel.add(filterLabel);

        filterField = new JTextField(28);
        filterField.setFont(filterField.getFont().deriveFont(13f));
        filterField.setToolTipText(
                "Examples:  minPlayers>=2   rating>7   name~=catan");
        filterField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    applyFilter();
                }
            }
        });
        panel.add(filterField);

        JLabel sortLabel = new JLabel("Sort by:");
        sortLabel.setForeground(Color.WHITE);
        sortLabel.setFont(sortLabel.getFont().deriveFont(Font.BOLD, 13f));
        panel.add(sortLabel);

        GameData[] sortOptions = {
                GameData.NAME, GameData.RATING, GameData.DIFFICULTY, GameData.RANK,
                GameData.MIN_PLAYERS, GameData.MAX_PLAYERS,
                GameData.MIN_TIME, GameData.MAX_TIME, GameData.YEAR
        };
        sortCombo = new JComboBox<>(sortOptions);
        sortCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof GameData) {
                    setText(friendlyName((GameData) value));
                }
                return this;
            }
        });
        panel.add(sortCombo);

        ascendingCheck = new JCheckBox("Ascending", true);
        ascendingCheck.setForeground(Color.WHITE);
        ascendingCheck.setBackground(PURPLE);
        ascendingCheck.setFont(ascendingCheck.getFont().deriveFont(13f));
        panel.add(ascendingCheck);

        JButton applyBtn = makeButton("Apply Filter", PURPLE_LIGHT);
        applyBtn.addActionListener(e -> applyFilter());
        panel.add(applyBtn);

        JButton resetBtn = makeButton("Reset", new Color(120, 90, 160));
        resetBtn.addActionListener(e -> resetFilter());
        panel.add(resetBtn);

        statusLabel = new JLabel("Showing 0 games");
        statusLabel.setForeground(new Color(220, 210, 255));
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.ITALIC, 12f));
        panel.add(statusLabel);

        return panel;
    }

    /**
     * Builds the left panel with the results table and add buttons.
     *
     * @return the assembled results panel
     */
    private JPanel buildResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setBackground(CREAM);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(PURPLE_LIGHT, 2),
                        "  Search Results  "),
                new EmptyBorder(4, 4, 4, 4)));

        String[] columns = {
                "#", "Name", "Rating", "Difficulty",
                "Min Players", "Max Players",
                "Min Time (min)", "Max Time (min)", "Year", "Rank"
        };
        resultsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        resultsTable = new JTable(resultsModel);
        resultsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        resultsTable.setRowHeight(24);
        resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        resultsTable.getTableHeader().setReorderingAllowed(false);
        resultsTable.setFillsViewportHeight(true);
        resultsTable.setBackground(Color.WHITE);
        resultsTable.setGridColor(new Color(220, 210, 255));
        resultsTable.setSelectionBackground(new Color(179, 157, 219));
        resultsTable.setSelectionForeground(Color.WHITE);
        resultsTable.setFont(resultsTable.getFont().deriveFont(13f));

        // color the table header
        JTableHeader header = resultsTable.getTableHeader();
        header.setBackground(HEADER_BG);
        header.setForeground(new Color(50, 30, 90));
        header.setFont(header.getFont().deriveFont(Font.BOLD, 13f));

        // alternate row colors
        resultsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 240, 255));
                }
                return c;
            }
        });

        int[] widths = {35, 220, 65, 80, 90, 95, 110, 110, 55, 55};
        for (int i = 0; i < widths.length; i++) {
            resultsTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        panel.add(new JScrollPane(resultsTable), BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        btnRow.setBackground(CREAM);

        JButton addSelBtn = makeButton("Add Selected to My List", GREEN);
        addSelBtn.addActionListener(e -> addSelectedGames());

        JButton addAllBtn = makeButton("Add All to My List", GREEN);
        addAllBtn.addActionListener(e -> addAllGames());

        btnRow.add(addSelBtn);
        btnRow.add(addAllBtn);
        panel.add(btnRow, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Builds the right panel with the personal game list and action buttons.
     *
     * @return the assembled my-list panel
     */
    private JPanel buildMyListPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setBackground(CREAM);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(PURPLE_LIGHT, 2),
                        "  My Game List  "),
                new EmptyBorder(4, 4, 4, 4)));
        panel.setMinimumSize(new Dimension(200, 0));

        listModel = new DefaultListModel<>();
        myList = new JList<>(listModel);
        myList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        myList.setFont(myList.getFont().deriveFont(13f));
        myList.setBackground(Color.WHITE);
        myList.setSelectionBackground(new Color(179, 157, 219));
        myList.setSelectionForeground(Color.WHITE);

        JScrollPane listScroll = new JScrollPane(myList);
        listScroll.getViewport().setBackground(Color.WHITE);
        panel.add(listScroll, BorderLayout.CENTER);

        JPanel btnCol = new JPanel(new GridLayout(4, 1, 5, 5));
        btnCol.setBackground(CREAM);
        btnCol.setBorder(new EmptyBorder(6, 8, 8, 8));

        listCountLabel = new JLabel("0 games in list", SwingConstants.CENTER);
        listCountLabel.setForeground(PURPLE);
        listCountLabel.setFont(listCountLabel.getFont().deriveFont(Font.BOLD, 13f));

        JButton removeBtn = makeButton("Remove Selected", RED);
        removeBtn.addActionListener(e -> removeSelectedFromList());

        JButton clearBtn = makeButton("Clear All", RED);
        clearBtn.addActionListener(e -> clearList());

        JButton saveBtn = makeButton("Save List", BLUE);
        saveBtn.addActionListener(e -> saveList());

        btnCol.add(listCountLabel);
        btnCol.add(removeBtn);
        btnCol.add(clearBtn);
        btnCol.add(saveBtn);
        panel.add(btnCol, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Helper to create a styled button with white text and given background color.
     *
     * @param text  the button label
     * @param color the background color
     * @return the styled button
     */
    private static JButton makeButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(btn.getFont().deriveFont(Font.BOLD, 13f));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1),
                new EmptyBorder(5, 12, 5, 12)));
        btn.setOpaque(true);
        return btn;
    }

    /**
     * Loads all games at startup so the table is populated immediately.
     */
    private void loadAllGames() {
        controller.resetFilter();
        List<BoardGame> all = controller.applyFilter("", GameData.NAME, true);
        refreshResultsTable(all);
    }

    /**
     * Reads the filter field and calls the controller to get filtered results.
     */
    private void applyFilter() {
        String filter = filterField.getText().trim();
        GameData sortOn = (GameData) sortCombo.getSelectedItem();
        boolean ascending = ascendingCheck.isSelected();
        try {
            List<BoardGame> results = controller.applyFilter(filter, sortOn, ascending);
            refreshResultsTable(results);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid filter expression:\n" + ex.getMessage(),
                    "Filter Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Resets the planner filter state and reloads all games.
     */
    private void resetFilter() {
        filterField.setText("");
        sortCombo.setSelectedItem(GameData.NAME);
        ascendingCheck.setSelected(true);
        controller.resetFilter();
        List<BoardGame> all = controller.applyFilter("", GameData.NAME, true);
        refreshResultsTable(all);
    }

    /**
     * Adds the selected rows from the results table to the personal list.
     */
    private void addSelectedGames() {
        int[] selectedRows = resultsTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select at least one game from the results table.",
                    "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int added = 0;
        for (int row : selectedRows) {
            String name = (String) resultsModel.getValueAt(row, 1);
            try {
                controller.addToList(name);
                added++;
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this,
                        "Could not add \"" + name + "\": " + ex.getMessage(),
                        "Add Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        if (added > 0) {
            refreshMyList();
        }
    }

    /**
     * Adds every game in the current filter result to the personal list.
     */
    private void addAllGames() {
        try {
            controller.addToList("all");
            refreshMyList();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error adding games: " + ex.getMessage(),
                    "Add Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Removes the selected games from the personal list.
     */
    private void removeSelectedFromList() {
        List<String> selected = myList.getSelectedValuesList();
        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select at least one game from your list.",
                    "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        for (String name : selected) {
            try {
                controller.removeFromList(name);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this,
                        "Could not remove \"" + name + "\": " + ex.getMessage(),
                        "Remove Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        refreshMyList();
    }

    /**
     * Clears all games from the personal list after confirmation.
     */
    private void clearList() {
        if (controller.getListCount() == 0) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Remove all " + controller.getListCount() + " games from your list?",
                "Clear List", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.clearList();
            refreshMyList();
        }
    }

    /**
     * Opens a file save dialog and writes the personal list to a text file.
     */
    private void saveList() {
        if (controller.getListCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Your list is empty, add some games first.",
                    "Empty List", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save Game List");
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fc.setSelectedFile(new File("games_list.txt"));
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Text files (*.txt)", "txt"));

        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = fc.getSelectedFile().getAbsolutePath();
            if (!path.toLowerCase().endsWith(".txt")) {
                path += ".txt";
            }
            controller.saveList(path);
            JOptionPane.showMessageDialog(this,
                    "List saved to:\n" + path,
                    "Saved", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Populates the results table from the given list of games.
     *
     * @param games the games to display
     */
    private void refreshResultsTable(List<BoardGame> games) {
        resultsModel.setRowCount(0);
        int i = 1;
        for (BoardGame game : games) {
            resultsModel.addRow(new Object[]{
                    i++,
                    game.getName(),
                    String.format("%.2f", game.getRating()),
                    String.format("%.2f", game.getDifficulty()),
                    game.getMinPlayers(),
                    game.getMaxPlayers(),
                    game.getMinPlayTime(),
                    game.getMaxPlayTime(),
                    game.getYearPublished(),
                    game.getRank()
            });
        }
        statusLabel.setText("Showing " + games.size() + " game" + (games.size() == 1 ? "" : "s"));
    }

    /**
     * Repopulates the personal list panel from the controller.
     */
    private void refreshMyList() {
        listModel.clear();
        for (String name : controller.getListNames()) {
            listModel.addElement(name);
        }
        int count = controller.getListCount();
        listCountLabel.setText(count + " game" + (count == 1 ? "" : "s") + " in list");
    }

    /**
     * Returns a human-readable display name for a GameData column.
     *
     * @param gd the enum value
     * @return a friendly string such as "Min Players"
     */
    private static String friendlyName(GameData gd) {
        switch (gd) {
            case NAME:        return "Name";
            case RATING:      return "Rating";
            case DIFFICULTY:  return "Difficulty";
            case RANK:        return "Rank";
            case MIN_PLAYERS: return "Min Players";
            case MAX_PLAYERS: return "Max Players";
            case MIN_TIME:    return "Min Time";
            case MAX_TIME:    return "Max Time";
            case YEAR:        return "Year Published";
            default:          return gd.name();
        }
    }
}
