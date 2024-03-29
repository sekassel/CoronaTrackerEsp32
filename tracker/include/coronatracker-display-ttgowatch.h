#ifdef LILYGO_WATCH_2020_V1

#include <Arduino.h>
#include "coronatracker-wifi.h"

#define ACTION_NOTHING 0
#define ACTION_ADVERTISE 1
#define ACTION_SCAN 2
#define ACTION_WIFI_CONFIG 3
#define ACTION_INFECTION_REQUEST 4
#define ACTION_SLEEP 5

void initDisplay();
void displayVerificationCountdown(int number);
void displayReleaseButton(void);
void displayUuidAndTekForVerification(std::string uuid, std::string pin);
void displayVerificationFailed(void);
void displayVerificationSuccess(void);
void configureWifiMessageOnDisplay();
void wifiConfiguredOnDisplay(bool configured);
void defaultDisplay(struct tm timeinfo, int action, exposure_status exposureStatus, int numberOfScanedDevices);
String weekDayToString(int weekDay);
void afterInfectionRequestOnDisplay(exposure_status exposureStatus);

#endif