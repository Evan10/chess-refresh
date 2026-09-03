package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {


    /**
     * translation from Chess Position to array location
     * row: 1, col:1 (bottom left) => board[0][0]
     * row: 1, col:8 (bottom right) => board[0][7]
     * row: 8, col:1 (top left) => board[7][0]
     * etc
     */
    private ChessPiece[][] board;

    public ChessBoard() {
        board = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int i,j;
        i = position.getRow() - 1;
        j = position.getColumn() - 1;
        board[i][j] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int i,j;
        i = position.getRow() - 1;
        j = position.getColumn() - 1;
        return board[i][j];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = ChessConstants.START_BOARD.clone();
    }

    @Override
    public String toString() {
        return "B{" + Arrays.toString(board) +"}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
