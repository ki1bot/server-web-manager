package com.rifqi.servermanager.ui.dialog;

import com.rifqi.servermanager.model.ServerCertificate;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.UUID;

public class CertificateDialog extends JDialog {
    private final JTextField aliasField =
            new JTextField(24);

    private final JTextField ownerField =
            new JTextField(24);

    private final JTextField issuerField =
            new JTextField(24);

    private final JTextField validUntilField =
            new JTextField(24);

    private final ServerCertificate existing;

    private ServerCertificate result;

    private CertificateDialog(
            Window owner,
            ServerCertificate existing
    ) {
        super(
                owner,
                existing == null
                        ? "Tambah Sertifikat"
                        : "Edit Sertifikat",
                ModalityType.APPLICATION_MODAL
        );

        this.existing = existing;

        buildUi();
        loadExisting();

        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    public static ServerCertificate showDialog(
            Component parent,
            ServerCertificate existing
    ) {
        Window owner =
                SwingUtilities.getWindowAncestor(
                        parent
                );

        CertificateDialog dialog =
                new CertificateDialog(
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
                "Alias",
                aliasField
        );

        addRow(
                form,
                1,
                "Owner",
                ownerField
        );

        addRow(
                form,
                2,
                "Issuer",
                issuerField
        );

        addRow(
                form,
                3,
                "Berlaku sampai",
                validUntilField
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
            validUntilField.setText(
                    LocalDate.now()
                            .plusYears(1)
                            .toString()
            );

            return;
        }

        aliasField.setText(
                existing.getAlias()
        );

        ownerField.setText(
                existing.getOwner()
        );

        issuerField.setText(
                existing.getIssuer()
        );

        validUntilField.setText(
                existing.getValidUntil()
                        .toString()
        );
    }

    private void save() {
        String alias =
                aliasField.getText().trim();

        String owner =
                ownerField.getText().trim();

        String issuer =
                issuerField.getText().trim();

        if (
                alias.isBlank()
                        || owner.isBlank()
                        || issuer.isBlank()
                        || validUntilField
                        .getText()
                        .trim()
                        .isBlank()
        ) {
            JOptionPane.showMessageDialog(
                    this,
                    "Semua field wajib diisi.",
                    "Validasi",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        LocalDate validUntil;

        try {
            validUntil =
                    LocalDate.parse(
                            validUntilField
                                    .getText()
                                    .trim()
                    );

        } catch (DateTimeParseException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "Tanggal harus menggunakan format YYYY-MM-DD.",
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
                new ServerCertificate(
                        id,
                        alias,
                        owner,
                        issuer,
                        validUntil
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