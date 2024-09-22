# webaspirator-lib

## Overview
The **webaspirator-lib** library makes it easier to download websites in Java. It is built on top of [dmanager-lib](https://github.com/ramirafrafi/dmanager-lib) library.

## Installation
The library is not yet published into Maven repository, you can download the [JAR file](build/webaspirator-lib-1.0-SNAPSHOT.jar) and add it to your project.

> _**NOTE:** This library depends on [dmanager-lib JAR file](https://github.com/ramirafrafi/dmanager-lib/blob/main/build/dmanager-lib-1.0-SNAPSHOT-jar-with-dependencies.jar)._

## Documentation
This library defines classes that can be used separately in a flexible manner, this doc takes a tour in explaining them.

### Class [`WebsiteAspiration`](src/main/java/me/ramirafrafi/webaspirator/lib/WebsiteAspiration.java)
Class presenting a single website download task, which implements [Runnable](https://docs.oracle.com/javase/8/docs/api/java/lang/Runnable.html):
```java
import me.ramirafrafi.webaspirator.lib.WebsiteAspiration;

class WebsiteAspirationExample 
{
  public static void main(String[] argv)
    {
      WebsiteAspiration websiteAspiration = new WebsiteAspiration(
        "https://example.net",  // URL of the website to be downloaded
        1,                      // Depth of website pages to be downloaded
        "/home/user/Downloads"  // Directory in which website will be saved
      );

      new Thread(websiteAspiration).start(); // Start the website download in a thread as it implements the Runnable interface, the run() method will be called automatically.

      websiteAspiration.hangon();   // Pauses the download.
      websiteAspiration.aspire();   // Resumes the download.
      websiteAspiration.stop();     // Stops the download.
      websiteAspiration.close();    // Releases resources.
    } 
}
```

### Interface [`WebAspirationListener`](src/main/java/me/ramirafrafi/webaspirator/lib/WebAspirationListener.java)
Interface definition for callbacks to be invoked on different download status changes:
```java
import me.ramirafrafi.dmanager.lib.WebsiteAspiration;
import me.ramirafrafi.dmanager.lib.WebAspirationListener;

class WebAspirationListenerExample 
{
  public static void main(String[] argv)
    {
      WebsiteAspiration websiteAspiration = new WebsiteAspiration(
        "https://example.net",
        1,
        "/home/user/Downloads"
      );

      new Thread(websiteAspiration).start();

      websiteAspiration.setListener(new WebAspirationListener() {
            @Override
            public void onAspire(WebsiteAspiration waspiration) {
                // Website download started, no file downloads started yet.
            }

            @Override
            public void onCompleted(WebsiteAspiration waspiration, State status) {
                // Website download completed, all files have been downloaded.
            }

            @Override
            public void onError(WebsiteAspiration waspiration) {
                // An error occured during website download.
            }

            @Override
            public void onHangon(WebsiteAspiration waspiration) {
                // Website download has been paused calling `websiteAspiration.hangon()`
            }

            @Override
            public void onDownloadSubmitted(WebsiteAspiration waspiration, FileDownload download) {
                // A new file download started, `download` is the created FileDownload instance.
            }

            @Override
            public void onDownloadCompleted(WebsiteAspiration waspiration, FileDownload download, State status, Response response) {
                // A file download completed.
            }

            @Override
            public void onDownloadError(WebsiteAspiration waspiration, FileDownload download) {
                // Error occured during specific file download.
            }
        });
    } 
}
```

### Class [`AspirationManager`](src/main/java/me/ramirafrafi/webaspirator/lib/AspirationManager.java)
> _**NOTE:** `AspirationManager` inherits from [`DownloadManager`](https://github.com/ramirafrafi/dmanager-lib/blob/main/src/main/java/me/ramirafrafi/dmanager/lib/DownloadManager.java) class of [dmanager-lib](https://github.com/ramirafrafi/dmanager-lib) library._

Class managing a pool of `WebsiteAspiration`:
```java
import me.ramirafrafi.dmanager.lib.WebsiteAspiration;
import me.ramirafrafi.dmanager.lib.AspirationManager;

class AspirationManagerExample 
{
  public static void main(String[] argv)
    {
      AspirationManager aspirationManager = new AspirationManager();  // Manages website download taks with a single thread pool, thus website downloads are not in parallel (their file downloads are in parallel)

      WebsiteAspiration websiteAspiration = new WebsiteAspiration(
        "https://example.net",
        1,
        "/home/user/Downloads"
      );

      aspirationManager.newTask(
        websiteAspiration,  // Add `websiteAspiration` to the managed pool
        true                // When it is `true`, website download will be put in the waiting queue if all the pool is used, or will be started immediately.
      );

      aspirationManager.stopTask(websiteAspiration);      // Stops `websiteAspiration`.
      aspirationManager.resumeTask(websiteAspiration);    // Resumes `websiteAspiration`.

      aspirationManager.stopAll();    // Stop all website downloads.
    } 
}
```
