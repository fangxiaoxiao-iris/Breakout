
/** I sited source code here
 * Title: HelloMVC: a simple MVC example
 * Author: Joseph Mack
 * Availability: http://www.austintek.com/mvc/
 */

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Game extends JPanel{

    private JFrame f;
    private Model model;
    private View view;
    private double frame_rate;
    private double ball_speed;
    private boolean gameBegin = false;
    private boolean over = false;
    private boolean win = false;
    private boolean playerBegin = false;


    public Game(String frame_rate, String ball_speed) {
        this.frame_rate = Double.parseDouble(frame_rate);
        this.ball_speed = Double.parseDouble(ball_speed);

        this.f = new JFrame("Breakout Game");
        this.f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.f.setVisible(true);
        this.f.setContentPane(this);
        f.setMinimumSize(new Dimension(800, 800));
        f.setMaximumSize(new Dimension(800, 800));

        // create view and model
        this.view = new View(800, 800);
        this.setLayout(new BorderLayout());
        this.add(view, BorderLayout.CENTER);
        this.f.pack();
        this.model = new Model();
        model.play();
    }

    class View extends JPanel {
        private int width;
        private int height;
        private JButton playButton;


        public View(int width, int height) {
            this.width = width;
            this.height = height;
            this.setLayout(new BorderLayout());

            if (!gameBegin) {
                this.playButton = new JButton("PLAY");
                playButton.setFont(new Font("TimesRoman", Font.BOLD, (int)(this.width*0.1)));
                playButton.setBorder(new LineBorder(Color.pink, 3));
                playButton.setBackground(Color.pink);
                playButton.setOpaque(true);
                playButton.setForeground(Color.orange);
                this.add(playButton, BorderLayout.SOUTH);
                playButton.addActionListener(e -> gameBegin = true);
            }
        }

        // getter and setter
        public void setWidth(int width) {
            this.width = width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }

        public void paintComponent(Graphics g) {
            setDoubleBuffered(true);
            if (!gameBegin) {
                this.startPage(g);

            } else if (gameBegin) {
                this.remove(playButton);

                if (!over) {
                    model.draw(g);
                    if (!playerBegin) {
                        this.pressKey(g);
                    }
                } else if (win) {
                    this.Win(g);
                } else if (over) {
                    this.Lose(g);
                }
            }
        }

        private void pressKey(Graphics g) {
            Graphics2D G = (Graphics2D) g;
            G.setColor(Color.red);
            Font font = new Font("TimesRoman", Font.BOLD, (int)(this.width*0.05));
            G.setFont(font);
            FontMetrics met = G.getFontMetrics();
            String line = "Press any key to get started";
            G.drawString(line, (int) (this.width / 2 - met.stringWidth(line) / 2), (int) (0.06 * this.height));
        }

        private void startPage(Graphics g) {
            Graphics2D G = (Graphics2D) g;
            G.setColor(Color.PINK);
            G.fillRect(0, 0, view.width, view.height);

            G.setColor(Color.orange);
            Font font = new Font("TimesRoman", Font.BOLD, (int)(this.width*0.2));
            G.setFont(font);
            String playGame = "Breakout";
            FontMetrics metrics = G.getFontMetrics();
            G.drawString(playGame, this.width/2-metrics.stringWidth(playGame)/2, (int)(this.height*0.5));

        }

        private void Win(Graphics g) {
            Graphics2D G = (Graphics2D) g;
            G.setColor(Color.PINK);
            G.fillRect(0, 0, view.width, view.height);

            G.setColor(Color.orange);
            Font font = new Font("TimesRoman", Font.BOLD, (int)(this.width*0.2));
            G.setFont(font);
            String playGame = "You win!";
            FontMetrics metrics = G.getFontMetrics();
            G.drawString(playGame, this.width/2-metrics.stringWidth(playGame)/2, (int)(this.height*0.5));
        }

        private void Lose(Graphics g) {
            Graphics2D G = (Graphics2D) g;
            G.setColor(Color.PINK);
            G.fillRect(0, 0, view.width, view.height);

            G.setColor(Color.orange);
            Font font = new Font("TimesRoman", Font.BOLD, (int)(this.width*0.2));
            G.setFont(font);
            String playGame = "You lose!";
            FontMetrics metrics = G.getFontMetrics();
            G.drawString(playGame, this.width/2-metrics.stringWidth(playGame)/2, (int)(this.height*0.5));

            G.setColor(Color.orange);
            font = new Font("TimesRoman", Font.BOLD, (int)(view.width*0.08));
            G.setFont(font);
            String showScore = "Score: " +model.score;
            metrics = G.getFontMetrics();
            G.drawString(showScore, this.width/2-metrics.stringWidth(showScore)/2, (int)(this.height*0.65));
        }
    }

    class Model implements KeyListener, MouseMotionListener, ComponentListener, MouseListener {

        private Board board;
        private Paddle paddle;
        private Ball ball;
        private ArrayList<Brick> bricks;
        private int score;

        public Model() {
            this.score = 0;
            board = new Board(0, 0, view.getWidth(), view.getHeight());

            // create paddle
            double width = view.getWidth()*0.20;
            double height = view.getHeight()*0.02;
            double x = view.getWidth()/2 - width/2;
            double y = view.getHeight() - 0.08*view.getHeight();
            this.paddle = new Paddle(x, y, width, height);

            // create ball
            double radius = 0.02*view.getHeight();
            x = view.getWidth()/2 - radius;
            y = y - 2*radius;
            this.ball = new Ball(x, y, radius);

            // create bricks
            this.bricks = new ArrayList<Brick>();
            x = 0;
            y = view.getHeight()*0.1;
            width = 0.1*view.getWidth();
            height = 0.05*view.getHeight();

            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 10; j++) {
                    if (i == 0|| i==1) {
                        Brick b = new Brick(x, y, width, height, Color.pink, 1);
                        bricks.add(b);
                    } else if (i==2 || i==3){
                        Brick b = new Brick(x, y, width, height, Color.orange, 2);
                        bricks.add(b);
                    } else {
                        Brick b = new Brick(x, y, width, height, Color.red, 3);
                        bricks.add(b);
                    }
                    x+=width;
                }
                x=0;
                y+=height;
            }

            f.setFocusable(true);
            f.addKeyListener(this);
            f.addMouseListener(this);
            addComponentListener(this);

        }

        public void play() {
            Timer timer = new Timer();
            TimerTask task = new TimerTask()  {
                @Override
                public void run() {
                    model.ballAnimation();
                    repaint();
                }
            };
            timer.schedule(task, 0, (1000/(int)frame_rate));
        }

        public void ballAnimation() {
            if (playerBegin) {

                this.collideWithPaddle();
                this.collideWithBoundary();
                this.collideWithBrick();

                ball.setX(ball.getX() + ball.getDx());
                ball.setY(ball.getY() + ball.getDy());

                if( ball.intersects(board.getMinX(), board.getMaxY(), board.getWidth(), 1) ) {
                    over = true;
                }

            }
        }

        public void collideWithPaddle() {
            Rectangle2D paddle = new Rectangle2D.Double(this.paddle.getX(), this.paddle.getY(), this.paddle.getWidth(), this.paddle.getHeight());
            if (ball.intersects(paddle)) {
                ball.setDy(ball.getDy()*-1);
            }
        }

        public void collideWithBoundary() {
            if (ball.getMinX() < board.getMinX()) {
                ball.setDx(ball.getDx()*-1);
            }
            if (ball.getMaxX() > board.getMaxX()) {
                ball.setDx(ball.getDx()*-1);
            }
            if (ball.getMinY() < board.getMinY()) {
                ball.setDy(ball.getDy()*-1);
            }
        }

        public void collideWithBrick() {
            for (Brick b:bricks) {
                if (!b.isnotExist()) {
                    if(ball.intersects(b)) {
                        if (ball.getMinY() < b.getMinY()) {
                            ball.setDy(ball.getDy()*(-1));

                        } else if (ball.getMaxY() > b.getMaxY()) {
                            ball.setDy(ball.getDy()*(-1));
                            //System.out.print(ball.getDx());

                        } else if (ball.getMinX() < b.getMinX()) {
                            ball.setDx(ball.getDx()*(-1));
                        } else if (ball.getMaxX() > b.getMaxX()) {
                            ball.setDx(ball.getDx()*(-1));
                        }
                        b.onBrick();
                        this.score+=1;
                    }

                }
            }

        }


        public void draw(Graphics g) {
            board.draw(g);
            paddle.draw(g);
            ball.draw(g);
            for (Brick b:bricks) {
                b.draw(g);
            }

            Graphics2D G = (Graphics2D)g;
            G.setColor(Color.pink);
            Font font = new Font("TimesRoman", Font.BOLD, (int)(view.width*0.03));
            G.setFont(font);
            String showScore = "Score: " + this.score;
            G.drawString(showScore, 10, 40);
        }

        @Override
        public void componentResized(ComponentEvent e) {

        }

        @Override
        public void componentMoved(ComponentEvent e) {

        }

        @Override
        public void componentShown(ComponentEvent e) {

        }

        @Override
        public void componentHidden(ComponentEvent e) {

        }

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            if(!playerBegin) {
                playerBegin = true;
                f.addMouseMotionListener(this);
            } else {
                double x = paddle.getX();
                if(e.getKeyCode() == KeyEvent.VK_LEFT) {
                    x -= 25;
                    if (x < board.getMinX()) {
                        x = board.getMinX();
                    }
                }
                if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    x += 25;
                    if (x + paddle.getWidth() > board.getMaxX()) {
                        x = board.getMaxX() - paddle.getWidth();
                    }
                }
                paddle.setRect(x, paddle.getY(), paddle.getWidth(), paddle.getHeight());
            }

        }

        @Override
        public void keyReleased(KeyEvent e) {

        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (!playerBegin) {
                playerBegin = true;
                f.addMouseMotionListener(this);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

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

        @Override
        public void mouseDragged(MouseEvent e) {

        }

        @Override
        public void mouseMoved(MouseEvent e) {
            double x_pos = e.getX();
            if (x_pos < board.getMinX()) {
                x_pos = board.getMinX();
            } else if (x_pos + paddle.getWidth() > board.getMaxX()) {
                x_pos = board.getMaxX() - paddle.getWidth();
            }
            paddle.setRect(x_pos, paddle.getY(), paddle.getWidth(), paddle.getHeight());
        }
    }

    class Rectangle extends Rectangle2D.Double {
        protected Color color;
        protected View view;

        public Rectangle(double x, double y, double width, double height) {
            super(x, y, width, height);
            this.color = Color.black;
        }

        public void draw(Graphics g) {
            Graphics2D G = (Graphics2D)g;
            G.setColor(this.color);
            G.fill(this);
        }

    }

    class Board extends Rectangle {
        public Board(double x, double y, double width, double height) {
            super(x, y, width, height);
            this.color = Color.WHITE;
        }

    }

    class Paddle extends Rectangle {
        public Paddle(double x, double y, double width, double height) {
            super(x, y, width, height);
            this.color = Color.pink;
        }
    }

    class Brick extends Rectangle {
        protected boolean notExist;
        protected int remainingChance;

        public Brick(double x, double y, double width, double height, Color c, int remainingChance) {
            super(x, y, width, height);
            this.notExist = false;
            this.remainingChance = remainingChance;
            this.color = c;
        }

        // getters and setters
        public boolean isnotExist() {
            return notExist;
        }
        public int getremainingChance() {
            return remainingChance;
        }
        public void setnotExist() {
            this.notExist = true;
        }

        public void onBrick() {
            this.remainingChance--;
            if (this.remainingChance == 0) {
                this.notExist = true;
            } else if (this.remainingChance == 1) {
                this.color = Color.PINK;
            } else if (this.remainingChance == 2) {
                this.color = Color.orange;
            }
        }

        public void draw(Graphics g) {
            Graphics2D G = (Graphics2D)g;
            if (!notExist) {
                G.setColor(color);
                G.fill(this);
                G.setColor(Color.WHITE);
                G.draw(this);
            }
        }

    }

    class Ball extends Ellipse2D.Double {

        private Color color = Color.pink;
        private double dx;
        private double dy;
        private double radius;


        public Ball(double x, double y, double radius) {
            super(x, y, radius*2, radius*2);
            this.radius = radius;
            this.dx = ball_speed*Math.cos(90);
            this.dy = -ball_speed*Math.sin(90);
        }

        // getters and setters
        public double getRadius() {
            return this.radius;
        }
        public double getDx() {
            return this.dx;
        }
        public double getDy() {
            return this.dy;
        }
        public void setX(double x) {
            this.x = x;
        }
        public void setY(double y) {
            this.y = y;
        }
        public void setDx(double dx) {
            this.dx = dx;
        }
        public void setDy(double dy) {
            this.dy = dy;
        }
        public void setRadius(double radius) {
            this.radius = radius;
        }

        public void draw(Graphics g) {
            Graphics2D G = (Graphics2D)g;
            G.setColor(this.color);
            G.fill(this);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Game game = new Game(args[0], args[1]);
            }
        });
    }
}

