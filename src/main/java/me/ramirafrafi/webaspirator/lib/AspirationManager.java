/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ramirafrafi.webaspirator.lib;

import java.util.Set;
import java.util.concurrent.Executors;
import me.ramirafrafi.dmanager.lib.DownloadManager;
import me.ramirafrafi.dmanager.lib.StatefulRunnable;

/**
 *
 * @author Admin
 */
public class AspirationManager extends DownloadManager {
    public AspirationManager() {
        super();
        executorService = Executors.newSingleThreadExecutor();
    }
    
    public Set<StatefulRunnable> getAspirations () {
        return getDownloads();
    }
}
