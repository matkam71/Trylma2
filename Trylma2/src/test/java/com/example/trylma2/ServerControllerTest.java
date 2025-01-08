package com.example.trylma2;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ServerControllerTest {

    private ServerController controller;

    @BeforeEach
    void setUp() {
        controller = new ServerController();
        BoardView boardView = new BoardView();
        controller.board = boardView.initializeBoard("2");
        controller.check = boardView.CheckWinnerBoard("2");
        controller.currentActivePlayer = 1;
        controller.jump = false;
    }

    @Test
    void testValidateStandardMove_Jump() {
        controller.board[3][5] = 3;
        controller.board[4][6] = 0;

        String move = "2 4 4 6";
        boolean result = controller.validateStandardMove(move);

        assertTrue(result, "Prawidłowy ruch powinien zostać zaakceptowany");
    }

    @Test
    void testValidateStandardMove_OutOfBounds() {
        String move = "0 4 0 5";
        boolean result = controller.validateStandardMove(move);

        assertFalse(result, "Ruch poza granicami planszy nie powinien być zaakceptowany");
    }

    @Test
    void testValidateStandardMove_OccupiedTarget() {
        controller.board[4][4] = 2;

        String move = "3 4 4 4";
        boolean result = controller.validateStandardMove(move);

        assertFalse(result, "Ruch na zajęte pole nie powinien być zaakceptowany");
    }

    @Test
    void testValidateStandardMove_OtherPlayer() {
        controller.currentActivePlayer  = 2;
        String move = "3 4 4 4";
        boolean result = controller.validateStandardMove(move);

        assertFalse(result, "Gracz nie powinien móc przesuwać pionków innego gracza");
    }

    @Test
    void testValidateSpeedMove_SpeedJump() {
        controller.board[2][4] = 1;
        controller.board[3][5] = 0;
        controller.board[4][6] = 1;
        controller.board[5][7] = 0;
        controller.board[6][8] = 0;
        String move = "2 4 6 8";
        boolean result = controller.validateSpeedMove(move);

        assertTrue(result, "Prawidłowy ruch w trybie szybkim powinien zostać zaakceptowany");
    }

    @Test
    void testValidateSpeedMove_TooFar() {
        controller.board[3][4] = 1;
        controller.board[6][7] = 0;

        String move = "3 4 6 7";
        boolean result = controller.validateSpeedMove(move);

        assertFalse(result, "Ruch za daleko nie powinien być zaakceptowany w trybie szybkim");
    }

    @Test
    void testValidateStandardMove_SameField() {
        String move = "3 4 3 4";
        boolean result = controller.validateStandardMove(move);
        assertFalse(result, "Ruch na to samo pole nie powinien być zaakceptowany");
    }

    @Test
    void testValidateStandardMove_DownLeft() {
        controller.board[5][5] = 1;
        String move = "5 5 6 4";
        boolean result = controller.validateStandardMove(move);
        assertFalse(result, "Ruch w nieodpowiednią stronę nie powinien być zaakceptowany");
    }

    @Test
    void testValidateStandardMove_UpRight() {
        controller.board[5][5] = 1;
        String move = "5 5 4 6";
        boolean result = controller.validateStandardMove(move);
        assertFalse(result, "Ruch w nieodpowiednią stronę nie powinien być zaakceptowany");
    }

    @Test
    void testValidateStandardMove_invalidMove() {
        controller.board[5][5] = 1;
        String Shortmove = "5 5 4";
        String invalidMove = "a 1 2 3";
        boolean result1 = controller.validateStandardMove(Shortmove);
        boolean result2 = controller.validateStandardMove(invalidMove);
        assertFalse(result1, "Ruch w złym formacie nie powinien być zaakceptowany");
        assertFalse(result2, "Ruch w złym formacie nie powinien być zaakceptowany");
    }

    @Test
    void testValidateStandardMove_jumpTest() {
        controller.jump = true;
        controller.jumpX = 5;
        controller.jumpY = 4;
        controller.board[5][5] = 1;
        controller.board[5][4] = 1;
        String move = "5 5 6 6";
        boolean result = controller.validateStandardMove(move);
        assertFalse(result, "Po skoku trzeba wykonać skok tym samym pionem");
    }

    @Test
    void testValidateStandardMove() {
        String move = "3 4 4 4";
        boolean result = controller.validateStandardMove(move);
        assertTrue(result, "Prawidłowy ruch powinien zostać zaakceptowany");
    }
}
