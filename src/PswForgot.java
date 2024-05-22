import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;

public class PswForgot extends MioFrame implements ActionListener {
    JTextField t1, t2;
    JButton b1;
    JLabel l1, l2, l3;

    public PswForgot() {
        l3 = new JLabel("Reset Password");
        l3.setFont(new Font("Gotham", Font.BOLD, 30));
        l3.setForeground(Color.WHITE);
        l3.setBounds(50, 10, 300, 40);

        l1 = new JLabel("Email:");
        l1.setBounds(60, 60, 80, 30);

        l2 = new JLabel("Password:");
        l2.setBounds(40, 100, 80, 30);

        t1 = new JTextField(60);
        t1.setBounds(100, 60, 100, 30);

        t2 = new JPasswordField(60);
        t2.setBounds(100, 100, 100, 30);

        b1 = new JButton("Reset");
        b1.setFont(new Font("Gotham", Font.BOLD, 14));
        b1.setForeground(Color.black);
        b1.setBackground(Color.white);
        b1.setBounds(90, 150, 120, 30);
        b1.addActionListener(this);

        try {
            BufferedImage img = ImageIO.read(getClass().getResource("logo.png")); // Change this to your image file path
            Image dimg = img.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            ImageIcon imageIcon = new ImageIcon(dimg);
            JLabel imageLabel = new JLabel(imageIcon);
            imageLabel.setBounds(230, 75, 40, 40); // Adjust position and size as needed
            add(imageLabel);
        } catch (IOException e) {
            e.printStackTrace();
        }

        add(l3);
        add(l1);
        add(l2);
        add(t1);
        add(t2);
        add(b1);



        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 250);
        setLayout(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String email = t1.getText().trim();
        String newPassword = t2.getText().trim();

        if (t1.getText().contains("@gmail.com")) {
            if (email.isEmpty() || newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Stringing Community dice:\n       Non hai scritto nulla!", "Stringing Community", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } else {
            JOptionPane.showMessageDialog(null, "Non e' presente alcun indirizzo mail!!", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            File inputFile = new File("login.txt");
            File tempFile = new File("temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            boolean found = false;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(email + " ")) {
                    String[] parts = line.split(" ");
                    if (parts.length >= 2 && parts[1].equals(newPassword)) {
                        JOptionPane.showMessageDialog(null, "La nuova password non può essere uguale alla vecchia", "Error", JOptionPane.ERROR_MESSAGE);
                        writer.write(line); // Keep the line unchanged
                    } else {
                        writer.write(email + " " + newPassword + " 0\n"); // Add "0" to indicate not logged in
                    }
                    writer.newLine();
                    found = true;
                } else {
                    writer.write(line);
                    writer.newLine();
                }
            }

            reader.close();
            writer.close();

            if (!found) {
                JOptionPane.showMessageDialog(null, "Mail non trovata", "Error", JOptionPane.ERROR_MESSAGE);
                tempFile.delete();
            } else {
                inputFile.delete();
                tempFile.renameTo(inputFile);
                JOptionPane.showMessageDialog(null, "Password aggiornata", "Success", JOptionPane.INFORMATION_MESSAGE);
                this.setVisible(false);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}// per pushare ciao Imraj
