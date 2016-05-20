# CircleWaveProgress

>circle wave view ， progress   
>The most important part  `WAVE` ，from  [john990-WaveView](https://github.com/john990/WaveView) ,thanks very much;


## Demo

![](https://github.com/macouen/CircleWaveProgress/raw/master/image/demo.gif) 


## Usage

### Gradle

```java
dependencies {
   compile 'com.oakzmm:circlewaveprogress:1.0.1'
}

```

### Maven

```java
<dependency>
  <groupId>com.oakzmm</groupId>
  <artifactId>circlewaveprogress</artifactId>
  <version>1.0.1</version>
  <type>pom</type>
</dependency>

```
### other
copy codes into you project


### use in you own code

- XML

> 1. do not support wrap_content
> 2. It recommended setting the exact value 
> 3. width = height

```java
  <com.oakzmm.library.cwp.CircleWaveProgress
        android:id="@+id/circleWaveProgress"
        android:layout_width="100dp"
        android:layout_height="100dp"
        />

```

- class

```java
//must call this method when init ,such as onCreate() or onResume();
circleWaveProgress.setWaveRun(true);

// stop wave by call setWaveRun(false) when Activity or Fragment or view invisiable,such as onPause();
circleWaveProgress.setWaveRun(false);

```

## Attributes

```java
 <declare-styleable name="CircleWaveProgress">
        <attr name="circle_progress" format="integer" />
        <attr name="circle_max" format="integer" />

        <attr name="circle_background_color" format="color" />
        <attr name="circle_border_color" format="color" />
        <attr name="circle_border_with" format="dimension" />

        <attr name="wave_font_color" format="color" />
        <attr name="wave_behind_color" format="color" />
        <attr name="wave_length" format="enum">
            <enum name="large" value="1" />
            <enum name="middle" value="2" />
            <enum name="little" value="3" />
        </attr>
        <attr name="wave_height" format="enum">
            <enum name="large" value="1" />
            <enum name="middle" value="2" />
            <enum name="little" value="3" />
        </attr>
        <attr name="wave_hz" format="enum">
            <enum name="fast" value="1" />
            <enum name="normal" value="2" />
            <enum name="slow" value="3" />
        </attr>

        <attr name="circle_text_size" format="dimension" />
        <attr name="circle_text_color" format="color" />
        <attr name="circle_text" format="string" />

    </declare-styleable>

```

