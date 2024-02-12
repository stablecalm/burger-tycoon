import java.util.*;

public class HighMap {
    private HighLevel canvas;	//게임화면
    
	//맵의 행과 열(9x3) Topping Line 12, Main Character Line 1
    private final int row = 9;
    private final int col = 3;
    private int mission;		//level(0~4)
    private boolean didnt;		// 불평은 딱 한번만 실행됨.
       
	private Random rand;
	private Timer timer, countdown;
	private CountFunc countFunc;		//time task(1초 간격으로 카운트를 하고, 남은시간을 줄임)
	private TimeFunc idleFunc;

	public int timelimit;		//남은 시간
	public int flag;			//게임의 진행상황(4~2:시작 카운트, 1: clear, 0: gaming, -1:fail )
	public int soyoung_pos;		//주인공의 위치(0~2)
	public int[] stack = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
	public int num_of_stack;
	public boolean complain;
	
	
	//-1:빈칸, 0:밑빵, 1:치즈, 2:야채, 3:쇠고기, 4:토마토, 5:윗빵, 주인공 6:start, 7:front, 8:left, 9right, 10surprise
    public int [][] mapArray= 
    {// 9x3
    		{-1,-1,-1},
    		{-1,-1,-1},
    		{-1,-1,-1},
    		{-1,-1,-1},
    		{-1,-1,-1},
    		{-1,-1,-1},
    		{-1,-1,-1},
    		{-1,-1,-1},
    		{-1, 6,-1}
    };
    
    //생성자
    public HighMap( HighLevel c, int mission, int timelimit)
    {
    	didnt = true;
    	complain = false;
    	num_of_stack = 0;			// 초기치 
    	soyoung_pos = 1;			// 최초 위치
    	flag = 6;

    	canvas=c;
		this.mission = mission;
		this.timelimit = timelimit;
		
		rand = new Random();
		idleFunc = new TimeFunc();
		countFunc = new CountFunc();
		
		timer = new Timer();
		countdown = new Timer();
		
		countdown.schedule(countFunc, 1000, 1000);	//1초후에 1초간격으로 countFunc.run() 실행	
	}
    
    public void GetTopping(int what)
    {
    	if( num_of_stack == 12 )
    	{// Stack 이 꽉 찬 상황에서 토핑이 들어오면
    		Done(false);
    		return;
    	}
    	stack[num_of_stack] = what;
    	num_of_stack++;
    	
    	if(didnt)
    	{
	    	if( num_of_stack > 6 && mission == 4)
	    	{// 마지막 미션의 특별 이벤트; 주문  정정.
	    		complain = true;
	    		didnt = false;
	    	}
    	}
    }
    
    //왼쪽으로 이동시키는 메서드
    public void moveLeft()
    {
    	soyoung_pos--;
    	if(soyoung_pos == -1)	//왼쪽 끝이면 더이상 이동하지 않음
    		soyoung_pos=0;
    	else
    	{
    		if( (mapArray[row-1][soyoung_pos]>-1)&&(mapArray[row-1][soyoung_pos]<6) )
    		{
    			GetTopping(mapArray[row-1][soyoung_pos]);
    		}
    		if(soyoung_pos == 0)
    		{
    			mapArray[row-1][soyoung_pos] = 8;	
    		}
       		else
       		{
       			mapArray[row-1][soyoung_pos] = 7;
       		}
    		mapArray[row-1][soyoung_pos+1] = -1;
    	}
    	canvas.repaint();
    }
    
    //차를 오른쪽으로 이동시키는 메서드
    public void moveRight()
    {
    	soyoung_pos++;
    	if(soyoung_pos == 3) //오른쪽 끝이면 더이상 이동하지 않음
    		soyoung_pos=2;
    	else
    	{
    		if( (mapArray[row-1][soyoung_pos]>-1)&&(mapArray[row-1][soyoung_pos]<6) )
    		{
    			GetTopping(mapArray[row-1][soyoung_pos]);
    		}
    		if(soyoung_pos == 2)
    		{
    			mapArray[row-1][soyoung_pos] = 9;	
    		}
       		else
       		{
       			mapArray[row-1][soyoung_pos] = 7;
       		}
    		mapArray[row-1][soyoung_pos-1] = -1;
    	}
    	canvas.repaint();
    }
 
    public class TimeFunc extends TimerTask implements Runnable
	{
    	int i, j;
    	int topping_pos;
		int what_topping;
		int get_topping;
    	public void run()
    	{
    		if(complain)
    		{
    			try {
					Thread.sleep( 2500 );
				} catch (InterruptedException e) {
					System.out.println("Thread Sleep 에서 문제가 발생하였습니다.");
				}
				complain = false;
    		}
    		get_topping = -1;
    		// 마지막 라인으로 이동하기 전 충돌 검사. 
    		for(j=0; j<col; j++)
			{
				if(mapArray[row-1][j] > 5)// 주인공이 서있는 자리 위에 
				{
					if( mapArray[row-2][j] > -1 ) // 토핑이 있으면
					{
						get_topping = mapArray[row-2][j];		// 충돌 flag set -> 토핑을 plate로
					}
				}
				else
				{
					mapArray[row-1][j] = mapArray[row-2][j];
				}
			}
    		// 그 이외의 라인의 경우 밑의 라인에 윗라인의 내용을 복사.
    		for(i=2; i < row ; i++)			
    		{
    			for(j=0; j<col; j++)
    			{
    				mapArray[row-i][j] = mapArray[row-i-1][j];
    			}
    		}
    		//맵의 첫줄에 새로운 토핑 생성
    		for(j=0; j<col; j++)
			{
				mapArray[0][j] = -1;	//일단 첫줄  초기화
			}
    		topping_pos = rand.nextInt()%3;	//새로운 토핑 열 위치
   			what_topping = Math.abs( rand.nextInt()%6 );	//6가지 토핑 중 랜덤  생성
   			if( topping_pos >= 0)
   			{//
   				mapArray[0][topping_pos] = what_topping;
   			}
    		if( get_topping != -1)
    		{
    			GetTopping( get_topping );
    		}
   			canvas.repaint();
    	}
	}

    public class CountFunc extends TimerTask implements Runnable
	{
    	public void run()
    	{
    		//시작전 카운트( flag값 4, 3, 2)
    		if( flag > 1 )
    		{
    			canvas.repaint();
    			if(flag == 2)
    			{
    				flag = -2;
    			}
    			else{
    				flag--;			//카운트를 줄임
    			}
    		}
    		else if( flag == -2)	// 3까지 세고 게임 시작.
			{
    			flag = 0;
				Start();
			}
    		else if(flag == 0)	//게임 중에는 남은 시간을 줄임.
    		{
    			timelimit--;
       			if( timelimit == 0)	//남은시간이 0이면
       			{
       				Done(false);		// 게임오버.
       			}
    		}
    	}
	}
    
    public void Start()
    {
    	// 하이레벨에서는 0.3초의 간격으로 idleFunc.run()을 실행함 
    	timer.schedule(idleFunc, 0, 300);
    }
  
    public void Done(boolean success)
    {
    	if(success)
    	{
    		flag = 1;
    	}
    	else
    	{
    		flag = -1;
    	}
    	for(int j=0; j<col; j++)
    	{// 게임용 메인 캐릭 이미지를 지움.
    		mapArray[row-1][j] = -1;
    	}
    	canvas.repaint();
    	countdown.cancel();
    	timer.cancel();
    }
 
}