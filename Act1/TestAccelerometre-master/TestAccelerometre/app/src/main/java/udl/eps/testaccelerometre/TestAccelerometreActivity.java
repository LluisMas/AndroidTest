package udl.eps.testaccelerometre;

import android.app.Activity;
import android.content.pm.PackageManager;
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
  private boolean hasAccelero = true;
  private boolean hasLight = true;
  private TextView view;
  private TextView upperView;
  private TextView bottomView;
  private TextView scrollView;
  private long lastUpdate;
  private long lastUpdatelight;
  private float lastLum = 0;
  private static final String KEY_SCROLL = "scroll";
  private static final String KEY_COLOR = "color";


  @Override
  public void onCreate(Bundle savedInstanceState) {


    super.onCreate(savedInstanceState);

    setContentView(R.layout.main);
    view = findViewById(R.id.textView);
    upperView = findViewById(R.id.textView2);
    bottomView = findViewById(R.id.textView3);
    scrollView = findViewById(R.id.sensor_data);

    upperView.setBackgroundColor(Color.GREEN);
    bottomView.setBackgroundColor(Color.YELLOW);
    scrollView.setBackgroundColor(Color.YELLOW);

    sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    PackageManager manager = getPackageManager();

    if(!manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER))
    {
        view.setText(getResources().getString(R.string. no_accelero));
        hasAccelero = false;
    }
    else
    {
        view.setText(getResources().getString(R.string.shake));
        setAccelerometerInformation();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

      if(!manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_LIGHT))
      {
          bottomView.setText(getResources().getString(R.string.no_light));
          hasLight = false;
      }
      else
      {
          Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
          String formated = getResources().getString(R.string.light_data, sensor.getMaximumRange());
          bottomView.setText(formated);
          sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);
      }
      if(savedInstanceState != null)
      {
          scrollView.setText(savedInstanceState.getString(KEY_SCROLL));
          color = savedInstanceState.getBoolean(KEY_COLOR);
          upperView.setBackgroundColor( (color) ? Color.GREEN : Color.RED);

      }
    lastUpdate = System.currentTimeMillis();
    lastUpdatelight = System.currentTimeMillis();
  }

  private void setAccelerometerInformation()
  {
      Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

      String text = getResources().getString(R.string.shake);
      text += "\n\nResolucio: "  + sensor.getResolution() + "\n";
      text += "Rang: "  + sensor.getMaximumRange() + "\n";
      text += "Consum: "  + sensor.getPower() + "\n";
      view.setText(text);
  }

  @Override
  public void onSensorChanged(SensorEvent event) {
      switch (event.sensor.getType())
      {
          case Sensor.TYPE_ACCELEROMETER:
          {
              getAccelerometer(event); // wth is that function name lmao
              break;
          }
          case Sensor.TYPE_LIGHT:
          {
              getLightSensor(event);
              break;
          }
      }
  }

  private void getLightSensor(SensorEvent event) {
      float values[] = event.values;

      float illuminance = values[0];

      long currentTime = System.currentTimeMillis();
      if (currentTime - lastUpdatelight < 2000 || Math.abs(lastLum - illuminance) < 5000)
          return;

      String light = scrollView.getText().toString();
      String intesity;
      if(illuminance < 13000)
          intesity = "LOW";
      else if(illuminance < 26000)
          intesity = "MEDIUM";
      else
          intesity = "HIGH";
      light += getResources().getString(R.string.light_changed, illuminance, intesity);
      scrollView.setText(light);

      lastUpdatelight = currentTime;
      lastLum = illuminance;
  }

  private void getAccelerometer(SensorEvent event) {
    float values[] = event.values;
    
    float x = values[0];
    float y = values[1];
    float z = values[2];

    float accelationSquareRoot = (x * x + y * y + z * z) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
    long actualTime = System.currentTimeMillis();

    if (accelationSquareRoot >= 2) 
    {
      if (actualTime - lastUpdate < 200)
        return;

      lastUpdate = actualTime;

      Toast.makeText(this, R.string.shuffed, Toast.LENGTH_SHORT).show();
      upperView.setBackgroundColor( (color) ? Color.GREEN : Color.RED);

      color = !color;
        setAccelerometerInformation();
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {
    // Do something here if sensor accuracy changes.
  }


    @Override
    protected void onResume() {
        super.onResume();

        if(hasAccelero)
        {
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        }
        if(hasLight)
        {
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);
        }

        lastUpdate = System.currentTimeMillis();
        lastUpdatelight = System.currentTimeMillis();
    }

    @Override
  protected void onPause() {
    // unregister listener
    super.onPause();
    sensorManager.unregisterListener(this);
  }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_SCROLL, scrollView.getText().toString());
        outState.putBoolean(KEY_COLOR, color);
    }
}
