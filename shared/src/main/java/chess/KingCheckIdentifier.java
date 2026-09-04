package chess;

import java.util.Collection;

public class KingCheckIdentifier {

    public static boolean isKingUnderAttack(ChessBoard board, ChessGame.TeamColor color) throws InvalidGameStateException {
        Collection<ChessPosition> positions = board.findChessPieceLoc(ChessPiece.PieceType.KING, color);
        if (positions.isEmpty()) {
            throw new InvalidGameStateException("No king piece found");
        } else if (positions.size() > 1) {
            throw new InvalidGameStateException("Multiple " + color + " kings");
        }
        ChessPosition kingPosition = positions.stream().findFirst().get();

        return isKingUnderAttackByRookBishopQueen(board, color, kingPosition) ||
                isKingUnderAttackByKnight(board, color, kingPosition) ||
                isKingUnderAttackByPawn(board, color, kingPosition) ||
                isKingUnderAttackByKing(board, color, kingPosition);
    }

    private static ChessPiece findChessPieceInLine(ChessBoard board, ChessPosition start, int xDirection, int yDirection) {
        ChessPosition pos = start;
        for (int i = 0; i < 8; i++) {
            pos = pos.offset(xDirection, yDirection);
            if (!pos.isValid()) break;
            ChessPiece piece = board.getPiece(pos);
            if (piece != null) {
                return piece;
            }
        }
        return null;
    }

    private static boolean isRookOrQueenInPlus(ChessBoard board, ChessPosition start, ChessGame.TeamColor color) {
        int[][] orientations = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] ori : orientations) {
            int x, y;
            x = ori[0];
            y = ori[1];
            ChessPiece piece = findChessPieceInLine(board, start, x, y);
            if (piece != null && (piece.getPieceType() == ChessPiece.PieceType.ROOK ||
                    piece.getPieceType() == ChessPiece.PieceType.QUEEN) &&
                    piece.getTeamColor() != color) {
                return true;
            }
        }
        return false;
    }

    private static boolean isBishopOrQueenInX(ChessBoard board, ChessPosition start, ChessGame.TeamColor color) {
        int[][] orientations = {{-1, -1}, {-1, 1}, {1, 1}, {1, -1}};

        for (int[] ori : orientations) {
            int x, y;
            x = ori[0];
            y = ori[1];
            ChessPiece piece = findChessPieceInLine(board, start, x, y);
            if (piece != null && (piece.getPieceType() == ChessPiece.PieceType.BISHOP ||
                    piece.getPieceType() == ChessPiece.PieceType.QUEEN) &&
                    piece.getTeamColor() != color) {
                return true;
            }
        }
        return false;
    }


    private static boolean isKingUnderAttackByRookBishopQueen(ChessBoard board, ChessGame.TeamColor color, ChessPosition kingPosition) {
        return isRookOrQueenInPlus(board, kingPosition, color) || isBishopOrQueenInX(board, kingPosition, color);
    }


    private static boolean isKingUnderAttackByKnight(ChessBoard board, ChessGame.TeamColor color, ChessPosition kingPosition) {
        int[][] offsets = {
                {-2, 1}, {-2, -1},
                {2, 1}, {2, -1},
                {-1, 2}, {-1, -2},
                {1, 2}, {1, -2}};

        for (int[] os : offsets) {
            ChessPosition pos = kingPosition.offset(os[0], os[1]);
            if (!pos.isValid()) continue;
            ChessPiece piece = board.getPiece(pos);
            if (piece != null
                    && piece.getPieceType() == ChessPiece.PieceType.KNIGHT
                    && piece.getTeamColor() != color) {
                return true;
            }
        }

        return false;
    }

    private static boolean isKingUnderAttackByPawn(ChessBoard board, ChessGame.TeamColor color, ChessPosition kingPosition) {

        int direction = color == ChessGame.TeamColor.WHITE ? 1 : -1;


        return false;
    }

    private static boolean isKingUnderAttackByKing(ChessBoard board, ChessGame.TeamColor color, ChessPosition kingPosition) {

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if(x == 0 && y == 0) continue;
                ChessPosition pos = kingPosition.offset(x,y);
                if(!pos.isValid()) continue;
                ChessPiece piece = board.getPiece(pos);
                if(piece!= null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() != color){
                    return true;
                }
            }
        }
        return false;
    }
}
