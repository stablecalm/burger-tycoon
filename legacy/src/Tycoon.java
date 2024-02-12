import javax.microedition.lcdui.*;		//GUI관련 클래스
import javax.microedition.midlet.*;		
import javax.microedition.media.*;		//배경음악 paly를 위해
import java.util.*;
import java.io.*;

//메인 미들릿 프로그램
public class Tycoon extends MIDlet implements CommandListener 
{
	public int mission;
	private Display display;				// 화면
    private LowLevel low;					// 미션 ( 난이도 하 ) 
    private HighLevel high;					// 미션 ( 난이도 상 )
	private Player p;						// 배경음악 재생기
	InputStream is;
	
	private int order[];					// 토핑 종류 배열 - 주문 배열.
	private StartScreen start = null;		// 시작화면
	private EndingScreen ending;		// 엔딩화면
	private Command startCmd = new Command("확인", Command.OK,1);	// 시작화면 -> 키 설명 화면
	private Command exitCmd  = new Command("종료", Command.EXIT,1);	// 시작화면 -> 키 설명 화면

	public Tycoon()
	{	
		super();
		display = Display.getDisplay(this);		// 현재 화면을 받아옴
		start = new StartScreen();				// 시작화면
		mission = 0; // 현재 수행한 미션 0개
		try
	    {    		
			start.addCommand(startCmd);
			start.addCommand(exitCmd);
			start.setCommandListener(this);
	    }
		catch(Exception e)
	    {
			System.out.println("start exception : "+e.getMessage());
	    }

	}
	
	public void startApp() throws MIDletStateChangeException
	{
		display.setCurrent(start);		//시작화면	
		try 
		{// 음악파일을 입력 스트림으로 열어서 플레이어로 실행
			is = getClass().getResourceAsStream("bgm/main.wav");
			p = Manager.createPlayer(is, "audio/X-wav");
			p.start();
		} 
		catch (IOException ioe)  {}		//스트림 열때
		catch (MediaException me){}	//Manager.createPlayer생성시
	}
	
	public void pauseApp() {}
	
	public void destroyApp(boolean unconditional)
	{
		if( p != null)
		{
			p.close();
		}
		display=null;
	}
	
	public void destroyCanvas()
	{// 게임 종료시
		if( p != null)
		{
			p.close();
		}
		low = null;	 
		high = null;
	}

	public void commandAction(Command c, Displayable d)
	{
	    if (d==start && c==startCmd)
		{
			if( start.state == 0 )			
			{// 시작 화면에서 커맨드가 들어오면
				start.state = 1;
				if (p!=null)	
				{
					p.close();			
				}
				try {
					is = getClass().getResourceAsStream("bgm/key.wav");
					p = Manager.createPlayer(is, "audio/X-wav");
					p.start();
				} 
				catch (IOException ioe)  {}	//스트림 열때
				catch (MediaException me){}	//Manager.createPlayer생성시
				start.repaint();			// 키 설명화면으로 이동 
			}
			else							
			{// 키 설명 화면에서 커맨드가 들어오면
				if (p!=null)	
				{
					p.close();			
				}
				GameRoutine();
			}
		}
	    else if (d==ending && c==startCmd)
	    {
	    	destroyApp(true);
			notifyDestroyed();
	    }
		else if ( c==exitCmd  )
		{
			destroyApp(true);
			notifyDestroyed();
		}
	}//end commandAction
	


	public void GameRoutine()
	{// 5개의 level에 대한 routine 처리
		order = new int[10];
		
		for(int index=0; index<10; index++)
		{
			order[index] = -1;
		}
		
		if( mission == 0)
		{	// MISSION 1
			// 밑빵 - 토마토 - 치즈 - 야채 - 윗빵 
			order[0] = 0;
			order[1] = 4;
			order[2] = 1;	
			order[3] = 2;
			order[4] = 5;
			low = new LowLevel(this, display, mission, 5, order, "bgm/level_0.wav", 30 );
			display.setCurrent( low );
		}
		else if( mission == 1 )
		{	// MISSION 2
			// 밑빵 - 치즈 - 토마토 - 야채 - 쇠고기 - 윗빵
			order[0] = 0;
			order[1] = 1;
			order[2] = 4;
			order[3] = 2;
			order[4] = 3;
			order[5] = 5;
			low = new LowLevel(this, display, mission, 6, order, "bgm/level_1.wav", 40 );
			display.setCurrent( low );
		}
		else if( mission == 2 )
		{	// MISSION 3
			// 밑빵 - 치즈 - 치즈 - 토마토 - 야채 - 쇠고기 - 야채 - 윗빵
			order[0] = 0;
			order[1] = 1;
			order[2] = 1;
			order[3] = 4;
			order[4] = 2;
			order[5] = 3;
			order[6] = 2;
			order[7] = 5;
			low = new LowLevel(this, display, mission, 8, order, "bgm/level_2.wav", 60 );
			display.setCurrent( low );
		}
		else if( mission == 3 )
		{	// MISSION 4
			// 밑빵 - 야채 - 토마토 - 치즈 - 쇠고기 - 쇠고기 - 토마토 - 윗빵 
			order[0] = 0;
			order[1] = 2;
			order[2] = 4;
			order[3] = 1;
			order[4] = 3;
			order[5] = 3;
			order[6] = 4;
			order[7] = 5;
			high = new HighLevel(this, display, mission, 8, order, "bgm/level_3.wav", 60 );
			display.setCurrent( high );
		}
		else if( mission == 4 )
		{	// MISSION 5
			// 밑빵 - 치즈 - 쇠고기 - 야채 - 토마토 - 쇠고기 - 야채 - 토마토 - 토마토 - 윗빵
			order[0] = 0;
			order[1] = 1;
			order[2] = 3;
			order[3] = 2;
			order[4] = 4;
			order[5] = 3;
			order[6] = 2;
			order[7] = 4;
			order[8] = 4;
			order[9] = 5;
			high = new HighLevel(this, display, mission, 8, order, "bgm/level_4.wav", 60 );
			display.setCurrent( high );
		}
		else
		{	// Ending
			if (p!=null)	
			{
				p.close();			
			}
			try {
				is = getClass().getResourceAsStream("bgm/ending.wav");
				p = Manager.createPlayer(is, "audio/X-wav");
				p.start();
			} 
			catch (IOException ioe)  {}	//스트림 열때
			catch (MediaException me){}	//Manager.createPlayer생성시
			ending = new EndingScreen();				// 시작화면
			try
		    {    		
				ending.addCommand(startCmd);
				ending.addCommand(exitCmd);
				ending.setCommandListener(this);
		    }
			catch(Exception e)
		    {
				System.out.println("start exception : "+e.getMessage());
		    }
			display.setCurrent( ending );
		}
	}

	//Inner class - 메인 화면
	class StartScreen extends Canvas
	{
		protected Image start_img=null;
		protected Image key_img=null;
		protected int state;
		//파일에서 그림을 읽어 이미지 생성
		public StartScreen()
		{
			state = 0;
			try
			{
				start_img = Image.createImage("/images/main.gif");
				key_img = Image.createImage("/images/key_helper.gif");
			}catch(Exception e)
			{
				System.out.println("can't find main image.");
			}
		}
		
		//화면에 그림
		public void paint(Graphics g)
		{	
			//캔버스의 넓이와 높이를 구함
			int width=this.getWidth();
			int height=this.getHeight();

			g.setColor(0x000000);			//검은색으로
			g.fillRect(0,0,width,height);	//전체를 채움
		
			//화면 중앙(g.HCENTER|g.VCENTER)에 시작 화면을 뿌림
			if( state == 0 )
			{
				g.drawImage(start_img, width/2, height/2, Graphics.HCENTER|Graphics.VCENTER);		
			}
			else
			{
				g.drawImage(key_img, width/2, height/2, Graphics.HCENTER|Graphics.VCENTER);		
			}
		}
	}					

//	Inner class - 메인 화면
	public class EndingScreen extends Canvas
	{
		private Moving m;
		private Image bgr = null;
		private Image move_1 =null;
		private Image move_2 =null;
		private boolean moving;
		private int x_pos;
		public EndingScreen()
		{
			moving = false;
			x_pos = -80;
			m = new Moving(this);
			try
			{
				bgr = Image.createImage("/images/ending.gif");
				move_1 = Image.createImage("/images/end_move_1.gif");
				move_2 = Image.createImage("/images/end_move_2.gif");
			}catch(Exception e)
			{
				System.out.println("can't find image.");
			}
		}

		//화면에 그림
		public void paint(Graphics g)
		{	
			g.drawImage( bgr, 120, 145, Graphics.HCENTER|Graphics.VCENTER);
			if( moving )
			{
				g.drawImage( move_1, x_pos, 150, Graphics.TOP|Graphics.LEFT);
				moving = false;
				x_pos += 3;
			}
			else{
				g.drawImage( move_2, x_pos, 150, Graphics.TOP|Graphics.LEFT);
				moving = true;
				x_pos += 3;
			}
		}
	}
	public class Moving {
	    private EndingScreen canvas;	//게임화면
	    private Timer timer;
		private CountFunc countFunc;		//time task(1초 간격으로 카운트를 하고, 남은시간을 줄임)

	    public Moving( EndingScreen c)
	    {
	    	canvas=c;
			countFunc = new CountFunc();
			
			timer = new Timer();
			timer.schedule(countFunc, 0, 500);	
		}
	    public class CountFunc extends TimerTask implements Runnable
		{
	    	int count = 0;
	    	public void run()
	    	{
	    		if( count < 110 )
	    		{
	    			canvas.repaint();
	    			count++;
	    		}
	    	}
		}
	}
}
	
	



