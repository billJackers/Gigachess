import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ComputerOpponent {
    // Test comment
    class MoveListener extends PlayerController {
        public MoveListener(Settings settings) {
            super(settings);
        }

        public void move(Square from, Square to) {
            super.move(from, to);
            runComputerMove();
        }

        public void shoot(Square from, Square to) {
            super.shoot(from, to);
            runComputerMove();
        }
    }

    private class Move {  // move class is used to pair "from" and "to" squares

        private final Square from;
        private final Square to;
        private final Type type;
        private float score;

        private enum Type {
            PIECE_MOVE,
            ARCHER_SHOT
        }

        public Move (Square from, Square to, Type moveType, float score) {
            this.from = from;
            this.to = to;
            this.type = moveType;
            this.score = score;
        }

        public void play() {
            switch (type){
                case PIECE_MOVE -> moveListener.move(from, to);
                case ARCHER_SHOT -> moveListener.shoot(from, to);
            }
        }

        public float getScore() {
            return score;
        }

        public void setScore(float score) {
            this.score = score;
        }
    }

    private final MoveListener moveListener;
    private final Board board;
    private static int globalDepth = 4;

    public ComputerOpponent(Settings settings) {
        JFrame gameWindow = new JFrame("Giga Chess");
        gameWindow.setLocationRelativeTo(null);

        String FEN = "rbbrqkrbbr/socnggncos/pppppppppp/X/X/X/X/PPPPPPPPPP/SOCNGGNCOS/RBBRQKRBBR";

        moveListener = new MoveListener(settings);  // the playerController but also how we know when the computer should move
        board = new Board(settings, moveListener, FEN);  // creates the board JPanel to draw on
        StatsDisplay stats = new StatsDisplay(board, settings);  // stats displayer JPanel

        // adding JPanels to JFrame
        gameWindow.add(stats, BorderLayout.NORTH);
        gameWindow.add(board, BorderLayout.SOUTH);

        gameWindow.setSize(board.getPreferredSize()); // Set the size of the window based on the size of the board
        gameWindow.setResizable(false); // don't allow the user to resize the window
        gameWindow.pack(); // pack() should be called after setResizable() to avoid issues on some platforms
        gameWindow.setVisible(true);
        gameWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public void runComputerMove() {
        if (moveListener.getCurrentTurn() != Sides.RED) return;  // it is the player's move so don't do anything

        ActionListener listener = e -> {
            runEvaluatedMove();
        };
        int delay = (int) (Math.random() * 2000);
        Timer timer = new Timer(delay, listener);
        timer.setRepeats(false);
        timer.start();

    }

    private float evaluate(Square from, Square to) {

        float score = 0;
        if (to.hasPiece()) {
            Piece enemy = to.getPiece();
            score += enemy.materialValue;
        }
        return score;
    }

    public void runEvaluatedMove() {

        ArrayList<Move> bestMoves = new ArrayList<>();  // arraylist of the highest scoring moves
        bestMoves.add(new Move(null, null, null, Integer.MIN_VALUE));

        for (Square from : board.getBoard()) {  // iterate through the board
            if (from.hasPiece() && from.getPiece().getSide() == Sides.RED) {  // get the square of every playable piece
                for (Square to : from.getPiece().getLegalMoves(board)) {  // get the legalMoves of the playable piece

                    float moveEval = evaluate(from, to);
                    if (moveEval > bestMoves.get(0).getScore()) {
                        bestMoves.clear();
                        bestMoves.add(new Move(from, to, Move.Type.PIECE_MOVE, moveEval));
                    }
                    else if (moveEval == bestMoves.get(0).getScore()) {
                        bestMoves.add(new Move(from, to, Move.Type.PIECE_MOVE, moveEval));
                    }
                }

                if (from.getPiece() instanceof Archer) {  // if the piece is an archer, we also need to get the shots
                    for (Square shots : from.getPiece().getTargets(board)) {  // get the legalMoves of the playable piece
                        float moveEval = evaluate(from, shots);
                        if (moveEval > bestMoves.get(0).getScore()) {
                            bestMoves.clear();
                            bestMoves.add(new Move(from, shots, Move.Type.ARCHER_SHOT, moveEval));
                        }
                        else if (moveEval == bestMoves.get(0).getScore()) {
                            bestMoves.add(new Move(from, shots, Move.Type.ARCHER_SHOT, moveEval));
                        }
                    }
                }

            }
        }
        Move randBestMove = bestMoves.get((int) ((Math.random() * (bestMoves.size()-1))));
        randBestMove.play();

    }

    public void runRandomMove() {
        Move randMove = getAllPossibleMoves().get((int) ((Math.random() * (getAllPossibleMoves().size()-1))));
        randMove.play();
    }

    // -1 is blue, 1 is red
    // https://www.youtube.com/watch?v=UZLnDvdeNo8
    // Doesn't work btw
    /*
    public Move alphaBeta(int depth, int beta, int alpha, Move move, int player) {

        ArrayList<Move> moves = getAllPossibleMoves();
        moves.add(new Move(null, null, null, Integer.MIN_VALUE));
        if (depth == 0 || moves.size() == 0) return move;
        moves = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        int temp = scanner.nextInt();

        for (int i = 0; i < temp; i++) {
            moves.add(new Move(new Square(0, 0, 60), new Square(0, 1, 60), Move.Type.PIECE_MOVE, 2));
        }

        player = 1-player;
        for (int i = 0; i < moves.size(); i++) {
            move.play();
            moveListener.swapTurns();
            Move returnMove = alphaBeta(depth-1, beta, alpha, move, player);
            int evalScore = move.getScore();
            moveListener.swapTurns();
            moveListener.undoMove();
            if (player == 0) {
                if (evalScore <= beta) {
                    beta = evalScore;
                    if (depth == globalDepth) move = returnMove;
                }
            } else {
                if (evalScore > alpha) {
                    alpha = evalScore;
                    if (depth == globalDepth) move = returnMove;
                }
            }
            if (alpha >= beta) {
                if (player == 0) {
                    move.setScore(beta);
                } else {
                    move.setScore(alpha);
                }
                return move;
            }
        }
        if (player == 0) {
            move.setScore(beta);
        } else {
            move.setScore(alpha);
        }
        return move;
    }

     */

    public ArrayList<Move> getAllPossibleMoves() {
        ArrayList<Move> allPossibleMoves = new ArrayList<>();
        for (Square from : board.getBoard()) {  // iterate through the board
            if (from.hasPiece() && from.getPiece().getSide() == Sides.RED) {  // get the square of every playable piece

                for (Square to : from.getPiece().getLegalMoves(board)) {  // get the legalMoves of the playable piece
                    allPossibleMoves.add(new Move(from, to, Move.Type.PIECE_MOVE, 0));  // add each legalMove to allPossibleMoves
                }

                if (from.getPiece() instanceof Archer) {  // if the piece is an archer, we also need to get the shots
                    for (Square shots : from.getPiece().getTargets(board)) {  // get the legalMoves of the playable piece
                        allPossibleMoves.add(new Move(from, shots, Move.Type.ARCHER_SHOT, 0));  // add each Archer shot to allPossibleMoves
                    }
                }

            }
        }
        return allPossibleMoves;
    }

}