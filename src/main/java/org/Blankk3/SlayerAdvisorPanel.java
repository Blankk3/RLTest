package org.Blankk3;

import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SlayerAdvisorPanel extends PluginPanel
{
    // Data loaded from resources/tasks.json
    private final List<TaskInfo> allTasks;

    // --------- LIST VIEW (default) ---------
    private final JTextField searchField = new JTextField();
    private final DefaultListModel<TaskInfo> resultsModel = new DefaultListModel<>();
    private final JList<TaskInfo> resultsList = new JList<>(resultsModel);

    // --------- TOP-LEVEL NAV (list <-> detail) ---------
    private final CardLayout mainLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(mainLayout);

    // --------- DETAIL VIEW ---------
    private final JButton backButton = new JButton("←");
    private final JLabel headerLabel = new JLabel("", SwingConstants.LEFT);

    private final JPanel tabBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
    private final CardLayout detailCards = new CardLayout();
    private final JPanel detailCardPanel = new JPanel(detailCards);

    private TaskInfo selectedTask;

    // Text areas for each tab
    private final JTextArea basicArea = new JTextArea();
    private final JTextArea bringArea = new JTextArea();
    private final JTextArea dropsArea = new JTextArea();
    private final JTextArea wikiArea = new JTextArea();

    public SlayerAdvisorPanel()
    {
        allTasks = new TaskRepository().loadTasks();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel listView = buildListView();
        JPanel detailView = buildDetailView();

        mainPanel.add(listView, "LIST");
        mainPanel.add(detailView, "DETAIL");

        add(mainPanel, BorderLayout.CENTER);

        mainLayout.show(mainPanel, "LIST");

        updateResults("");
    }

    private JPanel buildListView()
    {
        JPanel listView = new JPanel(new BorderLayout(0, 8));

        // Search
        searchField.setToolTipText("Search Slayer task…");
        listView.add(searchField, BorderLayout.NORTH);

        // Results list
        resultsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsList.setFixedCellHeight(26);
        resultsList.setFont(resultsList.getFont().deriveFont(13f));
        resultsList.setCellRenderer((list, value, index, isSelected, cellHasFocus) ->
        {
            JLabel label = new JLabel(value == null ? "" : value.getName());
            label.setOpaque(true);

            if (isSelected)
            {
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
            }
            else
            {
                label.setBackground(list.getBackground());
                label.setForeground(list.getForeground());
            }
            return label;
        });
        JScrollPane resultsScroll = new JScrollPane(resultsList);
        listView.add(resultsScroll, BorderLayout.CENTER);

        // Live filtering
        searchField.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override public void insertUpdate(DocumentEvent e) { onSearchChanged(); }
            @Override public void removeUpdate(DocumentEvent e) { onSearchChanged(); }
            @Override public void changedUpdate(DocumentEvent e) { onSearchChanged(); }

            private void onSearchChanged()
            {
                updateResults(searchField.getText());
            }
        });

        // Select monster -> go to detail view
        resultsList.addListSelectionListener(e ->
        {
            if (e.getValueIsAdjusting())
            {
                return;
            }

            TaskInfo selected = resultsList.getSelectedValue();
            if (selected == null)
            {
                return;
            }

            showDetailFor(selected);
        });

        return listView;
    }

    private JComponent createTextSection(JTextArea area)
    {
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return new JScrollPane(area);
    }

    private JPanel buildDetailView()
    {
        JPanel detailView = new JPanel(new BorderLayout(0, 8));

        // Header row: back button + monster name
        JPanel headerRow = new JPanel(new BorderLayout(8, 0));
        backButton.setFocusable(false);
        backButton.setToolTipText("Back to list");
        headerRow.add(backButton, BorderLayout.WEST);

        headerLabel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        headerRow.add(headerLabel, BorderLayout.CENTER);

        detailView.add(headerRow, BorderLayout.NORTH);

        // Tabs row (icons only)
        tabBar.add(createTab("ℹ", "BASIC", "Basic info"));
        tabBar.add(createTab("🎒", "BRING", "Recommended items to bring"));
        tabBar.add(createTab("⚔", "COMBAT", "Combat"));
        tabBar.add(createTab("💰", "DROPS", "Drops"));
        tabBar.add(createTab("🛡", "GEAR", "Gear"));
        tabBar.add(createTab("📖", "WIKI", "Wiki"));

        detailView.add(tabBar, BorderLayout.CENTER);

        // Dynamic tabs (from JSON)
        detailCardPanel.add(createTextSection(basicArea), "BASIC");
        detailCardPanel.add(createTextSection(bringArea), "BRING");
        detailCardPanel.add(createSection(
                "Combat\n\n" +
                        "Monster attack styles: (coming soon)\n" +
                        "Attributes / types: (coming soon)\n" +
                        "Weaknesses: (coming soon)\n"
        ), "COMBAT");
        detailCardPanel.add(createTextSection(dropsArea), "DROPS");
        detailCardPanel.add(createSection(
                "Gear\n\n" +
                        "Recommended gear sets: (coming soon)\n"
        ), "GEAR");
        detailCardPanel.add(createTextSection(wikiArea), "WIKI");

        detailView.add(detailCardPanel, BorderLayout.SOUTH);

        // Back button behavior
        backButton.addActionListener(e ->
        {
            resultsList.clearSelection(); // optional: avoid re-trigger
            mainLayout.show(mainPanel, "LIST");
            searchField.requestFocusInWindow();
        });

        // Default detail tab
        detailCards.show(detailCardPanel, "BASIC");

        return detailView;
    }

    private void showDetailFor(TaskInfo task)
    {
        headerLabel.setText(task.getName());

        // default tab
        detailCards.show(detailCardPanel, "BASIC");

        // switch to detail page
        mainLayout.show(mainPanel, "DETAIL");
    }

    private void updateResults(String query)
    {
        resultsModel.clear();

        List<TaskInfo> matches = filterTasks(query);
        for (TaskInfo t : matches)
        {
            resultsModel.addElement(t);
        }
    }

    private List<TaskInfo> filterTasks(String query)
    {
        String q = (query == null) ? "" : query.trim().toLowerCase();

        if (q.isEmpty())
        {
            return new ArrayList<>(allTasks);
        }

        return allTasks.stream()
                .filter(t -> t.getName() != null && t.getName().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    private JComponent createSection(String content)
    {
        JTextArea area = new JTextArea(content);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return new JScrollPane(area);
    }

    private JButton createTab(String label, String cardName, String tooltip)
    {
        JButton btn = new JButton(label);
        btn.setFocusable(false);
        btn.setToolTipText(tooltip);
        btn.addActionListener(e -> detailCards.show(detailCardPanel, cardName));
        return btn;
    }
}