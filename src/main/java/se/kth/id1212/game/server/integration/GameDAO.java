package se.kth.id1212.game.server.integration;


import com.mysql.cj.x.protobuf.MysqlxPrepare;
import se.kth.id1212.game.server.model.GameHistory;
import se.kth.id1212.game.server.model.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameDAO {
    private static Connection connection;
    private PreparedStatement createGame;
    private PreparedStatement insertScore;
    private PreparedStatement findOnlinePlayers;
    private PreparedStatement updatePlayerStatus;
    private PreparedStatement findPlayerHistory;

    private static PreparedStatement getAllInfo;

    public GameDAO() {
        try {
            connectToGameDB();
            prepareStatements();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void getAllInfo() {
        try (ResultSet rs = getAllInfo.executeQuery()) {
            while (rs.next()) {
                String name = rs.getString("player_name");
                Timestamp time = rs.getTimestamp("game_time");
                int round = rs.getInt("round");
                int score = rs.getInt("score");
                int gameId = rs.getInt("game_id");
                System.out.println("Name: " + name + ", Time: " + time + ", Round: " + round + ", Score: " + score + ", Game ID: " + gameId);
            }
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
            else{
                System.out.println(name +"," + status);
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
                PreparedStatement insertPlayer = connection.prepareStatement("INSERT INTO Player (name, status) VALUES (?, false)");
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


    public static void deletePlayers() throws SQLException {
        String query = "DELETE FROM player WHERE 1=1;";


        PreparedStatement statement = connection.prepareStatement(query);
        statement.executeUpdate();
        System.out.println("player rows deleted");
    }


    public static void deleteHistory() throws SQLException {
        String query = "DELETE FROM player_game WHERE 1=1;";


        PreparedStatement statement = connection.prepareStatement(query);
        statement.executeUpdate();
        System.out.println("player_game rows deleted");
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
        getAllInfo = connection.prepareStatement("SELECT game.id AS game_id, game.time AS game_time, game.round AS round, player.name AS player_name, player_game.score AS score FROM player_game "+
                "INNER JOIN game ON game.id = player_game.game_id "+
                "INNER JOIN player ON player.id = player_game.player_id "+
                "ORDER by game.id ASC, player_game.score DESC ");




    }


}
