import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SudokuSolver extends JFrame {

    private static final Color BG_TOP = new Color(10, 10, 35);
    private static final Color BG_BOT = new Color(30, 20, 80);
    private static final Color ACCENT_PURPLE = new Color(140, 82, 255);
    private static final Color ACCENT_PINK = new Color(255, 72, 176);
    private static final Color ACCENT_CYAN = new Color(0, 229, 255);
    private static final Color ACCENT_GOLD = new Color(255, 214, 0);
    private static final Color SUCCESS = new Color(57, 255, 20);
    private static final Color DANGER = new Color(255, 75, 75);

    private static final Color CELL_BG = new Color(25, 15, 60);
    private static final Color CELL_GIVEN = new Color(40, 25, 90);
    private static final Color CELL_SOLVED = new Color(20, 55, 40);
    private static final Color CELL_BORDER = new Color(80, 60, 140);
    private static final Color BOX_BORDER = new Color(140, 82, 255);

    private static final int[][] DEFAULT_PUZZLE = {
        {5, 3, 0, 0, 7, 0, 0, 0, 0},
        {6, 0, 0, 1, 9, 5, 0, 0, 0},
        {0, 9, 8, 0, 0, 0, 0, 6, 0},
        {8, 0, 0, 0, 6, 0, 0, 0, 3},
        {4, 0, 0, 8, 0, 3, 0, 0, 1},
        {7, 0, 0, 0, 2, 0, 0, 0, 6},
        {0, 6, 0, 0, 0, 0, 2, 8, 0},
        {0, 0, 0, 4, 1, 9, 0, 0, 5},
        {0, 0, 0, 0, 8, 0, 0, 7, 9}
    };

    private final JTextField[][] cells = new JTextField[9][9];
    private final boolean[][] given = new boolean[9][9];
    private JLabel statusLabel;

    public SudokuSolver() {
        setTitle("Sudoku Solver");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 750);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
        clearAll();
        setVisible(true);
    }

    private void buildUI() {
        JPanel root = gradientPanel();
        root.setLayout(new BorderLayout());
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildGrid(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(22, 30, 8, 30));

        JLabel title = label("SUDOKU SOLVER", 26, Font.BOLD, ACCENT_GOLD);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitle = label("Backtracking Algorithm | Enter puzzle and click Solve", 13, Font.PLAIN, ACCENT_CYAN);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(title);
        panel.add(subtitle);
        return panel;
    }

    private JPanel buildGrid() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        JPanel grid = new JPanel(new GridLayout(9, 9, 0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(BOX_BORDER);
                g2.setStroke(new BasicStroke(3f));

                int width = getWidth();
                int height = getHeight();
                int cellWidth = width / 9;
                int cellHeight = height / 9;

                for (int block = 0; block <= 3; block++) {
                    g2.drawLine(block * 3 * cellWidth, 0, block * 3 * cellWidth, height);
                    g2.drawLine(0, block * 3 * cellHeight, width, block * 3 * cellHeight);
                }
                g2.dispose();
            }
        };
        grid.setOpaque(false);
        grid.setPreferredSize(new Dimension(504, 504));

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                cells[row][col] = buildCell(row, col);
                grid.add(cells[row][col]);
            }
        }

        wrapper.add(grid);
        return wrapper;
    }

    private JTextField buildCell(int row, int col) {
        JTextField textField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };

        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.setFont(new Font("SansSerif", Font.BOLD, 20));
        textField.setForeground(Color.WHITE);
        textField.setCaretColor(ACCENT_GOLD);
        textField.setOpaque(true);
        textField.setBackground(CELL_BG);

        boolean rightBox = col % 3 == 2 && col < 8;
        boolean bottomBox = row % 3 == 2 && row < 8;
        int right = rightBox ? 3 : 1;
        int bottom = bottomBox ? 3 : 1;
        Border border = BorderFactory.createMatteBorder(1, 1, bottom, right, CELL_BORDER);
        textField.setBorder(border);

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char ch = e.getKeyChar();
                if (textField.getText().length() >= 1 || !Character.isDigit(ch) || ch == '0') {
                    e.consume();
                }
            }
        });

        return textField;
    }

    private JPanel buildFooter() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(8, 30, 20, 30));

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        buttonRow.setOpaque(false);

        JButton solveButton = roundBtn("SOLVE", ACCENT_PURPLE, Color.WHITE, 160, 46);
        JButton clearButton = roundBtn("CLEAR", new Color(80, 80, 140), Color.WHITE, 130, 46);
        JButton sampleButton = roundBtn("SAMPLE", ACCENT_PINK, Color.WHITE, 130, 46);

        solveButton.addActionListener(e -> solve());
        clearButton.addActionListener(e -> clearAll());
        sampleButton.addActionListener(e -> loadPuzzle(DEFAULT_PUZZLE));

        buttonRow.add(solveButton);
        buttonRow.add(clearButton);
        buttonRow.add(sampleButton);

        statusLabel = label("Ready - enter a puzzle or load a sample.", 13, Font.PLAIN, Color.LIGHT_GRAY);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(buttonRow, BorderLayout.CENTER);
        panel.add(statusLabel, BorderLayout.SOUTH);
        return panel;
    }

    private void solve() {
        int[][] board = new int[9][9];

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                String text = cells[row][col].getText().trim();
                if (text.isEmpty()) {
                    board[row][col] = 0;
                    given[row][col] = false;
                } else {
                    board[row][col] = Integer.parseInt(text);
                    given[row][col] = true;
                    cells[row][col].setBackground(CELL_GIVEN);
                    cells[row][col].setForeground(ACCENT_GOLD);
                }
            }
        }

        if (!isValidBoard(board)) {
            setStatus("Invalid puzzle - duplicate numbers detected.", DANGER);
            return;
        }

        setStatus("Solving with backtracking...", ACCENT_CYAN);

        long start = System.currentTimeMillis();
        boolean solved = backtrack(board);
        long elapsed = System.currentTimeMillis() - start;

        if (solved) {
            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    if (!given[row][col]) {
                        cells[row][col].setText(String.valueOf(board[row][col]));
                        cells[row][col].setBackground(CELL_SOLVED);
                        cells[row][col].setForeground(SUCCESS);
                    }
                }
            }
            setStatus("Solved in " + elapsed + " ms using backtracking.", SUCCESS);
        } else {
            setStatus("No solution exists for this puzzle.", DANGER);
        }
    }

    private void clearAll() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                cells[row][col].setText("");
                cells[row][col].setBackground(CELL_BG);
                cells[row][col].setForeground(Color.WHITE);
                cells[row][col].setEditable(true);
                given[row][col] = false;
            }
        }
        setStatus("Board cleared. Enter your puzzle.", Color.LIGHT_GRAY);
    }

    private void loadPuzzle(int[][] puzzle) {
        clearAll();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (puzzle[row][col] != 0) {
                    cells[row][col].setText(String.valueOf(puzzle[row][col]));
                    cells[row][col].setBackground(CELL_GIVEN);
                    cells[row][col].setForeground(ACCENT_GOLD);
                    given[row][col] = true;
                }
            }
        }
        setStatus("Sample puzzle loaded. Click SOLVE.", ACCENT_CYAN);
    }

    private boolean backtrack(int[][] board) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == 0) {
                    for (int num = 1; num <= 9; num++) {
                        if (isSafe(board, row, col, num)) {
                            board[row][col] = num;
                            if (backtrack(board)) {
                                return true;
                            }
                            board[row][col] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isSafe(int[][] board, int row, int col, int num) {
        for (int c = 0; c < 9; c++) {
            if (board[row][c] == num) {
                return false;
            }
        }

        for (int r = 0; r < 9; r++) {
            if (board[r][col] == num) {
                return false;
            }
        }

        int boxRow = (row / 3) * 3;
        int boxCol = (col / 3) * 3;
        for (int r = boxRow; r < boxRow + 3; r++) {
            for (int c = boxCol; c < boxCol + 3; c++) {
                if (board[r][c] == num) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isValidBoard(int[][] board) {
        for (int i = 0; i < 9; i++) {
            boolean[] rowSeen = new boolean[10];
            boolean[] colSeen = new boolean[10];
            boolean[] boxSeen = new boolean[10];

            for (int j = 0; j < 9; j++) {
                int rowValue = board[i][j];
                int colValue = board[j][i];
                int boxRow = (i / 3) * 3 + j / 3;
                int boxCol = (i % 3) * 3 + j % 3;
                int boxValue = board[boxRow][boxCol];

                if (rowValue != 0) {
                    if (rowSeen[rowValue]) {
                        return false;
                    }
                    rowSeen[rowValue] = true;
                }

                if (colValue != 0) {
                    if (colSeen[colValue]) {
                        return false;
                    }
                    colSeen[colValue] = true;
                }

                if (boxValue != 0) {
                    if (boxSeen[boxValue]) {
                        return false;
                    }
                    boxSeen[boxValue] = true;
                }
            }
        }
        return true;
    }

    private JPanel gradientPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, BG_TOP, 0, getHeight(), BG_BOT));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(140, 82, 255, 22));
                g2.fillOval(-80, -80, 300, 300);
                g2.setColor(new Color(255, 72, 176, 15));
                g2.fillOval(getWidth() - 180, getHeight() - 180, 300, 300);
                g2.dispose();
            }
        };
    }

    private JLabel label(String text, int size, int style, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", style, size));
        label.setForeground(color);
        return label;
    }

    private JButton roundBtn(String text, Color bg, Color fg, int width, int height) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(bg.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bg.brighter());
                } else {
                    g2.setColor(bg);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setForeground(fg);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(width, height));
        return button;
    }

    private void setStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(SudokuSolver::new);
    }
}
