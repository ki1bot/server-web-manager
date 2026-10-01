package com.rifqi.servermanager.ui.dialog;

import com.rifqi.servermanager.model.WebConfiguration;

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
import java.util.UUID;

public class ConfigurationDialog extends JDialog {
    private final JTextField nameField =
            new JTextField(24);

    private final JTextField urlField =
            new JTextField(24);

    private final JTextField locationField =
            new JTextField(24);

    private final JTextField authorityField =
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

    private final WebConfiguration existing;
    private WebConfiguration result;

    private ConfigurationDialog(
            Window owner,
            WebConfiguration existing
    ) {
        super(
                owner,
                existing == null
                        ? "Tambah Konfigurasi"
                        : "Edit Konfigurasi",
                ModalityType.APPLICATION_MODAL
        );

        this.existing = existing;

        buildUi();
        loadExisting();

        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    public static WebConfiguration showDialog(
            Component parent,
            WebConfiguration existing
    ) {
        Window owner =
                SwingUtilities.getWindowAncestor(
                        parent
                );

        ConfigurationDialog dialog =
                new ConfigurationDialog(
                        owner,
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
                "Nama konfigurasi",
                nameField
        );

        addRow(
                form,
                1,
                "URL",
                urlField
        );

        addRow(
                form,
                2,
                "Lokasi fisik",
                locationField
        );

        addRow(
                form,
                3,
                "Otoritas",
                authorityField
        );

        addRow(
                form,
                4,
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
            statusCombo.setSelectedItem(
                    "Unknown"
            );

            return;
        }

        nameField.setText(
                existing.getName()
        );

        urlField.setText(
                existing.getUrl()
        );

        locationField.setText(
                existing.getPhysicalLocation()
        );

        authorityField.setText(
                existing.getAuthority()
        );

        statusCombo.setSelectedItem(
                existing.getStatus()
        );
    }

    private void save() {
        String name =
                nameField.getText().trim();

        String url =
                urlField.getText().trim();

        String location =
                locationField.getText().trim();

        String authority =
                authorityField.getText().trim();

        String status =
                String.valueOf(
                        statusCombo.getSelectedItem()
                );

        if (
                name.isBlank()
                        || url.isBlank()
                        || location.isBlank()
                        || authority.isBlank()
        ) {
            JOptionPane.showMessageDialog(
                    this,
                    "Semua field wajib diisi.",
                    "Validasi",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (
                !url.startsWith("http://")
                        && !url.startsWith("https://")
        ) {
            JOptionPane.showMessageDialog(
                    this,
                    "URL harus diawali http:// atau https://.",
                    "Validasi",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        UUID id =
                existing == null
                        ? UUID.randomUUID()
                        : existing.getId();

        long requests =
                existing == null
                        ? 0
                        : existing.getRequestCount();

        long errors =
                existing == null
                        ? 0
                        : existing.getErrorCount();

        double responseTime =
                existing == null
                        ? 0.0
                        : existing.getLastResponseSeconds();

        result =
                new WebConfiguration(
                        id,
                        name,
                        url,
                        location,
                        authority,
                        status,
                        requests,
                        errors,
                        responseTime
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