// Controller_Server.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "windows.h"
#include "winio.h"  //添加依赖项



#pragma comment(lib,"D:\\WinIO_source\\Dll\\x86\\Debug\\WinIo.lib")
#pragma comment(lib,"WS2_32.lib")

#define KBC_CMD 0x64//键盘命令端口

#define KBC_DATA  0x60//键盘数据端口

void KBCWait4IBE()
{
 DWORD dwVal=0;
 do
 {
  GetPortVal(KBC_CMD,&dwVal,1);
 }while((&dwVal)&&(0x2)==0);
}

void MyKeyDown(DWORD KCode)    
{    
    Sleep(20);    //休息2秒         
    KBCWait4IBE(); //等待键盘缓冲区为空    
    SetPortVal( 0X64, 0xD2, 1 ); //发送键盘写入命令    
    Sleep(10);    
    KBCWait4IBE();    
    SetPortVal( 0X60, MapVirtualKey(KCode, 0), 1 ); //写入按键信息,按下键    
}    

void MyKeyUp(DWORD KCode)    
{    
    Sleep(10);    
    KBCWait4IBE(); //等待键盘缓冲区为空    
    if (SetPortVal(0X64, 0xD2, 1 )) //发送键盘写入命令    
    Sleep(10);    
    KBCWait4IBE();    
    SetPortVal(0X60, (MapVirtualKey(KCode, 0) | 0x80), 1);//写入按键信息，释放键    
}    

int _tmain(int argc, _TCHAR* argv[])
{

	//init
	//InstallWinIoDriver((PWSTR)"WinIo64.sys",0);
	LoadLibrary(L"WinIo32.dll");
	while(!InitializeWinIo())
	{
		ShutdownWinIo();
		printf("erro init winIo \n");
	}
	// end init
	//InitializeWinIo();


	WSADATA wsd;
    SOCKET server;                                            //服务器socket
    SOCKADDR_IN addrSrv;    
    char sendBuf[100]={0};
    //char recvBuf[100];
	char recvBuf[100]={0};//改成这样
    SOCKADDR_IN addrClient;
    SOCKET client;                                            //连接的客户端socket
    int len;
    if(WSAStartup(MAKEWORD(2,2),&wsd)!=0)
    {
        printf("start up failed!\n");
        return 0;
    }
    server=socket(AF_INET,SOCK_STREAM,0);                    //创建socket
    addrSrv.sin_addr.S_un.S_addr=htonl(INADDR_ANY);            //设置地址
    addrSrv.sin_family=AF_INET;									//TCP族
    addrSrv.sin_port=htons(6666);                            //设置端口号
    bind(server,(SOCKADDR*)&addrSrv,sizeof(SOCKADDR));        //绑定
    listen(server,5);                                        //设置最多连接数
    len=sizeof(SOCKADDR);
    while(1)
    {
		bool connect_flag = true;
        client=accept(server,(SOCKADDR*)&addrClient,&len);    //接收客户端连接
		printf("客户端接入\n");
        printf(sendBuf,"Welcome %s ",
        inet_ntoa(addrClient.sin_addr));


        send(client,sendBuf,strlen(sendBuf)+1,0);            //发送信息客户端


		while(connect_flag){
			recv(client,recvBuf,100,0);                            //接收客户端数据

			if(recvBuf[0] == '1')
			{
				printf("bingo test\n");
			}

			switch (recvBuf[0])
			{
			case 'A':
				MyKeyDown(VK_LEFT); //LEFT
				Sleep(300);
				MyKeyUp(VK_LEFT); 
				break;
			case 'a':
				MyKeyDown(0x41); //a
				Sleep(300);
				MyKeyUp(0x41); 
				break;
			case 'D':
				MyKeyDown(VK_RIGHT); //RIGHT
				Sleep(300);
				MyKeyUp(VK_RIGHT); 
				break;
			case 'W':
				MyKeyDown(VK_UP); //UP
				Sleep(300);
				MyKeyUp(VK_UP); 
				break;
			case 'S':
				MyKeyDown(VK_DOWN); //DOWN
				Sleep(300);
				MyKeyUp(VK_DOWN); 
				break;
			case '0':
				connect_flag=false;
				break;

			default:
				break;
			}

			printf("收到消息\n");
			printf("%s\n",recvBuf);
			//recvBuf[] = {0};

		}
        
        //closesocket(client);
    }
    closesocket(client);                                    //关闭连接
    WSACleanup();

	
	ShutdownWinIo();

	return 0;
}
