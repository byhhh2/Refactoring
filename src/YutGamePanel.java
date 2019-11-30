import javax.swing.*;
import java.awt.*;

public class InGameView extends JPanel {

    private UserPanel       user1Panel, user2Panel;
    private ImagePanel      BoardPanel, YutPanelView;
    private InGameData      _gameData;

    private JButton         leftYutThrowBtn, rightThrowBtn;

    public InGameView(){

        GameManager.getInstance().set_inGame(this);
        _gameData = GameManager.getInstance().get_gameData();

        setBounds(0,0,1000,800);
        setBackground(Color.white);
        setLayout(null);


        for(Pawn p:_gameData.leftPlayer.pawns) {
            this.add(p);

        }
        for(Pawn p:_gameData.rightPlayer.pawns) {
            this.add(p);
        }

        user1Panel = new UserPanel(_gameData.leftPlayer);
        user1Panel.setBounds(0,0,200,600);
        user1Panel.setVisible(true);
        add(user1Panel);

        user2Panel = new UserPanel(_gameData.rightPlayer);
        user2Panel.setBounds(800,0,200,600);
        add(user2Panel);

        BoardPanel = new ImagePanel(new ImageIcon("images/boardImage.png").getImage(), 600, 600, 200, 0);
        add(BoardPanel);

        leftYutThrowBtn = new JButton("윷 던지기");
        leftYutThrowBtn.setBounds(0,599, 200,200);
        leftYutThrowBtn.setLayout(null);
        add(leftYutThrowBtn);

        rightThrowBtn = new JButton("윷 던지기");
        rightThrowBtn.setBounds(800,599,200,200);
        rightThrowBtn.setLayout(null);

        add(rightThrowBtn);

        YutPanelView = new ImagePanel(new ImageIcon("").getImage(), 600,200,200,600);
        YutPanelView.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(YutPanelView);

        repaint();
    }//constructor

    /*public void paintComponent(Graphics page){

        super.paintComponent(page);


    }//paintComponent()*/

}//YutGamePanel()
