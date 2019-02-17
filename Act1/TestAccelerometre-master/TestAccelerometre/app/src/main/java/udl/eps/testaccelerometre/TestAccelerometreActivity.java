package udl.eps.testaccelerometre;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.widget.TextView;
import android.widget.Toast;

public class TestAccelerometreActivity extends Activity implements SensorEventListener {
  private SensorManager sensorManager;
  private boolean color = false;
  private TextView view;
  private long lastUpdate;

  

  @Override
  public void onCreate(Bundle savedInstanceState) {


    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    view = (TextView) findViewById(R.id.textView);
    view.setBackgroundColor(Color.GREEN);

    sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    sensorManager.registerListener(this,
              sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
              SensorManager.SENSOR_DELAY_NORMAL);
      // register this class as a listener for the accelerometer sensor
    lastUpdate = System.currentTimeMillis();
  }

  @Override
  public void onSensorChanged(SensorEvent event) {
          getAccelerometer(event);
  }

  private void getAccelerometer(SensorEvent event) {
    float values[] = event.values;
    
    float x = values[0];
    float y = values[1];
    float z = values[2];

    float accelationSquareRoot = (x * x + y * y + z * z)
        / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
    long actualTime = System.currentTimeMillis();
    if (accelationSquareRoot >= 2) 
    {
      if (actualTime - lastUpdate < 200) {
        return;
      }
      lastUpdate = actualTime;

      Toast.makeText(this, R.string.shuffed, Toast.LENGTH_SHORT).show();
      if (color) {
        view.setBackgroundColor(Color.GREEN);

      } else {
        view.setBackgroundColor(Color.RED);
      }
      color = !color;
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {
    // Do something here if sensor accuracy changes.
  }

  @Override
  protected void onPause() {
    // unregister listener
    super.onPause();
    sensorManager.unregisterListener(this);
  }
} 
