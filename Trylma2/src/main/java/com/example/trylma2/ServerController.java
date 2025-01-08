package com.example.trylma2;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.Random;
import static com.example.trylma2.Server.players;

/**
 * Kontroler obsługujący logikę gry dla serwera Trylmy.
 */
public class ServerController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Label activePlayerLabel;

    @FXML
    private Rectangle playerColorBox;

    @FXML
    public TextField moveInputField;

    @FXML
    private Button submitMoveButton;

    @FXML
    private Button passTurnButton;

    @FXML
    private Button checkWinnerButton;

    /**
     * Identyfikator obecnie aktywnego gracza.
     */
    public int currentActivePlayer = 1;

    /**
     * Plansza gry, reprezentowana jako tablica dwuwymiarowa.
     */
    public int[][] board;

    /**
     * Tablica pomocnicza do sprawdzania warunków zwycięstwa.
     */
    public int[][] check;

    /**
     * Tablica przechowująca identyfikatory zwycięzców.
     */
    int[] winners;

    /**
     * Indeks określający miejsce w rankingu zwycięzców.
     */
    int place = 1;

    /**
     * Flaga określająca, czy gra odbywa się w trybie szybkiej rozgrywki.
     */
    public boolean szybkie = false;

    /**
     * Metoda inicjalizująca komponenty po załadowaniu widoku FXML.
     */
    @FXML
    public void initialize() {
        if (rootPane == null) {
            System.err.println("rootPane is not initialized!");
        }
    }

    /**
     * Obsługuje wybór liczby graczy z menu kontekstowego.
     *
     * @param event Zdarzenie wyboru opcji.
     */
    @FXML
    public void onPlayerCountSelected(javafx.event.ActionEvent event) {
        MenuItem selectedMenuItem = (MenuItem) event.getSource();
        int playernumbers = 2;
        try {
            playernumbers = Integer.parseInt(selectedMenuItem.getText());
        } catch (NumberFormatException e) {
        }
        Random random = new Random();
        int playerCount = playernumbers;
        currentActivePlayer = random.nextInt(playerCount) + 1;
        Platform.runLater(() -> updateActivePlayer(currentActivePlayer));
        new Thread(() -> {
            Server.startServer(playerCount);
        }).start();

        String type = selectedMenuItem.getText();
        BoardView boardView = new BoardView();
        this.board = boardView.initializeBoard(type);
        this.check = boardView.CheckWinnerBoard(type);
        AnchorPane boardPane = boardView.createBoardPane(type);
        winners = new int[playerCount + 1];
        rootPane.getChildren().clear();
        rootPane.getChildren().addAll(boardPane, activePlayerLabel, playerColorBox, moveInputField, submitMoveButton, passTurnButton, checkWinnerButton);
    }

    /**
     * Obsługuje zdarzenie wysłania ruchu przez gracza.
     */
    @FXML
    public void onSubmitMove() {
        String move = moveInputField.getText();
        boolean validate;
        if (szybkie)
            validate = validateSpeedMove(move);
        else
            validate = validateStandardMove(move);

        if (validate) {
            System.out.println("Ruch: " + move + " przesłany.");
            for (Server.Player player : players) {
                player.out.println("Gracz " + currentActivePlayer + " wykonał ruch " + move);
            }
            String[] parts = move.split(" ");
            int x1 = Integer.parseInt(parts[0]);
            int y1 = Integer.parseInt(parts[1]);
            int x2 = Integer.parseInt(parts[2]);
            int y2 = Integer.parseInt(parts[3]);

            board[x1][y1] = 0;
            board[x2][y2] = currentActivePlayer;

            Platform.runLater(() -> {
                GridPane boardPane = BoardView.createGrid(board);
                moveInputField.clear();
                rootPane.getChildren().clear();
                rootPane.getChildren().addAll(boardPane, activePlayerLabel, playerColorBox, moveInputField, submitMoveButton, passTurnButton, checkWinnerButton);
                if (!jump) {
                    updateToNextActivePlayer();
                    jump = false;
                }
            });

        } else {
            System.err.println("Nieprawidłowy ruch.");
        }
    }

    /**
     * Flaga określająca, czy gracz wykonuje skok.
     */
    boolean jump = false;

    /**
     * Współrzędne X i Y początkowe skoku.
     */
    int jumpX, jumpY;

    /**
     * Waliduje ruch w standardowym trybie gry.
     *
     * @param move Ruch w formacie "x1 y1 x2 y2".
     * @return True, jeśli ruch jest poprawny; false w przeciwnym razie.
     */
    public boolean validateStandardMove(String move) {
        if (!move.matches("\\d+ \\d+ \\d+ \\d+"))
            return false;
        String[] parts = move.split(" ");
        int x1 = Integer.parseInt(parts[0]);
        int y1 = Integer.parseInt(parts[1]);
        int x2 = Integer.parseInt(parts[2]);
        int y2 = Integer.parseInt(parts[3]);
        if (jump && !(x1 == jumpX && y1 == jumpY))
            return false;
        if (jump && !((x2 - x1 == 2) || (x1 - x2 == 2) || (y2 - y1 == 2) || (y1 - y2 == 2)))
            return false;
        if (board[x1][y1] != currentActivePlayer)
            return false;
        if (board[x2][y2] != 0)
            return false;
        if ((y2 - y1 > 2 || y2 - y1 < -2 || x2 - x1 > 2 || x2 - x1 < -2))
            return false;
        if (x2 == x1) {
            if (!((y1 - y2 == 1) || (y2 - y1 == 1))) {
                if (!((y2 - y1 == 2 && board[x1][y1 + 1] > 0) || (y1 - y2 == 2 && board[x1][y1 - 1] > 0)))
                    return false;
                jump = true;
                jumpX = x2;
                jumpY = y2;
            }
        } else if (x2 - x1 == 2) {
            if (!((y2 - y1 == 2 && board[x1 + 1][y1 + 1] > 0) || (y1 == y2 && board[x1 + 1][y1] > 0)))
                return false;
            jump = true;
            jumpX = x2;
            jumpY = y2;
        } else if (x1 - x2 == 2) {
            if (!((y1 - y2 == 2 && board[x1 - 1][y1 - 1] > 0) || (y1 == y2 && board[x1 - 1][y1] > 0)))
                return false;
            jump = true;
            jumpX = x2;
            jumpY = y2;
        }
        if (x2 > x1 && y2 < y1)
            return false;
        if (x2 < x1 && y2 > y1)
            return false;
        if (board[x1][y1] == check[x1][y1] && check[x1][y1] != check[x2][y2])
            return false;
        return true;
    }

    /**
     * Waliduje ruch w szybkim trybie gry.
     *
     * @param move Ruch w formacie "x1 y1 x2 y2".
     * @return True, jeśli ruch jest poprawny; false w przeciwnym razie.
     */
    public boolean validateSpeedMove(String move) {
        if (!move.matches("\\d+ \\d+ \\d+ \\d+"))
            return false;
        String[] parts = move.split(" ");
        int x1 = Integer.parseInt(parts[0]);
        int y1 = Integer.parseInt(parts[1]);
        int x2 = Integer.parseInt(parts[2]);
        int y2 = Integer.parseInt(parts[3]);
        if (jump && !(x1 == jumpX && y1 == jumpY))
            return false;
        if (jump && !((x2 - x1 >= 2) || (x1 - x2 >= 2) || (y2 - y1 >= 2) || (y1 - y2 >= 2)))
            return false;
        if (board[x1][y1] != currentActivePlayer)
            return false;
        if (board[x2][y2] != 0)
            return false;
        if ((Math.abs(y2 - y1) > 2 && Math.abs(y2 - y1) % 2 == 1) || (Math.abs(x2 - x1) > 2 && Math.abs(x2 - x1) % 2 == 1))
            return false;

        if (x2 == x1 && Math.abs(y2 - y1) >= 2) {
            if (board[x1][(y1 + y2) / 2] <= 0)
                return false;
            int ym;
            if (y1 > y2)
                ym = y2;
            else
                ym = y1;
            for (int i = ym; i < y2 + y1 - ym - 1; i++) {
                if (board[x1][1 + i] != 0 && (1 + i) != ((y1 + y2) / 2))
                    return false;
            }
            jump = true;
            jumpX = x2;
            jumpY = y2;
        } else if (x2 - x1 >= 2) {
            if (y1 == y2) {
                if (board[(x1 + x2) / 2][y1] <= 0)
                    return false;
                for (int i = x1; i < x2 - 1; i++) {
                    if (board[1 + i][y1] != 0 && (1 + i) != ((x1 + x2) / 2))
                        return false;
                }
            } else if (y2 - y1 == x2 - x1) {
                if (board[(x1 + x2) / 2][(y1 + y2) / 2] <= 0)
                    return false;
                for (int i = 1; i < x2 - x1; i++) {
                    if (board[x1 + i][y1 + i] != 0 && (x1 + i) != ((x1 + x2) / 2))
                        return false;
                }
            } else
                return false;
            jump = true;
            jumpX = x2;
            jumpY = y2;
        } else if (x1 - x2 >= 2) {
            if (y1 == y2) {
                if (board[(x1 + x2) / 2][y1] <= 0)
                    return false;
                for (int i = x2; i < x1 - 1; i++) {
                    if (board[1 + i][y1] != 0 && (1 + i) != ((x1 + x2) / 2))
                        return false;
                }
            } else if (y1 - y2 == x1 - x2) {
                if (board[(x1 + x2) / 2][(y1 + y2) / 2] <= 0)
                    return false;
                for (int i = 1; i < x1 - x2; i++) {
                    if (board[x2 + i][y2 + i] != 0 && (x2 + i) != ((x1 + x2) / 2))
                        return false;
                }
            } else
                return false;
            jump = true;
            jumpX = x2;
            jumpY = y2;
        }

        if (x2 > x1 && y2 < y1)
            return false;
        if (x2 < x1 && y2 > y1)
            return false;
        if (board[x1][y1] == check[x1][y1] && check[x1][y1] != check[x2][y2])
            return false;
        return true;
    }

    /**
     * Aktualizuje interfejs użytkownika, aby wskazywał aktualnie aktywnego gracza.
     *
     * @param playerId Identyfikator gracza.
     */
    private void updateActivePlayer(int playerId) {
        currentActivePlayer = playerId;
        activePlayerLabel.setText("Gracz: " + playerId);
        playerColorBox.setFill(getPlayerColor(playerId));
    }

    /**
     * Zwraca kolor przypisany do danego gracza na podstawie jego identyfikatora.
     *
     * @param playerId identyfikator gracza (od 1 do 6).
     * @return kolor odpowiadający graczowi; czarny, jeśli identyfikator nie mieści się w zakresie.
     */
    private Color getPlayerColor(int playerId) {
        switch (playerId) {
            case 1:
                return Color.RED;
            case 2:
                return Color.BLUE;
            case 3:
                return Color.GREEN;
            case 4:
                return Color.YELLOW;
            case 5:
                return Color.PURPLE;
            case 6:
                return Color.ORANGE;
            default:
                return Color.BLACK;
        }
    }

    /**
     * Obsługuje zakończenie tury przez aktualnie aktywnego gracza.
     * Wyświetla komunikat o rezygnacji z ruchu i zmienia aktywnego gracza na następnego.
     */
    public void onPassTurn() {
        System.out.println("Gracz " + currentActivePlayer + " rezygnuje z ruchu.");
        for (Server.Player player : players) {
            player.out.println("Gracz " + currentActivePlayer + " rezygnuje z ruchu.");
        }
        updateToNextActivePlayer();
        jump = false;
    }

    /**
     * Sprawdza, czy aktualny gracz spełnił warunki wygranej.
     * Jeśli tak, przypisuje mu miejsce w klasyfikacji końcowej i zmienia aktywnego gracza.
     * Jeśli wszyscy gracze zakończyli grę, aplikacja zostaje zamknięta.
     *
     * @throws InterruptedException jeśli wystąpi problem z obsługą wątku (np. w czasie oczekiwania na zakończenie gry).
     */
    @FXML
    public void onCheckWinner() throws InterruptedException {
        int count = 0;
        for (int i = 0; i < 17; i++) {
            for (int j = 0; j < 17; j++) {
                if (board[i][j] == check[i][j] && check[i][j] == currentActivePlayer) {
                    count++;
                }
            }
        }
        if (count == 10) {
            System.out.println("Gracz " + currentActivePlayer + " zajął " + place + " miejsce");
            place++;
            if (place == winners.length - 1) {
                System.out.println("Gra się skończyła!");
                Platform.exit();
                Thread.sleep(2000);
            }
            winners[currentActivePlayer]++;
            updateToNextActivePlayer();
        } else {
            System.out.println("Jeszcze nie wygrałeś!");
        }
    }

    /**
     * Aktualizuje identyfikator aktywnego gracza na następnego, pomijając graczy, którzy zakończyli grę.
     */
    private void updateToNextActivePlayer() {
        do {
            currentActivePlayer = (currentActivePlayer % players.size()) + 1;
        } while (winners[currentActivePlayer] == 1);
        updateActivePlayer(currentActivePlayer);
    }

    /**
     * Aktywuje tryb szybkiej gry, ustawiając flagę `szybkie` na true.
     */
    public void szybkie() {
        szybkie = true;
    }

}