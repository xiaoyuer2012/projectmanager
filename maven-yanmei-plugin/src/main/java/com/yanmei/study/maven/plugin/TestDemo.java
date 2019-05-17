package com.yanmei.study.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;

@Mojo(name="yanmeiplugin", defaultPhase = LifecyclePhase.PACKAGE)
public class TestDemo extends AbstractMojo{
    @Parameter
    private String dirPath;
    @Parameter
    private Boolean skip=false;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if(skip == true){
            System.out.println("skip is true");
            return;
        }
        if(dirPath == null || dirPath.equals("")){
            System.out.println("the param can't be empty");
            return;
        }
        System.out.println("the param is "+dirPath);
        File file = new File(dirPath);
        findFile(file);
    }

    private void findFile(File files){
        if(!files.exists()) {
            System.out.println("the file or the dir is not exist!");
            return;
        }

        File[] childFiles = files.listFiles();
        if(childFiles == null || childFiles.length<=0){
            System.out.println("the file or the dir is not exist");
            return;
        }
        for(File file: childFiles){
            if(file.isDirectory()){
                findFile(file);
            }else {
                // 处理文件
                processFile(file);
            }

        }

    }
    private void processFile(File file) {
        String name = file.getName();
        if (name.endsWith(".html")) {
            Document document = null;
            try {
                document = Jsoup.parse(file, "utf-8");
                System.out.println("find html file--" + name + "-the dir is-"+file.getPath());
                processTag(document,"link","href");
                processTag(document,"script","src");
                FileOutputStream fos = null;
                fos = new FileOutputStream(file, false);
                OutputStreamWriter osw = null;
                osw = new OutputStreamWriter(fos, "utf-8");
                osw.write(document.html());
                osw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processTag(Document document,String tag,String attr) {
        Elements elements = document.getElementsByTag(tag);
        for (Element element : elements) {
            String attrValue = element.attr(attr);
            attrValue += "?v=" + System.currentTimeMillis();
            System.out.println(attrValue);
            element.attr(attr,attrValue);
        }
    }
}
