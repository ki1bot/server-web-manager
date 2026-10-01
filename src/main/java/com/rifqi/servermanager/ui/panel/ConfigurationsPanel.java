package com.rifqi.servermanager.ui.panel;

import com.rifqi.servermanager.model.WebConfiguration;
import com.rifqi.servermanager.service.ServerManagementService;
import com.rifqi.servermanager.ui.dialog.ConfigurationDialog;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

public class ConfigurationsPanel extends JPanel {
    private final ServerManagementService service;
    private final Runnable onChanged;

    private final DefaultTableModel model;
    private final JTable table;

    private List<WebConfiguration> rows =
            List.of();

    public ConfigurationsPanel(
            ServerManagementService service,
            Runnable onChanged
    ) {
        this.service = service;
        this.onChanged = onChanged;

        this.model =
                new DefaultTableModel(
                        new Object[]{
                                "Configuration",
                                "URL",
                                "Physical Location",
                                "Authority",
                                "Status"
                        },
                        0
                ) {
                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                };

        this.table =
                new JTable(model);

        buildUi();
        refreshData();
    }

    public void refreshData() {
        rows =
                service.getConfigurations();

        model.setRowCount(0);

        for (
                WebConfiguration configuration :
                rows
        ) {
            model.addRow(
                    new Object[]{
                            configuration.getName(),
                            configuration.getUrl(),
                            configuration.getPhysicalLocation(),
                            configuration.getAuthority(),
                            configuration.getStatus()
                    }
            );
        }
    }

    private void buildUi() {
        setLayout(
                new BorderLayout(
                        10,
                        10
                )
        );

        setBorder(
                BorderFactory.createEmptyBorder(
                        14,
                        14,
                        14,
                        14
                )
        );

        JLabel title =
                new JLabel(
                        "Configurations"
                );

        title.setFont(
                title.getFont()
                        .deriveFont(
                                Font.BOLD,
                                20f
                        )
        );

        JLabel description =
                new JLabel(
                        "Kelola nama konfigurasi, lokasi fisik, otoritas, URL, dan status server web."
                );

        JPanel heading =
                new JPanel(
                        new BorderLayout(
                                0,
                                4
                        )
                );

        heading.add(
                title,
                BorderLayout.NORTH
        );

        heading.add(
                description,
                BorderLayout.CENTER
        );

        JButton addButton =
                new JButton("Tambah");

        JButton editButton =
                new JButton("Edit");

        JButton deleteButton =
                new JButton("Hapus");

        JButton refreshButton =
                new JButton("Refresh");

        addButton.addActionListener(
                event ->
                        addConfiguration()
        );

        editButton.addActionListener(
                event ->
                        editConfiguration()
        );

        deleteButton.addActionListener(
                event ->
                        deleteConfiguration()
        );

        refreshButton.addActionListener(
                event ->
                        refreshData()
        );

        JPanel actions =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );

        actions.add(addButton);
        actions.add(editButton);
        actions.add(deleteButton);
        actions.add(refreshButton);

        JPanel top =
                new JPanel(
                        new BorderLayout()
                );

        top.add(
                heading,
                BorderLayout.CENTER
        );

        top.add(
                actions,
                BorderLayout.SOUTH
        );

        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        table.setAutoCreateRowSorter(
                true
        );

        table.setRowHeight(
                26
        );

        table.getTableHeader()
                .setReorderingAllowed(
                        false
                );

        add(
                top,
                BorderLayout.NORTH
        );

        add(
                new JScrollPane(table),
                BorderLayout.CENTER
        );
    }

    private void addConfiguration() {
        WebConfiguration result =
                ConfigurationDialog.showDialog(
                        this,
                        null
                );

        if (result == null) {
            return;
        }

        service.saveConfiguration(
                result
        );

        onChanged.run();
    }

    private void editConfiguration() {
        WebConfiguration selected =
                getSelectedConfiguration();

        if (selected == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Pilih konfigurasi yang ingin diedit.",
                    "Informasi",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        WebConfiguration result =
                ConfigurationDialog.showDialog(
                        this,
                        selected
                );

        if (result == null) {
            return;
        }

        service.saveConfiguration(
                result
        );

        onChanged.run();
    }

    private void deleteConfiguration() {
        WebConfiguration selected =
                getSelectedConfiguration();

        if (selected == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Pilih konfigurasi yang ingin dihapus.",
                    "Informasi",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        int answer =
                JOptionPane.showConfirmDialog(
                        this,
                        "Hapus konfigurasi '"
                                + selected.getName()
                                + "' beserta node yang terhubung?",
                        "Konfirmasi Hapus",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (
                answer
                        != JOptionPane.YES_OPTION
        ) {
            return;
        }

        service.deleteConfiguration(
                selected.getId()
        );

        onChanged.run();
    }

    private WebConfiguration getSelectedConfiguration() {
        int selectedRow =
                table.getSelectedRow();

        if (selectedRow < 0) {
            return null;
        }

        int modelRow =
                table.convertRowIndexToModel(
                        selectedRow
                );

        return rows.get(
                modelRow
        );
    }
}