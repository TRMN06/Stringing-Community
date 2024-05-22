import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class HomePage extends MioFrame implements ActionListener, WindowListener {

    JTextField t1,t2,t3;
    JButton b1, loginButton,profileButton;

    JLabel homeLabel,azioneNavLabel, drammaNavLabel, fantascienzaNavLabel, commediaNavLabel, horrorNavLabel, watchLaterLabel;
    private boolean loggedIn = false;
    LoginSc sc;

    String username;

    JScrollPane scrollPane;

    private JLabel consigliatiLabel,vistiLabel,azioneLabel, drammaLabel, fantascienzaLabel, commediaLabel, horrorLabel, l1;

    private JPanel contentPane, searchBarPanel, navBarPanel;

    ArrayList<Film> moviesList = new ArrayList<>();
    ArrayList<Film> searchResults = new ArrayList<>();

    ArrayList<JPanel> moviePanels = new ArrayList<>();

    private Map<String, Set<Film>> movieIndex = new HashMap<>(); //ricerca piu fast

    private List<Film> azioneMovies = new ArrayList<>();
    private List<Film> drammaMovies = new ArrayList<>();
    private List<Film> fantascienzaMovies = new ArrayList<>();
    private List<Film> commediaMovies = new ArrayList<>();
    private List<Film> horrorMovies = new ArrayList<>();

    public HomePage(String titolo) {
        contentPane = new JPanel(null);

        searchBarPanel = new JPanel(null);
        searchBarPanel.setBounds(1200, 20, 380, 50);

        t1 = new JTextField(60);
        t1.setBounds(0, 0, 270, 30);
        t1.setFont(new Font("Gotham", Font.BOLD, 14));
        t1.setForeground(Color.white);
        t1.setBackground(Color.black);
        t1.setBorder(BorderFactory.createLineBorder(Color.white));

        b1 = new JButton("Cerca");
        b1.setBounds(290, 0, 90, 30);
        b1.setFont(new Font("Gotham", Font.BOLD, 14));
        b1.setForeground(Color.black);
        b1.setBackground(Color.white);
        b1.addActionListener(this);

        loginButton = new JButton("Login");
        loginButton.setBounds(1700, 20, 90, 30);
        loginButton.setFont(new Font("Gotham", Font.BOLD, 14));
        loginButton.setForeground(Color.black);
        loginButton.setBackground(Color.white);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sc = new LoginSc("Stringing Community");
                sc.setBounds(0, 0, 350, 350);
                sc.rendiVisibile(sc);
                dispose();
            }
        });

        readLoginFromFile();

        contentPane.add(loginButton);

        searchBarPanel.add(t1);
        searchBarPanel.add(b1);

        contentPane.add(searchBarPanel);

        vistiLabel = createLabel("Visti Recentemente", 70, 300);
        contentPane.add(vistiLabel);

        // Add your labels
        azioneLabel = createLabel("Azione", 70, 780);
        contentPane.add(azioneLabel);

        drammaLabel = createLabel("Dramma", 70, 1120);
        contentPane.add(drammaLabel);

        fantascienzaLabel = createLabel("Fantascienza", 70, 1460);
        contentPane.add(fantascienzaLabel);

        commediaLabel = createLabel("Commedia", 70, 1800);
        contentPane.add(commediaLabel);

        horrorLabel = createLabel("Horror", 70, 2140);
        contentPane.add(horrorLabel);

        readMoviesFromFile("Film.txt", moviesList); //lettura film

        createButtons(moviesList, 70);

        b1.addActionListener(e -> searchMovies(t1.getText()));

        updateLoginUIfromFile();

        navBarPanel = new JPanel(new GridLayout(1, 7, 20, 0));
        navBarPanel.setBounds(45, 13, 800, 50);

        homeLabel = createNavLabel("Home");
        navBarPanel.add(homeLabel);

        azioneNavLabel = createNavLabel("Azione");
        navBarPanel.add(azioneNavLabel);

        drammaNavLabel = createNavLabel("Dramma");
        navBarPanel.add(drammaNavLabel);

        fantascienzaNavLabel = createNavLabel("Fantascienza");
        navBarPanel.add(fantascienzaNavLabel);

        commediaNavLabel = createNavLabel("Commedia");
        navBarPanel.add(commediaNavLabel);

        horrorNavLabel = createNavLabel("Horror");
        navBarPanel.add(horrorNavLabel);

        watchLaterLabel = createNavLabel("Watch Later");
        navBarPanel.add(watchLaterLabel);

        homeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showHomePage();
            }
        });

        azioneNavLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                MostraFilmNav("Azione");
            }
        });

        drammaNavLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                MostraFilmNav("Dramma");
            }
        });

        fantascienzaNavLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                MostraFilmNav("Fantascienza");
            }
        });

        commediaNavLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                MostraFilmNav("Commedia");
            }
        });

        horrorNavLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                MostraFilmNav("Horror");
            }
        });

        watchLaterLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mostraGuardaDopoFilm();
            }
        });

        contentPane.add(navBarPanel);

        scrollPane = new JScrollPane(contentPane);
        scrollPane.setBounds(0, 0, 800, 600);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        contentPane.setPreferredSize(new Dimension(2000, 2500));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane);
        mostraWatchedFilm();

        initializeMovieIndex();

        setTitle(titolo);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setVisible(true);
    }

    private void mostraWatchedFilm() {
        SwingWorker<List<String>, Void> worker = new SwingWorker<List<String>, Void>() {
            @Override
            protected List<String> doInBackground() throws Exception {
                List<String> lines = new ArrayList<>();

                File file = new File(username + ".txt");

                if (!file.exists()) {
                    System.err.println("file non trova porco : " + file.getAbsolutePath());
                    return lines;
                }

                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        lines.add(line);
                    }
                } catch (IOException e) {
                    System.err.println("errore: " + file.getAbsolutePath());
                    e.printStackTrace();
                }

                return lines;
            }

            @Override
            protected void done() {
                try {
                    List<String> lines = get();
                    clearWatchedMoviesPanels();

                    int labelWidth = 200;
                    int labelHeight = 300;
                    int maxMoviesToShow = 8;
                    int moviesAdded = 0;
                    int spacing = 20;
                    int startX = 70;

                    //loop for reversed file reading
                    for (int i = lines.size() - 1; i >= 0 && moviesAdded < maxMoviesToShow; i--) {
                        String[] parts = lines.get(i).split(",");
                        if (parts.length >= 2 && parts[1].trim().equals("1")) {
                            String movieName = parts[0].trim();
                            for (Film movie : moviesList) {
                                if (movie.getNome().equals(movieName)) {
                                    JPanel moviePanel = createPanel(movie, false);
                                    int xPos = startX + (labelWidth + spacing) * moviesAdded;
                                    moviePanel.setBounds(xPos, 400, labelWidth, labelHeight);
                                    moviePanel.setName("WatchedMoviePanel"); //set nome for identification
                                    contentPane.add(moviePanel);
                                    moviesAdded++;
                                    // Print the movie as it is added
                                    System.out.println("Added to Watched: " + movie.getNome());
                                }
                            }
                        }
                    }

                    contentPane.revalidate();
                    contentPane.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        worker.execute();
    }

    private void mostraGuardaDopoFilm() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                contentPane.removeAll();
                contentPane.add(navBarPanel);
                contentPane.add(searchBarPanel);
                contentPane.add(loginButton);


                try (BufferedReader br = new BufferedReader(new FileReader(username + ".txt"))) {
                    String line;
                    int movieCount = 0;
                    int labelWidth = 200;
                    int labelHeight = 300;
                    int maxFilmsPerRow = 8;
                    int xOffset = 20;
                    int yOffset = 80;
                    int currentX = 20;
                    int currentY = 80;

                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length >= 2 && parts[1].trim().equals("true")) {
                            String movieName = parts[0].trim();
                            for (Film movie : moviesList) {
                                if (movie.getNome().equals(movieName)) {
                                        JPanel moviePanel = createPanel(movie, true);
                                        moviePanel.setBounds(currentX, currentY, labelWidth, labelHeight);
                                        contentPane.add(moviePanel);
                                        movieCount++;
                                        currentX += labelWidth + xOffset;
                                    if (movieCount % maxFilmsPerRow == 0) {
                                            currentX = 20;
                                            currentY += labelHeight + xOffset;
                                    }
                                        System.out.println("Added to Watch Later: " + movie.getNome());
                                    break;
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                contentPane.revalidate();
                contentPane.repaint();

                return null;
            }
        };

        worker.execute();
    }

    private void removeGuardaDopo(String movieName) {
        try {
            File inputFile = new File(username + ".txt");
            File tempFile = new File("temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            boolean found = false;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) { // Check if the line is not empty
                    String[] parts = line.split(",");
                    if (parts.length >= 2 && parts[0].trim().equals(movieName) && parts[1].trim().equals("true")) {
                        // Set the flag to false to mark the movie as removed from Watch Later
                        writer.write(parts[0] + ",false\n");
                        found = true;
                    } else {
                        // Write lines that do not match the movie name directly
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }

            reader.close();
            writer.close();

            //se movie is found and removed, rename temp file to the original file
            if (found) {
                inputFile.delete();
                tempFile.renameTo(inputFile);
            } else {
                //if movie was not found, delete temporary file
                tempFile.delete();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void MostraFilmNav(String genre) {
        //navbar disabled
        setNavbarLabelsEnabled(false);

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                contentPane.removeAll();
                contentPane.add(navBarPanel);
                contentPane.add(searchBarPanel);
                contentPane.add(loginButton);

                List<Film> genreMovies = new ArrayList<>();
                switch (genre) {
                    case "Azione":
                        genreMovies.addAll(azioneMovies);
                        break;
                    case "Dramma":
                        genreMovies.addAll(drammaMovies);
                        break;
                    case "Fantascienza":
                        genreMovies.addAll(fantascienzaMovies);
                        break;
                    case "Commedia":
                        genreMovies.addAll(commediaMovies);
                        break;
                    case "Horror":
                        genreMovies.addAll(horrorMovies);
                        break;
                    default:
                        break;
                }

                //if list for the selected genre is empty, filter movies based on the genre
                if (genreMovies.isEmpty()) {
                    for (Film movie : moviesList) {
                        if (movie.getGenre().equals(genre)) {
                            genreMovies.add(movie);
                        }
                    }
                }

                int labelWidth = 200;
                int labelHeight = 300;
                int maxFilmsPerRow = 8;
                int xOffset = 20;
                int yOffset = 80;

                int batchSize = 2; //number of movies to load per grupo(batch)
                int startIndex = 0;

                //add panel for each movie batch to the content pane
                while (startIndex < genreMovies.size()) {
                    //caalculate the end index for the current batch
                    int endIndex = Math.min(startIndex + batchSize, genreMovies.size());


                    int row = startIndex / maxFilmsPerRow;
                    int col = startIndex % maxFilmsPerRow;
                    for (int i = startIndex; i < endIndex; i++) {
                        Film movie = genreMovies.get(i);
                        JPanel moviePanel = createPanel(movie, false);
                        if (moviePanel != null) {
                            int x = 20 + col * (labelWidth + xOffset);
                            int y = yOffset + row * (labelHeight + xOffset);
                            moviePanel.setBounds(x, y, labelWidth, labelHeight);
                            contentPane.add(moviePanel);
                            col++;
                            if (col >= maxFilmsPerRow) {
                                col = 0;
                                row++;
                            }
                        } else {
                            System.err.println("Error: porco dio il panel non va " + movie.getNome());
                        }
                    }

                    startIndex = endIndex;
                }

                contentPane.revalidate();
                contentPane.repaint();

                return null;
            }

            @Override
            protected void done() {
                // Re-enable the navbar labels after processing is finished
                setNavbarLabelsEnabled(true);
            }
        };

        // Execute the SwingWorker
        worker.execute();
    }

    private void setNavbarLabelsEnabled(boolean enabled) {
        azioneNavLabel.setEnabled(enabled);
        drammaNavLabel.setEnabled(enabled);
        fantascienzaNavLabel.setEnabled(enabled);
        commediaNavLabel.setEnabled(enabled);
        horrorNavLabel.setEnabled(enabled);
    }

    private void readLoginFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader("login.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length >= 3 && parts[2].equals("1")) {
                    loggedIn = true;
                    String email = parts[0];
                    int atIndex = email.indexOf('@');
                    if (atIndex != -1) {
                        username = email.substring(0, atIndex);
                        System.out.println("Username: " + username);
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateLoginUIfromFile() {
        if (loggedIn) {
            contentPane.remove(loginButton);

            profileButton = new JButton("Profilo");
            profileButton.setBounds(1700, 20, 90, 30);
            profileButton.setFont(new Font("Gotham", Font.BOLD, 14));
            profileButton.setForeground(Color.black);
            profileButton.setBackground(Color.white);
            contentPane.add(profileButton);


            profileButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    contentPane.removeAll();
                    contentPane.add(navBarPanel);
                    contentPane.add(searchBarPanel);

                    // Create and add new labels
                    JLabel dettagliLabel = new JLabel("Dettagli");
                    JLabel abbonamentiLabel = new JLabel("Abbonamenti");
                    JLabel comunicazioniLabel = new JLabel("Comunicazioni");
                    JLabel logoutLabel = new JLabel("Log Out");

                    dettagliLabel.setBounds(200, 400, 150, 30);
                    dettagliLabel.setFont(new Font("Gotham", Font.ITALIC, 20));
                    dettagliLabel.setForeground(Color.white);

                    abbonamentiLabel.setBounds(200, 450, 150, 30);
                    abbonamentiLabel.setFont(new Font("Gotham", Font.ITALIC, 20));
                    abbonamentiLabel.setForeground(Color.white);

                    comunicazioniLabel.setBounds(200, 500, 150, 30);
                    comunicazioniLabel.setFont(new Font("Gotham", Font.ITALIC, 20));
                    comunicazioniLabel.setForeground(Color.white);

                    logoutLabel.setBounds(200, 550, 150, 30);
                    logoutLabel.setFont(new Font("Gotham", Font.ITALIC, 20));
                    logoutLabel.setForeground(Color.red);

                    dettagliLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            JLabel emailLabel = new JLabel("La tua mail: ");
                            emailLabel.setFont(new Font("Gotham", Font.ITALIC, 18));
                            JLabel passwordLabel = new JLabel("La tua password: ");
                            passwordLabel.setFont(new Font("Gotham", Font.ITALIC, 18));
                            JLabel emailValue = new JLabel();
                            emailValue.setFont(new Font("Gotham", Font.ITALIC, 15));
                            JLabel passwordValue = new JLabel();
                            passwordValue.setFont(new Font("Gotham", Font.ITALIC, 15));
                            JLabel change = new JLabel("Vuoi cambiare la password?");
                            change.setFont(new Font("Ghotam", Font.ITALIC, 10));
                            JButton salva = new JButton("Clicca qui!");

                            emailLabel.setBounds(750, 400, 100, 30);
                            passwordLabel.setBounds(750, 440, 1500, 30);
                            emailValue.setBounds(900, 400, 200, 30);
                            passwordValue.setBounds(900, 440, 200, 30);
                            change.setBounds(750, 480, 150, 30);
                            salva.setBounds(900,480,100,30);


                            contentPane.add(emailLabel);
                            contentPane.add(passwordLabel);
                            contentPane.add(emailValue);
                            contentPane.add(passwordValue);
                            contentPane.add(change);
                            contentPane.add(salva);
                            contentPane.revalidate();
                            contentPane.repaint();

                            try (BufferedReader br = new BufferedReader(new FileReader("login.txt"))) {
                                String line;
                                if ((line = br.readLine()) != null) {
                                    String[] parts = line.split(" ");
                                    if (parts.length >= 2) {
                                        emailValue.setText(parts[0]);
                                        passwordValue.setText(parts[1]);
                                    }
                                }
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }

                            salva.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    PswForgot pf;
                                    pf = new PswForgot();
                                    pf.setVisible(true);
                                    pf.setBounds(400, 200, 330, 250);
                                    passwordValue.setText("");
                                }
                            });
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {
                            dettagliLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            dettagliLabel.setCursor(Cursor.getDefaultCursor());
                        }
                    });


                    abbonamentiLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseEntered(MouseEvent e) {
                            abbonamentiLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            abbonamentiLabel.setCursor(Cursor.getDefaultCursor());
                        }
                    });

                    comunicazioniLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseEntered(MouseEvent e) {
                            comunicazioniLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            comunicazioniLabel.setCursor(Cursor.getDefaultCursor());
                        }
                    });

                    logoutLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            loggedIn = false;

                            contentPane.remove(profileButton);

                            contentPane.add(loginButton);

                            contentPane.revalidate();
                            contentPane.repaint();

                            updateLoginFile(username, "0");

                            username = null;
                            loggedIn = false;

                            showHomePage();
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {
                            logoutLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            logoutLabel.setCursor(Cursor.getDefaultCursor());
                        }
                    });

                    contentPane.add(dettagliLabel);
                    contentPane.add(abbonamentiLabel);
                    contentPane.add(comunicazioniLabel);
                    contentPane.add(logoutLabel);

                    contentPane.revalidate();
                    contentPane.repaint();
                }
            });
        }
    }

    private void updateLoginFile(String email, String newValue) {
        Path path = Paths.get("login.txt");
        email = email + "@gmail.com";

        try {
            // Read all lines from the file
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

            // Iterate through the lines to find and update the entry with the given email
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] parts = line.split("\\s+");
                if (parts.length >= 3 && parts[0].trim().equals(email)) {
                    parts[2] = newValue;
                    lines.set(i, String.join(" ", parts));
                    break;
                }
            }

            // Write the updated lines back to the file
            Files.write(path, lines, StandardCharsets.UTF_8);

            // Log success
            System.out.println("Login status updated successfully for email: " + email);
        } catch (IOException e) {
            // Log and handle the exception
            System.err.println("Error updating login status for email: " + email);
            e.printStackTrace();
        }
    }

    private void keepUserLoggedIn() {
        if (loggedIn) {
            try {
                File inputFile = new File("login.txt");
                File tempFile = new File("temp.txt");//per evitare perdita dati

                BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\t");
                    if (parts.length >= 3 && parts[2].equals("1")) {
                        writer.write(parts[0] + "\t" + parts[1] + "\t" + "1");
                    } else {
                        writer.write(line);
                    }
                    writer.newLine();
                }

                reader.close();
                writer.close();

                //rename temp file tooriginal file
                inputFile.delete();
                tempFile.renameTo(inputFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void windowClosing(WindowEvent e) {
        //override del windowClosing
        keepUserLoggedIn();
        super.windowClosing(e);
    }

    private void readMoviesFromFile(String fileName, ArrayList<Film> list) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String movieName = parts[0];
                    String movieLink = parts[1];
                    String movieImageName = parts[2];
                    String genre = parts[3];

                    Film movie;
                    switch (genre) {
                        case "Azione":
                            movie = new FilmAzione(movieName, movieLink, movieImageName, genre);
                            list.add(movie);
                            break;
                        case "Dramma":
                            movie = new FilmDramma(movieName, movieLink, movieImageName, genre);
                            list.add(movie);
                            break;
                        case "Fantascienza":
                            movie = new FilmFantascienza(movieName, movieLink, movieImageName, genre);
                            list.add(movie);
                            break;
                        case "Commedia":
                            movie = new FilmCommedia(movieName, movieLink, movieImageName, genre);
                            list.add(movie);
                            break;
                        case "Horror":
                            movie = new FilmHorror(movieName, movieLink, movieImageName, genre);
                            list.add(movie);
                            break;
                        default:
                            movie = null;
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JLabel createLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        Font labelFont = label.getFont();
        label.setFont(new Font(labelFont.getName(), Font.BOLD, 25));
        label.setBounds(x, y, 400, 32);
        return label;
    }

    private JLabel createNavLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Gotham", Font.BOLD, 14));
        label.setForeground(Color.white);
        label.setHorizontalAlignment(SwingConstants.CENTER);

        //mouselisten per hover
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                label.setCursor(new Cursor(Cursor.HAND_CURSOR));
                label.setBackground(Color.black);
                label.setOpaque(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                label.setBackground(null);
                label.setOpaque(false);
            }
        });

        return label;
    }

    private void createButtons(ArrayList<Film> movies, int startingX) {
        JPanel panel = contentPane;
        int yOffsetAzione = 810;
        int yOffsetDramma = 1150;
        int yOffsetFantascienza = 1490;
        int yOffsetCommedia = 1830;
        int yOffsetHorror = 2170;

        int startingXAzione = 70;
        int startingXDramma = 70;
        int startingXFantascienza = 70;
        int startingXACommedia = 70;
        int startingXHorror = 70;

        int labelWidth = 200;
        int labelHeight = 300;
        int maxFilmsPerRow = 8;

        int azioneCount = 0;
        int drammaCount = 0;
        int fantascienzaCount = 0;
        int commediaCount = 0;
        int horrorCount = 0;

        for (Film movie : movies) {
            JPanel moviePanel = createPanel(movie, false);

            if (movie instanceof FilmAzione) {
                if (azioneCount < maxFilmsPerRow) {
                    moviePanel.setBounds(startingXAzione, yOffsetAzione, labelWidth, labelHeight);
                    startingXAzione += labelWidth + 20;
                    azioneCount++;
                }
            } else if (movie instanceof FilmDramma) {
                if (drammaCount < maxFilmsPerRow) {
                    moviePanel.setBounds(startingXDramma, yOffsetDramma, labelWidth, labelHeight);
                    startingXDramma += labelWidth + 20;
                    drammaCount++;
                }
            } else if (movie instanceof FilmFantascienza) {
                if (fantascienzaCount < maxFilmsPerRow) {
                    moviePanel.setBounds(startingXFantascienza, yOffsetFantascienza, labelWidth, labelHeight);
                    startingXFantascienza += labelWidth + 20;
                    fantascienzaCount++;
                }
            } else if (movie instanceof FilmCommedia) {
                if (commediaCount < maxFilmsPerRow) {
                    moviePanel.setBounds(startingXACommedia, yOffsetCommedia, labelWidth, labelHeight);
                    startingXACommedia += labelWidth + 20;
                    commediaCount++;
                }
            } else if (movie instanceof FilmHorror) {
                if (horrorCount < maxFilmsPerRow) {
                    moviePanel.setBounds(startingXHorror, yOffsetHorror, labelWidth, labelHeight);
                    startingXHorror += labelWidth + 20;
                    horrorCount++;
                }
            }
            panel.add(moviePanel);
            moviePanels.add(moviePanel); // aggiunta panel to list
        }
    }

    public JPanel createPanel(Film movie, boolean showRemoveButton) {
        try {
            BufferedImage img = ImageIO.read(getClass().getResource(movie.getImg_nome()));

            Image scaledImg = img.getScaledInstance(200, 250, Image.SCALE_SMOOTH);
            ImageIcon imageIcon = new ImageIcon(scaledImg);

            int hoverWidth = (int) (200 * 1.1);
            int hoverHeight = (int) (250 * 1.1);
            Image hoverImg = img.getScaledInstance(hoverWidth, hoverHeight, Image.SCALE_SMOOTH);
            ImageIcon hoverIcon = new ImageIcon(hoverImg);

            JPanel panel = new JPanel(new BorderLayout());
            panel.setPreferredSize(new Dimension(200, 300));

            JLabel label = new JLabel(imageIcon);
            label.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JPanel buttonPanel = new JPanel(new GridLayout(1, 2));

            JButton actionButton = new JButton(showRemoveButton ? "Remove" : "Add");
            actionButton.setFont(new Font("Gotham", Font.BOLD, 14));
            actionButton.setForeground(Color.black);
            actionButton.setBackground(Color.white);
            actionButton.setVisible(false);
            actionButton.setEnabled(loggedIn);

            actionButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (loggedIn) {
                        if (showRemoveButton) {
                            removeGuardaDopo(movie.getNome());

                            mostraGuardaDopoFilm();
                        } else {
                            //add movie to wl only if not added already
                            if (!isMovieInGuadaDopo(movie.getNome())) {
                                addToGuardaDopo(movie.getNome());
                                JOptionPane.showMessageDialog(null, "Film Aggiunto a Watch Later", "Info", JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Devi eseguire il login per eseguire questa azione.", "Avviso", JOptionPane.WARNING_MESSAGE);
                    }
                }
            });

            JButton openButton = new JButton("Open");
            openButton.setFont(new Font("Gotham", Font.BOLD, 14));
            openButton.setForeground(Color.black);
            openButton.setBackground(Color.white);
            openButton.setVisible(false);

            openButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (loggedIn) {
                        openLink(movie.getLink());

                        writeMovieToUserFile(username + ".txt", movie.getNome());

                        Timer timer = new Timer(500, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                mostraWatchedFilm();
                            }
                        });
                        timer.setRepeats(false); //only execute once
                        timer.start();
                    } else {
                        JOptionPane.showMessageDialog(null, "Devi eseguire il login per accedere al film.", "Avviso", JOptionPane.WARNING_MESSAGE);
                    }
                }
            });

            buttonPanel.add(actionButton);
            buttonPanel.add(openButton);

            Timer hoverTimer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    label.setIcon(imageIcon);
                    label.setSize(200, 250);
                    actionButton.setVisible(false);
                    openButton.setVisible(false);
                    panel.setPreferredSize(new Dimension(200, 300));
                    panel.revalidate();
                    panel.repaint();
                }
            });
            hoverTimer.setRepeats(false);

            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hoverTimer.stop();
                    label.setIcon(hoverIcon);
                    label.setSize(hoverWidth, hoverHeight);
                    actionButton.setVisible(true);
                    openButton.setVisible(true);
                    panel.setPreferredSize(new Dimension(hoverWidth, hoverHeight + 50));
                    panel.revalidate();
                    panel.repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hoverTimer.restart();
                }
            });

            actionButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    actionButton.setVisible(true);
                    actionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    hoverTimer.stop();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hoverTimer.restart();
                }
            });

            openButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    openButton.setVisible(true);
                    openButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    hoverTimer.stop();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hoverTimer.restart();
                }
            });

            panel.add(label, BorderLayout.CENTER);
            panel.add(buttonPanel, BorderLayout.SOUTH);

            return panel;
        } catch (IOException e) {
            e.printStackTrace();
            return new JPanel(); //return a default panel if image don't load
        }
    }

    private void clearWatchedMoviesPanels() {
        // lopp in contentPane and remove watched movie panels
        for (Component component : contentPane.getComponents()) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                if ("WatchedMoviePanel".equals(panel.getName())) {
                    contentPane.remove(panel);
                }
            }
        }

        contentPane.revalidate();
        contentPane.repaint();
    }

    public void writeMovieToUserFile(String fileName, String movieName) {
        try {
            //read the file to check if movie is present
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            boolean movieExists = false;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith(movieName + ",")) {
                    movieExists = true;
                    break;
                }
            }
            reader.close();

            //ff the movie is not present, append it to file
            if (!movieExists) {
                FileWriter writer = new FileWriter(fileName, true); // Append mode
                writer.write(movieName + ",1\n");
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isMovieInGuadaDopo(String movieName) {
        try (BufferedReader br = new BufferedReader(new FileReader(username + ".txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].trim().equals(movieName) && parts[1].trim().equals("true")) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void addToGuardaDopo(String movieName) {
        if (loggedIn) {
            try (FileWriter writer = new FileWriter(username + ".txt", true)) {
                writer.write(movieName + ",true\n");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void showHomePage() {

        contentPane.removeAll();
        contentPane.add(navBarPanel);
        contentPane.add(searchBarPanel);

        contentPane.add(azioneLabel);
        contentPane.add(drammaLabel);
        contentPane.add(fantascienzaLabel);
        contentPane.add(commediaLabel);
        contentPane.add(horrorLabel);

        contentPane.add(vistiLabel);

        mostraWatchedFilm();

        if (!loggedIn) {
            contentPane.add(loginButton);
        } else {
            contentPane.add(profileButton);
        }

        for (JPanel moviePanel : moviePanels) {
            contentPane.add(moviePanel);
        }

        contentPane.setPreferredSize(new Dimension(1600, 2500));

        scrollPane.setViewportView(contentPane);

        contentPane.revalidate();
        contentPane.repaint();
    }


    //fill a movie index map by grouping movies based on their first word, for optimizing searches
    private void initializeMovieIndex() {
        for (Film movie : moviesList) {
            String firstWord = movie.getNome().toLowerCase().split("\\s+")[0];
            movieIndex.computeIfAbsent(firstWord, k -> new HashSet<>()).add(movie);
        }
    }

    private void searchMovies(String searchText) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                //disable search button at start of the search
                SwingUtilities.invokeLater(() -> b1.setEnabled(false));

                searchResults.clear();

                if (searchText.isEmpty()) {
                    showHomePage();
                    return null;
                }

                String lowerCaseSearchText = searchText.toLowerCase();
                Set<Film> uniqueResults = new HashSet<>();

                //loop that searches inside the map, based on the first word in text field
                for (Map.Entry<String, Set<Film>> entry : movieIndex.entrySet()) {
                    if (entry.getKey().startsWith(lowerCaseSearchText)) {
                        uniqueResults.addAll(entry.getValue()
                                .stream()
                                .filter(movie -> movie.getNome().toLowerCase().contains(lowerCaseSearchText))
                                .collect(Collectors.toSet()));
                    }
                }

                // Add results to the searchResults list from the Set to ensure uniqueness
                searchResults.addAll(uniqueResults);

                SwingUtilities.invokeLater(() -> {
                    contentPane.removeAll();
                    contentPane.add(navBarPanel);
                    contentPane.add(searchBarPanel);
                    if (!loggedIn) {
                        contentPane.add(loginButton);
                    } else {
                        contentPane.add(profileButton);
                    }

                    //start coordi for search results
                    int startingX = 70;
                    int startingY = 100;
                    int labelWidth = 200;
                    int labelHeight = 300;
                    int maxFilmsPerRow = 8;
                    int xOffset = 20;

                    int currentX = startingX;
                    int currentY = startingY;

                    for (Film movie : searchResults) {
                        JPanel moviePanel = createPanel(movie, false);
                        moviePanel.setBounds(currentX, currentY, labelWidth, labelHeight);

                        currentX += labelWidth + xOffset;
                        if ((currentX + labelWidth + xOffset) > contentPane.getWidth()) {
                            currentX = startingX;
                            currentY += labelHeight + xOffset;
                        }

                        contentPane.add(moviePanel);
                    }

                    contentPane.revalidate();
                    contentPane.repaint();
                });

                return null;
            }

            @Override
            protected void done() {
                //enable search button after search is complete
                SwingUtilities.invokeLater(() -> b1.setEnabled(true));
            }
        };

        worker.execute();
    }

    private void openLink(String link) {
        try {
            Desktop.getDesktop().browse(new URI(link));
        } catch (IOException | URISyntaxException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == b1) {
            // Search button clicked
            String searchText = t1.getText().trim();
            searchMovies(searchText);
        }
    }

    private class ButtonClickListener implements ActionListener {
        private String link;

        public ButtonClickListener(String link) {
            this.link = link;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Desktop.getDesktop().browse(new URI(link));
            } catch (IOException | URISyntaxException ex) {
                ex.printStackTrace();
            }
        }
    }
}