package chess;

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
                ChessPosition pos = position.offset(x,y);
                if((x == 0 && y == 0)||!pos.isValid()) {
                    continue;
                }
                ChessPiece p = board.getPiece(pos);
                if (p == null || p.getTeamColor() != c) {
                    moves.add( new ChessMove(position,pos));
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



        return List.of();
    }

    private static Collection<ChessMove> findPawnMoves(ChessBoard board, ChessPosition position, ChessPiece piece){



        return List.of();
    }
}
