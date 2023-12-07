package org.example.Visual;

import org.example.BusinessLogic.BusinessLogic;
import org.example.BusinessLogic.GameData.Coords;
import org.example.BusinessLogic.GameData.Game.Game;
import org.example.BusinessLogic.GameData.Snake.Snake;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Custom extends JPanel
{
    //private final Timer  timer;
    public Game game;
    private int myId;
    public Custom(BusinessLogic bc){
        //setBackground(Color.BLACK);
        //this.game = game;
        //timer = new Timer(game.time,this);
        //timer.start();
//        addKeyListener(new ControllerKeys(bc));
////        setFocusable(true);
//        setFocusable(true);
//        requestFocus();



    }

    private String[][] generateRandomSeedMatrix(int rows, int cols) {
        String[][] matrix = new String[rows][cols];
        Random seedRandom = new Random();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = imagesField.get(seedRandom.nextInt(imagesField.size()));
            }
        }

        return matrix;
    }

    public void setGame(Game game)
    {
        if (sed==false) {
            width = getWidth() / game.getField().getWidth();
            height = getHeight() / game.getField().getHeight();
            seedMatrix = generateRandomSeedMatrix(game.getField().getWidth(),  game.getField().getHeight());
            sed=true;
        }



        width=getWidth()/game.getField().getWidth();
        height=getHeight()/game.getField().getHeight();
        this.game=game;
    }
    private void paintField(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        for(int i = 0; i < game.getField().getWidth(); i++)
        {
            for(int j = 0; j < game.getField().getHeight(); j++)
            {
                ImageIcon imageIcon = new ImageIcon(seedMatrix[i][j]);
                Image image = imageIcon.getImage();
                g2.drawImage(image, (int) (i * width), (int) (j * height), (int) (width), (int) (height), null);
            }
        }
    }
//    private void paintFoods(Graphics g){
//        Graphics2D g2 = (Graphics2D) g;
//        ArrayList<Coord> coords = game.getFood();
//        //System.out.println("-----------"+coords);
//        for (Coord c:coords){
//            Ellipse2D el = new Ellipse2D.Double(c.x+game.field.sizeBlock/4, c.y+game.field.sizeBlock/4, game.field.sizeBlock/2, game.field.sizeBlock/2);
//            g2.setColor(Color.RED);
//            g2.fill(el);
//            g2.draw(el);
//        }
//    }
    private void paintSnakes(Graphics g)
    {
        try {
            Graphics2D g2 = (Graphics2D) g;
            if (game != null) {
                ArrayList<Snake> snakes = game.getSnakes();
                for (Snake s : snakes) {
                    if (s.getId() == game.getMainPlayer().getId()) {
                        ArrayList<Coords> body = new ArrayList<>(s.getBody());
                        for (int i = 0; i < body.size(); i++) {

                            String imagePath = null;
                            if (i == body.size() - 1) {
                                switch (s.getDirect()) {
                                    case UP -> imagePath = "src/main/resources/headUP.png";
                                    case DOWN -> imagePath = "src/main/resources/headDOWN.png";
                                    case RIGHT -> imagePath = "src/main/resources/headRIGHT.png";
                                    case LEFT -> imagePath = "src/main/resources/headLEFT.png";
                                }
                                //imagePath = "src/main/resources/head.png";
                                ImageIcon imageIcon = new ImageIcon(imagePath);
                                Image image = imageIcon.getImage();
                                g2.drawImage(image, (int) (body.get(i).x * width), (int) (body.get(i).y * height), (int) (width), (int) (height), null);
                            } else if (i == 0) {
                                if ((body.get(i).y - body.get(i + 1).y > 0 || body.get(i).y - body.get(i + 1).y == -game.getField().getHeight() + 1) && body.get(i).y - body.get(i + 1).y != game.getField().getHeight() - 1 && body.get(i).x - body.get(i + 1).x == 0) {
                                    imagePath = "src/main/resources/tailUP.png";
                                } else if ((body.get(i).y - body.get(i + 1).y < 0 || body.get(i).y - body.get(i + 1).y == game.getField().getHeight() - 1) && body.get(i).y - body.get(i + 1).y != -game.getField().getHeight() + 1 && body.get(i).x - body.get(i + 1).x == 0) {
                                    imagePath = "src/main/resources/tailDOWN.png";
                                } else if (body.get(i).y - body.get(i + 1).y == 0 && body.get(i).x - body.get(i + 1).x != game.getField().getWidth() - 1 && (body.get(i).x - body.get(i + 1).x > 0 || body.get(i).x - body.get(i + 1).x == -game.getField().getWidth() + 1)) {
                                    imagePath = "src/main/resources/tailLEFT.png";
                                } else if (body.get(i).y - body.get(i + 1).y == 0 && body.get(i).x - body.get(i + 1).x != -game.getField().getWidth() + 1 && (body.get(i).x - body.get(i + 1).x < 0 || body.get(i).x - body.get(i + 1).x == game.getField().getWidth() - 1)) {
                                    imagePath = "src/main/resources/tailRIGHT.png";
                                }
                                ImageIcon imageIcon = new ImageIcon(imagePath);
                                Image image = imageIcon.getImage();

                                g2.drawImage(image, (int) (body.get(i).x * width), (int) (body.get(i).y * height), (int) width, (int) height, null);
                            } else {

                                if ((isMore(body.get(i).y, body.get(i + 1).y) && isMore(body.get(i).x, body.get(i - 1).x))
                                        || (isMore(body.get(i).y, body.get(i - 1).y) && isMore(body.get(i).x, body.get(i + 1).x))) {
                                    imagePath = "src/main/resources/bodyDR.png";
                                } else if ((isLess(body.get(i).y, body.get(i + 1).y, game.getField().getHeight()) && isMore(body.get(i).x, body.get(i - 1).x))
                                        || (isLess(body.get(i).y, body.get(i - 1).y, game.getField().getHeight()) && isMore(body.get(i).x, body.get(i + 1).x))) {
                                    imagePath = "src/main/resources/bodyUR.png";
                                } else if ((isLess(body.get(i).y, body.get(i + 1).y, game.getField().getHeight()) && isLess(body.get(i).x, body.get(i - 1).x, game.getField().getWidth()))
                                        || (isLess(body.get(i).y, body.get(i - 1).y, game.getField().getHeight()) && isLess(body.get(i).x, body.get(i + 1).x, game.getField().getWidth()))) {
                                    imagePath = "src/main/resources/bodyUL.png";
                                } else if ((isMore(body.get(i).y, body.get(i + 1).y) && isLess(body.get(i).x, body.get(i - 1).x, game.getField().getWidth()))
                                        || (isMore(body.get(i).y, body.get(i - 1).y) && isLess(body.get(i).x, body.get(i + 1).x, game.getField().getWidth()))) {
                                    imagePath = "src/main/resources/bodyDL.png";
                                } else if ((body.get(i).y - body.get(i + 1).y == 0 && body.get(i).x - body.get(i - 1).x != 0)
                                        || (body.get(i).y - body.get(i - 1).y == 0 && body.get(i).x - body.get(i + 1).x != 0)) {
                                    imagePath = "src/main/resources/bodyRIGHT.png";
                                } else if ((body.get(i).y - body.get(i + 1).y != 0 && body.get(i).x - body.get(i - 1).x == 0)
                                        || (body.get(i).y - body.get(i - 1).y != 0 && body.get(i).x - body.get(i + 1).x == 0)) {
                                    imagePath = "src/main/resources/bodyUP.png";
                                }
                                //imagePath = "src/main/resources/bodyUP.png";
                                ImageIcon imageIcon = new ImageIcon(imagePath);
                                Image image = imageIcon.getImage();

                                g2.drawImage(image, (int) (body.get(i).x * width), (int) (body.get(i).y * height), (int) width, (int) height, null);
                            }
                        }
                    } else {
                        ArrayList<Coords> body = new ArrayList<>(s.getBody());
                        for (int i = 0; i < body.size(); i++) {

                            String imagePath = null;
                            if (body.get(i).equals(s.getCoords())) {
                                switch (s.getDirect()) {
                                    case UP -> imagePath = "src/main/resources/headUP_enemy.png";
                                    case DOWN -> imagePath = "src/main/resources/headDOWN_enemy.png";
                                    case RIGHT -> imagePath = "src/main/resources/headRIGHT_enemy.png";
                                    case LEFT -> imagePath = "src/main/resources/headLEFT_enemy.png";
                                }
                                //imagePath = "src/main/resources/head.png";
                                ImageIcon imageIcon = new ImageIcon(imagePath);
                                Image image = imageIcon.getImage();
                                g2.drawImage(image, (int) (body.get(i).x * width), (int) (body.get(i).y * height), (int) (width), (int) (height), null);
                            } else if (i == 0) {
                                if ((body.get(i).y - body.get(i + 1).y > 0 || body.get(i).y - body.get(i + 1).y == -game.getField().getHeight() + 1) && body.get(i).y - body.get(i + 1).y != game.getField().getHeight() - 1 && body.get(i).x - body.get(i + 1).x == 0) {
                                    imagePath = "src/main/resources/tailUP_enemy.png";
                                } else if ((body.get(i).y - body.get(i + 1).y < 0 || body.get(i).y - body.get(i + 1).y == game.getField().getHeight() - 1) && body.get(i).y - body.get(i + 1).y != -game.getField().getHeight() + 1 && body.get(i).x - body.get(i + 1).x == 0) {
                                    imagePath = "src/main/resources/tailDOWN_enemy.png";
                                } else if (body.get(i).y - body.get(i + 1).y == 0 && body.get(i).x - body.get(i + 1).x != game.getField().getWidth() - 1 && (body.get(i).x - body.get(i + 1).x > 0 || body.get(i).x - body.get(i + 1).x == -game.getField().getWidth() + 1)) {
                                    imagePath = "src/main/resources/tailLEFT_enemy.png";
                                } else if (body.get(i).y - body.get(i + 1).y == 0 && body.get(i).x - body.get(i + 1).x != -game.getField().getWidth() + 1 && (body.get(i).x - body.get(i + 1).x < 0 || body.get(i).x - body.get(i + 1).x == game.getField().getWidth() - 1)) {
                                    imagePath = "src/main/resources/tailRIGHT_enemy.png";
                                }
                                ImageIcon imageIcon = new ImageIcon(imagePath);
                                Image image = imageIcon.getImage();

                                g2.drawImage(image, (int) (body.get(i).x * width), (int) (body.get(i).y * height), (int) width, (int) height, null);
                            } else {

                                if ((isMore(body.get(i).y, body.get(i + 1).y) && isMore(body.get(i).x, body.get(i - 1).x))
                                        || (isMore(body.get(i).y, body.get(i - 1).y) && isMore(body.get(i).x, body.get(i + 1).x))) {
                                    imagePath = "src/main/resources/bodyDR_enemy.png";
                                } else if ((isLess(body.get(i).y, body.get(i + 1).y, game.getField().getHeight()) && isMore(body.get(i).x, body.get(i - 1).x))
                                        || (isLess(body.get(i).y, body.get(i - 1).y, game.getField().getHeight()) && isMore(body.get(i).x, body.get(i + 1).x))) {
                                    imagePath = "src/main/resources/bodyUR_enemy.png";
                                } else if ((isLess(body.get(i).y, body.get(i + 1).y, game.getField().getHeight()) && isLess(body.get(i).x, body.get(i - 1).x, game.getField().getWidth()))
                                        || (isLess(body.get(i).y, body.get(i - 1).y, game.getField().getHeight()) && isLess(body.get(i).x, body.get(i + 1).x, game.getField().getWidth()))) {
                                    imagePath = "src/main/resources/bodyUL_enemy.png";
                                } else if ((isMore(body.get(i).y, body.get(i + 1).y) && isLess(body.get(i).x, body.get(i - 1).x, game.getField().getWidth()))
                                        || (isMore(body.get(i).y, body.get(i - 1).y) && isLess(body.get(i).x, body.get(i + 1).x, game.getField().getWidth()))) {
                                    imagePath = "src/main/resources/bodyDL_enemy.png";
                                } else if ((body.get(i).y - body.get(i + 1).y == 0 && body.get(i).x - body.get(i - 1).x != 0)
                                        || (body.get(i).y - body.get(i - 1).y == 0 && body.get(i).x - body.get(i + 1).x != 0)) {
                                    imagePath = "src/main/resources/bodyRIGHT_enemy.png";
                                } else if ((body.get(i).y - body.get(i + 1).y != 0 && body.get(i).x - body.get(i - 1).x == 0)
                                        || (body.get(i).y - body.get(i - 1).y != 0 && body.get(i).x - body.get(i + 1).x == 0)) {
                                    imagePath = "src/main/resources/bodyUP_enemy.png";
                                }
                                //imagePath = "src/main/resources/bodyUP.png";
                                ImageIcon imageIcon = new ImageIcon(imagePath);
                                Image image = imageIcon.getImage();

                                g2.drawImage(image, (int) (body.get(i).x * width), (int) (body.get(i).y * height), (int) width, (int) height, null);
                            }
                        }
                    }
                }
            }
        }
        catch (IndexOutOfBoundsException e)
        {
            System.err.println("couldnt paint");
        }
    }

    private void paintFoods(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        if (game!=null)
        {
            ArrayList<Coords> snakes = game.getField().getFoods();
                for (Coords p : snakes) {

                    String imagePath = "src/main/resources/apple.png";
                    ImageIcon imageIcon = new ImageIcon(imagePath);

                    Image image = imageIcon.getImage();

                    g2.drawImage(image, (int) (p.x * width), (int) (p.y * height), (int) width, (int) height, null);
                }
        }
    }

    private boolean isMore(int x,int y)
    {
        return (x-y>0 || x-y==-9) && x-y!=9;
    }

    private boolean isLess(int x,int y,int size)
    {
        return (x-y<0 || x-y==size-1) && x-y!=-size+1;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintField(g);
        paintFoods(g);
        paintSnakes(g);
    }

//    @Override
//    public void actionPerformed(ActionEvent e) {
////        if (new Random().nextInt(2)==0){
////            HashMap<Integer, Direction> f=new HashMap<>();
////            switch (new Random().nextInt(4)){
////                case (0)->f.put(1, Direction.UP);
////                case (1)->f.put(1, Direction.DOWN);
////                case (2)->f.put(1, Direction.RIGHT);
////                case (3)->f.put(1, Direction.LEFT);
////            }
////
////            game.go(f);
////        }else
//        game.go(new HashMap<>());
//        game.checkDied();
//        game.checkFood();
//        repaint();
//    }
//    public class FieldKeyListener extends KeyAdapter {
//        @Override
//        public void keyPressed(KeyEvent e) {
//            super.keyPressed(e);
//            int key = e.getKeyCode();
//            if(key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT){
//                game.getSnake().get(myId).changeDirection(Direction.valueOf(SnakesProto.Direction.LEFT.toString()));
//            }
//            if(key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT){
//                game.getSnake().get(myId).changeDirection(Direction.valueOf(SnakesProto.Direction.RIGHT.toString()));
//            }
//            if(key == KeyEvent.VK_W || key == KeyEvent.VK_UP){
//                game.getSnake().get(myId).changeDirection(Direction.valueOf(SnakesProto.Direction.UP.toString()));
//            }
//            if(key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN){
//                game.getSnake().get(myId).changeDirection(Direction.valueOf(SnakesProto.Direction.DOWN.toString()));
//            }
//            if(key == KeyEvent.VK_ESCAPE){
//                System.exit(1);
//            }
//        }
//    }

    int width;
    int height;
    String[][] seedMatrix;

    ArrayList<String> imagesField=new ArrayList<>()
    {{
        add("src/main/resources/field11.png");
        add("src/main/resources/field12.jpg");
        add("src/main/resources/field12.jpg");
        add("src/main/resources/field12.jpg");
        add("src/main/resources/field12.jpg");
        add("src/main/resources/field12.jpg");
        add("src/main/resources/field12.jpg");
        add("src/main/resources/field12.jpg");
        add("src/main/resources/field12.jpg");
        add("src/main/resources/field12.jpg");
        add("src/main/resources/field12.jpg");
        add("src/main/resources/field12.jpg");
        add("src/main/resources/field12.jpg");
        add("src/main/resources/field12.jpg");
        add("src/main/resources/field12.jpg");
        add("src/main/resources/field12.jpg");
        add("src/main/resources/field12.jpg");
        add("src/main/resources/field12.jpg");
        add("src/main/resources/field12.jpg");
        add("src/main/resources/field12.jpg");
        add("src/main/resources/field12.jpg");
        add("src/main/resources/field12.jpg");
        add("src/main/resources/field12.jpg");
        add("src/main/resources/field13.png");
        add("src/main/resources/field14.png");
        add("src/main/resources/field14.png");
    }};

    boolean sed=false;
}
