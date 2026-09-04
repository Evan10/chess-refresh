package chess;

import java.util.Collection;

public class KingCheckIdentifier {

    public static boolean isKingUnderAttack(ChessBoard board, ChessGame.TeamColor color) throws InvalidGameStateException{
        Collection<ChessPosition> positions = board.findChessPieceLoc(ChessPiece.PieceType.KING,color);
        if(positions.isEmpty()){
            throw new InvalidGameStateException("No king piece found");
        }


        return false;
    }

}
