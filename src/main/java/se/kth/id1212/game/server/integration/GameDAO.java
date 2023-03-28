package se.kth.id1212.game.server.integration;


import com.mysql.cj.x.protobuf.MysqlxPrepare;
import se.kth.id1212.game.server.model.GameHistory;
import se.kth.id1212.game.server.model.Player;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GameDAO {
    private Connection connection;
    private PreparedStatement createGame;
    private PreparedStatement insertScore;
    private PreparedStatement findOnlinePlayers;
    private PreparedStatement updatePlayerStatus;
    private PreparedStatement findPlayerHistory;

    public GameDAO() {
        try {
            connectToGameDB();
            prepareStatements();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int updateStatus(String name, boolean status) {
        int updatedRows = -1;
        try {
            updatePlayerStatus.setBoolean(1,status);
            updatePlayerStatus.setString(2,name);
            updatedRows = updatePlayerStatus.executeUpdate();
            if (updatedRows == -1) {
                System.out.println("Couldn't update player status.");
            }
            else {
                System.out.println(name+","+status);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return updatedRows;
    }

    public void login(String playerName) {
        int updatedRows = -1;
        try {
            updatedRows = updateStatus(playerName, true);
            if (updatedRows != 1) {
                PreparedStatement insertPlayer = connection.prepareStatement("INSERT INTO Player (name, status) VALUES (?, true)");
                insertPlayer.setString(1, playerName);
                insertPlayer.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Player> findAllOnlinePlayers() {
        List<Player> players = new ArrayList<>();
        try (ResultSet rs = findOnlinePlayers.executeQuery()) {
            while (rs.next()) {
                players.add(new Player(rs.getString("id"),
                        rs.getString("name"),
                        rs.getBoolean("status")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return players;
    }

    public int saveGame(int rounds) {
        ResultSet rs = null;
        int gameId = 0;
        try {
            createGame.setInt(1, rounds);
            createGame.executeUpdate();
            rs = createGame.getGeneratedKeys();
            if (rs != null && rs.next()) {
                gameId = rs.getInt(1);
            }
        } catch (SQLException e) {
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

    public List<GameHistory> findHistory(String clientName) {
        List<GameHistory> gameHistory = new ArrayList<>();
        try {
            findPlayerHistory.setString(1, clientName);
            ResultSet rs = findPlayerHistory.executeQuery();
            while (rs.next()) {
                gameHistory.add(new GameHistory(rs.getString("game_id"),
                        rs.getTimestamp("time"),
                        rs.getInt("round"),
                        rs.getString("name"),
                        rs.getInt("score")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return gameHistory;

    }

    private void connectToGameDB() throws SQLException {
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/game", "postgres", "postgres");
    }

    private void prepareStatements() throws SQLException {
        createGame = connection.prepareStatement("insert into game(time, round) values (now(), ?)",
                createGame.RETURN_GENERATED_KEYS);
        insertScore = connection.prepareStatement("insert into player_game(player_id, game_id, score) values (?, ?, ?)");
        findOnlinePlayers = connection.prepareStatement("select * from player where status = true");
        updatePlayerStatus = connection.prepareStatement("UPDATE player SET status = ? where name = ?");
        findPlayerHistory = connection.prepareStatement("select game_id, time, round, name, score from player_game " +
                "inner join game on game.id = game_id " +
                "inner join player on player.id = player_id " +
                "where player.name = ? " +
                "order by player_game.score DESC;");
    }


}
