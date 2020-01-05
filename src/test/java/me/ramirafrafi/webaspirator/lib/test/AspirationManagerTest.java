/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ramirafrafi.webaspirator.lib.test;

import java.net.MalformedURLException;
import org.asynchttpclient.Response;
import me.ramirafrafi.dmanager.lib.FileDownload;
import me.ramirafrafi.dmanager.lib.State;
import me.ramirafrafi.webaspirator.lib.AspirationManager;
import me.ramirafrafi.webaspirator.lib.WebAspirationListener;
import me.ramirafrafi.webaspirator.lib.WebsiteAspiration;

/**
 *
 * @author Admin
 */
public class AspirationManagerTest {
    static public void main(String[] args) throws MalformedURLException {
        WebsiteAspiration waspiration1 = 
                new WebsiteAspiration("https://docs.oracle.com/javase/7/docs/api/java/lang/IllegalStateException.html", 
                        1, "C:\\Users\\Admin\\Downloads\\Web aspirator");
        WebsiteAspiration waspiration2 = 
                new WebsiteAspiration("https://www.baeldung.com", 1, "C:\\Users\\Admin\\Downloads\\Web aspirator");
        WebsiteAspiration waspiration3 = 
                new WebsiteAspiration("https://www.nouvelessor.tn", 1, "C:\\Users\\Admin\\Downloads\\Web aspirator");
        
        waspiration1.setListener(new WebAspirationListener() {
            @Override
            public void onAspire(WebsiteAspiration waspiration) {
                System.out.println("----------------------------------------------------");
                System.out.println("Aspiring docs.oracle.com");
                System.out.println("----------------------------------------------------");
            }

            @Override
            public void onCompleted(WebsiteAspiration waspiration, State status) {
                System.out.println("----------------------------------------------------");
                System.out.println("Completed docs.oracle.com");
                System.out.println("----------------------------------------------------");
            }

            @Override
            public void onError(WebsiteAspiration waspiration) {}

            @Override
            public void onDownloadSubmitted(WebsiteAspiration waspiration, FileDownload download) {}

            @Override
            public void onDownloadCompleted(WebsiteAspiration waspiration, FileDownload download, State status, Response response) {}

            @Override
            public void onDownloadError(WebsiteAspiration waspiration, FileDownload download) {}

            @Override
            public void onHangon(WebsiteAspiration waspiration) {}
        });
        waspiration2.setListener(new WebAspirationListener() {
            @Override
            public void onAspire(WebsiteAspiration waspiration) {
                System.out.println("----------------------------------------------------");
                System.out.println("Aspiring www.baeldung.com");
                System.out.println("----------------------------------------------------");
            }

            @Override
            public void onCompleted(WebsiteAspiration waspiration, State status) {
                System.out.println("----------------------------------------------------");
                System.out.println("Completed www.baeldung.com");
                System.out.println("----------------------------------------------------");
            }

            @Override
            public void onError(WebsiteAspiration waspiration) {}

            @Override
            public void onDownloadSubmitted(WebsiteAspiration waspiration, FileDownload download) {}

            @Override
            public void onDownloadCompleted(WebsiteAspiration waspiration, FileDownload download, State status, Response response) {}

            @Override
            public void onDownloadError(WebsiteAspiration waspiration, FileDownload download) {}
            
            @Override
            public void onHangon(WebsiteAspiration waspiration) {}
        });
        waspiration3.setListener(new WebAspirationListener() {
            @Override
            public void onAspire(WebsiteAspiration waspiration) {
                System.out.println("----------------------------------------------------");
                System.out.println("Aspiring www.nouvelessor.tn");
                System.out.println("----------------------------------------------------");
            }

            @Override
            public void onCompleted(WebsiteAspiration waspiration, State status) {
                System.out.println("----------------------------------------------------");
                System.out.println("Completed www.nouvelessor.tn");
                System.out.println("----------------------------------------------------");
            }

            @Override
            public void onError(WebsiteAspiration waspiration) {}

            @Override
            public void onDownloadSubmitted(WebsiteAspiration waspiration, FileDownload download) {}

            @Override
            public void onDownloadCompleted(WebsiteAspiration waspiration, FileDownload download, State status, Response response) {}

            @Override
            public void onDownloadError(WebsiteAspiration waspiration, FileDownload download) {}
            
            @Override
            public void onHangon(WebsiteAspiration waspiration) {}
        });
        
        AspirationManager amanager = new AspirationManager();
        amanager.newTask(waspiration1, true);
        amanager.newTask(waspiration2, true);
        amanager.newTask(waspiration3, false);
        amanager.shutdown();
    }
}
