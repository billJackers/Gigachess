import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public class PlayerController implements MouseListener {  // handles player inputs

    private Square previouslySelected;
    private Sides currentTurn;
    private final Board board;
    private List<Square> legalMovesOfSelectedPiece;
    private List<Square> targetsOfSelectedArcher;
    private ConnectionHandler connectionHandler = null;

    public PlayerController(Board board) {
        previouslySelected = null;
        currentTurn = Sides.BLUE;
        this.board = board;
        board.addMouseListener(this); // OOP black magic
    }

    public void setConnectionHandler(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    public Sides getCurrentTurn() {
        return currentTurn;
    }

    public void swapTurns() {
        if (currentTurn == Sides.BLUE) currentTurn = Sides.RED;
        else currentTurn = Sides.BLUE;
        System.out.println("it is " + currentTurn.name() + "'s turn");
    }


    public void selectSquare(Square selected) {  // conditions: square must contain a piece
        selected.setState(Square.ActionStates.PLAYER_SELECTED);  // highlight square selected
        this.previouslySelected = selected;
        legalMovesOfSelectedPiece = selected.getPiece().getLegalMoves(board);
        for (Square move : legalMovesOfSelectedPiece) {
            move.setState(Square.ActionStates.LEGAL_MOVE);
        }
        if (selected.getPiece() instanceof Archer) { // if piece is archer
            targetsOfSelectedArcher = selected.getPiece().getTargets(board);
            for (Square shot : targetsOfSelectedArcher) { // highlight all targets
                shot.setState(Square.ActionStates.ARCHER_SHOT);
            }
        }
    }
    public void deselectCurrent() {
        this.previouslySelected.setState(Square.ActionStates.NONE);  // unhighlight square highlighted
        for (Square move : legalMovesOfSelectedPiece) {  // remove all legalMove highlights
            move.setState(Square.ActionStates.NONE);
        }
        System.out.println(previouslySelected.getPiece());
        if (targetsOfSelectedArcher != null) { // if piece is archer
            for (Square shot : targetsOfSelectedArcher) { // unhighlight all archer shot squares
                shot.setState(Square.ActionStates.NONE);
            }
        }
        this.previouslySelected = null;  // cut the references
        this.legalMovesOfSelectedPiece = null;
    }

    public void move(Square from, Square to) {
        Piece pieceToMove = from.getPiece();

        boolean enPassantMove = false;

        if (pieceToMove instanceof Pawn) {
            if (!to.hasPiece()) enPassantMove = true;
        }

        from.clearPiece();

        // King taken
        if (to.hasPiece() && to.getPiece() instanceof King) {
            System.out.println(pieceToMove.side + " has won!");
            WinFrame winFrame = new WinFrame(pieceToMove.side);
            SwingUtilities.invokeLater((Runnable) winFrame);
        }

        // Bomber explosion
        if (to.getPiece() instanceof Bomber && !(pieceToMove instanceof Assassin)) {
            explode(to);
        } else to.setPiece(pieceToMove);

        if (to.getPiece() instanceof Pawn) {
            ((Pawn) to.getPiece()).setToMoved();
        }

        // Pawn promotions
        if (to.getPiece() instanceof Pawn) {
            switch (to.getPiece().side) {
                case BLUE -> {
                    if (to.getFile() == 9) to.setPiece(new Queen(Sides.BLUE, 50, to));
                }
                case RED -> {
                    if (to.getFile() == 0) to.setPiece(new Queen(Sides.RED, 50, to));
                }
            }
        }

        // En Passant
        if (enPassantMove) {
            switch (pieceToMove.side) {
                case BLUE -> board.getBoard()[(to.getFile() * 10) + to.getRank() - 10].clearPiece();
                case RED -> board.getBoard()[(to.getFile() * 10) + to.getRank() + 10].clearPiece();
            }
        }

        switch (pieceToMove.side) {
            case BLUE: resetEnPassants(Sides.BLUE);
            break;
            case RED: resetEnPassants(Sides.RED);
        }

        if (pieceToMove instanceof Pawn) {
            int fromPos = (from.getFile() * 10) + from.getRank();
            int toPos = (to.getFile() * 10) + to.getRank();
            if (Math.abs(toPos-fromPos) == 20) to.getPiece().setEnPassantable(true);
        }

    }

    public void attemptMove(Square selected) {
        if (previouslySelected != null && previouslySelected.hasPiece()) {  // if we currently have a piece selected to move
            Piece pieceToMove = previouslySelected.getPiece();

            if (isCorrectPlayerMoving()) {  // if the correct player is moving

                if (legalMovesOfSelectedPiece.contains(selected) && pieceToMove.canCapture(selected)) {  // if it is legal to move to the new location
                    move(previouslySelected, selected);
                    if (connectionHandler != null) {
                        String moves = previouslySelected.getX() + " " + previouslySelected.getY() + " " + selected.getX() + " " + selected.getY();  // coords movedto and movedfrom
                        connectionHandler.send(moves);
                    }
                    swapTurns();  // swap the turns on a successful move
                }
                if (pieceToMove instanceof Archer && pieceToMove.getTargets(board).contains(selected)) {  // COMMENT YOUR CODE <-----------------------
                    if (selected.getPiece() instanceof Bomber) explode(selected);
                    else selected.clearPiece();
                    swapTurns();
                }
            }

            deselectCurrent();  // clear board states on after this click
        } else {  //
            if (selected.hasPiece()) {
                selectSquare(selected);
            }
        }
    }

    public boolean isCorrectPlayerMoving() {  // checks whether it is the correct player's turn
        return (currentTurn == Sides.BLUE && previouslySelected.getPiece().getSide() == Sides.BLUE) || (currentTurn == Sides.RED && previouslySelected.getPiece().getSide() == Sides.RED);
    }

    public void explode(Square s) {
        List<Square> targets = s.getPiece().getTargets(board);
        for (Square target : targets) target.clearPiece();
        s.clearPiece();
    }

    public void resetEnPassants(Sides side) {
        for (Square square : board.getBoard()) {
            if (square.hasPiece()) {
                switch (side) {
                    case BLUE -> {
                        if (square.getPiece().side == Sides.BLUE) square.getPiece().setEnPassantable(false);
                    }
                    case RED -> {
                        if (square.getPiece().side == Sides.RED) square.getPiece().setEnPassantable(false);
                    }
                }
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        Square squareSelected = board.getSquareClicked(e.getX(), e.getY());
        attemptMove(squareSelected);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}