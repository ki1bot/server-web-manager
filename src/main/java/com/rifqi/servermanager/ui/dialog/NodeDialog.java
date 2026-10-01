package com.rifqi.servermanager.ui.dialog;

import com.rifqi.servermanager.model.ServerNode;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.util.List;
import java.util.UUID;

public class NodeDialog extends JDialog {
    private final JTextField nameField =
            new JTextField(24);

    private final JComboBox<String> configurationCombo;

    private final JTextField hostField =
            new JTextField(24);

    private final JTextField portField =
            new JTextField(24);

    private final JTextField locationField =
            new JTextField(24);

    private final JComboBox<String> statusCombo =
            new JComboBox<>(
                    new String[]{
                            "Unknown",
                            "Online",
                            "Offline",
                            "Error",
                            "Maintenance"
                    }
            );

    private final ServerNode existing;
    private ServerNode result;

    private NodeDialog(
            Window owner,
            List<String> configurationNames,
            ServerNode existing
    ) {
        super(
                owner,
                existing == null
                        ? "Tambah Node"
                        : "Edit Node",
                ModalityType.APPLICATION_MODAL
        );

        this.existing = existing;

        this.configurationCombo =
                new JComboBox<>(
                        configurationNames.toArray(
                                String[]::new
                        )
                );

        buildUi();
        loadExisting();

        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    public static ServerNode showDialog(
            Component parent,
            List<String> configurationNames,
            ServerNode existing
    ) {
        Window owner =
                SwingUtilities.getWindowAncestor(
                        parent
                );

        NodeDialog dialog =
                new NodeDialog(
                        owner,
                        configurationNames,
                        existing
                );

        dialog.setVisible(true);

        return dialog.result;
    }

    private void buildUi() {
        JPanel form =
                new JPanel(
                        new GridBagLayout()
                );

        form.setBorder(
                BorderFactory.createEmptyBorder(
                        14,
                        14,
                        8,
                        14
                )
        );

        addRow(
                form,
                0,
                "Nama node",
                nameField
        );

        addRow(
                form,
                1,
                "Konfigurasi",
                configurationCombo
        );

        addRow(
                form,
                2,
                "Host / IP",
                hostField
        );

        addRow(
                form,
                3,
                "Port",
                portField
        );

        addRow(
                form,
                4,
                "Lokasi fisik",
                locationField
        );

        addRow(
                form,
                5,
                "Status",
                statusCombo
        );

        JButton saveButton =
                new JButton("Simpan");

        JButton cancelButton =
                new JButton("Batal");

        saveButton.addActionListener(
                event -> save()
        );

        cancelButton.addActionListener(
                event -> dispose()
        );

        JPanel buttons =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );

        buttons.add(cancelButton);
        buttons.add(saveButton);

        setLayout(
                new BorderLayout()
        );

        add(
                form,
                BorderLayout.CENTER
        );

        add(
                buttons,
                BorderLayout.SOUTH
        );

        getRootPane()
                .setDefaultButton(
                        saveButton
                );
    }

    private void loadExisting() {
        if (existing == null) {
            hostField.setText(
                    "127.0.0.1"
            );

            portField.setText(
                    "8080"
            );

            statusCombo.setSelectedItem(
                    "Unknown"
            );

            return;
        }

        nameField.setText(
                existing.getName()
        );

        configurationCombo.setSelectedItem(
                existing.getConfigurationName()
        );

        hostField.setText(
                existing.getHost()
        );

        portField.setText(
                String.valueOf(
                        existing.getPort()
                )
        );

        locationField.setText(
                existing.getLocation()
        );

        statusCombo.setSelectedItem(
                existing.getStatus()
        );
    }

    private void save() {
        String name =
                nameField.getText().trim();

        String configuration =
                String.valueOf(
                        configurationCombo.getSelectedItem()
                );

        String host =
                hostField.getText().trim();

        String location =
                locationField.getText().trim();

        String status =
                String.valueOf(
                        statusCombo.getSelectedItem()
                );

        if (
                name.isBlank()
                        || configuration.isBlank()
                        || host.isBlank()
                        || location.isBlank()
        ) {
            JOptionPane.showMessageDialog(
                    this,
                    "Semua field wajib diisi.",
                    "Validasi",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int port;

        try {
            port =
                    Integer.parseInt(
                            portField
                                    .getText()
                                    .trim()
                    );

        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "Port harus berupa angka.",
                    "Validasi",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (
                port < 1
                        || port > 65535
        ) {
            JOptionPane.showMessageDialog(
                    this,
                    "Port harus berada pada rentang 1 sampai 65535.",
                    "Validasi",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        UUID id =
                existing == null
                        ? UUID.randomUUID()
                        : existing.getId();

        result =
                new ServerNode(
                        id,
                        name,
                        configuration,
                        host,
                        port,
                        location,
                        status
                );

        dispose();
    }

    private void addRow(
            JPanel panel,
            int row,
            String label,
            Component component
    ) {
        GridBagConstraints left =
                new GridBagConstraints();

        left.gridx = 0;
        left.gridy = row;
        left.anchor =
                GridBagConstraints.WEST;

        left.insets =
                new Insets(
                        6,
                        0,
                        6,
                        12
                );

        GridBagConstraints right =
                new GridBagConstraints();

        right.gridx = 1;
        right.gridy = row;
        right.weightx = 1.0;
        right.fill =
                GridBagConstraints.HORIZONTAL;

        right.insets =
                new Insets(
                        6,
                        0,
                        6,
                        0
                );

        panel.add(
                new JLabel(label),
                left
        );

        panel.add(
                component,
                right
        );
    }
}