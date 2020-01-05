/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ramirafrafi.webaspirator.lib.test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.asynchttpclient.Response;
import me.ramirafrafi.dmanager.lib.FileDownload;
import me.ramirafrafi.dmanager.lib.State;
import me.ramirafrafi.webaspirator.lib.WebAspirationListener;
import me.ramirafrafi.webaspirator.lib.WebsiteAspiration;

/**
 *
 * @author Admin
 */
public class WebsiteAspirationTest {
    
    static public void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        WebsiteAspiration waspiration = 
                new WebsiteAspiration("https://docs.oracle.com/javase/7/docs/api/java/lang/IllegalStateException.html", 2, "C:\\Users\\Admin\\Downloads\\WebAspirator Websites");
        
        waspiration.setListener(new WebAspirationListener() {
            int submitted = 0;
            int completed = 0;
            int errors = 0;
            
            @Override
            public void onAspire(WebsiteAspiration waspiration) {
                System.out.println("===== Aspire =====");
            }

            @Override
            public void onCompleted(WebsiteAspiration waspiration, State status) {
                System.out.println("===== Completed =====");
            }

            @Override
            public void onError(WebsiteAspiration waspiration) {
                System.out.println("===== Error =====");
            }

            @Override
            public void onDownloadSubmitted(WebsiteAspiration waspiration, FileDownload download) {
                submitted++;
                System.out.println("===== " + completed + "/" + submitted + " downloads =====");
            }

            @Override
            public void onDownloadCompleted(WebsiteAspiration waspiration, FileDownload download, State status, Response response) {
                completed++;
                System.out.println("===== " + completed + "/" + submitted + " downloads =====");
            }

            @Override
            public void onDownloadError(WebsiteAspiration waspiration, FileDownload download) {
                errors++;
                System.out.println("===== " + errors + " errors =====");
            }

            @Override
            public void onHangon(WebsiteAspiration waspiration) {}
        });
        waspiration.run();
        
        /* Thread.sleep(5000);
        waspiration.stop(); */
    }
}
