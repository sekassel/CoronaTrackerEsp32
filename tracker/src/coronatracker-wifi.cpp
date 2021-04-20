#include "coronatracker-wifi.h"

bool disconnectWifi()
{
    Serial.println("Deactivating Wifi");
    return WiFi.disconnect(true, false);
}

bool connectToStoredWifi()
{
    // Try to establish to Wifi-Connection
    WiFi.mode(WIFI_STA);
    WiFi.begin();
    return WiFi.waitForConnectResult() == WL_CONNECTED;
}

std::map<uint32_t, uint16_t> getRSINAsMap(bool connectToWifi)
{
    std::map<uint32_t, uint16_t> rsinMap;
    if (connectToWifi && !connectToStoredWifi())
    {
        Serial.println("Could not Connect to Wifi");
        return rsinMap;
    }
    else
    {
        HTTPClient http;

        http.begin(String(SERVER_URL) + String(RSIN_URL));
        int code = http.GET();

        if (!(code == HTTP_CODE_OK))
        {
            Serial.println("Failed to connect to server");
            http.end();
            return rsinMap;
        }
        else
        {
            DynamicJsonDocument doc(960); //This allows 40 entries
            DeserializationError err = deserializeJson(doc, http.getString());
            http.end();

            if (err)
            {
                Serial.print(F("deserializeJson() failed with code "));
                Serial.println(err.c_str());
                return rsinMap;
            }
            else
            {
                JsonObject json = doc.as<JsonObject>();
                for (JsonPair pair : json)
                {
                    rsinMap.insert(std::make_pair(atoi(pair.key().c_str()), pair.value().as<unsigned short>()));
                }
            }
        }
    }
    return rsinMap;
}

bool getNewUuid(char *uuidstr)
{
    if (!WiFi.isConnected() && !connectToStoredWifi())
    {
        Serial.println("Could not Connect to Wifi");
        return false;
    }
    else
    {
        HTTPClient http;

        http.begin(String(SERVER_URL) + String(RSIN_UUID));
        int code = http.GET();

        if (!(code == HTTP_CODE_OK))
        {
            Serial.println("Failed to connect to server!");
            http.end();
            return false;
        }
        else
        {
            String uuid = http.getString();
            http.end();

            if (uuid.length() == 36)
            {
                strcpy(uuidstr, uuid.c_str());
                Serial.print("Got UUID: ");
                Serial.println(*uuidstr);
            } else {
                Serial.println("UUID from Server != 36 character! Invalid UUID length.");
                return false;
            }
        }
    }
    return true;
}

exposure_status checkForInfections()
{
    if (!connectToStoredWifi())
    {
        Serial.println("EXPOSURE_UPDATE_FAILED: Couldn't connect to Wifi!");
        return EXPOSURE_UPDATE_FAILED;
    }

    // TODO: add logic to send contact informations and get infection status


/*
    Serial.println("Prepare to send contact information.");
    auto rsinMap = getRSINAsMap(false);

    sqlite3 *db;
    if (sqlite3_open(MAIN_DATABASE_SQLITE_PATH, &db) != SQLITE_OK)
    {
        Serial.printf("ERROR opening database: %s\n", sqlite3_errmsg(db));
        return EXPOSURE_UPDATE_FAILED;
    }
*/

    disconnectWifi();
    return EXPOSURE_NO_DETECT;
}

bool configureWifi()
{
    WiFiManager wifiManager;
    wifiManager.setConfigPortalTimeout(300); //5 Minutes
    wifiManager.setConnectTimeout(30);       //30 Seconds

    int buttonState = digitalRead(0); // Button Pin checked if Pressed == 0
    bool result;

    // connects to wifi automatically but if boot pressed while startup
    // it will open hotspot for configuration
    if (buttonState != 0)
    {
        result = wifiManager.autoConnect("Coronatracker", NULL);
    }
    else
    {
        result = wifiManager.startConfigPortal("Coronatracker", NULL);
    }

    if (!result)
    {
        wifiManager.resetSettings();
    }
    return result;
}