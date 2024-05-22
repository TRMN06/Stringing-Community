import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SignUp extends MioFrame implements ActionListener,WindowListener{
    JTextField t1, t2;
    JButton b1;
    JLabel l1, l2, l3, l4, l5;

    public SignUp(){
        setLayout(null);

        l3 = new JLabel("Registrati");
        l3.setFont(new Font("Gotham", Font.BOLD, 30));
        l3.setForeground(Color.white);
        l3.setBounds(75, 20, 300, 40);

        l4 = new JLabel("Stringing Community");
        l4.setFont(new Font("Gotham", Font.BOLD, 15));
        l4.setForeground(Color.white);
        l4.setBounds(250, 80, 180, 30);

        l5 = new JLabel("Più film che indiani");
        l5.setFont(new Font("Gotham", Font.BOLD, 15));
        l5.setForeground(Color.white);
        l5.setBounds(280, 100, 180, 30);

        l1 = new JLabel("Email:");
        l1.setBounds(60, 80, 80, 30);

        l2 = new JLabel("Password:");
        l2.setBounds(40, 120, 80, 30);

        t1 = new JTextField(60);
        t1.setBounds(100, 80, 110, 30);

        t2 = new JPasswordField(60);
        t2.setBounds(100, 120, 110, 30);

        b1 = new JButton("Sign Up");
        b1.setFont(new Font("Gotham", Font.BOLD, 14));
        b1.setForeground(Color.black);
        b1.setBackground(Color.white);
        b1.setBounds(105, 160, 100, 33);

        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String uname = t1.getText().trim();
                String pwd = t2.getText().trim();

                if(t1.getText().contains("@gmail.com")) {
                    if (uname.isEmpty() || pwd.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Stringing Community dice:\n       Non hai scritto nulla!", "Stringing Community", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Non e' presente alcun indirizzo mail!!", "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    FileWriter fw = new FileWriter("login.txt", true);
                    fw.write(t1.getText()+" "+t2.getText()+" 0\n"); // Appending "0" to indicate not logged in
                    fw.close();

                    // Create a text file with the name before "@" in the email used for registration
                    String[] parts = uname.split("@");
                    String fileName = parts[0] + ".txt";
                    File userFile = new File(fileName);
                    if (userFile.createNewFile()) {
                        System.out.println("File created: " + userFile.getName());
                    } else {
                        System.out.println("File already exists.");
                    }

                    JFrame f = new JFrame();
                    JOptionPane.showMessageDialog(f, "Registration Completed");
                    dispose();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        });




        try {
            BufferedImage img = ImageIO.read(getClass().getResource("logo.png")); // Change this to your image file path
            Image dimg = img.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            ImageIcon imageIcon = new ImageIcon(dimg);
            JLabel imageLabel = new JLabel(imageIcon);
            imageLabel.setBounds(320, 140, 40, 40); // Adjust position and size as needed
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
        add(l4);
        add(l5);


    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
