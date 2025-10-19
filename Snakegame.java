import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class Snakegame extends JFrame {
    public Snakegame() {

        setTitle("Snake — Java Swing");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        add(new GamePanel());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Snakegame::new);
    }
}

class GamePanel extends JPanel implements ActionListener {

    static final int TILE = 25;                
    static final int COLS = 28;                
    static final int ROWS = 22;                
    static final int WIDTH = COLS * TILE;
    static final int HEIGHT = ROWS * TILE;
    static final int MAX_UNITS = COLS * ROWS;
    static final int START_LEN = 5;
    static final int DELAY = 100;              

    
    final int[] x = new int[MAX_UNITS];
    final int[] y = new int[MAX_UNITS];
    int body = START_LEN;
    int applesEaten = 0;
    int appleX, appleY;
    char dir = 'R';                            
    boolean running = false;
    boolean paused = false;
    final Timer timer = new Timer(DELAY, this);
    final Random rand = new Random();

    GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(245, 247, 250));
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                int k = e.getKeyCode();
                if (k == KeyEvent.VK_LEFT  && dir != 'R') dir = 'L';
                if (k == KeyEvent.VK_RIGHT && dir != 'L') dir = 'R';
                if (k == KeyEvent.VK_UP    && dir != 'D') dir = 'U';
                if (k == KeyEvent.VK_DOWN  && dir != 'U') dir = 'D';
                if (k == KeyEvent.VK_SPACE) paused = !paused;
                if (k == KeyEvent.VK_R && !running) start();
            }
        });
        start();
    }
    void start() {
        body = START_LEN;
        applesEaten = 0;
        dir = 'R';
        for (int i = 0; i < body; i++) { x[i] = (START_LEN - 1 - i) * TILE; y[i] = 5 * TILE; }
        newApple();
        running = true;
        paused = false;
        timer.start();
        repaint();
    }

    void newApple() {
        appleX = rand.nextInt(COLS) * TILE;
        appleY = rand.nextInt(ROWS) * TILE;
    }

    @Override public void actionPerformed(ActionEvent e) {
        if (!running || paused) { repaint(); return; }
        move();
        checkApple();
        checkCollisions();
        repaint();
    }

    void move() {
        for (int i = body; i > 0; i--) { x[i] = x[i-1]; y[i] = y[i-1]; }
        switch (dir) {
            case 'U' -> y[0] -= TILE;
            case 'D' -> y[0] += TILE;
            case 'L' -> x[0] -= TILE;
            case 'R' -> x[0] += TILE;
        }
    }

    void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            body++;
            applesEaten++;
            newApple();
        }
    }

    void checkCollisions() {
        
        for (int i = body; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) { running = false; break; }
        }

        if (x[0] < 0 || x[0] >= WIDTH || y[0] < 0 || y[0] >= HEIGHT) running = false;
        if (!running) timer.stop();
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(230, 233, 240));
        for (int i = 0; i <= COLS; i++) g2.drawLine(i*TILE, 0, i*TILE, HEIGHT);
        for (int j = 0; j <= ROWS; j++) g2.drawLine(0, j*TILE, WIDTH, j*TILE);
      
        g2.setColor(new Color(220, 66, 66));
        g2.fillOval(appleX + 4, appleY + 4, TILE-8, TILE-8);
        for (int i = 0; i < body; i++) {
            if (i == 0) g2.setColor(new Color(39, 125, 255));        // head
            else        g2.setColor(new Color(82, 156, 255));
            g2.fillRoundRect(x[i]+1, y[i]+1, TILE-2, TILE-2, 10, 10);
        }


        g2.setColor(Color.WHITE);
        g2.fillOval(x[0] + TILE/2 - 7, y[0] + 5, 6, 6);
        g2.fillOval(x[0] + TILE/2 + 1, y[0] + 5, 6, 6);
        g2.setColor(Color.DARK_GRAY);
        g2.fillOval(x[0] + TILE/2 - 6, y[0] + 6, 3, 3);
        g2.fillOval(x[0] + TILE/2 + 2, y[0] + 6, 3, 3);
        g2.setColor(new Color(40, 40, 40));
        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        g2.drawString("Score: " + applesEaten + (paused ? "  (PAUSE)" : ""), 10, 20);

        if (!running) {
            String msg = "Game Over — Score: " + applesEaten + "   (R = restart)";
            g2.setFont(new Font("SansSerif", Font.BOLD, 22));
            int w = g2.getFontMetrics().stringWidth(msg);
            g2.setColor(new Color(0,0,0,140));
            g2.fillRoundRect(WIDTH/2 - w/2 - 14, HEIGHT/2 - 26, w + 28, 44, 12, 12);
            g2.setColor(Color.WHITE);
            g2.drawString(msg, WIDTH/2 - w/2, HEIGHT/2 + 6);
        }
    }
}
