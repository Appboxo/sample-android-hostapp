[ ![Download](https://api.bintray.com/packages/appboxodev/AndroidSdk/sdk/images/download.svg) ](https://bintray.com/appboxodev/AndroidSdk/sdk/_latestVersion)
To install it, simply add the following line to your **build.gradle**:
```groovy
implementation 'com.appboxo:sdk:x.x.x'
```

**Init AppboxoSDK in your application class**
**kotlin**
```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Appboxo.init(this)
            .setConfig(
                Config.Builder()
                    .setClientId("CLIENT_ID")
                    .build()
            )
            .setLogger(DefaultLogger(BuildConfig.DEBUG))
            //or use your own Logger
            .setLogger(object: Logger{
                override fun error(error: Throwable) {
                    //doSomething
                }

                override fun debug(message: String) {
                    //doSomething
                }
            })            
    }
}
```
**java**
```java
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Appboxo.INSTANCE.init(this)
                .setConfig(new Config.Builder()
                        .setClientId("CLIENT_ID")
                        .build())
                .setLogger(new DefaultLogger(BuildConfig.DEBUG));
    }
}
```

**To open Miniapps, write this code**

**kotlin**   
```kotlin
val miniApp = Appboxo.getMiniApp(appId, authPayload, data)
miniApp.open(this)
```
**java**
```java
MiniApp miniApp = Appboxo.INSTANCE.getMiniApp(appId, authPayload, data);
miniApp.open(this);
```
**data** - to pass some data to miniapp, optional 

**Handle custom events from miniApp.**

**kotlin**
```kotlin
miniApp.setCustomEventListener { miniAppActivity, miniApp, customEvent -> 
    //doSomething
    customEvent.payload = mapOf("message" to "text")
    miniApp.sendEvent(customEvent)
}
```
**java**
```java

miniApp.setCustomEventListener(new MiniApp.CustomEventListener() {
            @Override
            public void handle(@NotNull AppboxoActivity miniAppActivity, @NotNull MiniApp miniApp, @NotNull CustomEvent customEvent) {
                Map<String, Object> payload = new HashMap<>();
                payload.put("message", "message");
                payload.put("id", 1);
                payload.put("checked", true);
                customEvent.setPayload(payload);
                miniApp.sendEvent(customEvent);
            }
        });
miniApp.open(this);
```
Use *miniAppActivity* to launch dialogs and activities. 
Use miniAppActivity.doOnActivityResult { requestCode, resultCode, data -> ...} to handle ActivityResult.    

**Handle miniapp lifecycle**
```kotlin
miniapp.setLifecycleListener(object : MiniApp.LifecycleListener {
                    override fun onLaunch(miniApp: MiniApp) {
                        //Called when the miniapp will launch with Appboxo.open(...)
                    }

                    override fun onResume(miniApp: MiniApp) {
                        //Called when the miniapp will start interacting with the user
                    }

                    override fun onPause(miniApp: MiniApp) {
                        //Called when the miniapp loses foreground state
                    }

                    override fun onClose(miniApp: MiniApp) {
                        //Called when clicked close button in miniapp or when destroyed miniapp activity
                    }

                    override fun onError(miniApp: MiniApp, message: String) {
                    }
                })
``` 

**To hide all the miniapps**
This method minimizes all opened miniapps.  
```kotlin
Appboxo.hideAllMiniApps()
```

**To logout from all the miniapps within your mobile application use this method** 
```kotlin
Appboxo.logout()
```
