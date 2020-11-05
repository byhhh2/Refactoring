import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Scanner;

public class InGameController {

    private InGameView _view;
    private InGameData _data;
    private PawnClickListener leftPawnListener, rightPawnListener;
    private double YutResult;

    public InGameController() {

        _view = GameManager.getInstance().get_inGame();         // InGameView 받아오기
        _data = GameManager.getInstance().get_gameData();     //   데이터 받아오기


        //각 플레이어의 폰 선택 리스터 생성
        leftPawnListener = new PawnClickListener();
        rightPawnListener = new PawnClickListener();


        //던지기 버튼에 리스너 넣기
        _view.leftThrowBtn.addActionListener(new ThrowingYut());
        _view.rightThrowBtn.addActionListener(new ThrowingYut());


        //능력 버튼에 리스너 넣기
        _view.leftUserPanel.btnAbility2.addActionListener(new UseAbility());
        _view.leftUserPanel.btnAbility1.addActionListener(new UseAbility());

        _view.rightUserPanel.btnAbility1.addActionListener(new UseAbility());
        _view.rightUserPanel.btnAbility2.addActionListener(new UseAbility());

        //게임 초기화하며 시작
        init_Game();
        change_playerImgnLabel();
        ready(_data.activatedPlayer);

    }

    private class PawnClickListener implements MouseListener {  //이동할 말을 선택하는 과정에서 플레이어의 말 클릭 시 이벤트 발생

        @Override
        public void mouseReleased(MouseEvent e) {
            _data.focusedPawn = (Pawn)e.getSource();    //이벤트가 발생한 말을 선택된 말로 설정

            for(ThrowData data:_data.previewPawns){ //선택된 말에 대해 이동 가능한 위치를 보여주기 위해 저장된 윷 결과 데이터에 있는 미리보기 말 초기화
                data.preview.setVisible(false); //이전에 보여지고있던 미리보기가 있는 경우에 대비해 미리보기 말들 모두 안보이게 하기
            }
            showAllPreviews();  //현재 선택된 말을 기준으로 다시 미리보기 띄우기
            _view.repaint();
        }


        @Override
        public void mousePressed(MouseEvent e) {}
        @Override
        public void mouseClicked(MouseEvent e) {}
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
    }

    private class MoveSelectedPawn implements MouseListener {   //플레이어의 말을 클릭했을 때 보여진 미리보기 말을 클락하면 발생하는 이벤트 리스너

        @Override
        public void mouseReleased(MouseEvent e) {
            Pawn p = (Pawn)e.getSource();
            ThrowData clicked = null;
            boolean catched;


            for(ThrowData data:_data.previewPawns){ //결과 데이터에서 이벤트가 발생한 미리보기 말 찾기
                if(data.preview == p) clicked = data;
            }

            //말 이동
            if(_data.focusedPawn.getCurrentIndex()==0) catched = _data.moveOnePawn(_data.activatedPlayer, _data.focusedPawn, p.getCurrentIndex());  //대기칸에 있는 말을 이동하는 경우 그 말만 이동
            else catched = _data.moveAllPawns(_data.activatedPlayer,_data.focusedPawn.getCurrentIndex(),p.getCurrentIndex());   //윷판 위의 말을 이동하는 경우 그 말이 있는 위치의 모든 말을 이동
            for(ThrowData data:_data.previewPawns) data.preview.setVisible(false);  //말 이동 후 미리보기 모두 보이지 않게 하기


            _data.previewPawns.remove(clicked); //결과 데이터의 리스트에서 방금 사용된 결과 데이터를 삭제
            _data.previewPawns.trimToSize();    //리스트 사이즈 갱신

            //현재 플레이어의 말이 전부 완주하면 game end -> 대화상자
            if(_data.activatedPlayer.score ==4){    //말 4개가 모두 완주
                String str;
                //승자에 따라 메시지 설정
                if(_data.activatedPlayer== _data.leftPlayer) str="Left Player Win!! \n Do you want new Game?" ;
                else str= "Right Player Win!! \n Do you want new Game?";

                JOptionPane dialog = new JOptionPane();
                int result = JOptionPane.showConfirmDialog( _view, str,"Game End",JOptionPane.YES_NO_OPTION);   //게임을 계속 진행할 지  묻기
                switch(result) {
                    case JOptionPane.NO_OPTION:
                        InputInfo frame = new InputInfo();

                        frame.setTitle("Input ID");
                        frame.setLocation(0,0);
                        frame.setSize(1000,800);
                        frame.setVisible(true);
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        frame.setResizable(true);

                        break;
                    case JOptionPane.YES_OPTION:
                        InputInfo frame1 = new InputInfo();

                        frame1.setTitle("Input ID");
                        frame1.setLocation(0,0);
                        frame1.setSize(1000,800);
                        frame1.setVisible(true);
                        frame1.setResizable(true);

                    default:
                        for(Pawn pawn:_data.activatedPlayer.pawns) pawn.removeMouseListener(_data.activatedPlayer == _data.leftPlayer ? leftPawnListener : rightPawnListener);

                        if( _data.activatedPlayer == _data.rightPlayer) passPlayerTurn();
                        ready(_data.leftPlayer);
                        _data.InGameData_init();
                        _data.previewPawns.clear();
                        _data.previewPawns.trimToSize();

                        _view.lblThrowing.setIcon(_view.lblThrowing.iconYut[0]);
                        _view.lblYutResult.setIcon(_data.iconYutText[6]);

                        GameManager.getInstance().get_view().showMenu();
                        return;
                } // switch
            }

            //게임이 계속 진행되는 경우

            if(catched) {   //말 이동 후 상대 말을 잡으면 윷을 던질 기회 획득
                ready(_data.activatedPlayer);   //윷 던질 준비 시키기
                for(Pawn pawn:_data.activatedPlayer.pawns) pawn.removeMouseListener(_data.activatedPlayer == _data.leftPlayer ? leftPawnListener : rightPawnListener);  //윷을 던지는 동안에는 말 이동시키지 못하도록 리스너 제거
            }
            else if(_data.previewPawns.size()==0) { //추가 턴을 획득하지 못하고 던진 윷 결과데이터들을 모두 사용한 경우
                for(Pawn pawn:_data.activatedPlayer.pawns) pawn.removeMouseListener(_data.activatedPlayer == _data.leftPlayer ? leftPawnListener : rightPawnListener);  //말 선택 리스너 모두 제거
                passPlayerTurn();   //상대에게 턴 넘겨주기
            }
            else {  //아직 이동할 결과데이터가 남아있는 경우
                boolean flag = true;

                //빽도 예외처리
                for(ThrowData data:_data.previewPawns) {    //남은 결과값들 중 빽도가 아닌 것이 있는지 확인
                    if(data.result != 6) flag = false;
                }
                if(flag == true){   //결과 데이터들이 모두 빽도인 경우
                    for(Pawn pawn:_data.activatedPlayer.pawns){ //움직일 수 있는 말이 있는지 확인
                        if(pawn.getCurrentIndex()!=0) flag = false;
                    }
                    if(flag == true){   //게임판에 올라온 말이 없는 경우(빽도 이동이 가능한 말이 없는 경우)
                        //더이상 이동할 수 없으므로 턴 넘겨주기
                        for(Pawn pawn:_data.activatedPlayer.pawns) pawn.removeMouseListener(_data.activatedPlayer == _data.leftPlayer ? leftPawnListener : rightPawnListener);
                        passPlayerTurn();
                        _data.previewPawns.clear();
                        _data.previewPawns.trimToSize();
                    }   //if
                }//if
            }//else
        }

        @Override
        public void mouseEntered(MouseEvent e) { }
        @Override
        public void mouseExited(MouseEvent e) { }
        @Override
        public void mouseClicked(MouseEvent e) { }
        @Override
        public void mousePressed(MouseEvent e) { }
    }

    private class ThrowingYut implements ActionListener {   //윷 던지기 버튼을 누르면 발생하는 이벤트 리스너
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton btn = (JButton) e.getSource();
            //본인 턴이 아닐 때 버튼이 눌리면 무시
            if ((_data.leftPlayer.isMyTurn && btn == _view.rightThrowBtn) || (_data.rightPlayer.isMyTurn && btn == _view.leftThrowBtn))
                return;

            if(_data.activatedPlayer.isNowAbility1Use==true)    //윷 결과 조작 능력 사용
                _data.activatedPlayer.isNowAbility1Use=false;

            else{   //윷 결과 랜덤으로 뽑기
                YutResult = Math.random();
                if (YutResult <= 0.1536)
                    _data.throwResult =1;
                else if (YutResult <= 0.4992)
                    _data.throwResult = 2;
                else if (YutResult <= 0.7584)
                    _data.throwResult = 3;
                else if (YutResult <= 0.8880)
                    _data.throwResult = 4;
                else if (YutResult <= 0.9136)
                    _data.throwResult = 5;
                else if (YutResult < 1)
                    _data.throwResult = 6;
            }

            _view.lblThrowing.start();  //윷 던지는 모습 보여주기
            btn.setEnabled(false);

            _view.lblThrowing.setResult(_data.throwResult); //Yut으로 결과값보내서 결과이미지 띄우기

            if (_data.throwResult != 4 && _data.throwResult != 5)   //윷이나 모가 나온 경우
                _data.throwableNCnt--;  //던질 수 있는 횟수 -1

            //던진 결과 저장
            ThrowData data = new ThrowData(_data.throwResult);
            data.preview.addMouseListener(new MoveSelectedPawn());
            _data.previewPawns.add(data);
            _view.add(data.preview);
            _view.setComponentZOrder(data.preview, 0);  //윷판보다 위에 보이도록 설정

            //윷 던질 기회 모두 사용한 경우
            if (_data.throwableNCnt == 0) {
                //우선 빽도 예외처리
                boolean flag = true;
                for(ThrowData d:_data.previewPawns) {   //결과 데이터들 중 빽도가 아닌 것이 있는 지 확인
                    if(d.result != 6) flag = false;
                }
                if(flag == true){   //결과들이 모두 빽도인 경우 말을 이동할 준비
                    for(Pawn pawn:_data.activatedPlayer.pawns){ //내 말 중 이동 가능한 말이 있는 지 확인
                        if(pawn.getCurrentIndex()!=0) flag = false;
                    }
                    if(flag == true){   //윷판에 올라온 말이 없는 경우(빽도 이동이 가능한 말이 없는 경우)
                        //상대 턴으로 넘어가기
                        for(Pawn pawn:_data.activatedPlayer.pawns) pawn.removeMouseListener(_data.activatedPlayer == _data.leftPlayer ? leftPawnListener : rightPawnListener);
                        passPlayerTurn();
                        _data.previewPawns.clear();
                        _data.previewPawns.trimToSize();
                    }
                    else{   //이동할 말이 있다면 말 이동을 위한 준비
                        //내 말 중 완주하지 않은 말에 말 선택 리스너를 add
                        for (Pawn P : _data.activatedPlayer.pawns)
                            if (P.isFinished() == false)
                                P.addMouseListener(_data.activatedPlayer == _data.leftPlayer ? leftPawnListener : rightPawnListener);
                    }
                }
                else {
                    //말 이동 준비
                    //내 말 중 완주하지 않은 말에 말 선택 리스너를  add
                    for (Pawn P : _data.activatedPlayer.pawns) {
                        if (P.isFinished() == false) {
                            P.addMouseListener(_data.activatedPlayer == _data.leftPlayer ? leftPawnListener : rightPawnListener);
                        }   //if
                    }   //for
                }   //else
            }   //if(다 던짐)
            else {  //던질 기회가 남았다면 다시 던질 준비
                ready(_data.activatedPlayer);
            }   //else
        }//actionPerformed()
    }

    //능력 버튼 클릭 시 발생하는 이벤트 리스너
    private class UseAbility implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
           Object obj = e.getSource();
            //어느 플레이어가 어떤 버튼을 눌렀는지 확인 후 능력 사용
            if(obj ==_view.leftUserPanel.btnAbility1)
            {
                _data.leftPlayer.abilities[0].run(_data.leftPlayer);
            }
            else if(obj ==_view.leftUserPanel.btnAbility2)
            {
                _data.leftPlayer.abilities[1].run(_data.leftPlayer);
            }
            else if(obj ==_view.rightUserPanel.btnAbility1)
            {
                _data.rightPlayer.abilities[0].run(_data.rightPlayer);
            }
            else if(obj ==_view.rightUserPanel.btnAbility2)
            {
                _data.rightPlayer.abilities[1].run(_data.rightPlayer);
            }
            _view.repaint();
        }
    }

    public void init_Game(){    //게임 초기화 메소드
        //양쪽 플레이어의 말에 add된 리스너 제거
        for(Pawn p:_data.leftPlayer.pawns)p.removeMouseListener(_data.activatedPlayer==_data.leftPlayer ? leftPawnListener : rightPawnListener);
        for(Pawn p:_data.rightPlayer.pawns)p.removeMouseListener(_data.activatedPlayer==_data.leftPlayer ? leftPawnListener : rightPawnListener);

        //양 플레이어의 던지기 버튼 비활성화
        _view.leftThrowBtn.setEnabled(false);
        _view.rightThrowBtn.setEnabled(false);
    }//init_Game()


    public void change_playerImgnLabel(){   //진행중인 차례에 맞게 플레이어 이미지 바꾸는 메소드
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

    public void ready(Player player){   //던지기 버튼을 누를 수 있도록 활성화 하는 메소드
        if(player == _data.leftPlayer) GameManager.getInstance().get_inGame().leftThrowBtn.setEnabled(true);
        else if(player == _data.rightPlayer) GameManager.getInstance().get_inGame().rightThrowBtn.setEnabled(true);
        _data.throwableNCnt = 1;
    }

    public void passPlayerTurn(){   //현재 진행중인 턴을 상대 차례로 넘기는 메소드

        _data.activatedPlayer.isMyTurn = false;
        _data.activatedPlayer = _data.activatedPlayer == _data.leftPlayer ? _data.rightPlayer : _data.leftPlayer;
        _data.activatedPlayer.isMyTurn = true;

        _view.lblYutResult.setIcon(_data.iconYutText[6]);   //윷 결과 지우기

        change_playerImgnLabel();   //플레이어 이미지와 라벨 변경
        ready(_data.activatedPlayer);   //던지기 버튼 활성화
    }

    public void showAllPreviews(){  //선택된 말이 이동할 수 있는 경우를 모두 보여주는 메소드
        for(ThrowData data:_data.previewPawns) {
            data.preview.setVisible(true);
            if(data.result == 6 && _data.focusedPawn.getCurrentIndex() == 0) data.preview.setVisible(false);    //말이 이동할 곳이 없는 경우는 보이지 않도록 설정
            else _data.findNextPoint(data); //말이 결과 데이터에 따라 이동하게 될 위치 찾기
        }
        GameManager.getInstance().get_inGame().repaint();
    }

}
