package me.matthewe.suggestion.database;

import me.matthewe.suggestion.Suggestion;
import me.matthewe.suggestion.SuggestionBot;
import me.matthewe.suggestion.SuggestionStatus;
import me.matthewe.suggestion.config.Config;

import me.matthewe.suggestion.handler.Handler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Matthew E on 12/10/2023 at 12:03 PM for the project TicketBot
 */
public class DatabaseHandler  extends Handler {
    private Connection connection;

    public DatabaseHandler(SuggestionBot suggestionBot, Config config) {
        super(suggestionBot, config);
        this.shutdownPriority = 5;
    }

    @Override
    public void onEnable() {

        this.startConnection();

    }

    private void startConnection() {
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://" + this.config.database.host.address + ":" + this.config.database.host.port + "/" + this.config.database.database, this.config.database.auth.username, this.config.database.auth.password);
        } catch (SQLException e) {
            e.printStackTrace();
            this.suggestionBot.shutdown();
        } finally {
            this.suggestionBot.getLogger().info("[DatabaseHandler] MySQL Connected!");
        }
        this.createTablesIfNotExist();
    }
/*
    private UUID id;
    private long messageId;
    private long user;
    private String description;
    private SuggestionStatus status;

 */
    private void createTablesIfNotExist() {
        String ticketsTable = "CREATE TABLE IF NOT EXISTS suggestions (\n" +
                "    id INT,\n" +
                "    userId BIGINT,\n" +
                "    messageId BIGINT,\n" +
                "    status VARCHAR(255),\n" +
                "    description TEXT" +
                ");";

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(ticketsTable);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.suggestionBot.getLogger().info("[DatabaseHandler] Created tables");
        }

    }

    @Override
    public void onDisable() {

    }

    public void deleteSuggestionFromDatabase(Suggestion suggestion) {
        String sql = "DELETE FROM suggestions WHERE id = ?;";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, suggestion.getId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Suggestion with ID " + suggestion.getId() + " was deleted successfully.");
            } else {
                System.out.println("No suggestion found with ID " + suggestion.getId());
            }
        } catch (SQLException e) {
            System.err.println("Error deleting suggestion: " + e.getMessage());
            e.printStackTrace();
        }

    }

//
    public void updateSuggestionInDatabase(Suggestion suggestion) {
        String sql = "UPDATE suggestions SET " +
                "userId = ?, " +
                "messageId = ?, " +
                "status = ?, " +
                "description = ?" +

                "WHERE id = ?;";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, suggestion.getUser());
            pstmt.setLong(2, suggestion.getMessageId());
            pstmt.setString(3, suggestion.getStatus().toString());
            pstmt.setString(4, suggestion.getDescription()); // Assuming ticketStatus is an enum
            pstmt.setInt(5, suggestion.getId()); // Assuming ticketStatus is an enum

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Suggestion with ID " + suggestion.getId() + " was updated successfully.");
            } else {
                System.out.println("No suggestion found with ID " + suggestion.getId() + " or no update was necessary.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating suggestion: " + e.getMessage());
            // Handle exception or rethrow it as needed
        }
    }


//

    /*
            String ticketsTable = "CREATE TABLE IF NOT EXISTS suggestions (\n" +
                "    id VARCHAR(255) PRIMARY KEY,\n" +
                "    userId BIGINT,\n" +
                "    messageId BIGINT,\n" +
                "    status VARCHAR(255),\n" +
                "    description TEXT\n" +
                ");";
     */
    public List<Suggestion> downloadSuggestionsFromDatabase() {
        List<Suggestion> tickets = new ArrayList<>();
        String sql = "SELECT * FROM suggestions;";

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Suggestion ticket = new Suggestion();
                ticket.setId(rs.getInt("id"));
                ticket.setUser(rs.getLong("userId"));
                ticket.setMessageId(rs.getLong("messageId"));
                ticket.setStatus(SuggestionStatus.valueOf(rs.getString("status")));
                ticket.setDescription(rs.getString("description"));


                tickets.add(ticket);
            }
        } catch (SQLException e) {
            System.err.println("Error downloading suggestions: " + e.getMessage());
            // Handle exception or rethrow it as needed
        }

        return tickets;
    }
//
/*
          "    id VARCHAR(255) PRIMARY KEY,\n" +
                "    userId BIGINT,\n" +
                "    messageId BIGINT,\n" +
                "    status VARCHAR(255),\n" +
                "    description TEXT\n" +
                ");";
 */
    public void uploadAllSuggestionsToDatabase(List<Suggestion> tickets) {
        String sql = "UPDATE suggestions SET " +
                "userId = ?, " +
                "messageId = ?, " +
                "status = ?, " +
                "description = ? " +

                "WHERE id = ?;";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (Suggestion ticket : tickets) {
                pstmt.setLong(1, ticket.getUser());
                pstmt.setLong(2, ticket.getMessageId());
                pstmt.setString(3, ticket.getStatus().toString());
                pstmt.setString(4, ticket.getDescription()); // Assuming ticketStatus is an enum
                pstmt.setInt(5, ticket.getId()); // Assuming ticketStatus is an enum


                pstmt.addBatch();
            }
            pstmt.executeBatch();
            System.out.println("All suggestions updated successfully.");
        } catch (SQLException e) {
            System.err.println("Error updating suggestions: " + e.getMessage());
            // Handle exception or rethrow it as needed
        }
    }
/*
        String sql = "UPDATE suggestions SET " +
                "userId = ?, " +
                "messageId = ?, " +
                "status = ?, " +
                "description = ? " +

                "WHERE id = ?;";
 */
    public void createSuggestionDatabase(Suggestion ticket) {
        String sql = "INSERT INTO suggestions (id, userId, messageId, status, description) VALUES (?, ?, ?, ?, ?);";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, ticket.getId());
            pstmt.setLong(2, ticket.getUser());
            pstmt.setLong(3, ticket.getMessageId());
            pstmt.setString(4, ticket.getStatus().toString());
            pstmt.setString(5, ticket.getDescription());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {

                System.out.println("New suggestion created successfully.");
            } else {
                System.out.println("No new suggestion was created.");
            }
        } catch (SQLException e) {
            System.err.println("Error creating a new suggestion: " + e.getMessage());
            // Handle exception or rethrow it as needed
        }
    }
}
