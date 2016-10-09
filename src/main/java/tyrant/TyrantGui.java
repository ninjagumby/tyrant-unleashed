package tyrant;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class TyrantGui extends JFrame {
    private static final long serialVersionUID = 1L;
    private final TyrantClient client_;
    private final TyrantProcessor processor_;
    private JTextField txtUserId;
    private JTextField txtPasswordHash;
    private Map<String, String> cards_;

    public TyrantGui(TyrantClient client) {
        SpringLayout springLayout = new SpringLayout();
        getContentPane().setLayout(springLayout);

        JButton btnNewButton = new JButton("Process Card Ownerships");
        springLayout.putConstraint(SpringLayout.WEST, btnNewButton, 139,
                SpringLayout.WEST, getContentPane());
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                List<UserCard> userCards = processor_
                        .processUserAccount(client_.getUserAccount(), cards_);

                writeFile(userCards);
            }
        });
        getContentPane().add(btnNewButton);

        JLabel lblUserId = new JLabel("User ID:");
        springLayout.putConstraint(SpringLayout.NORTH, lblUserId, 15,
                SpringLayout.NORTH, getContentPane());
        springLayout.putConstraint(SpringLayout.WEST, lblUserId, 83,
                SpringLayout.WEST, getContentPane());
        springLayout.putConstraint(SpringLayout.EAST, lblUserId, 133,
                SpringLayout.WEST, getContentPane());
        getContentPane().add(lblUserId);

        JLabel lblPasswordHash = new JLabel("Password Hash:");
        springLayout.putConstraint(SpringLayout.NORTH, lblPasswordHash, 16,
                SpringLayout.SOUTH, lblUserId);
        getContentPane().add(lblPasswordHash);

        txtUserId = new JTextField();
        springLayout.putConstraint(SpringLayout.NORTH, txtUserId, 10,
                SpringLayout.NORTH, getContentPane());
        springLayout.putConstraint(SpringLayout.WEST, txtUserId, 6,
                SpringLayout.EAST, lblUserId);
        springLayout.putConstraint(SpringLayout.EAST, txtUserId, -443,
                SpringLayout.EAST, getContentPane());
        getContentPane().add(txtUserId);
        txtUserId.setColumns(10);

        txtPasswordHash = new JTextField();
        springLayout.putConstraint(SpringLayout.NORTH, txtPasswordHash, 6, SpringLayout.SOUTH, txtUserId);
        springLayout.putConstraint(SpringLayout.EAST, lblPasswordHash, -6,
                SpringLayout.WEST, txtPasswordHash);
        springLayout.putConstraint(SpringLayout.NORTH, btnNewButton, 6,
                SpringLayout.SOUTH, txtPasswordHash);
        springLayout.putConstraint(SpringLayout.WEST, txtPasswordHash, 139,
                SpringLayout.WEST, getContentPane());
        springLayout.putConstraint(SpringLayout.EAST, txtPasswordHash, -379,
                SpringLayout.EAST, getContentPane());
        getContentPane().add(txtPasswordHash);
        txtPasswordHash.setColumns(10);

        client_ = client;
        processor_ = new TyrantProcessor();
        txtUserId.setText(client_.getUserId());
        txtPasswordHash.setText(client_.getPasswordHash());

        JLabel lblCardTotal = new JLabel("");
        springLayout.putConstraint(SpringLayout.WEST, lblCardTotal, 145, SpringLayout.EAST, txtUserId);
        springLayout.putConstraint(SpringLayout.SOUTH, lblCardTotal, 0,
                SpringLayout.SOUTH, lblUserId);
        springLayout.putConstraint(SpringLayout.EAST, lblCardTotal, -159, SpringLayout.EAST, getContentPane());
        getContentPane().add(lblCardTotal);

        cards_ = processor_.loadCardsFromInternet();
        lblCardTotal.setText("Loaded " + cards_.size() + " cards.");
        addDocumentListeners();
        this.setSize(700, 200);
    }

    protected void writeFile(List<UserCard> userCards) {
        JFileChooser fileChooser = new JFileChooser(
                System.getProperty("user.dir"));
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Text files", "txt", "text");
        fileChooser.setFileFilter(filter);
        if (fileChooser.showSaveDialog(
                TyrantGui.this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(file));
                for (UserCard card : userCards) {
                    writer.write(card.toString() + "\r\n");
                }
            } catch (IOException e) {
                System.err.println(e);
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        System.err.println(e);
                    }
                }
            }
        }
    }

    private void addDocumentListeners() {
        txtUserId.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                client_.setUserId(txtUserId.getText());
            }

            public void removeUpdate(DocumentEvent e) {
                client_.setUserId(txtUserId.getText());
            }

            public void insertUpdate(DocumentEvent e) {
                client_.setUserId(txtUserId.getText());
            }
        });
        txtPasswordHash.getDocument()
                .addDocumentListener(new DocumentListener() {
                    public void changedUpdate(DocumentEvent e) {
                        client_.setPasswordHash(txtPasswordHash.getText());
                    }

                    public void removeUpdate(DocumentEvent e) {
                        client_.setPasswordHash(txtPasswordHash.getText());
                    }

                    public void insertUpdate(DocumentEvent e) {
                        client_.setPasswordHash(txtPasswordHash.getText());
                    }
                });
    }
}
