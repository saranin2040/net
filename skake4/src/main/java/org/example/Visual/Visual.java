package org.example.Visual;

import org.example.BusinessLogic.*;
import org.example.BusinessLogic.GameData.Coords;
import org.example.BusinessLogic.GameData.Game.Game;
import org.example.BusinessLogic.Network.Data.DataGameAnnouncement;
import org.example.BusinessLogic.GameData.Player.Player;
import org.example.Controller.ControllerKeys;
import org.example.Controller.JoinController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Scanner;

public class Visual extends JFrame {

    public Visual (BusinessLogic bc)
    {
        this.bc=bc;

        setTitle("Grid Coloring App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 800);
        setLocationRelativeTo(null);

        if (bc.getGame() != null) {
            height = bc.getGame().getField().getHeight();
            width = bc.getGame().getField().getWidth();
        }

        //gridPanel=new Custom(bc);
        gridPanel=new StartPanel();
        //gridPanel = new Custom(new GridLayout(bc.getGame().getField().getWidth(), bc.getGame().getField().getHeight()));
        //gridPanel = new JPanel(new GridLayout(bc.getGame().getField().getHeight(),bc.getGame().getField().getWidth()));
        //gridPanel.setBackground(Color.BLACK);
        gridPanel.setPreferredSize(new Dimension(900, 100));






        // Создаем панель для размещения playersScrollPane и заголовка
        playersPanel=createList("Player list");
        gameInfoPanel=createList("Game inf");


//        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, gridPanel, containerPanel);
//        splitPane.setResizeWeight(1.0);




        // Создаем таблицу
        gameTableModel = new DefaultTableModel();
        gameTableModel.addColumn("Game name");
        gameTableModel.addColumn("Master");
        gameTableModel.addColumn("#");
        gameTableModel.addColumn("Size");
        gameTableModel.addColumn("Foods");
        gameTableModel.addColumn("");

        JTable gameTable = new JTable(gameTableModel);

        gameTable.getColumnModel().getColumn(gameTable.getColumnModel().getColumnIndex("Size")).setPreferredWidth(50);
        gameTable.getColumnModel().getColumn(gameTable.getColumnModel().getColumnIndex("Foods")).setPreferredWidth(50);
        gameTable.getColumnModel().getColumn(gameTable.getColumnModel().getColumnIndex("Game name")).setPreferredWidth(100);
        gameTable.getColumnModel().getColumn(gameTable.getColumnModel().getColumnIndex("Master")).setPreferredWidth(200);
        gameTable.getColumnModel().getColumn(gameTable.getColumnModel().getColumnIndex("#")).setPreferredWidth(20);

        TableColumn masterColumn = gameTable.getColumnModel().getColumn(5);
        masterColumn.setCellRenderer(new ButtonRenderer());
        masterColumn.setCellEditor(new ButtonEditor(new JTextField(),bc));

        JScrollPane tableScrollPane = new JScrollPane(gameTable);
        tableScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        // Создаем панель для размещения таблицы и заголовка
        JPanel tablePanel = new JPanel(new BorderLayout());
        JLabel tableTitleLabel = new JLabel("Found games");
        Font tableTitleFont = tableTitleLabel.getFont();
        tableTitleLabel.setFont(new Font(tableTitleFont.getName(), Font.PLAIN, SIZE_TEXT));
        tablePanel.add(tableTitleLabel, BorderLayout.NORTH);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);



        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout()); // Выберите подходящий менеджер компоновки

// Создайте кнопки
        JButton createGameButton = new JButton("<html><div style='text-align: center;'>" +
                                  "<div style='padding-top: 20px;'>Create</div>" +
                                    "<div style='padding-bottom: 20px;'>New Game</div>" +
                                    "</div></html>");

        JButton exitButton = new JButton("Exit");
        JButton playerNameButton = new JButton("<html><div style='text-align: center;'>" +
                "<div style='padding-top: 20px;'>Change</div>" +
                "<div style='padding-bottom: 20px;'>Player</div>" +
                "<div style='padding-bottom: 20px;'>Name</div>" +
                "</div></html>");

// Добавьте обработчики событий для кнопок
        createGameButton.addActionListener(e -> {
            bc.setStatusBuilding();
        });

        playerNameButton.addActionListener(e -> {
            if (bc.getStatus()==StatusGame.NONE) {
                System.out.println("enter player name:");
                Scanner scanner = new Scanner(System.in);
                String str = scanner.nextLine();

                bc.setPlayerName(str);
                System.out.println("change name: "+bc.getPlayerName());
            }
        });

        exitButton.addActionListener(e -> {
            // Действия при нажатии "Exit"
            bc.exit();
            //System.exit(0); // Выход из программы
        });

        Font buttonFont = new Font("Arial", Font.PLAIN, 46); // мя шрифта, стиль (PLAIN, BOLD, ITALIC), размер
        createGameButton.setFont(buttonFont);
        exitButton.setFont(buttonFont);
        playerNameButton.setFont(new Font("Arial", Font.PLAIN, 10));

        createGameButton.setPreferredSize(new Dimension(300, 150)); // Ширина 150, высота 50
        exitButton.setPreferredSize(new Dimension(150, 150));
        playerNameButton.setPreferredSize(new Dimension(20, 150));

// Добавьте кнопки на панель
        buttonPanel.add(createGameButton);
        buttonPanel.add(exitButton);
        buttonPanel.add(playerNameButton);


        JSplitPane list = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, playersPanel.container, gameInfoPanel.container);
        list.setResizeWeight(0.5);

        JSplitPane mainRightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, list, tablePanel);
        mainRightSplitPane.setResizeWeight(1.0);

        JPanel finalPanel = new JPanel(new BorderLayout());
        finalPanel.add(mainRightSplitPane, BorderLayout.CENTER);
        finalPanel.add(buttonPanel, BorderLayout.SOUTH);

          mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, gridPanel, finalPanel);
        mainSplitPane.setResizeWeight(0.75);

        add(mainSplitPane);
        //gridPanel = new JPanel();

        //add(gridPanel);

        requestFocus();

        addKeyListener(new ControllerKeys(bc));

        setFocusable(true);
        requestFocus();
        setLocationRelativeTo(null);

        timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                update2(bc.getGame());
                updateGameTable(bc.getListFoundGame());
            }
        });
        timer.start();
    }



    public void update2(Game game)
    {
        //gridPanel.setGame(game);

        if (bc.getStatus()==StatusGame.PLAY)
        {
            requestFocus();
            if (gridPanel instanceof Custom)
            {
                Custom custom = (Custom) gridPanel;
                custom.setGame(game);
            }
            else
            {
                gridPanel=new Custom(bc);
                gridPanel.setPreferredSize(new Dimension(900, 100));
                mainSplitPane.setLeftComponent(gridPanel);
                Custom custom = (Custom) gridPanel;
                custom.setGame(game);

            }


        }
        else if (bc.getStatus()==StatusGame.BUILDING)
        {
            if (!(gridPanel instanceof CreatePanel))
            {
                gridPanel=new CreatePanel(bc);
                gridPanel.setPreferredSize(new Dimension(9, 0));
                mainSplitPane.setLeftComponent(gridPanel);
            }
        }
        else if (bc.getStatus()==StatusGame.JOINING)
        {
            if (!(gridPanel instanceof JoiningPanel))
            {


                gridPanel=new JoiningPanel();
                mainSplitPane.setLeftComponent(gridPanel);
            }
        } else if (bc.getStatus()==StatusGame.NONE)
        {
            if (!(gridPanel instanceof StartPanel))
            {
                int x=gridPanel.getWidth();
                int y = gridPanel.getHeight();
                gridPanel=new StartPanel()
                ; gridPanel.setPreferredSize(new Dimension(x, y));
                mainSplitPane.setLeftComponent(gridPanel);
            }
        }

        if (game!=null) {
            height = game.getField().getHeight();
            width = game.getField().getWidth();
            playersPanel.list.removeAll();
            gameInfoPanel.list.removeAll();

            paintPlayers(game.getPlayersRate());
            paintGameInf(game);
        }



        gridPanel.revalidate();
        gridPanel.repaint();

        revalidate();
        repaint();
    }

//    @Override
//    public void update(Graphics g) {
//        gr
//    }

    private void paintPlayers(ArrayList<Player> players)
    {
        int i=1;
        for (Player player : players)
        {
//            JLabel playerLabel = new JLabel();
//            Font labelFont = playerLabel.getFont();
//            playerLabel.setFont(new Font(labelFont.getName(), Font.PLAIN, 24)); // Установка размера шрифта 14
            playersPanel.addString(i+". " + player.getName() + " " + player.getScore());
            i++;
        }
    }

    private void paintGameInf(Game game)
    {
        gameInfoPanel.addString("Master: "+game.getMasterPlayer().getName());
        gameInfoPanel.addString("Size: "+game.getField().getWidth()+"x"+game.getField().getHeight());
        gameInfoPanel.addString("Count food: "+game.getField().getFoods().size());
    }

    public  void updateGameTable(ArrayList<DataGameAnnouncement> listGames)
    {
        // Очищаем данные в таблице
        gameTableModel.setRowCount(0);

//        gameTableModel.addRow(new Object[]{
//                "lol",
//                "listGame.getMasterName()",
//                "listGame.getplayers().size()",
//               " listGame.getWidth() +  + listGame.getHeight()",
//               " listGame.getCountFood()"
//        });


        for (DataGameAnnouncement listGame:listGames) {
            gameTableModel.addRow(new Object[]{
                    listGame.getGameName(),
                    listGame.getMasterName()+" ["+listGame.getIp()+"]",
                    listGame.getplayers().size(),
                    listGame.getWidth() + "x" + listGame.getHeight(),
                    listGame.getCountFood()
            });
        }
    }

    private boolean containsCoords(int x, int y,ArrayList<Coords> coords) {
        for (Coords coord : coords) {
            if (coord.x == x && coord.y == y) {
                return true;
            }
        }
        return false;
    }

    private ListView createList(String nameList)
    {
        JPanel playersPanel = new JPanel();
        playersPanel.setLayout(new BoxLayout(playersPanel, BoxLayout.Y_AXIS));
        JScrollPane playersScrollPane = new JScrollPane(playersPanel);
        playersScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        playersPanel.setPreferredSize(new Dimension(200, 0));

        JPanel containerPanel = new JPanel(new BorderLayout());
        //containerPanel.add(new JLabel("Player list:"), BorderLayout.NORTH);

        JLabel titleLabel = new JLabel(nameList);
        Font titleFont = titleLabel.getFont();
        titleLabel.setFont(new Font(titleFont.getName(), Font.PLAIN, SIZE_TEXT)); // Установка размера шрифта 16
        containerPanel.add(titleLabel, BorderLayout.NORTH);

        containerPanel.add(playersScrollPane, BorderLayout.CENTER);
        return new ListView(playersPanel,containerPanel);
    }




    static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            setText("Join");
            return this;
        }
    }

    static class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String cellValue;

        public ButtonEditor(JTextField textField,BusinessLogic bc)
        {
            super(textField);

            button = new JButton();
            joinController=new JoinController(bc);
            button.addActionListener(joinController);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
        {
            joinController.setInf((String) table.getValueAt(row, 1),(String) table.getValueAt(row, 0));
            //button.requestFocusInWindow();
            //joinController.setInf(String.valueOf(row)+" "+String.valueOf(column));
            //cellValue = (String) table.getValueAt(2, column);
            return button;
        }

        JoinController joinController;
    }

    //JPanel gridPanel;
    int width;
    int height;
    int squareSize=20;
    private ListView playersPanel;
    private ListView gameInfoPanel;
    //private JScrollPane playersScrollPane;
    DefaultTableModel gameTableModel;
    BusinessLogic bc;

    private final int SIZE_TEXT=25;
    //private Custom gridPanel;
    private JPanel gridPanel;

    private Timer timer;

    JSplitPane mainSplitPane;
}