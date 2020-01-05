/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ramirafrafi.webaspirator.lib;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.io.FileUtils;
import org.apache.tika.mime.MimeTypeException;
import me.ramirafrafi.dmanager.lib.DownloadManager;
import me.ramirafrafi.dmanager.lib.FileDownload;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.apache.commons.validator.routines.UrlValidator;
import org.asynchttpclient.Response;
import me.ramirafrafi.dmanager.lib.FileDownloadListener;
import me.ramirafrafi.dmanager.lib.State;
import me.ramirafrafi.dmanager.lib.StatefulRunnable;

/**
 *
 * @author Admin
 */
public class WebsiteAspiration implements StatefulRunnable {

    private URL url = null;
    private int depth = -1;
    private String aspirationDir = null;
    private DownloadManager downloadManager = null;
    private ObservableList<FileDownload> downloads = null;
    private State status = State.STOPPED;
    private String websiteDirname = null;
    private String indexFilename = null;
    private Map<String, String> pages = null;
    private Map<String, String> files = null;
    private WebAspirationListener listener = new WebAspirationListener() {
        @Override
        public void onAspire(WebsiteAspiration waspiration) {
        }

        @Override
        public void onCompleted(WebsiteAspiration waspiration, State status) {
        }

        @Override
        public void onError(WebsiteAspiration waspiration) {
        }

        @Override
        public void onDownloadSubmitted(WebsiteAspiration waspiration, FileDownload download) {
        }

        @Override
        public void onDownloadCompleted(WebsiteAspiration waspiration, FileDownload download,
                State status, Response response) {
        }

        @Override
        public void onDownloadError(WebsiteAspiration waspiration, FileDownload download) {
        }

        @Override
        public void onHangon(WebsiteAspiration waspiration) {
        }
    };

    public WebsiteAspiration(String url, int depth, String aspirationDir) throws MalformedURLException {
        this.url = new URL(url);
        this.depth = depth;
        this.aspirationDir = aspirationDir;
        downloadManager = new DownloadManager();
        downloads = FXCollections.observableArrayList();
        pages = new HashMap<>();
        files = new HashMap<>();
    }

    @Override
    public void run() {
        try {
            aspire();
        } catch (IOException | InterruptedException | ExecutionException ex) {
            status = State.ERROR;
            listener.onError(this);
            Logger.getLogger(WebsiteAspiration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void hangon() {
        if (this.status == State.STOPPED || this.status == State.ERROR) {
            this.status = State.PENDING;
            listener.onHangon(this);
        }
    }

    @Override
    public void stop() {
        if (status != State.COMPLETE) {
            status = State.STOPPED;
        }
    }

    @Override
    public State getStatus() {
        return status;
    }

    public URL getUrl() {
        return url;
    }

    public int getDepth() {
        return depth;
    }

    public String getAspirationDir() {
        return aspirationDir;
    }

    public DownloadManager getDownloadManager() {
        return downloadManager;
    }

    public ObservableList<FileDownload> getDownloads() {
        return downloads;
    }

    public void setListener(WebAspirationListener listener) {
        this.listener = listener;
    }

    public void aspire() throws IOException, InterruptedException, ExecutionException {
        if (status == State.STOPPED || status == State.PENDING || status == State.ERROR) {
            makeDir();

            status = State.DOWNLOADING;
            listener.onAspire(this);
            aspire_(url.toString(), 0);
            close();

            CompletableFuture<Void> allFuture = CompletableFuture.allOf(downloadManager.getFutures());
            while (!allFuture.isDone()) {
                if (State.STOPPED == status) {
                    downloadManager.stopAll();
                    break;
                }
            }

            if (State.DOWNLOADING == status) {
                status = State.COMPLETE;
            }
            listener.onCompleted(this, status);
        }
    }

    public void close() {
        downloadManager.shutdown();
    }

    public void deleteFiles() throws IOException {
        (new File(aspirationDir + File.separator + indexFilename)).delete();
        websiteDirname = indexFilename.replace(".html", "");
        FileUtils.deleteDirectory(new File(aspirationDir + File.separator + websiteDirname));
    }

    private String aspire_(String pageUrl, int currentDepth) throws IOException {
        if (null != pages.get(pageUrl)) {
            return pages.get(pageUrl);
        }

        if (currentDepth < depth) {
            Document document = Jsoup.connect(pageUrl).get();
            String htmlFilename = getHtmlFilename(document);

            if (currentDepth == 0) {
                indexFilename = htmlFilename;
            }

            if ((new File(getHtmlFilepath(htmlFilename))).exists()) {
                return htmlFilename;
            }

            aspireFiles(document, currentDepth, "[src]", "src", "src");
            aspireFiles(document, currentDepth, "[data-src]", "data-src", "data-src");
            aspireFiles(document, currentDepth, "[srcset]", "srcset", "srcset");
            aspireFiles(document, currentDepth, "[href]", "href", "href");

            if (status == State.DOWNLOADING) {
                saveHtmlFile(pageUrl, document, htmlFilename);
            }

            return htmlFilename;
        }

        return null;
    }

    private void saveHtmlFile(String pageUrl, Document document, String htmlFilename) throws IOException {
        adaptDocument(document);

        String filePath = getHtmlFilepath(htmlFilename);
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(filePath), StandardCharsets.UTF_8)) {
            writer.write(document.outerHtml());
        }
        pages.put(pageUrl, htmlFilename);
    }

    private String downloadFile(Element elem, String fileUrl, int currentDepth) throws MalformedURLException, IOException, InterruptedException, ExecutionException, MimeTypeException {
        String downloadDir = aspirationDir + File.separator + websiteDirname;
        if (elem.nodeName().equals("a")) {
            String htmlFilename = aspire_(fileUrl, currentDepth + 1);
            return null == htmlFilename ? fileUrl : htmlFilename;
        } else {
            if (null == files.get(fileUrl)) {
                FileDownload fileDownload = new FileDownload(fileUrl, downloadDir);
                
                if (null == fileDownload.getFileName() || fileDownload.getFileName().isEmpty()) {
                    fileDownload.close();
                    return null;
                }

                fileDownload.setListener(new FileDownloadListener() {
                    @Override
                    public void onDownload(FileDownload fileDownload) {
                    }

                    @Override
                    public void onCompleted(State status, Response response) {
                        listener.onDownloadCompleted(WebsiteAspiration.this, fileDownload, status, response);
                    }

                    @Override
                    public void onAdvance(long downloaded) {
                    }

                    @Override
                    public void onError(FileDownload fileDownload) {
                        listener.onDownloadError(WebsiteAspiration.this, fileDownload);
                    }

                    @Override
                    public void onHangon(FileDownload fileDownload) {
                    }
                });

                downloadManager.newTask(fileDownload, true);
                downloads.add(fileDownload);
                files.put(fileUrl, fileDownload.getFileName());
                listener.onDownloadSubmitted(this, fileDownload);
            }
            return files.get(fileUrl);
        }
    }

    private String getResourceUrl(String fileName, int currentDepth) {
        return fileName == null
                ? "#"
                : (currentDepth == 0
                        ? ((new UrlValidator()).isValid(fileName)
                        ? fileName
                        : websiteDirname + "/" + fileName)
                        : (fileName.equals(indexFilename)
                        ? "../" + fileName
                        : fileName));
    }

    private String getWebsiteDirname() throws IOException {
        Document document = Jsoup.connect(url.toString()).get();
        return document.title();
    }

    private void aspireFiles(Document document, int currentDepth, String select, String inAttr, String outAttr) {
        if (State.STOPPED == status) {
            return;
        }

        Elements selected = document.select(select);
        for (Element selected_ : selected) {
            if (State.STOPPED == status) {
                return;
            }

            try {
                UrlValidator urlValidator = new UrlValidator();
                String absUrl = selected_.absUrl(inAttr).split(" ")[0];

                if (urlValidator.isValid(absUrl)) {
                    String fileName = downloadFile(selected_, absUrl, currentDepth);

                    if (null != fileName) {
                        selected_.attr(outAttr, getResourceUrl(fileName, currentDepth));
                    }
                }
            } catch (InterruptedException | ExecutionException | MimeTypeException | IOException ex) {
                Logger.getLogger(WebsiteAspiration.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void adaptDocument(Document document) {
        try {
            document.select("base").get(0).remove();
        } catch (IndexOutOfBoundsException ex) {
        }
    }

    private void makeDir() throws IOException {
        websiteDirname = getWebsiteDirname().replaceAll("[^a-zA-Z0-9\\.\\- ]", "_");
        File dir = new File(aspirationDir + File.separator + websiteDirname);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private String getHtmlFilename(Document document) {
        String fileName = document.title().replaceAll("[^a-zA-Z0-9\\.\\- ]", "_")
                + ".html";
        return fileName;
    }

    private String getHtmlFilepath(String htmlFilename) {
        return htmlFilename.equals(indexFilename)
                ? aspirationDir + File.separator + htmlFilename
                : aspirationDir + File.separator + websiteDirname + File.separator + htmlFilename;
    }
}
