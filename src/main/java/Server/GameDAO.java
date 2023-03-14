package Server;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GameDAO {
    private Connection connection;
    private PreparedStatement createGame;
    private PreparedStatement insertScore;

    public GameDAO() {
        try {
            connectToGameDB();
            prepareStatements();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPlayer(String playerName) {
        try {
            // Check if player already exists
            PreparedStatement checkPlayer = connection.prepareStatement("SELECT * FROM player WHERE name = ?");
            checkPlayer.setString(1, playerName);
            ResultSet rs = checkPlayer.executeQuery();

            // If player does not exist, insert new player
            if (!rs.next()) {
                PreparedStatement insertPlayer = connection.prepareStatement("INSERT INTO player (name) VALUES (?)");
                insertPlayer.setString(1, playerName);
                insertPlayer.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int saveGame(int rounds) {
        ResultSet rs = null;
        int gameId = 0;
        try {
            createGame.setInt(1, rounds);
            createGame.executeUpdate();
            rs = createGame.getGeneratedKeys();
            if(rs != null && rs.next()){
                gameId = rs.getInt(1);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return gameId;
    }

    public void updatePlayerGame(String playerName, int gameId, int point) {
        try {
            PreparedStatement checkPlayer = connection.prepareStatement("SELECT id FROM player WHERE name = ?");
            checkPlayer.setString(1, playerName);
            ResultSet rs = checkPlayer.executeQuery();
            if (rs.next()) {
                int playerId = rs.getInt("id");
                insertScore.setInt(1, playerId);
                insertScore.setInt(2, gameId);
                insertScore.setInt(3, point);
                insertScore.executeUpdate();
            } else {
                System.out.println("Player does not exist with given name.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void connectToGameDB() throws SQLException{
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/game","postgres","postgres");
    }

    private void prepareStatements() throws SQLException {
        createGame = connection.prepareStatement("insert into game(time, round) values (now(), ?)",
                createGame.RETURN_GENERATED_KEYS);
        insertScore = connection.prepareStatement("insert into player_game(player_id, game_id, score) values (?, ?, ?)");
    }
}
