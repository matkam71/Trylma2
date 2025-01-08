package com.example.trylma2;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class Server {
    private static final int PORT = 12345;
    public static final List<Player> players = new ArrayList<>();
    private static int currentTurn = 0;

    public static void startServer(int playerCount) {
        out.println("Serwer uruchomiony dla " + playerCount + " graczy.");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (players.size() < playerCount) {
                Socket socket = serverSocket.accept();
                Player player = new Player(socket, players.size() + 1);
                players.add(player);
                out.println("Połączono graczy: " + players.size() + "/" + playerCount);
                player.out.println("Witaj w grze! Twój numer Gracza to: " + player.id);
                if (players.size() < playerCount) {
                    player.out.println("Oczekiwanie na pozostałych graczy...");
                }
            }

            out.println("Wszyscy gracze połączeni. Gra się rozpoczyna!");
            for (Player player : players) {
                player.out.println("Wszyscy gracze połączeni. Gra się rozpoczyna!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Player {
        Socket socket;
        BufferedReader in;
        PrintWriter out;
        int id;
        Player(Socket socket, int id) throws IOException {
            this.socket = socket;
            this.id = id;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        }
    }

}
