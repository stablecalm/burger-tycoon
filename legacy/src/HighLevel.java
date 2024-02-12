import javax.microedition.lcdui.*;		//GUI관련 클래스
import javax.microedition.media.*;
import javax.microedition.media.MediaException;
import javax.microedition.midlet.MIDletStateChangeException;
import java.io.*;

public class HighLevel extends Canvas implements CommandListener {

    private Tycoon main;			//매인 클래스
    private Display display;		//현재화면
	private HighMap map;
	private Player p;
	InputStream is;

	private Image bgr = null;
	private Image front = null;
	private Image left = null;
	private Image right = null;
	private Image start = null;
	private Image surprise = null;
	private Image guest = null;
	private Image plate = null;
	private Image enter = null;
	private Image clear = null;
	private Image fail = null;
	private Image[] count = { null, null, null };
	private Image[] order_img = { null, null, null};
	private Image[] topping = { null,null,null,null,null,null };
	private Image complain = null;
	
    private int order[];
    private int mission;	
    private int number;
    private int timelimit;
    // 	손님. 주인공 대화 주고 받기. 0:손님 입장. 1:주문, 2:게임화면, 3:Clear, 4:Game_Over
	private int state;	
	
	// Toppping Line Array 9x3
	private final int row = 9;
	private final int col = 3;
	
	private Command cancelCmd;				// 토핑을 버림
	private Command okCmd;					// 햄버거 완성!!
	
	public HighLevel( Tycoon main, Display display, int mission, int number, int[] order, String bgm, int timelimit)
	{
		state = 0;
		this.main = main;
		this.display = display;
		this.mission = mission;
		this.number = number;
		this.order = order;
		this.timelimit = timelimit;
		

		cancelCmd = new Command("취소",Command.CANCEL,1);
		addCommand(cancelCmd);
		okCmd = new Command("확인",Command.OK,1);
		addCommand(okCmd);
		setCommandListener(this);

		if (p!=null)	
		{
			p.close();			
		}
		try {
			is = getClass().getResourceAsStream( bgm );
			p = Manager.createPlayer(is, "audio/X-wav");
			p.start();
		} 
		catch (IOException ioe){}
		catch (MediaException me){}

		try
		{
			bgr 			=	Image.createImage("/images/back.gif");
			front 			=	Image.createImage("/images/front.gif");
			left 			=	Image.createImage("/images/left.gif");
			right			=	Image.createImage("/images/right.gif");
			start 			=	Image.createImage("/images/start.gif");
			surprise 		=	Image.createImage("/images/surprise.gif");
			guest			=	Image.createImage("/images/guest1.gif");
			plate			=	Image.createImage("/images/plate.gif");
			enter			=	Image.createImage("/images/order1.gif");
			clear 			=	Image.createImage("/images/clear.gif");
			fail			=	Image.createImage("/images/fail.gif");
			count[0]		=	Image.createImage("/images/count1.gif");
			count[1]		=	Image.createImage("/images/count2.gif");
			count[2]		=	Image.createImage("/images/count3.gif");
			topping[0]		=	Image.createImage("/images/under.gif");
			topping[1]		=	Image.createImage("/images/cheese.gif");
			topping[2]		=	Image.createImage("/images/lettuce.gif");
			topping[3]		=	Image.createImage("/images/meat.gif");
			topping[4]		=	Image.createImage("/images/tomato.gif");
			topping[5]		=	Image.createImage("/images/over.gif");
			order_img[0]	=	Image.createImage("/images/level3.gif");
			order_img[1]	=	Image.createImage("/images/level4.gif");
			complain		=	Image.createImage("/images/complain.gif");
		}
		catch(Exception e)
		{
			System.out.println("이미지를 찾을 수 없음");
		}
	}

	protected void paint(Graphics g) 
	{
		
		//	캔버스의 넓이와 높이를 구함
		int width=this.getWidth();
		int height=this.getHeight();
		
		g.setColor(0x000000);			//검은색으로
		g.fillRect(0,0,width,height);	//전체를 채움

		if( state == 0 )
		{// 주인공 캐릭의 대사 " 주문하시겠습니까 ?"
				g.drawImage( enter, width/2, height/2, Graphics.HCENTER|Graphics.VCENTER);		
		}
		else if( state == 1)
		{// 손님의 주문 요구 - 레벨에 따른 주문 화면 풀력
			if( mission == 3 )		g.drawImage(order_img[0], width/2, height/2, Graphics.HCENTER|Graphics.VCENTER);		
			else if( mission == 4 )	g.drawImage(order_img[1], width/2, height/2, Graphics.HCENTER|Graphics.VCENTER);		
		}
		else{// state == 2;
		    //각 이미지의 크기를 얻음
			// 메인캐릭 60 * 60
		    int so_height = start.getHeight();
			int so_width = start.getWidth();
			// 토핑 60 * 13
			int topping_height = 20;//화면이 답답하지 않도록 Topping 간의 간격을 줌. 
			int topping_width = topping[0].getWidth();
			// 손님 60 * 72
			int guest_height = guest.getHeight();
			int guest_width = guest.getWidth();
			// 주인공 60 * 66
			int plate_height = plate.getHeight();
			int plate_width = plate.getWidth();
			int clear_height = start.getHeight();
			int clear_width = start.getWidth();
			int fail_height = start.getHeight();
			int fail_width = start.getWidth();
			
			//화면에 출력할 문자열(현재 Mission 과  남은 시간)
		    String str_mission = "";
		    String str_time = "";
		    str_mission = String.valueOf(mission+1);
		    str_time = String.valueOf( map.timelimit );
			
			g.setColor(0xffffff);	// 배경 흰색
			g.fillRect(0,0,width,height);	
	
			// 배경그림.
		    g.drawImage( bgr, 60, 0, Graphics.TOP|Graphics.LEFT);
			
			// 상단에 게임 정보 출력
			g.drawImage( guest, 0, 0, Graphics.TOP|Graphics.LEFT);
//			face:SYSTEM(0), style:BOLD(1), size:LARGE(16)
			Font f=Font.getFont(0, 1, 16 );	
			g.setFont(f);
			g.setColor(0x000000);	// 문자열 검은색
			g.drawString("Mission", 65, 10, Graphics.TOP|Graphics.LEFT);
			g.drawString(str_mission, 170, 10, Graphics.TOP|Graphics.LEFT);
			g.drawString("T I M E", 65, 30, Graphics.TOP|Graphics.LEFT);
			g.drawString(str_time, 170, 30, Graphics.TOP|Graphics.LEFT);
			
			//접시 위에 쌓여진 Topping 그림
			if( map.num_of_stack != 0 )
			{
				int i;
				for( i = 0; i < map.num_of_stack;i++)
				{
					if( map.stack[i] == 0)
					{
						g.drawImage( topping[0], 0, 210-i*13, Graphics.TOP|Graphics.LEFT);
					}
					else if( map.stack[i] == 1)
					{
						g.drawImage( topping[1], 0, 210-i*13, Graphics.TOP|Graphics.LEFT);
					}
					else if( map.stack[i] == 2)
					{
						g.drawImage( topping[2], 0, 210-i*13, Graphics.TOP|Graphics.LEFT);
					}
					else if( map.stack[i] == 3)
					{
						g.drawImage( topping[3], 0, 210-i*13, Graphics.TOP|Graphics.LEFT);
					}
					else if( map.stack[i] == 4)
					{
						g.drawImage( topping[4], 0, 210-i*13, Graphics.TOP|Graphics.LEFT);
					}
					else if( map.stack[i] == 5)
					{
						g.drawImage( topping[5], 0, 210-i*13, Graphics.TOP|Graphics.LEFT);
					}
				}
			}
		    //접시 
		    g.drawImage( plate, 0, 223, Graphics.TOP|Graphics.LEFT);
			if(map.complain == false)
			{
				// 배경 위에 주인공과 토핑 그리기 (map에서 각 element의 값을 확인해서 해당하는 그림을 그림)
				for(int i=0; i< row; i++)
				{
					 for(int j=0; j<col; j++)
					 {
						if (map.mapArray[i][j] == 6 )		//주인공 그림
						{
						   g.drawImage( start,60+j*so_width, 72 + i*topping_height, Graphics.TOP|Graphics.LEFT);
						}
						else if (map.mapArray[i][j] == 7 )		//주인공 그림
						{
						   g.drawImage( front,60+j*so_width, 72 + i*topping_height, Graphics.TOP|Graphics.LEFT);
						}
						else if (map.mapArray[i][j] == 8 )		// 왼쪽 
						{
						   g.drawImage( left,60+j*so_width, 72 + i*topping_height, Graphics.TOP|Graphics.LEFT);
						}
						else if (map.mapArray[i][j] == 9 )		// 오른쪽
						{
						   g.drawImage( right,60+j*so_width, 72 + i*topping_height, Graphics.TOP|Graphics.LEFT);
						}
						else if(map.mapArray[i][j] == 0)	// 밑빵
						{
							g.drawImage( topping[0],60+j*topping_width,72+i*topping_height, Graphics.TOP|Graphics.LEFT);
						}
						else if(map.mapArray[i][j] == 1)	// 치즈
						{
							g.drawImage( topping[1],60+j*topping_width,72+i*topping_height, Graphics.TOP|Graphics.LEFT);
						}
						else if(map.mapArray[i][j] == 2)	// 야채
						{
							g.drawImage( topping[2],60+j*topping_width,72+i*topping_height, Graphics.TOP|Graphics.LEFT);
						}
						else if(map.mapArray[i][j] == 3)	// 쇠고기 패티
						{
							g.drawImage( topping[3],60+j*topping_width,72+i*topping_height, Graphics.TOP|Graphics.LEFT);
						}
						else if(map.mapArray[i][j] == 4)	// 토마토
						{
							g.drawImage( topping[4],60+j*topping_width,72+i*topping_height, Graphics.TOP|Graphics.LEFT);
						}
						else if(map.mapArray[i][j] == 5)	// 윗빵
						{
							g.drawImage( topping[5],60+j*topping_width,72+i*topping_height, Graphics.TOP|Graphics.LEFT);
						}
	
				     }
				}
			}
			else{// 스테이지 5의 특별 이벤트 처리 부분.
				g.drawImage( complain,60,72, Graphics.TOP|Graphics.LEFT);
				g.drawImage( surprise,60+map.soyoung_pos*topping_width,72+8*topping_height, Graphics.TOP|Graphics.LEFT);
				this.order[1] = 4;// 치즈 대신 토마토
			}
			
			//게임상태에 따른 사운드 및 이미지 처리
			if( map.flag == 4 )
			{// Mission Clear
				g.drawImage( count[2], 150, 150, Graphics.VCENTER|Graphics.HCENTER);
			}
			else if( map.flag == 3 )
			{// Mission Clear
				g.drawImage( count[1], 150, 150, Graphics.VCENTER|Graphics.HCENTER);
			}
			else if( map.flag == 2 )
			{// Mission Clear
				g.drawImage( count[0], 150, 150, Graphics.VCENTER|Graphics.HCENTER);
			}
			
			else if( map.flag == 1 )
			{// Mission Clear
				g.drawImage( clear, 150, 150, Graphics.VCENTER|Graphics.HCENTER);
				state = 3;
			}
			
			else if( map.flag == -1 )
			{// Mission Fail
				g.drawImage( fail, 150, 150, Graphics.VCENTER|Graphics.HCENTER);
				state = 4;
			}
		
		}
	}
	//	키 입력 핸들러
	public void keyPressed(int keycode)
	{
		if( map.flag == 0)	//게임중일때만 핸들링 해줌
		{
			switch (getGameAction(keycode))
			{		
				case Canvas.LEFT:
					map.moveLeft();	//좌측이동
					break;
				case Canvas.RIGHT:
					map.moveRight();//우측이동
					break;
				default:
			}
		}
	}

	public void commandAction(Command c, Displayable d) 
	{
		if( c == cancelCmd )
		{
			if( state == 2 && map.flag == 0)
			{// 만들고 있는 햄버거 가장 위의 토핑을 버린다.
				if( map.num_of_stack == 0 )
				{//Vector가 비어있으면 아무 동작하지 않음.
					return;
				}
				else{
					map.stack[map.num_of_stack-1] = -1;
					map.num_of_stack--;
				}
			}
			else{
			// 게임 종료.
				
			}
			
		}
		else if( c == okCmd  )
		{
			if(state == 0)
			{
				state = 1;
				this.repaint();
			}
			else if(state == 1)
			{
				state = 2;
				map = new HighMap(this, mission, timelimit);
			}
			else if(state == 2 && map.flag == 0)
			{//햄버거 만든 것 확인받기.
				for(int i=0; i<this.number; i++)
				{
					if(order[i] != map.stack[i])
					{
						map.Done(false);
						return;
					}
				}
				map.Done(true);
			}
			else if(state == 3)
			{
				if( p!= null)
				{
					p.close();
				}
				main.destroyCanvas();
				main.mission++;
				main.GameRoutine();
			}
			else
			{// state == 4;
				if( p!= null)
				{
					p.close();
				}
				main.destroyCanvas();
				try {
					main.startApp();
				} catch (MIDletStateChangeException e) {
					System.out.println("다시 시작에 문제가 있습니다.");
				}
			}
		}
	}
	
}



