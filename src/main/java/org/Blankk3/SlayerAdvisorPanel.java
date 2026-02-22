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
    // Data (temporary; JSON later)
    private final List<String> allTasks = List.of(
            "Abyssal demons",
            "Bloodvelds",
            "Dagannoths",
            "Dust devils",
            "Gargoyles",
            "Greater demons",
            "Hellhounds",
            "Nechryael",
            "Kurasks",
            "Wyrms"
    );

    // --------- LIST VIEW (default) ---------
    private final JTextField searchField = new JTextField();
    private final DefaultListModel<String> resultsModel = new DefaultListModel<>();
    private final JList<String> resultsList = new JList<>(resultsModel);

    // --------- TOP-LEVEL NAV (list <-> detail) ---------
    private final CardLayout mainLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(mainLayout);

    // --------- DETAIL VIEW ---------
    private final JButton backButton = new JButton("←");
    private final JLabel headerLabel = new JLabel("", SwingConstants.LEFT);

    private final JPanel tabBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
    private final CardLayout detailCards = new CardLayout();
    private final JPanel detailCardPanel = new JPanel(detailCards);

    public SlayerAdvisorPanel()
    {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Build both pages
        JPanel listView = buildListView();
        JPanel detailView = buildDetailView();

        // Add pages to main card layout
        mainPanel.add(listView, "LIST");
        mainPanel.add(detailView, "DETAIL");

        add(mainPanel, BorderLayout.CENTER);

        // Default page
        mainLayout.show(mainPanel, "LIST");

        // Initial list population
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

            String selected = resultsList.getSelectedValue();
            if (selected == null)
            {
                return;
            }

            showDetailFor(selected);
        });

        return listView;
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

        // Detail cards
        detailCardPanel.add(createSection(
                "Basic Information\n\n" +
                        "Required items: (coming soon)\n" +
                        "Locations: (coming soon)\n" +
                        "Slayer masters: (coming soon)\n"
        ), "BASIC");

        detailCardPanel.add(createSection(
                "Recommended items to bring\n\n" +
                        "Suggested utility: (coming soon)\n" +
                        "Suggested supplies: (coming soon)\n" +
                        "Suggested equipment extras: (coming soon)\n"
        ), "BRING");

        detailCardPanel.add(createSection(
                "Combat\n\n" +
                        "Monster attack styles: (coming soon)\n" +
                        "Attributes / types: (coming soon)\n" +
                        "Weaknesses: (coming soon)\n"
        ), "COMBAT");

        detailCardPanel.add(createSection(
                "Drops\n\n" +
                        "Drop table: (coming soon)\n" +
                        "Prominent drops: (coming soon)\n"
        ), "DROPS");

        detailCardPanel.add(createSection(
                "Gear\n\n" +
                        "Recommended gear sets: (coming soon)\n"
        ), "GEAR");

        detailCardPanel.add(createSection(
                "Wiki\n\n" +
                        "Wiki link: (coming soon)\n" +
                        "Variants: (coming soon)\n"
        ), "WIKI");

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

    private void showDetailFor(String monsterName)
    {
        headerLabel.setText(monsterName);

        // Default to Locations each time
        detailCards.show(detailCardPanel, "BASIC");

        // Switch to detail page
        mainLayout.show(mainPanel, "DETAIL");
    }

    private void updateResults(String query)
    {
        resultsModel.clear();

        List<String> matches = filterTasks(query);
        for (String m : matches)
        {
            resultsModel.addElement(m);
        }
    }

    private List<String> filterTasks(String query)
    {
        String q = (query == null) ? "" : query.trim().toLowerCase();
        if (q.isEmpty())
        {
            return new ArrayList<>(allTasks);
        }

        return allTasks.stream()
                .filter(t -> t.toLowerCase().contains(q))
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