package com.example.trylma2;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class BoardView {
    private static final int TILE_SIZE = 30;
    private static final int BOARD_SIZE = 17;

    public AnchorPane createBoardPane(String type) {
        int[][] board = initializeBoard(type);

        GridPane grid = createGrid(board);
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(grid);

        AnchorPane.setTopAnchor(grid, 10.0);
        AnchorPane.setLeftAnchor(grid, 10.0);

        return anchorPane;
    }

    public static GridPane createGrid(int[][] board) {
        GridPane grid = new GridPane();

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Rectangle cell = new Rectangle(TILE_SIZE, TILE_SIZE);
                cell.setStroke(Color.BLACK);

                if (board[i][j] == -1) {
                    cell.setFill(Color.LIGHTGRAY);
                } else if (board[i][j] == 0) {
                    cell.setFill(Color.WHITE);
                } else {
                    cell.setFill(getPlayerColor(board[i][j]));
                }

                grid.add(cell, j, i);
            }
        }

        return grid;
    }

    public int[][] initializeBoard(String type) {
        int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = -1;
            }
        }

        for (int i = 4; i <= 8; i++) {
            for (int j = 0; j < i + 1; j++) {
                board[i][j + 4] = 0;
            }
        }
        for (int i = 12; i >= 9; i--) {
            for (int j = 0; j < 12 - i + 5; j++) {
                board[i][i - 4 + j] = 0;
            }
        }

        PlayerA(board, 1, 0, 4);
        switch (type) {
            case "2": {
                PlayerB(board, 0, 7, 12);
                PlayerA(board, 0, 9, 13);
                PlayerB(board, 2, 16, 12);
                PlayerA(board, 0, 9, 4);
                PlayerB(board, 0, 7, 3);
                break;
            }
            case "3": {
                PlayerB(board, 0, 7, 12);
                PlayerA(board, 2, 9, 13);
                PlayerB(board, 0, 16, 12);
                PlayerA(board, 3, 9, 4);
                PlayerB(board, 0, 7, 3);
                break;
            }
            case "4": {
                PlayerB(board, 2, 7, 12);
                PlayerA(board, 0, 9, 13);
                PlayerB(board, 3, 16, 12);
                PlayerA(board, 4, 9, 4);
                PlayerB(board, 0, 7, 3);
                break;
            }
            case "6": {
                PlayerB(board, 2, 7, 12);
                PlayerA(board, 3, 9, 13);
                PlayerB(board, 4, 16, 12);
                PlayerA(board, 5, 9, 4);
                PlayerB(board, 6, 7, 3);
                break;
            }
            case "YinYang": {
                PlayerB(board, 2, 7, 12);
                PlayerA(board, 0, 9, 13);
                PlayerB(board, 0, 16, 12);
                PlayerA(board, 0, 9, 4);
                PlayerB(board, 0, 7, 3);
                break;
            }
        }
        return board;
    }

    private static Color getPlayerColor(int player) {
        switch (player) {
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

    private void PlayerA(int[][] board, int z, int a, int b) {
        for (int i = 0; i < 4; i++) {
            for (int j = b; j <= b + i; j++) {
                board[a + i][j] = z;
            }
        }
    }

    private void PlayerB(int[][] board, int z, int a, int b) {
        for (int i = 0; i < 4; i++) {
            for (int j = b; j >= b - i; j--) {
                board[a - i][j] = z;
            }
        }
    }

    public int[][] CheckWinnerBoard(String type) {
        int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
        switch (type) {
            case "2": {
                PlayerA(board, 2, 9, 4);
                PlayerB(board, 1, 16, 12);
                break;
            }
            case "3": {
                PlayerB(board, 3, 7, 12);
                PlayerB(board, 1, 16, 12);
                PlayerB(board, 2, 7, 3);
                break;
            }
            case "4": {
                PlayerA(board, 3, 0, 4);
                PlayerB(board, 4, 7, 12);
                PlayerB(board, 1, 16, 12);
                PlayerA(board, 2, 9, 4);
                break;
            }
            case "6": {
                PlayerA(board, 4, 0, 4);
                PlayerB(board, 5, 7, 12);
                PlayerA(board, 6, 9, 13);
                PlayerB(board, 1, 16, 12);
                PlayerA(board, 2, 9, 4);
                PlayerB(board, 3, 7, 3);
                break;
            }
            case "YinYang": {
                PlayerA(board, 2, 0, 4);
                PlayerB(board, 1, 16, 12);
                break;
            }
        }
        return board;
    }
}
