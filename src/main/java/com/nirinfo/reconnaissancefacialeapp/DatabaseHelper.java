    package com.nirinfo.reconnaissancefacialeapp;

    import java.sql.*;
    import java.util.Optional;

    public class DatabaseHelper {

        private Connection connection;

        public DatabaseHelper() {
            try {
                String url = "jdbc:mysql://localhost:3306/reconn_face_bd"; // Nom de la base de données
                String user = "root";  // Nom d'utilisateur MySQL
                String password = "";  // Mot de passe MySQL

                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Connexion à la base de données réussie !");
            } catch (SQLException e) {
                System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
            }
        }

        public int getLastUserId() {
            try {
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT MAX(id) FROM users;");
                if (rs.next()) {
                    return rs.getInt(1);
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors de la récupération du dernier ID utilisateur : " + e.getMessage());
            }
            return -1;
        }

        public boolean insertUserWithPhoto(String userName, byte[] photo) {
            String sql = "INSERT INTO users (name, photo) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, userName);   // Associer le nom d'utilisateur
                statement.setBytes(2, photo);      // Associer la photo au format BLOB

                int rowsInserted = statement.executeUpdate(); // Exécuter la requête
                return rowsInserted > 0; // Retourne true si une ligne a été insérée
            } catch (SQLException e) {
                System.err.println("Erreur lors de l'insertion : " + e.getMessage());
            }
            return false; // Retourne false si une exception ou une erreur survient
        }

        public boolean insertUserWithPhotoAndEmail(String name, String email, byte[] photo) {
            String query = "INSERT INTO users (name, email, photo) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, name);
                stmt.setString(2, email);
                stmt.setBytes(3, photo);
                stmt.executeUpdate();
                return true;
            } catch (SQLException e) {
                System.err.println("Erreur lors de l'insertion de l'utilisateur : " + e.getMessage());
                return false;
            }
        }

        public Optional<byte[]> getUserPhoto(int userId) {
            String query = "SELECT photo FROM users WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return Optional.of(rs.getBytes("photo"));
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors de la récupération de la photo : " + e.getMessage());
            }
            return Optional.empty();
        }

        public Optional<byte[]> getUserPhotoByEmailAndName(String userName, String userEmail) {
            String query = "SELECT photo FROM users WHERE name = ? AND email = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, userName);
                stmt.setString(2, userEmail);
                ResultSet resultSet = stmt.executeQuery();
                if (resultSet.next()) {
                    return Optional.of(resultSet.getBytes("photo"));
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors de la récupération de la photo par email et nom : " + e.getMessage());
            }
            return Optional.empty();
        }

        public Connection getConnection() {
        return connection;
    }
            public byte[] getImageFromDatabase(String name, String email) {
            String query = "SELECT photo FROM users WHERE name = ? AND email = ?";
            byte[] imageData = null;

            try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
                pstmt.setString(1, name);
                pstmt.setString(2, email);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        imageData = rs.getBytes("photo");
                        if (imageData != null) {
                            System.out.println("Image récupérée avec succès. Taille : " + imageData.length + " octets.");
                        } else {
                            System.out.println("Aucune image trouvée pour cet utilisateur.");
                        }
                    } else {
                        System.out.println("Utilisateur introuvable.");
                    }
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la récupération de l'image : " + e.getMessage());
            }

            return imageData;
        }

            public void updatePresence(String name, String email, boolean isPresent) {
        String query = "UPDATE utilisateurs SET is_present = ? WHERE nom = ? AND email = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
            pstmt.setBoolean(1, isPresent);
            pstmt.setString(2, name);
            pstmt.setString(3, email);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Présence mise à jour avec succès.");
            } else {
                System.out.println("Échec de la mise à jour de la présence.");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour de la présence : " + e.getMessage());
        }
    }


    }
