#pragma once
#include "coronatracker-custom-typdefs.h"

#ifndef LED_PIN
#define LED_PIN 16
#endif

#define SCAN_TIME 10       // seconds
#define ADVERTISE_TIME 500 // milliseconds

// time variables
#define NTP_SERVER (const char*) "pool.ntp.org"

// used to be for main
void firstStartInitializeSteps(int *savedRsin);
void startInitializeSteps(void);
void processVerificationForUserInput(exposure_status *exp_state);

void actionScanForBluetoothDevices(int* scannedDevices);
void advertiseBluetoothDevice(int* scannedDevices, int *savedRsin);

void setupWifiConnection(bool* wifiInitialized);
void sendCollectedDataToServer(void);
void sendExposureInformationIfExists(void);
exposure_status getInfectionStatusFromServer(void);

// used to be used in coronatracker-utils only
bool initializeTek(int *savedRsin);
bool initializeTime(void);
bool initializeUuid(void);
size_t restartAfterErrorWithDelay(String errorMessage);