/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ramirafrafi.webaspirator.lib;

import org.asynchttpclient.Response;
import me.ramirafrafi.dmanager.lib.FileDownload;
import me.ramirafrafi.dmanager.lib.State;

/**
 *
 * @author Admin
 */
public interface WebAspirationListener {
    public void onAspire(WebsiteAspiration waspiration);
    public void onCompleted(WebsiteAspiration waspiration, State status);
    public void onError(WebsiteAspiration waspiration);
    public void onHangon(WebsiteAspiration waspiration);
    public void onDownloadSubmitted(WebsiteAspiration waspiration, FileDownload download);
    public void onDownloadCompleted(WebsiteAspiration waspiration, FileDownload download, State status, Response response);
    public void onDownloadError(WebsiteAspiration waspiration, FileDownload download);
}
