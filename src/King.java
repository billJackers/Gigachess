import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class King extends Piece {

    private static final String IMAGES_KING_BLUE = "images/bking.png";
    private static final String IMAGES_KING_RED = "images/rking.png";

    public King(Sides side, int size, Square initSquare) {
        super(side, size, initSquare);
        switch (side) {
            case BLUE -> this.image = getImageByFile(IMAGES_KING_BLUE);
            case RED -> this.image = getImageByFile(IMAGES_KING_RED);
        }
    }

    public String getName() { return "King"; }

    public List<Square> getLegalMoves(Board board) {
        return getKingLegalMoves(board);
    }

    @Override
    public List<Square> getTargets(Board board) {
        return null;
    }
}
