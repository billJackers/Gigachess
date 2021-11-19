import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Bomber extends Piece {

    private static final String IMAGES_BOMBER_BLUE = "images/bbomber.png";
    private static final String IMAGES_BOMBER_RED = "images/rbomber.png";

    public Bomber(Sides side, int size, Square initSquare) {
        super(side, size, initSquare);
        switch (side) {
            case BLUE -> this.image = getImageByFile(IMAGES_BOMBER_BLUE);
            case RED -> this.image = getImageByFile(IMAGES_BOMBER_RED);
        }
    }

    public List<Square> getLegalMoves() {
        ArrayList<Square> legalMoves = new ArrayList<>();

        int rank = this.parentSquare.getRank();
        int file = this.parentSquare.getFile();

        Square[] b = board.getBoard();

        int pos = (file*10) + rank;

        switch (this.side) {
            case BLUE -> {
                try {
                    if (!b[pos + 10].hasPiece() || this.canCapture(b[pos + 10])) legalMoves.add(b[pos + 10]);
                    if (pos + 10 < 100 && !b[pos + 10].hasPiece()) {
                        legalMoves.add(b[pos + 10]);
                    }
                    if (pos % 10 != 9 && pos + 10 < 100 && this.canCapture(b[pos + 11])) {
                        legalMoves.add(b[pos + 11]);
                    }

                    if (pos % 10 != 0 && pos + 9 < 100 && this.canCapture(b[pos + 9])) {
                        legalMoves.add(b[pos + 9]);
                    }
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Your bomber is stuck lol");
                }

            }

            case RED -> {
                try {
                    if (!b[pos - 10].hasPiece() || this.canCapture(b[pos - 10])) legalMoves.add(b[pos - 10]);
                    if (pos - 10 > 0 && !b[pos - 10].hasPiece()) {
                        legalMoves.add(b[pos - 10]);
                    }
                    if (pos % 10 != 9 && pos - 10 > 0 && this.canCapture(b[pos - 9])) {
                        legalMoves.add(b[pos - 9]);
                    }

                    if (pos % 10 != 0 && pos - 11 > 0 && this.canCapture(b[pos - 11])) {
                        legalMoves.add(b[pos - 11]);
                    }
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Your bomber is stuck lol");
                }
            }
        }

        return legalMoves;
    }

    @Override
    public List<Square> getTargets() {
        ArrayList<Square> targets = new ArrayList<>();

        int rank = this.parentSquare.getRank();
        int file = this.parentSquare.getFile();

        Square[] b = board.getBoard();

        int pos = (file*10) + rank;
        int row = pos / 10;
        int column = pos % 10;
        /*
        // Up
        if (pos >= 10 && b[pos-10].hasPiece()) {
            targets.add(b[pos-10]);
        }
        // Down
        if (pos < 90 && b[pos+10].hasPiece()) {
            targets.add(b[pos+10]);
        }
        // Right
        if (pos % 10 != 9 && b[pos+1].hasPiece()) {
            targets.add(b[pos+1]);
        }
        // Left
        if (pos % 10 != 0 && b[pos-1].hasPiece()) {
            targets.add(b[pos-1]);
        }
         */
        int[] bombTargets = {-11, -10, -9,-1, 1, 9, 10, 11};
        for (int target : bombTargets) {
            if (pos + target < 100 && pos + target >= 0 && acceptableRow(row, pos / 10) && acceptableColumn(column, pos % 10) && b[pos + target].hasPiece()) {
                targets.add(b[pos + target]);
            }
        }
        return targets;
    }

    public boolean acceptableRow(int row, int proposedRow) {
        int maxRow = row + 1;
        int minRow = row - 1;
        if (proposedRow <= maxRow && proposedRow >= minRow) {
            return true;
        }
        return false;
    }

    public boolean acceptableColumn(int column, int proposedColumn) {
        int maxColumn = column + 1;
        int minColumn = column - 1;
        if (proposedColumn <= maxColumn && proposedColumn >= minColumn) {
            return true;
        }
        return false;
    }

}
