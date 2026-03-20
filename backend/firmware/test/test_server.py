import paho.mqtt.client as mqtt
import json
import time

# Конфигурация для тестового сервера
BROKER = "localhost" # Тот же брокер, что и в config.py прошивки
PORT = 1883
SENSOR_ID = "sensor_001" # Должен совпадать с config.py прошивки

TOPIC_DATA = f"/device/{SENSOR_ID}/data"
TOPIC_STATUS = f"/device/{SENSOR_ID}/status"
TOPIC_CONFIG = f"/device/{SENSOR_ID}/config"

def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print(f"[TEST SERVER] Успешно подключено к MQTT брокеру {BROKER}:{PORT}")
        # Подписываемся на топики от устройства
        client.subscribe(TOPIC_DATA)
        client.subscribe(TOPIC_STATUS)
        print(f"[TEST SERVER] Ожидание данных от {SENSOR_ID} на топиках {TOPIC_DATA} и {TOPIC_STATUS}...")
    else:
        print(f"[TEST SERVER] Ошибка подключения. Код возврата: {rc}")

def on_message(client, userdata, msg):
    try:
        payload = json.loads(msg.payload.decode())
    except json.JSONDecodeError:
        print(f"❌ [ОШИБКА ДЕКОДИРОВАНИЯ] Топик: {msg.topic}")
        print(f"Payload: {msg.payload.decode()}")
        return

    if msg.topic == TOPIC_DATA:
        print("\n✅ [ДАННЫЕ ПОЛУЧЕНЫ]")
        validate_data(payload)
    elif msg.topic == TOPIC_STATUS:
        print("\n✅ [СТАТУС ПОЛУЧЕН]")
        validate_status(payload)
        
        # После получения статуса отправляем тестовую конфигурацию (проверка LISTEN_CONFIG)
        send_test_config(client)
    else:
        print(f"\n[НЕОЖИДАННЫЙ ТОПИК] {msg.topic}: {payload}")

def validate_data(payload):
    expected_keys = {"temperature", "temperature_time", "noise", "noise_time"}
    actual_keys = set(payload.keys())
    
    if expected_keys.issubset(actual_keys):
        print(f"   [PASS] Схема данных корректна.")
        print(f"   Температура: {payload.get('temperature')} C")
        print(f"   Шум: {payload.get('noise')} dB")
    else:
        missing = expected_keys - actual_keys
        print(f"   ❌ [FAIL] Отсутствуют ключи в data: {missing}")

def validate_status(payload):
    expected_keys = {"battery_level", "signal_strength", "timestamp", "errors"}
    actual_keys = set(payload.keys())
    
    if expected_keys.issubset(actual_keys):
        print(f"   [PASS] Схема статуса корректна.")
        print(f"   Сигнал сети: {payload.get('signal_strength')}%")
        print(f"   Ошибки: {payload.get('errors')}")
    else:
        missing = expected_keys - actual_keys
        print(f"   ❌ [FAIL] Отсутствуют ключи в status: {missing}")

def send_test_config(client):
    test_config = {
        "sampling_rate_noise": 60,
        "sampling_rate_temperature": 120,
        "restart_device": False,
        "health_check": True,
        "frequency_status": 300,
        "delete_device": False
    }
    print(f"\n[TEST SERVER] Отправка тестового конфига в топик {TOPIC_CONFIG}...")
    client.publish(TOPIC_CONFIG, json.dumps(test_config), qos=1)
    print("   Конфиг отправлен! Прошивка должна принять его в окне LISTEN_CONFIG (5 сек).")

if __name__ == "__main__":
    print("=== BeeIoT Test Server Started ===")
    print("Внимание: На устройстве ESP32 должен быть установлен SENSOR_ID = 'sensor_001'")
    
    client = mqtt.Client(client_id="beeiot_test_server")
    client.on_connect = on_connect
    client.on_message = on_message

    try:
        client.connect(BROKER, PORT, 60)
        client.loop_forever()
    except KeyboardInterrupt:
        print("\n[TEST SERVER] Остановлен пользователем.")
        client.disconnect()
    except Exception as e:
        print(f"[TEST SERVER] Ошибка брокера: {e}")
