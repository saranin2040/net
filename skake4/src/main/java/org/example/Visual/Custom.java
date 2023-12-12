package org.example.Visual;

import org.example.BusinessLogic.BusinessLogic;
import org.example.BusinessLogic.GameData.Coords;
import org.example.BusinessLogic.GameData.Game;
import org.example.BusinessLogic.GameData.Snake.Snake;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Custom extends JPanel
{
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

                                if ((isMore(body.get(i).y, body.get(i + 1).y, game.getField().getHeight()) && isMore(body.get(i).x, body.get(i - 1).x, game.getField().getHeight()))
                                        || (isMore(body.get(i).y, body.get(i - 1).y, game.getField().getHeight()) && isMore(body.get(i).x, body.get(i + 1).x, game.getField().getHeight()))) {
                                    imagePath = "src/main/resources/bodyDR.png";
                                } else if ((isLess(body.get(i).y, body.get(i + 1).y, game.getField().getHeight()) && isMore(body.get(i).x, body.get(i - 1).x, game.getField().getHeight()))
                                        || (isLess(body.get(i).y, body.get(i - 1).y, game.getField().getHeight()) && isMore(body.get(i).x, body.get(i + 1).x, game.getField().getHeight()))) {
                                    imagePath = "src/main/resources/bodyUR.png";
                                } else if ((isLess(body.get(i).y, body.get(i + 1).y, game.getField().getHeight()) && isLess(body.get(i).x, body.get(i - 1).x, game.getField().getWidth()))
                                        || (isLess(body.get(i).y, body.get(i - 1).y, game.getField().getHeight()) && isLess(body.get(i).x, body.get(i + 1).x, game.getField().getWidth()))) {
                                    imagePath = "src/main/resources/bodyUL.png";
                                } else if ((isMore(body.get(i).y, body.get(i + 1).y, game.getField().getHeight()) && isLess(body.get(i).x, body.get(i - 1).x, game.getField().getWidth()))
                                        || (isMore(body.get(i).y, body.get(i - 1).y, game.getField().getHeight()) && isLess(body.get(i).x, body.get(i + 1).x, game.getField().getWidth()))) {
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

                                if ((isMore(body.get(i).y, body.get(i + 1).y, game.getField().getHeight()) && isMore(body.get(i).x, body.get(i - 1).x, game.getField().getHeight()))
                                        || (isMore(body.get(i).y, body.get(i - 1).y, game.getField().getHeight()) && isMore(body.get(i).x, body.get(i + 1).x, game.getField().getHeight()))) {
                                    imagePath = "src/main/resources/bodyDR_enemy.png";
                                } else if ((isLess(body.get(i).y, body.get(i + 1).y, game.getField().getHeight()) && isMore(body.get(i).x, body.get(i - 1).x, game.getField().getHeight()))
                                        || (isLess(body.get(i).y, body.get(i - 1).y, game.getField().getHeight()) && isMore(body.get(i).x, body.get(i + 1).x, game.getField().getHeight()))) {
                                    imagePath = "src/main/resources/bodyUR_enemy.png";
                                } else if ((isLess(body.get(i).y, body.get(i + 1).y, game.getField().getHeight()) && isLess(body.get(i).x, body.get(i - 1).x, game.getField().getWidth()))
                                        || (isLess(body.get(i).y, body.get(i - 1).y, game.getField().getHeight()) && isLess(body.get(i).x, body.get(i + 1).x, game.getField().getWidth()))) {
                                    imagePath = "src/main/resources/bodyUL_enemy.png";
                                } else if ((isMore(body.get(i).y, body.get(i + 1).y, game.getField().getHeight()) && isLess(body.get(i).x, body.get(i - 1).x, game.getField().getWidth()))
                                        || (isMore(body.get(i).y, body.get(i - 1).y, game.getField().getHeight()) && isLess(body.get(i).x, body.get(i + 1).x, game.getField().getWidth()))) {
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

    private boolean isMore(int x,int y,int size)
    {
        return (x-y>0 || x-y==-size) && x-y!=size;
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

    int width;
    int height;
    String[][] seedMatrix;
    public Game game;

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
