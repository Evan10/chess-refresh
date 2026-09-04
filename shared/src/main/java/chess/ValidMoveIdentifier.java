package chess;

import com.sun.source.tree.WhileLoopTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ValidMoveIdentifier {

    public static Collection<ChessMove> findValidMoves(ChessBoard board, ChessPosition position, ChessPiece piece){

        return switch (piece.getPieceType()){
            case KING -> findKingMoves(board,position,piece);
            case QUEEN -> findQueenMoves(board,position,piece);
            case ROOK -> findRookMoves(board,position,piece);
            case BISHOP -> findBishopMoves(board,position,piece);
            case KNIGHT -> findKnightMoves(board,position,piece);
            case PAWN -> findPawnMoves(board,position,piece);
        };

    }


    private static Collection<ChessMove> findMovesInLine(ChessBoard board, ChessPosition position, ChessPiece piece, int xDirection, int yDirection){
        ChessGame.TeamColor c = piece.getTeamColor();
        boolean cont = true;
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        ChessPosition next = position.offset(xDirection,yDirection);
        while(cont){
            if (!next.isValid()){
                break;
            }
            ChessPiece p = board.getPiece(next);
            if (p != null){
                cont = false;
                if (p.getTeamColor() == c) {
                    break;
                }
            }
            validMoves.add(new ChessMove(position,next));
            next = next.offset(xDirection,yDirection);

        }
        return validMoves;
    }

    private static Collection<ChessMove> findMovesInPlus(ChessBoard board, ChessPosition position, ChessPiece piece){
        Collection<ChessMove> validMoves = findMovesInLine(board,position,piece,-1,0);
        validMoves.addAll(findMovesInLine(board,position,piece,1,0));
        validMoves.addAll(findMovesInLine(board,position,piece,0,-1));
        validMoves.addAll(findMovesInLine(board,position,piece,0,1));

        return validMoves;
    }

    private static Collection<ChessMove> findMovesInX(ChessBoard board, ChessPosition position, ChessPiece piece){
        Collection<ChessMove> validMoves = findMovesInLine(board,position,piece,-1,-1);
        validMoves.addAll(findMovesInLine(board,position,piece,1,-1));
        validMoves.addAll(findMovesInLine(board,position,piece,-1,1));
        validMoves.addAll(findMovesInLine(board,position,piece,1,1));

        return validMoves;
    }

    private static Collection<ChessMove> findKingMoves(ChessBoard board, ChessPosition position, ChessPiece piece){
        ChessGame.TeamColor c = piece.getTeamColor();
        ArrayList<ChessMove> moves = new ArrayList<>();
        for (int x = -1;x <= 1 ; x++){
            for (int y = -1;y <= 1 ; y++){
                ChessPosition next = position.offset(x,y);
                if((x == 0 && y == 0)||!next.isValid()) {
                    continue;
                }
                ChessPiece p = board.getPiece(next);
                if (p == null || p.getTeamColor() != c) {
                    moves.add( new ChessMove(position,next));
                }
            }
        }

        return moves;
    }

    private static Collection<ChessMove> findQueenMoves(ChessBoard board, ChessPosition position, ChessPiece piece){
        Collection<ChessMove> validMoves = findMovesInPlus(board,position,piece);
        validMoves.addAll(findMovesInX(board,position,piece));

        return validMoves;
    }

    private static Collection<ChessMove> findRookMoves(ChessBoard board, ChessPosition position, ChessPiece piece){
        return findMovesInPlus(board,position,piece);
    }

    private static Collection<ChessMove> findBishopMoves(ChessBoard board, ChessPosition position, ChessPiece piece){
        return findMovesInX(board,position,piece);
    }

    private static Collection<ChessMove> findKnightMoves(ChessBoard board, ChessPosition position, ChessPiece piece){
        int[][] offsets = {
                {-2, 1},{-2,-1},
                { 2, 1},{ 2,-1},
                {-1, 2},{-1,-2},
                { 1, 2},{ 1,-2}};

        ChessGame.TeamColor c = piece.getTeamColor();
        ArrayList<ChessMove> moves = new ArrayList<>();
        for (int[] offset : offsets) {
            int x, y;
            x = offset[0];
            y = offset[1];
            ChessPosition next = position.offset(x, y);
            if (!next.isValid()) {
                continue;
            }
            ChessPiece p = board.getPiece(next);
            if (p != null && p.getTeamColor() == c) {
                continue;
            }
            moves.add(new ChessMove(position, next));
        }

        return moves;
    }

    private static Collection<ChessMove> findPawnMoves(ChessBoard board, ChessPosition position, ChessPiece piece){
        ChessGame.TeamColor c = piece.getTeamColor();
        int direction = c == ChessGame.TeamColor.WHITE? 1 : -1;
        Collection<ChessMove> moves = new ArrayList<>();

        moves.addAll(findPawnAdvanceMoves(board, position, piece, direction));
        moves.addAll(findPawnAttackMoves(board, position, piece, direction));

        return moves;
    }

    private static Collection<ChessMove> findPawnAdvanceMoves(ChessBoard board, ChessPosition position, ChessPiece piece, int direction){
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPosition single = position.offset(0,direction);
        if(!single.isValid()){
            return List.of();
        }
        ChessPiece p = board.getPiece(single);
        if(p == null){
            if (isMovePromotable(single,piece)){
                moves.addAll(getMovePromotions(position,single));
            }else{
                moves.add(new ChessMove(position,single));
            }

            ChessPosition doubleStep = position.offset(0,direction * 2);
            if (doubleStep.isValid() && isPawnAtStart(position,piece)) {
                p = board.getPiece(doubleStep);
                if (p == null) {
                    moves.add(new ChessMove(position, doubleStep));
                }
            }
        }
        return moves;
    }

    private static Collection<ChessMove> findPawnAttackMoves(ChessBoard board, ChessPosition position, ChessPiece piece, int direction) {
        ChessGame.TeamColor c = piece.getTeamColor();
        ChessPiece p;
        ArrayList<ChessMove> moves = new ArrayList<>();

        ChessPosition attackLeft = position.offset(-1, direction);
        if (attackLeft.isValid()) {
            p = board.getPiece(attackLeft);
            if (p != null && p.getTeamColor() != c) {
                if (isMovePromotable(attackLeft, piece)) {
                    moves.addAll(getMovePromotions(position, attackLeft));
                } else {
                    moves.add(new ChessMove(position, attackLeft));
                }
            }
        }
        ChessPosition attackRight = position.offset(1, direction);
        if (attackRight.isValid()) {
            p = board.getPiece(attackRight);
            if (p != null && p.getTeamColor() != c) {
                if (isMovePromotable(attackRight, piece)) {
                    moves.addAll(getMovePromotions(position, attackRight));
                } else {
                    moves.add(new ChessMove(position, attackRight));
                }
            }
        }
        return moves;
    }

    private static Collection<ChessMove> getMovePromotions(ChessPosition start, ChessPosition end){
        return List.of(new ChessMove(start,end, ChessPiece.PieceType.QUEEN),
                new ChessMove(start,end, ChessPiece.PieceType.ROOK),
                new ChessMove(start,end, ChessPiece.PieceType.BISHOP),
                new ChessMove(start,end, ChessPiece.PieceType.KNIGHT));
    }

    private static boolean isPawnAtStart(ChessPosition position, ChessPiece piece){
        ChessGame.TeamColor c = piece.getTeamColor();
        return c == ChessGame.TeamColor.WHITE ? position.getRow() == 2 : position.getRow() == 7;
    }

    private static boolean isMovePromotable(ChessPosition position, ChessPiece piece){
        ChessGame.TeamColor c = piece.getTeamColor();
        return c == ChessGame.TeamColor.WHITE ? position.getRow() == 8 : position.getRow() == 1;
    }
}
