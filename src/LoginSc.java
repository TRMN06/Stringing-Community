import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.io.*;

public class LoginSc extends MioFrame implements ActionListener, WindowListener, KeyListener {

    JTextField t1, t2;
    JButton b1, b2, b3;
    JLabel l1, l2, l3, l4;
    private boolean login = false;

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public boolean enterPressed;

    public LoginSc(String titolo) {
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //ciao



        l1 = new JLabel("Stringing Community");
        l1.setFont(new Font("Gotham", Font.BOLD, 30));
        l1.setForeground(Color.white);
        l1.setBounds(10, 10, 315, 35);

        l3 = new JLabel("Email:");
        l3.setFont(new Font("Netflix Sans", Font.ITALIC, 14));
        l3.setForeground(Color.white);
        l3.setBounds(55, 60, 60, 30);

        t1 = new JTextField(60);
        t1.setBounds(100, 60, 120, 30);

        l2 = new JLabel("");
        l2.setBounds(80, 245, 300, 30);

        l4 = new JLabel("Password:");
        l4.setFont(new Font("Netflix Sans", Font.ITALIC, 14));
        l4.setForeground(Color.white);
        l4.setBounds(30, 100, 90, 30);

        t2 = new JPasswordField(60);
        t2.setBounds(100, 100, 120, 30);

        b1 = new JButton("Sign In");
        b1.setFont(new Font("Gotham", Font.BOLD, 14));
        b1.setForeground(Color.black);
        b1.setBackground(Color.white);
        b1.setBounds(110, 140, 100, 33);

        b2 = new JButton("Sign Up");
        b2.setFont(new Font("Gotham", Font.BOLD, 14));
        b2.setForeground(Color.black);
        b2.setBackground(Color.white);
        b2.setBounds(110, 180, 100, 33);

        b3 = new JButton("Reset Password");
        b3.setFont(new Font("Gotham", Font.BOLD, 12));
        b3.setForeground(Color.black);
        b3.setBackground(Color.white);
        b3.setBounds(100, 220, 130, 30);

        add(l3);
        add(l1);
        add(l2);
        add(t1);
        add(l4);
        add(t2);
        add(b1);
        add(b2);

        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String uname = t1.getText().trim();
                String pwd = t2.getText().trim();

                if (uname.isEmpty() || pwd.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Stringing Community dice:\n       Non hai scritto nulla!", "Attenzione", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                boolean matched = false;

                try {
                    FileReader fr = new FileReader("login.txt");
                    BufferedReader br = new BufferedReader(fr);
                    StringBuilder fileContent = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        System.out.println("Line from file: " + line); // Print the line from the file for debugging
                        if (line.equals(uname + " " + pwd + " 0")) { // Check if login is successful
                            matched = true;
                            fileContent.append(uname).append(" ").append(pwd).append(" 1").append("\n"); // Change "0" to "1"
                        } else {
                            fileContent.append(line).append("\n"); // Keep the line unchanged
                        }
                    }

                    fr.close();

                    // Write back to the file
                    FileWriter fw = new FileWriter("login.txt");
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(fileContent.toString());
                    bw.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                if (t1.getText().contains("@gmail.com")) {
                    if (matched) {
                        dispose();
                        login = true;
                        HomePage sc = new HomePage("StringingCommunity");
                        sc.setBounds(0, 0, 2000, 2000);
                        sc.setExtendedState(JFrame.MAXIMIZED_BOTH);
                        sc.setVisible(true);
                    } else {
                        l2.setText("Invalid Username or Password");
                        add(b3);
                        revalidate();
                        repaint();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Non e' presente alcun indirizzo mail!!", "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        b2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SignUp s = new SignUp();
                s.setVisible(true);
                s.setBounds(200, 200, 500, 300);
            }
        });

        b3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PswForgot pf;
                pf = new PswForgot();
                pf.setVisible(true);
                pf.setBounds(400, 200, 330, 250);
            }
        });

        b1.addKeyListener(new KeyListener() {

            public void ciao(int f){
                if (f == KeyEvent.VK_ENTER){
                    enterPressed = true;
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER){
                    String uname = t1.getText().trim();
                    String pwd = t2.getText().trim();

                    if (uname.isEmpty() || pwd.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Stringing Community dice:\n       Non hai scritto nulla!", "Attenzione", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    boolean matched = false;

                    try {
                        FileReader fr = new FileReader("login.txt");
                        BufferedReader br = new BufferedReader(fr);
                        String line;
                        while ((line = br.readLine()) != null) {
                            if (line.equals(uname + "\t" + pwd)) {
                                matched = true;
                                break;
                            }
                        }
                        fr.close();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    if(t1.getText().contains("@gmail.com")) {
                        if (matched) {
                            dispose();
                            HomePage sc = new HomePage("StringingCommunity");
                            sc.setBounds(0, 0, 2000, 2000);
                            sc.setExtendedState(JFrame.MAXIMIZED_BOTH);
                            sc.setVisible(true);
                        } else {
                            l2.setText("Invalid Username or Password");
                            add(b3);
                            revalidate();
                            repaint();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Non e' presente alcun indirizzo mail!!", "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }



            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }


    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
