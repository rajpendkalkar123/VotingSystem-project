import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.HashSet;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class VotingSystem {

    static HashMap<String, User> userMap = new HashMap<>();
    static HashMap<String, Candidate> candidateMap = new HashMap<>();
    static HashSet<String> voters = new HashSet<>();
    static boolean electionEnded = false;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Voting System - Login/Register");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        frame.setSize(600, 400);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("Voting Portal");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        frame.add(titleLabel, gbc);

        JPanel panel = new JPanel(new GridBagLayout());
        gbc.gridwidth = 1;

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);
        JLabel aadhaarLabel = new JLabel("Aadhaar Number:");
        JTextField aadhaarField = new JTextField(20);
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");
        JButton changePasswordButton = new JButton("Change Password");

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(aadhaarLabel, gbc);
        gbc.gridx = 1;
        panel.add(aadhaarField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(loginButton, gbc);
        gbc.gridx = 1;
        panel.add(registerButton, gbc);
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(changePasswordButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        frame.add(panel, gbc);
        frame.setVisible(true);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String aadhaar = aadhaarField.getText().trim();

            if (username.equals("admin") && password.equals("admin123")) {
                new AdminPanel(frame);
            } else if (userMap.containsKey(username)) {
                User user = userMap.get(username);
                if (user.getPassword().equals(password) && user.getAadhaar().equals(aadhaar)) {
                    new UserPanel(frame, username);
                } else {
                    StringBuilder errorMessage = new StringBuilder("Invalid login!");
                    if (!user.getPassword().equals(password)) {
                        errorMessage.append("\nIncorrect password.");
                    }
                    if (!user.getAadhaar().equals(aadhaar)) {
                        errorMessage.append("\nIncorrect Aadhaar number.");
                    }
                    JOptionPane.showMessageDialog(frame, errorMessage.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid login!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String aadhaar = aadhaarField.getText().trim();

            boolean isPasswordUnique = userMap.values().stream().noneMatch(user -> user.getPassword().equals(password));
            boolean isAadhaarUnique = userMap.values().stream().noneMatch(user -> user.getAadhaar().equals(aadhaar));

            StringBuilder errorMessage = new StringBuilder("Invalid registration!");

            if (username.isEmpty()) {
                errorMessage.append("\nUsername cannot be empty.");
            }
            if (password.length() < 8) {
                errorMessage.append("\nPassword must be at least 8 characters long.");
            }
            if (aadhaar.length() != 12 || !aadhaar.chars().allMatch(Character::isDigit)) {
                errorMessage.append("\nAadhaar number must be 12 digits long.");
            }
            if (!isPasswordUnique) {
                errorMessage.append("\nPassword is already in use.");
            }
            if (!isAadhaarUnique) {
                errorMessage.append("\nAadhaar number is already in use.");
            }

            if (errorMessage.toString().equals("Invalid registration!")) {
                userMap.put(username, new User(username, password, aadhaar));
                JOptionPane.showMessageDialog(frame, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, errorMessage.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        changePasswordButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String newPassword = new String(passwordField.getPassword()).trim();
            if (userMap.containsKey(username)) {
                userMap.get(username).setPassword(newPassword);
                JOptionPane.showMessageDialog(frame, "Password changed!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "User not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

class AdminPanel {
    private String selectedImagePath;

    public AdminPanel(JFrame frame) {
        frame.setVisible(false);
        JFrame adminFrame = new JFrame("Admin Panel");
        adminFrame.setSize(600, 400);
        adminFrame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        adminFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel nameLabel = new JLabel("Candidate Name:");
        JTextField nameField = new JTextField(20);
        JLabel numberLabel = new JLabel("Candidate Number:");
        JTextField numberField = new JTextField(20);
        JLabel symbolLabel = new JLabel("Candidate Symbol (Upload Image):");
        JButton uploadButton = new JButton("Upload Image");
        JButton addCandidateButton = new JButton("Add Candidate");
        JButton endElectionButton = new JButton("End Election");
        JButton viewResultsButton = new JButton("View Results");
        JButton backButton = new JButton("Back");

        gbc.gridx = 0;
        gbc.gridy = 0;
        adminFrame.add(nameLabel, gbc);
        gbc.gridx = 1;
        adminFrame.add(nameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        adminFrame.add(numberLabel, gbc);
        gbc.gridx = 1;
        adminFrame.add(numberField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        adminFrame.add(symbolLabel, gbc);
        gbc.gridx = 1;
        adminFrame.add(uploadButton, gbc);

        uploadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Choose Candidate Symbol Image");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "png", "gif"));
            int returnValue = fileChooser.showOpenDialog(adminFrame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                selectedImagePath = fileChooser.getSelectedFile().getAbsolutePath();
                JOptionPane.showMessageDialog(adminFrame, "Image uploaded: " + selectedImagePath, "Image Uploaded", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 3;
        adminFrame.add(addCandidateButton, gbc);
        gbc.gridy = 4;
        adminFrame.add(endElectionButton, gbc);
        gbc.gridy = 5;
        adminFrame.add(viewResultsButton, gbc);
        gbc.gridy = 6;
        adminFrame.add(backButton, gbc);

        addCandidateButton.addActionListener(e -> {
            String name = nameField.getText();
            String number = numberField.getText();

            if (!name.isEmpty() && !number.isEmpty() && selectedImagePath != null) {
                VotingSystem.candidateMap.put(number, new Candidate(name, number, selectedImagePath));
                JOptionPane.showMessageDialog(adminFrame, "Candidate added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(adminFrame, "Please fill all fields and upload an image!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        endElectionButton.addActionListener(e -> {
            VotingSystem.electionEnded = true;
            JOptionPane.showMessageDialog(adminFrame, "Election ended!", "Info", JOptionPane.INFORMATION_MESSAGE);
        });

        viewResultsButton.addActionListener(e -> {
            StringBuilder results = new StringBuilder("Election Results:\n\n");
            for (Candidate candidate : VotingSystem.candidateMap.values()) {
                results.append(candidate.getName()).append(" - ").append(candidate.getVotes()).append(" votes\n");
            }
            JOptionPane.showMessageDialog(adminFrame, results.toString(), "Election Results", JOptionPane.INFORMATION_MESSAGE);
        });

        backButton.addActionListener(e -> {
            adminFrame.dispose();
            frame.setVisible(true);
        });

        adminFrame.setVisible(true);
    }
}

class UserPanel {
    public UserPanel(JFrame frame, String username) {
        frame.setVisible(false);
        JFrame userFrame = new JFrame("Vote or View Results");
        userFrame.setSize(600, 400);
        userFrame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        userFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        if (VotingSystem.electionEnded) {
            // If election has ended, show the results as a bar graph
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            for (Candidate candidate : VotingSystem.candidateMap.values()) {
                dataset.addValue(candidate.getVotes(), candidate.getName(), "Candidates");
            }

            JFreeChart barChart = ChartFactory.createBarChart(
                "Election Results", 
                "Candidates", 
                "Votes", 
                dataset);

            ChartPanel chartPanel = new ChartPanel(barChart);
            chartPanel.setPreferredSize(new Dimension(500, 300));
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            userFrame.add(chartPanel, gbc);

            // Back button to return to the login screen
            JButton backButton = new JButton("Back");
            gbc.gridy = 1; gbc.gridwidth = 1;
            userFrame.add(backButton, gbc);
            backButton.addActionListener(e -> {
                userFrame.dispose();
                frame.setVisible(true);
            });

        } else {
            // Display candidates and allow voting
            JLabel voteLabel = new JLabel("Choose a candidate:");
            gbc.gridx = 0; gbc.gridy = 0; userFrame.add(voteLabel, gbc);

            int row = 1; // Start after vote label
            for (Candidate candidate : VotingSystem.candidateMap.values()) {
                JPanel candidatePanel = new JPanel();
                candidatePanel.setLayout(new BorderLayout());

                JLabel imageLabel = new JLabel();
                try {
                    ImageIcon imageIcon = new ImageIcon(candidate.getSymbolPath());
                    Image image = imageIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(image));
                } catch (Exception e) {
                    imageLabel.setText("Image not available");
                }

                JLabel nameLabel = new JLabel(candidate.getName());
                nameLabel.setHorizontalAlignment(SwingConstants.CENTER);

                candidatePanel.add(imageLabel, BorderLayout.NORTH);
                candidatePanel.add(nameLabel, BorderLayout.SOUTH);

                JButton voteButton = new JButton("Vote for " + candidate.getName());
                voteButton.addActionListener(e -> {
                    if (!VotingSystem.voters.contains(username)) {
                        candidate.vote();
                        VotingSystem.voters.add(username);
                        JOptionPane.showMessageDialog(userFrame, "Vote cast for " + candidate.getName() + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        userFrame.dispose();
                        frame.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(userFrame, "You have already voted!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });

                gbc.gridy = row++;
                userFrame.add(candidatePanel, gbc);
                gbc.gridy = row++;
                userFrame.add(voteButton, gbc);
            }

            JButton backButton = new JButton("Back");
            gbc.gridy = row;
            userFrame.add(backButton, gbc);
            backButton.addActionListener(e -> {
                userFrame.dispose();
                frame.setVisible(true);
            });
        }

        userFrame.setVisible(true);
    }
}

class User {
    private String username;
    private String password;
    private String aadhaar;

    public User(String username, String password, String aadhaar) {
        this.username = username;
        this.password = password;
        this.aadhaar = aadhaar;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getAadhaar() {
        return aadhaar;
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }
}

class Candidate {
    private String name;
    private String number;
    private String symbolPath;
    private int votes = 0;

    public Candidate(String name, String number, String symbolPath) {
        this.name = name;
        this.number = number;
        this.symbolPath = symbolPath;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getSymbolPath() {
        return symbolPath;
    }

    public int getVotes() {
        return votes;
    }

    public void vote() {
        votes++;
    }
} 