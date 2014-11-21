// Close_WinIo_service.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "windows.h"

int _tmain(int argc, _TCHAR* argv[])
{

	SC_HANDLE hSCManager = OpenSCManager(NULL, NULL, SC_MANAGER_ALL_ACCESS);
	SC_HANDLE hService = OpenService(hSCManager, L"WINIO", SERVICE_STOP | DELETE);
	ControlService(hService, SERVICE_CONTROL_STOP, NULL);
	DeleteService(hService);
	CloseServiceHandle(hSCManager);

	printf("WinIo Service should be closed\n");

	return 0;
}

