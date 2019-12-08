import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class InGameController {

    private InGameView _view;
    private InGameData _data;
    private PawnClickListener leftPawnListener, rightPawnListener;
    private double YutResult;
    public InGameController() {

        _view = GameManager.getInstance().get_inGame();
        _data = GameManager.getInstance().get_gameData();

        leftPawnListener = new PawnClickListener();
        rightPawnListener = new PawnClickListener();

        _view.leftThrowBtn.addActionListener(new ThrowingYut());
        _view.rightThrowBtn.addActionListener(new ThrowingYut());

        _view.leftUserPanel.btnAbility1.addActionListener(new UseAbility());

        _data.previewMovedPawn.addMouseListener(new MoveSelectedPawn());

        init_Game();

        change_playerImgnLabel();
        ready(_data.activatedPlayer);
    }

    private class PawnClickListener implements MouseListener {

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {
            System.out.println(3333);
            _data.focusedPawn = (Pawn)e.getSource();
            _data.previewMovedPawn.setVisible(true);
            _data.previewMovedPawn.setIcon(new ImageIcon(_data.focusedPawn.ImgSource()));
            _data.findNextPoint();
        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }

    private class MoveSelectedPawn implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) { }

        @Override
        public void mousePressed(MouseEvent e) { }

        @Override
        public void mouseReleased(MouseEvent e) {
            if(_data.focusedPawn.getCurrentIndex()==0) _data.moveOnePawn(_data.activatedPlayer, _data.focusedPawn, _data.previewMovedPawn.getCurrentIndex());
            else _data.moveAllPawns(_data.activatedPlayer,_data.boardIndexer[_data.focusedPawn.getCurrentIndex()],_data.boardIndexer[_data.previewMovedPawn.getCurrentIndex()]);
            for(Pawn p:_data.activatedPlayer.pawns) p.removeMouseListener(_data.activatedPlayer==_data.leftPlayer ? leftPawnListener : rightPawnListener);
            _data.previewMovedPawn.setVisible(false);
            //finish turn(pass turn)
            switch(_data.throwResult){
                case 4:
                case 5:
                    ready(_data.activatedPlayer);
                    break;
                default:
                    _data.activatedPlayer.isMyTurn = false;
                    passPlayerTurn();
                    _data.activatedPlayer.isMyTurn = true;
            }

            for(Pawn p:_data.activatedPlayer.pawns) p.removeMouseListener(_data.activatedPlayer == _data.leftPlayer ? leftPawnListener : rightPawnListener);
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    private class ThrowingYut implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton btn = (JButton) e.getSource();
            if((_data.leftPlayer.isMyTurn && btn == _view.rightThrowBtn) || (_data.rightPlayer.isMyTurn && btn == _view.leftThrowBtn)) return;
            YutResult = Math.random();
            if(YutResult <= 0.1536)
                _data.throwResult = 1;
            else if(YutResult <= 0.4992)
                _data.throwResult = 2;
            else if(YutResult <= 0.7584)
                _data.throwResult = 3;
            else if (YutResult <= 0.8880)
                _data.throwResult = 4;
            else if (YutResult <= 0.9136)
                _data.throwResult = 5;
            else if (YutResult < 1)
                _data.throwResult = 6;

            _view.lblThrowing.start();
            btn.setEnabled(false);
            System.out.println(_data.throwResult);
            if(_data.throwResult == 6) {
                for(Pawn p:_data.activatedPlayer.pawns) {
                    if(p.isFinished() == false && p.getCurrentIndex() != 0){
                        for(Pawn P:_data.activatedPlayer.pawns) if(P.isFinished()==false) P.addMouseListener(_data.activatedPlayer==_data.leftPlayer ? leftPawnListener : rightPawnListener);
                        return;
                    }
                }
                passPlayerTurn();
            }
            for(Pawn P:_data.activatedPlayer.pawns) if(P.isFinished()==false) P.addMouseListener(_data.activatedPlayer==_data.leftPlayer ? leftPawnListener : rightPawnListener);
        }
    }
    private class UseAbility implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
           Object obj = e.getSource();
            if(obj ==_view.leftUserPanel.btnAbility2)
            {
                _data.leftPlayer.abilities[1].use();
                _view.repaint();
            }
        }
    }

    public void init_Game(){
        for(Pawn p:_data.leftPlayer.pawns)p.removeMouseListener(_data.activatedPlayer==_data.leftPlayer ? leftPawnListener : rightPawnListener);
        for(Pawn p:_data.rightPlayer.pawns)p.removeMouseListener(_data.activatedPlayer==_data.leftPlayer ? leftPawnListener : rightPawnListener);
        _view.leftThrowBtn.setEnabled(false);
        _view.rightThrowBtn.setEnabled(false);
    }


    public void change_playerImgnLabel(){
        //left player turn
        if(_data.activatedPlayer == _data.leftPlayer){
            _data.leftPlayer.imgPlayer.setIcon(_data.leftPlayer.iconPalyer[0]);
            _data.leftPlayer.imgPlayer.setBounds(-25,90,250,230);
            _data.rightPlayer.imgPlayer.setIcon(_data.rightPlayer.iconPalyer[1]);
            _data.rightPlayer.imgPlayer.setBounds(-1,90,250,230);

            _data.leftPlayer.lblTurn.setVisible(true);
            _data.rightPlayer.lblTurn.setVisible(false);
        }
        //right player turn
        else{
            _data.leftPlayer.imgPlayer.setIcon(_data.leftPlayer.iconPalyer[1]);
            _data.leftPlayer.imgPlayer.setBounds(-1,90,250,230);
            _data.rightPlayer.imgPlayer.setIcon(_data.rightPlayer.iconPalyer[0]);
            _data.rightPlayer.imgPlayer.setBounds(-25,90,250,230);

            _data.leftPlayer.lblTurn.setVisible(false);
            _data.rightPlayer.lblTurn.setVisible(true);
        }
    }

    public void ready(Player player){
        if(player == _data.leftPlayer) GameManager.getInstance().get_inGame().leftThrowBtn.setEnabled(true);
        else if(player == _data.rightPlayer) GameManager.getInstance().get_inGame().rightThrowBtn.setEnabled(true);
    }

    public void passPlayerTurn(){

        _data.activatedPlayer.isMyTurn = false;
        _data.activatedPlayer = _data.activatedPlayer == _data.leftPlayer ? _data.rightPlayer : _data.leftPlayer;
        _data.activatedPlayer.isMyTurn = true;
        change_playerImgnLabel();
        ready(_data.activatedPlayer);
    }

    public void activate(){

    }
}
