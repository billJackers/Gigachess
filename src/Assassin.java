import java.util.List;

public class Assassin extends Piece {

    private static final String IMAGES_ASSASSIN_BLUE = "images/bassassin.png";
    private static final String IMAGES_ASSASSIN_RED = "images/rassassin.png";

    public Assassin(Sides side, int size) {
        super(side, size);
        switch (side) {
            case BLUE -> this.image = getImageByFile(IMAGES_ASSASSIN_BLUE);
            case RED -> this.image = getImageByFile(IMAGES_ASSASSIN_RED);
        }
    }

    public List<Square> getLegalMoves(Board b) {
        return null;
    }

}