package com.rifqi.servermanager.ui.panel;

import com.rifqi.servermanager.model.ServerNode;
import com.rifqi.servermanager.model.WebConfiguration;
import com.rifqi.servermanager.service.ServerManagementService;
import com.rifqi.servermanager.ui.dialog.NodeDialog;

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

public class NodesPanel extends JPanel {
    private final ServerManagementService service;
    private final Runnable onChanged;

    private final DefaultTableModel model;
    private final JTable table;

    private List<ServerNode> rows =
            List.of();

    public NodesPanel(
            ServerManagementService service,
            Runnable onChanged
    ) {
        this.service = service;
        this.onChanged = onChanged;

        this.model =
                new DefaultTableModel(
                        new Object[]{
                                "Node",
                                "Configuration",
                                "Host",
                                "Port",
                                "Physical Location",
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
                service.getNodes();

        model.setRowCount(0);

        for (
                ServerNode node :
                rows
        ) {
            model.addRow(
                    new Object[]{
                            node.getName(),
                            node.getConfigurationName(),
                            node.getHost(),
                            node.getPort(),
                            node.getLocation(),
                            node.getStatus()
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
                        "Nodes"
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
                        "Kelola instance atau node yang menjadi bagian dari setiap konfigurasi server web."
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
                        addNode()
        );

        editButton.addActionListener(
                event ->
                        editNode()
        );

        deleteButton.addActionListener(
                event ->
                        deleteNode()
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

    private void addNode() {
        List<String> configurationNames =
                getConfigurationNames();

        if (configurationNames.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Buat konfigurasi terlebih dahulu sebelum menambah node.",
                    "Informasi",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        ServerNode result =
                NodeDialog.showDialog(
                        this,
                        configurationNames,
                        null
                );

        if (result == null) {
            return;
        }

        service.saveNode(
                result
        );

        onChanged.run();
    }

    private void editNode() {
        ServerNode selected =
                getSelectedNode();

        if (selected == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Pilih node yang ingin diedit.",
                    "Informasi",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        ServerNode result =
                NodeDialog.showDialog(
                        this,
                        getConfigurationNames(),
                        selected
                );

        if (result == null) {
            return;
        }

        service.saveNode(
                result
        );

        onChanged.run();
    }

    private void deleteNode() {
        ServerNode selected =
                getSelectedNode();

        if (selected == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Pilih node yang ingin dihapus.",
                    "Informasi",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        int answer =
                JOptionPane.showConfirmDialog(
                        this,
                        "Hapus node '"
                                + selected.getName()
                                + "'?",
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

        service.deleteNode(
                selected.getId()
        );

        onChanged.run();
    }

    private ServerNode getSelectedNode() {
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

    private List<String> getConfigurationNames() {
        return service
                .getConfigurations()
                .stream()
                .map(
                        WebConfiguration::getName
                )
                .toList();
    }
}