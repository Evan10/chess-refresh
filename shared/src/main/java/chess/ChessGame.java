package chess;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor turn;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        turn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    public boolean isTeamTurn(TeamColor color){ return color == turn;}
    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    public void toggleTurn(){
        setTeamTurn(turn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null){
            return List.of();
        }
        Collection<ChessMove> moves = piece.pieceMoves(board,startPosition);
        ChessBoard moveTester = new ChessBoard();
        board.mirrorTo(moveTester);

        for(ChessMove move: moves){
            ChessPiece.PieceType p = piece.getPieceType();
            if(move.getPromotionPiece() != null){
                p = move.getPromotionPiece();
            }
            ChessPiece endPiece = new ChessPiece(piece.getTeamColor(),p);
            moveTester.addPiece(startPosition,null);
            moveTester.addPiece(move.getEndPosition(),endPiece);

            if(KingCheckIdentifier.isKingUnderAttack(moveTester,piece.getTeamColor())){
                moves.remove(move);
            }
            board.mirrorTo(moveTester);
        }

        return board.getPiece(startPosition).pieceMoves(board,startPosition);
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null){
            throw new InvalidMoveException("No piece at start position");
        }
        if( piece.getTeamColor() != turn){
            throw new InvalidMoveException("It it not this players turn");
        }
        Collection<ChessMove> moves = validMoves(move.getStartPosition());
        if(!moves.contains(move)){
            throw new InvalidMoveException("Invalid move");
        }

        ChessPiece.PieceType p = piece.getPieceType();
        if(move.getPromotionPiece() != null){
            p = move.getPromotionPiece();
        }
        ChessPiece endPiece = new ChessPiece(piece.getTeamColor(),p);

        board.addPiece(move.getStartPosition(),null);
        board.addPiece(move.getEndPosition(),endPiece);
        toggleTurn();
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return KingCheckIdentifier.isKingUnderAttack(board,teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && !hasValidMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && isTeamTurn(teamColor) && !hasValidMoves(teamColor);
    }


    public boolean hasValidMoves(TeamColor teamColor){
        Collection<ChessPosition> teamPositions = board.findChessPieceLoc(null, teamColor);
        for( ChessPosition pos : teamPositions){
            ChessPiece piece = board.getPiece(pos);
            if(piece != null && !piece.pieceMoves(board,pos).isEmpty()){
                return true;
            }
        }
        return false;
    }
    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public String toString() {
        return "G{" + board +
                "\n, turn=" + turn +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && turn == chessGame.turn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, turn);
    }
}
