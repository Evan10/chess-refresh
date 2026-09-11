package chess;

public class ChessConstants {

    public static final ChessPiece BK = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
    public static final ChessPiece BQ = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
    public static final ChessPiece BR = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
    public static final ChessPiece BKN = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
    public static final ChessPiece BB = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
    public static final ChessPiece BP = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);

    public static final ChessPiece WK = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
    public static final ChessPiece WQ = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
    public static final ChessPiece WR = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
    public static final ChessPiece WKN = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
    public static final ChessPiece WB = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
    public static final ChessPiece WP = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);

    private static final ChessPiece[][] START_BOARD = {
            {WR  ,WKN ,WB  ,WQ  ,WK  ,WB  ,WKN ,WR  },
            {WP  ,WP  ,WP  ,WP  ,WP  ,WP  ,WP  ,WP  },
            {null,null,null,null,null,null,null,null},
            {null,null,null,null,null,null,null,null},
            {null,null,null,null,null,null,null,null},
            {null,null,null,null,null,null,null,null},
            {BP  ,BP  ,BP  ,BP  ,BP  ,BP  ,BP  ,BP  },
            {BR  ,BKN ,BB  ,BQ  ,BK  ,BB  ,BKN ,BR  }};

    public static ChessPiece[][] getStartBoardDeepCopy(){
        ChessPiece[][] board = new ChessPiece[8][];
        for(int i = 0; i < 8 ; i++){
            board[i] = START_BOARD[i].clone();
        }
        return board;
    }
}
